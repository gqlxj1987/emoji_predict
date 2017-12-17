package com.kikatech.engine.ngram.analyzer

import com.kikatech.engine.ngram.util._
import org.apache.spark.SparkContext
import org.apache.spark.storage.StorageLevel

/**
  * Created by huminghe on 2017/8/14.
  */
class NGramCustomizedLocaleAnalyzer extends Analyzer {

  override def doAnalyze(sparkContext: SparkContext, paramArray: Array[String]): Unit = {

    // TODO: 使用数据库或配置文件读取权重等

    val weight1 = 0.5
    val weight2 = 0.5

    val resultNumberLimit = 200000
    val biGramNumberLimit = 9

    val separator = CharacterUtil.CONSTANT_TAB

    val filteredCorpusPath = "/corpus/filtered/"

    if (paramArray.length != 9) {
      sparkContext.stop()
    }

    val jobName = paramArray(0)
    val vocabularyPath = paramArray(1)
    val nation = paramArray(2)
    val corpus1Path = paramArray(3)
    val corpus2Path = paramArray(4)
    val uniGramOutputPath = paramArray(5)
    val biGramOutputPath = paramArray(6)
    val saveSentences = paramArray(7)
    val partitionNum = paramArray(8).toInt

    logInfo("job name: " + jobName)
    logInfo("vocabulary path: " + vocabularyPath)
    logInfo("nation: " + nation)
    logInfo("corpus1 path: " + corpus1Path)
    logInfo("corpus2 path: " + corpus2Path)
    logInfo("unigram output path: " + uniGramOutputPath)
    logInfo("bigram output path: " + biGramOutputPath)
    logInfo("save sentences: " + saveSentences)
    logInfo("partition num: " + partitionNum)

    val vocabularySet = DictUtil.getVocabularySet(sparkContext, vocabularyPath)

    val sentenceRDD1 = CorpusUtil.getOnlineWeightedSentenceRDDByLocale(sparkContext, corpus1Path, nation, partitionNum)
    sentenceRDD1.persist(StorageLevel.DISK_ONLY)
    if (saveSentences.toBoolean) {
      SparkUtil.deletePath(sparkContext, filteredCorpusPath + jobName)
      sentenceRDD1.map(_._1).saveAsTextFile(filteredCorpusPath + jobName + "/1")
    }

    val uniGramRDD1 = NGramUtil.getWeightedNGramRDD(sentenceRDD1, separator, vocabularySet, 1)
    val biGramRDD1 = NGramUtil.getWeightedNGramRDD(sentenceRDD1, separator, vocabularySet, 2)
    sentenceRDD1.unpersist()

    val uniGramScoreRDD1 = NGramUtil.getUniGramScoreRDDOfDouble(uniGramRDD1)
    val biGramScoreRDD1 = NGramUtil.getBiGramScoreRDDOfDouble(uniGramRDD1, biGramRDD1, separator, weight1)
    uniGramScoreRDD1.persist()
    biGramScoreRDD1.persist()

    val sentenceRDD2 = CorpusUtil.getOnlineWeightedSentenceRDDByLocale(sparkContext, corpus2Path, nation, partitionNum)
    sentenceRDD2.persist(StorageLevel.DISK_ONLY)
    if (saveSentences.toBoolean) {
      sentenceRDD2.map(_._1).saveAsTextFile(filteredCorpusPath + jobName + "/2")
    }

    val uniGramRDD2 = NGramUtil.getWeightedNGramRDD(sentenceRDD2, separator, vocabularySet, 1)
    val biGramRDD2 = NGramUtil.getWeightedNGramRDD(sentenceRDD2, separator, vocabularySet, 2)
    sentenceRDD2.unpersist()

    val uniGramScoreRDD2 = NGramUtil.getUniGramScoreRDDOfDouble(uniGramRDD2)
    val biGramScoreRDD2 = NGramUtil.getBiGramScoreRDDOfDouble(uniGramRDD2, biGramRDD2, separator, weight2)
    uniGramScoreRDD1.persist()
    biGramScoreRDD2.persist()

    val mergedUniGramScoreRDD = uniGramScoreRDD1.map(score => (score._1, score._2 * weight1))
      .union(uniGramScoreRDD2.map(score => (score._1, score._2 * weight2)))
      .reduceByKey((a, b) => a + b)
      .sortBy(a => a._2, ascending = false)

    val max = mergedUniGramScoreRDD.first()._2

    val finalUniGramScoreRDD = mergedUniGramScoreRDD.map(score => {
      val scoreInRange = Math.round(score._2 / max * 250) + 1
      (score._1, scoreInRange)
    })

    NGramUtil.writeUniGramFile(finalUniGramScoreRDD, separator, uniGramOutputPath, resultNumberLimit)

    val finalBiGramScoreRDD = biGramScoreRDD1.union(biGramScoreRDD2)
      .repartition(200)
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

    uniGramScoreRDD1.unpersist()
    uniGramScoreRDD2.unpersist()
    biGramScoreRDD1.unpersist()
    biGramScoreRDD2.unpersist()

  }

}
