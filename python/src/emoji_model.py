import tensorflow as tf


def index_data_type():
    return tf.int32


class EmojiModel(object):
    def __init__(self, is_training, config):
        self.batch_size = config.batch_size
        self.num_steps = config.num_steps
        self.num_layers = config.num_layers
        self.hidden_size = config.hidden_size
        self.embedding_size = config.embedding_size
        self.vocab_size = config.vocab_size
        self.input_data = tf.placeholder(dtype=index_data_type(), shape=[self.batch_size, self.num_steps],
                                         name="input_word_ids")
        self.target = tf.placeholder(dtype=index_data_type(), shape=[self.batch_size, self.num_steps],
                                     name="output_word_ids")
        self.seq_len = tf.placeholder(dtype=index_data_type(), shape=[self.batch_size], name="seq_len")
        self.mask = tf.placeholder(dtype=tf.float32, shape=[self.batch_size, self.num_steps], name="mask")
        self.top_k = tf.placeholder(dtype=index_data_type(), shape=[], name="top_k")

        def lstm_cell(hidden_size):
            return tf.contrib.rnn.BasicLSTMCell(hidden_size, forget_bias=1.0, state_is_tuple=True)

        attn_cell = lstm_cell

        if is_training and config.keep_prob < 1:
            def attn_cell(hidden_size):
                return tf.contrib.rnn.DropoutWrapper(lstm_cell(hidden_size), output_keep_prob=config.keep_prob)

        with tf.variable_scope("Model"):
            lstm_state_as_tensor_shape = [self.num_layers, 2, self.batch_size, self.hidden_size]
            self.initial_state = tf.placeholder_with_default(tf.zeros(lstm_state_as_tensor_shape, dtype=tf.float32),
                                                             lstm_state_as_tensor_shape, name="state_in")

            unstack_state = tf.unstack(self.initial_state, axis=0)

            tuple_state = tuple([tf.contrib.rnn.LSTMStateTuple(unstack_state[idx][0], unstack_state[idx][1]) for idx in
                                 range(self.num_layers)])

            with tf.variable_scope("Embedding"):
                self.embedding = tf.get_variable("word_embedding", [self.vocab_size, self.embedding_size],
                                                 dtype=tf.float32)
                inputs = tf.nn.embedding_lookup(self.embedding, self.input_data)
                embedding_to_rnn = tf.get_variable("embedding_to_rnn", [self.embedding_size, self.hidden_size],
                                                   dtype=tf.float32)

                inputs = tf.reshape(tf.matmul(tf.reshape(inputs, [-1, self.embedding_size]), embedding_to_rnn),
                                    shape=[self.batch_size, self.num_steps, self.hidden_size])

                if is_training and config.keep_prob < 1:
                    inputs = tf.nn.dropout(inputs, config.keep_prob)

                print("inputs shape:", inputs.get_shape())

            with tf.variable_scope("RNN"):
                rnn_cell = tf.contrib.rnn.MultiRNNCell([attn_cell(self.hidden_size) for _ in range(self.num_layers)],
                                                       state_is_tuple=True)

                outputs, state_out = tf.nn.dynamic_rnn(rnn_cell, inputs, sequence_length=self.seq_len,
                                                       initial_state=tuple_state)

                self.outputs = tf.identity(outputs, "outputs")
                self.output = tf.reshape(self.outputs, [-1, self.hidden_size], name="output")

            with tf.variable_scope("Softmax"):
                rnn_output_to_final_output = tf.get_variable("rnn_output_to_final_output",
                                                             [self.hidden_size, self.embedding_size], dtype=tf.float32)
                self.softmax_w = tf.get_variable("softmax_w", [self.embedding_size, self.vocab_size], dtype=tf.float32)
                self.softmax_b = tf.get_variable("softmax_b", [self.vocab_size], dtype=tf.float32)

            logits = tf.matmul(tf.matmul(self.output, rnn_output_to_final_output), self.softmax_w) + self.softmax_b

            probabilities = tf.nn.softmax(logits, name="probabilities")

            top_k_logits, top_k_prediction = tf.nn.top_k(logits, self.top_k, name="top_k_prediction")

            loss = tf.contrib.legacy_seq2seq.sequence_loss_by_example(logits=[logits],
                                                                      targets=[tf.reshape(self.target, [-1])],
                                                                      weights=[tf.reshape(self.mask, [-1])],
                                                                      average_across_timesteps=False)

            self.cost = tf.reduce_mean(loss)
            self.final_state = tf.identity(state_out, "state_out")
            self.logits = logits
            self.probabilities = probabilities
            self.top_k_predictions = top_k_prediction
            self.top_k_logits = top_k_logits

        if not is_training:
            return

        self.lr = tf.get_variable(name="learning_rate", shape=[], dtype=tf.float32,
                                  initializer=tf.constant_initializer(config.learning_rate), trainable=False)

        tvars = tf.get_collection(tf.GraphKeys.GLOBAL_VARIABLES, scope="Model/Model")

        grads, _ = tf.clip_by_global_norm(tf.gradients(self.cost, tvars), config.max_grad_norm)

        optimizer = tf.train.GradientDescentOptimizer(self.lr)
        self.global_step = tf.Variable(0, trainable=False, name="global_step")
        self.train_op = optimizer.apply_gradients(zip(grads, tvars), global_step=self.global_step)

    def get_global_step(self, session):
        gs = session.run([self.global_step])
        return sum(gs)

    def update_lr(self, regr):
        self.lr = self.lr * regr
