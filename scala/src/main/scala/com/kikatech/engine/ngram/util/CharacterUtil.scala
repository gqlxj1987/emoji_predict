package com.kikatech.engine.ngram.util

import java.util.regex.Pattern

import com.kikatech.engine.ngram.util.NationCodeEnum._

/**
  * Created by huminghe on 2017/6/1.
  */
object CharacterUtil {

  val CONSTANT_SPACE32: String = String.valueOf(32.toChar)

  val CONSTANT_EMPTY: String = ""

  val CONSTANT_TAB: String = "\t"

  val CONSTANT_NEWLINE: String = "\n"

  val CONSTANT_DOT: String = "\\."

  val CONSTANT_RT: String = "RT"

  val CONSTANT_NONE: String = "none"

  val CONSTANT_BI_GRAM_WORD_SPLIT: String = "#"

  val COLUMN_APART = "[\t]+"

  val CONSTANT_EMOJI = "(?:[\\uD83C\\uDF00-\\uD83D\\uDDFF]|[\\uD83E\\uDD00-\\uD83E\\uDDFF]|[\\uD83D\\uDE00-\\uD83D\\uDE4F]|" +
    "[\\uD83D\\uDE80-\\uD83D\\uDEFF]|[\\u2600-\\u26FF]\\uFE0F?|[\\u2700-\\u27BF]\\uFE0F?|\\u24C2\\uFE0F?|[\\uD83C\\uDDE6-\\uD83C\\uDDFF]{1,2}|" +
    "[\\uD83C\\uDD70\\uD83C\\uDD71\\uD83C\\uDD7E\\uD83C\\uDD7F\\uD83C\\uDD8E\\uD83C\\uDD91-\\uD83C\\uDD9A]\\uFE0F?|" +
    "[\\u0023\\u002A\\u0030-\\u0039]\\uFE0F?\\u20E3|[\\u2194-\\u2199\\u21A9-\\u21AA]\\uFE0F?|[\\u2B05-\\u2B07\\u2B1B\\u2B1C\\u2B50\\u2B55]\\uFE0F?|" +
    "[\\u2934\\u2935]\\uFE0F?|[\\u3030\\u303D]\\uFE0F?|[\\u3297\\u3299]\\uFE0F?|" +
    "[\\uD83C\\uDE01\\uD83C\\uDE02\\uD83C\\uDE1A\\uD83C\\uDE2F\\uD83C\\uDE32-\\uD83C\\uDE3A\\uD83C\\uDE50\\uD83C\\uDE51]\\uFE0F?|" +
    "[\\u203C\\u2049]\\uFE0F?|[\\u25AA\\u25AB\\u25B6\\u25C0\\u25FB-\\u25FE]\\uFE0F?|[\\u00A9\\u00AE]\\uFE0F?|" +
    "[\\u2122\\u2139]\\uFE0F?|\\uD83C\\uDC04\\uFE0F?|\\uD83C\\uDCCF\\uFE0F?|[\\u231A\\u231B\\u2328\\u23CF\\u23E9-\\u23F3\\u23F8-\\u23FA]\\uFE0F?)" +
    "[\\uD83C\\uDFFB-\\uD83C\\uDFFF]?"

  val CONSTANT_SPECIAL_CHARACTER = "[་`~\\\"<>\\(\\)`´\\[\\]\\^_#\\*&\\$+=\\/%" +
    "\\u0082\\u0084\\u0085\\u0094\\u0093\\u0092\\u0091\\u0096\\u0097\\u0098" +
    "\\u061B\\u066C\\u065C\\u061F" +
    "＂、〈〉》《「」『』【】〝〞“”〟＇（）＜＞［］{}̀＾＿̀｀､･" +
    "\\u00A0\\uFEFF\\u20B9\\u1680\\u180E\\u2000-\\u200B\\u202F\\u205F\\u3000]"

  val CONSTANT_STR_PATTERN_STATEMENT_TERMINATOR =
    "[།！？。!\\?,:;\\|\\u104A\\u104B\\u061F\\u065C\\u060C\\u066C\\u061F\\u06D4\\u066B\\u066C\\u061F\\u061F]" +
      "|\\.+\\s|\\.+\\n"

  val CONSTANT_STR_PATTERN_TERMINATOR_WIKI =
    "[<>\\{\\}།！？。!\\?,:;\\|\\u104A\\u104B\\u061F\\u065C\\u060C\\u066C\\u061F\\u06D4\\u066B\\u066C\\u061F\\u061F" +
      "\\[\\]\\^&\\$+=\\/%\\\\、〈〉》《「」『』【】〝〞“”〟＇（）＜＞［］{}̀＾＿̀｀､]" +
      "|\\.+\\s|\\.+\\n"

  val CONSTANT_ONLINE_PUNCTUATION = "[་`~\\\"<>\\(\\)`´\\[\\]\\^_\\.#\\*&\\$+=\\/%" +
    "\\u0082\\u0084\\u0085\\u0094\\u0093\\u0092\\u0091\\u0096\\u0097\\u0098" +
    "\\u061B\\u066C\\u065C\\u061F" +
    "＂、〈〉》《「」『』【】〝〞“”〟＇（）＜＞［］{}̀＾＿̀｀､･" +
    "\\u00A0\\uFEFF\\u20B9\\u1680\\u180E\\u2000-\\u200B\\u202F\\u205F\\u3000" +
    "།！？。!\\?,:;\\|\\u104A\\u104B\\u061F\\u065C\\u060C\\u066C\\u061F\\u06D4\\u066B\\u066C\\u061F\\u061F]"

  val CONSTANT_ONLINE_PUNCTUATION_AND_SPACE = "[་`~\\\"<>\\(\\)`´\\[\\]\\^_\\.#\\*&\\s\\n\\t\\$+=\\/%" +
    "\\u0082\\u0084\\u0085\\u0094\\u0093\\u0092\\u0091\\u0096\\u0097\\u0098" +
    "\\u061B\\u066C\\u065C\\u061F" +
    "＂、〈〉》《「」『』【】〝〞“”〟＇（）＜＞［］{}̀＾＿̀｀､･" +
    "\\u00A0\\uFEFF\\u20B9\\u1680\\u180E\\u2000-\\u200B\\u202F\\u205F\\u3000" +
    "།！？。!\\?,:;\\|\\u104A\\u104B\\u061F\\u065C\\u060C\\u066C\\u061F\\u06D4\\u066B\\u066C\\u061F\\u061F]"

  val CONSTANT_SOME_PUNCTUATION = "[་`~\\\"\\-`´\\^_#\\*\\$+=\\/%" +
    "\\u0082\\u0084\\u0085\\u0094\\u0093\\u0092\\u0091\\u0096\\u0097\\u0098" +
    "\\u061B\\u066C\\u065C\\u061F" +
    "＂、〈〉》《「」『』【】〝〞“”〟＇（）＜＞［］{}̀＾＿̀｀､･" +
    "\\u00A0\\uFEFF\\u20B9\\u1680\\u180E\\u2000-\\u200B\\u202F\\u205F\\u3000" +
    "།！？。\\|\\u104A\\u104B\\u061F\\u065C\\u060C\\u066C\\u061F\\u06D4\\u066B\\u066C\\u061F\\u061F]+"

  /**
    * ------------------------
    * language regular expression string
    * ------------------------
    */

  val CONSTANT_NON_LATIN_PATTERN = "^[^A-Za-z]+$"

  val CONSTANT_US_STR_PATTERN = "^[A-Za-z']+$";
  val CONSTANT_US_DIGT_STR_PATTERN = "^[A-Za-z'0-9]+$";

  val CONSTANT_ES_STR_PATTERN = "^[\\u00C1\\u00C9\\u00CD\\u00D1\\u00D3\\u00DA" +
    "\\u00DC\\u00E1\\u00E9\\u00ED\\u00F1\\u00F3\\u00FA\\u00FCA-Za-z']+$";
  val CONSTANT_ES_DIG_STR_PATTERN = "^[\\u00C0-\\u00FFA-Za-z'0-9]+$";

  val CONSTANT_PT_STR_PATTERN = "^[\\u00E0\\u00C0\\u00E1\\u00C1\\u00E2\\u00C2\\u00E3\\u00C3\\u00E7\\u00C7\\u00E9" +
    "\\u00C9\\u00EA\\u00CA\\u00ED\\u00CD\\u00F3\\u00D3\\u00F4\\u00D4\\u00F5\\u00D5\\u00FA" +
    "\\u00EA\\u00FC\\u00ECA-Za-z']+$";

  val CONSTANT_PT_DIG_STR_PATTERN = "^[\\u00E0\\u00C0\\u00E1\\u00C1\\u00E2\\u00C2\\u00E3\\u00C3" +
    "\\u00E7\\u00C7\\u00E9\\u00C9\\u00EA\\u00CA\\u00ED\\u00CD\\u00F3\\u00D3\\u00F4\\u00D4\\u00F5" +
    "\\u00D5\\u00FA\\u00EA\\u00FC\\u00ECA-Za-z']+$";

  val CONSTANT_FR_STR_PATTERN = "^[\\u00C0\\u00C1\\u00C2\\u00C3\\u00C7\\u00C8\\u00C9\\u00CA" +
    "\\u00CC\\u00CD\\u00CE\\u00CF\\u00D2\\u00D3\\u00D4\\u00D5\\u00D9\\u00DA\\u00DB\\u00E0" +
    "\\u00E1\\u00E2\\u00E3\\u00E7\\u00E8\\u00E9\\u00EA\\u00EC\\u00ED\\u00EE\\u00EF\\u00F2" +
    "\\u00F3\\u00F4\\u00F5\\u00F9\\u00FA\\u00FBA-Za-z'\\-]+$";

  val CONSTANT_DE_STR_PATTERN = "^[\\u00C4\\u00D6\\u00DC\\u00E4\\u00F6\\u00F6\\u00FC\\u00DFA-Za-z']+$";
  // val CONSTANT_DE_STR_PATTERN = "^[\\u00C4\\u00D6\\u00DC\\u00E4\\u00F6\\u00F6\\u00FC\\u00DFA-Za-z']+$";
  val CONSTANT_CN_STR_PATTERN = "^[\\u4e00-\\u9fa5]+$";
  val CONSTANT_JP_STR_PATTERN = "^[\\u3031-\\u30FF]+$";
  val CONSTANT_RU_STR_PATTERN = "^[\\u0410-\\u044F\\0451]+$";

  // it 2016-12-15 confirm
  val CONSTANT_IT_STR_PATTERN = "^[\\-A-Za-z'\\u00E0\\u00E8\\u00E9\\u00EC\\u00F2\\u00F9\\u00C0\\u00C8\\u00C9" +
    "\\u00CC\\u00D2\\u00D9]+$";

  val CONSTANT_MS_MY_STR_PATTERN = CONSTANT_US_STR_PATTERN

  // 在s3上In目录里存储是的"印度尼西亚"的语料
  val CONSTANT_IN_ID_STR_PATTERN = CONSTANT_US_STR_PATTERN

  //  val CONSTANT_TH_STR_PATTERN = "\\p{InThai}";
  val CONSTANT_TH_STR_PATTERN = "^[\\u0e01\\u0e02\\u0e03\\u0e04\\u0e05\\u0e06\\u0e07\\u0e08\\u0e09\\u0e0a" +
    "\\u0e0b\\u0e0c\\u0e0d\\u0e0e\\u0e0f\\u0e10\\u0e11\\u0e12\\u0e13\\u0e14\\u0e15\\u0e16\\u0e17\\u0e18\\" +
    "u0e19\\u0e1a\\u0e1b\\u0e1c\\u0e1d\\u0e1e\\u0e1f\\u0e20\\u0e21\\u0e22\\u0e23\\u0e24\\u0e25\\u0e26\\u0e27" +
    "\\u0e28\\u0e29\\u0e2a\\u0e2b\\u0e2c\\u0e2d\\u0e2e\\u0e2f\\u0e30\\u0e31\\u0e32\\u0e33\\u0e34\\u0e35" +
    "\\u0e36\\u0e37\\u0e38\\u0e39\\u0e3a\\u0e40\\u0e41\\u0e42\\u0e43\\u0e44\\u0e45\\u0e46\\u0e47\\u0e48" +
    "\\u0e49\\u0e4a\\u0e4b\\u0e4c\\u0e4d\\u0e4e]+$";

  // 越南 2016-12-15 confirm
  val CONSTANT_VI_STR_PATTERN = "^[\\u0061\\u0041\\u00E0\\u00C0\\u1EA3\\u1EA2\\u00E3\\u00C3\\u00E1\\u00C1" +
    "\\u1EA1\\u1EA0\\u0103\\u0102\\u1EB1\\u1EB0\\u1EB3\\u1EB2\\u1EB5\\u1EB4\\u1EAF\\u1EAE\\u1EB7\\u1EB6" +
    "\\u00E2\\u00C2\\u1EA7\\u1EA6\\u1EA9\\u1EA8\\u1EAB\\u1EAA\\u1EA5\\u1EA4\\u1EAD\\u1EAC\\u0062\\u0042" +
    "\\u0063\\u0043\\u0064\\u0044\\u0111\\u0110\\u0065\\u0045\\u00E8\\u00C8\\u1EBB\\u1EBA\\u1EBD\\u1EBC" +
    "\\u00E9\\u00C9\\u1EB9\\u1EB8\\u00EA\\u00CA\\u1EC1\\u1EC0\\u1EC3\\u1EC2\\u1EC5\\u1EC4\\u1EBF\\u1EBE" +
    "\\u1EC7\\u1EC6\\u0066\\u0046\\u0067\\u0047\\u0068\\u0048\\u0069\\u0049\\u00EC\\u00CC\\u1EC9\\u1EC8" +
    "\\u0129\\u0128\\u00ED\\u00CD\\u1ECB\\u1ECA\\u006A\\u004A\\u006B\\u004B\\u006C\\u004C\\u006D\\u004D" +
    "\\u006E\\u004E\\u006F\\u004F\\u00F2\\u00D2\\u1ECF\\u1ECE\\u00F5\\u00D5\\u00F3\\u00D3\\u1ECD\\u1ECC" +
    "\\u00F4\\u00D4\\u1ED3\\u1ED2\\u1ED5\\u1ED4\\u1ED7\\u1ED6\\u1ED1\\u1ED0\\u1ED9\\u1ED8\\u01A1\\u01A0" +
    "\\u1EDD\\u1EDC\\u1EDF\\u1EDE\\u1EE1\\u1EE0\\u1EDB\\u1EDA\\u1EE3\\u1EE2\\u0070\\u0050\\u0071\\u0051" +
    "\\u0072\\u0052\\u0073\\u0053\\u0074\\u0054\\u0075\\u0055\\u00F9\\u00D9\\u1EE7\\u1EE6\\u0169\\u0168" +
    "\\u00FA\\u00DA\\u1EE5\\u1EE4\\u01B0\\u01AF\\u1EEB\\u1EEA\\u1EED\\u1EEC\\u1EEF\\u1EEE\\u1EE9\\u1EE8" +
    "\\u1EF1\\u1EF0\\u0076\\u0056\\u0077\\u0057\\u0078\\u0058\\u0079\\u0059\\u1EF3\\u1EF2\\u1EF7\\u1EF6" +
    "\\u1EF9\\u1EF8\\u00FD\\u00DD\\u1EF5\\u1EF4\\u007A\\u005A]+$"

  // 老挝 2016-12-15 confirm
  val CONSTANT_LO_LA_STR_PATTERN = "^[\\u0E81\\u0E82\\u0E84\\u0E87\\u0E88\\u0E8A\\u0E8D\\u0E94\\u0E95" +
    "\\u0E96\\u0E97\\u0E99\\u0E9A\\u0E9B\\u0E9C\\u0E9D\\u0E9E\\u0E9F\\u0EA1\\u0EA2\\u0EA3\\u0EA5\\u0EA7" +
    "\\u0EAA\\u0EAB\\u0EAD\\u0EAE\\u0EAF\\u0EB0\\u0EB1\\u0EB2\\u0EB3\\u0EB4\\u0EB5\\u0EB6\\u0EB7\\u0EB8" +
    "\\u0EB9\\u0EBB\\u0EBC\\u0EBD\\u0EC0\\u0EC1\\u0EC2\\u0EC3\\u0EC4\\u0EC6\\u0EC8\\u0EC9\\u0ECA\\u0ECB" +
    "\\u0ECC\\u0ECD\\u0ED0\\u0ED1\\u0ED2\\u0ED3\\u0ED4\\u0ED5\\u0ED6\\u0ED7\\u0ED8\\u0ED9\\u0EDC\\u0EDD]+$"

  // 阿拉伯 ar 2016-12-15 confirm
  val CONSTANT_AR_STR_PATTERN = "^[\\u0621\\u0622\\u0623\\u0624\\u0625\\u0626\\u0627\\u0628\\u0629\\u062A" +
    "\\u062B\\u062C\\u062D\\u062E\\u062F\\u0630\\u0631\\u0632\\u0633\\u0634\\u0635\\u0636\\u0637\\u0638" +
    "\\u0639\\u063A\\u0641\\u0642\\u0643\\u0644\\u0645\\u0646\\u0647\\u0648\\u0649\\u064A]+$"

  // 缅甸 my 2016-12-15 confirm
  val CONSTANT_my_STR_PATTERN = "^[\\u1000-\\u109F]+$"

  // ------------------------Being 印度语系列 unicode 字符
  // hindi--印地语[天城文] 2015-12-14 confirm
  //  val CONSTANT_hi_IN_STR_PATTERN = "^[\\u0900-\\u097F]+$";
  val CONSTANT_hi_IN_STR_PATTERN = "^[\\u0900-\\u097F]+$";

  // hinglish--与英文一致
  val CONSTANT_hn_IN_STR_PATTERN = CONSTANT_US_STR_PATTERN;

  // kannada--坎那达语[埃纳德语, 卡纳达]
  val CONSTANT_kn_IN_STR_PATTERN = "^[\\u0C80-\\u0CFF]+$";

  // malayalam--马拉雅拉姆语
  val CONSTANT_ml_IN_STR_PATTERN = "^[\\u0D00-\\u0D7F]+$";

  // telugu--泰卢固语
  val CONSTANT_te_IN_STR_PATTERN = "^[\\u0C00-\\u0C7F]+$";

  // bengali--孟加拉语
  val CONSTANT_bn_IN_STR_PATTERN = "^[\\u0980-\\u09FF]+$";

  // punjabi--旁遮普语 旁遮普语古木基文
  val CONSTANT_pa_IN_STR_PATTERN = "^[\\u0A00-\\u0A7F]+$";

  // tamil--泰米尔语
  val CONSTANT_ta_IN_STR_PATTERN = "^[\\u0B80-\\u0BFF]+$";

  // assamese--阿萨姆语
  val CONSTANT_as_IN_STR_PATTERN = "^([\\u0980-\\u09AF]|[\\u09B2-\\u09FF])+$";

  // gujarati--古吉拉特语
  val CONSTANT_gu_IN_STR_PATTERN = "^[\\u0A80-\\u0AFF]+$";

  // marathi--马拉地语
  val CONSTANT_mr_IN_STR_PATTERN = CONSTANT_hi_IN_STR_PATTERN;

  // Oriya--奥利亚语
  val CONSTANT_or_IN_STR_PATTERN: String = "^([\\u0B05-\\u0B0C]" +
    "|[\\u0B0F\\u0B10]" +
    "|[\\u0B13-\\u0B28]" +
    "|[\\u0B2A-\\u0B30]" +
    "|[\\u0B32\\u0B33]" +
    "|[\\u0B35-\\u0B39]" +
    "|[\\u0B5C-\\u0B61])+$"

  // Sanskrit--梵文
  val CONSTANT_sa_IN_STR_PATTERN: String = "^[\\u0900-\\u097F]+$"

  // Sindhi--信德语
  val CONSTANT_sd_IN_STR_PATTERN: String = "^[\\u0622\\u0628\\u067B\\u0680\\u067E" +
    "\\u06A6\\u062A\\u0629\\u067F\\u0679\\u067A\\u062B\\u062C\\u0684\\u0683\\u0686" +
    "\\u0687\\u062D\\u062E\\u062F\\u068C\\u0688\\u068F\\u068D\\u0630\\u0631\\u0691" +
    "\\u0632\\u0698\\u0633\\u0634\\u0635\\u0636\\u0637\\u0638\\u0639\\u063A\\u0641" +
    "\\u0642\\u06AA\\u06A9\\u06AF\\u06B3\\u06B1\\u0699\\u0644\\u0645\\u0646\\u06BA" +
    "\\u06BB\\u0648\\u06C4\\u0647\\u06BE\\u0621\\u0649\\u0620\\u06D3\\u06D2\\u0650" +
    "\\u0610\\u06F3\\u06DD]+$"

  // 2017-05-10 by ab
  val CONSTANT_sd_2_IN_STR_PATTERN: String = "^[\\u0642\\u0635\\u064A\\u0631\\u067F" +
    "\\u0639\\u06B2\\u0646\\u067E\\u0687\\u0627\\u0633\\u062F\\u0641\\u06AF\\u06C1" +
    "\\u062C\\u06AA\\u0644\\u06A9\\u06B1\\u0632\\u062E\\u0637\\u0680\\u0628\\u0646" +
    "\\u0645\\u061F\\u0636\\u0699\\u067D\\u062B\\u063A\\u06BE\\u067A\\u06A6\\u0683" +
    "\\u0684\\u0686\\u0622\\u0634\\u068A\\u06A6\\u062D\\u06E1\\u06FD\\u068F\\u068C" +
    "\\u068D\\u0626\\u0630\\u0651\\u0638\\u0654\\u067B\\u06BB\\u06FE]+$"

  // -------------------End 印度语系列 unicode 字符

  // urdu--乌尔都语
  // https://en.wikipedia.org/wiki/Perso-Arabic_Script_Code_for_Information_Interchange
  val CONSTANT_ur_IN_STR_PATTERN_BAK_2 = "^[\\u0640-\\u0656]+$"
  val CONSTANT_ur_IN_STR_PATTERN_BAK = "^[\\u0600-\\u06FF]+$"
  val CONSTANT_ur_IN_STR_PATTERN = "^[\\u0621\\u0622\\u0623\\u0624\\u0626\\u0627" +
    "\\u0628\\u062A\\u062B\\u062C\\u062D\\u062E\\u062F\\u0630\\u0631\\u0632\\u0633" +
    "\\u0634\\u0635\\u0636\\u0637\\u0638\\u0639\\u063A\\u0641\\u0642\\u0644\\u0645" +
    "\\u0646\\u0648\\u064B\\u064E\\u0650\\u0651\\u0652\\u0670\\u0679\\u067E\\u0686" +
    "\\u0688\\u0691\\u0698\\u06A9\\u06AF\\u06BA\\u06BE\\u06C0\\u06C1\\u06C3\\u06CC\\u06D2\\u06D8]+$";

  // si--僧伽罗 2016-12-15 confirm
  val CONSTANT_si_STR_PATTERN = "^[\\u0D82\\u0D85\\u0D86\\u0D87\\u0D88\\u0D89\\u0D8A\\u0D8B\\u0D8C\\u0D8D" +
    "\\u0D91\\u0D92\\u0D93\\u0D94\\u0D95\\u0D96\\u0D9A\\u0D9B\\u0D9C\\u0D9D\\u0D9E\\u0D9F" +
    "\\u0DA0\\u0DA1\\u0DA2\\u0DA4\\u0DA5\\u0DA7\\u0DA8\\u0DA9\\u0DAA\\u0DAB\\u0DAC\\u0DAD" +
    "\\u0DAE\\u0DAF\\u0DB0\\u0DB1\\u0DB3\\u0DB4\\u0DB5\\u0DB6\\u0DB7\\u0DB8\\u0DB9\\u0DBA" +
    "\\u0DBB\\u0DBD\\u0DC0\\u0DC1\\u0DC2\\u0DC3\\u0DC4\\u0DC5\\u0DC6\\u0DCA\\u0DCF\\u0DD0" +
    "\\u0DD1\\u0DD2\\u0DD3\\u0DD4\\u0DD6\\u0DD8\\u0DD9\\u0DDA\\u0DDB\\u0DDC\\u0DDD\\u0DDE" +
    "\\u0DDF\\u0DF2\\u0DF3]+$";

  // tr---土尔其
  val CONSTANT_tr_TR_PATTERN = "^[\\-a-zA-A'\\u011f\\u011e\\u0131\\u0130\\u00f6\\u00d6\\u00fc\\" +
    "u00dc\\u015f\\u015e\\u00e7\\u00c7]+$"

  // fa---波斯语
  val CONSTANT_fa_IR_PATTERN = "^[\\u0621\\u0622\\u0623\\u0624\\u0625\\u0626\\u0627\\u0628\\u0629\\u062A" +
    "\\u062B\\u062C\\u062D\\u062E\\u062F\\u0630\\u0631\\u0632\\u0633\\u0634\\u0635\\u0636\\u0637\\u0638" +
    "\\u0639\\u063A\\u0641\\u0642\\u0643\\u0644\\u0645\\u0646\\u0647\\u0648\\u0649\\u064A\\u0698\\u06AF\\u0686\\u067E]+$"

  // Hebrew--希伯来语
  val CONSTANT_he_STR_PATTERN = "^[\\u0590-\\u05F4]+$";

  // amharic--阿姆哈拉语
  val CONSTANT_am_ET_STR_PATTERN_BAK = "^([\\u1200-\\u1248]" +
    "|[\\u124A-\\u124D]" +
    "|[\\u1250-\\u12566]" +
    "|[\\u1258]" +
    "|[\\u125A-\\u125D]" +
    "|[\\u1260-\\u1288]" +
    "|[\\u128A-\\u128D]" +
    "|[\\u1290-\\u12B0]" +
    "|[\\u12B2-\\u12B5]" +
    "|[\\u12B8-\\u12BE]" +
    "|[\\u12C0]" +
    "|[\\u12C2-\\u12C5]" +
    "|[\\u12C8-\\u12CF]" +
    "|[\\u12D0-\\u12D6]" +
    "|[\\u12D8-\\u1310]" +
    "|[\\u1313-\\u1315]" +
    "|[\\u1318-\\u135A])+$";

  val CONSTANT_am_ET_STR_PATTERN = "^[\\u1240\\u12C8\\u1228\\u1270\\u12E8\\u1350" +
    "\\u1330\\u1230\\u12F0\\u1348\\u1308\\u1200\\u1300\\u12A8\\u1208\\u1340" +
    "\\u12D8\\u12A0\\u1328\\u1238\\u1260\\u1290\\u1218\\u1298]+$";

  // su_US--巽他語
  val CONSTANT_su_STR_PATTERN: String = CONSTANT_US_STR_PATTERN

  // jv_JV--爪哇语
  val CONSTANT_jv_STR_PATTERN: String = CONSTANT_US_STR_PATTERN

  // tl_PH--Tagalog
  val CONSTANT_tl_PH_STR_PATTERN: String = CONSTANT_US_STR_PATTERN

  // ne_IN--Nepali 尼泊尔语
  val CONSTANT_ne_IN_STR_PATTERN: String = "^[\\u0900-\\u097F]+$";

  // km_KH--Khmer 高棉语
  val CONSTANT_km_KH_STR_PATTERN: String = "^[\\u1780-\\u17DD]+$";

  // sw_SW--Swahili 斯瓦希里语
  val CONSTANT_sw_SW_STR_PATTERN: String = CONSTANT_US_STR_PATTERN

  // ha--Hausa 豪萨语
  val CONSTANT_ha_STR_PATTERN: String = CONSTANT_US_STR_PATTERN

  // el--Greek 现代希腊语
  val CONSTANT_el_GRC_STR_PATTERN: String = "^[\\u03C2\\u03B5\\u03AD" +
    "\\u03C1\\u03C4\\u03C5\\u03CD\\u03CB\\u03B0\\u03B8\\u03B9" +
    "\\u03AF\\u03CA\\u0390\\u03BF\\u03CC\\u03C0\\u03C2\\u03B5" +
    "\\u03AD\\u03C1\\u03C4\\u03C5\\u03B8\\u03B9\\u03AF\\u03CA" +
    "\\u0390\\u03BF\\u03CC\\u03C0\\u03B1\\u03AC\\u03C3\\u03B4" +
    "\\u03C6\\u03B3\\u03B7\\u03AE\\u03BE\\u03BA\\u03BB\\u03B6" +
    "\\u03C7\\u03C8\\u03C9\\u03CE\\u03B2\\u03BD\\u03BC]+$";

  // nl--Dutch 荷兰语
  val CONSTANT_nl_STR_PATTERN: String = "^[\\u00E1\\u00E4\\u00E2" +
    "\\u00E0\\u00E6\\u00E3\\u00E5\\u0101\\u00E9\\u00EB\\u00EA" +
    "\\u00E8\\u0119\\u0117\\u0113\\u00ED\\u00EF\\u00EC\\u00EE" +
    "\\u012F\\u012B\\u0133\\u00F3\\u00F6\\u00F4\\u00F2\\u00F5" +
    "\\u0153\\u00F8\\u014D\\u00FA\\u00FC\\u00FB\\u00F9\\u016B" +
    "\\u00F1\\u0144\\u0133\\u0071\\u0077\\u0065\\u0072\\u0074" +
    "\\u0079\\u0075\\u0069\\u006f\\u0070\\u0061\\u0073\\u0064" +
    "\\u0066\\u0067\\u0068\\u006a\\u006b\\u006c\\u007a\\u0078" +
    "\\u0063\\u0076\\u0062\\u006e\\u006d\\u0051\\u0057\\u0045" +
    "\\u0052\\u0054\\u0059\\u0055\\u0049\\u004f\\u0050\\u0041" +
    "\\u0053\\u0044\\u0046\\u0047\\u0048\\u004a\\u004b\\u004c" +
    "\\u005a\\u0058\\u0043\\u0056\\u0042\\u004e\\u004d]+$"

  // bo_CN--Tibetan 葳语
  val CONSTANT_bo_CN_STR_PATTERN: String = "^([\\u0F00-\\u0FFF])+$"


  // kk_KZ Kazakh 哈萨克语
  val CONSTANT_kk_KZ_STR_PATTERN: String = "^[\\u0439\\u0446\\u0443\\u043a\\u0435\\u043d\\u0433" +
    "\\u0448\\u0449\\u0437\\u0445\\u0444\\u044b\\u0432\\u0430\\u043f\\u0440\\u043e\\u043b" +
    "\\u0434\\u0436\\u044d\\u044f\\u0447\\u0441\\u043c\\u0438\\u0442\\u044c\\u0431\\u044e" +
    "\\u0419\\u0426\\u0423\\u041a\\u0415\\u041d\\u0413\\u0428\\u0429\\u0417\\u0425\\u0424" +
    "\\u042b\\u0412\\u0410\\u041f\\u0420\\u041e\\u041b\\u0414\\u0416\\u042d\\u042f\\u0427" +
    "\\u0421\\u041c\\u0418\\u0422\\u042c\\u0411\\u042e\\u04b1\\u04af\\u049b\\u0451\\u04a3" +
    "\\u0493\\u0456\\u04d9\\u04e9\\u04bb\\u044a\\u04b0\\u04ae\\u049a\\u0401\\u04a2\\u0492" +
    "\\u0406\\u04d8\\u04e8\\u04ba\\u042a]+$"


  // ro_ROU 罗马尼亚 Romanian
  val CONSTANT_ro_ROU_STR_PATTERN: String = "^[\\u0439\\u0446\\u0443\\u043a\\u0435\\u043d\\u0433" +
    "\\u0448\\u0449\\u0437\\u0445\\u0444\\u044b\\u0432\\u0430\\u043f\\u0440\\u043e\\u043b" +
    "\\u0434\\u0436\\u044d\\u044f\\u0447\\u0441\\u043c\\u0438\\u0442\\u044c\\u0431\\u044e" +
    "\\u0419\\u0426\\u0423\\u041a\\u0415\\u041d\\u0413\\u0428\\u0429\\u0417\\u0425\\u0424" +
    "\\u042b\\u0412\\u0410\\u041f\\u0420\\u041e\\u041b\\u0414\\u0416\\u042d\\u042f\\u0427" +
    "\\u0421\\u041c\\u0418\\u0422\\u042c\\u0411\\u042e\\u04b1\\u04af\\u049b\\u0451\\u04a3" +
    "\\u0493\\u0456\\u04d9\\u04e9\\u04bb\\u044a\\u04b0\\u04ae\\u049a\\u0401\\u04a2\\u0492" +
    "\\u0406\\u04d8\\u04e8\\u04ba\\u042a\\u0071\\u0077\\u0065\\u0072\\u0074\\u0079\\u0075" +
    "\\u0069\\u006f\\u0070\\u0061\\u0073\\u0064\\u0066\\u0067\\u0068\\u006a\\u006b\\u006c" +
    "\\u007a\\u0078\\u0063\\u0076\\u0062\\u006e\\u006d\\u0051\\u0057\\u0045\\u0052\\u0054" +
    "\\u0059\\u0055\\u0049\\u004f\\u0050\\u0041\\u0053\\u0044\\u0046\\u0047\\u0048\\u004a" +
    "\\u004b\\u004c\\u005a\\u0058\\u0043\\u0056\\u0042\\u004e\\u004d]+$"

  // sv_swe 瑞典网站 Swedish
  val CONSTANT_sv_SWE_STR_PATTERN: String = "^[\\u0071\\u0077\\u0065\\u0072\\u0074\\u0079\\u0075" +
    "\\u0069\\u006f\\u0070\\u00e5\\u0061\\u0073\\u0064\\u0066\\u0067\\u0068\\u006b\\u006a\\u006c" +
    "\\u00f6\\u00e4\\u007a\\u0078\\u0063\\u0076\\u0062\\u006e\\u006d\\u0051\\u0057\\u0045\\u0052" +
    "\\u0054\\u0059\\u0055\\u0049\\u004f\\u0050\\u00c5\\u0041\\u0053\\u0044\\u0046\\u0047\\u0048" +
    "\\u004a\\u004b\\u004c\\u00d6\\u00c4\\u005a\\u0058\\u0043\\u0056\\u0042\\u004e\\u004d\\u0118" +
    "\\u00ca\\u00cb\\u00c8\\u00c9\\u0158\\u00de\\u0164\\u0178\\u00dd\\u016a\\u00d9\\u00db\\u00da" +
    "\\u00dc\\u00cf\\u00cc\\u00cd\\u00ce\\u014c\\u00d4\\u00d5\\u00d2\\u00d3\\u00c1\\u00c0\\u00c2" +
    "\\u0104\\u00c3\\u015a\\u0160\\u015e\\u0053\\u0053\\u00d0\\u010e\\u0141\\u0152\\u00d8\\u00c6" +
    "\\u017b\\u0179\\u017d\\u010c\\u00c7\\u0106\\u0147\\u0143\\u00d1\\u0119\\u00ea\\u00eb\\u00e8" +
    "\\u00e9\\u00fe\\u0165\\u0159\\u00ff\\u00fd\\u016b\\u00f9\\u00fb\\u00fa\\u00fc\\u00ef\\u00ec" +
    "\\u00ed\\u00ee\\u014d\\u00f4\\u00f5\\u00f2\\u00f3\\u00e1\\u00e0\\u00e2\\u0105\\u00e3\\u015b" +
    "\\u0161\\u015f\\u00df\\u00f0\\u010f\\u0142\\u0153\\u00f8\\u00e6\\u017c\\u017a\\u017e\\u010d" +
    "\\u00e7\\u0107\\u0148\\u0144\\u00f1]+$"

  // nl_ZAF 南非荷兰语 Nederlands_ZAF
  val CONSTANT_nl_ZAF_STR_PATTERN: String = "^[\\u0117\\u00eb\\u0119\\u0113\\u00e8\\u00e9\\u00ea" +
    "\\u0133\\u00fd\\u016b\\u00fc\\u00f9\\u00fb\\u00fa\\u0133\\u012b\\u00ee\\u012f\\u00ef\\u00ec" +
    "\\u00ed\\u014d\\u00f8\\u0153\\u00f5\\u00f2\\u00f6\\u00f4\\u00f3\\u00e6\\u00e3\\u00e5\\u0101" +
    "\\u00e1\\u00e2\\u00e4\\u00e0\\u00f1\\u0144\\u0116\\u00cb\\u0118\\u0112\\u00c8\\u00c9\\u00ca" +
    "\\u0132\\u00dd\\u016a\\u00dc\\u00d9\\u00db\\u00da\\u0132\\u012a\\u00ce\\u012e\\u00cf\\u00cc" +
    "\\u00cd\\u014c\\u00d8\\u0152\\u00d5\\u00d2\\u00d6\\u00d4\\u00d3\\u00c6\\u00c3\\u00c5\\u0100" +
    "\\u00c1\\u00c2\\u00c4\\u00c0\\u00d1\\u0143\\u0071\\u0077\\u0065\\u0072\\u0074\\u0079\\u0075" +
    "\\u0069\\u006f\\u0070\\u0061\\u0073\\u0064\\u0066\\u0067\\u0068\\u006a\\u006b\\u006c\\u007a" +
    "\\u0078\\u0063\\u0076\\u0062\\u006e\\u006d\\u0051\\u0057\\u0045\\u0052\\u0054\\u0059\\u0055" +
    "\\u0049\\u004f\\u0050\\u0041\\u0053\\u0044\\u0046\\u0047\\u0048\\u004a\\u004b\\u004c\\u005a" +
    "\\u0058\\u0043\\u0056\\u0042\\u004e\\u004d]+$"


  // cs_CZK 捷克语 Czech
  val CONSTANT_cs_CZK_STR_PATTERN: String = "^[\\u0117\\u00eb\\u0119\\u0113\\u00e8\\u00e9\\u011b" +
    "\\u00ea\\u0159\\u0165\\u017c\\u017e\\u017a\\u016b\\u00fc\\u00f9\\u00fb\\u00fa\\u016f\\u012b" +
    "\\u00ec\\u012f\\u00ef\\u00ed\\u00ee\\u014d\\u00f8\\u0153\\u00f5\\u00f2\\u00f4\\u00f6\\u00f3" +
    "\\u00e6\\u00e3\\u00e5\\u0101\\u00e1\\u00e0\\u00e2\\u00e4\\u015b\\u0161\\u00df\\u010f\\u00fd" +
    "\\u00ff\\u0107\\u010d\\u00e7\\u0144\\u0148\\u00f1\\u0116\\u00cb\\u0118\\u0112\\u00c8\\u00c9" +
    "\\u011a\\u00ca\\u0158\\u0164\\u017b\\u017d\\u0179\\u016a\\u00dc\\u00d9\\u00db\\u00da\\u016e" +
    "\\u012a\\u00cc\\u012e\\u00cf\\u00cd\\u00ce\\u014c\\u00d8\\u0152\\u00d5\\u00d2\\u00d4\\u00d6" +
    "\\u00d3\\u00c6\\u00c3\\u00c5\\u0100\\u00c1\\u00c0\\u00c2\\u00c4\\u015a\\u0160\\u0053\\u0053" +
    "\\u010e\\u00dd\\u0178\\u0106\\u010c\\u00c7\\u0143\\u0147\\u00d1\\u0071\\u0077\\u0065\\u0072" +
    "\\u0074\\u0079\\u0075\\u0069\\u006f\\u0070\\u0061\\u0073\\u0064\\u0066\\u0067\\u0068\\u006a" +
    "\\u006b\\u006c\\u007a\\u0078\\u0063\\u0076\\u0062\\u006e\\u006d\\u0051\\u0057\\u0045\\u0052" +
    "\\u0054\\u0059\\u0055\\u0049\\u004f\\u0050\\u0041\\u0053\\u0044\\u0046\\u0047\\u0048\\u004a" +
    "\\u004b\\u004c\\u005a\\u0058\\u0043\\u0056\\u0042\\u004e\\u004d]+$"


  // hu_HUN 匈牙利语 Hungarian
  val CONSTANT_hu_HUN_STR_PATTERN: String = "^[\\u0119\\u0117\\u0113\\u00ea\\u00e9\\u00e8\\u00eb" +
    "\\u016b\\u00fb\\u00f9\\u0171\\u00fa\\u00fc\\u012b\\u00ec\\u012f\\u00ef\\u00ed\\u00ee\\u014d" +
    "\\u00f8\\u0153\\u00f5\\u00f2\\u00f4\\u0151\\u00f6\\u00f3\\u00e6\\u00e3\\u00e5\\u0101\\u00e1" +
    "\\u00e0\\u00e2\\u00e4\\u0118\\u0116\\u0112\\u00ca\\u00c9\\u00c8\\u00cb\\u016a\\u00db\\u00d9" +
    "\\u0170\\u00da\\u00dc\\u012a\\u00cc\\u012e\\u00cf\\u00cd\\u00ce\\u014c\\u00d8\\u0152\\u00d5" +
    "\\u00d2\\u00d4\\u0150\\u00d6\\u00d3\\u00c6\\u00c3\\u00c5\\u0100\\u00c1\\u00c0\\u00c2\\u00c4" +
    "\\u0071\\u0077\\u0065\\u0072\\u0074\\u0079\\u0075\\u0069\\u006f\\u0070\\u0061\\u0073\\u0064" +
    "\\u0066\\u0067\\u0068\\u006a\\u006b\\u006c\\u007a\\u0078\\u0063\\u0076\\u0062\\u006e\\u006d" +
    "\\u0051\\u0057\\u0045\\u0052\\u0054\\u0059\\u0055\\u0049\\u004f\\u0050\\u0041\\u0053\\u0044" +
    "\\u0046\\u0047\\u0048\\u004a\\u004b\\u004c\\u005a\\u0058\\u0043\\u0056\\u0042\\u004e\\u004d]+$"


  // pl_POL 波兰语 Polish
  val CONSTANT_pl_POL_STR_PATTERN: String = "^[\\u00eb\\u0117\\u0113\\u00e9\\u0119\\u00e8\\u00ea" +
    "\\u00fc\\u00fa\\u016b\\u00f9\\u00fb\\u014d\\u00f8\\u0153\\u00f5\\u00f2\\u00f4\\u00f6\\u00f3" +
    "\\u00e6\\u00e3\\u00e5\\u0101\\u0105\\u00e1\\u00e0\\u00e2\\u00e4\\u0161\\u015b\\u00df\\u0142" +
    "\\u017c\\u017a\\u017e\\u010d\\u0107\\u00e7\\u0144\\u00f1\\u00cb\\u0116\\u0112\\u00c9\\u0118" +
    "\\u00c8\\u00ca\\u00dc\\u00da\\u016a\\u00d9\\u00db\\u014c\\u00d8\\u0152\\u00d5\\u00d2\\u00d4" +
    "\\u00d6\\u00d3\\u00c6\\u00c3\\u00c5\\u0100\\u0104\\u00c1\\u00c0\\u00c2\\u00c4\\u0160\\u015a" +
    "\\u0053\\u0053\\u0141\\u017b\\u0179\\u017d\\u010c\\u0106\\u00c7\\u0143\\u00d1\\u0071\\u0077" +
    "\\u0065\\u0072\\u0074\\u0079\\u0075\\u0069\\u006f\\u0070\\u0061\\u0073\\u0064\\u0066\\u0067" +
    "\\u0068\\u006a\\u006b\\u006c\\u007a\\u0078\\u0063\\u0076\\u0062\\u006e\\u006d\\u0051\\u0057" +
    "\\u0045\\u0052\\u0054\\u0059\\u0055\\u0049\\u004f\\u0050\\u0041\\u0053\\u0044\\u0046\\u0047" +
    "\\u0048\\u004a\\u004b\\u004c\\u005a\\u0058\\u0043\\u0056\\u0042\\u004e\\u004d]+$"


  // no_NOR 挪威语 Norwegian
  val CONSTANT_no_NOR_STR_PATTERN: String = "^[\\u00e5\\u00f8\\u00e6\\u0119\\u0117\\u0113\\u00ea" +
    "\\u00e9\\u00e8\\u00eb\\u016b\\u00f9\\u00fc\\u00fb\\u00fa\\u014d\\u00f5\\u0153\\u00f6\\u00f3" +
    "\\u00f4\\u00f2\\u00e2\\u00e3\\u0101\\u00e0\\u00e4\\u00e1\\u00c5\\u00d8\\u00c6\\u0118\\u0116" +
    "\\u0112\\u00ca\\u00c9\\u00c8\\u00cb\\u016a\\u00d9\\u00dc\\u00db\\u00da\\u014c\\u00d5\\u0152" +
    "\\u00d6\\u00d3\\u00d4\\u00d2\\u00c2\\u00c3\\u0100\\u00c0\\u00c4\\u00c1\\u0071\\u0077\\u0065" +
    "\\u0072\\u0074\\u0079\\u0075\\u0069\\u006f\\u0070\\u0061\\u0073\\u0064\\u0066\\u0067\\u0068" +
    "\\u006a\\u006b\\u006c\\u007a\\u0078\\u0063\\u0076\\u0062\\u006e\\u006d\\u0051\\u0057\\u0045" +
    "\\u0052\\u0054\\u0059\\u0055\\u0049\\u004f\\u0050\\u0041\\u0053\\u0044\\u0046\\u0047\\u0048" +
    "\\u004a\\u004b\\u004c\\u005a\\u0058\\u0043\\u0056\\u0042\\u004e\\u004d]+$"


  /**
    * --------------------------------
    * language pattern
    * --------------------------------
    */
  val PAT_STATEMENT_TERMINATOR: Pattern = Pattern.compile(CONSTANT_STR_PATTERN_STATEMENT_TERMINATOR);

  val PAT_NON_LATIN: Pattern = Pattern.compile(CONSTANT_NON_LATIN_PATTERN)

  val PAT_EMOJI: Pattern = Pattern.compile(CONSTANT_EMOJI)

  // United States
  val PAT_US: Pattern = Pattern.compile(CONSTANT_US_STR_PATTERN);
  val PAT_US_DIGT: Pattern = Pattern.compile(CONSTANT_US_DIGT_STR_PATTERN);
  // 法国
  val PAT_FR: Pattern = Pattern.compile(CONSTANT_FR_STR_PATTERN);
  // 葡萄牙
  val PAT_PT: Pattern = Pattern.compile(CONSTANT_PT_STR_PATTERN);
  val PAT_PT_DIG: Pattern = Pattern.compile(CONSTANT_PT_DIG_STR_PATTERN);
  // 西班牙
  val PAT_ES: Pattern = Pattern.compile(CONSTANT_ES_STR_PATTERN);
  val PAT_ES_DIG: Pattern = Pattern.compile(CONSTANT_ES_DIG_STR_PATTERN);

  // 西班牙-美国
  val PAT_ES_US: Pattern = Pattern.compile(CONSTANT_ES_STR_PATTERN);

  // 中国
  val PAT_CN: Pattern = Pattern.compile(CONSTANT_CN_STR_PATTERN);

  // 日本
  val PAT_JP: Pattern = Pattern.compile(CONSTANT_JP_STR_PATTERN);

  // 德国
  val PAT_DE: Pattern = Pattern.compile(CONSTANT_DE_STR_PATTERN);

  // 俄罗斯
  val PAT_RU: Pattern = Pattern.compile(CONSTANT_RU_STR_PATTERN);

  // 意大利
  val PAT_IT: Pattern = Pattern.compile(CONSTANT_IT_STR_PATTERN);

  // 泰语
  val PAT_TH: Pattern = Pattern.compile(CONSTANT_TH_STR_PATTERN);

  // 老挝
  val PAT_LO_LA: Pattern = Pattern.compile(CONSTANT_LO_LA_STR_PATTERN);

  // 马来
  val PAT_MS_MY: Pattern = Pattern.compile(CONSTANT_MS_MY_STR_PATTERN);

  // 越南
  val PAT_VI: Pattern = Pattern.compile(CONSTANT_VI_STR_PATTERN);

  // 印度尼西亚
  val PAT_IN_ID: Pattern = Pattern.compile(CONSTANT_IN_ID_STR_PATTERN);

  // 阿拉伯
  val PAT_AR: Pattern = Pattern.compile(CONSTANT_AR_STR_PATTERN);

  // 缅甸
  val PAT_MY: Pattern = Pattern.compile(CONSTANT_my_STR_PATTERN);

  // 印度语系列 unicode 字符----Being
  // hindi--印地语[天城文]
  val PAT_HI_IN: Pattern = Pattern.compile(CONSTANT_hi_IN_STR_PATTERN);

  // hinglish--与英文一致
  val PAT_HN_IN: Pattern = Pattern.compile(CONSTANT_hn_IN_STR_PATTERN);

  // kannada--坎那达语[埃纳德语, 卡纳达]
  val PAT_KN_IN: Pattern = Pattern.compile(CONSTANT_kn_IN_STR_PATTERN);

  // malayalam--马拉雅拉姆语
  val PAT_ML_IN: Pattern = Pattern.compile(CONSTANT_ml_IN_STR_PATTERN);

  // telugu--泰卢固语
  val PAT_TE_IN: Pattern = Pattern.compile(CONSTANT_te_IN_STR_PATTERN);

  // bengali--孟加拉语
  val PAT_BN_IN: Pattern = Pattern.compile(CONSTANT_bn_IN_STR_PATTERN);

  // punjabi--旁遮普语 旁遮普语古木基文
  val PAT_PA_IN: Pattern = Pattern.compile(CONSTANT_pa_IN_STR_PATTERN);

  // tamil--泰米尔语
  val PAT_TA_IN: Pattern = Pattern.compile(CONSTANT_ta_IN_STR_PATTERN);

  // assamese--阿萨姆语
  val PAT_AS_IN: Pattern = Pattern.compile(CONSTANT_as_IN_STR_PATTERN);

  // gujarati--古吉拉特语
  val PAT_GU_IN: Pattern = Pattern.compile(CONSTANT_gu_IN_STR_PATTERN);

  // marathi--马拉地语
  val PAT_MR_IN: Pattern = Pattern.compile(CONSTANT_mr_IN_STR_PATTERN);

  // urdu--乌尔都语
  val PAT_UR_IN: Pattern = Pattern.compile(CONSTANT_ur_IN_STR_PATTERN);

  // Nepali--尼泊尔语
  val PAT_NE_IN: Pattern = Pattern.compile(CONSTANT_ne_IN_STR_PATTERN)

  // Sanskirt--梵文
  val PAT_SA_IN: Pattern = Pattern.compile(CONSTANT_sa_IN_STR_PATTERN)

  // Oriya--奥利亚语
  val PAT_OR_IN: Pattern = Pattern.compile(CONSTANT_or_IN_STR_PATTERN)

  // Sindhi--信德语
  val PAT_SD_IN: Pattern = Pattern.compile(CONSTANT_sd_IN_STR_PATTERN)

  // 印度语系列 unicode 字符----End

  // si--僧伽罗
  val PAT_SI: Pattern = Pattern.compile(CONSTANT_si_STR_PATTERN);

  // tr--土尔其
  val PAT_TR_TR: Pattern = Pattern.compile(CONSTANT_tr_TR_PATTERN);

  // fa_IR--波斯语
  val PAT_FA_IR: Pattern = Pattern.compile(CONSTANT_fa_IR_PATTERN);

  // su_US--巽他語
  val PAT_SU_SU: Pattern = Pattern.compile(CONSTANT_su_STR_PATTERN)

  // jv_JV--爪哇语
  val PAT_JV_JV: Pattern = Pattern.compile(CONSTANT_jv_STR_PATTERN)

  // tl_PH--他加禄语
  val PAT_TL_PH: Pattern = Pattern.compile(CONSTANT_tl_PH_STR_PATTERN)

  // am_ET
  val PAT_AM_ET: Pattern = Pattern.compile(CONSTANT_am_ET_STR_PATTERN)

  // km_KH--高棉语
  val PAT_KM_KH: Pattern = Pattern.compile(CONSTANT_km_KH_STR_PATTERN)

  // km_KH--高棉语
  val PAT_SW_SW: Pattern = Pattern.compile(CONSTANT_sw_SW_STR_PATTERN)

  // ha-Hausa-豪萨语
  val PAT_HA: Pattern = Pattern.compile(CONSTANT_ha_STR_PATTERN)

  // el--Greek 现代希腊语
  val PAT_EL_GRC: Pattern = Pattern.compile(CONSTANT_el_GRC_STR_PATTERN)

  // nl--Dutch 荷兰语
  val PAT_NL: Pattern = Pattern.compile(CONSTANT_nl_STR_PATTERN)

  // bo--Tibetan 葳语
  val PAT_BO_CN: Pattern = Pattern.compile(CONSTANT_bo_CN_STR_PATTERN)

  // kk_KZ 哈萨克语 Kazakh
  val PAT_KK_KZ: Pattern = Pattern.compile(CONSTANT_kk_KZ_STR_PATTERN)

  // ro_ROU 罗马尼亚 Romanian
  val PAT_RO_ROU: Pattern = Pattern.compile(CONSTANT_ro_ROU_STR_PATTERN)

  // sv_swe 瑞典网站 Swedish
  val PAT_SV_SWE: Pattern = Pattern.compile(CONSTANT_sv_SWE_STR_PATTERN)

  // nl_ZAF 南非荷兰语 Nederlands_ZAF
  val PAT_NL_ZAF: Pattern = Pattern.compile(CONSTANT_nl_ZAF_STR_PATTERN)

  // cs_CZK 捷克语 Czech
  val PAT_CS_CZK: Pattern = Pattern.compile(CONSTANT_cs_CZK_STR_PATTERN)

  // hu_HUN 匈牙利语 Hungarian
  val PAT_HU_HUN: Pattern = Pattern.compile(CONSTANT_hu_HUN_STR_PATTERN)

  // pl_POL 波兰语 Polish
  val PAT_PL_POL: Pattern = Pattern.compile(CONSTANT_pl_POL_STR_PATTERN)

  // no_NOR 挪威语 Norwegian
  val PAT_NO_NOR: Pattern = Pattern.compile(CONSTANT_no_NOR_STR_PATTERN)

  def getCharacterPattern(nationCode: Value): java.util.regex.Pattern = {
    nationCode match {
      case NationCodeEnum.NON_LATIN => {
        PAT_NON_LATIN
      }
      case NationCodeEnum.EMOJI => {
        PAT_EMOJI;
      }
      case NationCodeEnum.ES => {
        PAT_ES;
      }
      case NationCodeEnum.ES_US => {
        PAT_ES_US;
      }
      case NationCodeEnum.FR => {
        PAT_FR;
      }
      case NationCodeEnum.PT_BR => {
        PAT_PT;
      }
      case NationCodeEnum.PT_PT => {
        PAT_PT;
      }
      case NationCodeEnum.CN => {
        PAT_CN;
      }
      case NationCodeEnum.JP => {
        PAT_JP;
      }
      case NationCodeEnum.DE => {
        PAT_DE;
      }
      case NationCodeEnum.RU => {
        PAT_RU;
      }
      case NationCodeEnum.AR => {
        PAT_AR;
      }
      case NationCodeEnum.IT => {
        PAT_IT;
      }
      case NationCodeEnum.US => {
        PAT_US;
      }
      case NationCodeEnum.US_DIG => {
        PAT_US_DIGT;
      }
      case NationCodeEnum.ES_DIG => {
        PAT_ES_DIG;
      }
      case NationCodeEnum.PT_BR_DIG => {
        PAT_PT_DIG;
      }
      case NationCodeEnum.TH => {
        PAT_TH;
      }
      case NationCodeEnum.LO_LA => {
        PAT_LO_LA;
      }
      case NationCodeEnum.SI => {
        PAT_SI;
      }

      /**
        * 印度语系列 unicode 字符----Being
        */
      case NationCodeEnum.HI_IN => {
        PAT_HI_IN;
      }
      case NationCodeEnum.HN_IN => {
        PAT_HN_IN;
      }
      case NationCodeEnum.KN_IN => {
        PAT_KN_IN;
      }
      case NationCodeEnum.ML_IN => {
        PAT_ML_IN;
      }
      case NationCodeEnum.TE_IN => {
        PAT_TE_IN;
      }
      case NationCodeEnum.BN_IN => {
        PAT_BN_IN;
      }
      case NationCodeEnum.PA_IN => {
        PAT_PA_IN;
      }
      case NationCodeEnum.TA_IN => {
        PAT_TA_IN;
      }
      case NationCodeEnum.AS_IN => {
        PAT_AS_IN;
      }
      case NationCodeEnum.GU_IN => {
        PAT_GU_IN;
      }
      case NationCodeEnum.MR_IN => {
        PAT_MR_IN;
      }
      case NationCodeEnum.UR_IN => {
        PAT_UR_IN;
      }
      case NationCodeEnum.VI => {
        PAT_VI;
      }
      case NationCodeEnum.NE_IN => {
        PAT_NE_IN;
      }
      case NationCodeEnum.MS_MY => {
        PAT_MS_MY;
      }
      case NationCodeEnum.MY => {
        PAT_MY;
      }
      case NationCodeEnum.IN_ID => {
        PAT_IN_ID;
      }
      case NationCodeEnum.SA_IN => {
        PAT_SA_IN;
      }
      case NationCodeEnum.SD_IN => {
        PAT_SD_IN;
      }

      /**
        * 印度语系列 unicode 字符----End
        */

      case NationCodeEnum.TR_TR => {
        PAT_TR_TR;
      }

      case NationCodeEnum.FA_IR => {
        PAT_FA_IR;
      }

      case NationCodeEnum.SU_SU => {
        PAT_SU_SU;
      }

      case NationCodeEnum.JV_JV => {
        PAT_JV_JV;
      }

      case NationCodeEnum.TL_PH => {
        PAT_TL_PH;
      }

      case NationCodeEnum.AM_ET => {
        PAT_AM_ET;
      }

      case NationCodeEnum.KM_KH => {
        PAT_KM_KH;
      }

      case NationCodeEnum.SW_SW => {
        PAT_SW_SW;
      }

      case NationCodeEnum.HA => {
        PAT_HA;
      }

      case NationCodeEnum.NL => {
        PAT_NL;
      }

      case NationCodeEnum.EL_GRC => {
        PAT_EL_GRC;
      }
      case NationCodeEnum.BO_CN => {
        PAT_BO_CN;
      }
      case NationCodeEnum.KK_KZ => {
        PAT_KK_KZ;
      }
      case NationCodeEnum.RO_ROU => {
        PAT_RO_ROU;
      }
      case NationCodeEnum.SV_SWE => {
        PAT_SV_SWE;
      }
      case NationCodeEnum.NL_ZAF => {
        PAT_NL_ZAF;
      }
      case NationCodeEnum.CS_CZK => {
        PAT_CS_CZK;
      }
      case NationCodeEnum.HU_HUN => {
        PAT_HU_HUN;
      }
      case NationCodeEnum.PL_POL => {
        PAT_PL_POL;
      }
      case NationCodeEnum.NO_NOR => {
        PAT_NO_NOR;
      }

      case _ => {
        null;
      }

    }
  }

}
