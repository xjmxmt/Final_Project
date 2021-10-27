from collections import namedtuple, deque
import random

# state: round_num, last_user_action_idx, last_user_emotion_idx, last_agent_action_idx
Transition = namedtuple(
    'Transition',
    ('round_num_2', 'last_user_action_idx_2', 'last_user_emotion_idx_2', 'last_agent_action_idx_2',  # last state
     'round_num_1', 'last_user_action_idx_1', 'last_user_emotion_idx_1', 'last_agent_action_idx_1',  # state
     'reward'))


class Structure:

    def __init__(self, size):
        self.deque = deque(maxlen=size)  # to have a maximum size
        self.set = set()  # to keep element unique

    def append(self, value):
        if value not in self.set:
            if len(self.deque) == self.deque.maxlen:
                discard = self.deque.popleft()
                self.set.discard(discard)
            self.deque.append(value)
            self.set.add(value)


class ReplayMemory(object):

    def __init__(self, capacity):
        self.memory = Structure(size=capacity)

    def push(self, *args):
        """Save a transition"""
        self.memory.append(Transition(*args))

    def sample(self, batch_size):
        return random.sample(self.memory.deque, batch_size)

    def __len__(self):
        return len(self.memory.deque)
