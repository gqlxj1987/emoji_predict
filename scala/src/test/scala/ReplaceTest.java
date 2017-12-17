/**
 * Created by huminghe on 2017/12/6.
 */
public class ReplaceTest {

    public static void main(String[] args) {

        String aa = "ccccccaaacccac 12123:123   33.33as<><><>dfasdf;;;:::??? ?? ?? !!!! .,.,., ,,, 1 1 1 2  3 ";
        String bb = aa.replaceAll("[0-9]+\\S*", " num ").replaceAll("\\.+", " . ").replaceAll("\\?+", " ? ").replaceAll("!+", " ! ").replaceAll(",+", " , ").replaceAll(":+", " : ")
                .replaceAll(";+", " ; ").replaceAll("&+", " & ").replaceAll("[<>]+", " ");
        System.out.println(bb);
    }
}
