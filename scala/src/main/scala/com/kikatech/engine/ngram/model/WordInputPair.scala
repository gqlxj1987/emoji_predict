package com.kikatech.engine.ngram.model

/**
  * Created by huminghe on 2017/9/12.
  */
case class WordInputPair(keys: String, var word: String, chooseInfo: Choose,
                         var isPartOf: Boolean = true,
                         var isDelete: Boolean = false, var deleteCount: Int = 0,
                         var isNeedDelete: Boolean = false,
                         var autoCorrection: Boolean = false,
                         var falseCorrection: Boolean = false)
