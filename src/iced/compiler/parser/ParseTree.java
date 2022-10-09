package iced.compiler.parser;

import java.util.ArrayList;
import java.util.List;

public class ParseTree {
    private ParseNode root;
    private List<ParseNode> list;
    public ParseTree(ParseNode root) {
        this.root = root;
    }

    public ParseNode getRoot() {
        return root;
    }

    public List<ParseNode> traversal() {
        list=new ArrayList<>();
        if(getRoot()!=null)
            traverse(getRoot());
        return list;
    }
    private void traverse(ParseNode node){
        if(!node.isLeaf()){
//            for(ParseNode child:node.getChildren()){
//                System.out.print(child.getSymbol().getType()+" ");
//            }
//            System.out.println();
            for(ParseNode child:node.getChildren())
                traverse(child);
        }
        list.add(node);
    }
}
