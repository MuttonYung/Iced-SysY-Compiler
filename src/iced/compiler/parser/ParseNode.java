package iced.compiler.parser;

import iced.compiler.lexer.Token;
import iced.compiler.sematics.Block;
import iced.compiler.sematics.Operator;
import iced.compiler.sematics.Symbol;

import java.util.ArrayList;
import java.util.List;

public class ParseNode {
    private final Token token;
    private ParseNode father;
    private final List<ParseNode> children=new ArrayList<>();
    private final List<Operator> expressions=new ArrayList<>();
    private Block block;
    private Symbol symbol;
    private Operator operator;

    public void addOperator(Operator op) {
        expressions.add(op);
    }

    public List<Operator> getExpressions() {
        return expressions;
    }

    public ParseNode(Token token){
        this.token = token;
    }
    public ParseNode(int code){
        this.token =new Token("",code);
    }

    public Token getToken() {
        return token;
    }

    public Block getBlock() {
        return block;
    }

    public void setBlock(Block block) {
        this.block = block;
    }


    public ParseNode getFather() {
        return father;
    }

    public void setFather(ParseNode father) {
        this.father = father;
    }

    public List<ParseNode> getChildren() {
        return children;
    }

    public void child(ParseNode node){
        node.setFather(this);
        children.add(node);
    }

    public void father(ParseNode node){
        node.child(this);
    }

    public boolean isLeaf(){
        return getChildren().size()==0;
    }

    public Symbol getSymbol() {
        return symbol;
    }

    public void setSymbol(Symbol symbol) {
        this.symbol = symbol;
    }

    public Operator getOperator() {
        return operator;
    }

    public void setOperator(Operator operator) {
        this.operator = operator;
    }
}
