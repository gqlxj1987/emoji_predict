package com.kikatech.engine.ngram.task

import com.kikatech.engine.ngram.analyzer.CalculateOnlineMetricsAnalyzer
import com.kikatech.engine.ngram.util.SparkUtil

/**
  * Created by huminghe on 2017/9/13.
  */
object CalculateOnlineMetricsTask extends AnalyzeTask {

  def main(args: Array[String]) {

    if (args.length < 4) {
      logError("Usage: <job-name> " +
        "<trace-source-path> " +
        "<output-path> " +
        "<partition-num> " +
        "<dv> " +
        "<engine> " +
        "<locale> " +
        "<nation> " +
        "<app-version>")
      System.exit(1)
    }

    val jobName: String = args(0)
    val sparkContext = SparkUtil.initSparkContext(this.getClass, jobName)
    val analyzer = new CalculateOnlineMetricsAnalyzer
    analyzer.doAnalyze(sparkContext, args)

  }

}
