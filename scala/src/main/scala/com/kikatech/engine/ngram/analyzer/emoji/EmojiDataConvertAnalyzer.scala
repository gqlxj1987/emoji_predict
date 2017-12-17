package com.kikatech.engine.ngram.analyzer.emoji

import java.io.FileWriter

import com.kikatech.engine.ngram.analyzer.Analyzer
import com.kikatech.engine.ngram.util.CharacterUtil
import org.apache.spark.SparkContext

import scala.collection.mutable.ArrayBuffer

/**
  * Created by huminghe on 2017/12/8.
  */
class EmojiDataConvertAnalyzer extends Analyzer {

  override def doAnalyze(sparkContext: SparkContext, paramArray: Array[String]): Unit = {

    if (paramArray.length != 5) {
      sparkContext.stop()
    }

    val jobName = paramArray(0)
    val vocabularyPath = paramArray(1)
    val inputPath = paramArray(2)
    val outputPath = paramArray(3)
    val partitionNum = paramArray(4).toInt

    logInfo("job name: " + jobName)
    logInfo("vocabulary path: " + vocabularyPath)
    logInfo("input path: " + inputPath)
    logInfo("output path: " + outputPath)
    logInfo("partition num: " + partitionNum)

    val vocabularyMap = sparkContext.textFile(vocabularyPath)
      .map(line => line.split(CharacterUtil.CONSTANT_TAB))
      .filter(_.nonEmpty)
      .map(array => (array(0), array(1).toInt))
      .collect()
      .toMap


    val sentenceRDD = sparkContext.textFile(inputPath, partitionNum)
      .map(sentence => {
        val wordArray = sentence.split(" ").filter(_.nonEmpty)
        val resultArray = ArrayBuffer.empty[Int]
        resultArray += 0
        for (word <- wordArray) {
          resultArray += vocabularyMap.getOrElse(word, 1)
        }
        resultArray.mkString(" ")
      })


    val out = new FileWriter(outputPath, false)
    sentenceRDD
      .collect()
      .foreach(w => {
        out.write(w)
        out.write(CharacterUtil.CONSTANT_NEWLINE)
      })
    out.close()
  }

}
