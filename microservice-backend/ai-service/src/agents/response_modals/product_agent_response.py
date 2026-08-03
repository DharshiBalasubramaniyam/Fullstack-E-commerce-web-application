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
        description="""
Response message containing the result of the product operation.

The message is consumed by the main agent. It should provide enough information
for the main agent to either:
- show a response to the user, or
- decide the next workflow step.

Rules based on operation:

SEARCH:
- Summarize the products found.
- Include relevant product names, variants, prices, and important details.
- If products were found, ask the user to select products when a selection is
  required before continuing (for example, adding to cart or place order).
- If no products are found, explain that no matching products were found and
  suggest alternatives or ask for another search query.

COMPARE:
- Provide a clear comparison between the requested products.
- Include differences such as price, size, variants, features, benefits, and
  suitability.
- Do not ask the user to select a product unless the user explicitly requested
  a recommendation.

DETAILS:
- Provide detailed information about the requested product.
- Include available variants, pricing, specifications, and other useful details.
- Do not modify the current product selection.

Do not include:
- Internal agent details.
- Tool calls.
- Database information.
- Routing instructions.
"""
    )

    operation: Literal[
        "SEARCH",
        "COMPARE",
        "DETAILS"
    ] = Field(
        description="""
The product operation that was performed.

Use:
- SEARCH:
    When finding products based on user criteria such as keywords, category,
    price range, brand, or other filters.

- COMPARE:
    When comparing two or more products requested by the user.

- DETAILS:
    When retrieving detailed information about a specific product.
"""
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