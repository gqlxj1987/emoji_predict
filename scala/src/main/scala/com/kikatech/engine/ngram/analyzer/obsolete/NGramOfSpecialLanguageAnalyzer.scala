package com.kikatech.engine.ngram.analyzer.obsolete

import com.kikatech.engine.ngram.analyzer.Analyzer
import com.kikatech.engine.ngram.util._
import org.apache.spark.SparkContext
import org.apache.spark.storage.StorageLevel

import scala.collection.mutable

/**
  * Created by huminghe on 2017/8/31.
  */
class NGramOfSpecialLanguageAnalyzer extends Analyzer {

  override def doAnalyze(sparkContext: SparkContext, paramArray: Array[String]): Unit = {

    val dict1Weight = 0.03
    val dict2Weight = 0.03
    val dict3Weight = 0.03
    val originWeight = 0.03

    val onlineWeight = 0.7
    val twitterWeight = 0.3

    val dict1BiGramWeight = 1
    val dict2BiGramWeight = 1
    val dict3BiGramWeight = 1
    val originBiGramWeight = 1

    val onlineBiGramWeight = 1
    val twitterBiGramWeight = 1

    val resultNumberLimit = 300000
    val biGramNumberLimit = 9

    val separator = CharacterUtil.CONSTANT_TAB

    val intermediateNGramPath = "/corpus/ngram/"

    if (paramArray.length != 15) {
      sparkContext.stop()
    }

    val jobName = paramArray(0)
    val dict1UniGramPath = paramArray(1)
    val dict2UniGramPath = paramArray(2)
    val dict3UniGramPath = paramArray(3)
    val originUniGramPath = paramArray(4)
    val dict1BiGramPath = paramArray(5)
    val dict2BiGramPath = paramArray(6)
    val dict3BiGramPath = paramArray(7)
    val originBiGramPath = paramArray(8)
    val onlineCorpusPath = paramArray(9)
    val twitterCorpusPath = paramArray(10)
    val language = paramArray(11)
    val uniGramOutputPath = paramArray(12)
    val biGramOutputPath = paramArray(13)
    val partitionNum = paramArray(14).toInt

    logInfo("job name: " + jobName)
    logInfo("dict 1 uniGram path: " + dict1UniGramPath)
    logInfo("dict 2 uniGram path: " + dict2UniGramPath)
    logInfo("dict 3 uniGram path: " + dict3UniGramPath)
    logInfo("origin uniGram path: " + originUniGramPath)
    logInfo("dict 1 biGram path: " + dict1BiGramPath)
    logInfo("dict 2 biGram path: " + dict2BiGramPath)
    logInfo("dict 3 biGram path: " + dict3BiGramPath)
    logInfo("origin biGram path: " + originBiGramPath)
    logInfo("online corpus path: " + onlineCorpusPath)
    logInfo("twitter corpus path: " + twitterCorpusPath)
    logInfo("language: " + language)
    logInfo("uniGram output path: " + uniGramOutputPath)
    logInfo("biGram output path: " + biGramOutputPath)
    logInfo("partition num: " + partitionNum)

    val vocabularySet1 = DictUtil.getVocabularySet(sparkContext, dict1UniGramPath)
    val vocabularySet2 = DictUtil.getVocabularySet(sparkContext, dict2UniGramPath)
    val vocabularySet3 = DictUtil.getVocabularySet(sparkContext, dict3UniGramPath)
    val vocabularySet4 = DictUtil.getVocabularySet(sparkContext, originUniGramPath)
    val vocabularySet = vocabularySet1 ++ vocabularySet2 ++ vocabularySet3 ++ vocabularySet4

    val vocabularyWeightMap: collection.mutable.Map[String, Double] = new mutable.HashMap[String, Double]()

    DictUtil.addScoreToVocabularyWeightMap(vocabularyWeightMap, vocabularySet1, dict1Weight)
    DictUtil.addScoreToVocabularyWeightMap(vocabularyWeightMap, vocabularySet2, dict2Weight)
    DictUtil.addScoreToVocabularyWeightMap(vocabularyWeightMap, vocabularySet3, dict3Weight)
    DictUtil.addScoreToVocabularyWeightMap(vocabularyWeightMap, vocabularySet4, originWeight)

    val twitterSentenceRDD = CorpusUtil.getTwitterSentenceRDD(sparkContext, twitterCorpusPath, partitionNum)
    twitterSentenceRDD.persist(StorageLevel.DISK_ONLY)
    val twitterUniGramRDD = NGramUtil.getNGramRDD(twitterSentenceRDD, separator, vocabularySet, 1, language).map(info => (info._1, info._2.toDouble))
    twitterUniGramRDD.persist()
    val twitterBiGramRDD = NGramUtil.getNGramRDD(twitterSentenceRDD, separator, vocabularySet, 2, language).map(info => (info._1, info._2.toDouble))
    twitterSentenceRDD.unpersist()

    val twitterUniGramScoreRDD = NGramUtil.getUniGramScoreRDDOfDouble(twitterUniGramRDD)
    val twitterBiGramScoreRDD = NGramUtil.getBiGramScoreRDDOfDouble(twitterUniGramRDD, twitterBiGramRDD, separator, twitterBiGramWeight)
    twitterUniGramRDD.unpersist()
    twitterUniGramScoreRDD.persist()
    twitterBiGramScoreRDD.persist(StorageLevel.DISK_ONLY)

    SparkUtil.deletePath(sparkContext, intermediateNGramPath + jobName)
    twitterUniGramScoreRDD.map(rdd => rdd._1 + separator + rdd._2).saveAsTextFile(intermediateNGramPath + jobName + "/twitterUniGram")
    twitterBiGramScoreRDD.map(rdd => rdd._1 + separator + rdd._2).saveAsTextFile(intermediateNGramPath + jobName + "/twitterBiGram")

    val onlineCorpusWeightedSentenceRDD = CorpusUtil.getOnlineWeightedSentenceRDD(sparkContext, onlineCorpusPath, partitionNum)
    onlineCorpusWeightedSentenceRDD.persist(StorageLevel.DISK_ONLY)
    val onlineUniGramRDD = NGramUtil.getWeightedNGramRDD(onlineCorpusWeightedSentenceRDD, separator, vocabularySet, 1, language)
    onlineUniGramRDD.persist()
    val onlineBiGramRDD = NGramUtil.getWeightedNGramRDD(onlineCorpusWeightedSentenceRDD, separator, vocabularySet, 2, language)
    onlineCorpusWeightedSentenceRDD.unpersist()

    val onlineUniGramScoreRDD = NGramUtil.getUniGramScoreRDDOfDouble(onlineUniGramRDD)
    val onlineBiGramScoreRDD = NGramUtil.getBiGramScoreRDDOfDouble(onlineUniGramRDD, onlineBiGramRDD, separator, onlineBiGramWeight)
    onlineUniGramRDD.unpersist()
    onlineUniGramScoreRDD.persist()
    onlineBiGramScoreRDD.persist(StorageLevel.DISK_ONLY)

    onlineUniGramScoreRDD.map(rdd => rdd._1 + separator + rdd._2).saveAsTextFile(intermediateNGramPath + jobName + "/onlineCorpusUniGram")
    onlineBiGramScoreRDD.map(rdd => rdd._1 + separator + rdd._2).saveAsTextFile(intermediateNGramPath + jobName + "/onlineCorpusBiGram")

    val mergedUniGramScoreRDD = twitterUniGramScoreRDD.map(score => (score._1, score._2 * twitterWeight))
      .union(onlineUniGramScoreRDD.map(score => (score._1, score._2 * onlineWeight)))
      .reduceByKey((a, b) => a + b)
      .map(score => {
        val weight = vocabularyWeightMap.getOrElse(score._1, 1.0)
        (score._1, score._2 * weight)
      })
      .sortBy(a => a._2, ascending = false)

    val max = mergedUniGramScoreRDD.first()._2

    val finalUniGramScoreRDD = mergedUniGramScoreRDD.map(score => {
      val scoreInRange = Math.round(score._2 / max * 250) + 1
      (score._1, scoreInRange)
    })

    NGramUtil.writeUniGramFile(twitterUniGramScoreRDD.map(x => (x._1, Math.round(x._2))), separator, uniGramOutputPath + ".twitter", resultNumberLimit)
    NGramUtil.writeUniGramFile(onlineUniGramScoreRDD.map(x => (x._1, Math.round(x._2))), separator, uniGramOutputPath + ".online", resultNumberLimit)

    val uniGramOutput = NGramUtil.writeUniGramFile(finalUniGramScoreRDD, separator, uniGramOutputPath, resultNumberLimit)

    val dict1BiGramScoreRDD = DictUtil.getBiGramRDD(sparkContext, dict1BiGramPath, dict1BiGramWeight)
    val dict2BiGramScoreRDD = DictUtil.getBiGramRDD(sparkContext, dict2BiGramPath, dict2BiGramWeight)
    val dict3BiGramScoreRDD = DictUtil.getBiGramRDD(sparkContext, dict3BiGramPath, dict3BiGramWeight)
    val originBiGramScoreRDD = DictUtil.getOriginalBiGramRDD(sparkContext, originBiGramPath, originBiGramWeight, separator)

    val vocabulary = uniGramOutput.map(line => line.split(separator)(0))

    val biGramScoreFromDict = dict1BiGramScoreRDD.union(dict2BiGramScoreRDD).union(dict3BiGramScoreRDD).union(originBiGramScoreRDD)
      .repartition(200)
      .filter(rdd => vocabulary.contains(rdd._1) && vocabulary.contains(rdd._2))

    val biGramScoreWithoutKika = dict1BiGramScoreRDD.union(dict2BiGramScoreRDD).union(dict3BiGramScoreRDD)
      .repartition(200)
      .filter(rdd => vocabulary.contains(rdd._1) && vocabulary.contains(rdd._2))

    val biGramScoreFromCorpus = twitterBiGramScoreRDD.union(onlineBiGramScoreRDD)
      .repartition(200)
      .filter(rdd => vocabulary.contains(rdd._1) && vocabulary.contains(rdd._2))

    val mergedBiGramScore = biGramScoreFromDict.union(biGramScoreFromCorpus).repartition(200)
    val mergedBiGramScore2 = biGramScoreFromDict.map(x => (x._1, x._2, x._3 * 2)).union(biGramScoreFromCorpus).repartition(200)
    val mergedBiGramScore3 = biGramScoreFromDict.map(x => (x._1, x._2, x._3 * 0.5)).union(biGramScoreFromCorpus).repartition(200)

    val finalDict1BiGramScore = dict1BiGramScoreRDD.map(x => (x._1, x._2, Math.round(x._3)))
    val finalDict2BiGramScore = dict2BiGramScoreRDD.map(x => (x._1, x._2, Math.round(x._3)))
    val finalDict3BiGramScore = dict3BiGramScoreRDD.map(x => (x._1, x._2, Math.round(x._3)))
    val finalOriginBiGramScore = originBiGramScoreRDD.map(x => (x._1, x._2, Math.round(x._3)))
    val finalBiGramScoreFromDict = NGramUtil.mergeBiGramScore(biGramScoreFromDict, biGramNumberLimit)

    NGramUtil.writeBiGramFile(finalDict1BiGramScore, separator, biGramOutputPath + ".dict1")
    NGramUtil.writeBiGramFile(finalDict2BiGramScore, separator, biGramOutputPath + ".dict2")
    NGramUtil.writeBiGramFile(finalDict3BiGramScore, separator, biGramOutputPath + ".dict3")
    NGramUtil.writeBiGramFile(finalOriginBiGramScore, separator, biGramOutputPath + ".origin")
    NGramUtil.writeBiGramFile(finalBiGramScoreFromDict, separator, biGramOutputPath + ".mergeDict")

    val finalBiGramScoreWithoutKika = NGramUtil.mergeBiGramScore(biGramScoreWithoutKika, biGramNumberLimit)
    NGramUtil.writeBiGramFile(finalBiGramScoreWithoutKika, separator, biGramOutputPath + ".withoutKika")

    val finalBiGramScoreFromTwitter = NGramUtil.mergeBiGramScore(twitterBiGramScoreRDD, biGramNumberLimit)
    NGramUtil.writeBiGramFile(finalBiGramScoreFromTwitter, separator, biGramOutputPath + ".twitter")

    val finalBiGramScoreFromOnline = NGramUtil.mergeBiGramScore(onlineBiGramScoreRDD, biGramNumberLimit)
    NGramUtil.writeBiGramFile(finalBiGramScoreFromOnline, separator, biGramOutputPath + ".online")

    val finalBiGramScoreFromCorpus = NGramUtil.mergeBiGramScore(biGramScoreFromCorpus, biGramNumberLimit)
    NGramUtil.writeBiGramFile(finalBiGramScoreFromCorpus, separator, biGramOutputPath + ".corpus")

    val finalBiGramScoreMerged = NGramUtil.mergeBiGramScore(mergedBiGramScore, biGramNumberLimit)
    NGramUtil.writeBiGramFile(finalBiGramScoreMerged, separator, biGramOutputPath + ".merged")

    val finalBiGramScoreMerged2 = NGramUtil.mergeBiGramScore(mergedBiGramScore2, biGramNumberLimit)
    NGramUtil.writeBiGramFile(finalBiGramScoreMerged2, separator, biGramOutputPath + ".merged2")

    val finalBiGramScoreMerged3 = NGramUtil.mergeBiGramScore(mergedBiGramScore3, biGramNumberLimit)
    NGramUtil.writeBiGramFile(finalBiGramScoreMerged3, separator, biGramOutputPath + ".merged3")


    twitterUniGramScoreRDD.unpersist()
    twitterBiGramScoreRDD.unpersist()
    onlineUniGramScoreRDD.unpersist()
    onlineBiGramScoreRDD.unpersist()

  }

}