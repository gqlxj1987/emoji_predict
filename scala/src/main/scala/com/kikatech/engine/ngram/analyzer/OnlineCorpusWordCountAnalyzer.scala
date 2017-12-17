package com.kikatech.engine.ngram.analyzer

import java.io.FileWriter

import com.kikatech.engine.ngram.util.{CharacterUtil, CorpusUtil}
import com.vdurmont.emoji.EmojiParser
import org.apache.spark.SparkContext

/**
  * Created by huminghe on 2017/9/1.
  */
class OnlineCorpusWordCountAnalyzer extends Analyzer {

  override def doAnalyze(sparkContext: SparkContext, paramArray: Array[String]): Unit = {

    if (paramArray.length != 5) {
      sparkContext.stop()
    }

    val jobName = paramArray(0)
    val corpusPath = paramArray(1)
    val language = paramArray(2)
    val outputPath = paramArray(3)
    val partitionNum = paramArray(4).toInt

    logInfo("job name: " + jobName)
    logInfo("corpus path: " + corpusPath)
    logInfo("language: " + language)
    logInfo("output path: " + outputPath)
    logInfo("partition num: " + partitionNum)

    val wordCountRDD = if (corpusPath.indexOf(CharacterUtil.CONSTANT_NONE) <= -1) {
      sparkContext.textFile(corpusPath, partitionNum)
        .map(_.split(CharacterUtil.CONSTANT_TAB))
        .filter(_.nonEmpty)
        .filter(_.length > 1)
        .map(array => EmojiParser.removeAllEmojis(array(1)))
        .flatMap(line => {
          val sentence = line.replaceAll(CharacterUtil.CONSTANT_ONLINE_PUNCTUATION, CharacterUtil.CONSTANT_SPACE32)
          CorpusUtil.tokenizeSentence(sentence, language)
        })
        .filter(_.nonEmpty)
        .map(word => {
          (word, 1)
        })
        .reduceByKey((a, b) => a + b)
        .sortBy(a => a._2, ascending = false)
    } else {
      sparkContext.emptyRDD[(String, Int)]
    }

    val output = wordCountRDD.map(p => p._1 + CharacterUtil.CONSTANT_TAB + p._2.toString).collect()

    val out = new FileWriter(outputPath, false)

    output
      .foreach(w => {
        out.write(w)
        out.write(CharacterUtil.CONSTANT_NEWLINE)
      })
    out.close()

  }

}
