from langchain_core.tools import tool

from src.agents.base_agent import BaseAgent
from src.agents.prompts.system_prompts import order_prep_agent_system_prompt
from src.agents.response_modals.order_prep_agent_respose import OrderPreparationAgentResponse
from src.core.config import GEMINI_MODEL

class OrderPreparationAgent(BaseAgent):

    def __init__(self, tools):

        super().__init__(
            name="Order Preparation Agent",
            model=GEMINI_MODEL,
            system_prompt=order_prep_agent_system_prompt,
            response_format=OrderPreparationAgentResponse,
            tools=tools
        )