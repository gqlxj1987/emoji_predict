package com.kikatech.engine.ngram.task.emoji

import com.kikatech.engine.ngram.analyzer.emoji.EmojiDataAnalyzer
import com.kikatech.engine.ngram.task.AnalyzeTask
import com.kikatech.engine.ngram.util.SparkUtil

/**
  * Created by huminghe on 2017/12/6.
  */
object EmojiDataTask extends AnalyzeTask {

  def main(args: Array[String]) {

    if (args.length != 5) {
      logError("Usage: <job-name> " +
        "<vocabulary-path> " +
        "<corpus-path> " +
        "<output-path> " +
        "<partition-num>")
      System.exit(1)
    }

    val jobName: String = args(0)
    val sparkContext = SparkUtil.initSparkContext(this.getClass, jobName)
    val analyzer = new EmojiDataAnalyzer
    analyzer.doAnalyze(sparkContext, args)

  }

}
