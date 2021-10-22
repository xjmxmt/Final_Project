import matplotlib.pyplot as plt
import re
import numpy as np
import math
import torch


user_actions = ['proceed', 'silence', 'end_dialog']

user_emotions = ['Affection', 'Anger', 'Annoyance', 'Anticipation', 'Aversion', 'Confidence', 'Disapproval', 'Disconnection',
                 'Disquietment', 'Doubt/Confusion', 'Embarrassment', 'Engagement', 'Esteem', 'Excitement', 'Fatigue', 'Fear',
                 'Happiness', 'Pain', 'Peace', 'Pleasure', 'Sadness', 'Sensitivity', 'Suffering', 'Surprise', 'Sympathy', 'Yearning']
emotion_level1 = ['Disconnection', 'Disquietment', 'Doubt/Confusion', 'Embarrassment']  # penalty = 1
emotion_level2 = ['Annoyance', 'Disapproval', 'Fatigue', 'Fear']  # penalty = 2
emotion_level3 = ['Anger', 'Aversion', 'Pain', 'Sadness', 'Suffering']  # penalty = 3
emotion_level0 = ['Affection', 'Anticipation', 'Confidence', 'Engagement', 'Esteem',
                  'Excitement', 'Happiness', 'Peace', 'Pleasure', 'Sensitivity',
                  'Surprise', 'Sympathy']  # penalty = 0

agent_actions = ['goto_next_state', 'smile', 'gaze', 'look_away', 'goto_encourage_state', 'say_again']


def convert_user_action_to_idx(action):

    assert action in user_actions, 'Not a proper user action!'
    return user_actions.index(action)


def convert_idx_to_user_action(idx):

    assert idx < len(user_actions), 'Not a proper user action!'
    return user_actions[idx]


def convert_user_emotion_to_idx(emotion):

    assert emotion in user_emotions, 'Not a proper user emotion!'

    level = None
    if emotion in emotion_level1:
        level = 1
    elif emotion in emotion_level2:
        level = 2
    elif emotion in emotion_level3:
        level = 3
    elif emotion in emotion_level0:
        level = 0
    return level


def convert_agent_action_to_idx(action):

    assert action in agent_actions, 'Not a proper agent action!'
    return agent_actions.index(action)


def convert_idx_to_agent_action(idx):

    assert idx < len(agent_actions), 'Not a proper agent action!'
    return agent_actions[idx]


def get_state_vector(cfg, round_num, last_user_action_idx, last_user_emotion_idx, last_agent_action_idx):

    # one-hot vector of round index
    round_num_vector = torch.zeros((int(math.log(cfg.max_rounds, 2)), ))
    round_num_vector[round_num] = 1.0

    # one-hot vector of last user action
    last_user_action_vector = torch.zeros((cfg.num_user_action, ))
    last_user_action_vector[last_user_action_idx] = 1.0

    # one-hot vector of last user emotion
    last_user_emotion_vector = torch.zeros((cfg.num_emotion, ))
    last_user_emotion_vector[last_user_emotion_idx] = 1.0

    # one-hot vector of last agent action
    last_agent_action_vector = torch.zeros((cfg.action_shape, ))
    last_agent_action_vector[last_agent_action_idx] = 1.0

    state_vector = torch.hstack([round_num_vector, last_user_action_vector,
                                 last_user_emotion_vector, last_agent_action_vector]).view(-1)

    return state_vector


def convert_list_to_dict(l):

    assert len(l) == len(set(l)), 'List must be unique!'
    return {k: v for v, k in enumerate(l)}


def draw_loss_curve(path):

    with open(path, 'r') as f:
        lines = f.readlines()

    losses = []
    for l in lines:
        loss = re.search(r'Loss: (.*)', l.strip()).group().replace('Loss:', '')
        losses.append(float(loss))
    x = np.array(list(range(len(losses))))
    plt.plot(x, losses)
    plt.yscale('linear')
    plt.xlabel('dialogues')
    plt.ylabel('loss')
    plt.show()


if __name__ == '__main__':
    draw_loss_curve('../logs/dqn_log.txt')