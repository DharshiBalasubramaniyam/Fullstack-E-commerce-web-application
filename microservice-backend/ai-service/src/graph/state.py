from typing import Annotated, Literal, TypedDict

from langchain_core.messages import BaseMessage
from langgraph.graph import add_messages

from src.registry.agent_registry import agent_registry


class GraphState(TypedDict):

    user_id: str | None
    
    messages: Annotated[list[BaseMessage], add_messages]

    products: list | None
    # selected_product: dict | None

    cart: dict | None

    orders: list

    next_node: Literal[
        "product_node",
        "cart_node",
        "main_node",
        "__end__"
    ]
    message_for_next_node: str
    message_from_next_node: str




