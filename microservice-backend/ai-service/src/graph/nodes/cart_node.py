from typing import Literal

from langchain_core.messages import AIMessage, HumanMessage
from langgraph.types import Command

from src.agents.response_modals.cart_agent_response import CartAgentResponse
from src.graph.state import GraphState
from src.registry.agent_registry import agent_registry


async def cart_node(state: GraphState) -> Command[Literal["__end__", "product_node", "order_preparation_node"]]:

    print("=== CART NODE ===")

    message = get_cart_node_message(state)
    
    result: CartAgentResponse = await agent_registry.get("cart").invoke(message)

    update = {}
    goto = "__end__"

    if result.status == "COMPLETED":
        if state.get("from_node") is not None and state.get("from_node") == "order_preparation_node":
            goto = "order_preparation_node"
            update = {
                "next_node": "order_preparation_node",
                "from_node": "cart_node",
                "cart": result.cart
            }

        else:
            update = {
                "messages": [
                    AIMessage(content=result.message, name="cart_agent")
                ],
                "next_node": "__end__",
                "from_node": "cart_node",
                "cart": result.cart
            }

    elif result.status == "PRODUCT_NOT_FOUND":
        update = {
            "from_node": "cart_node",
            "next_node": "product_node",
            "message_for_next_node": result.message
        }
        goto = "product_node"

    elif result.status == "NEED_CLARIFICATION" or result.status == "FAILED":
        update = {
            "messages": [
                AIMessage(content=result.message, name="cart_agent")
            ],
            "next_node": "__end__",
            "from_node": "cart_node",
        }
    
    return Command(
        update=update,
        goto=goto
    )

def get_cart_node_message(state: GraphState):
    user_id = state.get("user_id")
    products = state.get("products")
                 
    return f"""
User request:
{state["message_for_next_node"]}

**State**
User id: {user_id}

Active products list:
{products if products else "No active products"}
"""
