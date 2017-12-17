package com.kikatech.engine.ngram.analyzer

import java.io.FileWriter

import com.kikatech.engine.ngram.util.CharacterUtil
import org.apache.spark.SparkContext

/**
  * Created by huminghe on 2017/9/6.
  */
class OnlineCorpusRawSentenceGetAnalyzer extends Analyzer {

  override def doAnalyze(sparkContext: SparkContext, paramArray: Array[String]): Unit = {

    if (paramArray.length != 4) {
      sparkContext.stop()
    }

    val jobName = paramArray(0)
    val corpusPath = paramArray(1)
    val outputPath = paramArray(2)
    val partitionNum = paramArray(3).toInt

    logInfo("job name: " + jobName)
    logInfo("corpus path: " + corpusPath)
    logInfo("output path: " + outputPath)
    logInfo("partition num: " + partitionNum)

    val sentenceRDD = if (corpusPath.indexOf(CharacterUtil.CONSTANT_NONE) <= -1) {
      sparkContext.textFile(corpusPath, partitionNum)
        .map(_.split(CharacterUtil.CONSTANT_TAB))
        .filter(_.nonEmpty)
        .filter(_.length > 1)
        .map(array => array(1))
    } else {
      sparkContext.emptyRDD[String]
    }

    val outputArray = sentenceRDD.collect()

    val output = new FileWriter(outputPath, false)
    outputArray.foreach(w => {
      output.write(w)
      output.write(CharacterUtil.CONSTANT_NEWLINE)
    })
    output.close()
  }

}
