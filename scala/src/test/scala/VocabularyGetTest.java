import com.kikatech.engine.ngram.util.CharacterUtil;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by huminghe on 2017/9/1.
 */
public class VocabularyGetTest {

    public static void main(String[] args) {
        String path = "/Users/xm/Documents/tmp/word_new/new/konkani.txt";

        String addPath1 = "/Users/xm/Documents/tmp/word_new/malayalam/manglish_top_words_1102.txt";

        String addPath2 = "/Users/xm/Documents/tmp/word_new/malayalam/confirm/malayalam0922.txt";

        String addPath3 = "/Users/xm/Documents/tmp/word_new/malayalam/confirm/manglish_words_filtered_0925-4.txt";

        String addPath4 = "/Users/xm/Documents/tmp/word_new/punglish/confirm/punglish_1106.txt";

        String out = "/Users/xm/Documents/tmp/word_new/new/konkani_filtered.txt";

        String removePath = "/Users/xm/Documents/tmp/word_new/new/hindi_unigram.txt";

        String removePath2 = "/Users/xm/Documents/tmp/word_new/marathi/marathi_except_old.txt";

        String removePath3 = "/Users/xm/Documents/tmp/word_new/marathi/confirm/marathi_new.txt";

        String hinglishPath = "/Users/xm/Documents/tmp/word/india/filters/hinglish.txt";

        String englishPath = "/Users/xm/Documents/tmp/word/india/filters/english.txt";

        Set<String> words = getWordsSet(path);

        // words.addAll(getWordsSet(addPath1));
        // words.addAll(getWordsSet(addPath2));
        // words.addAll(getWordsSet(addPath3));
        // words.addAll(getWordsSet(addPath4));

        // words.removeAll(getWordsSet(englishPath));
        // words.removeAll(getWordsSet(hinglishPath));
        words.removeAll(getWordsSet(removePath));
        // words.removeAll(getWordsSet(removePath2));
        // words.removeAll(getWordsSet(removePath3));

        try {
            final BufferedWriter writer = new BufferedWriter(new FileWriter(out));
            for (String s : words) {
                writer.write(s);
                writer.newLine();
            }
            writer.close();
        } catch (Exception e) {
            System.out.println("sadfsda");
        }

    }

    private static Set<String> getWordsSet(String path) {

        File file = new File(path);
        Set<String> wordSet = new HashSet<String>();

        try {

            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                String[] wordArray = line.split(CharacterUtil.CONSTANT_TAB());
                if (wordArray.length >= 1) {
                    wordSet.add(wordArray[0].trim());
                }
            }

            fileReader.close();

        } catch (Exception e) {
            System.out.println("hahaha");
        }

        return wordSet;

    }

}
