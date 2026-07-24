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

    message: str = Field(
        description="A conversational response summarizing the result for Main Agent."
    )

    cart: Cart = Field(
        description="User cart information after adding an item, updating quantity or removing an item"
    )