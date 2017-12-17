package com.kikatech.engine.ngram.analyzer.statistics

import java.io.FileWriter

import com.kikatech.engine.ngram.analyzer.Analyzer
import com.kikatech.engine.ngram.util.{CharacterUtil, CorpusUtil, DictUtil}
import org.apache.spark.SparkContext

/**
  * Created by huminghe on 2017/7/28.
  */
class UserFilterAnalyzer extends Analyzer {

  override def doAnalyze(sparkContext: SparkContext, paramArray: Array[String]): Unit = {

    val filterWordThreshold = 3

    val filterSentenceThreshold = 5

    if (paramArray.length != 5) {
      sparkContext.stop()
    }

    val jobName = paramArray(0)
    val corpusPath = paramArray(1)
    val vocabularyPath = paramArray(2)
    val outputPath = paramArray(3)
    val partitionNum = paramArray(4).toInt

    logInfo("job name: " + jobName)
    logInfo("corpus path: " + corpusPath)
    logInfo("vocabulary path: " + vocabularyPath)
    logInfo("output path: " + outputPath)
    logInfo("partition num: " + partitionNum)

    val vocabularySet = DictUtil.getVocabularySet(sparkContext, vocabularyPath)

    val userSentenceRDD = CorpusUtil.getOnlineUserSentenceRDD(sparkContext, corpusPath, partitionNum)

    val userInfoRDD = userSentenceRDD.map(info => {
      val user = info._2
      val sentence = info._1
      val wordArray = sentence.split(CharacterUtil.CONSTANT_SPACE32)
        .filter(_.nonEmpty)

      var inVocabularyNum = 0
      var outVocabularyNum = 0

      for (idx <- wordArray.indices) {
        var word = wordArray(idx)
        var inVocabulary = vocabularySet.contains(word)
        if (!inVocabulary) {
          word = word.toLowerCase()
          inVocabulary = vocabularySet.contains(word)
        }
        if (!inVocabulary) {
          word = word.substring(0, 1).toUpperCase.concat(word.substring(1))
          inVocabulary = vocabularySet.contains(word)
        }
        if (!inVocabulary) {
          word = word.toUpperCase()
          inVocabulary = vocabularySet.contains(word)
        }

        if (inVocabulary) {
          inVocabularyNum += 1
        } else {
          outVocabularyNum += 1
        }
      }

      if (wordArray.length < 3) {
        (user, (0, 0))
      } else if (inVocabularyNum > outVocabularyNum * filterWordThreshold) {
        (user, (1, 0))
      } else {
        (user, (0, 1))
      }
    })
      .reduceByKey((a, b) => (a._1 + b._1, a._2 + b._2))

    val resultMap = userInfoRDD.map(info => {
      val count = info._2
      if (count._1 > count._2 * filterSentenceThreshold) {
        (1, info._1)
      } else {
        (0, info._1)
      }
    })
      .countByKey()

    val out = new FileWriter(outputPath, false)
    resultMap.foreach(w => {
      out.write(w._1 + CharacterUtil.CONSTANT_TAB + w._2)
      out.write(CharacterUtil.CONSTANT_NEWLINE)
    })
    out.close()

  }

}
