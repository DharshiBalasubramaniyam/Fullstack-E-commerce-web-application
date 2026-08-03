from src.agents.base_agent import BaseAgent
from src.agents.response_modals.main_agent_response import MainAgentResponse
from src.agents.prompts.system_prompts import main_agent_system_prompt
from src.core.config import GEMINI_MODEL

class MainAgent(BaseAgent):

    def __init__(self, tools):

        super().__init__(
            name="Main Agent",
            model=GEMINI_MODEL,
            system_prompt=main_agent_system_prompt,
            response_format=MainAgentResponse,
            tools=tools
        )