package com.kikatech.engine.ngram.analyzer.obsolete

import com.kikatech.engine.ngram.analyzer.Analyzer
import com.kikatech.engine.ngram.util.{CharacterUtil, CorpusUtil, DictUtil, NGramUtil}
import org.apache.spark.SparkContext

/**
  * Created by huminghe on 2017/7/10.
  */
class NGramFromVocabularyAnalyzer extends Analyzer {

  override def doAnalyze(sparkContext: SparkContext, paramArray: Array[String]): Unit = {

    val onlineWeight = 0.7
    val twitterWeight = 0.3

    val onlineBiGramWeight = 0.7
    val twitterBiGramWeight = 0.3

    val resultNumberLimit = 200000
    val biGramNumberLimit = 9

    val separator = CharacterUtil.CONSTANT_TAB

    if (paramArray.length != 7) {
      sparkContext.stop()
    }

    val jobName = paramArray(0)
    val vocabularyPath = paramArray(1)
    val onlineCorpusPath = paramArray(2)
    val twitterCorpusPath = paramArray(3)
    val uniGramOutputPath = paramArray(4)
    val biGramOutputPath = paramArray(5)
    val partitionNum = paramArray(6).toInt

    logInfo("job name: " + jobName)
    logInfo("vocabulary path: " + vocabularyPath)
    logInfo("online corpus path: " + onlineCorpusPath)
    logInfo("twitter corpus path: " + twitterCorpusPath)
    logInfo("unigram output path: " + uniGramOutputPath)
    logInfo("bigram output path: " + biGramOutputPath)
    logInfo("partition num: " + partitionNum)

    val vocabularySet = DictUtil.getVocabularySet(sparkContext, vocabularyPath)

    val twitterSentenceRDD = CorpusUtil.getTwitterSentenceRDD(sparkContext, twitterCorpusPath, partitionNum)
    val twitterUniGramRDD = NGramUtil.getNGramRDD(twitterSentenceRDD, separator, vocabularySet, 1).map(info => (info._1, info._2.toDouble))
    val twitterBiGramRDD = NGramUtil.getNGramRDD(twitterSentenceRDD, separator, vocabularySet, 2).map(info => (info._1, info._2.toDouble))

    twitterUniGramRDD.persist()
    twitterBiGramRDD.persist()

    val twitterUniGramScoreRDD = NGramUtil.getUniGramScoreRDDOfDouble(twitterUniGramRDD)
    val twitterBiGramScoreRDD = NGramUtil.getBiGramScoreRDDOfDouble(twitterUniGramRDD, twitterBiGramRDD, separator, twitterBiGramWeight)

    val onlineCorpusWeightedSentenceRDD = CorpusUtil.getOnlineWeightedSentenceRDD(sparkContext, onlineCorpusPath, partitionNum)
    val onlineUniGramRDD = NGramUtil.getWeightedNGramRDD(onlineCorpusWeightedSentenceRDD, separator, vocabularySet, 1)
    val onlineBiGramRDD = NGramUtil.getWeightedNGramRDD(onlineCorpusWeightedSentenceRDD, separator, vocabularySet, 2)

    onlineUniGramRDD.persist()
    onlineBiGramRDD.persist()

    val onlineUniGramScoreRDD = NGramUtil.getUniGramScoreRDDOfDouble(onlineUniGramRDD)
    val onlineBiGramScoreRDD = NGramUtil.getBiGramScoreRDDOfDouble(onlineUniGramRDD, onlineBiGramRDD, separator, onlineBiGramWeight)

    val mergedUniGramScoreRDD = twitterUniGramScoreRDD.map(score => (score._1, score._2 * twitterWeight))
      .union(onlineUniGramScoreRDD.map(score => (score._1, score._2 * onlineWeight)))
      .reduceByKey((a, b) => a + b)
      .sortBy(a => a._2, ascending = false)

    val max = mergedUniGramScoreRDD.first()._2

    val finalUniGramScoreRDD = mergedUniGramScoreRDD.map(score => {
      val scoreInRange = Math.round(score._2 / max * 250) + 1
      (score._1, scoreInRange)
    })

    NGramUtil.writeUniGramFile(finalUniGramScoreRDD, separator, uniGramOutputPath, resultNumberLimit)

    val finalBiGramScoreRDD = twitterBiGramScoreRDD.union(onlineBiGramScoreRDD)
      .groupBy(_._1)
      .flatMap((info: (String, Iterable[(String, String, Double)])) => {
        val total = info._2.map(_._3).sum
        info._2
          .groupBy(_._2)
          .map(x => {
            val sum = x._2.map(_._3).sum
            (info._1, x._1, sum)
          })
          .toSeq
          .sortWith((a, b) => a._3 > b._3)
          .take(biGramNumberLimit)
          .map(x => {
            val score = Math.round(x._3 / total * 14) + 1
            (x._1, x._2, score)
          })
      })

    NGramUtil.writeBiGramFile(finalBiGramScoreRDD, separator, biGramOutputPath)

    twitterUniGramRDD.unpersist()
    twitterBiGramRDD.unpersist()
    onlineUniGramRDD.unpersist()
    onlineBiGramRDD.unpersist()

  }

}
