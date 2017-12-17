package com.kikatech.engine.ngram.analyzer.statistics

import java.io.FileWriter

import com.kikatech.engine.ngram.analyzer.Analyzer
import com.kikatech.engine.ngram.util.{CharacterUtil, CorpusUtil, DictUtil}
import com.vdurmont.emoji.EmojiParser
import org.apache.spark.SparkContext

import scala.collection.mutable

/**
  * Created by huminghe on 2017/8/18.
  */
class SentenceCountAnalyzer extends Analyzer {

  override def doAnalyze(sparkContext: SparkContext, paramArray: Array[String]): Unit = {

    val threshold = 0.75

    if (paramArray.length != 6) {
      sparkContext.stop()
    }

    val jobName = paramArray(0)
    val corpusPath = paramArray(1)
    val includeVocabularyPath = paramArray(2)
    val excludeVocabularyPath = paramArray(3)
    val outputPath = paramArray(4)
    val partitionNum = paramArray(5).toInt

    logInfo("job name: " + jobName)
    logInfo("corpus path: " + corpusPath)
    logInfo("include vocabulary path: " + includeVocabularyPath)
    logInfo("exclude vocabulary path: " + excludeVocabularyPath)
    logInfo("output path: " + outputPath)
    logInfo("partition num: " + partitionNum)

    val includeVocabularySet = DictUtil.getVocabularySetLowerCase(sparkContext, includeVocabularyPath)

    val excludeVocabularySet = DictUtil.getVocabularySetLowerCase(sparkContext, excludeVocabularyPath)

    val sentenceCategoryCount = if (corpusPath.indexOf(CharacterUtil.CONSTANT_NONE) <= -1) {
      sparkContext.textFile(corpusPath, partitionNum)
        .map(_.split(CharacterUtil.CONSTANT_TAB))
        .filter(_.nonEmpty)
        .filter(_.length > 1)
        .map(array => EmojiParser.removeAllEmojis(array(1)))
        .map(line => {
          val sentence = line.toLowerCase
          val words = sentence
            .replaceAll(CharacterUtil.CONSTANT_ONLINE_PUNCTUATION, CharacterUtil.CONSTANT_SPACE32)
            .split(CharacterUtil.CONSTANT_SPACE32)
            .filter(_.nonEmpty)
          val wordCount = words.length
          var includeWordsNumber = 0
          var excludeWordsNumber = 0
          for (word <- words) {
            if (includeVocabularySet.contains(word)) {
              includeWordsNumber += 1
            } else if (excludeVocabularySet.contains(word)) {
              excludeWordsNumber += 1
            }
          }
          if (wordCount < 2) {
            ("none", 1)
          } else if (includeWordsNumber > wordCount * threshold) {
            ("include", 1)
          } else if (excludeWordsNumber > wordCount * threshold) {
            ("exclude", 1)
          } else {
            ("none", 1)
          }
        })
        .countByKey()
    } else {
      new mutable.HashMap[String, Long]()
    }

    val out = new FileWriter(outputPath, false)
    sentenceCategoryCount.foreach(w => {
      out.write(w._1 + CharacterUtil.CONSTANT_TAB + w._2)
      out.write(CharacterUtil.CONSTANT_NEWLINE)
    })
    out.close()

  }

}
