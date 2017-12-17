import com.kikatech.engine.ngram.util.CharacterUtil;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by huminghe on 2017/11/28.
 */
public class PallCountTest {

    public static void main(String[] args) {

        String origin = "/Users/xm/Downloads/punjabi/";

        String result = "/Users/xm/Downloads/punjabi/punjabi_count.txt";

        int count = 0;

        int count1 = 0;

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(result));

            Map<String, Integer> pairMap = new HashMap<String, Integer>();

            for (int i = 1; i < 51; i++) {

                String fileName = origin + "punglish" + i + ".txt";

                try {
                    FileReader fileReader = new FileReader(fileName);
                    BufferedReader bufferedReader = new BufferedReader(fileReader);

                    String line;

                    while ((line = bufferedReader.readLine()) != null) {

                        if (StringUtils.isNotBlank(line)) {
                            String line2 = bufferedReader.readLine();
                            if (StringUtils.isNotBlank(line2)) {
                                String line3 = bufferedReader.readLine();
                                if (StringUtils.isNotBlank(line3)) {
                                    String line4 = bufferedReader.readLine();
                                    String latinSentenceOrigin = line2;
                                    String indiaSentence = line3;
                                    String latinSentenceNew = line2;

                                    Pattern filter = Pattern.compile("[a-zA-Z]");
                                    Matcher m = filter.matcher(line3);
                                    if (m.find()) {
                                        latinSentenceNew = line3;
                                    } else {
                                        indiaSentence = line3;
                                    }

                                    if (StringUtils.isNotBlank(line4)) {
                                        indiaSentence = line4;
                                        String line5 = bufferedReader.readLine();
                                        if (StringUtils.isNotBlank(line5)) {
                                            indiaSentence = line5;
                                            String line6 = bufferedReader.readLine();
                                            if (StringUtils.isNotBlank(line6)) {
                                                continue;
                                            }
                                        }
                                    }

                                    latinSentenceOrigin = latinSentenceOrigin.toLowerCase().trim().replaceAll("\t", " ").replaceAll(CharacterUtil.CONSTANT_ONLINE_PUNCTUATION(), " ")
                                            .replaceAll("\\s+", " ");
                                    latinSentenceNew = latinSentenceNew.toLowerCase().trim().replaceAll("\t", " ").replaceAll(CharacterUtil.CONSTANT_ONLINE_PUNCTUATION(), " ")
                                            .replaceAll("\\s+", " ");
                                    indiaSentence = indiaSentence.trim().replaceAll("\t", " ").replaceAll(CharacterUtil.CONSTANT_ONLINE_PUNCTUATION(), " ")
                                            .replaceAll("\\s+", " ");

                                    Pattern latinFilter = Pattern.compile("[^a-zA-Z\\s]");
                                    Matcher nonLatinMatcher = latinFilter.matcher(latinSentenceOrigin);
                                    Matcher nonLatinMatcher2 = latinFilter.matcher(latinSentenceNew);

                                    Pattern indiaFilter = Pattern.compile("[a-zA-Z]");
                                    Matcher nonIndiaMatcher = indiaFilter.matcher(indiaSentence);

                                    if (!nonLatinMatcher.find() && !nonIndiaMatcher.find() && !nonLatinMatcher2.find()) {
                                        count1 ++;
                                        String[] latinWordsArray1 = latinSentenceOrigin.split(" ");
                                        String[] latinWordsArray2 = latinSentenceNew.split(" ");
                                        String[] indiaWordsArray = indiaSentence.split(" ");
                                        if (latinWordsArray1.length == latinWordsArray2.length && latinWordsArray1.length == indiaWordsArray.length) {
                                            for (int k = 0; k < latinWordsArray1.length; k++) {
                                                String latinWord = latinWordsArray1[k];
                                                String latinWordNew = latinWordsArray2[k];
                                                String indiaWord = indiaWordsArray[k];
                                                if (StringUtils.isNotBlank(latinWord)) {
                                                    String key = latinWord + "\t" + latinWordNew + "\t" + indiaWord;
                                                    if (pairMap.containsKey(key)) {
                                                        pairMap.put(key, pairMap.get(key) + 1);
                                                    } else {
                                                        pairMap.put(key, 1);
                                                    }
                                                    count++;
                                                }
                                            }
                                        }
                                    }

                                }
                            }
                        }

                    }
                    fileReader.close();

                } catch (Exception e) {

                    System.out.println("hahaha" + i);
                }
            }

            List<Map.Entry<String, Integer>> list = new ArrayList<Map.Entry<String, Integer>>(pairMap.entrySet());

            Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
                public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                    return o2.getValue().compareTo(o1.getValue());
                }
            });

            for (Map.Entry<String, Integer> entry : list) {
                String key = entry.getKey();
                Integer num = entry.getValue();
                writer.write(key + "\t" + num);
                writer.newLine();
            }
            writer.close();

            System.out.println(count);
            System.out.println(count1);

        } catch (Exception e) {
            System.out.println("hahaha");
        }


    }
}
