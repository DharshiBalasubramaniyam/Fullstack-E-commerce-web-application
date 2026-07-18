from fastmcp import Client
from fastmcp.client.transports import StreamableHttpTransport
from langchain_mcp_adapters.client import MultiServerMCPClient  
from langchain_mcp_adapters.tools import load_mcp_tools


class MCPClient:

    def __init__(self, name: str, url: str):
        self.name = name

        # self.client = Client(
        #     StreamableHttpTransport(
        #         url=url,
        #         headers={
        #             "Authorization": "Bearer your-token"
        #         }
        #     )
        # )
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
        # MultiServerMCPClient is lazy by default.
        # No explicit connect required.
        print(f"{self.name} MCP client ready")

    async def disconnect(self):
        # No explicit disconnect required for stateless mode
        print(f"{self.name} MCP client closed")

    async def get_tools(self):
        return await self.client.get_tools()