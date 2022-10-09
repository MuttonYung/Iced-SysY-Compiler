package iced.compiler.lexer;

import java.util.ArrayList;
import java.util.List;

public class SymbolStream {
    private int pointer=0;

    public int getPointer() {
        return pointer;
    }

    public void setPointer(int pointer) {
        this.pointer = pointer;
    }

//    public int getHead() {
//        return head;
//    }
//
//    public void setHead(int head) {
//        this.head = head;
//    }

//    private int head=0;
    private List<Symbol> symbolList=new ArrayList<>();
    public SymbolStream(){}
    public Symbol nextSymbol(){
        if(pointer<0 ||pointer>=symbolList.size())
            return null;
        return symbolList.get(pointer++);
    }
//    public void back(){
//        if(pointer>head)
//            pointer=head;
//    }
//    public void forward(){
//        if(pointer>head)
//            head=pointer;
//    }
    public void reset(){
        pointer=0;
    }
    public void push(Symbol symbol){
        symbolList.add(symbol);
    }
}
