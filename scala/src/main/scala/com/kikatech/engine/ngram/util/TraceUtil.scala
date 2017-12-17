package com.kikatech.engine.ngram.util

import com.kikatech.engine.ngram.model._
import com.vdurmont.emoji.EmojiParser

import scala.collection.mutable.ArrayBuffer
import scala.tools.scalap.scalax.util.StringUtil

/**
  * Created by huminghe on 2017/9/12.
  */
object TraceUtil {

  private val regInputRegex = "input:([0-9]+),[0-9]+,[0-9]+,([^;]*)".r
  private val chooseInputRegex = "choose([0-9]):([0-9]+),([0-9]),(false|true),([^;]+)".r

  private val keyFlag = "KEY:"

  private val wordFlag = "WORD:"

  private val punFlag = "PUN:"

  private val digitalFlag = "NUM:"

  private val generatedSplitFlag = " #*# "

  private val splitOriFlag = "#\\*#"

  private val splitFlag = ";"

  private val symbolFlag = "symbol"

  private val shiftFlag = "shift"

  private val deleteFlag = "delete"

  private val spaceFlag = " "

  private val noAlphaBetaFlag = "([^a-zA-Z0-9]+)"

  private val symbolNumberRegex = "([0-9,.?!;]+)".r

  val sentenceFlag = "[0-9,.?!;]+"

  private def extractInputInfo(text: String) = {
    text match {
      case regInputRegex(t, char) => {
        char match {
          case symbolNumberRegex(x) => Input(x, InputType.Punctuation)
          case a: String => {
            if (a.equals(symbolFlag)) {
              Input(a, InputType.Function)
            }
            else if (a.equals(shiftFlag)) {
              Input(a, InputType.Function)
            } else {
              if (a.isEmpty) {
                Input(" ")
              } else {
                Input(a)
              }
            }
          }
          case _ => Input(char)
        }
      }
      case chooseInputRegex(chooseType, t, choosePos, popPattern, wordListArray) =>
        try {
          val wordList = wordListArray.split(",").toList
          Choose(chooseType.toInt, choosePos.toInt, wordList)
        } catch {
          case _: Throwable => None
        }
      case _ => Input(" ")
    }
  }

  def extractInputChooseWordType(line: Array[String]): Array[InputChooseWord] = {

    val textGroup = line(4).toLowerCase.split(splitFlag)
    val extractedInfo = textGroup.map(extractInputInfo).filter(_ != None)
    val inputBuffer = new ArrayBuffer[Input]()
    val currentWordInputBuffer = new ArrayBuffer[String]()

    val wordListBuffer = new ArrayBuffer[String]()
    val wordInputPairBuffer = new ArrayBuffer[WordInputPair]()
    val punctuationInputPairBuffer = new ArrayBuffer[WordInputPair]()

    var falseCount: Int = 0
    var autoCorrectionError: Int = 0
    var spaceFalseCount: Int = 0

    val inputChooseWordTypeBuffer = new ArrayBuffer[InputChooseWord]()

    for (item <- extractedInfo) {
      item match {
        case input: Input => {
          if (input.inputType == InputType.Punctuation) {
            punctuationInputPairBuffer.append(WordInputPair(input.inputChars, input.inputChars, Choose(0, 0, null)))
          }
          inputBuffer += input
          if (input.inputChars.equals(deleteFlag)) {
            if (currentWordInputBuffer.nonEmpty) {
              // 输入单词中途有删除操作
              currentWordInputBuffer.remove(currentWordInputBuffer.length - 1)
            } else {
              // 删除了候选词
              if (wordInputPairBuffer.nonEmpty) {
                wordInputPairBuffer.takeRight(1)(0).isDelete = true
                wordInputPairBuffer.takeRight(1)(0).falseCorrection = true
                wordInputPairBuffer.takeRight(1)(0).deleteCount += 1
                falseCount += 1
                if (inputChooseWordTypeBuffer.nonEmpty) {
                  inputChooseWordTypeBuffer.takeRight(1)(0).isRightChoose = false
                  inputChooseWordTypeBuffer.takeRight(1)(0).takeIntoCount = false
                }

                if (wordInputPairBuffer.takeRight(1)(0).autoCorrection) {
                  autoCorrectionError += 1
                }
                if (wordInputPairBuffer.takeRight(1)(0).chooseInfo.chooseType == 4) {
                  spaceFalseCount += 1
                }
                if (wordInputPairBuffer.takeRight(1)(0).deleteCount > 1) {
                  if (inputChooseWordTypeBuffer.nonEmpty) {
                    inputChooseWordTypeBuffer.takeRight(1)(0).isRightChoose = true
                  }

                  wordInputPairBuffer.takeRight(1)(0).falseCorrection = false
                  falseCount -= 1
                  if (wordInputPairBuffer.takeRight(1)(0).autoCorrection) {
                    autoCorrectionError -= 1
                  }
                  if (wordInputPairBuffer.takeRight(1)(0).chooseInfo.chooseType == 4) {
                    spaceFalseCount -= 1
                  }
                }
              }
            }
          }
          else {
            currentWordInputBuffer += input.inputChars
          }
        }

        case choose: Choose => {
          if (inputBuffer.isEmpty) {
          } else {
            if (choose.chooseType == 6 && inputBuffer.takeRight(1)(0).inputType != InputType.Punctuation) {
              ;
            } else {
              try {
                val word = choose.wordCandidates(choose.chooseNum)
                wordListBuffer += word
                var keys = currentWordInputBuffer.mkString("").trim
                val xx = keys.split(CharacterUtil.CONSTANT_ONLINE_PUNCTUATION_AND_SPACE)
                keys = xx(xx.length - 1).replaceAll("emoji", "").replaceAll("shift", "").replaceAll("symbol", "").replaceAll("enter", "")

                val isPartOf = if (word.toLowerCase.startsWith(keys.toLowerCase)) true else false
                val autoCorrection = (choose.chooseType == 4) && !word.equalsIgnoreCase(keys) && (keys.length > 0)

                if (wordInputPairBuffer.isEmpty || !wordInputPairBuffer.takeRight(1)(0).isDelete) {
                  if (keys.nonEmpty || wordInputPairBuffer.nonEmpty) {
                    inputChooseWordTypeBuffer += InputChooseWord(keys, word, choose.chooseType)
                  }
                }

                wordInputPairBuffer.append(WordInputPair(keys, word, choose, isPartOf, autoCorrection = autoCorrection))
                /*
                if (punctuationInputPairBuffer.nonEmpty) {
                  punctuationInputPairBuffer.foreach(wordInputPairBuffer.append(_))
                }
                */
                punctuationInputPairBuffer.clear()

              } catch {
                case _: Throwable => ;
              }
            }
            currentWordInputBuffer.clear()
          }
        }

        case _ => ;
      }
    }

    /*
    if (currentWordInputBuffer.nonEmpty) {
      try {
        val word = lastWord
        var keys = currentWordInputBuffer.mkString("").trim
        val xx = keys.split(CharacterUtil.CONSTANT_ONLINE_PUNCTUATION_AND_SPACE)
        keys = xx(xx.length - 1).replaceAll("emoji", "").replaceAll("shift", "").replaceAll("symbol", "").replaceAll("enter", "")
        inputChooseWordTypeBuffer += InputChooseWord(keys, word, 4)
      } catch {
        case _: Throwable => ;
      }
      currentWordInputBuffer.clear()
    }
    */

    inputChooseWordTypeBuffer.toArray

  }

  def main(args: Array[String]): Unit = {
    val aa = "com.whatsapp\tHihi harga lps dis 150\tbeca747150a34009aeeb0e8f32dc453a\t20170926070639569\t11506466431121;input:0,169,172,S;input:158,57,174,a;input:284,675,60,p;input:451,179,80,e;input:877,471,406, ;choose4:878,0,false,Sape,Sapa,Sale;input:1572,505,163,j;input:1842,183,78,e;input:2019,483,401, ;choose4:2020,0,false,je,ke,ne;input:2256,656,174,l;input:2492,359,182,g;input:2728,449,382, ;choose4:2729,0,false,lg,LG,lh;input:2973,661,58,p;input:3277,472,384, ;choose4:3278,0,false,p,o,pun;input:3545,313,81,t;input:3833,466,68,u;\t{\"dv\":\"2\",\"ws\":\"2131755627,\",\"lang_pos\":\",ms_MY\\t\",\"na\":\"my\",\"app_vcode\":\"126801\",\"aid\":\"810bad8f12ac504\",\"os\":\"5.0.2\",\"osi\":21,\"extra\":{\"kb_restart\":\"true\",\"engine\":\"DLEngine\",\"engine_version\":\"1\",\"theme\":\"normal\",\"inputType\":\"180225\",\"imeOptions\":\"1140850692\"}}\t810bad8f12ac504"

    val list = extractInputChooseWordType(aa.split(CharacterUtil.CONSTANT_TAB))
    list.foreach(x => print(x.word + "\t" + x.keys + "\n"))
  }

}
