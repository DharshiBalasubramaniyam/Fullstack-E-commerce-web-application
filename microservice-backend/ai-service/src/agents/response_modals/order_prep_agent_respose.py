from typing import Literal
from pydantic import BaseModel, Field

from src.agents.response_modals.cart_agent_response import Cart

class CheckoutInfo(BaseModel):
    user_id: str
    cart: Cart
    firstName: str
    lastName: str
    address: str
    city: str
    phoneNo: str


class OrderPreparationAgentResponse(BaseModel):

    status: Literal[
        "COMPLETED",
        "PRODUCT_MISSING_IN_CART",
        "NEED_CLARIFICATION",
        "PARTIAL_INFO",
        "FAILED"
    ] = Field(
        description="""
Status of the order operation.

Use:

COMPLETED:
- Cart id and other user information were collected.

PRODUCT_MISSING_IN_CART:
- The cart does not have any products.

NEED_CLARIFICATION:
- Request user information from the user.
- Examples:
  - Delivery address missing.
  - Phone number missing.

FAILED:
- The operation failed due to an internal error or external service failure.
"""
    )

    message: str = Field(
        description="""
Message for the main agent.

The main agent uses this message to respond to the user or decide the next step.

Rules:

For COMPLETED:
- Return None

For PRODUCT_MISSING_IN_CART:
- Explain that the products not in the cart
- Prompt to add the items to cart
- Mention the product name and quantity information

For NEED_CLARIFICATION:
- Ask the user only for the missing information required to continue.

For FAILED:
- Provide a user-friendly error message.
- Do not expose internal errors, stack traces, or implementation details.
"""
    )

    checkoutInfo: CheckoutInfo | None