package com.kikatech.engine.ngram.tokenizer

/**
  * Created by huminghe on 2017/8/30.
  */
trait Tokenizer[T] extends Serializable {

  def tokenize(input: T): List[T]

}
