import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.util.MathUtils;

import java.io.*;
import java.util.*;

/**
 * Created by huminghe on 2017/10/28.
 */
public class WordsPaTest {

    public static void main(String[] args) {

        String origin = "/Users/xm/Desktop/kn/merge-sentences.txt";

        String result = "/Users/xm/Documents/projects/hinglish/data/seq2seq/kn-word-pair-new.txt";

        try {
            FileReader fileReader = new FileReader(origin);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            BufferedWriter writer = new BufferedWriter(new FileWriter(result));

            String line;

            Set<String> pairSet = new HashSet<String>();

            Map<String, Integer> pairMap = new HashMap<String, Integer>();

            int count = 0;

            while ((line = bufferedReader.readLine()) != null) {
                String[] sentenceArray = line.split("\t");
                if (sentenceArray.length == 2) {
                    String latinSentence = sentenceArray[0];
                    String indiaSentence = sentenceArray[1];
                    String[] latinWordsArray = latinSentence.split(" ");
                    String[] indiaWordsArray = indiaSentence.split(" ");
                    if (latinWordsArray.length == indiaWordsArray.length) {
                        count++;
                        for (int i = 0; i < latinWordsArray.length; i++) {
                            String latinWord = latinWordsArray[i];
                            String indiaWord = indiaWordsArray[i];
                            if (StringUtils.isNotBlank(latinWord)) {
                                pairSet.add(latinWord + "\t" + indiaWord);
                                String key = latinWord + "\t" + indiaWord;
                                if (pairMap.containsKey(key)) {
                                    pairMap.put(key, pairMap.get(key) + 1);
                                } else {
                                    pairMap.put(key, 1);
                                }
                            }
                        }
                    }
                }

            }

            /*
            for (String pair : pairSet) {
                writer.write(pair);
                writer.newLine();
            }
            */

            List<Map.Entry<String, Integer>> list = new ArrayList<Map.Entry<String, Integer>>(pairMap.entrySet());

            Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
                public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                    return o2.getValue().compareTo(o1.getValue());
                }
            });

            Set<String> latinWordSet = new HashSet<>();

            List<String> finalArray = new ArrayList<>();

            for (Map.Entry<String, Integer> entry : list) {
                String key = entry.getKey();
                String latinWord = key.split("\t")[0];
                Integer num = entry.getValue();
                double n = Math.ceil(Math.log(num));
                if (!latinWordSet.contains(latinWord)) {
                    n = n + 1;
                }

                for (int j = 0; j < n; j ++) {
                    finalArray.add(key);
                }
                latinWordSet.add(latinWord);
            }

            Collections.shuffle(finalArray);

            finalArray.forEach(x -> {
                try {
                    writer.write(x);
                    writer.newLine();
                } catch (Exception e) {

                }
            });

            writer.close();
            bufferedReader.close();

            System.out.print(count);

        } catch (Exception e) {

            System.out.println("asdfasdf");
        }
    }

}
