package com.kikatech.engine.ngram.analyzer

import com.kikatech.engine.ngram.util._
import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD
import org.apache.spark.storage.StorageLevel

import scala.collection.mutable

/**
  * Created by huminghe on 2017/9/7.
  */
class NGramIntegratedAnalyzer extends Analyzer {

  private val separator = CharacterUtil.CONSTANT_TAB
  private val uniGramNumberLimit = 300000
  private val biGramNumberLimit = 5
  private val triGramNumberLimit = 3
  private val triGramBaseNumberLimit = 60000
  private val intermediateNGramPath = "/corpus/ngram/"

  override def doAnalyze(sparkContext: SparkContext, paramArray: Array[String]): Unit = {

    // TODO: 使用数据库或配置文件读取权重等
    val dict1Weight = 0.15
    val dict2Weight = 0.15
    val dict3Weight = 0.15
    val originWeight = 0.15

    val onlineWeight = 1
    val twitterWeight = 0.3
    val wikiWeight = 0.3
    val crawlerWeight = 0.3

    val dict1BiGramWeight = 0.4
    val dict2BiGramWeight = 0.5
    val dict3BiGramWeight = 0.3
    val originBiGramWeight = 1

    val onlineBiGramWeight = 1
    val twitterBiGramWeight = 0.3
    val wikiBiGramWeight = 0.3
    val crawlerBiGramWeight = 0.3

    if (paramArray.length != 16) {
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
    val crawlerCorpusPath = paramArray(12)
    val language = paramArray(13)
    val outputPath = paramArray(14)
    val partitionNum = paramArray(15).toInt

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
    logInfo("crawler corpus path: " + crawlerCorpusPath)
    logInfo("language: " + language)
    logInfo("output path: " + outputPath)
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

    val twitterSentenceRDDRaw = CorpusUtil.getTwitterSentenceRDD(sparkContext, twitterCorpusPath, partitionNum)
    val twitterSentenceRDD = CorpusUtil.filterSentenceRDD(twitterSentenceRDDRaw, vocabularySet, language, 1, 0.7)
    twitterSentenceRDD.persist(StorageLevel.DISK_ONLY)
    val twitterUniGramRDD = NGramUtil.getNGramRDD(twitterSentenceRDD, separator, vocabularySet, 1, language).map(info => (info._1, info._2.toDouble))
    twitterUniGramRDD.persist()
    val twitterBiGramRDD = NGramUtil.getNGramRDD(twitterSentenceRDD, separator, vocabularySet, 2, language).map(info => (info._1, info._2.toDouble))
    twitterBiGramRDD.persist()

    val twitterUniGramScoreRDD = NGramUtil.getUniGramScoreRDDOfDouble(twitterUniGramRDD)
    val twitterBiGramScoreRDD = NGramUtil.getBiGramScoreRDDOfDouble(twitterUniGramRDD, twitterBiGramRDD, separator, twitterBiGramWeight)
    twitterUniGramRDD.unpersist()
    twitterUniGramScoreRDD.persist()
    twitterBiGramScoreRDD.persist()

    val twitterTriGramScoreRDD = getTriGramRDD(twitterBiGramRDD, twitterSentenceRDD, vocabularySet, language)
    twitterBiGramRDD.unpersist()

    SparkUtil.deletePath(sparkContext, intermediateNGramPath + jobName)
    outputResults(twitterUniGramScoreRDD, twitterBiGramScoreRDD, twitterTriGramScoreRDD, separator, outputPath, jobName, "twitter")

    val onlineCorpusWeightedSentenceRDD = CorpusUtil.getOnlineWeightedSentenceRDD(sparkContext, onlineCorpusPath, partitionNum)
    onlineCorpusWeightedSentenceRDD.persist(StorageLevel.DISK_ONLY)
    val onlineUniGramRDD = NGramUtil.getWeightedNGramRDD(onlineCorpusWeightedSentenceRDD, separator, vocabularySet, 1, language)
    onlineUniGramRDD.persist()
    val onlineBiGramRDD = NGramUtil.getWeightedNGramRDD(onlineCorpusWeightedSentenceRDD, separator, vocabularySet, 2, language)
    onlineBiGramRDD.persist()

    val onlineUniGramScoreRDD = NGramUtil.getUniGramScoreRDDOfDouble(onlineUniGramRDD)
    val onlineBiGramScoreRDD = NGramUtil.getBiGramScoreRDDOfDouble(onlineUniGramRDD, onlineBiGramRDD, separator, onlineBiGramWeight)
    onlineUniGramRDD.unpersist()
    onlineUniGramScoreRDD.persist()
    onlineBiGramScoreRDD.persist()

    val onlineTopBiGram = onlineBiGramRDD.take(triGramBaseNumberLimit).toSet
    val onlineTopBiGramWords = onlineTopBiGram.map(_._1)
    val onlineTriGramRDD = NGramUtil.getWeightedNGramRDDFilterByPrevious(onlineCorpusWeightedSentenceRDD,
      separator, vocabularySet, onlineTopBiGramWords, 3, language)
    val onlineTriGramScoreRDD = NGramUtil.getTriGramScoreRDD(onlineTopBiGram, onlineTriGramRDD, separator, triGramNumberLimit)

    onlineBiGramRDD.unpersist()
    onlineCorpusWeightedSentenceRDD.unpersist()
    outputResults(onlineUniGramScoreRDD, onlineBiGramScoreRDD, onlineTriGramScoreRDD, separator, outputPath, jobName, "online")

    val wikiSentenceRDDRaw = CorpusUtil.getWikiSentenceRDD(sparkContext, wikiCorpusPath, partitionNum)
    val wikiSentenceRDD = CorpusUtil.filterSentenceRDD(wikiSentenceRDDRaw, vocabularySet, language, 2, 0.7)
    wikiSentenceRDD.persist()
    val wikiUniGramRDD = NGramUtil.getNGramRDD(wikiSentenceRDD, separator, vocabularySet, 1, language).map(info => (info._1, info._2.toDouble))
    wikiUniGramRDD.persist()
    val wikiBiGramRDD = NGramUtil.getNGramRDD(wikiSentenceRDD, separator, vocabularySet, 2, language).map(info => (info._1, info._2.toDouble))
    wikiBiGramRDD.persist()

    val wikiUniGramScoreRDD = NGramUtil.getUniGramScoreRDDOfDouble(wikiUniGramRDD)
    val wikiBiGramScoreRDD = NGramUtil.getBiGramScoreRDDOfDouble(wikiUniGramRDD, wikiBiGramRDD, separator, wikiBiGramWeight)
    wikiUniGramRDD.unpersist()
    wikiUniGramScoreRDD.persist()
    wikiBiGramScoreRDD.persist()

    val wikiTriGramScoreRDD = getTriGramRDD(wikiBiGramRDD, wikiSentenceRDD, vocabularySet, language)
    wikiBiGramRDD.unpersist()

    outputResults(wikiUniGramScoreRDD, wikiBiGramScoreRDD, wikiTriGramScoreRDD, separator, outputPath, jobName, "wiki")

    val crawlerSentenceRDDRaw = CorpusUtil.getWikiSentenceRDD(sparkContext, crawlerCorpusPath, partitionNum)
    val crawlerSentenceRDD = CorpusUtil.filterSentenceRDD(crawlerSentenceRDDRaw, vocabularySet, language, 2, 0.7)
    crawlerSentenceRDD.persist()
    val crawlerUniGramRDD = NGramUtil.getNGramRDD(crawlerSentenceRDD, separator, vocabularySet, 1, language).map(info => (info._1, info._2.toDouble))
    crawlerUniGramRDD.persist()
    val crawlerBiGramRDD = NGramUtil.getNGramRDD(crawlerSentenceRDD, separator, vocabularySet, 2, language).map(info => (info._1, info._2.toDouble))
    crawlerBiGramRDD.persist()

    val crawlerUniGramScoreRDD = NGramUtil.getUniGramScoreRDDOfDouble(crawlerUniGramRDD)
    val crawlerBiGramScoreRDD = NGramUtil.getBiGramScoreRDDOfDouble(crawlerUniGramRDD, crawlerBiGramRDD, separator, crawlerBiGramWeight)
    crawlerUniGramRDD.unpersist()
    crawlerUniGramScoreRDD.persist()
    crawlerBiGramScoreRDD.persist()

    val crawlerTriGramScoreRDD = getTriGramRDD(crawlerBiGramRDD, crawlerSentenceRDD, vocabularySet, language)
    crawlerBiGramRDD.unpersist()

    outputResults(crawlerUniGramScoreRDD, crawlerBiGramScoreRDD, crawlerTriGramScoreRDD, separator, outputPath, jobName, "crawler")

    val mergedUniGramScoreRDD = twitterUniGramScoreRDD.map(score => (score._1, score._2 * twitterWeight))
      .union(onlineUniGramScoreRDD.map(score => (score._1, score._2 * onlineWeight)))
      .union(wikiUniGramScoreRDD.map(score => (score._1, score._2 * wikiWeight)))
      .union(crawlerUniGramScoreRDD.map(score => (score._1, score._2 * crawlerWeight)))
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

    val uniGramOutput = NGramUtil.writeUniGramFile(finalUniGramScoreRDD, separator, outputPath + "unigram.txt", uniGramNumberLimit)

    val dict1BiGramScoreRDD = DictUtil.getBiGramRDD(sparkContext, dict1BiGramPath, dict1BiGramWeight)
    val dict2BiGramScoreRDD = DictUtil.getBiGramRDD(sparkContext, dict2BiGramPath, dict2BiGramWeight)
    val dict3BiGramScoreRDD = DictUtil.getBiGramRDD(sparkContext, dict3BiGramPath, dict3BiGramWeight)
    val originBiGramScoreRDD = DictUtil.getOriginalBiGramRDD(sparkContext, originBiGramPath, originBiGramWeight, separator)

    val vocabulary = uniGramOutput.map(line => line.split(separator)(0))

    val biGramScoreFromDict = dict1BiGramScoreRDD.union(dict2BiGramScoreRDD).union(dict3BiGramScoreRDD).union(originBiGramScoreRDD)
      .repartition(200)
      .filter(rdd => vocabulary.contains(rdd._1) && vocabulary.contains(rdd._2))

    val biGramScoreFromCorpus = twitterBiGramScoreRDD.union(onlineBiGramScoreRDD).union(wikiBiGramScoreRDD).union(crawlerBiGramScoreRDD)
      .repartition(200)
      .filter(rdd => vocabulary.contains(rdd._1) && vocabulary.contains(rdd._2))

    val mergedBiGramScore = biGramScoreFromDict.union(biGramScoreFromCorpus).repartition(200)

    val finalDict1BiGramScore = dict1BiGramScoreRDD.map(x => (x._1, x._2, Math.round(x._3)))
    val finalDict2BiGramScore = dict2BiGramScoreRDD.map(x => (x._1, x._2, Math.round(x._3)))
    val finalDict3BiGramScore = dict3BiGramScoreRDD.map(x => (x._1, x._2, Math.round(x._3)))
    val finalOriginBiGramScore = originBiGramScoreRDD.map(x => (x._1, x._2, Math.round(x._3)))
    val finalBiGramScoreFromDict = NGramUtil.mergeBiGramScore(biGramScoreFromDict, biGramNumberLimit)

    NGramUtil.writeBiGramFile(finalDict1BiGramScore, separator, outputPath + "bigram.dict1.txt")
    NGramUtil.writeBiGramFile(finalDict2BiGramScore, separator, outputPath + "bigram.dict2.txt")
    NGramUtil.writeBiGramFile(finalDict3BiGramScore, separator, outputPath + "bigram.dict3.txt")
    NGramUtil.writeBiGramFile(finalOriginBiGramScore, separator, outputPath + "bigram.origin.txt")
    NGramUtil.writeBiGramFile(finalBiGramScoreFromDict, separator, outputPath + "bigram.mergeDict.txt")

    val finalBiGramScoreFromCorpus = NGramUtil.mergeBiGramScore(biGramScoreFromCorpus, biGramNumberLimit)
    NGramUtil.writeBiGramFile(finalBiGramScoreFromCorpus, separator, outputPath + "bigram.corpus.txt")

    val finalBiGramScoreMerged = NGramUtil.mergeBiGramScore(mergedBiGramScore, biGramNumberLimit)
    NGramUtil.writeBiGramFile(finalBiGramScoreMerged, separator, outputPath + "bigram.merged.txt")

    twitterUniGramScoreRDD.unpersist()
    twitterBiGramScoreRDD.unpersist()
    onlineUniGramScoreRDD.unpersist()
    onlineBiGramScoreRDD.unpersist()
    wikiUniGramScoreRDD.unpersist()
    wikiBiGramScoreRDD.unpersist()

    val filter = vocabulary.take(20000).toSet

    val filteredSentencesTwitter = getTestCorpus(twitterSentenceRDD, filter, language, 10000)
    NGramUtil.writeFile(filteredSentencesTwitter, outputPath + "testCorpus.twitter.txt")
    twitterSentenceRDD.unpersist()

    val filteredSentencesWiki = getTestCorpus(wikiSentenceRDD, filter, language, 10000)
    NGramUtil.writeFile(filteredSentencesWiki, outputPath + "testCorpus.wiki.txt")
    wikiSentenceRDD.unpersist()

    val filteredSentencesCrawler = getTestCorpus(crawlerSentenceRDD, filter, language, 10000)
    NGramUtil.writeFile(filteredSentencesCrawler, outputPath + "testCorpus.crawler.txt")
    crawlerSentenceRDD.unpersist()

  }

  private def getTriGramRDD(biGramRDD: RDD[(String, Double)], sentenceRDD: RDD[String], vocabularySet: Set[String],
                            language: String): RDD[(String, String, Double)] = {
    val topBiGram = biGramRDD.take(triGramBaseNumberLimit).toSet
    val topBiGramWords = topBiGram.map(_._1)
    val triGramRDD = NGramUtil.getNGramRDDFilterByPrevious(sentenceRDD, separator, vocabularySet, topBiGramWords, 3, language)
      .map(info => (info._1, info._2.toDouble))
    NGramUtil.getTriGramScoreRDD(topBiGram, triGramRDD, separator, triGramNumberLimit)
  }

  private def outputResults(uniGramRDD: RDD[(String, Double)], biGramRDD: RDD[(String, String, Double)], triGramRDD: RDD[(String, String, Double)],
                            separator: String, outputPath: String, jobName: String, source: String): Unit = {

    uniGramRDD.map(rdd => rdd._1 + separator + rdd._2).saveAsTextFile(intermediateNGramPath + jobName + "/" + source + "UniGram")
    biGramRDD.map(rdd => rdd._1 + separator + rdd._2 + separator + rdd._3).saveAsTextFile(intermediateNGramPath + jobName + "/" + source + "BiGram")
    NGramUtil.writeUniGramFile(uniGramRDD.map(x => (x._1, Math.round(x._2))), separator, outputPath + "unigram." + source + ".txt", uniGramNumberLimit)
    val finalBiGramScore = NGramUtil.mergeBiGramScore(biGramRDD, biGramNumberLimit)
    NGramUtil.writeBiGramFile(finalBiGramScore, separator, outputPath + "bigram." + source + ".txt")
    NGramUtil.writeTriGramFile(triGramRDD, separator, outputPath + "trigram." + source + ".txt")

  }

  private def getTestCorpus(sentenceRDD: RDD[String], filter: collection.immutable.Set[String], language: String, num: Int): Array[String] = {
    sentenceRDD
      .map(sentence => {
        val words = CorpusUtil.tokenizeSentence(sentence, language)
        var b = true
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
        b = b && longWordNumber >= 1
        b = b && (includeWordsNumber * 5 > words.length * 4)
        if (b) {
          words.mkString(CharacterUtil.CONSTANT_SPACE32)
        } else {
          CharacterUtil.CONSTANT_EMPTY
        }
      })
      .filter(_.nonEmpty)
      .take(num * 5)
      .distinct
      .take(num)
  }

}
