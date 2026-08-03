import json
from typing import Literal

from langchain_core.messages import AIMessage, HumanMessage

from src.agents.response_modals.order_prep_agent_respose import CheckoutInfo
from src.graph.state import GraphState
from langgraph.types import Command, interrupt

from src.registry.agent_registry import agent_registry


async def order_confirmation_node(state: GraphState) -> Command[Literal["__end__", "order_confirmation_node", "main_node"]]:

    print("=== ORDER CONFIRM NODE ===")

    checkoutInfo: CheckoutInfo = state.get("checkoutInfo")

    summary_text = get_confirm_message(state.get("from_node"), checkoutInfo)

    print(len(summary_text))

    user_response = interrupt(summary_text)

    user_input = str(user_response).strip().lower()

    update = {}
    goto = ""

    if user_input == "y":
        update, goto = await process_order(checkoutInfo, summary_text)
                

    elif user_input == "n":
        update = {
            "messages": [
                AIMessage(
                    content=summary_text, 
                    name="order_confirmation_node",
                    response_metadata={
                        "interrupt": True
                    }
                ),
                HumanMessage(content=user_response),
                AIMessage(
                    content="Your request has been cancelled. No action has been taken. If you'd like to proceed later, simply submit the request again", 
                    name="order_confirmation_node"
                )
            ],
            "next_node": "__end__",
            "from_node": "order_confirmation_node",
        }
        goto = "__end__"     


    else:
        update = {
            "messages": [
                AIMessage(
                    content=summary_text, 
                    name="order_confirmation_node",
                    response_metadata={
                        "interrupt": True
                    }
                ),
                HumanMessage(content=user_response),
            ],
            "next_node": "order_confirmation_node",
            "from_node": "order_confirmation_node",
            "checkoutInfo": checkoutInfo,
        }
        goto = "order_confirmation_node"


    print(update)
    print(goto)

    return Command(
        update=update,
        goto=goto
    )


def get_confirm_message(from_node: str, checkoutInfo: CheckoutInfo):
    if from_node == "order_preparation_node":
        total_price = checkoutInfo.cart.subtotal
    
        items_summary = "\n".join([f"- {item.quantity} x {item.product_name} (${item.price} ea)" for item in checkoutInfo.cart.cart_items])
    
        return (
            f"**Order Summary**\n{items_summary}\n\n"
            f"**Total Amount:** ${total_price:.2f}\n\n"
            f"**Billing info:** \n"
            f"- First name: {checkoutInfo.firstName}"
            f"- Last name: {checkoutInfo.lastName}"
            f"- Address: {checkoutInfo.address}"
            f"- City: {checkoutInfo.city}"
            f"- Phone no: {checkoutInfo.phoneNo}\n\n"
            f"Do you confirm this order?"
        )
    
    elif from_node == "order_confirmation_node":
        return "Invalid response. Please reply with 'Y' or 'N' only."

    return ""

async def process_order(checkoutInfo: CheckoutInfo, summary_text: str):
    order_mcp_tools = await agent_registry.order_mcp.get_tools()

    create_order_tool = next(
        (tool for tool in order_mcp_tools if tool.name == "createOrder"),
        None,
    )


    if create_order_tool:
        response = await create_order_tool.ainvoke({
            "userId": checkoutInfo.user_id,
            "firstName": checkoutInfo.firstName,
            "lastName": checkoutInfo.lastName,
            "address": checkoutInfo.address,
            "city": checkoutInfo.city,
            "phoneNo": checkoutInfo.phoneNo,
            "cartId": checkoutInfo.cart.cart_id,
        })
        print("Order res:", response)

        response_json = json.loads(response[0]["text"])
        print("response is JSON string: ", response[0]["text"])


        if response_json.status == "FAIL":
            update = {
                "messages": [
                    AIMessage(
                        content=summary_text,
                        name="order_confirmation_node",
                        response_metadata={
                            "interrupt": True
                        }
                    ),
                    HumanMessage(content="Y"),
                    AIMessage(
                        content="We are experiencing some technical issues in processing your order. \n\nPlease place your order by visiting your cart.", 
                        name="order_confirmation_node"
                    )
                ],
                "next_node": "__end__",
                "from_node": "order_confirmation_node",
                "checkoutInfo": None,
            }
            goto = "__end__"
            return update, goto


        update = {
            "messages": [
                AIMessage(
                    content=summary_text,
                    name="order_confirmation_node",
                    response_metadata={
                        "interrupt": True
                    }
                ),
                HumanMessage(content="Y"),
                AIMessage(
                    content=f"You order has been places successfully.\nYour order number is {response_json.orderId}.\nWe have sent a notification email to your email.", 
                    name="order_confirmation_node"
                )
            ],
            "next_node": "__end__",
            "from_node": "order_confirmation_node",
            "checkoutInfo": None,
        }
        goto = "__end__"
        return update, goto
