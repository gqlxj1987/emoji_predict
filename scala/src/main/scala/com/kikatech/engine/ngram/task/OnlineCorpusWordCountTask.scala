package com.kikatech.engine.ngram.task

import com.kikatech.engine.ngram.analyzer.OnlineCorpusWordCountAnalyzer
import com.kikatech.engine.ngram.util.SparkUtil

/**
  * Created by huminghe on 2017/9/1.
  */
object OnlineCorpusWordCountTask extends AnalyzeTask {

  def main(args: Array[String]) {

    if (args.length != 5) {
      logError("Usage: <job-name> " +
        "<input-path> " +
        "<language> " +
        "<output-path> " +
        "<partition-num>")
      System.exit(1)
    }

    val jobName: String = args(0)
    val sparkContext = SparkUtil.initSparkContext(this.getClass, jobName)
    val analyzer = new OnlineCorpusWordCountAnalyzer
    analyzer.doAnalyze(sparkContext, args)

  }

}