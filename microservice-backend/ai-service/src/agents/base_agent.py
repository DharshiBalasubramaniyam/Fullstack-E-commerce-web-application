from typing import Optional, Type

from langchain.agents import create_agent
from langchain_core.messages import BaseMessage, HumanMessage, AIMessage, ToolMessage
from pydantic import BaseModel


class BaseAgent:

    def __init__(
        self,
        name: str,
        model: str,
        system_prompt: str,
        tools: list | None,
        response_format: Optional[Type[BaseModel]] = None
    ):
        self.name = name
        self.model = model
        self.system_prompt = system_prompt
        self.agent = None
        self.response_format = response_format
        self.tools = tools


    async def initialize(self):

        print(
            f"{self.name} tools loaded: " + str(len(self.tools))
        )

        self.agent = create_agent(
            model=self.model,
            system_prompt=self.system_prompt,
            response_format=self.response_format,
            tools=self.tools
        )

        print(
            f"{self.name} initialized"
        )


    async def invoke(self, message: str | list[BaseMessage]):

        input = message

        if isinstance(message, str):
            input = [
                HumanMessage(content=message)
            ]

        result = await self.agent.ainvoke(
            {
                "messages": input
            }
        )

        for message in result["messages"]:
            if isinstance(message, HumanMessage):
                print(f"[{self.name}] HumanMessage: {message.content}")

            elif isinstance(message, AIMessage):
                if message.tool_calls:
                    for tool_call in message.tool_calls:
                        print(
                            f"[{self.name}] AIMessage: tool_call={tool_call['name']}, "
                            f"args={tool_call['args']}"
                        )
                else:
                    # AI's final response
                    print(f"[{self.name}] AIMessage: {message.text}")

            elif isinstance(message, ToolMessage):
                print(f"[{self.name}] ToolMessage: {message.content}")

        return result["structured_response"]
