from typing import Literal
from pydantic import BaseModel, Field

class OrderRequest(BaseModel):
    firstName: str
    lastName: str
    address: str
    city: str
    phoneNo: str
    
class MainAgentResponse(BaseModel):

    next_node: Literal[
        "product_node",
        "cart_node",
        "order_preparation_node",
        "__end__"
    ] = Field(
        description="""
        The next node to route.
        - '__end__': This represents end user. Use this when the **context** contains the answer of the user query or you need to ask any clarification questions regarding an action from user.
        - 'product_node': This represents product worker agent. Use this to search products, get product details or compare products.
        - 'cart_node': This represents cart worker agent. Use this to add items to cart, update items in cart, remove items from cart or get cart information.
        - 'order_preparation_node': This represents order preparation worker agent. Use this to place orders.
        """
    )


    message: str = Field(
        description="""
        The message for the next node.
        If next_node is a worker agent, this should be the instruction
        that the worker agent needs to perform. 
        If next_node is __end__, this should be the final response
        shown to the user or clarification question from user regarding user action.
        """
    )

    reasoning: str = Field(
        description="""
Brief 1-2 sentence explanation based on current context and history.
"""
    )