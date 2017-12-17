package com.kikatech.engine.ngram.analyzer

import com.kikatech.engine.ngram.util.{CharacterUtil, DictUtil, NGramUtil}
import org.apache.spark.SparkContext

/**
  * Created by huminghe on 2017/6/1.
  */
class NGramAnalyzer extends Analyzer {

  override def doAnalyze(sparkContext: SparkContext, paramArray: Array[String]): Unit = {

    if (paramArray.length != 6) {
      sparkContext.stop()
    }

    val jobName = paramArray(0)
    val vocabularyPath = paramArray(1)
    val inputPath = paramArray(2)
    val uniGramOutputPath = paramArray(3)
    val biGramOutputPath = paramArray(4)
    val partitionNum = paramArray(5).toInt

    logInfo("job name: " + jobName)
    logInfo("vocabulary path: " + vocabularyPath)
    logInfo("input path: " + inputPath)
    logInfo("unigram output path: " + uniGramOutputPath)
    logInfo("bigram output path: " + biGramOutputPath)
    logInfo("partition num: " + partitionNum)

    val vocabularySet = DictUtil.getVocabularySet(sparkContext, vocabularyPath)

    val sentenceRDD = sparkContext.textFile(inputPath, partitionNum)
      .map(_.replaceAll(CharacterUtil.CONSTANT_SPECIAL_CHARACTER, CharacterUtil.CONSTANT_SPACE32)
        .replaceAll(CharacterUtil.CONSTANT_STR_PATTERN_STATEMENT_TERMINATOR, CharacterUtil.CONSTANT_NEWLINE)
        .replaceAll(CharacterUtil.CONSTANT_ONLINE_PUNCTUATION, CharacterUtil.CONSTANT_SPACE32))
      .flatMap(_.split(CharacterUtil.CONSTANT_TAB))
      .flatMap(_.split(CharacterUtil.CONSTANT_NEWLINE))
      .filter(_.nonEmpty)

    val separator = CharacterUtil.CONSTANT_TAB

    val uniGramRDD = NGramUtil.getNGramRDD(sentenceRDD, separator, vocabularySet, 1)

    val biGramRDD = NGramUtil.getNGramRDD(sentenceRDD, separator, vocabularySet, 2)

    sentenceRDD.unpersist()

    val uniGramScoreRDD = NGramUtil.getUniGramScoreRDD(uniGramRDD)

    val biGramLimit = 6

    val biGramScoreRDD = NGramUtil.getBiGramScoreRDD(uniGramRDD, biGramRDD, separator, biGramLimit)

    uniGramRDD.unpersist()

    biGramRDD.unpersist()

    NGramUtil.writeUniGramFile(uniGramScoreRDD, separator, uniGramOutputPath, 200000)

    NGramUtil.writeBiGramFile(biGramScoreRDD, separator, biGramOutputPath)

  }

}
