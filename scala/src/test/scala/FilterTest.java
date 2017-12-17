import com.kikatech.engine.ngram.util.CharacterUtil;
import org.apache.commons.lang3.StringUtils;

import java.io.*;

/**
 * Created by huminghe on 2017/7/18.
 */
public class FilterTest {

    public static void main(String[] args) {

        String origin = "/Users/xm/Documents/emoji/data_toge/emoji_vocabulary.txt";

        String result = "/Users/xm/Documents/emoji/data_toge/emoji_vocabulary_idx.txt";

        try {
            FileReader fileReader = new FileReader(origin);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            BufferedWriter writer = new BufferedWriter(new FileWriter(result));

            String line;

            int idx = 0;

            while ((line = bufferedReader.readLine()) != null) {
                // line = line.replaceAll("[\\S]+@[\\S]+", "");
                // String r = line.replaceAll(" ", "\t");
                String r = line.split("\t")[0];
                // r = r.replaceAll("^[0-9\\t\\-\\s]+$", "");
                if (StringUtils.isNotBlank(r)) {
                    writer.write(r + "\t" + idx);
                    writer.newLine();
                }
                idx ++;
            }
            writer.close();
            bufferedReader.close();

        } catch (Exception e) {

        }
    }

}
