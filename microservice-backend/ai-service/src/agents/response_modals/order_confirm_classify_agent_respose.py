from typing import Literal

from pydantic import BaseModel


class OrderConfirmationClassificationAgentResponse(BaseModel):
    action: Literal[
        "CONFIRMED",
        "CANCELLED",
        "MODIFY_CART"
    ]