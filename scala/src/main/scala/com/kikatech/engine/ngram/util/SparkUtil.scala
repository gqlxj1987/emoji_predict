package com.kikatech.engine.ngram.util

import org.apache.commons.lang.{ClassUtils, StringUtils}
import org.apache.hadoop.fs.Path
import org.apache.spark.{SparkConf, SparkContext}

/**
  * Created by lipeng on 1/4/16.
  */
object SparkUtil {

  def initSparkContext(runClass: Class[_], name: String): SparkContext = {
    val className = ClassUtils.getShortClassName(runClass)
    val jobName = if (StringUtils.isNotEmpty(name)) {
      className + "[" + name + "]"
    } else {
      className
    }
    val sparkConf = new SparkConf().setAppName(jobName)
    val sparkContext = new SparkContext(sparkConf)
    S3Util.settingS3NProperties(sparkContext.hadoopConfiguration)
    sparkContext
  }

  def initSparkContext(sparkConf: SparkConf): SparkContext = {
    val sparkContext: SparkContext = new SparkContext(sparkConf)
    S3Util.settingS3NProperties(sparkContext.hadoopConfiguration)
    sparkContext
  }

  def deletePath(sparkContext: SparkContext, pathString: String): Boolean = {
    val hadoopConf = sparkContext.hadoopConfiguration
    val hdfs = org.apache.hadoop.fs.FileSystem.get(hadoopConf)
    val path = new Path(pathString)
    if (hdfs.exists(path)) {
      hdfs.delete(path, true)
    } else {
      false
    }
  }

  def main(args: Array[String]) {
    initSparkContext(this.getClass, "")
  }
}
