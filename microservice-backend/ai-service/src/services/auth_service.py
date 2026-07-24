import traceback

from fastapi import Depends, HTTPException
from fastapi.security import HTTPAuthorizationCredentials, HTTPBearer
import py_eureka_client.eureka_client as eureka_client
import json

from src.core.config import AUTH_SERVICE_APP_NAME

security = HTTPBearer()

# TODO: move urls to .env
async def validate_token(token: str):
    try:
        response = await eureka_client.do_service_async(
            AUTH_SERVICE_APP_NAME,
            f"/auth/isValidToken?token={token}",
        )

        if isinstance(response, str):
            response = json.loads(response)
            print("response is JSON string: ", response)

        if response.get("success"):
            print("success")
            return response["response"]   # userId + authorities

        print("fail")
        return None
    except Exception as e:
        print(f"Failed to communicate via Eureka: {e}")
        return None
    
async def get_current_user(
    credentials: HTTPAuthorizationCredentials = Depends(security)
):

    token = credentials.credentials

    try:
        user = await validate_token(token=token)

        if not user:
            raise HTTPException(status_code=401, detail="Invalid token")

        if "ROLE_USER" not in user["authorities"]:
            raise HTTPException(status_code=403, detail="Forbidden")

        id = user["userId"]

        return {
            "user_id": id,
        }

    except Exception as e:
        print(f"Failed to get current user: {e}")
        return None
