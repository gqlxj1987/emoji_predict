package com.kikatech.engine.ngram.task

import com.kikatech.engine.ngram.analyzer.NGramIntegratedAnalyzer
import com.kikatech.engine.ngram.util.SparkUtil

/**
  * Created by huminghe on 2017/9/7.
  */
object NGramIntegratedTask extends AnalyzeTask {

  def main(args: Array[String]) {

    if (args.length != 16) {
      logError("Usage: <job-name> " +
        "<dict-1-unigram-path> " +
        "<dict-2-unigram-path> " +
        "<dict-3-unigram-path> " +
        "<origin-unigram-path> " +
        "<dict-1-bigram-path> " +
        "<dict-2-bigram-path> " +
        "<dict-3-bigram-path> " +
        "<origin-bigram-path> " +
        "<online-corpus-path> " +
        "<twitter-corpus-path> " +
        "<wiki-corpus-path> " +
        "<crawler-corpus-path> " +
        "<language> " +
        "<output-path> " +
        "<partition-num>")
      System.exit(1)
    }

    val jobName: String = args(0)
    val sparkContext = SparkUtil.initSparkContext(this.getClass, jobName)
    val analyzer = new NGramIntegratedAnalyzer
    analyzer.doAnalyze(sparkContext, args)

  }

}
