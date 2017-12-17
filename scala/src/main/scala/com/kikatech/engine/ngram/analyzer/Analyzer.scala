package com.kikatech.engine.ngram.analyzer

import org.apache.spark.{Logging, SparkContext}

/**
  * Created by huminghe on 2017/6/1.
  */
trait Analyzer extends Serializable with Logging {

  def doAnalyze(sparkContext: SparkContext, paramArray: Array[String]): Unit

}
