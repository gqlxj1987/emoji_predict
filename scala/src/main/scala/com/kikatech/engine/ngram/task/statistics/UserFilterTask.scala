package com.kikatech.engine.ngram.task.statistics

import com.kikatech.engine.ngram.analyzer.statistics.UserFilterAnalyzer
import com.kikatech.engine.ngram.task.AnalyzeTask
import com.kikatech.engine.ngram.util.SparkUtil

/**
  * Created by huminghe on 2017/7/28.
  */
object UserFilterTask extends AnalyzeTask {

  def main(args: Array[String]) {

    if (args.length != 5) {
      logError("Usage: <job-name> " +
        "<corpus-path> " +
        "<vocabulary-path> " +
        "<output-path> " +
        "<partition-num>")
      System.exit(1)
    }

    val jobName: String = args(0)
    val sparkContext = SparkUtil.initSparkContext(this.getClass, jobName)
    val analyzer = new UserFilterAnalyzer
    analyzer.doAnalyze(sparkContext, args)

  }

}
