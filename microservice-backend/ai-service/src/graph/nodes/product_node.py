from typing import Literal

from langchain_core.messages import AIMessage, HumanMessage, ToolMessage
from langgraph.types import Command

from src.agents.response_modals.product_agent_response import ProductAgentResponse
from src.graph.state import GraphState
from src.registry.agent_registry import agent_registry


async def product_node(state: GraphState) -> Command[Literal["__end__", "cart_node"]]:

    print("=== PRODUCT NODE ===")

    message = get_product_node_message(state)
   
    result: ProductAgentResponse = await agent_registry.get("product").invoke(message)
    
    update = {}
    goto = "__end__"


    if result.operation == "SEARCH":
        if state.get("products") is None:
            update["products"] = result.products
        else:
            update["products"] = state["products"] + result.products


    if state.get("from_node") is not None and state.get("from_node") == "cart_node":
        goto = "cart_node"
    else:
        update["messages"] = [
            AIMessage(content=result.message, name="product_agent")
        ]

    update["from_node"] = "product_node"

    return Command(
        update=update,
        goto=goto
    )

def get_product_node_message(state: GraphState):

    products = state.get("products")

    return f"""
User request:
{state["message_for_next_node"]}

**State**
Active products list:
{products if products else "No active products"}
"""
