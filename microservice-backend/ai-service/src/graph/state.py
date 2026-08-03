from typing import Annotated, Literal, TypedDict

from langchain_core.messages import BaseMessage
from langgraph.graph import add_messages

from src.agents.response_modals.product_agent_response import Product
from src.agents.response_modals.order_prep_agent_respose import CheckoutInfo
from src.registry.agent_registry import agent_registry


class GraphState(TypedDict):

    user_id: str | None
    
    messages: Annotated[list[BaseMessage], add_messages]

    products: list[Product] | None

    cart: dict | None

    from_node: Literal[
        "product_node",
        "cart_node",
        "order_preparation_node",
        "order_confirmation_node",
        "main_node",
    ]
    
    next_node: Literal[
        "product_node",
        "cart_node",
        "order_preparation_node",
        "order_confirmation_node",
        "main_node",
        "__end__"
    ]
    message_for_next_node: str
    message_from_next_node: str

    checkoutInfo: CheckoutInfo | None




