package iced.compiler.parser;

import iced.compiler.SysY;
import iced.compiler.lexer.Symbol;
import iced.compiler.lexer.SymbolStream;

public class Parser extends SysY {

    private Symbol symbol;
    private final SymbolStream symbolStream;
    private ParseTree parseTree;

    public ParseTree getParseTree() {
        return parseTree;
    }

    public Parser(SymbolStream symbolStream) {
        this.symbolStream = symbolStream;
    }

    private void nextSym(){
        symbol=symbolStream.nextSymbol();
    }

    public void start(){
        nextSym();
        parseTree=new ParseTree(CompUnit());
    }

    public void error(){
        throw new RuntimeException("symbol = "+symbol.getName());
    }
    public ParseNode CompUnit(){
        int head= symbolStream.getPointer()-1;
        ParseNode node=new ParseNode(CompUnit);
        ParseNode syntax;
        while((syntax=Decl())!=null)
            node.child(syntax);
        while((syntax=FuncDef())!=null)
            node.child(syntax);
        
        if((syntax=MainFuncDef())==null){
            error();
            symbolStream.setPointer(head);nextSym();
            return null;
        }
        node.child(syntax);
        return node;
    }

    public ParseNode Decl(){
        int head= symbolStream.getPointer()-1;
        ParseNode node=new ParseNode(Decl);
        ParseNode syntax;
        if((syntax=ConstDecl())==null)
            if((syntax=VarDecl())==null){
                symbolStream.setPointer(head);nextSym();
                return null;
            }
        node.child(syntax);
        return node;
    }
    public ParseNode ConstDecl(){
        int head= symbolStream.getPointer()-1;
        ParseNode node=new ParseNode(ConstDecl);
        ParseNode syntax;

        if((syntax=terminator(CONSTTK))==null){
            symbolStream.setPointer(head);nextSym();
            return null;
        }
        node.child(syntax);

        if((syntax=BType())==null){
            symbolStream.setPointer(head);nextSym();
            return null;
        }
        node.child(syntax);

        if((syntax=ConstDef())==null){
            symbolStream.setPointer(head);nextSym();
            return null;
        }
        node.child(syntax);

        while((syntax=terminator(COMMA))!=null){
            node.child(syntax);
            if((syntax=ConstDef())==null){
                symbolStream.setPointer(head);nextSym();
                return null;
            }
            node.child(syntax);
        }

        if((syntax=terminator(SEMICN))==null){
            symbolStream.setPointer(head);nextSym();
            return null;
        }
        node.child(syntax);
        return node;
    }
    public ParseNode VarDecl(){
        int head= symbolStream.getPointer()-1;
        ParseNode node=new ParseNode(VarDecl);
        ParseNode syntax;

        if((syntax=BType())==null){
            symbolStream.setPointer(head);nextSym();
            return null;
        }
        node.child(syntax);

        if((syntax=VarDef())==null){
            symbolStream.setPointer(head);nextSym();
            return null;
        }
        node.child(syntax);

        while((syntax=terminator(COMMA))!=null){
            node.child(syntax);
            if((syntax=VarDef())==null){
                symbolStream.setPointer(head);nextSym();
                return null;
            }
            node.child(syntax);
        }

        if((syntax=terminator(SEMICN))==null){
            symbolStream.setPointer(head);nextSym();
            return null;
        }
        node.child(syntax);
        return node;
    }

    public ParseNode BType(){
        int head= symbolStream.getPointer()-1;
        ParseNode node=new ParseNode(BType);
        ParseNode syntax;
        if((syntax=terminator(INTTK))==null){
            symbolStream.setPointer(head);nextSym();
            return null;
        }
        node.child(syntax);
        return node;
    }

    public ParseNode ConstDef(){
        int head= symbolStream.getPointer()-1;
        ParseNode node=new ParseNode(ConstDef);
        ParseNode syntax;
        if((syntax=terminator(IDENFR))==null){
            symbolStream.setPointer(head);nextSym();
            return null;
        }
        node.child(syntax);
        while((syntax=terminator(LBRACK))!=null){
            node.child(syntax);

            if((syntax=ConstExp())==null){
                symbolStream.setPointer(head);nextSym();
                return null;
            }
            node.child(syntax);

            if((syntax=terminator(RBRACK))==null){
                symbolStream.setPointer(head);nextSym();
                return null;
            }
            node.child(syntax);
        }
        if((syntax=terminator(ASSIGN))==null){
            symbolStream.setPointer(head);nextSym();
            return null;
        }
        node.child(syntax);
        if((syntax=ConstInitVal())==null){
            symbolStream.setPointer(head);nextSym();
            return null;
        }
        node.child(syntax);
        return node;
    }

    public ParseNode ConstInitVal(){
        int head= symbolStream.getPointer()-1;
        ParseNode node=new ParseNode(ConstInitVal);
        ParseNode syntax;
        if((syntax=ConstExp())!=null)
            node.child(syntax);
        else if((syntax=terminator(LBRACE))!=null){
            node.child(syntax);
            if((syntax=ConstInitVal())!=null){
                node.child(syntax);
                while((syntax=terminator(COMMA))!=null){
                    node.child(syntax);

                    if((syntax=ConstInitVal())==null){
                        symbolStream.setPointer(head);nextSym();
                        return null;
                    }
                    node.child(syntax);
                }
            }
            
            if((syntax=terminator(RBRACE))==null){
                symbolStream.setPointer(head);nextSym();
                return null;
            }
            node.child(syntax);
        }else{
            symbolStream.setPointer(head);nextSym();
            return null;
        }
        return node;
    }

    public ParseNode VarDef(){
        int head= symbolStream.getPointer()-1;
        ParseNode node=new ParseNode(VarDef);
        ParseNode syntax;
        if((syntax=terminator(IDENFR))==null){
            symbolStream.setPointer(head);nextSym();
            return null;
        }
        node.child(syntax);
        
        while((syntax=terminator(LBRACK))!=null){
            node.child(syntax);
            if((syntax=ConstExp())==null){
                symbolStream.setPointer(head);nextSym();
                return null;
            }
            node.child(syntax);
            if((syntax=terminator(RBRACK))==null){
                symbolStream.setPointer(head);nextSym();
                return null;
            }
            node.child(syntax);
        }
        if((syntax=terminator(ASSIGN))!=null){
            node.child(syntax);
            if((syntax=InitVal())==null){
                symbolStream.setPointer(head);nextSym();
                return null;
            }
            node.child(syntax);
        }
        return node;
    }

    public ParseNode InitVal(){
        int head= symbolStream.getPointer()-1;
        ParseNode node=new ParseNode(InitVal);
        ParseNode syntax;
        if((syntax=Exp())!=null)
            node.child(syntax);
        else if((syntax=terminator(LBRACE))!=null){
            node.child(syntax);
            if((syntax=InitVal())!=null){
                node.child(syntax);
                while((syntax=terminator(COMMA))!=null){
                    node.child(syntax);
                    if((syntax=InitVal())==null){
                        symbolStream.setPointer(head);nextSym();
                        return null;
                    }
                    node.child(syntax);
                }
            }
            if((syntax=terminator(RBRACE))==null){
                symbolStream.setPointer(head);nextSym();
                return null;
            }
            node.child(syntax);
        }else{
            symbolStream.setPointer(head);nextSym();
            return null;
        }
        return node;
    }
    public ParseNode FuncDef(){
        int head= symbolStream.getPointer()-1;
        ParseNode node=new ParseNode(FuncDef);
        ParseNode syntax;

        if((syntax=FuncType())==null){
            symbolStream.setPointer(head);nextSym();
            return null;
        }
        node.child(syntax);
        if((syntax=terminator(IDENFR))!=null)
            node.child(syntax);
        else {
            symbolStream.setPointer(head);nextSym();
            return null;
        }
        if((syntax=terminator(LPARENT))==null){
            symbolStream.setPointer(head);nextSym();
            return null;
        }
        node.child(syntax);
        
        if((syntax=FuncFParams())!=null)
            node.child(syntax);
        
        if((syntax=terminator(RPARENT))==null){
            symbolStream.setPointer(head);nextSym();
            return null;
        }
        node.child(syntax);
        
        if((syntax=Block())==null){
            symbolStream.setPointer(head);nextSym();
            return null;
        }
        node.child(syntax);
        return node;
    }
    public ParseNode MainFuncDef(){
        int head= symbolStream.getPointer()-1;
        ParseNode node=new ParseNode(MainFuncDef);
        ParseNode syntax;

        if((syntax=terminator(INTTK))==null){
            symbolStream.setPointer(head);nextSym();
            return null;
        }
        node.child(syntax);

        if((syntax=terminator(MAINTK))==null){
            symbolStream.setPointer(head);nextSym();
            return null;
        }
        node.child(syntax);

        if((syntax=terminator(LPARENT))==null){
            symbolStream.setPointer(head);nextSym();
            return null;
        }
        node.child(syntax);

        if((syntax=terminator(RPARENT))==null){
            symbolStream.setPointer(head);nextSym();
            return null;
        }
        node.child(syntax);

        if((syntax=Block())==null){
            symbolStream.setPointer(head);nextSym();
            return null;
        }
        node.child(syntax);
        return node;
    }
    public ParseNode FuncType(){
        int head= symbolStream.getPointer()-1;
        ParseNode node=new ParseNode(FuncType);
        ParseNode syntax;

        if((syntax=terminator(VOIDTK))==null
                &&(syntax=terminator(INTTK))==null){
            symbolStream.setPointer(head);nextSym();
            return null;
        }
        node.child(syntax);
        return node;
    }
    public ParseNode FuncFParams(){
        int head= symbolStream.getPointer()-1;
        ParseNode node=new ParseNode(FuncFParams);
        ParseNode syntax;

        if((syntax=FuncFParam())==null){
            symbolStream.setPointer(head);nextSym();
            return null;
        }
        node.child(syntax);

        while((syntax=terminator(COMMA))!=null){
            node.child(syntax);
            if((syntax=FuncFParam())==null){
                symbolStream.setPointer(head);nextSym();
                return null;
            }
            node.child(syntax);
        }
        return node;
    }
    public ParseNode FuncFParam(){
        int head= symbolStream.getPointer()-1;
        ParseNode node=new ParseNode(FuncFParam);
        ParseNode syntax;
        if((syntax=BType())==null){
            symbolStream.setPointer(head);nextSym();
            return null;
        }
        node.child(syntax);

        if((syntax=terminator(IDENFR))==null){
            symbolStream.setPointer(head);nextSym();
            return null;
        }
        node.child(syntax);

        if((syntax=terminator(LBRACK))!=null){
            node.child(syntax);
            if((syntax=terminator(RBRACK))==null){
                symbolStream.setPointer(head);nextSym();
                return null;
            }
            node.child(syntax);

            while((syntax=terminator(LBRACK))!=null){
                node.child(syntax);

                if((syntax=ConstExp())==null){
                    symbolStream.setPointer(head);nextSym();
                    return null;
                }
                node.child(syntax);

                if((syntax=terminator(RBRACK))==null){
                    symbolStream.setPointer(head);nextSym();
                    return null;
                }
                node.child(syntax);
            }
        }
        return node;
    }
    public ParseNode Block(){
        int head= symbolStream.getPointer()-1;
        ParseNode node=new ParseNode(Block);
        ParseNode syntax;

        if((syntax=terminator(LBRACE))==null){
            symbolStream.setPointer(head);nextSym();
            return null;
        }
        node.child(syntax);

        while((syntax=BlockItem())!=null)
            node.child(syntax);

        if((syntax=terminator(RBRACE))==null){
            symbolStream.setPointer(head);nextSym();
            return null;
        }
        node.child(syntax);
        return node;
    }
    public ParseNode BlockItem(){
        int head= symbolStream.getPointer()-1;
        ParseNode node=new ParseNode(BlockItem);
        ParseNode syntax;
        if((syntax=Stmt())==null
            &&(syntax=Decl())==null){
            symbolStream.setPointer(head);nextSym();
            return null;
        }
        node.child(syntax);
        return node;
    }

    public ParseNode Stmt(){
        int head= symbolStream.getPointer()-1;
        ParseNode node=new ParseNode(Stmt);
        ParseNode syntax;
        if((syntax=LVal())!=null){
            node.child(syntax);
            if((syntax=terminator(ASSIGN))!=null){
                node.child(syntax);
            }else {
                symbolStream.setPointer(head);nextSym();
                node=new ParseNode(Stmt);
                if((syntax=Exp())!=null){
                    node.child(syntax);
                    if((syntax=terminator(SEMICN))==null){
                        symbolStream.setPointer(head);nextSym();
                        return null;
                    }
                    node.child(syntax);
                    return node;
                }else{
                    symbolStream.setPointer(head);nextSym();
                    return null;
                }
            }
            if((syntax=Exp())!=null)
                node.child(syntax);
            else if((syntax=terminator(GETINTTK))!=null){
                node.child(syntax);
                if((syntax=terminator(LPARENT))==null){
                    symbolStream.setPointer(head);nextSym();
                    return null;
                }
                node.child(syntax);

                if((syntax=terminator(RPARENT))==null){
                    symbolStream.setPointer(head);nextSym();
                    return null;
                }
                node.child(syntax);
            }else{
                symbolStream.setPointer(head);nextSym();
                return null;
            }
            if((syntax=terminator(SEMICN))==null){
                symbolStream.setPointer(head);nextSym();
                return null;
            }
            node.child(syntax);
        }
        else if((syntax=terminator(SEMICN))!=null){
            node.child(syntax);
        }else if((syntax=Exp())!=null){
            node.child(syntax);
            if((syntax=terminator(SEMICN))==null){
                symbolStream.setPointer(head);nextSym();
                return null;
            }
            node.child(syntax);
        }else if((syntax=Block())!=null)
            node.child(syntax);
        else if((syntax=terminator(IFTK))!=null){
            node.child(syntax);
            if((syntax=terminator(LPARENT))==null){
                symbolStream.setPointer(head);nextSym();
                return null;
            }
            node.child(syntax);

            if((syntax=Cond())==null){
                symbolStream.setPointer(head);nextSym();
                return null;
            }
            node.child(syntax);

            if((syntax=terminator(RPARENT))==null){
                symbolStream.setPointer(head);nextSym();
                return null;
            }
            node.child(syntax);

            if((syntax=Stmt())==null){
                symbolStream.setPointer(head);nextSym();
                return null;
            }
            node.child(syntax);

            if((syntax=terminator(ELSETK))!=null){
                node.child(syntax);
                if((syntax=Stmt())==null){
                    symbolStream.setPointer(head);nextSym();
                    return null;
                }
                node.child(syntax);
            }
        }else if((syntax=terminator(WHILETK))!=null){
            node.child(syntax);
            if((syntax=terminator(LPARENT))==null){
                symbolStream.setPointer(head);nextSym();
                return null;
            }
            node.child(syntax);

            if((syntax=Cond())==null){
                symbolStream.setPointer(head);nextSym();
                return null;
            }
            node.child(syntax);

            if((syntax=terminator(RPARENT))==null){
                symbolStream.setPointer(head);nextSym();
                return null;
            }
            node.child(syntax);

            if((syntax=Stmt())==null){
                symbolStream.setPointer(head);nextSym();
                return null;
            }
            node.child(syntax);
        }else if((syntax=terminator(BREAKTK))!=null
                ||(syntax=terminator(CONTINUETK))!=null){
            node.child(syntax);

            if((syntax=terminator(SEMICN))==null){
                symbolStream.setPointer(head);nextSym();
                return null;
            }
            node.child(syntax);
        }else if((syntax=terminator(RETURNTK))!=null){
            node.child(syntax);
            if((syntax=Exp())!=null)
                node.child(syntax);

            if((syntax=terminator(SEMICN))==null){
                symbolStream.setPointer(head);nextSym();
                return null;
            }
            node.child(syntax);
        }else if((syntax=terminator(PRINTFTK))!=null){
            node.child(syntax);

            if((syntax=terminator(LPARENT))==null){
                symbolStream.setPointer(head);nextSym();
                return null;
            }
            node.child(syntax);

            if((syntax=terminator(STRCON))==null){
                symbolStream.setPointer(head);nextSym();
                return null;
            }
            node.child(syntax);

            while((syntax=terminator(COMMA))!=null){
                node.child(syntax);
                if((syntax=Exp())==null){
                    symbolStream.setPointer(head);nextSym();
                    return null;
                }
                node.child(syntax);
            }

            if((syntax=terminator(RPARENT))==null){
                symbolStream.setPointer(head);nextSym();
                return null;
            }
            node.child(syntax);

            if((syntax=terminator(SEMICN))==null){
                symbolStream.setPointer(head);nextSym();
                return null;
            }
            node.child(syntax);
        }else{
            symbolStream.setPointer(head);nextSym();
            return null;
        }
        return node;
    }
    public ParseNode Exp(){
        int head= symbolStream.getPointer()-1;
        ParseNode node=new ParseNode(Exp);
        ParseNode syntax;
        if((syntax=AddExp())==null){
            symbolStream.setPointer(head);nextSym();
            return null;
        }
        node.child(syntax);
        return node;
    }

    public ParseNode Cond(){
        int head= symbolStream.getPointer()-1;
        ParseNode node=new ParseNode(Cond);
        ParseNode syntax;
        if((syntax=LOrExp())==null){
            symbolStream.setPointer(head);nextSym();
            return null;
        }
        node.child(syntax);
        return node;
    }
    public ParseNode LVal(){
        int head= symbolStream.getPointer()-1;
        ParseNode node=new ParseNode(LVal);
        ParseNode syntax;
        if((syntax=terminator(IDENFR))==null){
            symbolStream.setPointer(head);nextSym();
            return null;
        }
        node.child(syntax);

        while((syntax=terminator(LBRACK))!=null){
            node.child(syntax);
            if((syntax=Exp())==null){
                symbolStream.setPointer(head);nextSym();
                return null;
            }
            node.child(syntax);

            if((syntax=terminator(RBRACK))==null){
                symbolStream.setPointer(head);nextSym();
                return null;
            }
            node.child(syntax);
        }
        return node;
    }
    public ParseNode PrimaryExp(){
        int head= symbolStream.getPointer()-1;
        ParseNode node=new ParseNode(PrimaryExp);
        ParseNode syntax;
        if((syntax=terminator(LPARENT))!=null){
            node.child(syntax);
            if((syntax=Exp())==null){
                symbolStream.setPointer(head);nextSym();
                return null;
            }
            node.child(syntax);

            if((syntax=terminator(RPARENT))==null){
                symbolStream.setPointer(head);nextSym();
                return null;
            }
            node.child(syntax);
        }
        else if((syntax=LVal())!=null)
            node.child(syntax);
        else if((syntax=Number())!=null)
            node.child(syntax);
        else{
            symbolStream.setPointer(head);nextSym();
            return null;
        }
        return node;
    }
    public ParseNode Number() {
        int head= symbolStream.getPointer()-1;
        ParseNode node = new ParseNode(Number);
        ParseNode syntax;
        if((syntax=terminator(INTCON))==null){
            symbolStream.setPointer(head);nextSym();
            return null;
        }
        node.child(syntax);
        return node;
    }
    public ParseNode UnaryExp(){
        int head= symbolStream.getPointer()-1;
        ParseNode node=new ParseNode(UnaryExp);
        ParseNode syntax;

        if((syntax=terminator(IDENFR))!=null){
            node.child(syntax);
            if((syntax=terminator(LPARENT))!=null) {
                node.child(syntax);
            }else{
                node=new ParseNode(UnaryExp);
                symbolStream.setPointer(head);nextSym();
                if((syntax=PrimaryExp())!=null) {
                    node.child(syntax);
                    return node;
                }else{
                    symbolStream.setPointer(head);nextSym();
                    return null;
                }
            }
            if((syntax=FuncRParams())!=null)
                node.child(syntax);
            if((syntax=terminator(RPARENT))==null) {
                symbolStream.setPointer(head);nextSym();
                return null;
            }
            node.child(syntax);
        }else if((syntax=PrimaryExp())!=null)
            node.child(syntax);
        else if((syntax=UnaryOp())!=null){
            node.child(syntax);

            if((syntax=UnaryExp())==null){
                symbolStream.setPointer(head);nextSym();
                return null;
            }
            node.child(syntax);
        }else {
            symbolStream.setPointer(head);nextSym();
            return null;
        }
        return node;
    }
    public ParseNode UnaryOp(){
        int head= symbolStream.getPointer()-1;
        ParseNode node=new ParseNode(UnaryOp);
        ParseNode syntax;
        if((syntax=terminator(PLUS))==null
                &&(syntax=terminator(MINU))==null
                &&(syntax=terminator(NOT))==null) {
            symbolStream.setPointer(head);nextSym();
            return null;
        }
        node.child(syntax);
        return node;
    }
    public ParseNode FuncRParams(){
        int head= symbolStream.getPointer()-1;
        ParseNode node=new ParseNode(FuncRParams);
        ParseNode syntax;

        if((syntax=Exp())==null){
                symbolStream.setPointer(head);nextSym();
                return null;
            }
            node.child(syntax);
        while((syntax=terminator(COMMA))!=null){
            node.child(syntax);
            if((syntax=Exp())==null){
                symbolStream.setPointer(head);nextSym();
                return null;
            }
            node.child(syntax);
        }
        return node;
    }
    public ParseNode MulExp(){
        int head= symbolStream.getPointer()-1;
        ParseNode node=new ParseNode(MulExp);
        ParseNode syntax;

        if((syntax=UnaryExp())==null){
                symbolStream.setPointer(head);nextSym();
                return null;
            }
            node.child(syntax);
        while((syntax=terminator(MULT))!=null
                ||(syntax=terminator(DIV))!=null
                ||(syntax=terminator(MOD))!=null){
            ParseNode node1=new ParseNode(MulExp);
            node1.child(node);
            node1.child(syntax);
            if((syntax=UnaryExp())==null){
                symbolStream.setPointer(head);nextSym();
                return null;
            }
            node1.child(syntax);
            node=node1;
        }
        return node;
    }
    public ParseNode AddExp(){
        int head= symbolStream.getPointer()-1;
        ParseNode node=new ParseNode(AddExp);
        ParseNode syntax;

        if((syntax=MulExp())==null){
            symbolStream.setPointer(head);nextSym();
            return null;
        }
        node.child(syntax);

        while((syntax=terminator(PLUS))!=null
                ||(syntax=terminator(MINU))!=null){
            ParseNode node1=new ParseNode(AddExp);
            node1.child(node);
            node1.child(syntax);
            if((syntax=MulExp())==null){
                symbolStream.setPointer(head);nextSym();
                return null;
            }
            node1.child(syntax);
            node=node1;
        }
        return node;
    }
    public ParseNode RelExp(){
        int head= symbolStream.getPointer()-1;
        ParseNode node=new ParseNode(RelExp);
        ParseNode syntax;

        if((syntax=AddExp())==null){
            symbolStream.setPointer(head);nextSym();
            return null;
        }
        node.child(syntax);

        while((syntax=terminator(LSS))!=null
                ||(syntax=terminator(LEQ))!=null
                ||(syntax=terminator(GRE))!=null
                ||(syntax=terminator(GEQ))!=null){
            ParseNode node1=new ParseNode(RelExp);
            node1.child(node);
            node1.child(syntax);
            if((syntax=AddExp())==null){
                symbolStream.setPointer(head);nextSym();
                return null;
            }
            node1.child(syntax);
            node=node1;
        }
        return node;
    }
    public ParseNode EqExp(){
        int head= symbolStream.getPointer()-1;
        ParseNode node=new ParseNode(EqExp);
        ParseNode syntax;

        if((syntax=RelExp())==null){
            symbolStream.setPointer(head);nextSym();
            return null;
        }
        node.child(syntax);
        while((syntax=terminator(EQL))!=null||(syntax=terminator(NEQ))!=null){
            ParseNode node1=new ParseNode(EqExp);
            node1.child(node);
            node1.child(syntax);
            if((syntax=RelExp())==null){
                symbolStream.setPointer(head);nextSym();
                return null;
            }
            node1.child(syntax);
            node=node1;
        }
        return node;
    }
    public ParseNode LAndExp(){
        int head= symbolStream.getPointer()-1;
        ParseNode node=new ParseNode(LAndExp);
        ParseNode syntax;

        if((syntax=EqExp())==null){
            symbolStream.setPointer(head);nextSym();
            return null;
        }
        node.child(syntax);
        while((syntax=terminator(AND))!=null){
            ParseNode node1=new ParseNode(LAndExp);
            node1.child(node);
            node1.child(syntax);

            if((syntax=EqExp())==null){
                symbolStream.setPointer(head);nextSym();
                return null;
            }
            node1.child(syntax);
            node=node1;
        }
        return node;
    }
    public ParseNode LOrExp(){
        int head= symbolStream.getPointer()-1;
        ParseNode node=new ParseNode(LOrExp);
        ParseNode syntax;

        if((syntax=LAndExp())==null){
                symbolStream.setPointer(head);nextSym();
                return null;
            }
            node.child(syntax);
        while((syntax=terminator(OR))!=null){
            ParseNode node1=new ParseNode(LOrExp);
            node1.child(node);
            node1.child(syntax);
            if((syntax=LAndExp())==null){
                symbolStream.setPointer(head);nextSym();
                return null;
            }
            node1.child(syntax);
            node=node1;
        }
        return node;
    }
    public ParseNode ConstExp(){
        int head= symbolStream.getPointer()-1;
        ParseNode node=new ParseNode(ConstExp);
        ParseNode syntax;

        if((syntax=AddExp())==null){
            symbolStream.setPointer(head);nextSym();
            return null;
        }
        node.child(syntax);
        return node;
    }
    public ParseNode terminator(int a){
        if(symbol==null||!isTerminator(a))
            return null;
        if(symbol.getCode()==a){
            ParseNode node=new ParseNode(symbol);
            nextSym();
            return node;
        }
        return null;
    }
}
