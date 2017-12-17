package com.kikatech.engine.ngram.task.statistics

import com.kikatech.engine.ngram.analyzer.statistics.SentenceCountAnalyzer
import com.kikatech.engine.ngram.task.AnalyzeTask
import com.kikatech.engine.ngram.util.SparkUtil

/**
  * Created by huminghe on 2017/8/18.
  */
object SentenceCountTask extends AnalyzeTask {

  def main(args: Array[String]) {

    if (args.length != 6) {
      logError("Usage: <job-name> " +
        "<corpus-path> " +
        "<include-vocabulary-path> " +
        "<exclude-vocabulary-path> " +
        "<output-path> " +
        "<partition-num>")
      System.exit(1)
    }

    val jobName: String = args(0)
    val sparkContext = SparkUtil.initSparkContext(this.getClass, jobName)
    val analyzer = new SentenceCountAnalyzer
    analyzer.doAnalyze(sparkContext, args)

  }

}
