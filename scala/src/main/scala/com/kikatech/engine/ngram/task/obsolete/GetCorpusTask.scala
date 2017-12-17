package com.kikatech.engine.ngram.task.obsolete

import com.kikatech.engine.ngram.analyzer.obsolete.GetCorpusAnalyzer
import com.kikatech.engine.ngram.task.AnalyzeTask
import com.kikatech.engine.ngram.util.SparkUtil

/**
  * Created by huminghe on 2017/7/10.
  */
object GetCorpusTask extends AnalyzeTask {

  def main(args: Array[String]) {

    if (args.length != 6) {
      logError("Usage: <job-name> " +
        "<corpus-path> " +
        "<vocabulary-path> " +
        "<sentence-number> " +
        "<output-path> " +
        "<partition-num>")
      System.exit(1)
    }

    val jobName: String = args(0)
    val sparkContext = SparkUtil.initSparkContext(this.getClass, jobName)
    val analyzer = new GetCorpusAnalyzer
    analyzer.doAnalyze(sparkContext, args)

  }

}
