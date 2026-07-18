from langchain.agents import create_agent
from langchain_core.messages import HumanMessage, AIMessage, ToolMessage


class BaseMcpAgent:

    def __init__(
        self,
        name: str,
        model: str,
        mcp_client
    ):
        self.name = name
        self.model = model
        self.mcp_client = mcp_client
        self.agent = None


    async def initialize(self):

        tools = await self.mcp_client.get_tools()

        print(
            f"{self.name} tools loaded: {len(tools)}"
        )

        self.agent = create_agent(
            model=self.model,
            tools=tools
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