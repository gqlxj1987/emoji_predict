package com.kikatech.engine.ngram.task

import com.kikatech.engine.ngram.analyzer.OnlineCorpusRawSentenceGetAnalyzer
import com.kikatech.engine.ngram.util.SparkUtil

/**
  * Created by huminghe on 2017/9/6.
  */
object OnlineCorpusRawSentenceGetTask extends AnalyzeTask {

  def main(args: Array[String]) {

    if (args.length != 4) {
      logError("Usage: <job-name> " +
        "<corpus-path> " +
        "<output-path> " +
        "<partition-num>")
      System.exit(1)
    }

    val jobName: String = args(0)
    val sparkContext = SparkUtil.initSparkContext(this.getClass, jobName)
    val analyzer = new OnlineCorpusRawSentenceGetAnalyzer
    analyzer.doAnalyze(sparkContext, args)

  }

}
