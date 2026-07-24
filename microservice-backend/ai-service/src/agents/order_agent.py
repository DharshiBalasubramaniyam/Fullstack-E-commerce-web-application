from src.agents.base_mcp_agent import BaseMcpAgent
from src.agents.prompts.system_prompts import order_agent_system_prompt
from src.core.config import GEMINI_MODEL


class OrderAgent(BaseMcpAgent):

    def __init__(self, mcp_client):

        super().__init__(
            name="Order Agent",
            model=GEMINI_MODEL,
            system_prompt=order_agent_system_prompt,
            mcp_client=mcp_client
        )