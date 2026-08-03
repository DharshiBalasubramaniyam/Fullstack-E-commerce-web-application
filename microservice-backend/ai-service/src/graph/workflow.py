from langgraph.graph import StateGraph, START

from src.core.config import MONGODB_CONNECTION_URL
from src.graph.state import GraphState
from src.graph.nodes.main_node import main_node
from src.graph.nodes.product_node import product_node
from src.graph.nodes.cart_node import cart_node
from src.graph.nodes.order_preparation_node import order_preparation_node
from src.graph.nodes.order_confirmation_node import order_confirmation_node

from langgraph.checkpoint.mongodb import MongoDBSaver
from pymongo import MongoClient


def build_graph():

    builder = StateGraph(GraphState)

    builder.add_node(
        "main_node",
        main_node,
    )
    builder.add_node(
        "product_node",
        product_node,
    )
    builder.add_node(
        "cart_node",
        cart_node,
    )

    builder.add_node(
        "order_preparation_node",
        order_preparation_node,
    )

    builder.add_node(
        "order_confirmation_node",
        order_confirmation_node,
    )

    builder.add_edge(
        START,
        "main_node"
    )

    client = MongoClient(MONGODB_CONNECTION_URL)

    checkpointer = MongoDBSaver(client) 

    graph = builder.compile(checkpointer=checkpointer)


    # print(graph.aget_state())

    return graph

