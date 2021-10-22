import numpy as np
import torch
import yaml
import logging
import copy
from utils.base_config import BareConfig
from configs.dialog_config import all_intents, all_slots
from utils.functions import convert_list_to_dict


class StateTracker:
    """
    State tracker, infer about the state of the dialogue, given all the history up to that turn
    """

    def __init__(self):

        self.round_num = 0
        self.max_round_num = 10
        self.history = []  # history actions
        self.current_informs = {}

        self.num_intents = len(all_intents)
        self.num_slots = len(all_slots)
        self.intents_dict = convert_list_to_dict(all_intents)
        self.slots_dict = convert_list_to_dict(all_slots)

        self.none_state = np.zeros((self.get_state_size(), ))

        self.math_tutor = MathTutor()

    def get_state_size(self):

        return 2 * self.num_intents + 5 * self.num_slots + 1 + self.max_round_num

    def print_history(self):

        for action in self.history:
            print('printing history...')
            print(action)

    def get_state(self, done=False):

        # if done then return none state which is a state filled with zeros
        if done:
            return self.none_state

        user_action = self.history[-1]
        last_agent_action = self.history[-2] if len(self.history) > 1 else None

        # one-hot vector of intents
        user_action_vector = np.zeros((self.num_intents, ))
        user_action_vector[self.intents_dict[user_action['inform']]] = 1.0

        # bag of inform slots to represent user action
        user_inform_slot_vector = np.zeros((self.num_slots, ))
        for key in user_action['inform_slots'].keys():
            user_inform_slot_vector[self.slots_dict[key]] = 1.0

        # bag of request slots to represent user action
        user_request_slot_vector = np.zeros((self.num_slots, ))
        for key in user_action['request_slots'].keys():
            user_request_slot_vector[self.slots_dict[key]] = 1.0

        # bag of filled_in slots
        current_slot_vector = np.zeros((self.num_slots, ))
        for key in self.current_informs:
            current_slot_vector[self.slots_dict[key]] = 1.0

        # one-hot vector of last agent intent
        agent_action_vector = np.zeros((self.num_intents,))
        if last_agent_action:
            agent_action_vector[self.intents_dict[last_agent_action['intent']]] = 1.0

        # one-hot vector of last agent inform slots
        agent_inform_slot_vector = np.zeros((self.num_slots,))
        if last_agent_action:
            for key in last_agent_action['inform_slots'].keys():
                agent_inform_slot_vector[self.slots_dict[key]] = 1.0

        # one-hot vector of last agent request slots
        agent_request_slot_vector = np.zeros((self.num_slots,))
        if last_agent_action:
            for key in last_agent_action['request_slots'].keys():
                agent_request_slot_vector[self.slots_dict[key]] = 1.0

        # ???
        # value representation of the round num
        turn_vector = np.zeros((1,)) + self.round_num / 5.

        # one-hot vector of the round num
        turn_onehot_vector = np.zeros((self.max_round_num,))
        turn_onehot_vector[self.round_num - 1] = 1.0

        state_representation = np.hstack(
            [user_action_vector, user_inform_slot_vector, user_request_slot_vector, current_slot_vector, agent_action_vector,
             agent_inform_slot_vector, agent_request_slot_vector, turn_vector, turn_onehot_vector]).flatten()

        return state_representation

    def update_state_agent(self, agent_action):

        pass

    def update_state_user(self):

        pass


if __name__ == '__main__':

    config_path = 'configs/experiment.yaml'
    with open(config_path) as file:
        cfg = BareConfig()
        yaml_data = yaml.load(file, Loader=yaml.FullLoader)
        cfg.merge_yaml(yaml_data)

    logging.basicConfig(
        filename=cfg.log_path,
        filemode='a+',
        level=logging.INFO,
        format='%(levelname)s: %(message)s'
    )

    if cfg.cuda:
        device = torch.device('cuda')
    else:
        device = torch.device('cuda' if torch.cuda.is_available() else 'cpu')
    logging.info(f'Using device {device}')


    num_actions = 5
    hidden_size = 80
