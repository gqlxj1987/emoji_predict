import com.kikatech.engine.ngram.tokenizer.ThaiTokenizer;
import com.kikatech.engine.ngram.util.CharacterUtil;

/**
 * Created by huminghe on 2017/8/30.
 */
public class TokenizerTest {

    public static void main(String[] args) {

        String thDocument = "ญี่ปุ่นควรระมัดระวังคำพูดและพฤติกรรมเกี่ยวกับปัญหาทะเลจีนใต้ \n"
                + "สำนักข่าวแห่งประเทศจีนรายงานว่า นายหง เหล่ย โฆษกกระทรวงการต่างประเทศจีนกล่าวเมื่อวันที่ 19 มกราคมว่า ญี่ปุ่นควรจดจำประวัติศาสตร์การรุกรานให้แม่นยำ สำนึกผิดอย่างยิ่ง และระมัดระวังคำพูดและพฤติกรรมเกี่ยวกับปัญหาทะเลจีนใต้ \n"
                + "นายชินโซ อาเบะ นายกรัฐมนตรีญี่ปุ่นกล่าวเมื่อวันที่ 18 มกราคมว่า ญี่ปุ่นสใส่ใจอย่างยิ่งต่อการที่จีนสร้างเกาะเทียมกลางทะเลจีนใต้ และทดลองบุกเบิกทรัพยากรทั้งน้ำมันและแก๊สธรรมชาติในทะเลจีนตะวันออก เรียกร้องประชาคมโลกแสดงความเห็นเกี่ยวกับเรื่องนี้มากขึ้น \n"
                + "นายหง เหล่ยกล่าวต่อการนี้ว่า การบุกเบิกน้ำมันและแก๊สธรรมชาติของจีน ล้วนกระทำในน่านน้ำทะเลที่อยู่ภายใต้การควบคุมของจีนเองโดยปราศจากข้อกังขา ทุกสิ่งทุกอย่างอยู่ในกรอบอธิปไตยของจีนเอง อนึ่ง จีนครองอธิปไตยเหนือหมู่เกาะหนานซาและน่านน้ำทะเลโดยรอบอย่างมิอาจโต้แย้งได้";

        // VietnameseTokenizer vietnameseTokenizer = new VietnameseTokenizer();


        String aa = "\uD83D\uDE4F\uD83C\uDFFD\uD83D\uDE4F\uD83D\uDE00\uD83E\uDD00\uD83C\uDFFB\uD83C\uDFFB\uD83C\uDFFC\uD83C\uDFFFaaa\uD83D\uDC86\uD83C\uDFFF";

        aa = aa.replaceAll(CharacterUtil.CONSTANT_EMOJI(), "h");

        System.out.println(aa);

        ThaiTokenizer tokenizer = new ThaiTokenizer();
        scala.collection.immutable.List list = tokenizer.tokenize(thDocument);
        System.out.println(list);
        scala.collection.immutable.List list2 = tokenizer.tokenizeNew(thDocument);
        System.out.println(list2);


    }
}
