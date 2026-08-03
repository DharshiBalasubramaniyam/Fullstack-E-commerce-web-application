from typing import Literal

from langchain_core.messages import AIMessage, HumanMessage
from langgraph.types import Command

from src.graph.state import GraphState
from src.registry.agent_registry import agent_registry

async def main_node(state: GraphState) -> Command[Literal["product_node", "cart_node", "order_preparation_node", "__end__"]]:

    print("=== MAIN NODE ===")

    message = get_main_node_message(state)
    
    result = await agent_registry.get("main").invoke(message)

    if result.next_node == "__end__":
        return Command(
            update={
                "messages": [
                    AIMessage(
                        content=result.message,
                        name="main_agent"
                    )
                ],
                "next_node": "__end__",
                "message_for_next_node": "",
                "message_from_next_node": "",
            },
            goto="__end__"
        )
    
    return Command(
            update={
                "from_node": "main_node",
                "next_node": result.next_node,
                "message_for_next_node": result.message
            },
            goto=result.next_node
        )
        

def get_main_node_message(state: GraphState) -> str:
    return f"""
{state['messages']}
"""
