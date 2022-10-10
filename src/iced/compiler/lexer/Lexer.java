package iced.compiler.lexer;

import iced.compiler.SysY;

import javax.imageio.IIOException;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

/***
 * 词法分析器
 */
public class Lexer extends SysY {
    public SymbolStream getSymbolStream() {
        return symbolStream;
    }

    public void setSymbolStream(SymbolStream symbolStream) {
        this.symbolStream = symbolStream;
    }

    private SymbolStream symbolStream;
    private String buff="";
    private int pointer=0;
    private String nonDigit="abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ_";
    private String numbers="0123456789";
    private int line=0,offset=0;
//    private String symbols="`,./;'[]\\-=~!@#$%^&*()<>?:\"{}|_+";
//    private HashMap<String,String> wordType;
    private HashMap<String,Integer> symbolCode;
    private HashMap<String,String> wordValue=new HashMap<>();
    private HashMap<String,String> doubleOperator;
    private BufferedReader bufferedReader;

    public static final String NAME_OF_INTEGER="INTCON";
    public static final String NAME_OF_IDENTIFIER="IDENFR";
    public static final String NAME_OF_STRING="STRCON";
    public static final String SAMPLE_OF_COMMENT="//COMMENT";
    public static final String EOF="\0";

//    public Lexer(){
//        wordType=new HashMap<>();
//        loadSymbolTypes();
//        symbolStream =new SymbolStream();
//    }
//    public Lexer(List<String> vocabulary){
//        wordType=new HashMap<>();
//        loadSymbolTypes();
//        symbolStream =new SymbolStream();
//    }
//    public Lexer(HashMap<String,String> wordType){
//        this.wordType=wordType;
//        loadSymbolTypes();
//        symbolStream =new SymbolStream();
//    }

    public Lexer(BufferedReader bufferedReader) {
        this.bufferedReader = bufferedReader;
        loadSymbolTypes();
        symbolStream =new SymbolStream();
        symbolCode=new HashMap<>();
        symbolCode.put("!",NOT);
        symbolCode.put("*",MULT);
        symbolCode.put("=",ASSIGN);
        symbolCode.put("&&",AND);
        symbolCode.put("/",DIV);
        symbolCode.put(";",SEMICN);
        symbolCode.put("||",OR);
        symbolCode.put("%",MOD);
        symbolCode.put(",",COMMA);
        symbolCode.put("main",MAINTK);
        symbolCode.put("while",WHILETK);
        symbolCode.put("<",LSS);
        symbolCode.put("(",LPARENT);
        symbolCode.put("const",CONSTTK);
        symbolCode.put("getint",GETINTTK);
        symbolCode.put("<=",LEQ);
        symbolCode.put(")",RPARENT);
        symbolCode.put("int",INTTK);
        symbolCode.put("printf",PRINTFTK);
        symbolCode.put(">",GRE);
        symbolCode.put("[",LBRACK);
        symbolCode.put("break",BREAKTK);
        symbolCode.put("return",RETURNTK);
        symbolCode.put(">=",GEQ);
        symbolCode.put("]",RBRACK);
        symbolCode.put("continue",CONTINUETK);
        symbolCode.put("+",PLUS);
        symbolCode.put("==",EQL);
        symbolCode.put("{",LBRACE);
        symbolCode.put("if",IFTK);
        symbolCode.put("-",MINU);
        symbolCode.put("!=",NEQ);
        symbolCode.put("}",RBRACE);
        symbolCode.put("else",ELSETK);
        symbolCode.put("void",VOIDTK);
    }

    /***
     * 获取该词法分析器中的下一个单词
     * @return 下一个单词
     */
    public String nextWord()throws IOException{
        String token="";
        char c=getChar();
        while(c!='\0'&&Character.isWhitespace(c))
            c=getChar();
        offset=pointer;
        if(nonDigit.contains(c+"")){
            token+=c;
            c=getChar();
            while(nonDigit.contains(c+"")||numbers.contains(c+"")){
                token+=c;
                c=getChar();
            }
            unGetChar();

            if(!symbolCode.keySet().contains(token)){
                symbolCode.put(token,IDENFR);
            }

//            return token;
        }else if(numbers.contains(c+"")){
            token+=c;
            c=getChar();
            while(numbers.contains(c+"")){
                token+=c;
                c=getChar();
            }
            unGetChar();
//            return token;
        }else if(c=='"'){
            token+=c;
            c=getChar();
            while(c!='"'){
                token+=c;
                c=getChar();
            }
            token+=c;

//            return token;
        }else if(doubleOperator.keySet().contains(c+"")){
            char key0=c;
            token+=c;
            c=getChar();
            if(doubleOperator.get(key0+"").equals(c+""))
                token+=c;
            else
                unGetChar();
//            return token;
        }else if(c=='/'){
            token+=c;
            c=getChar();
            if(c=='*') {
                boolean comment=true;
                while(comment){
                    c=getChar();
                    while(c=='*'){
                        c=getChar();
                        if(c=='/')
                            comment=false;
                    }
                }
                token=SAMPLE_OF_COMMENT;
            }
            else if(c=='/'){
                nextLine();
                token=SAMPLE_OF_COMMENT;
            }
            else
                unGetChar();
        }
        else if(c=='\0')
            token=EOF;
        else token=c+"";
        if(!token.equals(EOF)&&!token.equals(SAMPLE_OF_COMMENT)){
            Symbol symbol=new Symbol(token,getCode(token));
            symbol.setLine(line);
            symbol.setOffset(offset);
            symbolStream.push(symbol);
        }
        return token;
    }

//    public String getTypeName(String str){
//        if(isInteger(str))
//            return NAME_OF_INTEGER;
//        else if(str.charAt(0)=='"')
//            return NAME_OF_STRING;
//        return wordType.get(str);
//    }
    public int getCode(String str){
        if(isInteger(str))
            return INTCON;
        else if(str.charAt(0)=='"')
            return STRCON;
        return symbolCode.get(str);
    }
//    public void pushString(String str){
//        buff+=str;
//    }
    private void nextLine()throws IOException{
        buff=bufferedReader.readLine();
        line++;
        pointer=0;
    }
    private char getChar() throws IOException {
        if(pointer>=buff.length()){
            nextLine();
            if(buff==null)
                return '\0';
            else
                return '\n';
        }
        char c=buff.charAt(pointer);
        pointer++;
        return c;
    }
    private void unGetChar(){
        if(pointer>0)pointer--;
    }
    private void loadSymbolTypes(){
//        wordType.put(SAMPLE_OF_NUMBER,NAME_OF_INTEGER);
        doubleOperator=new HashMap<>();
        doubleOperator.put(">","=");
        doubleOperator.put("<","=");
        doubleOperator.put("=","=");
        doubleOperator.put("!","=");
        doubleOperator.put("&","&");
        doubleOperator.put("|","|");
    }
    private boolean isInteger(String str){
        return str.matches("[0-9]+");
    }

//    public HashMap<String, String> getWordType() {
//        return wordType;
//    }
//
//    public void setWordType(HashMap<String, String> wordType) {
//        this.wordType = wordType;
//    }
}
