package com.kikatech.engine.ngram.analyzer

import java.io.FileWriter

import com.kikatech.engine.ngram.util.{CharacterUtil, CorpusUtil, TraceUtil}
import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD
import org.slf4j.LoggerFactory

/**
  * Created by huminghe on 2017/9/12.
  */
class CalculateOnlineMetricsAnalyzer extends Analyzer {

  private val LOG = LoggerFactory.getLogger(this.getClass)

  private def calculateMetrics(localeTraceFiltered: RDD[Array[String]], outputPath: String, engine: String) = {
    val localeTrace = localeTraceFiltered.filter(_ (4).contains("choose"))

    val allCount = localeTraceFiltered.count

    val chooseCount = localeTrace.count

    val inputChooseWordData = localeTrace.flatMap(item => {
      TraceUtil.extractInputChooseWordType(item)
    })

    inputChooseWordData.cache()

    val wordCount = inputChooseWordData.count
    val rightData = inputChooseWordData.filter(_.isRightChoose)
    rightData.cache()

    val rightDataCount = rightData.count.toFloat
    val engineWorkCount = rightData.filter(p => !p.keys.equalsIgnoreCase(p.word)).count.toFloat

    val predictWordCount = rightData.filter(p =>
      p.word.startsWith(p.keys) & (!p.keys.equalsIgnoreCase(p.word))).count.toFloat

    val intoCountData = inputChooseWordData.filter(_.takeIntoCount)

    val averageWordLength = intoCountData.map(_.word.length).sum.toFloat / wordCount.toFloat

    val averageKeysLength = intoCountData.map(_.keys.length).sum.toFloat / wordCount.toFloat

    val inputEff = averageWordLength / averageKeysLength

    val activeChooseData = intoCountData.filter(_.chooseType == 2)
    val activeWordCount = activeChooseData.count.toFloat

    val activeWordLength = activeChooseData.map(_.word.length).sum.toFloat / activeWordCount

    val activeKeysLength = activeChooseData.map(_.keys.length).sum.toFloat / activeWordCount

    val activeInputEff = activeWordLength / activeKeysLength


    val falseRatio = 1 - rightDataCount / wordCount
    val engineWorkRatio = engineWorkCount / rightDataCount
    val predictWordRatio = predictWordCount / engineWorkCount

    val result = "engine type: " + engine + "\nword count: " + wordCount.toString +
      "\nfalse ratio: " + falseRatio.toString + "\nengine work ratio: " + engineWorkRatio.toString +
      "\npredict word ratio: " + predictWordRatio.toString + "\nword average length: " + averageWordLength.toString +
      "\nactive word average length: " + activeWordLength.toString + "\ninput efficiency: " + inputEff.toString +
      "\nactive input efficiency: " + activeInputEff.toString + "\nall count: " + allCount.toString +
      "\nchoose count: " + chooseCount.toString + "\n"

    LOG.info(result)

    val out = new FileWriter(outputPath, true)
    out.write(result)
    out.close()
  }

  override def doAnalyze(sparkContext: SparkContext, paramArray: Array[String]): Unit = {

    if (paramArray.length < 4) {
      sparkContext.stop()
    }

    val jobName = paramArray(0)
    val traceSourcePath = paramArray(1)
    val outputPath = paramArray(2)
    val partitionNum = paramArray(3).toInt
    val dv = if (paramArray.length > 4) paramArray(4) else CharacterUtil.CONSTANT_NONE
    val engine = if (paramArray.length > 5) paramArray(5) else CharacterUtil.CONSTANT_NONE
    val locale = if (paramArray.length > 6) paramArray(6) else CharacterUtil.CONSTANT_NONE
    val nation = if (paramArray.length > 7) paramArray(7) else CharacterUtil.CONSTANT_NONE
    val appVersion = if (paramArray.length > 8) paramArray(8) else CharacterUtil.CONSTANT_NONE

    logInfo("job name: " + jobName)
    logInfo("trace source path: " + traceSourcePath)
    logInfo("output path: " + outputPath)
    logInfo("partition num: " + partitionNum)
    if (paramArray.length > 4) logInfo("dv: " + dv)
    if (paramArray.length > 5) logInfo("engine: " + engine)
    if (paramArray.length > 6) logInfo("locale: " + locale)
    if (paramArray.length > 7) logInfo("nation: " + nation)
    if (paramArray.length > 8) logInfo("app version: " + appVersion)

    val traceTextRdd = sparkContext.textFile(traceSourcePath, partitionNum)

    traceTextRdd.persist()

    val out = new FileWriter(outputPath, true)
    out.write("test group: \n")
    out.close()

    val localeTraceFiltered = traceTextRdd
      .map(_.split(CharacterUtil.CONSTANT_TAB))
      .filter(_.length >= 6)
      .filter(item => CorpusUtil.filteredByInfo(item(5), locale, nation, appVersion, engine, dv))
      .filter(_ (4).nonEmpty)
      .filter(_ (4).contains("input:"))
      .filter(!_ (4).contains("slide:"))

    calculateMetrics(localeTraceFiltered, outputPath, engine)

    if (!dv.equalsIgnoreCase(CharacterUtil.CONSTANT_NONE)) {
      val out2 = new FileWriter(outputPath, true)
      out2.write("control group: \n")
      out2.close()

      val localeTraceFilteredControl = traceTextRdd
        .map(_.split(CharacterUtil.CONSTANT_TAB))
        .filter(_.length >= 6)
        .filter(item => CorpusUtil.filteredByInfoExceptDv(item(5), locale, nation, appVersion, engine, dv))
        .filter(_ (4).nonEmpty)
        .filter(_ (4).contains("input:"))
        .filter(!_ (4).contains("slide:"))

      calculateMetrics(localeTraceFilteredControl, outputPath, engine)
    }

    traceTextRdd.unpersist()

  }

}
