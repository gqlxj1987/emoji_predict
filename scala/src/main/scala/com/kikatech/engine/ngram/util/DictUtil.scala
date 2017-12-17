package com.kikatech.engine.ngram.util

import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD

import scala.collection.immutable.HashSet
import scala.collection.mutable.ArrayBuffer

/**
  * Created by huminghe on 2017/8/23.
  */
object DictUtil {

  private val biGramFromDictThreshold = 30

  def getVocabularySet(sparkContext: SparkContext, vocabularyPath: String): collection.immutable.Set[String] = {
    if (vocabularyPath.indexOf("none") <= -1) {
      sparkContext.textFile(vocabularyPath)
        .map(line => line.split(CharacterUtil.CONSTANT_TAB))
        .filter(_.nonEmpty)
        .map(array => array(0).trim)
        .filter(_.nonEmpty)
        .collect()
        .toSet
    } else {
      new HashSet[String]
    }
  }

  def getVocabularySetLowerCase(sparkContext: SparkContext, vocabularyPath: String): collection.immutable.Set[String] = {
    if (vocabularyPath.indexOf("none") <= -1) {
      sparkContext.textFile(vocabularyPath)
        .map(line => line.split(CharacterUtil.CONSTANT_TAB))
        .filter(_.nonEmpty)
        .map(array => array(0).trim)
        .filter(_.nonEmpty)
        .map(_.toLowerCase)
        .collect()
        .toSet
    } else {
      new HashSet[String]
    }
  }

  def addScoreToVocabularyWeightMap(vocabularyWeightMap: collection.mutable.Map[String, Double],
                                    vocabularySet: collection.immutable.Set[String],
                                    weight: Double = 1): Unit = {
    vocabularySet.foreach(word => {
      vocabularyWeightMap.put(word, vocabularyWeightMap.getOrElse(word, 1.0) + weight)
    })
  }

  def getBiGramRDD(sparkContext: SparkContext, dictPath: String, weight: Double = 1): RDD[(String, String, Double)] = {

    if (dictPath.indexOf(CharacterUtil.CONSTANT_NONE) <= -1) {

      // 将一些重复的默认推荐词去掉
      val filterSet = sparkContext.textFile(dictPath)
        .map(line => {
          val array = line.split(CharacterUtil.CONSTANT_TAB)
          if (array.nonEmpty && array.length >= 2) {
            array(1).trim
          } else {
            CharacterUtil.CONSTANT_EMPTY
          }
        })
        .filter(_.nonEmpty)
        .map(s => (s, 1))
        .countByKey()
        .filter(_._2 > biGramFromDictThreshold)
        .keys
        .toSet

      sparkContext.textFile(dictPath)
        .flatMap(line => {
          val BiGramScoreArray = ArrayBuffer.empty[(String, String, Double)]
          val array = line.split(CharacterUtil.CONSTANT_TAB)
          if (array.nonEmpty && array.length >= 2) {
            val firstWord = array(0).trim
            val suggest = array(1).trim
            if (!filterSet.contains(suggest)) {
              val secondWordArray = suggest
                .split(CharacterUtil.CONSTANT_BI_GRAM_WORD_SPLIT)
                .map(_.trim)
                .filter(_.nonEmpty)

              var score = 10.0
              for (idx <- secondWordArray.indices
                   if idx < 10) {
                BiGramScoreArray += {
                  (firstWord, secondWordArray(idx), score * weight)
                }
                score = score * 0.7
              }
            }
          }
          BiGramScoreArray
        })
    } else {
      sparkContext.emptyRDD[(String, String, Double)]
    }
  }

  def getOriginalBiGramRDD(sparkContext: SparkContext, dictPath: String, weight: Double = 1, separator: String): RDD[(String, String, Double)] = {
    if (dictPath.indexOf(CharacterUtil.CONSTANT_NONE) <= -1) {
      sparkContext.textFile(dictPath)
        .map(line => {
          val array = line.split(separator)
          if (array.length >= 3) {
            (array(0), array(1), array(2).toDouble * weight)
          } else {
            null
          }
        })
        .filter(_ != null)
    } else {
      sparkContext.emptyRDD[(String, String, Double)]
    }
  }

}
