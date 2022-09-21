import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

/***
 * 词法分析 测试程序
 */
public class Main {
    /***
     * 主函数：不断从控制台读入字符串，并进行词法分析
     * @param args
     */
    public static void main(String[] args) {
        Scanner stdin=new Scanner(System.in);
        HashMap<String,String> wordSheet=new HashMap<>();
        //保留字单词表
        wordSheet.put("The","冠词");
        wordSheet.put("the","冠词");
        wordSheet.put("We","主语");
        wordSheet.put("He","主语");
        wordSheet.put("we","主语");
        wordSheet.put("he","主语");
        wordSheet.put("I","主语");
        wordSheet.put("big","形容词");
        wordSheet.put("ran","动词");
        wordSheet.put("sat","动词");
        wordSheet.put("ate","动词");
        wordSheet.put("peanut","名词");
        wordSheet.put("elephant","名词");
        Morphology morphology=new Morphology(wordSheet);
        while(true) {
            String str=stdin.nextLine();
            //将读入的字符串放入词法分析器中
            morphology.pushString(str+"\n");
            String word=morphology.nextWord();
            while(word!=Morphology.EOF){
                System.out.println(String.format("[%d]%s:%s",
//                        morphology.getCode(word),
                        morphology.getTypeName(word),
                        word
                        )
                );
                word=morphology.nextWord();
            }
        }
    }
}
