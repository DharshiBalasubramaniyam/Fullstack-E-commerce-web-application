from contextlib import asynccontextmanager

from fastapi import FastAPI
import py_eureka_client.eureka_client as eureka

from src.agents.main_agent import MainAgent
from src.core.registry.agent_registry import agent_registry
from src.agents.product_agent import ProductAgent
from config import *

from pydantic import BaseModel

from src.core.mcp.mcp_client import MCPClient

from dotenv import load_dotenv
load_dotenv()

product_mcp = MCPClient(
    "Product",
    "http://localhost:8080/api/product-service/mcp"
)
product_agent = ProductAgent(product_mcp)
main_agent = MainAgent()

@asynccontextmanager
async def lifespan(app: FastAPI):

    # Connect Eureka
    await eureka.init_async(
        instance_host="localhost",
        eureka_server="http://localhost:8761/eureka/",
        app_name="AI-SERVICE",
        instance_port=9012
    )
    print("Registered with Eureka")

    # Connect MCP servers
    await product_mcp.connect()
    print("Connected with product mcp")

    # Initialize agents
    await product_agent.initialize()
    await main_agent.initialize()
    print("Initialized agents")

    agent_registry.register(
        "product",
        product_agent
    )
    agent_registry.register(
        "main",
        main_agent
    )
    print("Added agents to registry")

    yield
    
    # Cleanup
    await eureka.stop()
    await product_mcp.disconnect()

    print("Deregistered from Eureka, Stoped MCP")


app = FastAPI(lifespan=lifespan)

@app.get("/chat/{query}")
async def chat(query: str):
    agent = agent_registry.get("main")

    return await agent.invoke(query)

@app.get("/")
async def root(query: str):
    return "working..."