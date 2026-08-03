from dotenv import load_dotenv
import os

load_dotenv()

APP_NAME = os.getenv("APP_NAME")
APP_PORT = os.getenv("APP_PORT")

EUREKA_HOST = os.getenv("EUREKA_HOST")
EUREKA_SERVER = os.getenv("EUREKA_SERVER")

GEMINI_API_KEY = os.getenv("GEMINI_API_KEY")
GEMINI_MODEL = os.getenv("GEMINI_MODEL")

PRODUCT_MCP_URL = os.getenv("PRODUCT_MCP_URL")
CART_MCP_URL = os.getenv("CART_MCP_URL")
ORDER_MCP_URL = os.getenv("ORDER_MCP_URL")

AUTH_SERVICE_APP_NAME = os.getenv("AUTH_SERVICE_APP_NAME")
MONGODB_CONNECTION_URL = os.getenv("MONGODB_CONNECTION_URL")