from langchain.agents import create_agent
from langchain_core.messages import HumanMessage, AIMessage, ToolMessage


class BaseAgent:

    def __init__(
        self,
        name: str,
        model: str,
        tools: any,
    ):
        self.name = name
        self.model = model
        self.tools = tools
        self.agent = None


    async def initialize(self):

        self.agent = create_agent(
            model=self.model,
            tools=self.tools
        )

        print(
            f"{self.name} initialized"
        )


    async def invoke(self, message: str):

        result = await self.agent.ainvoke(
            {
                "messages": [
                    {
                        "role": "user",
                        "content": message
                    }
                ]
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

        return result["messages"][-1].content