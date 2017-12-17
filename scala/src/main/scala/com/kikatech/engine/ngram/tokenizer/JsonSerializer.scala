package com.kikatech.engine.ngram.tokenizer

import com.google.gson.Gson

import scala.reflect.ClassTag

/**
  * Created by huminghe on 2017/8/14.
  */
class JsonSerializer[T >: Null] extends Serializer[T, String] {

  private val gson = new Gson()

  def serialize(obj: T): String = {
    try {
      gson.toJson(obj)
    } catch {
      case e: Throwable => throw new RuntimeException(e.getMessage)
    }
  }

  def deSerialize(jsonStr: String)(implicit ct: ClassTag[T]): T = {
    try {
      gson.fromJson(jsonStr, ct.runtimeClass).asInstanceOf[T]
    } catch {
      case e: Throwable => {
        null
      }
    }
  }

}
