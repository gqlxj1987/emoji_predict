package com.kikatech.engine.ngram.task

import com.kikatech.engine.ngram.analyzer.RawWordCountAnalyzer
import com.kikatech.engine.ngram.util.SparkUtil

/**
  * Created by huminghe on 2017/6/5.
  */
object RawWordCountTask extends AnalyzeTask {

  def main(args: Array[String]) {

    if (args.length != 6) {
      logError("Usage: <job-name> " +
        "<input-path> " +
        "<output-path> " +
        "<nation-code> " +
        "<language> " +
        "<partition-num>")
      System.exit(1)
    }

    val jobName: String = args(0)
    val sparkContext = SparkUtil.initSparkContext(this.getClass, jobName)
    val analyzer = new RawWordCountAnalyzer
    analyzer.doAnalyze(sparkContext, args)

  }

}
