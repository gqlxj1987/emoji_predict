package com.kikatech.engine.ngram.model

/**
  * Created by huminghe on 2017/9/12.
  */
case class Metrics(wordCount: Int, inputEfficiency: Float, screenOnFalseRatio: Float,
                   spaceInputEfficiency: Float, spaceFalseRatio: Float, selectRatio: Float) {

  override def toString: String = {
    "{ wordCount = " + wordCount.toString +
      " , inputEfficiency = " + inputEfficiency.toString +
      " , screenOnFalseRatio = " + screenOnFalseRatio.toString +
      " , spaceInputEfficiency = " + spaceInputEfficiency.toString +
      " , spaceFalseRatio = " + spaceFalseRatio.toString +
      " , selectRatio = " + selectRatio.toString + " }"
  }

}
