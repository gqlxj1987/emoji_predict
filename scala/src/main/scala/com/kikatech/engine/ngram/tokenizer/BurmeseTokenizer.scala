package com.kikatech.engine.ngram.tokenizer

import java.io.{IOException, InputStream}

import cn.edu.kmust.seanlp.SEANLP
import com.ibm.icu.text.{BreakIterator, RuleBasedBreakIterator}
import com.kikatech.engine.ngram.util.CharacterUtil
import org.apache.commons.lang3.StringUtils
import org.apache.spark.Logging

import scala.collection.mutable.ListBuffer

/**
  * Created by huminghe on 2017/9/1.
  */
class BurmeseTokenizer extends Tokenizer[String] with Logging {

  private val burmeseDefaultBoundary: BreakIterator = readBreakIterator("MyanmarSyllable.brk")

  private def readBreakIterator(filename: String): RuleBasedBreakIterator = {
    val is: InputStream = BurmeseTokenizer.this
      .getClass
      .getResourceAsStream("/org/apache/lucene/analysis/icu/segmentation/" + filename)
    try {
      val bi: RuleBasedBreakIterator = RuleBasedBreakIterator.getInstanceFromCompiledRules(is)
      is.close()
      bi
    } catch {
      case e: IOException => {
        throw new RuntimeException(e)
      }
    }
  }

  override def tokenize(input: String): List[String] = {
    val burmeseBoundary: RuleBasedBreakIterator = burmeseDefaultBoundary
      .clone()
      .asInstanceOf[RuleBasedBreakIterator]
    burmeseBoundary.setText(input)
    val tokenList = new ListBuffer[String]()
    var start: Int = burmeseBoundary.first
    var end: Int = burmeseBoundary.next
    while (end != BreakIterator.DONE) {
      {
        val word: String = input.substring(start, end)
        if (!StringUtils.isEmpty(word.trim)) {
          tokenList += word.trim
        }
      }
      start = end
      end = burmeseBoundary.next
    }
    tokenList.toList
  }

  def tokenizeNew(input: String): List[String] = {
    val tokenList = new ListBuffer[String]()
    try {
      val listJ = SEANLP.Burmese.crfSegment(input)
      val listS = collection.JavaConverters.iterableAsScalaIterableConverter(listJ).asScala
      listS.foreach(term => term.getWord.split(CharacterUtil.CONSTANT_SPACE32).foreach(word => tokenList += word))
    } catch {
      case ex: Exception => logError("Error, message: " + input, ex)
    }
    tokenList.toList
  }

}
