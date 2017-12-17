import com.kikatech.engine.ngram.util.CharacterUtil;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by huminghe on 2017/12/12.
 */
public class FilterNewTest {

    public static void main(String[] args) {

        String origin = "/Users/xm/Documents/emoji/emoji_test_more.txt";

        String result = "/Users/xm/Documents/emoji/emoji_test_filtered_more.txt";

        try {
            FileReader fileReader = new FileReader(origin);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            BufferedWriter writer = new BufferedWriter(new FileWriter(result));

            String line;

            while ((line = bufferedReader.readLine()) != null) {
                Pattern p = Pattern.compile(CharacterUtil.CONSTANT_EMOJI());
                Matcher m = p.matcher(line);
                if (true) {
                    line = line.replaceAll(CharacterUtil.CONSTANT_EMOJI(), " $0 ").replaceAll("\\s+", " ").trim();
                    writer.write(line);
                    writer.newLine();
                }
            }
            writer.close();
            bufferedReader.close();

        } catch (Exception e) {

        }
    }

}
