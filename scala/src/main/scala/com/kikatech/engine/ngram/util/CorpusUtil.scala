package com.kikatech.engine.ngram.util

import com.kikatech.engine.ngram.model.MetaInfo
import com.kikatech.engine.ngram.tokenizer.{BurmeseTokenizer, JsonSerializer, ThaiTokenizer, VietnameseTokenizer}
import com.vdurmont.emoji.EmojiParser
import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD

/**
  * Created by huminghe on 2017/7/5.
  */
object CorpusUtil {

  def getWeightByAppName(appName: String): Double = {
    1.0
  }

  def tokenizeSentence(sentence: String, languageCode: String): List[String] = {
    val language = languageCode.toLowerCase
    if (language.equals("vi")) {
      val tokenizer = new VietnameseTokenizer
      tokenizer.tokenize(sentence).filter(_.nonEmpty)
    } else if (language.equals("th")) {
      val tokenizer = new ThaiTokenizer
      tokenizer.tokenize(sentence).filter(_.nonEmpty)
    } else if (language.equals("my")) {
      val tokenizer = new BurmeseTokenizer
      tokenizer.tokenize(sentence).filter(_.nonEmpty)
    } else if (language.equals("my_new")) {
      val tokenizer = new BurmeseTokenizer
      tokenizer.tokenizeNew(sentence).filter(_.nonEmpty)
    } else if (language.endsWith("th_new")) {
      val tokenizer = new ThaiTokenizer
      tokenizer.tokenizeNew(sentence).filter(_.nonEmpty)
    } else {
      sentence.split(CharacterUtil.CONSTANT_SPACE32).filter(_.nonEmpty).toList
    }
  }

  def filterSentenceRDD(sentenceRDD: RDD[String], vocabulary: collection.immutable.Set[String], languageCode: String,
                        wordNumberThreshold: Int, wordProportionThreshold: Double): RDD[String] = {
    sentenceRDD
      .filter(sentence => {
        val words = tokenizeSentence(sentence, languageCode)
        var b = true
        var includeWordsNumber = 0
        for (word <- words) {
          if (vocabulary.contains(word)) {
            includeWordsNumber += 1
          }
        }
        b = b && includeWordsNumber > wordNumberThreshold
        b = b && (includeWordsNumber > words.length * wordProportionThreshold)
        b
      })
  }

  def getTwitterSentenceRDD(sparkContext: SparkContext, corpusPath: String, partitionNum: Int): RDD[String] = {
    if (corpusPath.indexOf(CharacterUtil.CONSTANT_NONE) <= -1) {
      sparkContext.textFile(corpusPath, partitionNum)
        .distinct()
        .map(corpus => EmojiParser.removeAllEmojis(corpus))
        .map(_.replaceAll(CharacterUtil.CONSTANT_RT, CharacterUtil.CONSTANT_EMPTY))
        .map(_.replaceAll(CharacterUtil.CONSTANT_SPECIAL_CHARACTER, CharacterUtil.CONSTANT_SPACE32))
        .map(_.replaceAll(CharacterUtil.CONSTANT_STR_PATTERN_STATEMENT_TERMINATOR, CharacterUtil.CONSTANT_NEWLINE))
        .flatMap(_.split(CharacterUtil.CONSTANT_TAB))
        .flatMap(_.split(CharacterUtil.CONSTANT_NEWLINE))
        .filter(_.nonEmpty)
    } else {
      sparkContext.emptyRDD[String]
    }
  }

  def getWikiSentenceRDD(sparkContext: SparkContext, corpusPath: String, vocabulary: collection.immutable.Set[String], partitionNum: Int): RDD[String] = {
    if (corpusPath.indexOf(CharacterUtil.CONSTANT_NONE) <= -1) {
      val sentences = sparkContext.textFile(corpusPath, partitionNum)
        .map(_.replaceAll(CharacterUtil.CONSTANT_STR_PATTERN_TERMINATOR_WIKI, CharacterUtil.CONSTANT_NEWLINE))
        .map(_.replaceAll(CharacterUtil.CONSTANT_ONLINE_PUNCTUATION, CharacterUtil.CONSTANT_SPACE32))
        .flatMap(_.split(CharacterUtil.CONSTANT_TAB))
        .flatMap(_.split(CharacterUtil.CONSTANT_NEWLINE))
        .filter(_.nonEmpty)
      filterSentenceRDD(sentences, vocabulary, "none", 2, 0.7)
    } else {
      sparkContext.emptyRDD[String]
    }
  }

  def getWikiSentenceRDD(sparkContext: SparkContext, corpusPath: String, partitionNum: Int): RDD[String] = {
    if (corpusPath.indexOf(CharacterUtil.CONSTANT_NONE) <= -1) {
      sparkContext.textFile(corpusPath, partitionNum)
        .map(_.replaceAll(CharacterUtil.CONSTANT_STR_PATTERN_TERMINATOR_WIKI, CharacterUtil.CONSTANT_NEWLINE))
        .map(_.replaceAll(CharacterUtil.CONSTANT_ONLINE_PUNCTUATION, CharacterUtil.CONSTANT_SPACE32))
        .flatMap(_.split(CharacterUtil.CONSTANT_TAB))
        .flatMap(_.split(CharacterUtil.CONSTANT_NEWLINE))
        .filter(_.nonEmpty)
    } else {
      sparkContext.emptyRDD[String]
    }
  }


  def getOnlineWeightedSentenceRDD(sparkContext: SparkContext, corpusPath: String, partitionNum: Int): RDD[(String, Double)] = {
    if (corpusPath.indexOf(CharacterUtil.CONSTANT_NONE) <= -1) {
      sparkContext.textFile(corpusPath, partitionNum)
        .map(_.split(CharacterUtil.CONSTANT_TAB))
        .filter(_.nonEmpty)
        .filter(_.length > 1)
        .flatMap(array => {
          val appName = array(0)
          val weight = CorpusUtil.getWeightByAppName(appName)
          val sentences = processOnlineCorpusToSentence(array(1))
          sentences.map(sentence => (sentence, weight))
        })
    } else {
      sparkContext.emptyRDD[(String, Double)]
    }
  }

  def getOnlineWeightedSentenceRDDByLocale(sparkContext: SparkContext, corpusPath: String, nation: String, partitionNum: Int): RDD[(String, Double)] = {
    if (corpusPath.indexOf(CharacterUtil.CONSTANT_NONE) <= -1) {
      sparkContext.textFile(corpusPath, partitionNum)
        .map(_.split(CharacterUtil.CONSTANT_TAB))
        .filter(_.nonEmpty)
        .filter(_.length >= 6)
        .filter(array => filteredByLocale(array(5), nation))
        .flatMap(array => {
          val appName = array(0)
          val weight = CorpusUtil.getWeightByAppName(appName)
          val sentences = processOnlineCorpusToSentence(array(1))
          sentences.map(sentence => (sentence, weight))
        })
    } else {
      sparkContext.emptyRDD[(String, Double)]
    }
  }

  def getOnlineUserSentenceRDD(sparkContext: SparkContext, corpusPath: String, partitionNum: Int): RDD[(String, String, Double)] = {
    if (corpusPath.indexOf(CharacterUtil.CONSTANT_NONE) <= -1) {
      sparkContext.textFile(corpusPath, partitionNum)
        .map(_.split(CharacterUtil.CONSTANT_TAB))
        .filter(_.nonEmpty)
        .filter(_.length > 2)
        .flatMap(array => {
          val appName = array(0)
          val weight = CorpusUtil.getWeightByAppName(appName)
          val user = array(2)
          val sentences = processOnlineCorpusToParagraph(array(1))
          sentences.map(sentence => (sentence, user, weight))
        })
    } else {
      sparkContext.emptyRDD[(String, String, Double)]
    }
  }

  def getOnlineUserSentenceRDDByLocale(sparkContext: SparkContext, corpusPath: String, nation: String, partitionNum: Int): RDD[(String, String, Double)] = {
    if (corpusPath.indexOf(CharacterUtil.CONSTANT_NONE) <= -1) {
      sparkContext.textFile(corpusPath, partitionNum)
        .map(_.split(CharacterUtil.CONSTANT_TAB))
        .filter(_.nonEmpty)
        .filter(_.length >= 6)
        .filter(array => filteredByLocale(array(5), nation))
        .flatMap(array => {
          val appName = array(0)
          val weight = CorpusUtil.getWeightByAppName(appName)
          val user = array(2)
          val sentences = processOnlineCorpusToParagraph(array(1))
          sentences.map(sentence => (sentence, user, weight))
        })
    } else {
      sparkContext.emptyRDD[(String, String, Double)]
    }
  }

  def processOnlineCorpusToSentence(corpus: String): Array[String] = {
    EmojiParser.removeAllEmojis(corpus)
      .replaceAll(CharacterUtil.CONSTANT_SPECIAL_CHARACTER, CharacterUtil.CONSTANT_SPACE32)
      .replaceAll(CharacterUtil.CONSTANT_STR_PATTERN_STATEMENT_TERMINATOR, CharacterUtil.CONSTANT_NEWLINE)
      .replaceAll(CharacterUtil.CONSTANT_ONLINE_PUNCTUATION, CharacterUtil.CONSTANT_SPACE32)
      .split(CharacterUtil.CONSTANT_NEWLINE)
  }

  def processOnlineCorpusToParagraph(corpus: String): Array[String] = {
    EmojiParser.removeAllEmojis(corpus)
      .replaceAll(CharacterUtil.CONSTANT_ONLINE_PUNCTUATION, CharacterUtil.CONSTANT_SPACE32)
      .split(CharacterUtil.CONSTANT_NEWLINE)
  }

  def filteredByLocale(metaInfoString: String, na: String): Boolean = {
    if (na.indexOf(CharacterUtil.CONSTANT_NONE) > -1) {
      return true
    }
    if (metaInfoString == null || metaInfoString.isEmpty) {
      return false
    }
    if (!metaInfoString.startsWith("{")) {
      return false
    }
    val metaSerializer = new JsonSerializer[MetaInfo]
    val metaInfo = metaSerializer.deSerialize(metaInfoString)
    if (metaInfo == null) {
      return false
    }
    metaInfo.na != null && metaInfo.na.equalsIgnoreCase(na)

  }

  def filteredByInfo(metaInfoString: String, locale: String = CharacterUtil.CONSTANT_NONE,
                     na: String = CharacterUtil.CONSTANT_NONE, appVcode: String = CharacterUtil.CONSTANT_NONE,
                     engine: String = CharacterUtil.CONSTANT_NONE, dv: String = CharacterUtil.CONSTANT_NONE): Boolean = {
    try {
      if (metaInfoString == null || metaInfoString.isEmpty) {
        return false
      }
      if (!metaInfoString.startsWith("{")) {
        return false
      }
      val metaSerializer = new JsonSerializer[MetaInfo]
      val metaInfo = metaSerializer.deSerialize(metaInfoString)
      if (metaInfo == null) {
        return false
      }
      if (metaInfo.extra == null) {
        return false
      }
      val filterLocale = CharacterUtil.CONSTANT_NONE.equals(locale) ||
        (metaInfo.lang_pos != null && metaInfo.lang_pos.replaceAll("[\\t,]+", "").toLowerCase.contains(locale.toLowerCase))
      val filterNa = CharacterUtil.CONSTANT_NONE.equals(na) || (metaInfo.na != null && metaInfo.na.equalsIgnoreCase(na))
      val filterAppVcode = CharacterUtil.CONSTANT_NONE.equals(appVcode) || (metaInfo.app_vcode != null && metaInfo.app_vcode.toInt >= appVcode.toInt)
      val filterEngine = CharacterUtil.CONSTANT_NONE.equals(engine) || (metaInfo.extra.engine != null && metaInfo.extra.engine.equalsIgnoreCase(engine))
      val filterDv = CharacterUtil.CONSTANT_NONE.equals(dv) || (metaInfo.dv != null && metaInfo.dv.equalsIgnoreCase(dv))
      filterLocale && filterNa && filterAppVcode && filterEngine && filterDv
    } catch {
      case _: Throwable => false;
    }
  }

  def filteredByInfoExceptDv(metaInfoString: String, locale: String = CharacterUtil.CONSTANT_NONE,
                             na: String = CharacterUtil.CONSTANT_NONE, appVcode: String = CharacterUtil.CONSTANT_NONE,
                             engine: String = CharacterUtil.CONSTANT_NONE, dv: String = CharacterUtil.CONSTANT_NONE): Boolean = {
    try {
      if (metaInfoString == null || metaInfoString.isEmpty) {
        return false
      }
      if (!metaInfoString.startsWith("{")) {
        return false
      }
      val metaSerializer = new JsonSerializer[MetaInfo]
      val metaInfo = metaSerializer.deSerialize(metaInfoString)
      if (metaInfo == null) {
        return false
      }
      if (metaInfo.extra == null) {
        return false
      }
      val filterLocale = CharacterUtil.CONSTANT_NONE.equals(locale) ||
        (metaInfo.lang_pos != null && metaInfo.lang_pos.replaceAll("[\\t,]+", "").toLowerCase.contains(locale.toLowerCase))
      val filterNa = CharacterUtil.CONSTANT_NONE.equals(na) || (metaInfo.na != null && metaInfo.na.equalsIgnoreCase(na))
      val filterAppVcode = CharacterUtil.CONSTANT_NONE.equals(appVcode) || (metaInfo.app_vcode != null && metaInfo.app_vcode.toInt >= appVcode.toInt)
      val filterEngine = CharacterUtil.CONSTANT_NONE.equals(engine) || (metaInfo.extra.engine != null && metaInfo.extra.engine.equalsIgnoreCase(engine))
      val filterDv = CharacterUtil.CONSTANT_NONE.equals(dv) || (metaInfo.dv != null && !metaInfo.dv.equalsIgnoreCase(dv))
      filterLocale && filterNa && filterAppVcode && filterEngine && filterDv
    } catch {
      case _: Throwable => false;
    }
  }

}
