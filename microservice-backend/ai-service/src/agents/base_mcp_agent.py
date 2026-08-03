from typing import Optional, Type

from langchain.agents import create_agent
from langchain_core.messages import BaseMessage, HumanMessage, AIMessage, ToolMessage
from pydantic import BaseModel

from src.agents.base_agent import BaseAgent

class BaseMcpAgent(BaseAgent):

    def __init__(
        self,
        name: str,
        model: str,
        system_prompt: str,
        mcp_client,
        tools: list | None,
        response_format: Optional[Type[BaseModel]] = None
    ):
        super().__init__(
            name = name,
            model = model,
            system_prompt = system_prompt,
            response_format = response_format,
            tools=tools
        )
        self.mcp_client = mcp_client
        self.agent = None


    async def initialize(self):

        tools = await self.mcp_client.get_tools()

        print(
            f"{self.name} tools loaded: {len(tools)}"
        )

        self.agent = create_agent(
            model=self.model,
            system_prompt=self.system_prompt,
            tools=tools,
            response_format=self.response_format
        )

        print(
            f"{self.name} initialized"
        )