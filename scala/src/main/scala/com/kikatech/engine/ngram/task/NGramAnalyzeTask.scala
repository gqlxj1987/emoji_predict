package com.kikatech.engine.ngram.task

import com.kikatech.engine.ngram.analyzer.NGramAnalyzer
import com.kikatech.engine.ngram.util.SparkUtil

/**
  * Created by huminghe on 2017/6/1.
  */
object NGramAnalyzeTask extends AnalyzeTask {

  def main(args: Array[String]) {

    if (args.length != 6) {
      logError("Usage: <job-name> " +
        "<vocabulary-path> " +
        "<corpus-path> " +
        "<unigram-output-path> " +
        "<bigram-output-path> " +
        "<partition-num>")
      System.exit(1)
    }

    val jobName: String = args(0)
    val sparkContext = SparkUtil.initSparkContext(this.getClass, jobName)
    val analyzer = new NGramAnalyzer
    analyzer.doAnalyze(sparkContext, args)

  }

}
