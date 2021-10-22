import os
os.environ["CUDA_VISIBLE_DEVICES"] = "0"
from flask import Flask, Response, request
import cv2
import videocamera as vc
import numpy as np
#from EmotionModel import *
import os
from lib.yolo_inference import yolo_infer2
from PIL import Image
import cv2
import torch
from torchvision import transforms
import torch.nn as nn
import uuid
from lib.emotic import Emotic
from lib.inference import infer
from lib.yolo_inference import get_bbox
from lib.yolo_utils import prepare_yolo, rescale_boxes, non_max_suppression


def calEmoModel(image_path):

    image_context = cv2.cvtColor(cv2.imread(image_path), cv2.COLOR_BGR2RGB)
    try:
        bbox_yolo = get_bbox(yolo, device, image_context)  # x1, y1, x2, y2
        bbox_area = list()  # calculate bbox area
        for ipbx, pred_bbox in enumerate(bbox_yolo):
            x1, y1, x2, y2 = pred_bbox
            bbox_area.append((x2 - x1) * (y2 - y1))
        bbox_yolo = [bbox_yolo[np.argmax(bbox_area)]]  # bbox with the largest area

        for ipbx, pred_bbox in enumerate(bbox_yolo):
            emo_vals[f"bbox_{ipbx}"] = {'cont': [], 'cat': None}
            pred_cat, pred_cont = infer(context_norm, body_norm, ind2cat, ind2vad, device, thresholds, models, image_context=image_context, bbox=pred_bbox, to_print=True)
            write_text_vad = list()
            for continuous in pred_cont:
                write_text_vad.append(str('%.1f' %(continuous)))
                emo_vals[f"bbox_{ipbx}"]['cont'].append(continuous)
            emo_vals[f"bbox_{ipbx}"]['cat'] = pred_cat
            write_text_vad = 'vad ' + ' '.join(write_text_vad)
            image_context = cv2.rectangle(image_context, (pred_bbox[0], pred_bbox[1]),(pred_bbox[2] , pred_bbox[3]), (255, 0, 0), 3)
            cv2.putText(image_context, write_text_vad, (pred_bbox[0], pred_bbox[1] - 5), cv2.FONT_HERSHEY_PLAIN, 1, (0,139,139), 2)
            for i, emotion in enumerate(pred_cat):
                cv2.putText(image_context, emotion, (pred_bbox[0], pred_bbox[1] + (i+1)*12), cv2.FONT_HERSHEY_PLAIN, 1, (0,139,139), 2)
    except Exception as e:
        print ('Exception for image ',image_path)
        print (e)

    if write_op == True:
        result_file_path = os.path.join(result_path, str(uuid.uuid4()) +'.jpg')
        cv2.imwrite(result_file_path, cv2.cvtColor(image_context, cv2.COLOR_RGB2BGR))
    if return_op == True:
        return_image = Image.fromarray(image_context)

    # the inference function returns a dict, top-level key is the bounding box. Each bounding box has sub keys:
    #                1. cont - for continuous V-A-D emotion values
    #                2. cat - for the top k categorical emotions
    print('emo vals: ', emo_vals)
    return_image.save('return_image.jpg')
    return emo_vals['bbox_0']['cat'][0]


app = Flask(__name__)
video_camera = vc.VideoCamera()


def gen(camera):
    ret, img = camera.get_frame()
    cv2.imwrite('image.jpg', img)
    xs = calEmoModel('image.jpg')
    return xs


@app.route('/emotion')
def retEmotion():
    print('This is running...')
    print('Request from client: ', request.args['data'])
    return Response(gen(video_camera))


if __name__ == '__main__':

    # setting up the categories
    # our model outputs numbers as categories, so we much have a mapping from the numbers to the emotions
    cat = ['Affection', 'Anger', 'Annoyance', 'Anticipation', 'Aversion', 'Confidence', 'Disapproval', 'Disconnection', \
           'Disquietment', 'Doubt/Confusion', 'Embarrassment', 'Engagement', 'Esteem', 'Excitement', 'Fatigue', 'Fear','Happiness', \
           'Pain', 'Peace', 'Pleasure', 'Sadness', 'Sensitivity', 'Suffering', 'Surprise', 'Sympathy', 'Yearning']
    cat2ind = {}
    ind2cat = {}
    for idx, emotion in enumerate(cat):
        cat2ind[emotion] = idx
        ind2cat[idx] = emotion

    vad = ['Valence', 'Arousal', 'Dominance']
    ind2vad = {}
    for idx, continuous in enumerate(vad):
        ind2vad[idx] = continuous

    # we need to normalise the demo image using the statistics of the training data the model was trained on
    context_mean = [0.4690646, 0.4407227, 0.40508908]
    context_std = [0.2514227, 0.24312855, 0.24266963]
    body_mean = [0.43832874, 0.3964344, 0.3706214]
    body_std = [0.24784276, 0.23621225, 0.2323653]
    context_norm = [context_mean, context_std]
    body_norm = [body_mean, body_std]

    # Model code
    image_path = 'experiment/bale.jpeg'
    result_path = 'experiment/results/'
    model_path = 'experiment/models/'
    write_op = False
    return_op = True

    device = torch.device("cuda" if torch.cuda.is_available() else "cpu")
    yolo = prepare_yolo(model_path)
    yolo = yolo.to(device)
    yolo.eval()

    thresholds = torch.FloatTensor(np.load(os.path.join(result_path, 'val_thresholds.npy'))).to(device)
    model_context = torch.load(os.path.join(model_path,'model_context1.pth')).to(device)
    model_body = torch.load(os.path.join(model_path,'model_body1.pth')).to(device)
    emotic_model = torch.load(os.path.join(model_path,'model_emotic1.pth')).to(device)
    models = [model_context, model_body, emotic_model]

    emo_vals = {}
    result_file_path = None
    return_image = None

    HOST = '127.0.0.1'
    PORT = 6000  # rl dialog manager is using Port 5000
    app.run(host=HOST, port=PORT, debug=True)
