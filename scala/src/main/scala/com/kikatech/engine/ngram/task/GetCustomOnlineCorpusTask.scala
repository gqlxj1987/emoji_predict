package com.kikatech.engine.ngram.task

import com.kikatech.engine.ngram.analyzer.GetCustomOnlineCorpusAnalyzer
import com.kikatech.engine.ngram.util.SparkUtil

/**
  * Created by huminghe on 2017/8/31.
  */
object GetCustomOnlineCorpusTask extends AnalyzeTask {

  def main(args: Array[String]) {

    if (args.length != 10) {
      logError("Usage: <job-name> " +
        "<corpus-path> " +
        "<include-vocabulary-path> " +
        "<exclude-vocabulary-path> " +
        "<language> " +
        "<threshold> " +
        "<output-path> " +
        "<sentence-number> " +
        "<remove-punctuation>" +
        "<partition-num>")
      System.exit(1)
    }

    val jobName: String = args(0)
    val sparkContext = SparkUtil.initSparkContext(this.getClass, jobName)
    val analyzer = new GetCustomOnlineCorpusAnalyzer
    analyzer.doAnalyze(sparkContext, args)

  }

}
