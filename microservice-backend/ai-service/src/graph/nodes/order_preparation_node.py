from typing import Literal

from langchain_core.messages import AIMessage, HumanMessage
from langgraph.types import Command

from src.agents.response_modals.order_prep_agent_respose import OrderPreparationAgentResponse
from src.graph.state import GraphState
from src.registry.agent_registry import agent_registry


async def order_preparation_node(state: GraphState) -> Command[Literal["__end__", "cart_node", "order_confirmation_node"]]:

    print("=== ORDER PREP NODE ===")

    message = get_order_preparation_node_message(state)
    
    result: OrderPreparationAgentResponse = await agent_registry.get("order_preparation").invoke(message)

    update = {}
    goto = "__end__"

    if result.status == "COMPLETED":
        update = {
            "next_node": "order_confirmation_node",
            "from_node": "order_preparation_node",
            "checkoutInfo": result.checkoutInfo,
        }
        goto = "order_confirmation_node"

    elif result.status == "CART_NOT_FOUND" or result.status == "PRODUCT_MISSING_IN_CART":
        update = {
            "from_node": "order_preparation_node",
            "next_node": "cart_node",
            "message_for_next_node": result.message,
        }
        goto = "cart_node"

    elif result.status == "NEED_CLARIFICATION":
        update = {
            "messages": [
                AIMessage(content=result.message, name="order_preparation_agent")
            ],
            "next_node": "__end__",
            "from_node": "order_preparation_node",
        }

    elif result.status == "FAILED":
            update = {
                "messages": [
                    AIMessage(content=result.message, name="order_preparation_agent")
                ],
                "next_node": "__end__",
                "from_node": "order_preparation_node",
                "checkoutInfo": None,
            }
    
    return Command(
        update=update,
        goto=goto
    )

def get_order_preparation_node_message(state: GraphState):
    context = f"""
User id: {state.get("user_id")}
"""
                
    if state.get('checkoutInfo') is not None:
        context += f"""
Checkout info:
{state.get('checkoutInfo')}
"""
    else:
        context += """
Checkout info:
None
""" 
            
    message = f"""
User request:
{state["message_for_next_node"]}

**State**
{context}
"""

    return message
