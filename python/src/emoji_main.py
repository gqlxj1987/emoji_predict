from __future__ import division
from __future__ import print_function

import tensorflow as tf
import os
import sys
from tensorflow.python.framework.graph_util import convert_variables_to_constants

import reader
import emoji_model

flags = tf.flags

logging = tf.logging

flags.DEFINE_string("model_config", "/Users/xm/Documents/emoji/model/config.cfg", "")
flags.DEFINE_string("data_path", "/Users/xm/Documents/emoji/data/emoji_simple.txt", "")
flags.DEFINE_string("valid_data", "/Users/xm/Documents/emoji/data/emoji_simple.txt", "")
flags.DEFINE_string("save_path", "/Users/xm/Documents/emoji/model/model_new/", "")
flags.DEFINE_string("graph_save_path", "/Users/xm/Documents/emoji/model/graph_new/", "")
flags.DEFINE_string("model_name", "model", "")

FLAGS = flags.FLAGS


class Config():
    def __init__(self):
        self.init_scale = 0.05
        self.learning_rate = 0.001
        self.max_grad_norm = 5
        self.num_layers = 2
        self.num_steps = 25
        self.hidden_size = 400
        self.embedding_size = 150
        self.max_epoch = 100
        self.keep_prob = 0.6
        self.lr_decay = 0.9
        self.batch_size = 32
        self.vocab_size = 20241
        self.gpu_fraction = 0.95

    def get_config(self, filename=None):
        if filename is not None:
            with open(filename) as f:
                for line in f:
                    if line.startswith("#"):
                        continue
                    param, value = line.split()
                    if param == "init_scale":
                        self.init_scale = float(value)
                    elif param == "learning_rate":
                        self.learning_rate = float(value)
                    elif param == "max_grad_norm":
                        self.max_grad_norm = float(value)
                    elif param == "num_layers":
                        self.num_layers = int(value)
                    elif param == "num_steps":
                        self.num_steps = int(value)
                    elif param == "hidden_size":
                        self.hidden_size = int(value)
                    elif param == "embedding_size":
                        self.embedding_size = int(value)
                    elif param == "max_epoch":
                        self.max_epoch = int(value)
                    elif param == "keep_prob":
                        self.keep_prob = float(value)
                    elif param == "lr_decay":
                        self.lr_decay = float(value)
                    elif param == "batch_size":
                        self.batch_size = int(value)
                    elif param == "vocab_size":
                        self.vocab_size = int(value)
                    elif param == "gpu_fraction":
                        self.gpu_fraction = float(value)


def export_graph(session, iter):
    variables_to_export = ["Test/Model/Model/state_out",
                           "Test/Model/Model/top_k_prediction",
                           "Test/Model/Model/probabilities"]

    graph_def = convert_variables_to_constants(session, session.graph_def, variables_to_export)
    model_export_name = os.path.join(FLAGS.graph_save_path, "model-iter" + str(iter) + ".pb")

    f = open(model_export_name, "wb")
    f.write(graph_def.SerializeToString())
    f.close()
    print("graph is saved to: ", model_export_name)


def run_evaluate_epoch(session, model, raw_data=None, eval_op=None):
    costs = 0.0
    previous_state = session.run(model.initial_state)
    fetches = {
        "cost": model.cost,
        "state_out": model.final_state,
        "top_k_prediction": model.top_k_predictions,
        "top_k_logits": model.top_k_logits
    }
    if eval_op is not None:
        fetches["eval_op"] = eval_op

    batch_size = model.batch_size
    num_steps = model.num_steps

    prediction_made = 0.0
    top1_correct_total, top3_correct_total = 0.0, 0.0

    for step, epoch_data in enumerate(reader.corpus_iterator(raw_data, batch_size, num_steps)):
        feed_dict = {
            model.input_data: epoch_data[0],
            model.target: epoch_data[1],
            model.mask: epoch_data[2],
            model.seq_len: epoch_data[3],
            model.initial_state: previous_state,
            model.top_k: 5
        }

        vals = session.run(fetches, feed_dict)
        cost = vals["cost"]
        previous_state = vals["state_out"]

        top_k_prediction = vals["top_k_prediction"]

        costs += cost

        target_1d = epoch_data[1].reshape([-1])
        prediction_made += len(target_1d)

        top1_correct = 0
        top3_correct = 0
        for i in range(len(target_1d)):
            if top_k_prediction[i, 0] == target_1d[i]:
                top1_correct += 1
            if top_k_prediction[i, 0] == target_1d[i] or top_k_prediction[i, 1] == target_1d[i] or top_k_prediction[
                i, 2] == target_1d[i]:
                top3_correct += 1
        top1_correct_total += top1_correct
        top3_correct_total += top3_correct

    print("evaluate")
    top1_acc = top1_correct_total / prediction_made
    top3_acc = top3_correct_total / prediction_made
    print("top1 accuracy = " + str(top1_acc))
    print("top3 accuracy = " + str(top3_acc))


def run_epoch(session, model, eval_op=None, raw_data=None):
    previous_state = session.run(model.initial_state)

    fetches = {
        "cost": model.cost,
        "state_out": model.final_state
    }

    if eval_op is not None:
        fetches["eval_op"] = eval_op

    batch_size = model.batch_size
    num_steps = model.num_steps

    total_length = sum([len(lst) for lst in raw_data])

    batch_length = total_length // batch_size
    epoch_size = (batch_length - 1) // num_steps

    print("epoch size: ", epoch_size)

    for step, epoch_data in enumerate(reader.corpus_iterator(raw_data, batch_size, num_steps)):
        feed_dict = {
            model.input_data: epoch_data[0],
            model.target: epoch_data[1],
            model.mask: epoch_data[2],
            model.seq_len: epoch_data[3],
            model.initial_state: previous_state
        }

        vals = session.run(fetches, feed_dict)

        cost = vals["cost"]
        previous_state = vals["state_out"]

        print("step: ", step)
        print("cost: ", cost)


def main(_):
    if not FLAGS.data_path:
        raise ValueError("error")

    if not os.path.isdir(FLAGS.save_path):
        os.mkdir(FLAGS.save_path)
    if not os.path.isdir(FLAGS.graph_save_path):
        os.mkdir(FLAGS.graph_save_path)

    config = Config()
    config.get_config(FLAGS.model_config)

    eval_config = Config()
    eval_config.get_config(FLAGS.model_config)
    eval_config.batch_size = 1
    eval_config.num_steps = 1

    data = reader.read_data(FLAGS.data_path)
    valid_data = reader.read_data(FLAGS.valid_data)

    with tf.Graph().as_default():
        initializer = tf.random_uniform_initializer(-config.init_scale, config.init_scale)
        gpu_config = tf.ConfigProto()
        gpu_config.gpu_options.per_process_gpu_memory_fraction = config.gpu_fraction

        with tf.Session(config=gpu_config) as session:
            with tf.name_scope("Train"):
                with tf.variable_scope("Model", reuse=None, initializer=initializer):
                    mtrain = emoji_model.EmojiModel(is_training=True, config=config)
                tf.summary.scalar("Training Loss", mtrain.cost)

            with tf.name_scope("Valid"):
                with tf.variable_scope("Model", reuse=True, initializer=initializer):
                    mvalid = emoji_model.EmojiModel(is_training=False, config=config)

            with tf.name_scope("Test"):
                with tf.variable_scope("Model", reuse=True, initializer=initializer):
                    msave = emoji_model.EmojiModel(is_training=False, config=eval_config)

            sv = tf.train.Saver()

            if not FLAGS.model_name.endswith(".ckpt"):
                FLAGS.model_name += ".ckpt"

            session.run(tf.global_variables_initializer())

            for i in range(config.max_epoch):

                run_epoch(session, mtrain, eval_op=mtrain.train_op, raw_data=data)

                print("epoch " + str(i) + " finished")

                mtrain.update_lr(config.lr_decay)
                print("new learning rate: " + str(session.run(mtrain.lr)))

                if valid_data is not None:
                    run_evaluate_epoch(session, mvalid, valid_data)

                if FLAGS.save_path:
                    print("saving model to %s" % FLAGS.save_path)
                    step = mtrain.get_global_step(session)
                    model_save_path = os.path.join(FLAGS.save_path, FLAGS.model_name)
                    sv.save(session, model_save_path, global_step=step)
                export_graph(session, i)
                sys.stdout.flush()


if __name__ == "__main__":
    tf.app.run()
