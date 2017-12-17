package com.kikatech.engine.ngram.analyzer.emoji

import java.io.FileWriter
import java.util.regex.Pattern

import com.kikatech.engine.ngram.analyzer.Analyzer
import com.kikatech.engine.ngram.util.{CharacterUtil, DictUtil}
import com.vdurmont.emoji.EmojiParser
import org.apache.spark.SparkContext

import scala.collection.mutable.ArrayBuffer

/**
  * Created by huminghe on 2017/12/6.
  */
class EmojiDataAnalyzer extends Analyzer {

  override def doAnalyze(sparkContext: SparkContext, paramArray: Array[String]): Unit = {

    if (paramArray.length != 5) {
      sparkContext.stop()
    }

    val jobName = paramArray(0)
    val vocabularyPath = paramArray(1)
    val inputPath = paramArray(2)
    val outputPath = paramArray(3)
    val partitionNum = paramArray(4).toInt

    logInfo("job name: " + jobName)
    logInfo("vocabulary path: " + vocabularyPath)
    logInfo("input path: " + inputPath)
    logInfo("output path: " + outputPath)
    logInfo("partition num: " + partitionNum)

    val vocabularySet = DictUtil.getVocabularySetLowerCase(sparkContext, vocabularyPath)

    val sentenceRDD = sparkContext.textFile(inputPath, partitionNum)
      .map(_.toLowerCase)
      .filter(sentence => {
        val sentenceFiltered = EmojiParser.removeAllEmojis(sentence)
          .replaceAll(CharacterUtil.CONSTANT_ONLINE_PUNCTUATION, CharacterUtil.CONSTANT_SPACE32)
        val wordArray = sentenceFiltered.split(" ").filter(_.nonEmpty)
        var inVoc = 0
        for (word <- wordArray) {
          if (vocabularySet.contains(word)) {
            inVoc += 1
          }
        }
        inVoc > 0 && inVoc > wordArray.length * 0.8
      })
      .map(_.replaceAll(CharacterUtil.CONSTANT_EMOJI, " $0 "))
      .map(_.replaceAll("[<>\\[\\]\\(\\){}\"]+", " "))
      .map(_.replaceAll("[0-9]+\\S*", " <num> "))
      .map(_.replaceAll(CharacterUtil.CONSTANT_SOME_PUNCTUATION, " <pun> "))
      .map(_.replaceAll("\\.+", " . ").replaceAll("\\?+", " ? ").replaceAll("!+", " ! ").replaceAll(",+", " , ").replaceAll(":+", " : ")
        .replaceAll(";+", " ; ").replaceAll("&+", " & "))
      .filter(_.nonEmpty)
      .flatMap(sentence => {
        val wordArray = sentence.split(" ").filter(_.nonEmpty)
        val newArray = ArrayBuffer.empty[String]
        var state = 1
        val endPuncPattern = Pattern.compile("[\\.\\?!\\s]")
        val emojiPattern = Pattern.compile(CharacterUtil.CONSTANT_EMOJI)
        for (word <- wordArray) {
          val endPuncMatcher = endPuncPattern.matcher(word)
          val emojiMatcher = emojiPattern.matcher(word)
          if (endPuncMatcher.find() || emojiMatcher.find()) {
            state = 2
          } else {
            if (state == 2) {
              newArray += "\n"
            }
            state = 1
          }
          newArray += word
        }
        newArray.mkString(" ").split("\n").map(_.trim)
      })
      .filter(sentence => {
        val wordArray = sentence.split(" ")
        wordArray.length > 1
      })


    val out = new FileWriter(outputPath, false)
    sentenceRDD
      .collect()
      .foreach(w => {
        out.write(w)
        out.write(CharacterUtil.CONSTANT_NEWLINE)
      })
    out.close()
  }

}
