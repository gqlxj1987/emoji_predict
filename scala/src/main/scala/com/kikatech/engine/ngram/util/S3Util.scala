package com.kikatech.engine.ngram.util

import java.net.URI
import java.text.{DateFormat, SimpleDateFormat}

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.s3native.NativeS3FileSystem
import org.apache.hadoop.fs.{FileStatus, Path}
import org.apache.spark.{Logging, SparkContext}

/**
  * Created by lipeng on 1/4/16.
  */
object S3Util extends Logging {
  val PARAM_S3_SCHEMA = "s3://";
  val PARAM_S3N_SCHEMA = "s3n://";
  val A_DAY = 24 * 60 * 60 * 1000

  val DATE_FM: DateFormat = new SimpleDateFormat("yyyyMMdd");
  private val S3_USER_NAME = "lipeng";

  private def getNativeS3FileSystem(): NativeS3FileSystem = {
    return new NativeS3FileSystem();
  }


  def pathExist(sparkContext: SparkContext, path: String): Boolean = {
    val s3fs: NativeS3FileSystem = getNativeS3FileSystem();
    val s3Uri: URI = new URI(path);
    val s3Path: Path = new Path(path);
    val hadoopConf: Configuration = sparkContext.hadoopConfiguration;
    settingS3Properties(hadoopConf);
    s3fs.initialize(s3Uri, hadoopConf);
    try {
      if (s3fs.exists(s3Path)) {
        return true;
      }
    } catch {
      case _: Exception => false;
    } finally {
    }
    return false;
  }

  def removePathOrFile(sparkContext: SparkContext, path: String, isDelete: Boolean): Boolean = {
    val s3fs: NativeS3FileSystem = getNativeS3FileSystem();
    val s3Uri: URI = new URI(path);
    val s3Path: Path = new Path(path);
    val hadoopConf: Configuration = sparkContext.hadoopConfiguration;
    settingS3Properties(hadoopConf);
    s3fs.initialize(s3Uri, hadoopConf);
    try {
      if (s3fs.exists(s3Path)) {
        if (isDelete) {
          return s3fs.delete(s3Path, true);
        } else {
          logInfo("\t\t ##############################################################");
          logInfo("");
          logInfo("");
          logInfo(" Do you really want to delete this file (" + path + ")? [Yes/Y] ?");
          logInfo("");
          logInfo("");
          logInfo("\t\t  ##############################################################");
          val input: String = Console.in.readLine().toUpperCase();
          if ("YES".equals(input) || "Y".equals(input)) {
            return s3fs.delete(s3Path, true);
          } else {
            return false;
          }
        }
      } else {
        logInfo("\t\t ##############################################################");
        logInfo("");
        logInfo("");
        logInfo(" The file doesn't exist. file (" + path + ")");
        logInfo("");
        logInfo("");
        logInfo("\t\t  ##############################################################");
        return true;
      }

    } catch {
      case _: Exception => false;
    } finally {
    }
    return false;
  }

  def subFileArray(sparkContext: SparkContext, path: String): Array[FileStatus] = {
    val s3fs: NativeS3FileSystem = getNativeS3FileSystem();
    val s3Uri: URI = new URI(path);
    val s3Path: Path = new Path(path);
    val hadoopConf: Configuration = sparkContext.hadoopConfiguration;
    settingS3Properties(hadoopConf);
    s3fs.initialize(s3Uri, hadoopConf);
    try {
      if (s3fs.exists(s3Path)) {
        logInfo("\t\t S3 Path existed. [ " + s3Path + " ] ")
        return s3fs.listStatus(s3Path);
      } else {
        logInfo("\t\t S3 Path not existed. [ " + s3Path + " ] ")
      }
    } catch {
      case ex: Exception => {
        ex.printStackTrace()
      }
    } finally {
    }
    return null;
  }

  def subFileCount(sparkContext: SparkContext, path: String): Int = {
    val subFile_arr: Array[FileStatus] = subFileArray(sparkContext, path);
    return if (null == subFile_arr) {
      logInfo("\t\t S3 Path file size = -1 ")
      -1;
    } else {
      logInfo("\t\t S3 Path file size = " + subFile_arr.size)
      subFile_arr.size
    }
  }

  def settingS3Properties(conf: Configuration): Unit = {
    conf.set("fs.s3.impl", "org.apache.hadoop.fs.s3.S3FileSystem");
    conf.set("fs.s3n.awsAccessKeyId", "AKIAIZDSNI3B3C3EO2HQ")
    conf.set("fs.s3n.awsSecretAccessKey", "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")
  }

  def settingS3NProperties(conf: Configuration): Unit = {
    conf.set("fs.s3n.impl", "org.apache.hadoop.fs.s3native.NativeS3FileSystem");
    conf.set("fs.s3n.awsAccessKeyId", "AKIAIZDSNI3B3C3EO2HQ")
    conf.set("fs.s3n.awsSecretAccessKey", "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")
  }

  def settingS3NProperties(sparkContext: SparkContext): Unit = {
    settingS3NProperties(sparkContext.hadoopConfiguration);
  }

  def main(args: Array[String]) {
  }
}
