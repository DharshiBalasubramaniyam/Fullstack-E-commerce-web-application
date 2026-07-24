from typing import Literal

from pydantic import BaseModel, Field

class ProductVarient(BaseModel):
    sku: str
    color: str
    size: str
    price: str
    inStock: str


class Product(BaseModel):
    product_id: str
    product_name: str
    inventory: list[ProductVarient]


class ProductAgentResponse(BaseModel):

    message: str = Field(
        description="A conversational response summarizing the result for Main Agent."
    )

    operation: Literal[
        "SEARCH",
        "COMPARE",
        "DETAILS",
        "OTHER"
    ] = Field(
        description="The type of product operation that was performed."
    )


    products: list[Product] | None = Field(
        default=None,
        description=(
            "The updated list of products when the operation changes the current "
            "product selection (e.g., SEARCH). "
            "Leave as null for operations like COMPARE or DETAILS that do not "
            "change the current product list."
        )
    )