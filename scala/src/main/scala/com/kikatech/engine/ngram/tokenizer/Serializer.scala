package com.kikatech.engine.ngram.tokenizer

import scala.reflect.ClassTag

/**
  * Created by huminghe on 2017/8/14.
  */
trait Serializer[T1, T2] extends Serializable {

  def serialize(obj: T1): Any

  def deSerialize(serializedObj: T2)(implicit ct: ClassTag[T1]): T1

}
