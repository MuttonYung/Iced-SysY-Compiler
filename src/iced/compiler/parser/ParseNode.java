package iced.compiler.parser;

import iced.compiler.lexer.Symbol;

import java.util.ArrayList;
import java.util.List;

public class ParseNode {
    private Symbol symbol;
    private ParseNode father;
    private List<ParseNode> children=new ArrayList<>();

    public ParseNode(Symbol symbol){
        this.symbol=symbol;
    }
    public ParseNode(int code){
        this.symbol=new Symbol("",code);
    }


    public Symbol getSymbol() {
        return symbol;
    }

    public void setSymbol(Symbol symbol) {
        this.symbol = symbol;
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
        setFather(node);
        node.getChildren().add(this);
    }
    public boolean isLeaf(){
        return getChildren().size()==0;
    }
}
