import com.kikatech.engine.ngram.util.CharacterUtil;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.*;

/**
 * Created by huminghe on 2017/8/29.
 */
public class SentencesMergeTest {

    public static void main(String[] args) {
        String filePath = "/Users/xm/Documents/tmp/corpus/telugu11/";

        String result = "/Users/xm/Documents/tmp/corpus/telugu11/telugu_sentences.txt";

        Set<String> sentencesSet = new HashSet<String>();


        for (int j = 1; j < 13; j++) {

            String fileName = filePath + j + "/sentences.txt";

            try {
                File file = new File(fileName);

                FileReader fileReader = new FileReader(file);

                BufferedReader bufferedReader = new BufferedReader(fileReader);

                String line;

                while ((line = bufferedReader.readLine()) != null) {


                    int num = 0;
                    String r = line.replaceAll(CharacterUtil.CONSTANT_ONLINE_PUNCTUATION(), " ");
                    r = r.replaceAll("^[0-9\\t\\-\\s]+$", "");
                    String[] aa = r.trim().split(" ");
                    String cc = "";
                    for (String a : aa) {
                        if (StringUtils.isNotBlank(a)) {
                            num ++;
                            cc += a + " ";

                        }
                    }
                    if (num > 2) {
                        sentencesSet.add(cc.trim());
                    }

                }


            } catch (Exception e) {
                System.out.print("hahaha");
            }

            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter(result));

                List<String> aa = new ArrayList<String>();

                aa.addAll(sentencesSet);

                Collections.shuffle(aa);

                Collections.shuffle(aa);

                for (String s : aa) {
                    writer.write(s);
                    writer.newLine();
                }

                writer.close();

            } catch (Exception e) {
                System.out.print("hahaha");
            }


        }
    }
}

