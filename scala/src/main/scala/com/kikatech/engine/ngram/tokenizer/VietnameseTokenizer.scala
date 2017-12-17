package com.kikatech.engine.ngram.tokenizer

import cn.edu.kmust.seanlp.SEANLP
import org.apache.spark.Logging

import scala.collection.mutable.ListBuffer

/**
  * Created by huminghe on 2017/8/30.
  */
class VietnameseTokenizer extends Tokenizer[String] with Logging {

  override def tokenize(input: String): List[String] = {
    val tokenList = new ListBuffer[String]()
    try {
      val listJ = SEANLP.Vietnamese.crfSegment(input)
      val listS = collection.JavaConverters.iterableAsScalaIterableConverter(listJ).asScala
      listS.foreach(term => tokenList += term.getWord)
    } catch {
      case ex: Exception => logError("Error, message: " + input, ex)
    }
    tokenList.toList
  }

}
