import com.kikatech.engine.ngram.util.CharacterUtil;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by huminghe on 2017/11/8.
 */
public class WordsFreGetTest {

    public static void main(String[] args) {

        String origin = "/Users/xm/Desktop/kn/kn-word-pair-lowFre-2.txt";

        String result = "/Users/xm/Desktop/kn/word-fre-result.txt";

        String indiaWordFreFile = "/Users/xm/Documents/projects/hinglish/data/seq2word/india-words-kn.txt";

        String latinWordFreFile = "/Users/xm/Documents/projects/hinglish/data/seq2word/latin-words-kn.txt";

        Map<String, Integer> latinWordFre = getWordFre(latinWordFreFile);

        Map<String, Integer> indiaWordFre = getWordFre(indiaWordFreFile);

        try {
            FileReader fileReader = new FileReader(origin);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            BufferedWriter writer = new BufferedWriter(new FileWriter(result));

            String line;

            while ((line = bufferedReader.readLine()) != null) {
                String[] pair = line.split("\t");
                if (pair.length == 2) {
                    String latinWord = pair[0];
                    String indiaWord = pair[1];

                    Integer latinFre = latinWordFre.getOrDefault(latinWord, 0);
                    Integer indiaFre = indiaWordFre.getOrDefault(indiaWord, 0);

                    writer.write(latinWord + "\t" + indiaWord + "\t" + latinFre + "\t" + indiaFre);
                    writer.newLine();

                }
            }
            writer.close();
            bufferedReader.close();

        } catch (Exception e) {

            System.out.println("asdfasdf");
        }
    }

    private static Map<String, Integer> getWordFre(String path) {

        File file = new File(path);
        Map<String, Integer> wordFre = new HashMap<>();

        try {

            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                String[] wordArray = line.split(CharacterUtil.CONSTANT_TAB());
                if (wordArray.length >= 1) {
                    wordFre.put(wordArray[0], Integer.valueOf(wordArray[1]));
                }
            }

            fileReader.close();

        } catch (Exception e) {
            System.out.println("hahaha");
        }

        return wordFre;

    }
}
