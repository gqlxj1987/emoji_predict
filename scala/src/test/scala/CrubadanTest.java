import akka.japi.pf.Match;
import com.kikatech.engine.ngram.util.CharacterUtil;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by huminghe on 2017/10/17.
 */
public class CrubadanTest {

    public static void main(String[] args) {

        String origin = "/Users/xm/Desktop/dict_data/lv/lv-wordbigrams.txt";

        String result = "/Users/xm/Desktop/dict_data/lv/lv-bigram.txt";

        try {
            FileReader fileReader = new FileReader(origin);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            BufferedWriter writer = new BufferedWriter(new FileWriter(result));

            String line;

            line = bufferedReader.readLine();

            String[] array1 = line.split(" ");

            int length = array1.length;

            String maxStr = array1[length - 1];

            int max = new Integer(maxStr);

            int coeff = length > 2 ? 14 : 250;

            StringBuilder s = new StringBuilder();

            for (int i = 0; i < length - 1; i++) {
                s.append(array1[i]).append("\t");
            }
            s.append(coeff + 1);
            writer.write(s.toString());
            writer.newLine();

            while ((line = bufferedReader.readLine()) != null) {
                // line = line.replaceAll("[\\S]+@[\\S]+", "");

                String[] array = line.split(" ");
                if (array.length >= length) {
                    int fre = new Integer(array[length - 1]);
                    int freNew = fre * coeff / max + 1;
                    StringBuilder ss = new StringBuilder();

                    int skip = 0;


                    String cc = "[“”—\\-\\.\\\\0-9་`~\\\"<>\\(\\)`´\\[\\]\\^_\\.#\\*&" +
                            "\\u0082\\u0084\\u0085\\u0094\\u0093\\u0092\\u0091\\u0096\\u0097\\u0098" +
                            "\\u061B\\u066C\\u065C\\u061F" +
                            "＂、〈〉》《「」『』【】〝〞〟＇（）＜＞［］{}̀＾＿̀｀､･" +
                            "\\u00A0\\uFEFF\\u20B9\\u1680\\u180E\\u2000-\\u200B\\u202F\\u205F\\u3000" +
                            "།！？。!\\?,:;\\|\\u104A\\u104B\\u061F\\u065C\\u060C\\u066C\\u061F\\u06D4\\u066B\\u066C\\u061F\\u061F]";

                    Pattern filter = Pattern.compile(cc);
                    Matcher matcher = filter.matcher(array[0]);
                    if (matcher.find()) {
                        continue;
                    }
                    if (length > 2) {
                        Matcher matcher2 = filter.matcher(array[1]);
                        if (matcher2.find()) {
                            skip = 1;
                        }
                    }
                    if (skip == 1) {
                        continue;
                    }

                    for (int i = 0; i < length - 1; i++) {
                        ss.append(array[i]).append("\t");
                    }
                    ss.append(freNew);
                    writer.write(ss.toString());
                    writer.newLine();
                }
                // r = r.replaceAll("^[0-9\\t\\-\\s]+$", "");
            }
            writer.close();
            bufferedReader.close();

        } catch (Exception e) {

        }
    }

}
