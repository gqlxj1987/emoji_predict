package com.kikatech.engine.ngram.util

import java.io.FileWriter

import org.apache.spark.rdd.RDD

import scala.collection.mutable.ArrayBuffer

/**
  * Created by huminghe on 2017/7/5.
  */
object NGramUtil {

  def getNGramRDD(sentenceRDD: RDD[String],
                  separator: String,
                  vocabularySet: Set[String],
                  gramNum: Int,
                  language: String = CharacterUtil.CONSTANT_NONE,
                  threshold: Double = 4): RDD[(String, Int)] = {
    sentenceRDD
      .flatMap(sentence => {
        val wordList = CorpusUtil.tokenizeSentence(sentence, language)

        val nGramCombinationArray = ArrayBuffer.empty[(String, Int)]
        val nGramWordArray = new Array[String](gramNum)
        val nGramBooleanArray = new Array[Boolean](gramNum)
        var inVocabularyNum = 0
        var outVocabularyNum = 0
        for (i <- 0 until gramNum by 1) {
          nGramBooleanArray(i) = false
        }

        for (idx <- wordList.indices) {
          var word = wordList(idx)
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
          for (j <- 0 until gramNum - 1 by 1) {
            nGramWordArray(j) = nGramWordArray(j + 1)
            nGramBooleanArray(j) = nGramBooleanArray(j + 1)
          }
          nGramWordArray(gramNum - 1) = word
          nGramBooleanArray(gramNum - 1) = inVocabulary

          if (inVocabulary) {
            inVocabularyNum += 1
          } else {
            outVocabularyNum += 1
          }

          if (!nGramBooleanArray.contains(false)) {
            val key = nGramWordArray.mkString(separator)
            nGramCombinationArray += {
              (key, 1)
            }
          }
        }
        if (inVocabularyNum >= 2 && inVocabularyNum >= outVocabularyNum * threshold) {
          nGramCombinationArray
        } else {
          ArrayBuffer.empty[(String, Int)]
        }
      })
      .reduceByKey((a, b) => a + b)
      .sortBy(a => a._2, ascending = false)
  }

  def getWeightedNGramRDD(weightedSentenceRDD: RDD[(String, Double)],
                          separator: String,
                          vocabularySet: Set[String],
                          gramNum: Int,
                          language: String = CharacterUtil.CONSTANT_NONE,
                          threshold: Double = 4): RDD[(String, Double)] = {
    weightedSentenceRDD
      .flatMap(weightedSentence => {
        val wordList = CorpusUtil.tokenizeSentence(weightedSentence._1, language)

        val nGramCombinationArray = ArrayBuffer.empty[(String, Double)]
        val nGramWordArray = new Array[String](gramNum)
        val nGramBooleanArray = new Array[Boolean](gramNum)
        var inVocabularyNum = 0
        var outVocabularyNum = 0
        for (i <- 0 until gramNum by 1) {
          nGramBooleanArray(i) = false
        }

        for (idx <- wordList.indices) {
          var word = wordList(idx)
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
          for (j <- 0 until gramNum - 1 by 1) {
            nGramWordArray(j) = nGramWordArray(j + 1)
            nGramBooleanArray(j) = nGramBooleanArray(j + 1)
          }
          nGramWordArray(gramNum - 1) = word
          nGramBooleanArray(gramNum - 1) = inVocabulary

          if (inVocabulary) {
            inVocabularyNum += 1
          } else {
            outVocabularyNum += 1
          }

          if (!nGramBooleanArray.contains(false)) {
            val key = nGramWordArray.mkString(separator)
            nGramCombinationArray += {
              (key, weightedSentence._2)
            }
          }
        }
        if (inVocabularyNum >= 2 && inVocabularyNum >= outVocabularyNum * threshold) {
          nGramCombinationArray
        } else {
          ArrayBuffer.empty[(String, Double)]
        }
      })
      .reduceByKey((a, b) => a + b)
      .sortBy(a => a._2, ascending = false)
  }

  def getNGramRDDFilterByPrevious(sentenceRDD: RDD[String],
                                  separator: String,
                                  vocabularySet: Set[String],
                                  previousGramSet: Set[String],
                                  gramNum: Int,
                                  language: String = CharacterUtil.CONSTANT_NONE,
                                  threshold: Double = 4): RDD[(String, Int)] = {
    sentenceRDD
      .flatMap(sentence => {
        val wordList = CorpusUtil.tokenizeSentence(sentence, language)

        val nGramCombinationArray = ArrayBuffer.empty[(String, Int)]
        val nGramWordArray = new Array[String](gramNum)
        val nGramBooleanArray = new Array[Boolean](gramNum)
        var inVocabularyNum = 0
        var outVocabularyNum = 0
        for (i <- 0 until gramNum by 1) {
          nGramBooleanArray(i) = false
        }

        for (idx <- wordList.indices) {
          var word = wordList(idx)
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
          for (j <- 0 until gramNum - 1 by 1) {
            nGramWordArray(j) = nGramWordArray(j + 1)
            nGramBooleanArray(j) = nGramBooleanArray(j + 1)
          }
          nGramWordArray(gramNum - 1) = word
          nGramBooleanArray(gramNum - 1) = inVocabulary

          if (inVocabulary) {
            inVocabularyNum += 1
          } else {
            outVocabularyNum += 1
          }

          if (!nGramBooleanArray.contains(false)) {
            if (gramNum > 1) {
              val prev = nGramWordArray.take(gramNum - 1)
              val prevGram = prev.mkString(separator)
              if (previousGramSet.contains(prevGram)) {
                val key = nGramWordArray.mkString(separator)
                nGramCombinationArray += {
                  (key, 1)
                }
              }
            }
          }
        }
        if (inVocabularyNum >= 2 && inVocabularyNum >= outVocabularyNum * threshold) {
          nGramCombinationArray
        } else {
          ArrayBuffer.empty[(String, Int)]
        }

      })
      .reduceByKey((a, b) => a + b)
      .sortBy(a => a._2, ascending = false)
  }

  def getWeightedNGramRDDFilterByPrevious(weightedSentenceRDD: RDD[(String, Double)],
                                          separator: String,
                                          vocabularySet: Set[String],
                                          previousGramSet: Set[String],
                                          gramNum: Int,
                                          language: String = CharacterUtil.CONSTANT_NONE,
                                          threshold: Double = 4): RDD[(String, Double)] = {
    weightedSentenceRDD
      .flatMap(weightedSentence => {
        val wordList = CorpusUtil.tokenizeSentence(weightedSentence._1, language)

        val nGramCombinationArray = ArrayBuffer.empty[(String, Double)]
        val nGramWordArray = new Array[String](gramNum)
        val nGramBooleanArray = new Array[Boolean](gramNum)
        var inVocabularyNum = 0
        var outVocabularyNum = 0
        for (i <- 0 until gramNum by 1) {
          nGramBooleanArray(i) = false
        }

        for (idx <- wordList.indices) {
          var word = wordList(idx)
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
          for (j <- 0 until gramNum - 1 by 1) {
            nGramWordArray(j) = nGramWordArray(j + 1)
            nGramBooleanArray(j) = nGramBooleanArray(j + 1)
          }
          nGramWordArray(gramNum - 1) = word
          nGramBooleanArray(gramNum - 1) = inVocabulary

          if (inVocabulary) {
            inVocabularyNum += 1
          } else {
            outVocabularyNum += 1
          }

          if (!nGramBooleanArray.contains(false)) {
            if (gramNum > 1) {
              val prev = nGramWordArray.take(gramNum - 1)
              val prevGram = prev.mkString(separator)
              if (previousGramSet.contains(prevGram)) {
                val key = nGramWordArray.mkString(separator)
                nGramCombinationArray += {
                  (key, weightedSentence._2)
                }
              }
            }
          }
        }
        if (inVocabularyNum >= 2 && inVocabularyNum >= outVocabularyNum * threshold) {
          nGramCombinationArray
        } else {
          ArrayBuffer.empty[(String, Double)]
        }
      })
      .reduceByKey((a, b) => a + b)
      .sortBy(a => a._2, ascending = false)
  }

  def getUniGramScoreRDD(uniGramRDD: RDD[(String, Int)]): RDD[(String, Long)] = {

    val max = uniGramRDD.first()._2

    val logMax = Math.pow(Math.log(max), 2)

    uniGramRDD.filter(_._2 > 1).map(info => (info._1, Math.round(Math.pow(Math.log(info._2), 2) / logMax * 249) + 1))
  }

  def getUniGramScoreRDDOfDouble(uniGramRDD: RDD[(String, Double)]): RDD[(String, Double)] = {

    val max = if (uniGramRDD.isEmpty()) {
      1
    } else {
      uniGramRDD.first()._2
    }

    val logMax = Math.pow(Math.log(max), 2)

    uniGramRDD.filter(_._2 > 3.5).map(info => (info._1, Math.pow(Math.log(info._2), 2) / logMax * 250))
  }

  def getBiGramScoreRDD(uniGramRDD: RDD[(String, Int)],
                        biGramRDD: RDD[(String, Int)],
                        separator: String,
                        biGramLimit: Int): RDD[(String, String, Long)] = {

    val uniGramMap = uniGramRDD.collectAsMap()

    biGramRDD
      .filter(_._2 > 1)
      .map(info => {
        val biGram = info._1.split(separator)
        if (biGram.length >= 2) {
          val total = uniGramMap.get(biGram(0))
          if (total.isDefined) {
            val p: Double = info._2.toDouble / total.get
            (biGram(0), biGram(1), p)
          } else {
            (biGram(0), biGram(1), 0.toDouble)
          }
        } else {
          null
        }
      })
      .filter(_ != null)
      .filter(_._3 > 0.05)
      .groupBy(r => r._1)
      .flatMap((info: (String, Iterable[(String, String, Double)])) => {
        info._2
          .toSeq.sortWith((x, y) => x._3 > y._3)
          .take(biGramLimit)
          .map(it => {
            val score = Math.round(it._3 * 14) + 1
            (it._1, it._2, score)
          })
      })

  }

  def getBiGramScoreRDDOfDouble(uniGramRDD: RDD[(String, Double)],
                                biGramRDD: RDD[(String, Double)],
                                separator: String,
                                weight: Double): RDD[(String, String, Double)] = {

    val uniGramMap = uniGramRDD.collectAsMap()

    biGramRDD
      .filter(_._2 > 3.5)
      .map(info => {
        val biGram = info._1.split(separator)
        if (biGram.length >= 2) {
          val total = uniGramMap.get(biGram(0))
          if (total.isDefined) {
            val p: Double = info._2 / total.get * weight * 15
            (biGram(0), biGram(1), p)
          } else {
            (biGram(0), biGram(1), 0.toDouble)
          }
        } else {
          null
        }
      })
      .filter(_ != null)
      .filter(_._3 > 0.25 * weight)
  }

  def getTriGramScoreRDD(biGramSet: Set[(String, Double)],
                         triGramRDD: RDD[(String, Double)],
                         separator: String,
                         triGramLimit: Int): RDD[(String, String, Double)] = {

    val biGramMap = biGramSet.toMap

    triGramRDD
      .map(info => {
        val triGram = info._1.split(separator)
        if (triGram.length >= 3) {
          val biGram = triGram.take(2).mkString(separator)
          val total = biGramMap.get(biGram)
          if (total.isDefined) {
            val p: Double = info._2 / total.get
            (biGram, triGram(2), p)
          } else {
            (biGram, triGram(2), 0.toDouble)
          }
        } else {
          null
        }
      })
      .filter(_ != null)
      .filter(_._3 > 0)
      .groupBy(r => r._1)
      .flatMap(info => {
        info._2
          .toSeq
          .sortWith((x, y) => x._3 > y._3)
          .take(triGramLimit)
      })
  }

  def mergeBiGramScore(scoreRDD: RDD[(String, String, Double)], biGramNumberLimit: Int): RDD[(String, String, Long)] = {
    scoreRDD
      .map(x => (x._1 + CharacterUtil.CONSTANT_TAB + x._2, x._3))
      .reduceByKey((a, b) => a + b)
      .map(x => {
        val array = x._1.split(CharacterUtil.CONSTANT_TAB)
        if (array.length >= 2) {
          (array(0), array(1), x._2)
        } else {
          null
        }
      })
      .filter(_ != null)
      .groupBy(_._1)
      .flatMap((info: (String, Iterable[(String, String, Double)])) => {
        val biGramScoreArray = ArrayBuffer.empty[(String, String, Long)]
        val seq = info._2
          .toSeq.sortWith((x, y) => x._3 > y._3)
        var score = 15
        for (info <- seq) {
          if (score > 15 - biGramNumberLimit) {
            biGramScoreArray += {
              (info._1, info._2, score)
            }
            score -= 1
          }
        }
        biGramScoreArray
      })
  }

  def writeUniGramFile(uniGramRDD: RDD[(String, Long)], separator: String, outputPath: String, numLimit: Int): Array[String] = {
    val uniGramOutput = uniGramRDD.map(p => {
      p._1 + separator + p._2.toString
    })
      .collect()
      .take(numLimit)

    val uniGramOut = new FileWriter(outputPath, false)
    uniGramOutput.foreach(w => {
      uniGramOut.write(w)
      uniGramOut.write(CharacterUtil.CONSTANT_NEWLINE)
    })
    uniGramOut.close()

    uniGramOutput
  }

  def writeBiGramFile(biGramRDD: RDD[(String, String, Long)], separator: String, outputPath: String): Unit = {
    val biGramOutput = biGramRDD.map(p => {
      p._1 + separator + p._2 + separator + p._3.toString
    })
      .collect()

    val biGramOut = new FileWriter(outputPath, false)
    biGramOutput.foreach(w => {
      biGramOut.write(w)
      biGramOut.write(CharacterUtil.CONSTANT_NEWLINE)
    })
    biGramOut.close()
  }

  def writeTriGramFile(triGramRDD: RDD[(String, String, Double)], separator: String, outputPath: String): Unit = {
    val triGramOutput = triGramRDD.map(p => {
      p._1 + separator + p._2 + separator + p._3.toString
    })
      .collect()

    val triGramOut = new FileWriter(outputPath, false)
    triGramOutput.foreach(w => {
      triGramOut.write(w)
      triGramOut.write(CharacterUtil.CONSTANT_NEWLINE)
    })
    triGramOut.close()
  }

  def writeFile(content: Array[String], outputPath: String): Unit = {
    val output = new FileWriter(outputPath, false)
    content.foreach(w => {
      output.write(w)
      output.write(CharacterUtil.CONSTANT_NEWLINE)
    })
    output.close()
  }

}
