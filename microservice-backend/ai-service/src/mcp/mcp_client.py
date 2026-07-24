from langchain_mcp_adapters.client import MultiServerMCPClient  
from langchain_mcp_adapters.tools import load_mcp_tools


class MCPClient:

    def __init__(self, name: str, url: str):
        self.name = name

        self.client = MultiServerMCPClient(
            {
                name: {
                    "transport": "streamable_http",
                    "url": url,
                }
            }
        )

        self.session = None

    async def connect(self):
        print(f"{self.name} MCP client ready")

    async def disconnect(self):
        print(f"{self.name} MCP client closed")

    async def get_tools(self):
        return await self.client.get_tools()