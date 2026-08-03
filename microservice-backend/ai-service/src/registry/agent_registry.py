from typing import Literal

from src.agents.base_agent import BaseAgent
from src.agents.base_mcp_agent import BaseMcpAgent
from src.agents.cart_agent import CartAgent
from src.agents.main_agent import MainAgent
from src.agents.order_preparation_agent import OrderPreparationAgent
from src.agents.product_agent import ProductAgent
from src.core.config import CART_MCP_URL, ORDER_MCP_URL, PRODUCT_MCP_URL
from src.mcp.mcp_client import MCPClient

# TODO: Move urls to .env
class AgentRegistry:

    def __init__(self):
        self.product_mcp = None
        self.cart_mcp = None
        self.agents = {}
    
    async def initialize(self):
        
        # product agent
        self.product_mcp = MCPClient(
            "Product",
            PRODUCT_MCP_URL
        )
        product_agent = ProductAgent(mcp_client=self.product_mcp, tools=[])
        await product_agent.initialize()

        # cart agent
        self.cart_mcp = MCPClient(
            "Cart",
            CART_MCP_URL
        )
        cart_agent = CartAgent(mcp_client=self.cart_mcp, tools=[])
        await cart_agent.initialize()

        # order preparation agent
        self.order_mcp = MCPClient(
            "Order",
            ORDER_MCP_URL
        )
        order_prep_tools = []
        for tool in await self.cart_mcp.get_tools():
            if tool.name == "getCartItemsByUser":
                order_prep_tools.append(tool)
                break

        order_prep_agent = OrderPreparationAgent(tools=order_prep_tools)
        await order_prep_agent.initialize()

        # main agent
        main_agent = MainAgent(tools=[])
        await main_agent.initialize()

        # Add agents to registry
        self.agents = {
            "product": product_agent,
            "cart": cart_agent,
            "order_preparation": order_prep_agent,
            "main": main_agent
        }
        print('Added all agents to registry!')

    def register(self, name, agent):
        self.agents[name] = agent


    def get(self, name: Literal["product", "cart", "order_preparation", "main"]):
        return self.agents[name]


agent_registry = AgentRegistry()