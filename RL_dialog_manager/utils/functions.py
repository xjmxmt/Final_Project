import matplotlib.pyplot as plt
import re
import numpy as np


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