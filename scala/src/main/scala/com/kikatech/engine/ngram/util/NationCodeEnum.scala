package com.kikatech.engine.ngram.util

import org.apache.spark.Logging

/**
  * Created by huminghe on 2017/6/5.
  */
object NationCodeEnum extends Enumeration with Logging {

  type NationCodeEnum = Value;
  // 美

  val NON_LATIN = Value("NON_LATIN")

  val EMOJI = Value("EMOJI")

  val US = Value("EN_US");
  val US_DIG = Value("EN_US_DIGT");

  // 英
  val GB = Value("EN_GB");

  // 西
  val PT_PT = Value("PT_PT");

  // 巴
  val PT_BR = Value("PT_BR");
  val PT_BR_DIG = Value("PT_BR_DIG");

  // 法
  val FR = Value("FR");

  // 西
  val ES = Value("ES");
  val ES_US = Value("ES_US");
  val ES_DIG = Value("ES_DIG");

  // 中
  val CN = Value("CN");

  // 日
  val JP = Value("JP");

  // 德
  val DE = Value("DE");

  // 俄
  val RU = Value("RU");

  // 阿拉伯
  val AR = Value("AR");

  // 意大利
  val IT = Value("IT");

  // 泰语
  val TH = Value("TH");

  // 越南
  val MS_MY = Value("MS_MY");

  // 马来
  val VI = Value("VI");

  // 印度尼西亚
  val IN_ID = Value("IN_ID");

  // 老挝
  val LO_LA = Value("LO_LA");

  // 僧伽罗
  val SI = Value("SI");

  // 缅甸
  val MY = Value("MY");

  /**
    * 印度语系列 unicode 字符----Being
    */
  // hindi--印地语[天城文]
  val HI_IN = Value("HI_IN");

  // hinglish--与英文一致
  val HN_IN = Value("HN_IN");

  // kannada--坎那达语[埃纳德语, 卡纳达]
  val KN_IN = Value("KN_IN");

  // malayalam--马拉雅拉姆语
  val ML_IN = Value("ML_IN");

  // telugu--泰卢固语
  val TE_IN = Value("TE_IN");

  // bengali--孟加拉语
  val BN_IN = Value("BN_IN");

  // punjabi--旁遮普语 旁遮普语古木基文
  val PA_IN = Value("PA_IN");

  // tamil--泰米尔语
  val TA_IN = Value("TA_IN");

  // assamese--阿萨姆语
  val AS_IN = Value("AS_IN");

  // gujarati--古吉拉特语
  val GU_IN = Value("GU_IN");

  // marathi--马拉地语
  val MR_IN = Value("MR_IN");

  // urdu--乌尔都语
  val UR_IN = Value("UR_IN");

  // ne_NP--尼泊尔语
  val NE_IN = Value("NE_IN");

  // sa_NP--旁遮普语
  val SA_IN = Value("SA_IN");

  // or_IN--奥利亚语
  val OR_IN = Value("OR_IN");

  // sd_IN--信德语
  val SD_IN = Value("SD_IN");

  /**
    * 印度语系列 unicode 字符----End
    */

  // tr--土尔其
  val TR_TR = Value("TR_TR");

  // fa_IR--波斯语
  val FA_IR = Value("FA_IR");

  val SU_SU = Value("SU_SU");

  val JV_JV = Value("JV_JV");

  val TL_PH = Value("TL_PH");

  // am_ET-Amharic-阿姆哈拉语
  val AM_ET = Value("AM_ET");

  // km_KH-Khmer-高棉语
  val KM_KH = Value("KM_KH");


  // SW_SW-Swahili-斯瓦西里
  val SW_SW = Value("SW_SW");

  // HA_HA-Hausa-豪萨语
  val HA = Value("HA");

  // el--Greek 现代希腊语
  val EL_GRC = Value("EL_GRC");

  // nl--Dutch 荷兰语
  val NL = Value("NL");

  // NL_ZAF 南非荷兰语 Nederlands_ZAF
  val NL_ZAF = Value("NL_ZAF");

  // bo_CN Tibetan 葳语
  val BO_CN = Value("BO_CN");

  // KK_KZ 哈萨克语 Kazakh
  val KK_KZ = Value("KK_KZ");

  // RO_ROU 罗马尼亚 Romanian
  val RO_ROU = Value("RO_ROU");

  // SV_SWE 瑞典网站 Swedish
  val SV_SWE = Value("SV_SWE");

  // cs_CZK 捷克语 Czech
  val CS_CZK = Value("CS_CZK");

  // hu_HUN 匈牙利语 Hungarian
  val HU_HUN = Value("HU_HUN");

  // pl_POL 波兰语 Polish
  val PL_POL = Value("PL_POL");

  // no_NOR 挪威语 Norwegian
  val NO_NOR = Value("NO_NOR");

}
