from typing import Literal

from langchain_core.messages import AIMessage, HumanMessage
from langgraph.types import Command

from src.graph.state import GraphState
from src.registry.agent_registry import agent_registry

async def main_node(state: GraphState) -> Command[Literal["product_node", "cart_node", "__end__"]]:

    print("=== MAIN NODE ===")

    context = ""

    if state.get('next_node') is not None and state.get('next_node') in ["product_node", "cart_node"] and state.get('message_from_next_node') is not None:
        context = f"""

Context:
{state.get('message_from_next_node')}
"""
    
    result = await agent_registry.get("main").invoke(
        f"""
{state['messages']}
{context}
"""
    )

    print(">> next_node: " + result.next_node)

    if result.next_node == "product_node":

        print("Routing to product node")

        return Command(
            update={
                "next_node": "product_node",
                "message_for_next_node": result.message,
                # "arguments_for_next_node": result.arguments
            },
            goto="product_node"
        )


    elif result.next_node == "cart_node":

        return Command(
            update={
                "next_node": "cart_node",
                "message_for_next_node": result.message,
                # "arguments_for_next_node": result.arguments
            },
            goto="cart_node"
        )


    # elif result.next_node == "order_agent":

    #     return Command(
    #         update={
    #             "next_node": "order_agent",
    #             "message_for_next_node": result.message,
    #             "arguments_for_next_node": result.arguments
    #         },
    #         goto="order_node"
    #     )


    else:
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

def get_main_node_message(state: GraphState) -> str:
    context = ""
#     context = f"""
# User id: {state.get("user_id")}
# """
             
#     if state.get('products') is not None and len(state.get('products')) > 0:
#         context += f"""
# Product catelog:
# {state.get('products')}
# """
         
#     if state.get('cart_id') is not None:
#         context += f"""
# Cart id:{state.get('cart_id')}
# """
        
#     if state.get('cart_items') is not None and len(state.get('cart_items')) > 0:
#             context += f"""
#     Cart id:{state.get('cart_items')}
#     """

    # if state.get('messages') is not None and len(state.get('messages')) > 1:
    #         context += f"""
    # Previous conversation:
    # {state.get('messages')[0:len(state.get('messages'))]}
    # """

    # context += f"""
    #     \n\nUser query:
    #     {state.get('current_question')}
    #     """
    

    # print("=== Main Context ===")
    # print(context)

    return context

