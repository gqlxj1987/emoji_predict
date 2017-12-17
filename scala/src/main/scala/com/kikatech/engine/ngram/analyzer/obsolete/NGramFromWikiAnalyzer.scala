package com.kikatech.engine.ngram.analyzer.obsolete

import java.io.FileWriter

import com.kikatech.engine.ngram.analyzer.Analyzer
import com.kikatech.engine.ngram.util._
import org.apache.commons.lang.math.RandomUtils
import org.apache.spark.SparkContext
import org.apache.spark.storage.StorageLevel

import scala.collection.mutable

/**
  * Created by huminghe on 2017/8/23.
  */
class NGramFromWikiAnalyzer extends Analyzer {

  override def doAnalyze(sparkContext: SparkContext, paramArray: Array[String]): Unit = {

    val dict1Weight = 0.4
    val dict2Weight = 0.4
    val dict3Weight = 0.4
    val originWeight = 0.4

    val onlineWeight = 0.1
    val twitterWeight = 0.1
    val wikiWeight = 0.8

    val dict1BiGramWeight = 1
    val dict2BiGramWeight = 1
    val dict3BiGramWeight = 1
    val originBiGramWeight = 1

    val onlineBiGramWeight = 1
    val twitterBiGramWeight = 1
    val wikiBiGramWeight = 1

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
    val wikiCorpusPath = paramArray(11)
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
    logInfo("wiki corpus path: " + wikiCorpusPath)
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
    val twitterUniGramRDD = NGramUtil.getNGramRDD(twitterSentenceRDD, separator, vocabularySet, 1).map(info => (info._1, info._2.toDouble))
    twitterUniGramRDD.persist()
    val twitterBiGramRDD = NGramUtil.getNGramRDD(twitterSentenceRDD, separator, vocabularySet, 2).map(info => (info._1, info._2.toDouble))
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
    val onlineUniGramRDD = NGramUtil.getWeightedNGramRDD(onlineCorpusWeightedSentenceRDD, separator, vocabularySet, 1)
    onlineUniGramRDD.persist()
    val onlineBiGramRDD = NGramUtil.getWeightedNGramRDD(onlineCorpusWeightedSentenceRDD, separator, vocabularySet, 2)
    onlineCorpusWeightedSentenceRDD.unpersist()

    val onlineUniGramScoreRDD = NGramUtil.getUniGramScoreRDDOfDouble(onlineUniGramRDD)
    val onlineBiGramScoreRDD = NGramUtil.getBiGramScoreRDDOfDouble(onlineUniGramRDD, onlineBiGramRDD, separator, onlineBiGramWeight)
    onlineUniGramRDD.unpersist()
    onlineUniGramScoreRDD.persist()
    onlineBiGramScoreRDD.persist(StorageLevel.DISK_ONLY)

    onlineUniGramScoreRDD.map(rdd => rdd._1 + separator + rdd._2).saveAsTextFile(intermediateNGramPath + jobName + "/onlineCorpusUniGram")
    onlineBiGramScoreRDD.map(rdd => rdd._1 + separator + rdd._2).saveAsTextFile(intermediateNGramPath + jobName + "/onlineCorpusBiGram")

    val wikiSentenceRDD = CorpusUtil.getWikiSentenceRDD(sparkContext, wikiCorpusPath, vocabularySet, partitionNum)
    wikiSentenceRDD.persist()
    val wikiUniGramRDD = NGramUtil.getNGramRDD(wikiSentenceRDD, separator, vocabularySet, 1).map(info => (info._1, info._2.toDouble))
    wikiUniGramRDD.persist()
    val wikiBiGramRDD = NGramUtil.getNGramRDD(wikiSentenceRDD, separator, vocabularySet, 2).map(info => (info._1, info._2.toDouble))

    val wikiUniGramScoreRDD = NGramUtil.getUniGramScoreRDDOfDouble(wikiUniGramRDD)
    val wikiBiGramScoreRDD = NGramUtil.getBiGramScoreRDDOfDouble(wikiUniGramRDD, wikiBiGramRDD, separator, wikiBiGramWeight)
    wikiUniGramRDD.unpersist()
    wikiUniGramScoreRDD.persist()
    wikiBiGramScoreRDD.persist(StorageLevel.DISK_ONLY)

    wikiUniGramScoreRDD.map(rdd => rdd._1 + separator + rdd._2).saveAsTextFile(intermediateNGramPath + jobName + "/wikiUniGram")
    wikiBiGramScoreRDD.map(rdd => rdd._1 + separator + rdd._2).saveAsTextFile(intermediateNGramPath + jobName + "/wikiBiGram")

    val mergedUniGramScoreRDD = twitterUniGramScoreRDD.map(score => (score._1, score._2 * twitterWeight))
      .union(onlineUniGramScoreRDD.map(score => (score._1, score._2 * onlineWeight)))
      .union(wikiUniGramScoreRDD.map(score => (score._1, score._2 * wikiWeight)))
      .reduceByKey((a, b) => a + b)
      .map(score => {
        val weight = vocabularyWeightMap.getOrElse(score._1, 1.0)
        (score._1, score._2 * weight)
      })
      .sortBy(a => a._2, ascending = false)

    val max = if (mergedUniGramScoreRDD.isEmpty()) {
      1
    } else {
      mergedUniGramScoreRDD.first()._2
    }

    val finalUniGramScoreRDD = mergedUniGramScoreRDD.map(score => {
      val scoreInRange = Math.round(score._2 / max * 250) + 1
      (score._1, scoreInRange)
    })

    val uniGramOutput = NGramUtil.writeUniGramFile(finalUniGramScoreRDD, separator, uniGramOutputPath, resultNumberLimit)

    val dict1BiGramScoreRDD = DictUtil.getBiGramRDD(sparkContext, dict1BiGramPath, dict1BiGramWeight)
    val dict2BiGramScoreRDD = DictUtil.getBiGramRDD(sparkContext, dict2BiGramPath, dict2BiGramWeight)
    val dict3BiGramScoreRDD = DictUtil.getBiGramRDD(sparkContext, dict3BiGramPath, dict3BiGramWeight)
    val originBiGramScoreRDD = DictUtil.getOriginalBiGramRDD(sparkContext, originBiGramPath, originBiGramWeight, separator)

    val vocabulary = uniGramOutput.map(line => line.split(separator)(0))

    val biGramScoreFromDict = dict1BiGramScoreRDD.union(dict2BiGramScoreRDD).union(dict3BiGramScoreRDD).union(originBiGramScoreRDD)
      .repartition(200)
      .filter(rdd => vocabulary.contains(rdd._1) && vocabulary.contains(rdd._2))

    val biGramScoreFromCorpus = twitterBiGramScoreRDD.union(onlineBiGramScoreRDD).union(wikiBiGramScoreRDD)
      .repartition(200)
      .filter(rdd => vocabulary.contains(rdd._1) && vocabulary.contains(rdd._2))

    val mergedBiGramScore = biGramScoreFromDict.union(biGramScoreFromCorpus).repartition(200)

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

    val finalBiGramScoreFromCorpus = NGramUtil.mergeBiGramScore(biGramScoreFromCorpus, biGramNumberLimit)
    NGramUtil.writeBiGramFile(finalBiGramScoreFromCorpus, separator, biGramOutputPath + ".corpus")

    val finalBiGramScoreMerged = NGramUtil.mergeBiGramScore(mergedBiGramScore, biGramNumberLimit)
    NGramUtil.writeBiGramFile(finalBiGramScoreMerged, separator, biGramOutputPath + ".merged")

    twitterUniGramScoreRDD.unpersist()
    twitterBiGramScoreRDD.unpersist()
    onlineUniGramScoreRDD.unpersist()
    onlineBiGramScoreRDD.unpersist()

    val filter = vocabulary.take(10000)

    val filteredSentences = wikiSentenceRDD
      .map(sentence => {
        val words = sentence
          .split(CharacterUtil.CONSTANT_SPACE32)
          .filter(_.nonEmpty)
        var b = true
        b = b && words.length > 2
        var includeWordsNumber = 0
        var longWordNumber = 0
        for (word <- words) {
          if (filter.contains(word)) {
            includeWordsNumber += 1
            if (word.length > 2) {
              longWordNumber += 1
            }
          }
        }
        b = b && (longWordNumber >= 4)
        b = b && (includeWordsNumber * 8 > words.length * 7)
        if (b) {
          words.mkString(CharacterUtil.CONSTANT_SPACE32)
        } else {
          CharacterUtil.CONSTANT_EMPTY
        }
      })
      .filter(_.nonEmpty)
      .distinct()
      .map(x => (x, RandomUtils.nextDouble()))
      .sortBy(x => x._2, ascending = false)
      .map(_._1)
      .take(10000)

    wikiSentenceRDD.unpersist()

    val output = new FileWriter(uniGramOutputPath + ".testCorpus", false)
    filteredSentences.foreach(w => {
      output.write(w)
      output.write(CharacterUtil.CONSTANT_NEWLINE)
    })
    output.close()
  }

}
