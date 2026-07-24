from src.agents.cart_agent import CartAgent
from src.agents.main_agent import MainAgent
from src.agents.product_agent import ProductAgent
from src.core.config import CART_MCP_URL, PRODUCT_MCP_URL
from src.mcp.mcp_client import MCPClient

# TODO: Move urls to .env
class AgentRegistry:

    def __init__(self):
        self.product_mcp = None
        self.cart_mcp = None
        self.agents = {}
    
    async def initialize(self):
        self.cart_mcp = MCPClient(
            "Cart",
            CART_MCP_URL
        )
        
        # product agent
        self.product_mcp = MCPClient(
            "Product",
            PRODUCT_MCP_URL
        )
        product_agent = ProductAgent(self.product_mcp)
        await product_agent.initialize()

        # cart agent
        cart_agent = CartAgent(self.cart_mcp)
        await cart_agent.initialize()

        # main agent
        main_agent = MainAgent()
        await main_agent.initialize()

        # Add agents to registry
        self.agents = {
            "product": product_agent,
            "cart": cart_agent,
            "main": main_agent
        }
        print('Added all agents to registry!')

    def register(self, name, agent):
        self.agents[name] = agent


    def get(self, name):
        return self.agents[name]


agent_registry = AgentRegistry()