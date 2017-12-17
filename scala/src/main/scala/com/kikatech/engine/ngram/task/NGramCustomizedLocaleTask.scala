package com.kikatech.engine.ngram.task

import com.kikatech.engine.ngram.analyzer.NGramCustomizedLocaleAnalyzer
import com.kikatech.engine.ngram.util.SparkUtil

/**
  * Created by huminghe on 2017/8/14.
  */
object NGramCustomizedLocaleTask extends AnalyzeTask {

  def main(args: Array[String]) {

    if (args.length != 9) {
      logError("Usage: <job-name> " +
        "<vocabulary-path> " +
        "<nation> " +
        "<corpus1-path> " +
        "<corpus2-path> " +
        "<unigram-output-path> " +
        "<bigram-output-path> " +
        "<save-sentences> " +
        "<partition-num>")
      System.exit(1)
    }

    val jobName: String = args(0)
    val sparkContext = SparkUtil.initSparkContext(this.getClass, jobName)
    val analyzer = new NGramCustomizedLocaleAnalyzer
    analyzer.doAnalyze(sparkContext, args)

  }

}
