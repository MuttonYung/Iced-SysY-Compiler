import iced.compiler.SysY;
import iced.compiler.lexer.Symbol;
import iced.compiler.parser.ParseNode;
import iced.compiler.parser.Parser;
import iced.compiler.lexer.Lexer;

import java.io.*;
import java.util.List;

/***
 * Test Program
 */
public class Compiler {
    //Mission codes.
    private static final int NUN=0,LEXER=1,PARSER=2,ERROR=3;
    private static final int mission=PARSER;
    //String.strip() is not available in JDK1.8
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
     * Compile testfile.txt
     */
    public static void main(String[] args) throws IOException {
        File testf =new File("testfile.txt");
        File outf =new File("output.txt");
        FileOutputStream out=new FileOutputStream(outf);
        BufferedReader testfReader=new BufferedReader(new FileReader(testf));
        StringBuilder result= new StringBuilder();

        //Push all words to SymbolStream.
        Lexer lexer =new Lexer(testfReader);
        String word= lexer.nextWord();
        while(!word.equals(Lexer.EOF))
            word= lexer.nextWord();

        //Lexer test output
        if(mission==LEXER){
            Symbol symbol;
            while((symbol=lexer.getSymbolStream().nextSymbol())!=null){
                result.append(SysY.getType(symbol.getCode())).append(" ").append(symbol.getName()).append("\n");
            }

            out.write(stringStrip(result.toString()).getBytes());
            testfReader.close();
            out.close();
            return;
        }

        //Build and traverse the ParseTree.
        Parser parser =new Parser(lexer.getSymbolStream());
        parser.start();

        //Parser test output
        if(mission==PARSER){
            List<ParseNode> list=parser.getParseTree().traversal();
            for(ParseNode node:list){
                String name=node.getSymbol().getName();
                int code=node.getSymbol().getCode();
                if(code==SysY.BlockItem||code==SysY.Decl||code==SysY.BType)
                    continue;
                if(SysY.isTerminator(code))
                    result.append(SysY.getType(code)).append(" ").append(name).append("\n");
                else
                    result.append(SysY.getType(code)).append("\n");
            }

            out.write(stringStrip(result.toString()).getBytes());
            testfReader.close();
            out.close();
            return;
        }

        out.write(stringStrip(result.toString()).getBytes());
        testfReader.close();
        out.close();
    }
}
