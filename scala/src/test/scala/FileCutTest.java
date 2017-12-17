import java.io.*;

/**
 * Created by huminghe on 2017/7/17.
 */
public class FileCutTest {

    public static void main(String[] args) {
        String filePath = "/Users/xm/Documents/tmp/corpus/telugu11/telguish/";

        String origin = "/Users/xm/Documents/tmp/corpus/telugu11/te_compare.txt";

        try {
            File file = new File(origin);

            FileReader fileReader = new FileReader(file);

            BufferedReader bufferedReader = new BufferedReader(fileReader);

            int i = 1;
            int count = 0;

            String line;

            String fileName = filePath + "telguish" + i + ".txt";

            BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));

            while ((line = bufferedReader.readLine()) != null) {
                count++;
                writer.write(line);
                writer.newLine();
                if (count == 32000) {
                    i++;
                    writer.close();
                    fileName = filePath + "telguish" + i + ".txt";
                    writer = new BufferedWriter(new FileWriter(fileName));
                    count = 0;
                }
            }
            writer.close();
            fileReader.close();

        } catch (Exception e) {
            System.out.print("hahaha");
        }

    }
}
