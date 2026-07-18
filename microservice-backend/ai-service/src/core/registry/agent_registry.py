class AgentRegistry:

    def __init__(self):
        self.agents = {}


    def register(self, name, agent):
        self.agents[name] = agent


    def get(self, name):
        return self.agents[name]


agent_registry = AgentRegistry()