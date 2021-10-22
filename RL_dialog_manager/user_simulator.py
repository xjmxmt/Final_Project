import random
import numpy as np


"""
    User simulator basically contains the rules for training the RL dialog manager,
    which means, if we want to make the strategy a bit different, we need to modify the user simulator.
"""


class UserSimulator:

    def __init__(self,
                 max_rounds=20,
                 gamma=0.999):

        self.possible_emotions = \
            ['Affection', 'Anger', 'Annoyance', 'Anticipation', 'Aversion', 'Confidence', 'Disapproval', 'Disconnection',
             'Disquietment', 'Doubt/Confusion', 'Embarrassment', 'Engagement', 'Esteem', 'Excitement', 'Fatigue', 'Fear', 'Happiness',
             'Pain', 'Peace', 'Pleasure', 'Sadness', 'Sensitivity', 'Suffering', 'Surprise', 'Sympathy', 'Yearning']

        self.emotion_level1 = ['Disconnection', 'Disquietment', 'Doubt/Confusion', 'Embarrassment']  # penalty = 1
        self.emotion_level2 = ['Annoyance', 'Disapproval', 'Fatigue', 'Fear']  # penalty = 2
        self.emotion_level3 = ['Anger', 'Aversion', 'Pain', 'Sadness', 'Suffering']  # penalty = 3
        self.emotion_level0 = ['Affection', 'Anticipation', 'Confidence', 'Engagement', 'Esteem',
                               'Excitement', 'Happiness', 'Peace', 'Pleasure', 'Sensitivity',
                               'Surprise', 'Sympathy']  # penalty = 0

        self.possible_actions = ['proceed', 'silence', 'end_dialog']
        self.actions_dict = {'proceed': 0, 'silence': 1, 'end_dialog': 2}

        self.current_round = 0
        self.max_rounds = max_rounds
        self.num_emotion_levels = 4
        self.num_actions = 3
        self.emotion_history = np.zeros((self.num_emotion_levels, ))
        self.action_history = np.zeros((self.num_actions, ))

        self.gamma = gamma

        self.last_emotion_level = None
        self.last_emotion = None
        self.last_action_idx = None
        self.last_action = None
        self.reward = 0.0

    def random_user_emotion(self, level=None):

        assert 0 <= level <= 3

        emotion = None
        if level is None:
            emotion_level = random.randint(0, 3)
            if emotion_level == 0:
                emotion = random.choice(self.emotion_level0)
            elif emotion_level == 1:
                emotion = random.choice(self.emotion_level1)
            elif emotion_level == 2:
                emotion = random.choice(self.emotion_level2)
            elif emotion_level == 3:
                emotion = random.choice(self.emotion_level3)
        else:
            emotion_level = level
            if emotion_level == 0:
                emotion = random.choice(self.emotion_level0)
            elif emotion_level == 1:
                emotion = random.choice(self.emotion_level1)
            elif emotion_level == 2:
                emotion = random.choice(self.emotion_level2)
            elif emotion_level == 3:
                emotion = random.choice(self.emotion_level3)

        return emotion_level, emotion

    def update_emotion_history(self, current_emotion_level):

        self.emotion_history[current_emotion_level] += 1

    def update_action_history(self, current_action_idx):

        self.action_history[current_action_idx] += 1

    def reset(self):

        self.current_round = 0
        self.reward = 0.0
        self.emotion_history = np.zeros((self.num_emotion_levels, ))
        self.action_history = np.zeros((self.num_actions, ))
        self.last_emotion_level = None
        self.last_emotion = None
        self.last_action_idx = None
        self.last_action = None

    def sample_random_action(self):

        action = random.choice(self.possible_actions)
        action_idx = self.actions_dict[action]

        return action_idx, action

    def from_action_to_idx(self, action):

        return self.actions_dict[action]

    def get_user_action(self, current_emotion_level):

        """
        Important function.
        User simulator uses this function to secretly generate action from emotion, since emotion is hidden,
        using current emotion and emotion history.
        """

        # add random noise
        # 1/20
        flag = random.randint(0, 19)
        if flag == 9:
            action_idx, action = self.sample_random_action()
            return action_idx, action

        action = None
        action_idx = None
        emotion_indicator = np.mean(self.emotion_history)

        if current_emotion_level == 3 and emotion_indicator >= 3:
            # the user is too frustrated to continue
            action = 'end_dialog'
            action_idx = self.actions_dict[action]

        elif current_emotion_level == 3 and emotion_indicator < 3:
            # the user is not feeling okay
            action = random.choice(['silence', 'end_dialog'])
            action_idx = self.actions_dict[action]

        elif current_emotion_level == 0:
            # the user is very happy
            action = 'proceed'
            action_idx = self.actions_dict[action]

        elif current_emotion_level <= 2 and emotion_indicator < 3:
            # the user is feeling well
            action = random.choice(['proceed', 'silence'])
            action_idx = self.actions_dict[action]

        elif current_emotion_level <= 2 and emotion_indicator >= 3:
            # the user is feeling better
            action = random.choice(self.possible_actions)
            action_idx = self.actions_dict[action]

        self.update_action_history(action_idx)

        return action_idx, action

    def update_last_variables(self, level, action_idx, emotion, action):

        self.last_emotion_level = level
        self.last_action_idx = action_idx
        self.last_emotion = emotion
        self.last_action = action

        self.current_round += 1

    def update_user_emotion_and_action(self, agent_action):

        """
        Important function.
        User simulator generate emotion from agent action and last round emotion,
        as the other history is already considered in get_user_action() func.
        """

        # possible agent actions: [goto_next_state, smile, gaze, look_away, goto_encourage_state, say_again]

        level, action_idx, emotion, action = None, None, None, None

        if agent_action == 'goto_next_state' or agent_action == 'gaze' \
            or agent_action == 'look_away':

            if self.last_emotion_level == 3:
                # user has already feeling bad in the last round
                level = 3

                _, emotion = self.random_user_emotion(level)
                self.update_emotion_history(level)
                action_idx, action = self.get_user_action(level)
                self.update_action_history(action_idx)

                self.update_last_variables(level, action_idx, emotion, action)
                self.update_accumulated_rewards()

            elif self.last_emotion_level == 0:
                # user was very happy in the last round
                level = random.randint(0, 1)

                _, emotion = self.random_user_emotion(level)
                self.update_emotion_history(level)
                action_idx, action = self.get_user_action(level)
                self.update_action_history(action_idx)

                self.update_last_variables(level, action_idx, emotion, action)
                self.update_accumulated_rewards()

            else:
                # user was not very happy in the last round, but okay
                level = random.randint(1, 3)

                _, emotion = self.random_user_emotion(level)
                self.update_emotion_history(level)
                action_idx, action = self.get_user_action(level)
                self.update_action_history(action_idx)

                self.update_last_variables(level, action_idx, emotion, action)
                self.update_accumulated_rewards()

        elif agent_action == 'smile' or agent_action == 'goto_encourage_state':

            if self.last_emotion_level == 3:
                # user has already feeling bad in the last round
                level = random.randint(1, 3)

                _, emotion = self.random_user_emotion(level)
                self.update_emotion_history(level)
                action_idx, action = self.get_user_action(level)
                self.update_action_history(action_idx)

                self.update_last_variables(level, action_idx, emotion, action)
                self.update_accumulated_rewards()

            elif self.last_emotion_level == 0:
                # user was very happy in the last round
                level = 0

                _, emotion = self.random_user_emotion(level)
                self.update_emotion_history(level)
                action_idx, action = self.get_user_action(level)
                self.update_action_history(action_idx)

                self.update_last_variables(level, action_idx, emotion, action)
                self.update_accumulated_rewards()

            else:
                # user was not very happy in the last round, but okay
                level = random.randint(0, 2)

                _, emotion = self.random_user_emotion(level)
                self.update_emotion_history(level)
                action_idx, action = self.get_user_action(level)
                self.update_action_history(action_idx)

                self.update_last_variables(level, action_idx, emotion, action)
                self.update_accumulated_rewards()

        elif agent_action == 'say_again':

            if self.last_emotion_level == 3:
                # user has already feeling bad in the last round
                level = random.randint(2, 3)

                _, emotion = self.random_user_emotion(level)
                self.update_emotion_history(level)
                action_idx, action = self.get_user_action(level)
                self.update_action_history(action_idx)

                self.update_last_variables(level, action_idx, emotion, action)
                self.update_accumulated_rewards()

            elif self.last_emotion_level == 0:
                # user was very happy in the last round
                level = random.randint(0, 1)

                _, emotion = self.random_user_emotion(level)
                self.update_emotion_history(level)
                action_idx, action = self.get_user_action(level)
                self.update_action_history(action_idx)

                self.update_last_variables(level, action_idx, emotion, action)
                self.update_accumulated_rewards()

            else:
                # user was not very happy in the last round, but okay
                level = random.randint(1, 3)

                _, emotion = self.random_user_emotion(level)
                self.update_emotion_history(level)
                action_idx, action = self.get_user_action(level)
                self.update_action_history(action_idx)

                self.update_last_variables(level, action_idx, emotion, action)
                self.update_accumulated_rewards()

        return level, action_idx, emotion, action

    def update_accumulated_rewards(self):

        self.reward += self.last_emotion_level * self.gamma

        return self.reward

    def get_rewards(self):

        return self.reward


if __name__ == '__main__':
    user_simulator = UserSimulator()

    possible_agent_actions = ['goto_next_state', 'smile', 'gaze', 'look_away', 'goto_encourage_state', 'say_again']

