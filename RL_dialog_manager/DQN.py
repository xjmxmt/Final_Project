import random
import numpy as np
import yaml
import logging
import os
import math
import torch
import torch.nn as nn
import torch.optim as optim
from user_simulator import UserSimulator
from utils.base_config import BareConfig


class DQN(nn.Module):

    """
    Deep Q-learning, to learn the policy responsible for searching best action
    """

    def __init__(self, state_shape, action_shape, hidden_shape=24):

        super(DQN, self).__init__()
        self.state_shape = state_shape
        self.action_shape = action_shape
        self.hidden_shape = hidden_shape
        self.linear1 = nn.Linear(state_shape, hidden_shape)
        self.relu = nn.ReLU()
        self.linear2 = nn.Linear(hidden_shape, action_shape)

    def forward(self, x):

        h1 = self.linear1(x)
        a1 = self.relu(h1)
        h2 = self.linear2(a1)
        return h2


class DQNTrainer:

    def __init__(self,
                 batch_size,
                 max_iteration,
                 max_rounds,
                 update_steps,
                 num_user_action,
                 num_emotion,
                 state_shape,
                 action_shape,
                 hidden_shape=24,
                 gamma=0.999):

        self.batch_size = batch_size
        self.max_iteration = max_iteration
        self.update_steps = update_steps
        self.num_user_action = num_user_action
        self.num_emotion = num_emotion
        self.state_shape = state_shape
        self.action_shape = action_shape

        self.max_rounds = max_rounds
        self.user_simulator = UserSimulator(max_rounds=self.max_rounds, gamma=gamma)

        # do the training
        self.main_network = DQN(state_shape, action_shape, hidden_shape=hidden_shape)
        # save weights after every update_steps
        self.target_network = DQN(state_shape, action_shape, hidden_shape=hidden_shape)

        self.possible_agent_actions = ['goto_next_state', 'smile', 'gaze', 'look_away', 'goto_encourage_state', 'say_again']
        self.actions_dict = {'goto_next_state': 0, 'smile': 1, 'gaze': 2,
                             'look_away': 3, 'goto_encourage_state': 4, 'say_again': 5}

        # loss: Huber loss function
        self.criterion = nn.SmoothL1Loss()
        self.optimizer = optim.RMSprop(self.main_network.parameters())

    def from_action_to_idx(self, action):

        return self.actions_dict[action]

    def get_state_vector(self, round_num, last_user_action_idx, last_user_emotion_idx, last_agent_action_idx):

        # one-hot vector of round index
        round_num_vector = torch.zeros((int(math.log(cfg.max_rounds, 2)), ))
        round_num_vector[round_num] = 1.0

        # one-hot vector of last user action
        last_user_action_vector = torch.zeros((self.num_user_action, ))
        last_user_action_vector[last_user_action_idx] = 1.0

        # one-hot vector of last user emotion
        last_user_emotion_vector = torch.zeros((self.num_emotion, ))
        last_user_emotion_vector[last_user_emotion_idx] = 1.0

        # one-hot vector of last agent action
        last_agent_action_vector = torch.zeros((self.action_shape, ))
        last_agent_action_vector[last_agent_action_idx] = 1.0

        state_vector = torch.hstack([round_num_vector, last_user_action_vector,
                                     last_user_emotion_vector, last_agent_action_vector]).view(-1)

        return state_vector

    def clipping_rewards(self, rewards):

        for idx in range(len(rewards)):
            value = rewards[idx]
            if value >= 2: rewards[idx] = -1
            elif 1 <= value < 2: rewards[idx] = 0
            else: rewards[idx] = 1

        return rewards

    def train_net(self):

        """
        Function to train the DQN, and save the weights.
        Using random policy and user simulator to generate data.

        User actions = ['proceed', 'silence', 'end_dialog']
        Agent actions = ['goto_next_state', 'smile', 'gaze', 'look_away', 'goto_encourage_state', 'say_again']
        """

        # initializer: HeUniform()

        for iteration in range(self.max_iteration):

            round_num = 0
            rewards_array = torch.zeros((self.batch_size, ))
            last_user_action = None
            last_user_action_idx = None
            last_user_emotion = None
            last_user_emotion_idx = None

            initial_action = random.choice(self.possible_agent_actions)
            initial_action_idx = self.from_action_to_idx(initial_action)
            last_agent_action = initial_action
            last_agent_action_idx = initial_action_idx
            state_vector = torch.zeros((self.state_shape, ))

            for batch_idx in range(self.batch_size):
                self.user_simulator.reset()
                while True:
                    if round_num > self.max_rounds: break
                    elif last_user_action is 'end_dialog': break
                    elif batch_idx == 0:
                        level, action_idx, emotion, action = self.user_simulator.update_user_emotion_and_action(initial_action)
                        last_user_action, last_user_action_idx = action, action_idx
                        last_user_emotion, last_user_emotion_idx = emotion, level
                        round_num += 1
                    else:
                        state_vector = self.get_state_vector(round_num, last_user_action_idx, last_user_emotion_idx, last_agent_action_idx)
                        action_logits = self.main_network(state_vector)
                        action = self.possible_agent_actions[torch.max(action_logits)]
                        level, action_idx, emotion, action = self.user_simulator.update_user_emotion_and_action(action)
                        last_user_action, last_user_action_idx = action, action_idx
                        last_user_emotion, last_user_emotion_idx = emotion, level
                        round_num += 1

                reward = self.user_simulator.get_rewards()
                rewards_array[batch_idx] = reward

            clipped_rewards = self.clipping_rewards(rewards_array)
            # logging.info(f'Iteration {iteration+1}/{self.max_iteration}. '
            #              f'One dialogue finished. Rewards: {clipped_rewards}')

            action_logits = self.main_network(state_vector)

            loss = self.criterion(action_logits, clipped_rewards)
            logging.info(f'Iteration {iteration+1}/{self.max_iteration}. '
                         f'One dialogue finished. Loss: {loss}')

            self.optimizer.zero_grad()
            loss.backward()
            for param in self.main_network.parameters():
                param.grad.data.clamp_(-1, 1)
            self.optimizer.step()

            if iteration % self.update_steps == 0:
                self.target_network.load_state_dict(self.main_network.state_dict())


if __name__ == '__main__':

    config_path = 'configs/experiment.yaml'

    with open(config_path) as file:
        cfg = BareConfig()
        yaml_data = yaml.load(file, Loader=yaml.FullLoader)
        cfg.merge_yaml(yaml_data)

    logging.basicConfig(
        filename=cfg.dqn_log_path,
        filemode='a+',
        level=logging.INFO,
        format='%(levelname)s: %(message)s'
    )

    num_state = int(math.log(cfg.max_rounds, 2)) + cfg.num_user_action * 2 + cfg.action_shape * 2

    trainer = DQNTrainer(
        batch_size=cfg.batch_size,
        max_iteration=cfg.max_iteration,
        max_rounds=cfg.max_rounds,
        update_steps=cfg.update_steps,
        num_user_action=cfg.num_user_action,
        num_emotion=cfg.num_emotion,
        state_shape=num_state,
        action_shape=cfg.action_shape,
        hidden_shape=cfg.hidden_shape,
        gamma=cfg.gamma
    )

    trainer.train_net()
