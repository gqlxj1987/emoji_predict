package com.kikatech.engine.ngram.task

import com.kikatech.engine.ngram.analyzer.CustomUserCorpusAnalyzer
import com.kikatech.engine.ngram.util.SparkUtil

/**
  * Created by huminghe on 2017/8/16.
  */
object CustomUserCorpusTask extends AnalyzeTask {

  def main(args: Array[String]) {

    if (args.length != 9) {
      logError("Usage: <job-name> " +
        "<corpus-path> " +
        "<vocabulary-path> " +
        "<exclude-vocabulary-path> " +
        "<filter word threshold> " +
        "<filter sentence threshold> " +
        "<nation> " +
        "<output-path> " +
        "<partition-num>")
      System.exit(1)
    }

    val jobName: String = args(0)
    val sparkContext = SparkUtil.initSparkContext(this.getClass, jobName)
    val analyzer = new CustomUserCorpusAnalyzer
    analyzer.doAnalyze(sparkContext, args)

  }

}
