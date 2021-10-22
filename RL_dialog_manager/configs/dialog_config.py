"""
The goal of the dqn is to take a state and produce a close-to-optimal action.
Specifically, the agent receives a state which represents the history of the current
conversation from the dialogue state tracker (ST), and picks a dialogue response to take.

2 stages: warm-up & training
    warm-up runs before training to fill the agent’s memory using usually a random policy
    the agent uses a very basic rule-based policy during warm-up

"""

"""
User config
"""

# all possible intents of user, for one-hot encoding
user_intents = ['request_answer', 'request_method', 'useless_instruction', 'done', 'give_up', 'thanks']

actions = ['encourage', 'say_goodbye', 'greeting']

"""
Agent config
"""

# possible inform and request slots for the agent
agent_inform_slots = ['dividend', 'divisor', 'result']
agent_request_slots = ['dividend', 'divisor', 'emotion']

# possible actions for agent
agent_actions = [
    {'intent': 'done', 'inform_slots': {}, 'request_slots': {}},  # triggers closing off conversation
    {'intent': 'match_found', 'inform_slots': {}, 'request_slots': {}}
]
for slot in agent_inform_slots:
    # 'PLACEHOLDER' will be filled in
    agent_actions.append({'intent': 'inform', 'inform_slots': {slot: 'PLACEHOLDER'}, 'request_slots': {}})
for slot in agent_request_slots:
    # 'UNK' will be requested from user
    agent_actions.append({'intent': 'request', 'inform_slots': {}, 'request_slots': {slot: 'UNK'}})

"""
Global config
"""

# all possible user intents
all_intents = ['inform', 'request', 'done', 'thanks', 'reject']

# all possible slot types
all_slots = ['dividend', 'divisor', 'emotion']






# # Possible inform and request slots for the agent
# agent_inform_slots = ['answer']
#
# # Possible actions for agent
# agent_actions = [
#     {'intent': 'done', 'inform_slots': {}, 'request_slots': {}},  # Triggers closing of conversation
#     {'intent': 'match_found', 'inform_slots': {}, 'request_slots': {}}
# ]
# for slot in agent_inform_slots:
#     agent_actions.append({'intent': 'inform', 'inform_slots': {slot: 'PLACEHOLDER'}, 'request_slots': {}})
# for slot in agent_request_slots:
#     agent_actions.append({'intent': 'request', 'inform_slots': {}, 'request_slots': {slot: 'UNK'}})
#
# # Rule-based policy request list
# rule_requests = ['moviename', 'starttime', 'city', 'date', 'theater', 'numberofpeople']
# # These are possible inform slot keys that cannot be used to query
# # no_query_keys = ['numberofpeople', usersim_default_key]


