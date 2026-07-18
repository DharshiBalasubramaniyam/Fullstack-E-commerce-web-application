from langchain_core.tools import tool
from src.core.registry.agent_registry import agent_registry


@tool(
    "searchProducts",
    description="""
        Search the product catalog for available products.
        Use this tool whenever the user asks about products,
        product availability, recommendations, categories,
        prices, or wants to find items to buy.
    """
)
async def product_tool(query: str):
    """
    Handles product related questions.
    """

    print("Called product tool: " + query)

    agent = agent_registry.get("product")

    return await agent.invoke(query)