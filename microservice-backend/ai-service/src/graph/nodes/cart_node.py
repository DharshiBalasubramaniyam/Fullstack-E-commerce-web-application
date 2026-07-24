from typing import Literal

from langchain_core.messages import AIMessage, HumanMessage
from langgraph.types import Command

from src.graph.state import GraphState
from src.registry.agent_registry import agent_registry


async def cart_node(state: GraphState) -> Command[Literal["main_node"]]:

    print("=== CART NODE ===")

    message = get_cart_node_message(state)
    
    result = await agent_registry.get("cart").invoke(message)

    update = {}
    update["message_from_next_node"] = f"""
{result.message}
"""

    if result.cart is not None:
        update["cart"] = result.cart
        update["message_from_next_node"] = f"""
{result.message}
Updated cart:
{result.cart}
"""
    
    return Command(
        update=update,
        goto="main_node"
    )

def get_cart_node_message(state: GraphState):
    context = f"""
User id: {state.get("user_id")}
"""
                 
    if state.get('products') is not None and len(state.get('products')) > 0:
        context += f"""
Product catelog:
{state.get('products')}
"""
    if state.get('cart') is not None:
            context += f"""
    User cart:
    {state.get('cart')}
    """
            
    message = f"""
User request:
{state["message_for_next_node"]}

Context:
{context}
"""

    print("===Cart node message===")
    print(message)

    return message