from contextlib import asynccontextmanager
import uuid

from fastapi import Depends, FastAPI, Request, Response, Body
from fastapi.middleware.cors import CORSMiddleware
from langchain_core.messages import AIMessage, BaseMessage, HumanMessage, ToolMessage
import py_eureka_client.eureka_client as eureka
from langgraph.types import Command, StateSnapshot
from pydantic import BaseModel

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

app.add_middleware(
    CORSMiddleware,
    allow_origins=[
        "http://127.0.0.1:5173",
        "http://localhost:5173",
    ],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

class ChatRequest(BaseModel):
    query: str

@app.get("/session")
async def create_session(
    response: Response, 
    request: Request
):

    existing_session = request.cookies.get(
        "chat_session_id"
    )

    graph = request.app.state.graph

    if existing_session:

        thread_id = request.cookies.get(
            "chat_session_id"
        )

        config = {"configurable": {"thread_id": thread_id}}
    
        snapshot: StateSnapshot = await graph.aget_state(config)

        if not snapshot:
            return {
                "messages": [
                    {"type": "ai", "content": "Hi, How can i assist you today?"}
                ],
                "thread_id": thread_id
            }
        return {
            "messages": snapshot.values.get(
                "messages",
                []
            ),
            "thread_id": thread_id
        }

    session_id = str(uuid.uuid4())

    response.set_cookie(
        key="chat_session_id",
        value=session_id,
        httponly=True,
        # secure=True,
        samesite="lax"
    )

    return {
        "messages": [
            {"type": "ai", "content": "Hi, How can i assist you today?"}
        ],
        "thread_id": session_id
    }


@app.post("/chat")
async def chat(
    req: Request,
    body: ChatRequest,
    current_user=Depends(get_current_user),
):
    print("current_user", current_user)

    user_id = current_user["user_id"]
    graph = req.app.state.graph

    thread_id = req.cookies.get(
        "chat_session_id"
    )

    print(thread_id) 

    config = {"configurable": {"thread_id": thread_id}}

    snapshotBefore: StateSnapshot = graph.get_state(config)
    
    if len(snapshotBefore.interrupts) > 0:
        result = await graph.ainvoke(
                Command(resume=body.query),
                config
            )
    else:
        result = await graph.ainvoke(
            {
                "messages":[
                    HumanMessage(
                        content=body.query, name="user"
                    )
                ],
                "user_id": user_id,
            },
            config
        )

    snapshotAfter: StateSnapshot = graph.get_state(config)

    if len(snapshotAfter.interrupts) > 0:
        interrupt = snapshotAfter.interrupts[0]
        return {"type": "assistant", "content": interrupt.value, "response_metadata": {"interrupt": True}}

    return {"type": "assistant", "content": result["messages"][-1].content}

@app.get("/history")
async def history(
    thread_id: str,
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
