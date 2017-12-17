import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.*;

/**
 * Created by huminghe on 2017/11/29.
 */
public class WordCountTest {

    public static void main(String[] args) {

        String origin = "/Users/xm/Desktop/ta/india-sentences.txt";

        String result = "/Users/xm/Desktop/ta/india-words-ta.txt";

        try {
            FileReader fileReader = new FileReader(origin);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            BufferedWriter writer = new BufferedWriter(new FileWriter(result));

            String line;

            Map<String, Integer> pairMap = new HashMap<String, Integer>();

            int count = 0;

            while ((line = bufferedReader.readLine()) != null) {

                String[] wordArray = line.split(" ");
                for (int i = 0; i < wordArray.length; i++) {
                    String word = wordArray[i];
                    if (StringUtils.isNotBlank(word)) {
                        if (pairMap.containsKey(word)) {
                            pairMap.put(word, pairMap.get(word) + 1);
                        } else {
                            pairMap.put(word, 1);
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

            for (Map.Entry<String, Integer> entry : list) {
                String key = entry.getKey();
                Integer num = entry.getValue();
                writer.write(key + "\t" + num);
                writer.newLine();
            }

            writer.close();
            bufferedReader.close();

            System.out.print(count);

        } catch (Exception e) {

            System.out.println("asdfasdf");
        }
    }
}
