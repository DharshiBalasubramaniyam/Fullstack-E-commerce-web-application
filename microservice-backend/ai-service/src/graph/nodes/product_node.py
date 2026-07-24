from typing import Literal

from langchain_core.messages import AIMessage, HumanMessage, ToolMessage
from langgraph.types import Command

from src.graph.state import GraphState
from src.registry.agent_registry import agent_registry


async def product_node(state: GraphState) -> Command[Literal["main_node"]]:

    print("=== PRODUCT NODE ===")

    context = ""

    if state.get('products') is not None and len(state.get('products')) > 0:
        context=f"""
Context:
{state.get('products')}
"""

    message = f"""
User request:
{state["message_for_next_node"]}
{context}
"""
    result = await agent_registry.get("product").invoke(message)
    
    update = {}

    if result.operation == "SEARCH":
        update["products"] = result.products
        update["message_from_next_node"] = f"""
{result.message}
Products catelog:
{result.products}
"""
    else:
        update["message_from_next_node"] = {result.message}


    return Command(
        update=update,
        goto="main_node"
    )

def get_product_node_message(state: GraphState):
    message = f"""
User request:
{state["message_for_next_node"]}

Arguments:
{state["arguments_for_next_node"]}
"""

    print("===Product node message===")
    print(message)

    return message
