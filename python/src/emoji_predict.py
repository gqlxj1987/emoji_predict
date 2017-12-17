# coding=utf-8
from __future__ import division
from __future__ import print_function

import tensorflow as tf
import reader
import re
import codecs
import argparse

parser = argparse.ArgumentParser()
parser.add_argument("--graph", help="path to graph file",
                    default="/Users/xm/Documents/emoji/model_new/model-iter33-60-01.pb")
parser.add_argument("--test_corpus", help="path to test corpus file",
                    default="/Users/xm/Documents/emoji/emoji_test_filtered_more.txt")
parser.add_argument("--vocabulary", help="path to vocabulary",
                    default="/Users/xm/Documents/emoji/voca/emoji_vocabulary_idx.txt")

args = parser.parse_args()


class Model(object):
    def __init__(self, graph_def_path, vacab_path):

        prefix = "import/"

        self.input_word_ids = prefix + "Test/Model/input_word_ids:0"
        self.model_length = prefix + "Test/Model/seq_len:0"
        self.state_in = prefix + "Test/Model/Model/state_in:0"
        self.state_out = prefix + "Test/Model/Model/state_out:0"
        self.top_k = prefix + "Test/Model/top_k:0"
        self.model_probs = prefix + "Test/Model/Model/probabilities:0"
        self.model_top_k_result = prefix + "Test/Model/Model/top_k_prediction:1"

        with open(graph_def_path, "rb") as f:
            graph_def = tf.GraphDef()
            graph_def.ParseFromString(f.read())
            tf.import_graph_def(graph_def)

        self.word_to_id, self.id_to_word = reader.load_vocabulary(vacab_path)

    def predict_word_id(self, session, previous_state, word_id):

        feed_dict = {}
        variables_to_fetch = [self.state_out, self.model_top_k_result, self.model_probs]

        feed_dict[self.input_word_ids] = [[word_id]]
        feed_dict[self.model_length] = [1]
        feed_dict[self.top_k] = 10
        if previous_state is not None:
            feed_dict[self.state_in] = previous_state

        state_out, top_k_predictions, probs = session.run(variables_to_fetch, feed_dict=feed_dict)

        return state_out, top_k_predictions, probs

    def predict_sentence(self, session, sentence):

        print(sentence, end="")

        previous_state, _, _ = self.predict_word_id(session, None, 0)

        p2 = re.compile('[<>\[\](){}\"]+')
        sentence = p2.sub(" ", sentence)
        p1 = re.compile('[0-9]\\S*')
        sentence = p1.sub(" <num> ", sentence)
        p3 = re.compile("[~\-^_#*$+=/\\\\%|]+")
        sentence = p3.sub(" <pun> ", sentence)
        p4 = re.compile("(?P<name>[.!?:;,&])[.!?;:,&]*")
        sentence = p4.sub(" \g<name> ", sentence)
        p5 = re.compile("\s+")
        sentence = p5.sub(" ", sentence)
        sentence = sentence.lower()

        words = sentence.split()

        word_ids = [self.word_to_id[word] if self.word_to_id.__contains__(word) else 1 for word in words]

        word_num = 0
        emoji_num = 0
        same_num = 0
        diff_num = 0
        top1_cover_num = 0
        top3_cover_num = 0
        top1_emoji = 0
        top3_emoji = 0
        top1_same_cover = 0
        top3_same_cover = 0
        top1_diff_cover = 0
        top3_diff_cover = 0

        for i in range(1, len(word_ids)):

            input_id = word_ids[i - 1]
            input_word = words[i - 1]
            goal_id = word_ids[i]
            goal_word = words[i]

            previous_state, top_k_predictions, probs = self.predict_word_id(session, previous_state, input_id)

            top_k_probs = [probs[0][s] for s in top_k_predictions[0]]
            top_k_words = [self.id_to_word[s] for s in top_k_predictions[0]]

            for t, word_id in enumerate(top_k_predictions[0]):
                if word_id < 10:
                    top_k_probs[t] = 0

            top_k_probs, top_k_words = (list(t) for t in zip(*sorted(zip(top_k_probs, top_k_words), reverse=True)))

            top_k_words = top_k_words[0:3]

            top1_cover = False
            top3_cover = False

            if (input_id >= 808 or input_id == 1) and (goal_id >= 808 or goal_id == 1):
                word_num += 1
                for j in range(len(top_k_words)):
                    if top_k_words[j] == goal_word:
                        if j + 1 <= 1:
                            top1_cover_num += 1
                            top1_cover = True
                        if j + 1 <= 3:
                            top3_cover_num += 1
                            top3_cover = True

            if 10 < goal_id < 808 and (input_id <= 10 or input_id >= 808):
                emoji_num += 1
                for j in range(len(top_k_words)):
                    if top_k_words[j] == goal_word:
                        if j + 1 <= 1:
                            top1_emoji += 1
                            top1_cover = True
                        if j + 1 <= 3:
                            top3_emoji += 1
                            top3_cover = True

            if 10 < goal_id < 808 and 10 < input_id < 808:
                if input_id == goal_id:
                    same_num += 1
                    for j in range(len(top_k_words)):
                        if top_k_words[j] == goal_word:
                            if j + 1 <= 1:
                                top1_same_cover += 1
                                top1_cover = True
                            if j + 1 <= 3:
                                top3_same_cover += 1
                                top3_cover = True

                if input_id != goal_id:
                    diff_num += 1
                    for j in range(len(top_k_words)):
                        if top_k_words[j] == goal_word:
                            if j + 1 <= 1:
                                top1_diff_cover += 1
                                top1_cover = True
                            if j + 1 <= 3:
                                top3_diff_cover += 1
                                top3_cover = True

            result = ", ".join(["'" + word + "'" for word in top_k_words])
            print("word: %s, goal: %s, prediction: %s, top1: %s, top3: %s" % (
                input_word, goal_word, result, top1_cover, top3_cover))

        print()

        return word_num, top1_cover_num, top3_cover_num, emoji_num, top1_emoji, top3_emoji, same_num, top1_same_cover, top3_same_cover, diff_num, top1_diff_cover, top3_diff_cover


if __name__ == '__main__':

    word_total = 0
    word_top1_total = 0
    word_top3_total = 0
    emoji_total = 0
    emoji_top1_total = 0
    emoji_top3_total = 0
    same_total = 0
    same_top1_total = 0
    same_top3_total = 0
    diff_total = 0
    diff_top1_total = 0
    diff_top3_total = 0

    with codecs.open(args.test_corpus, "r", encoding="utf-8") as f:
        with tf.Graph().as_default():
            with tf.Session() as session:
                model = Model(args.graph, args.vocabulary)

                for line in f.readlines():
                    word, top1, top3, emoji, emoji_top1, emoji_top3, same, same_top1, same_top3, diff, diff_top1, diff_top3 = model.predict_sentence(
                        session, line)
                    word_total += word
                    word_top1_total += top1
                    word_top3_total += top3
                    emoji_total += emoji
                    emoji_top1_total += emoji_top1
                    emoji_top3_total += emoji_top3
                    same_total += same
                    same_top1_total += same_top1
                    same_top3_total += same_top3
                    diff_total += diff
                    diff_top1_total += diff_top1
                    diff_top3_total += diff_top3

                print("words prediction total: " + str(word_total))
                print("word top1 hit total: " + str(word_top1_total) + "   accuracy: " + str(
                    word_top1_total / word_total))
                print("word top3 hit total: " + str(word_top3_total) + "   accuracy: " + str(
                    word_top3_total / word_total))
                print("emoji prediction total: " + str(emoji_total))
                print("emoji top1 hit total: " + str(emoji_top1_total) + "   accuracy: " + str(
                    emoji_top1_total / emoji_total))
                print("emoji top3 hit total: " + str(emoji_top3_total) + "   accuracy: " + str(
                    emoji_top3_total / emoji_total))
                print("same emoji prediction total: " + str(same_total))
                print("same emoji top1 hit total: " + str(same_top1_total) + "   accuracy: " + str(
                    same_top1_total / same_total))
                print("same emoji top3 hit total: " + str(same_top3_total) + "   accuracy: " + str(
                    same_top3_total / same_total))
                print("diff emoji prediction total: " + str(diff_total))
                print("diff emoji top1 hit total: " + str(diff_top1_total) + "   accuracy: " + str(
                    diff_top1_total / diff_total))
                print("diff emoji top3 hit total: " + str(diff_top3_total) + "   accuracy: " + str(
                    diff_top3_total / diff_total))
