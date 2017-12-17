package com.kikatech.engine.ngram.analyzer.obsolete

import java.io.FileWriter

import com.kikatech.engine.ngram.analyzer.Analyzer
import com.kikatech.engine.ngram.util.{CharacterUtil, DictUtil}
import com.vdurmont.emoji.EmojiParser
import org.apache.commons.lang.math.RandomUtils
import org.apache.spark.SparkContext

/**
  * Created by huminghe on 2017/7/10.
  */
class GetCorpusAnalyzer extends Analyzer {

  override def doAnalyze(sparkContext: SparkContext, paramArray: Array[String]): Unit = {

    if (paramArray.length != 6) {
      sparkContext.stop()
    }

    val jobName = paramArray(0)
    val corpusPath = paramArray(1)
    val vocabularyPath = paramArray(2)
    val sentenceNum = paramArray(3).toInt
    val outputPath = paramArray(4)
    val partitionNum = paramArray(5).toInt

    logInfo("job name: " + jobName)
    logInfo("corpus path: " + corpusPath)
    logInfo("vocabulary path: " + vocabularyPath)
    logInfo("sentence number: " + sentenceNum)
    logInfo("output path: " + outputPath)
    logInfo("partition num: " + partitionNum)

    val vocabularySet = DictUtil.getVocabularySet(sparkContext, vocabularyPath)

    val sentenceRDD = if (corpusPath.indexOf(CharacterUtil.CONSTANT_NONE) <= -1) {
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
          for (word <- words) {
            b = b && vocabularySet.contains(word)
          }
          b
        })
    } else {
      sparkContext.emptyRDD[String]
    }

    val outputRDD = sentenceRDD
      .map(x => (x, RandomUtils.nextDouble()))
      .sortBy(x => x._2, ascending = false)
      .take(sentenceNum)
      .map(_._1)

    val output = new FileWriter(outputPath, false)
    outputRDD.foreach(w => {
      output.write(w)
      output.write(CharacterUtil.CONSTANT_NEWLINE)
    })
    output.close()
  }

}
