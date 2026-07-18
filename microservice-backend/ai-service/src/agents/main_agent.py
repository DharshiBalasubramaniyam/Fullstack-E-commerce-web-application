from src.core.agents.base_agent import BaseAgent
from src.tools.product_tool import product_tool


class MainAgent(BaseAgent):

    def __init__(self):

        tools = [product_tool]

        super().__init__(
            name="Main Agent",
            model="google_genai:gemini-3.1-flash-lite",
            tools=tools
        )