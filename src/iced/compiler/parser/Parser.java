package iced.compiler.parser;
import iced.compiler.error.Error;
import iced.compiler.SysY;
import iced.compiler.lexer.Token;
import iced.compiler.lexer.TokenStream;

import java.util.List;

public class Parser extends SysY {

    private Token token;
    private final TokenStream tokenStream;
    private ParseTree parseTree;
    private static int tag=0;

    private int newTag(){
        return tag++;
    }

    public ParseTree getParseTree() {
        return parseTree;
    }

    public Parser(TokenStream tokenStream) {
        this.tokenStream = tokenStream;
    }

    private void nextSym(){
        token= tokenStream.nextToken();
    }

    public void start(){
        nextSym();
        parseTree=new ParseTree(CompUnit());
    }

    public void error(){
        throw new RuntimeException("Farset: "+tokenStream.farest());
    }
    public ParseNode CompUnit(){
        int head= tokenStream.getPointer()-1;
        ParseNode node=new ParseNode(CompUnit);
        ParseNode syntax;
        while((syntax=Decl())!=null)
            node.child(syntax);
        while((syntax=FuncDef())!=null)
            node.child(syntax);
        
        if((syntax=MainFuncDef())==null){
            error();
            tokenStream.setPointer(head);nextSym();
            return null;
        }
        node.child(syntax);
        return node;
    }

    public ParseNode Decl(){
        int head= tokenStream.getPointer()-1;
        ParseNode node=new ParseNode(Decl);
        ParseNode syntax;
        if((syntax=ConstDecl())==null)
            if((syntax=VarDecl())==null){
                tokenStream.setPointer(head);nextSym();
                return null;
            }
        node.child(syntax);
        return node;
    }
    public ParseNode ConstDecl(){
        int head= tokenStream.getPointer()-1;
        ParseNode node=new ParseNode(ConstDecl);
        ParseNode syntax;

        if((syntax=terminator(CONSTTK))==null){
            tokenStream.setPointer(head);nextSym();
            return null;
        }
        node.child(syntax);

        if((syntax=BType())==null){
            tokenStream.setPointer(head);nextSym();
            return null;
        }
        node.child(syntax);

        if((syntax=ConstDef())==null){
            tokenStream.setPointer(head);nextSym();
            return null;
        }
        node.child(syntax);

        while((syntax=terminator(COMMA))!=null){
            node.child(syntax);
            if((syntax=ConstDef())==null){
                tokenStream.setPointer(head);nextSym();
                return null;
            }
            node.child(syntax);
        }
        if((syntax=terminator(SEMICN))==null){
            getErrorList().add(new Error(tokenStream.lastToken(),'i'));
        }
        else
            node.child(syntax);
        return node;
    }
    public ParseNode VarDecl(){
        int head= tokenStream.getPointer()-1;
        ParseNode node=new ParseNode(VarDecl);
        ParseNode type,vardef,syntax;

        if((type=BType())==null){
            tokenStream.setPointer(head);nextSym();
            return null;
        }
        node.child(type);

        if((vardef=VarDef())==null){
            tokenStream.setPointer(head);nextSym();
            return null;
        }
        node.child(vardef);

        while((syntax=terminator(COMMA))!=null){
            node.child(syntax);
            if((vardef=VarDef())==null){
                tokenStream.setPointer(head);nextSym();
                return null;
            }
            node.child(vardef);
        }
        if((syntax=terminator(LPARENT))!=null){
            tokenStream.setPointer(head);nextSym();
            return null;
        }else if((syntax=terminator(SEMICN))==null){
            getErrorList().add(new Error(tokenStream.lastToken(),'i'));
        }
        else
            node.child(syntax);
        return node;
    }

    public ParseNode BType(){
        int head= tokenStream.getPointer()-1;
        ParseNode node=new ParseNode(BType);
        ParseNode syntax;
        if((syntax=terminator(INTTK))==null){
            tokenStream.setPointer(head);nextSym();
            return null;
        }
        node.child(syntax);
        return node;
    }

    public ParseNode ConstDef(){
        int head= tokenStream.getPointer()-1;
        ParseNode node=new ParseNode(ConstDef);
        ParseNode syntax;
        if((syntax=Ident())==null){
            tokenStream.setPointer(head);nextSym();
            return null;
        }
        node.child(syntax);
        while((syntax=terminator(LBRACK))!=null){
            node.child(syntax);

            if((syntax=ConstExp())==null){
                tokenStream.setPointer(head);nextSym();
                return null;
            }
            node.child(syntax);

            if((syntax=terminator(RBRACK))==null){
                getErrorList().add(new Error(tokenStream.lastToken(),'k'));
//                tokenStream.setPointer(head);nextSym();
//                return null;
            }else
                node.child(syntax);
        }
        if((syntax=terminator(ASSIGN))==null){
            tokenStream.setPointer(head);nextSym();
            return null;
        }
        node.child(syntax);
        if((syntax=ConstInitVal())==null){
            tokenStream.setPointer(head);nextSym();
            return null;
        }
        node.child(syntax);
        return node;
    }

    public ParseNode ConstInitVal(){
        int head= tokenStream.getPointer()-1;
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
                        tokenStream.setPointer(head);nextSym();
                        return null;
                    }
                    node.child(syntax);
                }
            }
            
            if((syntax=terminator(RBRACE))==null){
                tokenStream.setPointer(head);nextSym();
                return null;
            }
            node.child(syntax);
        }else{
            tokenStream.setPointer(head);nextSym();
            return null;
        }
        return node;
    }

    public ParseNode VarDef(){
        int head= tokenStream.getPointer()-1;
        ParseNode node=new ParseNode(VarDef);
        ParseNode syntax;
        if((syntax=Ident())==null){
            tokenStream.setPointer(head);nextSym();
            return null;
        }
        node.child(syntax);
        
        while((syntax=terminator(LBRACK))!=null){
            node.child(syntax);
            if((syntax=ConstExp())==null){
                tokenStream.setPointer(head);nextSym();
                return null;
            }
            node.child(syntax);
            if((syntax=terminator(RBRACK))==null){
                getErrorList().add(new Error(tokenStream.lastToken(),'k'));
//                tokenStream.setPointer(head);nextSym();
//                return null;
            }else
                node.child(syntax);
        }
        if((syntax=terminator(ASSIGN))!=null){
            node.child(syntax);
            if((syntax=InitVal())==null){
                tokenStream.setPointer(head);nextSym();
                return null;
            }
            node.child(syntax);
        }
        return node;
    }

    public ParseNode InitVal(){
        int head= tokenStream.getPointer()-1;
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
                        tokenStream.setPointer(head);nextSym();
                        return null;
                    }
                    node.child(syntax);
                }
            }
            if((syntax=terminator(RBRACE))==null){
                tokenStream.setPointer(head);nextSym();
                return null;
            }
            node.child(syntax);
        }else{
            tokenStream.setPointer(head);nextSym();
            return null;
        }
        return node;
    }
    public ParseNode FuncDef(){
        int head= tokenStream.getPointer()-1;
        ParseNode node=new ParseNode(FuncDef);
        ParseNode syntax;

        if((syntax=FuncType())==null){
            tokenStream.setPointer(head);nextSym();
            return null;
        }
        node.child(syntax);
        if((syntax=Ident())!=null)
            node.child(syntax);
        else {
            tokenStream.setPointer(head);nextSym();
            return null;
        }
        if((syntax=terminator(LPARENT))==null){
            tokenStream.setPointer(head);nextSym();
            return null;
        }
        node.child(syntax);
        
        if((syntax=FuncFParams())!=null)
            node.child(syntax);
        
        if((syntax=terminator(RPARENT))==null){
            getErrorList().add(new Error(tokenStream.lastToken(),'j'));
//            tokenStream.setPointer(head);nextSym();
//            return null;
        }
        else
            node.child(syntax);
        
        if((syntax=Block())==null){
            tokenStream.setPointer(head);nextSym();
            return null;
        }
        node.child(syntax);
        return node;
    }
    public ParseNode MainFuncDef(){
        int head= tokenStream.getPointer()-1;
        ParseNode node=new ParseNode(MainFuncDef);
        ParseNode syntax;

        if((syntax=terminator(INTTK))==null){
            tokenStream.setPointer(head);nextSym();
            return null;
        }
        node.child(syntax);

        if((syntax=terminator(MAINTK))==null){
            tokenStream.setPointer(head);nextSym();
            return null;
        }
        node.child(syntax);

        if((syntax=terminator(LPARENT))==null){
            tokenStream.setPointer(head);nextSym();
            return null;
        }
        node.child(syntax);

        if((syntax=terminator(RPARENT))==null){
            getErrorList().add(new Error(tokenStream.lastToken(),'j'));
//            tokenStream.setPointer(head);nextSym();
//            return null;
        }else
            node.child(syntax);

        if((syntax=Block())==null){
            tokenStream.setPointer(head);nextSym();
            return null;
        }
        node.child(syntax);
        return node;
    }
    public ParseNode FuncType(){
        int head= tokenStream.getPointer()-1;
        ParseNode node=new ParseNode(FuncType);
        ParseNode syntax;

        if((syntax=terminator(VOIDTK))==null
                &&(syntax=terminator(INTTK))==null){
            tokenStream.setPointer(head);nextSym();
            return null;
        }
        node.child(syntax);
        return node;
    }
    public ParseNode FuncFParams(){
        int head= tokenStream.getPointer()-1;
        ParseNode node=new ParseNode(FuncFParams);
        ParseNode syntax;

        if((syntax=FuncFParam())==null){
            tokenStream.setPointer(head);nextSym();
            return null;
        }
        node.child(syntax);

        while((syntax=terminator(COMMA))!=null){
            node.child(syntax);
            if((syntax=FuncFParam())==null){
                tokenStream.setPointer(head);nextSym();
                return null;
            }
            node.child(syntax);
        }
        return node;
    }
    public ParseNode FuncFParam(){
        int head= tokenStream.getPointer()-1;
        ParseNode node=new ParseNode(FuncFParam);
        ParseNode syntax;
        if((syntax=BType())==null){
            tokenStream.setPointer(head);nextSym();
            return null;
        }
        node.child(syntax);

        if((syntax=Ident())==null){
            tokenStream.setPointer(head);nextSym();
            return null;
        }
        node.child(syntax);

        if((syntax=terminator(LBRACK))!=null){
            node.child(syntax);
            if((syntax=terminator(RBRACK))==null){
                getErrorList().add(new Error(tokenStream.lastToken(),'k'));
//                tokenStream.setPointer(head);nextSym();
//                return null;
            }else
                node.child(syntax);

            while((syntax=terminator(LBRACK))!=null){
                node.child(syntax);

                if((syntax=ConstExp())==null){
                    tokenStream.setPointer(head);nextSym();
                    return null;
                }
                node.child(syntax);

                if((syntax=terminator(RBRACK))==null){
                    getErrorList().add(new Error(tokenStream.lastToken(),'k'));
//                    tokenStream.setPointer(head);nextSym();
//                    return null;
                }else
                    node.child(syntax);
            }
        }
        return node;
    }
    public ParseNode Block(){
        int head= tokenStream.getPointer()-1;
            
        ParseNode node=new ParseNode(Block);
        ParseNode syntax;


        if((syntax=terminator(LBRACE))==null){
            tokenStream.setPointer(head);nextSym();
            return null;
        }
        node.child(syntax);

        while((syntax=BlockItem())!=null)
            node.child(syntax);

        if((syntax=terminator(RBRACE))==null){
            tokenStream.setPointer(head);nextSym();
            return null;
        }
        node.child(syntax);

        return node;
    }
    public ParseNode BlockItem(){
        int head= tokenStream.getPointer()-1;
        ParseNode node=new ParseNode(BlockItem);
        ParseNode syntax;
        if((syntax=Stmt())==null
            &&(syntax=Decl())==null){
            tokenStream.setPointer(head);nextSym();
            return null;
        }
        node.child(syntax);
        return node;
    }

    public ParseNode Stmt(){
        int head= tokenStream.getPointer()-1;
        ParseNode node=new ParseNode(Stmt);
        ParseNode syntax;
        if((syntax=LVal())!=null){
            node.child(syntax);
            if((syntax=terminator(ASSIGN))!=null){
                node.child(syntax);
            }else {
                tokenStream.setPointer(head);nextSym();
                node=new ParseNode(Stmt);
                if((syntax=Exp())!=null){
                    node.child(syntax);

                    if((syntax=terminator(SEMICN))==null){
                        getErrorList().add(new Error(tokenStream.lastToken(),'i'));
                    }
                    else
                        node.child(syntax);
                    return node;
                }else{
                    tokenStream.setPointer(head);nextSym();
                    return null;
                }
            }
            if((syntax=Exp())!=null)
                node.child(syntax);
            else if((syntax=terminator(GETINTTK))!=null){
                node.child(syntax);
                if((syntax=terminator(LPARENT))==null){
                    tokenStream.setPointer(head);nextSym();
                    return null;
                }
                node.child(syntax);

                if((syntax=terminator(RPARENT))==null){
                    getErrorList().add(new Error(tokenStream.lastToken(),'j'));
//                    tokenStream.setPointer(head);nextSym();
//                    return null;
                }else
                    node.child(syntax);
            }else{
                tokenStream.setPointer(head);nextSym();
                return null;
            }
            if((syntax=terminator(SEMICN))==null){
                getErrorList().add(new Error(tokenStream.lastToken(),'i'));
            }
            else
                node.child(syntax);
        }
        else if((syntax=terminator(SEMICN))!=null){
            node.child(syntax);
        }else if((syntax=Exp())!=null){
            node.child(syntax);
            if((syntax=terminator(SEMICN))==null){
                getErrorList().add(new Error(tokenStream.lastToken(),'i'));
            }
            else
                node.child(syntax);
        }else if((syntax=Block())!=null)
            node.child(syntax);
        else if((syntax=terminator(IFTK))!=null){
            node.child(syntax);
            if((syntax=terminator(LPARENT))==null){
                tokenStream.setPointer(head);nextSym();
                return null;
            }
            node.child(syntax);

            if((syntax=Cond())==null){
                tokenStream.setPointer(head);nextSym();
                return null;
            }
            node.child(syntax);

            if((syntax=terminator(RPARENT))==null){
                getErrorList().add(new Error(tokenStream.lastToken(),'j'));
//                tokenStream.setPointer(head);nextSym();
//                return null;
            }else
                node.child(syntax);

            if((syntax=Stmt())==null){
                tokenStream.setPointer(head);nextSym();
                return null;
            }
            node.child(syntax);

            if((syntax=terminator(ELSETK))!=null){
                node.child(syntax);
                if((syntax=Stmt())==null){
                    tokenStream.setPointer(head);nextSym();
                    return null;
                }
                node.child(syntax);
            }
        }else if((syntax=terminator(WHILETK))!=null){
            node.child(syntax);
            if((syntax=terminator(LPARENT))==null){
                tokenStream.setPointer(head);nextSym();
                return null;
            }
            node.child(syntax);

            if((syntax=Cond())==null){
                tokenStream.setPointer(head);nextSym();
                return null;
            }
            node.child(syntax);

            if((syntax=terminator(RPARENT))==null){
                getErrorList().add(new Error(tokenStream.lastToken(),'j'));
//                tokenStream.setPointer(head);nextSym();
//                return null;
            }else
                node.child(syntax);

            if((syntax=Stmt())==null){
                tokenStream.setPointer(head);nextSym();
                return null;
            }
            node.child(syntax);
        }else if((syntax=terminator(BREAKTK))!=null
                ||(syntax=terminator(CONTINUETK))!=null){
            node.child(syntax);
            if((syntax=terminator(SEMICN))==null){
                getErrorList().add(new Error(tokenStream.lastToken(),'i'));
            }
            else
                node.child(syntax);
        }else if((syntax=terminator(RETURNTK))!=null){
            node.child(syntax);
            if((syntax=Exp())!=null)
                node.child(syntax);
            if((syntax=terminator(SEMICN))==null){
                getErrorList().add(new Error(tokenStream.lastToken(),'i'));
            }
            else
                node.child(syntax);
        }else if((syntax=terminator(PRINTFTK))!=null){
            node.child(syntax);

            if((syntax=terminator(LPARENT))==null){
                tokenStream.setPointer(head);nextSym();
                return null;
            }
            node.child(syntax);

            if((syntax=terminator(STRCON))==null){
                tokenStream.setPointer(head);nextSym();
                return null;
            }
            node.child(syntax);

            while((syntax=terminator(COMMA))!=null){
                node.child(syntax);
                if((syntax=Exp())==null){
                    tokenStream.setPointer(head);nextSym();
                    return null;
                }
                node.child(syntax);
            }

            if((syntax=terminator(RPARENT))==null){
                getErrorList().add(new Error(tokenStream.lastToken(),'j'));
//                tokenStream.setPointer(head);nextSym();
//                return null;
            }else
                node.child(syntax);
            if((syntax=terminator(SEMICN))==null){
                getErrorList().add(new Error(tokenStream.lastToken(),'i'));
            }
            else
                node.child(syntax);
        }else{
            tokenStream.setPointer(head);nextSym();
            return null;
        }
        return node;
    }
    public ParseNode Exp(){
        int head= tokenStream.getPointer()-1;
        ParseNode node=new ParseNode(Exp);
        ParseNode syntax;
        if((syntax=AddExp())==null){
            tokenStream.setPointer(head);nextSym();
            return null;
        }
        node.child(syntax);
        return node;
    }

    public ParseNode Cond(){
        int head= tokenStream.getPointer()-1;
        ParseNode node=new ParseNode(Cond);
        ParseNode syntax;
        if((syntax=LOrExp())==null){
            tokenStream.setPointer(head);nextSym();
            return null;
        }
        node.child(syntax);
        return node;
    }
    public ParseNode LVal(){
        int head= tokenStream.getPointer()-1;
        ParseNode node=new ParseNode(LVal);
        ParseNode syntax;
        if((syntax=Ident())==null){
            tokenStream.setPointer(head);nextSym();
            return null;
        }
        node.child(syntax);

        while((syntax=terminator(LBRACK))!=null){
            node.child(syntax);
            if((syntax=Exp())==null){
                tokenStream.setPointer(head);nextSym();
                return null;
            }
            node.child(syntax);

            if((syntax=terminator(RBRACK))==null){
                getErrorList().add(new Error(tokenStream.lastToken(),'k'));
//                tokenStream.setPointer(head);nextSym();
//                return null;
            }else
                node.child(syntax);
        }
        return node;
    }
    public ParseNode PrimaryExp(){
        int head= tokenStream.getPointer()-1;
        ParseNode node=new ParseNode(PrimaryExp);
        ParseNode syntax;
        if((syntax=terminator(LPARENT))!=null){
            node.child(syntax);
            if((syntax=Exp())==null){
                tokenStream.setPointer(head);nextSym();
                return null;
            }
            node.child(syntax);

            if((syntax=terminator(RPARENT))==null){
                getErrorList().add(new Error(tokenStream.lastToken(),'j'));
//                tokenStream.setPointer(head);nextSym();
//                return null;
            }else
                node.child(syntax);
        }
        else if((syntax=LVal())!=null)
            node.child(syntax);
        else if((syntax=Number())!=null)
            node.child(syntax);
        else{
            tokenStream.setPointer(head);nextSym();
            return null;
        }
        return node;
    }
    public ParseNode Number() {
        int head= tokenStream.getPointer()-1;
        ParseNode node = new ParseNode(Number);
        ParseNode syntax;
        if((syntax=terminator(INTCON))==null){
            tokenStream.setPointer(head);nextSym();
            return null;
        }
        node.child(syntax);
        return node;
    }
    public ParseNode UnaryExp(){
        int head= tokenStream.getPointer()-1;
        ParseNode node=new ParseNode(UnaryExp);
        ParseNode syntax;

        if((syntax=Ident())!=null){
            node.child(syntax);
            if((syntax=terminator(LPARENT))!=null) {
                node.child(syntax);
            }else{
                node=new ParseNode(UnaryExp);
                tokenStream.setPointer(head);nextSym();
                if((syntax=PrimaryExp())!=null) {
                    node.child(syntax);
                    return node;
                }else{
                    tokenStream.setPointer(head);nextSym();
                    return null;
                }
            }
            if((syntax=FuncRParams())!=null)
                node.child(syntax);
            if((syntax=terminator(RPARENT))==null) {
                getErrorList().add(new Error(tokenStream.lastToken(),'j'));
//                tokenStream.setPointer(head);nextSym();
//                return null;
            }else
                node.child(syntax);
        }else if((syntax=PrimaryExp())!=null)
            node.child(syntax);
        else if((syntax=UnaryOp())!=null){
            node.child(syntax);

            if((syntax=UnaryExp())==null){
                tokenStream.setPointer(head);nextSym();
                return null;
            }
            node.child(syntax);
        }else {
            tokenStream.setPointer(head);nextSym();
            return null;
        }
        return node;
    }
    public ParseNode UnaryOp(){
        int head= tokenStream.getPointer()-1;
        ParseNode node=new ParseNode(UnaryOp);
        ParseNode syntax;
        if((syntax=terminator(PLUS))==null
                &&(syntax=terminator(MINU))==null
                &&(syntax=terminator(NOT))==null) {
            tokenStream.setPointer(head);nextSym();
            return null;
        }
        node.child(syntax);
        return node;
    }
    public ParseNode FuncRParams(){
        int head= tokenStream.getPointer()-1;
        ParseNode node=new ParseNode(FuncRParams);
        ParseNode syntax;

        if((syntax=Exp())==null){
                tokenStream.setPointer(head);nextSym();
                return null;
            }
            node.child(syntax);
        while((syntax=terminator(COMMA))!=null){
            node.child(syntax);
            if((syntax=Exp())==null){
                tokenStream.setPointer(head);nextSym();
                return null;
            }
            node.child(syntax);
        }
        return node;
    }
    public ParseNode MulExp(){
        int head= tokenStream.getPointer()-1;
        ParseNode node=new ParseNode(MulExp);
        ParseNode syntax;

        if((syntax=UnaryExp())==null){
                tokenStream.setPointer(head);nextSym();
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
                tokenStream.setPointer(head);nextSym();
                return null;
            }
            node1.child(syntax);
            node=node1;
        }
        return node;
    }
    public ParseNode AddExp(){
        int head= tokenStream.getPointer()-1;
        ParseNode node=new ParseNode(AddExp);
        ParseNode syntax;

        if((syntax=MulExp())==null){
            tokenStream.setPointer(head);nextSym();
            return null;
        }
        node.child(syntax);

        while((syntax=terminator(PLUS))!=null
                ||(syntax=terminator(MINU))!=null){
            ParseNode node1=new ParseNode(AddExp);
            node1.child(node);
            node1.child(syntax);
            if((syntax=MulExp())==null){
                tokenStream.setPointer(head);nextSym();
                return null;
            }
            node1.child(syntax);
            node=node1;
        }
        return node;
    }
    public ParseNode RelExp(){
        int head= tokenStream.getPointer()-1;
        ParseNode node=new ParseNode(RelExp);
        ParseNode syntax;

        if((syntax=AddExp())==null){
            tokenStream.setPointer(head);nextSym();
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
                tokenStream.setPointer(head);nextSym();
                return null;
            }
            node1.child(syntax);
            node=node1;
        }
        return node;
    }
    public ParseNode EqExp(){
        int head= tokenStream.getPointer()-1;
        ParseNode node=new ParseNode(EqExp);
        ParseNode syntax;

        if((syntax=RelExp())==null){
            tokenStream.setPointer(head);nextSym();
            return null;
        }
        node.child(syntax);
        while((syntax=terminator(EQL))!=null||(syntax=terminator(NEQ))!=null){
            ParseNode node1=new ParseNode(EqExp);
            node1.child(node);
            node1.child(syntax);
            if((syntax=RelExp())==null){
                tokenStream.setPointer(head);nextSym();
                return null;
            }
            node1.child(syntax);
            node=node1;
        }
        return node;
    }
    public ParseNode LAndExp(){
        int head= tokenStream.getPointer()-1;
        ParseNode node=new ParseNode(LAndExp);
        ParseNode syntax;

        if((syntax=EqExp())==null){
            tokenStream.setPointer(head);nextSym();
            return null;
        }
        node.child(syntax);
        while((syntax=terminator(AND))!=null){
            ParseNode node1=new ParseNode(LAndExp);
            node1.child(node);
            node1.child(syntax);

            if((syntax=EqExp())==null){
                tokenStream.setPointer(head);nextSym();
                return null;
            }
            node1.child(syntax);
            node=node1;
        }
        return node;
    }
    public ParseNode LOrExp(){
        int head= tokenStream.getPointer()-1;
        ParseNode node=new ParseNode(LOrExp);
        ParseNode syntax;

        if((syntax=LAndExp())==null){
                tokenStream.setPointer(head);nextSym();
                return null;
            }
            node.child(syntax);
        while((syntax=terminator(OR))!=null){
            ParseNode node1=new ParseNode(LOrExp);
            node1.child(node);
            node1.child(syntax);
            if((syntax=LAndExp())==null){
                tokenStream.setPointer(head);nextSym();
                return null;
            }
            node1.child(syntax);
            node=node1;
        }
        return node;
    }
    public ParseNode ConstExp(){
        int head= tokenStream.getPointer()-1;
        ParseNode node=new ParseNode(ConstExp);
        ParseNode syntax;

        if((syntax=AddExp())==null){
            tokenStream.setPointer(head);nextSym();
            return null;
        }
        node.child(syntax);
        return node;
    }
    public ParseNode terminator(int a){
        if(token==null||!isTerminator(a))
            return null;
        if(token.getCode()==a){
            ParseNode node=new ParseNode(token);
            nextSym();
            return node;
        }
        return null;
    }
    public ParseNode Ident(){
        if(token==null)
            return null;
        if(token.getCode()==IDENFR){
//            if(symbol==null)
//                return null;
            ParseNode node=new ParseNode(token);
            nextSym();
            return node;
        }
        return null;
    }
}
