from src.agents.base_mcp_agent import BaseMcpAgent
from src.agents.prompts.system_prompts import cart_agent_system_prompt
from src.agents.response_modals.cart_agent_response import CartAgentResponse
from src.core.config import GEMINI_MODEL


class CartAgent(BaseMcpAgent):

    def __init__(self, mcp_client):

        super().__init__(
            name="Cart Agent",
            model=GEMINI_MODEL,
            system_prompt=cart_agent_system_prompt,
            mcp_client=mcp_client,
            response_format=CartAgentResponse
        )