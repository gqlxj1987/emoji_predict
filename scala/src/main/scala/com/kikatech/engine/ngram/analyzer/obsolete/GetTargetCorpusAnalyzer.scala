package com.kikatech.engine.ngram.analyzer.obsolete

import java.io.FileWriter

import com.kikatech.engine.ngram.analyzer.Analyzer
import com.kikatech.engine.ngram.util.{CharacterUtil, CorpusUtil, DictUtil}
import com.vdurmont.emoji.EmojiParser
import org.apache.commons.lang.math.RandomUtils
import org.apache.spark.SparkContext

/**
  * Created by huminghe on 2017/7/13.
  */
class GetTargetCorpusAnalyzer extends Analyzer {

  override def doAnalyze(sparkContext: SparkContext, paramArray: Array[String]): Unit = {

    if (paramArray.length != 7) {
      sparkContext.stop()
    }

    val jobName = paramArray(0)
    val corpusPath = paramArray(1)
    val includeVocabularyPath = paramArray(2)
    val excludeVocabularyPath = paramArray(3)
    val outputPath = paramArray(4)
    val sentenceNumber = paramArray(5)
    val partitionNum = paramArray(6).toInt

    logInfo("job name: " + jobName)
    logInfo("corpus path: " + corpusPath)
    logInfo("include vocabulary path: " + includeVocabularyPath)
    logInfo("exclude vocabulary path: " + excludeVocabularyPath)
    logInfo("sentence number: " + sentenceNumber)
    logInfo("output path: " + outputPath)
    logInfo("partition num: " + partitionNum)

    val includeVocabularySet = DictUtil.getVocabularySetLowerCase(sparkContext, includeVocabularyPath)

    val excludeVocabularySet = DictUtil.getVocabularySetLowerCase(sparkContext, excludeVocabularyPath)

    val sentenceArray = if (corpusPath.indexOf(CharacterUtil.CONSTANT_NONE) <= -1) {
      sparkContext.textFile(corpusPath, partitionNum)
        .map(_.split(CharacterUtil.CONSTANT_TAB))
        .filter(_.nonEmpty)
        .filter(_.length > 1)
        .map(array => EmojiParser.removeAllEmojis(array(1)))
        .filter(line => {
          val sentence = line.toLowerCase
          val words = sentence
            .replaceAll(CharacterUtil.CONSTANT_ONLINE_PUNCTUATION, CharacterUtil.CONSTANT_SPACE32)
            .split(CharacterUtil.CONSTANT_SPACE32)
            .filter(_.nonEmpty)
          var b = true
          b = b && words.length > 2
          var includeWordsNumber = 0
          var excludeWordsNumber = 0
          var longWordNumber = 0
          for (word <- words) {
            if (includeVocabularySet.contains(word)) {
              includeWordsNumber += 1
              if (word.length > 2) {
                longWordNumber += 1
              }
            }
            if (excludeVocabularySet.contains(word)) {
              excludeWordsNumber += 1
            }
          }
          b = b && (longWordNumber >= 3)
          b = b && (includeWordsNumber >= excludeWordsNumber * 2)
          b = b && (includeWordsNumber * 8 > words.length * 7)
          b
        })
        .distinct()
        .map(x => (x, RandomUtils.nextDouble()))
        .sortBy(x => x._2, ascending = false)
        .map(_._1)
        .take(sentenceNumber.toInt)
    } else {
      new Array[String](10)
    }

    val output = new FileWriter(outputPath, false)
    sentenceArray.foreach(w => {
      output.write(w)
      output.write(CharacterUtil.CONSTANT_NEWLINE)
    })
    output.close()
  }

}
