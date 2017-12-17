package com.kikatech.engine.ngram.analyzer

import java.io.FileWriter

import com.kikatech.engine.ngram.util.{CharacterUtil, CorpusUtil, DictUtil}
import org.apache.spark.SparkContext
import org.apache.spark.storage.StorageLevel

/**
  * Created by huminghe on 2017/8/16.
  */
class CustomUserCorpusAnalyzer extends Analyzer {

  override def doAnalyze(sparkContext: SparkContext, paramArray: Array[String]): Unit = {

    if (paramArray.length != 9) {
      sparkContext.stop()
    }

    val jobName = paramArray(0)
    val corpusPath = paramArray(1)
    val vocabularyPath = paramArray(2)
    val excludeVocabularyPath = paramArray(3)
    val filterWordThreshold = paramArray(4)
    val filterSentenceThreshold = paramArray(5)
    val nation = paramArray(6)
    val outputPath = paramArray(7)
    val partitionNum = paramArray(8).toInt

    logInfo("job name: " + jobName)
    logInfo("corpus path: " + corpusPath)
    logInfo("vocabulary path: " + vocabularyPath)
    logInfo("exclude vocabulary path: " + excludeVocabularyPath)
    logInfo("filter word threshold: " + filterWordThreshold)
    logInfo("filter sentence threshold: " + filterSentenceThreshold)
    logInfo("nation: " + nation)
    logInfo("output path: " + outputPath)
    logInfo("partition num: " + partitionNum)

    val vocabularySet = DictUtil.getVocabularySetLowerCase(sparkContext, vocabularyPath)

    val excludeVocabularySet = DictUtil.getVocabularySetLowerCase(sparkContext, excludeVocabularyPath)

    val userSentenceRDD = CorpusUtil.getOnlineUserSentenceRDDByLocale(sparkContext, corpusPath, nation, partitionNum)

    userSentenceRDD.persist(StorageLevel.DISK_ONLY)

    val userInfoRDD = userSentenceRDD.map(info => {
      val user = info._2
      val sentence = info._1.toLowerCase
      val wordArray = sentence
        .split(CharacterUtil.CONSTANT_SPACE32)
        .filter(_.nonEmpty)

      var inVocabularyNum = 0
      var outVocabularyNum = 0

      for (idx <- wordArray.indices) {
        val word = wordArray(idx)
        val inVocabulary = vocabularySet.contains(word)

        if (inVocabulary) {
          inVocabularyNum += 1
        } else {
          outVocabularyNum += 1
        }
      }

      if (wordArray.length < 4) {
        (user, (0, 0))
      } else if (inVocabularyNum > filterWordThreshold.toDouble * wordArray.length) {
        (user, (1, 0))
      } else {
        (user, (0, 1))
      }
    })
      .reduceByKey((a, b) => (a._1 + b._1, a._2 + b._2))

    val userTag = userInfoRDD.map(info => {
      val count = info._2
      if (count._1 > count._2 * filterSentenceThreshold.toDouble) {
        (1, info._1)
      } else {
        (0, info._1)
      }
    })

    val targetUsers = userTag.filter(_._1 == 1).map(_._2).collect().toSet

    val out = new FileWriter(outputPath + "/users.txt", false)
    targetUsers.foreach(w => {
      out.write(w)
      out.write(CharacterUtil.CONSTANT_NEWLINE)
    })
    out.close()

    val targetSentences = userSentenceRDD
      .filter(info => targetUsers.contains(info._2))
      .map(_._1)
      .filter(sentence => {
        val words = sentence.toLowerCase.split(CharacterUtil.CONSTANT_SPACE32)
        var includeWordsNumber = 0
        var excludeWordsNumber = 0
        for (word <- words) {
          if (vocabularySet.contains(word)) {
            includeWordsNumber += 1
          }
          if (excludeVocabularySet.contains(word)) {
            excludeWordsNumber += 1
          }
        }
        includeWordsNumber > filterWordThreshold.toDouble * words.length && excludeWordsNumber * 2 <= includeWordsNumber
      })

    val sentenceOut = new FileWriter(outputPath + "/sentences.txt", false)
    targetSentences.distinct().collect().foreach(s => {
      sentenceOut.write(s)
      sentenceOut.write(CharacterUtil.CONSTANT_NEWLINE)
    })
    sentenceOut.close()

    val uniGram = targetSentences
      .map(_.replaceAll(CharacterUtil.CONSTANT_ONLINE_PUNCTUATION, CharacterUtil.CONSTANT_SPACE32))
      .flatMap(_.split(CharacterUtil.CONSTANT_SPACE32))
      .filter(_.nonEmpty)
      .map(_.toLowerCase())
      .map(word => {
        (word, 1)
      })
      .reduceByKey((a, b) => a + b)
      .sortBy(a => a._2, ascending = false)

    val uniGramInfo = uniGram.map(p => p._1 + CharacterUtil.CONSTANT_TAB + p._2.toString)
      .collect()

    val uniGramOut = new FileWriter(outputPath + "/uniGram.txt", false)
    uniGramInfo
      .foreach(w => {
        uniGramOut.write(w)
        uniGramOut.write(CharacterUtil.CONSTANT_NEWLINE)
      })
    uniGramOut.close()

    userSentenceRDD.unpersist()

  }

}
