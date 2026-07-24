from typing import Literal
from pydantic import BaseModel, Field

class MainAgentResponse(BaseModel):

    next_node: Literal[
        "product_node",
        "cart_node",
        "__end__"
    ] = Field(
        description="""
        The next action to perform.
        - Use 'product_node' when product search, product details, or product-related information is required.
        - Use 'cart_node' when cart-related action is required.
        - Use '__end__' when the response can be directly given to the user without calling another agent.
        """
    )


    message: str = Field(
        description="""
        The instruction for the next node.
        If next_node is a worker agent, this should describe the task
        that the worker agent needs to perform.
        If next_node is __end__, this should be the final response
        shown to the user.
        """
    )


    # arguments: dict | None = Field(
    #     default=None,
    #     description="""
    #     Additional structured arguments required by the next agent.
    #     Use this field to pass information needed by worker agents.
    #     Always send user_id as an argument when next agent is "cart_node" or "order_node"

    #     Examples:
    #     - Get details of a product:
    #       {
    #         "product_id": ["1234"],
    #       }
    #     - Compare products:
    #       {
    #         "product_ids": ["1234", "6789"],
    #       }

    #     - Add to cart:
    #       {
    #         "user_id": "6er33r"
    #         "product_id": "12345",
    #         "sku": "12345-1",
    #         "quantity": 2
    #       }

    #     Keep this empty when no additional arguments are required.
    #     """
    # )