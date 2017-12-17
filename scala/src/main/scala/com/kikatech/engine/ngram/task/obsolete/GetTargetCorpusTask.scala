package com.kikatech.engine.ngram.task.obsolete

import com.kikatech.engine.ngram.analyzer.obsolete.GetTargetCorpusAnalyzer
import com.kikatech.engine.ngram.task.AnalyzeTask
import com.kikatech.engine.ngram.util.SparkUtil

/**
  * Created by huminghe on 2017/7/13.
  */
object GetTargetCorpusTask extends AnalyzeTask {

  def main(args: Array[String]) {

    if (args.length != 7) {
      logError("Usage: <job-name> " +
        "<corpus-path> " +
        "<include-vocabulary-path> " +
        "<exclude-vocabulary-path> " +
        "<output-path> " +
        "<sentence-number> " +
        "<partition-num>")
      System.exit(1)
    }

    val jobName: String = args(0)
    val sparkContext = SparkUtil.initSparkContext(this.getClass, jobName)
    val analyzer = new GetTargetCorpusAnalyzer
    analyzer.doAnalyze(sparkContext, args)

  }

}
