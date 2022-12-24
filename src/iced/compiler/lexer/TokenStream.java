package iced.compiler.lexer;

import java.util.ArrayList;
import java.util.List;

public class TokenStream {
    private int max=-1;
    private int pointer=0;
    public int getPointer() {
        return pointer;
    }
    public void setPointer(int pointer) {
        this.pointer = pointer;
    }
    private final List<Token> tokenList =new ArrayList<>();
    public TokenStream(){}
    public Token nextToken(){
        if(pointer<0 ||pointer>= tokenList.size())
            return null;
        if(max<0||pointer>max)
            max=pointer;
        return tokenList.get(pointer++);
    }
    public void reset(){
        pointer=0;
    }
    public void push(Token token){
        tokenList.add(token);
    }
    public int farest(){return max;}
    public Token getToken(int i){
        if(i<0 ||i>= tokenList.size())
            return null;
        return tokenList.get(i);
    }
    public Token lastToken(){return getToken(pointer-2);}
}
