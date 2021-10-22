import random


class Agent:
    def __init__(self, state_size, num_actions, eps):
        self.state_size = state_size
        self.num_actions = num_actions
        self.eps = eps

    def get_action(self, state, use_rule=False):
        # self.eps is initialized to the starting epsilon and does NOT get annealed
        if self.eps > random.random():
            index = random.randint(0, self.num_actions - 1)
            # self._map_index_to_action(index) takes an index and maps the action from all possible agent actions
            action = self._map_index_to_action(index)
            return index, action
        else:
            if use_rule:
                return self._rule_action()
            else:
                return self._dqn_action(state)
