package iced.compiler.sematics;

import javax.swing.plaf.synth.SynthButtonUI;
import java.util.ArrayList;
import java.util.List;

public class Block {
    private int dataSize;
    List<Symbol> symbolTable=new ArrayList<>();
    private String name;
    private Block father;
    private int layer;
    private List<Operator> operators=new ArrayList<>();

    public Block() {
        dataSize=16;
        layer=0;
    }
    public void addSymbol(Symbol symbol){
        symbol.setOffset(dataSize);
        symbolTable.add(symbol);
        dataSize+=symbol.getSize();
        symbol.setLayer(layer);
//        System.out.println(
//                "Layer "+layer+":"
//        );
//        System.out.println(
//                symbol.getOffset()+" : "+symbol.getName()
//                        +"("+symbol.getSize()+")"+" added"
//        );
//        System.out.println(
//                "current:"
//        );
//        for(Symbol symbol1:symbolTable)
//            System.out.println(
//                    symbol1.getOffset()+" : "+symbol1.getName()
//            );
    }
    public Symbol getSymbol(String name){
        for(Symbol symbol : symbolTable){
            if(symbol.getName().equals(name))
                return symbol;
        }
        if(father!=null)
            return father.getSymbol(name);
        return null;
    }
    public Symbol getSymbol(String name,boolean onlyThis){
        if(!onlyThis)
            return getSymbol(name);
        for(Symbol symbol : symbolTable){
            if(symbol.getName().equals(name))
                return symbol;
        }
        return null;
    }

    public Block getFather() {
        return father;
    }

    public void setFather(Block father) {
        this.father = father;
        this.layer= father.getLayer()+1;
    }

    public int getLayer() {
        return layer;
    }

    public int getDataSize() {
        return dataSize;
    }
}
