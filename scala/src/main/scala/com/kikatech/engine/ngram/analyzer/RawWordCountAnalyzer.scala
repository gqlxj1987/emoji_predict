package com.kikatech.engine.ngram.analyzer

import java.io.FileWriter
import java.util.regex.{Matcher, Pattern}

import com.kikatech.engine.ngram.util.{CharacterUtil, CorpusUtil, NationCodeEnum}
import org.apache.spark.SparkContext

/**
  * Created by huminghe on 2017/6/5.
  */
class RawWordCountAnalyzer extends Analyzer {

  private val WORD_COUNT_LENGTH = 300000

  override def doAnalyze(sparkContext: SparkContext, paramArray: Array[String]): Unit = {

    if (paramArray.length != 6) {
      sparkContext.stop()
    }

    val jobName = paramArray(0)
    val inputPath = paramArray(1)
    val outputPath = paramArray(2)
    val countryCode = paramArray(3).toUpperCase()
    val language = paramArray(4)
    var nationCodeEnum: NationCodeEnum.Value = null
    var pattern: Pattern = null
    try {
      nationCodeEnum = NationCodeEnum.withName(countryCode)
      pattern = CharacterUtil.getCharacterPattern(nationCodeEnum)
    } catch {
      case ex: Exception => {
        logError("\t nation-code=" + pattern)
        nationCodeEnum = null
      }
    }
    val partitionNum: Int = paramArray(5).toInt

    logInfo("job name: " + jobName)
    logInfo("input path: " + inputPath)
    logInfo("output path: " + outputPath)
    logInfo("country code: " + countryCode)
    logInfo("language: " + language)
    logInfo("partition num: " + partitionNum)

    val wordCountRdd = sparkContext.textFile(inputPath, partitionNum)
      .map(line => line.replaceAll(CharacterUtil.CONSTANT_SPECIAL_CHARACTER, CharacterUtil.CONSTANT_SPACE32))
      .map(line => line.replaceAll(CharacterUtil.CONSTANT_STR_PATTERN_STATEMENT_TERMINATOR, CharacterUtil.CONSTANT_NEWLINE))
      .flatMap(_.split(CharacterUtil.CONSTANT_TAB))
      .flatMap(_.split(CharacterUtil.CONSTANT_NEWLINE))
      .flatMap(sentence => CorpusUtil.tokenizeSentence(sentence, language))
      .filter(_.nonEmpty)
      .filter(word => {
        val wordMatcher: Matcher = pattern.matcher(word)
        wordMatcher.find()
      })
      .map(_.toLowerCase)
      .map(word => {
        (word, 1)
      })
      .reduceByKey((a, b) => a + b)
      .sortBy(a => a._2, ascending = false)

    val output = wordCountRdd.map(p => p._1 + CharacterUtil.CONSTANT_TAB + p._2.toString)
      .collect()

    val out = new FileWriter(outputPath, false)
    output
      .take(WORD_COUNT_LENGTH)
      .foreach(w => {
        out.write(w)
        out.write(CharacterUtil.CONSTANT_NEWLINE)
      })
    out.close()

  }
}
