package iced.compiler.lexer;

import iced.compiler.SysY;
import iced.compiler.error.Error;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;

public class Lexer extends SysY {
    private final TokenStream tokenStream;
    private String buff="";
    private int pointer=0;
    private int line=0;
    private final HashMap<String,Integer> symbolCode;
    private final HashMap<String,String> doubleOperator;
    private final BufferedReader bufferedReader;

    public static final String SAMPLE_OF_COMMENT="//COMMENT";
    public static final String EOF="\0";

    public Lexer(BufferedReader bufferedReader) {
        this.bufferedReader = bufferedReader;

        doubleOperator=new HashMap<>();
        doubleOperator.put(">","=");
        doubleOperator.put("<","=");
        doubleOperator.put("=","=");
        doubleOperator.put("!","=");
        doubleOperator.put("&","&");
        doubleOperator.put("|","|");

        tokenStream =new TokenStream();
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
     * Get nextWord from SymbolStream.
     * @return The next word, EOF if finished.
     */
    public String nextWord()throws IOException{
        StringBuilder token= new StringBuilder();
        char c=getChar();
        while(c!='\0'&&Character.isWhitespace(c))
            c=getChar();
        int offset = pointer,iline=line;
        String nonDigit = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ_";
        String numbers = "0123456789";
        if(nonDigit.contains(c+"")){
            token.append(c);
            c=getChar();
            while(nonDigit.contains(c+"")|| numbers.contains(c+"")){
                token.append(c);
                c=getChar();
            }
            unGetChar();

            if(!symbolCode.containsKey(token.toString())){
                symbolCode.put(token.toString(),IDENFR);
            }

        }else if(numbers.contains(c+"")){
            token.append(c);
            c=getChar();
            while(numbers.contains(c+"")){
                token.append(c);
                c=getChar();
            }
            unGetChar();
        }else if(c=='"'){
            token.append(c);
            c=getChar();
            while(c!='"'){
                token.append(c);
                c=getChar();
            }
            token.append(c);
        }else if(doubleOperator.containsKey(c+"")){
            char key0=c;
            token.append(c);
            c=getChar();
            if(doubleOperator.get(key0+"").equals(c+""))
                token.append(c);
            else
                unGetChar();
        }else if(c=='/'){
            token.append(c);
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
                token = new StringBuilder(SAMPLE_OF_COMMENT);
            }
            else if(c=='/'){
                nextLine();
                token = new StringBuilder(SAMPLE_OF_COMMENT);
            }
            else
                unGetChar();
        }
        else if(c=='\0')
            token = new StringBuilder(EOF);
        else token = new StringBuilder(c + "");
        if(!token.toString().equals(EOF)&&!token.toString().equals(SAMPLE_OF_COMMENT)){
            Token symbol=new Token(token.toString(),getCode(token.toString()));
            symbol.setLine(iline);
            symbol.setOffset(offset);
            tokenStream.push(symbol);
        }
        return token.toString();
    }

    public int getCode(String str){
        if(isInteger(str))
            return INTCON;
        else if(str.charAt(0)=='"')
            return STRCON;
        return symbolCode.get(str);
    }

    private void nextLine()throws IOException{
        buff=bufferedReader.readLine();
        line++;
        pointer=0;
    }

    private char getChar() throws IOException {
        if(buff==null)
            return '\0';
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

    private boolean isInteger(String str){
        return str.matches("[0-9]+");
    }

    public TokenStream getSymbolStream() {
        return tokenStream;
    }

}
