from contextlib import asynccontextmanager

from fastapi import Depends, FastAPI, Request
from langchain_core.messages import AIMessage, BaseMessage, HumanMessage, ToolMessage
import py_eureka_client.eureka_client as eureka

from src.core.config import APP_NAME, APP_PORT, EUREKA_HOST, EUREKA_SERVER
from src.registry.agent_registry import agent_registry
from config import *

from dotenv import load_dotenv

from src.graph.workflow import build_graph
from src.services.auth_service import get_current_user
load_dotenv()

@asynccontextmanager
async def lifespan(app: FastAPI):

    # Connect Eureka
    await eureka.init_async(
        instance_host=EUREKA_HOST,
        eureka_server=EUREKA_SERVER,
        app_name=APP_NAME,
        instance_port=int(APP_PORT)
    )
    print("Registered with Eureka")

    # Initialize agents and add to registry
    await agent_registry.initialize()

    # Build LangGraph once
    app.state.graph = build_graph()

    yield
    
    # Cleanup
    await eureka.stop()
    print("Deregistered from Eureka, Stopped MCP")


app = FastAPI(lifespan=lifespan)

# TODO: Add user id in main node 
# TODO: Add checkpointer and thread id
# TODO: Write hitsory in a file
@app.get("/chat/{query}")
async def chat(
    query: str, 
    thread_id: int,
    req: Request,
    current_user=Depends(get_current_user),
):
    print("current_user", current_user)

    user_id = current_user["user_id"]
    graph = req.app.state.graph

    config = {"configurable": {"thread_id": thread_id}}

    result = await graph.ainvoke(
        {
            "messages":[
                HumanMessage(
                    content=query, name="user"
                )
            ],
            "user_id": user_id,
        },
        config
    )

    return result["messages"][-1].content

@app.get("/history")
async def chat(
    thread_id: int,
    req: Request,
):
    graph = req.app.state.graph
    
    config = {"configurable": {"thread_id": thread_id}}

    return graph.get_state(config)
    

def printMessages(mesages: list[BaseMessage]):
    for message in mesages:
        if isinstance(message, HumanMessage):
            print(f"[/chat-{message.name}] HumanMessage: {message.content}")

        elif isinstance(message, AIMessage):
            if message.tool_calls:
                for tool_call in message.tool_calls:
                    print(
                        f"[/chat-{message.name}] AIMessage: tool_call={tool_call['name']}, "
                        f"args={tool_call['args']}"
                    )
            else:
                # AI's final response
                print(f"[/chat-{message.name}] AIMessage: {message.text}")

        elif isinstance(message, ToolMessage):
            print(f"[/chat-{message.name}] ToolMessage: {message.content}")
