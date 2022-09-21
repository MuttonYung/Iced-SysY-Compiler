import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;

/***
 * 词法分析 测试程序
 */
public class Compiler {
    public static String readAllBytes(FileInputStream fileIn) throws IOException {
        String after="";
        byte b=(byte) fileIn.read();
        while(b>=0){
            after+=(char)b;
            b=(byte) fileIn.read();
        }
        return after;
    }
    public static String stringStrip(String str){
        String after="";
        int begin=0,end=str.length()-1;
        while(end>=0&&Character.isWhitespace(str.charAt(end)))
            end--;
        while(begin<=end&&Character.isWhitespace(str.charAt(begin)))
            begin++;
        after+=str.substring(begin,end+1);
        return after;
    }
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
//        File wordsetf =new File("src/wordset.txt");
//        FileInputStream wordsetIn=new FileInputStream(wordsetf);
        String wordSetTxt="!\tNOT\t*\tMULT\t=\tASSIGN\n" +
                "&&\tAND\t/\tDIV\t;\tSEMICN\n" +
                "||\tOR\t%\tMOD\t,\tCOMMA\n" +
                "main\tMAINTK\twhile\tWHILETK\t<\tLSS\t(\tLPARENT\n" +
                "const\tCONSTTK\tgetint\tGETINTTK\t<=\tLEQ\t)\tRPARENT\n" +
                "int\tINTTK\tprintf\tPRINTFTK\t>\tGRE\t[\tLBRACK\n" +
                "break\tBREAKTK\treturn\tRETURNTK\t>=\tGEQ\t]\tRBRACK\n" +
                "continue\tCONTINUETK\t+\tPLUS\t==\tEQL\t{\tLBRACE\n" +
                "if\tIFTK\t-\tMINU\t!=\tNEQ\t}\tRBRACE\n" +
                "else\tELSETK\tvoid\tVOIDTK";
//        String words[]=new String(wordsetIn.readNBytes(Integer.MAX_VALUE)).split("\t|\n| ");
//        String words[]=readAllBytes(wordsetIn).split("\t|\n| ");
        String words[]=wordSetTxt.split("\t|\n| ");
        for(int i=0;i+1<words.length;i+=2){
//            System.out.println(words[i]);
//            wordSheet.put(words[i].strip(),words[i+1].strip());
            wordSheet.put(stringStrip(words[i]),stringStrip(words[i+1]));
        }
        Morphology morphology=new Morphology(wordSheet);

//        String str=new String(in.readNBytes(Integer.MAX_VALUE));
        String str=readAllBytes(in);
        //将读入的字符串放入词法分析器中
        morphology.pushString(PreOperate.removeComments(str));
        String word=morphology.nextWord();

        while(!word.equals(Morphology.EOF)){
//            System.out.println(String.format("%s:%s",
//                    morphology.getTypeName(word),
//                    word
//                    )
//            );
            result+=morphology.getTypeName(word)+" "+word+"\n";
            word=morphology.nextWord();
        }
//        out.write(result.strip().getBytes());
        out.write(stringStrip(result).getBytes());
//        }
        in.close();
        out.close();
    }
}
