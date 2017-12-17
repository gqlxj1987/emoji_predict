import com.kikatech.engine.ngram.util.CharacterUtil;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by huminghe on 2017/10/27.
 */
public class ParallelTest {

    public static void main(String[] args) {

        String origin = "/Users/xm/Desktop/ta/";

        String latinSentences = "/Users/xm/Desktop/ta/latin-sentences.txt";

        String indiaSentences = "/Users/xm/Desktop/ta/india-sentences.txt";

        String mergeSentences = "/Users/xm/Desktop/ta/merge-sentences.txt";

        try {
            BufferedWriter writerLatin = new BufferedWriter(new FileWriter(latinSentences));

            BufferedWriter writerIndia = new BufferedWriter(new FileWriter(indiaSentences));

            BufferedWriter writerMerge = new BufferedWriter(new FileWriter(mergeSentences));

            for (int i = 1; i < 51; i++) {

                String fileName = origin + "tanglish" + i + ".txt";

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
                                    String latinSentence = line2;
                                    String indiaSentence = line3;

                                    Pattern filter = Pattern.compile("[a-zA-Z]");
                                    Matcher m = filter.matcher(line3);
                                    if (m.find()) {
                                        latinSentence = line3;
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

                                    latinSentence = latinSentence.toLowerCase().trim().replaceAll("\t", " ").replaceAll(CharacterUtil.CONSTANT_ONLINE_PUNCTUATION(), " ")
                                            .replaceAll("\\s+", " ");
                                    indiaSentence = indiaSentence.trim().replaceAll("\t", " ").replaceAll(CharacterUtil.CONSTANT_ONLINE_PUNCTUATION(), " ")
                                            .replaceAll("\\s+", " ");

                                    Pattern latinFilter = Pattern.compile("[^a-zA-Z\\s]");
                                    Matcher nonLatinMatcher = latinFilter.matcher(latinSentence);

                                    Pattern indiaFilter = Pattern.compile("[0-9a-zA-Z]");
                                    Matcher nonIndiaMatcher = indiaFilter.matcher(indiaSentence);

                                    if (!nonLatinMatcher.find() && !nonIndiaMatcher.find()) {
                                        writerLatin.write(latinSentence);
                                        writerLatin.newLine();
                                        writerIndia.write(indiaSentence);
                                        writerIndia.newLine();
                                        writerMerge.write(latinSentence + "\t" + indiaSentence);
                                        writerMerge.newLine();
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
            writerIndia.close();
            writerLatin.close();
            writerMerge.close();


        } catch (Exception e) {
            System.out.println("hahaha");
        }


    }
}
