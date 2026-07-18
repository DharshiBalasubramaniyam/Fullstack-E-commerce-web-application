from src.core.agents.base_mcp_agent import BaseMcpAgent


class ProductAgent(BaseMcpAgent):

    def __init__(self, mcp_client):

        super().__init__(
            name="Product Agent",
            model="google_genai:gemini-3.1-flash-lite",
            mcp_client=mcp_client
        )