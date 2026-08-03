from src.agents.base_mcp_agent import BaseMcpAgent
from src.agents.prompts.system_prompts import product_agent_system_prompt
from src.agents.response_modals.product_agent_response import ProductAgentResponse
from src.core.config import GEMINI_MODEL


class ProductAgent(BaseMcpAgent):

    def __init__(self, mcp_client, tools):

        super().__init__(
            name="Product Agent",
            model=GEMINI_MODEL,
            system_prompt=product_agent_system_prompt,
            mcp_client=mcp_client,
            response_format=ProductAgentResponse,
            tools=tools
        )