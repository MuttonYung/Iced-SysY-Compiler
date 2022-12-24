package iced.compiler.sematics;

import iced.compiler.SysY;
import iced.compiler.parser.ParseNode;
import iced.compiler.parser.ParseTree;

public class SymbolTable extends SysY {
    private ParseTree parseTree;
    private Block base;
    public SymbolTable(ParseTree parseTree) {
        this.parseTree = parseTree;
    }

    public void build(){
        base=new Block();
        traverse(parseTree.getRoot());
    }
    private void traverse(ParseNode node){
        if(node.getToken().getCode()== Block){
            if(!node.isLeaf()){
                for(ParseNode child:node.getChildren())
                    traverse(child);
            }
        }else if(node.getToken().getCode()== VarDecl){

        }else if(node.getToken().getCode()== ConstDecl){

        }else if(node.getToken().getCode()== FuncDef){

        }else if(node.getToken().getCode()== Stmt){

        }else if(node.getToken().getCode()== Block){

        }else if(node.getToken().getCode()== Block){

        }else if(node.getToken().getCode()== Block){

        }else if(node.getToken().getCode()== Block){

        }else if(node.getToken().getCode()== Block){

        }else if(node.getToken().getCode()== Block){

        }else if(node.getToken().getCode()== Block){

        }else if(node.getToken().getCode()== Block){

        }else if(node.getToken().getCode()== Block){

        }else if(node.getToken().getCode()== Block){

        }else if(node.getToken().getCode()== Block){

        }else if(node.getToken().getCode()== Block){

        }else if(node.getToken().getCode()== Block){

        }else if(node.getToken().getCode()== Block){

        }else if(node.getToken().getCode()== Block){

        }else if(node.getToken().getCode()== Block){

        }else if(node.getToken().getCode()== Block){

        }else if(node.getToken().getCode()== Block){

        }else if(node.getToken().getCode()== Block){

        }else if(node.getToken().getCode()== Block){

        }else if(node.getToken().getCode()== Block){

        }else if(node.getToken().getCode()== Block){

        }else if(node.getToken().getCode()== Block){

        }else if(node.getToken().getCode()== Block){

        }else if(node.getToken().getCode()== Block){

        }else if(node.getToken().getCode()== Block){

        }else if(node.getToken().getCode()== Block){

        }else if(node.getToken().getCode()== Block){

        }else{

        }
    }
    public ParseTree getParseTree() {
        return parseTree;
    }
}
