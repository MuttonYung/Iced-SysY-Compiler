package iced.compiler.error;

import iced.compiler.lexer.Token;

public class Error {
    private final Token token;
    private final char code;

    public Error(Token token, char code) {
        this.token = token;
        this.code = code;
    }

    public Token getToken() {
        return token;
    }

    public int getLine(){
        return token.getLine();
    }

    public char getCode() {
        return code;
    }
}
