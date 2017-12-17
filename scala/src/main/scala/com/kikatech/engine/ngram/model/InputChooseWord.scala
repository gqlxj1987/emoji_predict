package com.kikatech.engine.ngram.model

import com.kikatech.engine.ngram.util.CharacterUtil

/**
  * Created by huminghe on 2017/9/12.
  */
case class InputChooseWord(keys: String, var word: String, chooseType: Int, var isRightChoose: Boolean = true, var takeIntoCount: Boolean = true) {

  override def toString: String = {
    keys + CharacterUtil.CONSTANT_TAB + word + CharacterUtil.CONSTANT_TAB +
      chooseType.toString + CharacterUtil.CONSTANT_TAB + isRightChoose.toString
  }

}
