from typing import Literal

from pydantic import BaseModel, Field

class CartItem(BaseModel):
    product_id: str
    product_name: str
    sku: str
    color: str
    size: str
    price: str
    quantity: float

class Cart(BaseModel):
    cart_id: str
    cart_items: list[CartItem]
    subtotal: float


class CartAgentResponse(BaseModel):

    status: Literal[
        "COMPLETED",
        "PRODUCT_NOT_FOUND",
        "NEED_CLARIFICATION",
        "FAILED",
    ] = Field(
        description="""
Status of the cart operation.

Use:
- COMPLETED:
    The requested cart action was successfully completed.
    Examples:
    - Product added to cart.
    - Product removed from cart.
    - Quantity updated.
    - Cart fetched successfully.

- PRODUCT_NOT_FOUND:
    The requested product could not be identified from the 'Active products list' or 'User Cart'.

- NEED_CLARIFICATION:
    More information is required from user before performing the cart operation.
    Examples:
    - Multiple products match the request.
    - User did not specify which product.
    - Required variant (size, colour, etc.) is missing.
    - Required quantity is missing.
- FAILED:
    Any error occured while processing a tool or Tool returns 'Service unavilable'

Do not include:
- Internal agent details.
- Tool calls.
- Database information.
- Routing instructions.
"""
    )

    message: str = Field(
        description="""
Natural language response for the main agent to show to the user.

Rules:
- COMPLETED:
    Return a confirmation message describing what was done.
    Ask next possible action questions like 
     - Would you like me to place order?
     - Are you looking for any other products?

- PRODUCT_NOT_FOUND:
    Return a message for the MAIN AGENT describing what the product agent should
    do next.
    Example:
    'Search for product X requested by the user so the user can select one
    to add to the cart.'

- NEED_CLARIFICATION:
    Ask only the minimum clarification needed to continue.
    Do not mention internal tools or implementation details.

- FAILED:
    Politely say to user that currently unable to process request and try again later
"""
    )

    cart: Cart | None = Field(
        default=None,
        description="""
Updated cart after a successful cart operation.

Populate only when status is COMPLETED.

Return None when:
- status is PRODUCT_NOT_FOUND
- status is NEED_CLARIFICATION
- status is Failed
"""
    )

    