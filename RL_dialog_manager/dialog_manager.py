from flask import Flask, Response, request
import torch
import math
import os
import yaml
import logging
from DQN import DQN
from utils.base_config import BareConfig
from utils.functions import convert_user_action_to_idx, convert_user_emotion_to_idx, \
    convert_agent_action_to_idx, convert_idx_to_user_action, convert_idx_to_agent_action, \
    get_state_vector


app = Flask(__name__)

round_num = 0
last_agent_action_idx = 0
last_user_action_idx = 0
last_user_emotion_idx = 0


@app.route('/emotion')
def return_action():
    print('This is running...')

    # data is post in a form of round_num-last_user_action_idx-last_user_emotion_idx-last_agent_action_idx
    data = request.args['data']
    print('Request from client: ', data)

    global round_num
    global last_user_action_idx
    global last_user_emotion_idx
    global last_agent_action_idx
    round_num, user_action, user_emotion_idx, agent_action = data.split('-')

    if round_num == 0:
        # need to be modified later, the initial state
        state_vector = get_state_vector(cfg, int(round_num), convert_user_action_to_idx(user_action),
                                        int(user_emotion_idx),
                                        convert_agent_action_to_idx(agent_action))
    else:
        print(f'info: {round_num}, {last_user_action_idx}, {last_user_emotion_idx}, {last_agent_action_idx}')
        state_vector = get_state_vector(cfg, int(round_num), last_user_action_idx,
                                        last_user_emotion_idx, last_agent_action_idx)

    last_user_action_idx = convert_user_action_to_idx(user_action)
    last_user_emotion_idx = int(user_emotion_idx)
    last_agent_action_idx = convert_agent_action_to_idx(agent_action)

    action_logits = dqn(state_vector)
    action_idx = torch.argmax(action_logits)
    action = convert_idx_to_agent_action(action_idx)

    return Response(action)


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

    num_state = int(math.log(cfg.max_rounds, 2)) + cfg.num_user_action + cfg.action_shape + cfg.num_emotion

    dqn = DQN(
        num_state,
        cfg.action_shape,
        hidden_shape=cfg.hidden_shape
    )
    device = torch.device('cpu')
    dqn.load_state_dict(torch.load(os.path.join(cfg.dir_checkpoint, f'saved_weights.pth'),
                                   map_location=device))

    HOST = '127.0.0.1'
    PORT = 5000
    app.run(host=HOST, port=PORT, debug=True)

