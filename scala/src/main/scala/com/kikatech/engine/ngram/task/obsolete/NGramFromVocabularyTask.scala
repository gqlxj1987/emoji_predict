package com.kikatech.engine.ngram.task.obsolete

import com.kikatech.engine.ngram.analyzer.obsolete.NGramFromVocabularyAnalyzer
import com.kikatech.engine.ngram.task.AnalyzeTask
import com.kikatech.engine.ngram.util.SparkUtil

/**
  * Created by huminghe on 2017/7/10.
  */
object NGramFromVocabularyTask extends AnalyzeTask {

  def main(args: Array[String]) {

    if (args.length != 7) {
      logError("Usage: <job-name> " +
        "<vocabulary-path> " +
        "<online-corpus-path> " +
        "<twitter-corpus-path> " +
        "<unigram-output-path> " +
        "<bigram-output-path> " +
        "<partition-num>")
      System.exit(1)
    }

    val jobName: String = args(0)
    val sparkContext = SparkUtil.initSparkContext(this.getClass, jobName)
    val analyzer = new NGramFromVocabularyAnalyzer
    analyzer.doAnalyze(sparkContext, args)

  }

}
