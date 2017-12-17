from __future__ import division
from __future__ import print_function

import numpy as np
import codecs
import random


def load_vocabulary(filename=""):
    word_to_id, id_to_word = dict(), dict()
    with codecs.open(filename, mode="r", encoding="utf-8") as f:
        for i, line in enumerate(f.readlines()):
            content = line.split()
            word = content[0]
            word_to_id[word] = i
            id_to_word[i] = word
    return word_to_id, id_to_word


def read_data(filename=""):
    corpus = []
    with codecs.open(filename, mode="r", encoding="utf-8") as f:
        for line in f.readlines():
            word_ids = line.split(" ")
            try:
                corpus.append([word_id.strip() for word_id in word_ids])
            except KeyError:
                print("key error")

    print("len(corpus) = ", len(corpus))
    return corpus


def corpus_iterator(raw_data, batch_size, num_steps, shuffle=True):
    def flatten(lst):
        return [x for item in lst for x in item]

    if shuffle:
        random.shuffle(raw_data)

    epoch = flatten(raw_data)
    batch_length = len(epoch) // batch_size
    valid_epoch_range = batch_size * batch_length

    epoch = np.reshape(np.array(epoch[:valid_epoch_range], dtype=np.int32), [batch_size, -1])

    epoch_size = (batch_length - 1) // num_steps

    for i in range(epoch_size):
        word_input = epoch[:, i * num_steps:(i + 1) * num_steps]
        word_output = epoch[:, i * num_steps + 1: (i + 1) * num_steps + 1]
        sequence_lengths = np.array([num_steps] * batch_size, dtype=np.int32)

        mask = np.ones([batch_size, num_steps])
        mask[word_output < 808] = 6.0
        mask[word_output < 11] = 0.1

        yield (word_input, word_output, mask, sequence_lengths)
