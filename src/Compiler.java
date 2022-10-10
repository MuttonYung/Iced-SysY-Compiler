import iced.compiler.SysY;
import iced.compiler.lexer.Symbol;
import iced.compiler.parser.ParseNode;
import iced.compiler.parser.Parser;
import iced.compiler.lexer.Lexer;
import iced.compiler.PreOperate;

import java.io.*;
import java.util.HashMap;
import java.util.List;

/***
 * 词法分析 测试程序
 */
public class Compiler {
    //Mission codes.
    private static final int NUN=0,LEXER=1,PARSER=2,ERROR=3;
    private static final int mission=PARSER;
    public static String readAllBytes(FileInputStream fileIn) throws IOException {
        String after="";
        byte b=(byte) fileIn.read();
        while(b!=-1){
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
        File testf =new File("testfile.txt");
        File outf =new File("output.txt");

        FileInputStream testfIn=new FileInputStream(testf);
        FileOutputStream out=new FileOutputStream(outf);
        HashMap<String,String> wordSheet=new HashMap<>();
        BufferedReader testfReader=new BufferedReader(new FileReader(testf));
        String result="";
        //保留字单词表
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
        String words[]=wordSetTxt.split("\t|\n| ");
        for(int i=0;i+1<words.length;i+=2){
            String word=stringStrip(words[i]);
            String terminator=stringStrip(words[i+1]);
            wordSheet.put(word,terminator);

//            System.out.println("symbolCode.put(\""+word+"\","+terminator+");");
//            System.out.println("codeName.put("+terminator+",\""+terminator+"\");");
//            parser.getTerminator().add(terminator);
        }
        Lexer lexer =new Lexer(testfReader);
//        lexer.setWordType(wordSheet);
//        String str=readAllBytes(testfIn);
//        System.out.println(str);
        //将读入的字符串放入词法分析器中
//        lexer.pushString(PreOperate.removeComments(str));
        String word= lexer.nextWord();

        while(!word.equals(Lexer.EOF)){
//            result+= lexer.getTypeName(word)+" "+word+"\n";
            word= lexer.nextWord();
        }
        if(mission==LEXER){
            Symbol symbol;
            while((symbol=lexer.getSymbolStream().nextSymbol())!=null){
                result+= SysY.getType(symbol.getCode()) +" "+symbol.getName()
//                        +" "+symbol.getLine()+":"+symbol.getOffset()
                        +"\n";
            }
        }
//        Symbol sym;
//        result+="--DEBUGGING--\n";
//        while((sym=lexer.getSymbolStream().nextSymbol())!=null){
//            result+= sym.getType()+" "+sym.getName()+"\n";
//        }
//        result+="--FINISH--\n";
//        lexer.getSymbolStream().reset();

        Parser parser =new Parser(lexer.getSymbolStream());
        parser.start();
        List<ParseNode> list=parser.getParseTree().traversal();
        if(mission==PARSER)
            for(ParseNode node:list){
                String name=node.getSymbol().getName();
                int code=node.getSymbol().getCode();
                if(code==SysY.BlockItem||code==SysY.Decl||code==SysY.BType)
                    continue;
                if(SysY.isTerminator(code))
                    result+= SysY.getType(code)+" "+name+"\n";
                else
                    result+= SysY.getType(code)+"\n";

//                if(type.equals("<BlockItem>")||type.equals("<Decl>")
//                        ||type.equals("<Ident>")||type.equals("<BType>"))
//                    continue;
//                if(name=="")
//                    result+= type+"\n";
//                else
//                    result+= type+" "+name+"\n";
            }
        out.write(stringStrip(result).getBytes());
        testfReader.close();
        out.close();
    }
}
