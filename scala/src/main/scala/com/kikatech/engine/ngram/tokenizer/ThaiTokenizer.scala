package com.kikatech.engine.ngram.tokenizer

import java.text.BreakIterator
import java.util.Locale

import cn.edu.kmust.seanlp.SEANLP
import org.apache.spark.Logging

import scala.collection.mutable.ListBuffer

/**
  * Created by huminghe on 2017/8/30.
  */
class ThaiTokenizer extends Tokenizer[String] with Logging {

  private val thaiBoundary = BreakIterator.getWordInstance(new Locale("th", "TH", "TH"))

  override def tokenize(input: String): List[String] = {
    val tokenList = new ListBuffer[String]()
    try {
      thaiBoundary.setText(input)
      var start = thaiBoundary.first()
      var end = thaiBoundary.next()
      while (end != BreakIterator.DONE) {
        val word = input.substring(start, end)
        if (word.trim.nonEmpty) {
          tokenList += word.trim
        }
        start = end
        end = thaiBoundary.next()
      }
    } catch {
      case ex: Exception => {
        logError("Error, message: " + input, ex)
      }
    }
    tokenList.toList
  }

  def tokenizeNew(input: String): List[String] = {
    val tokenList = new ListBuffer[String]()
    try {
      val listJ = SEANLP.Thai.dCRFSegment(input)
      val listS = collection.JavaConverters.iterableAsScalaIterableConverter(listJ).asScala
      listS.foreach(term => tokenList += term.getWord)
    } catch {
      case ex: Exception => logError("Error, message: " + input, ex)
    }
    tokenList.toList
  }

}
