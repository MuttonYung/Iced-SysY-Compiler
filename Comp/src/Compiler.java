import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

/***
 * 词法分析 测试程序
 */
public class Compiler {
    /***
     * 主函数：从文件读入字符串，并进行词法分析
     * @param args
     */
    public static void main(String[] args) throws IOException {
        File inf =new File("testfile.txt");
        File outf =new File("output.txt");

        FileInputStream in=new FileInputStream(inf);
        FileOutputStream out=new FileOutputStream(outf);
//        Scanner stdin=new Scanner(System.in);
        HashMap<String,String> wordSheet=new HashMap<>();
        String result="";
        //保留字单词表
        File wordsetf =new File("src/wordset.txt");
        FileInputStream wordsetIn=new FileInputStream(wordsetf);
        String words[]=new String(wordsetIn.readAllBytes()).split("\t|\n| ");
        for(int i=0;i+1<words.length;i+=2){
            System.out.println(words[i]);
            wordSheet.put(words[i].strip(),words[i+1].strip());
        }
        Morphology morphology=new Morphology(wordSheet);

        String str=new String(in.readAllBytes());
        //将读入的字符串放入词法分析器中
        morphology.pushString(str);
        String word=morphology.nextWord();

        while(!word.equals(Morphology.EOF)){
            System.out.println(String.format("%s:%s",
                    morphology.getTypeName(word),
                    word
                    )
            );
            result+=morphology.getTypeName(word)+" "+word+"\n";
            word=morphology.nextWord();
        }
        out.write(result.strip().getBytes());
//        }
    }
}
