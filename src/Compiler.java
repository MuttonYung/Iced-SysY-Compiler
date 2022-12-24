import iced.compiler.SysY;
import iced.compiler.error.Error;
import iced.compiler.lexer.Token;
import iced.compiler.parser.ParseNode;
import iced.compiler.parser.Parser;
import iced.compiler.lexer.Lexer;
import iced.compiler.sematics.CodeGenerate;
import iced.compiler.sematics.SemanticsAnalyser;

import java.io.*;
import java.util.Comparator;
import java.util.List;

/***
 * Test Program
 */
public class Compiler {
    //Mission codes.
    private static final int NUN=0,LEXER=1,PARSER=2,ERROR=3,SEMANTIC=4;
    private static final int mission=ERROR;
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
    static class ErrorSort implements Comparator<Error> {
        // override the compare() method
        public int compare(Error e1, Error e2)
        {
            if (e1.getLine() == e2.getLine()&&e1.getCode() == e2.getCode())
                return 0;
            else if (e1.getLine() > e2.getLine()||
                    (e1.getLine() == e2.getLine()&&e1.getCode() > e2.getCode()))
                return 1;
            else
                return -1;
        }
    }
    /***
     * Compile testfile.txt
     */
    public static void main(String[] args) throws IOException {
        File testf =new File("testfile.txt");
        BufferedReader testfReader=new BufferedReader(new FileReader(testf));
        StringBuilder result= new StringBuilder();

        //Push all words to SymbolStream.
        Lexer lexer =new Lexer(testfReader);
        String word= lexer.nextWord();
        while(!word.equals(Lexer.EOF))
            word= lexer.nextWord();

        //Lexer test output
        if(mission==LEXER){
            Token symbol;
            while((symbol=lexer.getSymbolStream().nextToken())!=null){
                result.append(SysY.getType(symbol.getCode())).append(" ").append(symbol.getName()).append("\n");
            }

            File outf =new File("output.txt");
            FileOutputStream outfOut=new FileOutputStream(outf);
            outfOut.write(stringStrip(result.toString()).getBytes());
            testfReader.close();
            outfOut.close();
            return;
        }

        //Build and traverse the ParseTree.
        Parser parser =new Parser(lexer.getSymbolStream());
        parser.start();

        //Parser test output
        if(mission==PARSER){
            List<ParseNode> list=parser.getParseTree().traversal();
            for(ParseNode node:list){
                String name=node.getToken().getName();
                int code=node.getToken().getCode();
                if(code==SysY.BlockItem||code==SysY.Decl||code==SysY.BType)
                    continue;
                if(SysY.isTerminator(code))
                    result.append(SysY.getType(code)).append(" ").append(name).append("\n");
                else
                    result.append(SysY.getType(code)).append("\n");
            }

            File outf =new File("output.txt");
            FileOutputStream outfOut=new FileOutputStream(outf);
            outfOut.write(stringStrip(result.toString()).getBytes());
            testfReader.close();
            outfOut.close();
            return;
        }


        SemanticsAnalyser semanticsAnalyser=new SemanticsAnalyser(parser.getParseTree());
        semanticsAnalyser.build();
        CodeGenerate codeGenerate=new CodeGenerate(semanticsAnalyser.getOperatorList());
        codeGenerate.build();
        //Semantics test output
        if(mission==SEMANTIC){
            for(String cmd:codeGenerate.getCodeList())
                result.append(cmd+'\n');
            File outf =new File("mips.txt");
            FileOutputStream outfOut=new FileOutputStream(outf);
            outfOut.write(stringStrip(result.toString()).getBytes());
            testfReader.close();
            outfOut.close();
            return;
        }


        //Error test output
        if(mission==ERROR){
            List<Error> errorList=SysY.getErrorList();
            errorList.sort(new ErrorSort());
            for(Error error:SysY.getErrorList())
                result.append(error.getLine()+" "+error.getCode()+"\n");
            File errorf =new File("error.txt");
            FileOutputStream errorfOut=new FileOutputStream(errorf);

            errorfOut.write(stringStrip(result.toString()).getBytes());
            testfReader.close();
            errorfOut.close();
            return;
        }
    }
}
