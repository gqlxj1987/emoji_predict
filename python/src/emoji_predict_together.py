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
                    default="/Users/xm/Documents/emoji/model_new/model-iter31-together-60-01.pb")
parser.add_argument("--test_corpus", help="path to test corpus file",
                    default="/Users/xm/Documents/emoji/emoji_test_filtered_together.txt")
parser.add_argument("--vocabulary", help="path to vocabulary",
                    default="/Users/xm/Documents/emoji/data_toge/emoji_vocabulary_idx.txt")

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
        feed_dict[self.top_k] = 6
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
        top1_cover_num = 0
        top3_cover_num = 0
        top1_emoji = 0
        top3_emoji = 0
        first_emoji_num = 0
        first_top1_cover = 0
        first_top3_cover = 0
        second_emoji_num = 0
        second_top1_cover = 0
        second_top3_cover = 0

        p6 = re.compile(u'[\uD800-\uDBFF][\uDC00-\uDFFF]')

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

            if (input_id >= 807 or input_id == 1) and (goal_id >= 807 or goal_id == 1):
                word_num += 1
                for j in range(len(top_k_words)):
                    if top_k_words[j] == goal_word:
                        if j + 1 <= 1:
                            top1_cover_num += 1
                            top1_cover = True
                        if j + 1 <= 3:
                            top3_cover_num += 1
                            top3_cover = True

            if 10 < goal_id < 807 and (input_id <= 10 or input_id >= 807):
                goal_emoji_list = p6.findall(goal_word)
                top_k_lists = [p6.findall(top_k_word) for top_k_word in top_k_words]
                emoji_num += 1

                for j in range(len(top_k_words)):
                    if top_k_words[j] == goal_word:
                        if j + 1 <= 1:
                            top1_emoji += 1
                            top1_cover = True
                        if j + 1 <= 3:
                            top3_emoji += 1
                            top3_cover = True

                if len(goal_emoji_list) > 0:
                    first_emoji_num += 1

                if len(goal_emoji_list) > 1:
                    second_emoji_num += 1

                top1_cover_1 = False
                top3_cover_1 = False
                top1_cover_2 = False
                top3_cover_2 = False

                for j in range(len(top_k_words)):
                    if len(top_k_lists[j]) > 0 and len(goal_emoji_list) > 0:
                        if top_k_lists[j][0] == goal_emoji_list[0]:
                            if j + 1 <= 1:
                                top1_cover_1 = True
                            if j + 1 <= 3:
                                top3_cover_1 = True

                    if len(top_k_lists[j]) > 1 and len(goal_emoji_list) > 1:
                        if top_k_lists[j][1] == goal_emoji_list[1]:
                            if j + 1 <= 1:
                                top1_cover_2 = True
                            if j + 1 <= 3:
                                top3_cover_2 = True

                if top1_cover_1:
                    first_top1_cover += 1
                if top3_cover_1:
                    first_top3_cover += 1
                if top1_cover_2:
                    second_top1_cover += 1
                if top3_cover_2:
                    second_top3_cover += 1

            result = ", ".join(["'" + word + "'" for word in top_k_words])
            print("word: %s, goal: %s, prediction: %s, top1: %s, top3: %s" % (
                input_word, goal_word, result, top1_cover, top3_cover))

        print()

        return word_num, top1_cover_num, top3_cover_num, emoji_num, top1_emoji, top3_emoji, first_emoji_num, first_top1_cover, first_top3_cover, second_emoji_num, second_top1_cover, second_top3_cover


if __name__ == '__main__':

    word_total = 0
    word_top1_total = 0
    word_top3_total = 0
    emoji_total = 0
    emoji_top1_total = 0
    emoji_top3_total = 0
    first_total = 0
    first_top1_total = 0
    first_top3_total = 0
    second_total = 0
    second_top1_total = 0
    second_top3_total = 0

    with codecs.open(args.test_corpus, "r", encoding="utf-8") as f:
        with tf.Graph().as_default():
            with tf.Session() as session:
                model = Model(args.graph, args.vocabulary)

                for line in f.readlines():
                    word, top1, top3, emoji, emoji_top1, emoji_top3, first, first_top1, first_top3, second, second_top1, second_top3 = model.predict_sentence(
                        session, line)
                    word_total += word
                    word_top1_total += top1
                    word_top3_total += top3
                    emoji_total += emoji
                    emoji_top1_total += emoji_top1
                    emoji_top3_total += emoji_top3
                    first_total += first
                    first_top1_total += first_top1
                    first_top3_total += first_top3
                    second_total += second
                    second_top1_total += second_top1
                    second_top3_total += second_top3

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
                print("first emoji prediction total: " + str(first_total))
                print("first emoji top1 hit total: " + str(first_top1_total) + "   accuracy: " + str(
                    first_top1_total / first_total))
                print("first emoji top3 hit total: " + str(first_top3_total) + "   accuracy: " + str(
                    first_top3_total / first_total))
                print("second emoji prediction total: " + str(second_total))
                print("second emoji top1 hit total: " + str(second_top1_total) + "   accuracy: " + str(
                    second_top1_total / second_total))
                print("second emoji top3 hit total: " + str(second_top3_total) + "   accuracy: " + str(
                    second_top3_total / second_total))
