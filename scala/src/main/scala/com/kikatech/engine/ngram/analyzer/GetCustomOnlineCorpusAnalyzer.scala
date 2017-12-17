package com.kikatech.engine.ngram.analyzer

import java.io.FileWriter

import com.kikatech.engine.ngram.tokenizer.ThaiTokenizer
import com.kikatech.engine.ngram.util.{CharacterUtil, CorpusUtil, DictUtil}
import com.vdurmont.emoji.EmojiParser
import org.apache.commons.lang.math.RandomUtils
import org.apache.spark.SparkContext

/**
  * Created by huminghe on 2017/8/31.
  */
class GetCustomOnlineCorpusAnalyzer extends Analyzer {

  override def doAnalyze(sparkContext: SparkContext, paramArray: Array[String]): Unit = {

    // TODO: 计算中一些参数通过更自由的方式设置

    if (paramArray.length != 10) {
      sparkContext.stop()
    }

    val jobName = paramArray(0)
    val corpusPath = paramArray(1)
    val includeVocabularyPath = paramArray(2)
    val excludeVocabularyPath = paramArray(3)
    val language = paramArray(4)
    val threshold = paramArray(5).toDouble
    val outputPath = paramArray(6)
    val sentenceNumber = paramArray(7)
    val removePunctuation = paramArray(8)
    val partitionNum = paramArray(9).toInt

    logInfo("job name: " + jobName)
    logInfo("corpus path: " + corpusPath)
    logInfo("include vocabulary path: " + includeVocabularyPath)
    logInfo("exclude vocabulary path: " + excludeVocabularyPath)
    logInfo("language: " + language)
    logInfo("threshold: " + threshold)
    logInfo("output path: " + outputPath)
    logInfo("sentence number: " + sentenceNumber)
    logInfo("remove punctuation: " + removePunctuation)
    logInfo("partition num: " + partitionNum)

    val includeVocabularySet = DictUtil.getVocabularySetLowerCase(sparkContext, includeVocabularyPath)

    val excludeVocabularySet = DictUtil.getVocabularySetLowerCase(sparkContext, excludeVocabularyPath)

    var sentenceArray = if (corpusPath.indexOf(CharacterUtil.CONSTANT_NONE) <= -1) {
      sparkContext.textFile(corpusPath, partitionNum)
        .map(_.split(CharacterUtil.CONSTANT_TAB))
        .filter(_.nonEmpty)
        .filter(_.length > 1)
        .map(array => {
          val sentence = EmojiParser.removeAllEmojis(array(1))
          if (removePunctuation.equals("true")) {
            sentence.replaceAll(CharacterUtil.CONSTANT_ONLINE_PUNCTUATION, CharacterUtil.CONSTANT_SPACE32)
          } else {
            sentence
          }
        })
        .filter(line => {

          val sentence = line.toLowerCase.replaceAll(CharacterUtil.CONSTANT_ONLINE_PUNCTUATION, CharacterUtil.CONSTANT_SPACE32)

          val wordList = CorpusUtil.tokenizeSentence(sentence, language)

          var b = true
          var includeWordsNumber = 0
          var excludeWordsNumber = 0
          var longWordNumber = 0
          for (word <- wordList) {
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
          b = b && (longWordNumber >= 1)
          b = b && (includeWordsNumber >= excludeWordsNumber * 2)
          b = b && (includeWordsNumber > wordList.length * threshold)
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

    if (language.equals("th")) {
      sentenceArray = sentenceArray.map(sentence => {
        val tokenizer = new ThaiTokenizer
        val words = tokenizer.tokenize(sentence).filter(_.nonEmpty)
        words.mkString(CharacterUtil.CONSTANT_SPACE32)
      })
    }

    val output = new FileWriter(outputPath, false)
    sentenceArray.foreach(w => {
      output.write(w)
      output.write(CharacterUtil.CONSTANT_NEWLINE)
    })
    output.close()
  }

}
