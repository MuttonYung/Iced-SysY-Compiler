package iced.compiler.parser;

import iced.compiler.SysY;
import iced.compiler.lexer.Symbol;
import iced.compiler.lexer.SymbolStream;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Parser extends SysY {

//    private String sym="";
    private Symbol symbol;
    private String startSymbol="";
    private HashMap<String,String> rules=new HashMap<>();
    private List<String> terminator=new ArrayList<>();

    public Parser(SymbolStream symbolStream) {
        this.symbolStream = symbolStream;
    }

    private SymbolStream symbolStream=new SymbolStream();


    private ParseTree parseTree;

    public String getStartSymbol() {
        return startSymbol;
    }
    public ParseTree getParseTree() {
        return parseTree;
    }

    public void setStartSymbol(String startSymbol) {
        this.startSymbol = startSymbol;
    }

    public HashMap<String, String> getRules() {
        return rules;
    }

    public void setRules(HashMap<String, String> rules) {
        this.rules = rules;
    }

    public List<String> getTerminator() {
        return terminator;
    }

    public void setTerminator(List<String> terminator) {
        this.terminator = terminator;
    }


    public Parser(){
//        rules.put();
    }
    public Parser(String startSymbol){
        this.startSymbol=startSymbol;
//        rules.put();
    }

    public boolean getLogMark() {
        return logMark;
    }

    public void setLogMark(boolean logMark) {
        this.logMark = logMark;
    }

    private boolean logMark=true;
    private String log="";

//    public String getSym() {
//        return sym;
//    }
//
//    public String getWord() {
//        return word;
//    }
//
//    String word="";

    private void nextSym(){
        symbol=symbolStream.nextSymbol();
//        if(symbol==null){
//            word="";
//            sym="";
//            return;
//        }
//        word= symbol.getName();
//        sym= symbol.getType();
//        System.out.println("symbol: "+sym+" "+word);
    }
    public void start(){
        nextSym();
        parseTree=new ParseTree(CompUnit());
    }
//    public ParseNode analyze(String symbol){
//        ParseNode node=new ParseNode(symbol);
//        ////symbolStream.back();nextSym();
//        int head= symbolStream.getPointer()-1;
//        boolean correct=true;
//        if(terminator.contains(symbol)) {
//            if(symbol.equals(symbolStream.nextSymbol())){
//                return node;
//            }else{
//                error();
//                return null;
//            }
//        }else{
//            ParseNode syntax;
//            if(symbol.equals(CompUnit)){
//                syntax=analyze(Decl);
//                while(syntax!=null){
//                    node.child(syntax);
//                    syntax=analyze(Decl);
//                }
//                syntax=analyze(ConstDecl);
//                while(syntax!=null){
//                    node.child(syntax);
//                    syntax=analyze(ConstDecl);
//                }
//                syntax=analyze(MainFuncDef);
//                if(syntax==null){
//                    error();
//                    return null;
//                }
//            }else if(symbol.equals(Decl)){
//                syntax=analyze(MainFuncDef);
//            }else if(symbol.equals(ConstDecl)){
//                ConstDecl();
//            }else if(symbol.equals(BType)){
//                BType();
//            }else if(symbol.equals(ConstDef)){
//                ConstDef();
//            }else if(symbol.equals(ConstInitVal)){
//                ConstInitVal();
//            }else if(symbol.equals()){
//
//            }else if(symbol.equals()){
//
//            }else if(symbol.equals()){
//
//            }else if(symbol.equals()){
//
//            }else if(symbol.equals()){
//
//            }else if(symbol.equals()){
//
//            }else if(symbol.equals()){
//
//            }else if(symbol.equals()){
//
//            }else{
//            String rule=rules.get(symbol);
//            if(rule!=null&& rule!=""){
//                String symbols[]=rule.split(" ");
//                for(String sym:symbols){
//                    if(analyze(sym)!=null) {
//                        correct=true;
//                    }else{
//                        correct=false;
//                        break;
//                    }
//                }
//            }else correct=false;
//            }
//        }
//
//        if(correct){
//            //symbolStream.forward();
//            return node;
//        }
//        else {
//            symbolStream.setPointer(head);nextSym();
//            return null;
//        }
//    }
    public void error(){
//        System.out.println("sym="+sym);
//        System.out.println("word="+word);
        throw new RuntimeException("symbol = "+symbol.getName());
    }
    public ParseNode CompUnit(){
        ////symbolStream.back();nextSym();
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
        //symbolStream.forward();
        return node;
    }
    public ParseNode Decl(){
        ////symbolStream.back();nextSym();
        int head= symbolStream.getPointer()-1;
        ParseNode node=new ParseNode(Decl);
        ParseNode syntax;
        if((syntax=ConstDecl())==null)
            if((syntax=VarDecl())==null){
//            error();
                symbolStream.setPointer(head);nextSym();
                return null;
            }
        node.child(syntax);
//        else if((syntax=VarDecl())!=null)
//            node.child(syntax);
//        else {
////            error();
//            symbolStream.setPointer(head);nextSym();
//            return null;
//        }
        //symbolStream.forward();
        return node;
    }
    public ParseNode ConstDecl(){
        ////symbolStream.back();nextSym();
        int head= symbolStream.getPointer()-1;
        ParseNode node=new ParseNode(ConstDecl);
        ParseNode syntax;

        if((syntax=terminator(CONSTTK))==null){
            symbolStream.setPointer(head);nextSym();
            return null;
        }
        node.child(syntax);
        if((syntax=BType())==null){
//            error();
            symbolStream.setPointer(head);nextSym();
            return null;
        }
        node.child(syntax);
        if((syntax=ConstDef())==null){
//            error();
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
//            error();
            symbolStream.setPointer(head);nextSym();
            return null;
        }
        node.child(syntax);
        //symbolStream.forward();
        return node;
    }
    public ParseNode VarDecl(){
        ////symbolStream.back();nextSym();
        int head= symbolStream.getPointer()-1;
        ParseNode node=new ParseNode(VarDecl);
        ParseNode syntax;

        if((syntax=BType())==null){
//            error();
            symbolStream.setPointer(head);nextSym();
            return null;
        }
        node.child(syntax);
        if((syntax=VarDef())==null){
//            error();
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
//            error();
            symbolStream.setPointer(head);nextSym();
            return null;
        }
        node.child(syntax);
        //symbolStream.forward();
        return node;
    }

    public ParseNode BType(){
        ////symbolStream.back();nextSym();
        int head= symbolStream.getPointer()-1;
        ParseNode node=new ParseNode(BType);
        ParseNode syntax;
        if((syntax=terminator(INTTK))==null){
            symbolStream.setPointer(head);nextSym();
            return null;
        }
        node.child(syntax);
        //symbolStream.forward();
        return node;
    }

    public ParseNode ConstDef(){
        ////symbolStream.back();nextSym();
        int head= symbolStream.getPointer()-1;
        ParseNode node=new ParseNode(ConstDef);
        ParseNode syntax;
        if((syntax=terminator(IDENFR))==null){
//            error();
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
        //symbolStream.forward();
        return node;
    }

    public ParseNode ConstInitVal(){
        ////symbolStream.back();nextSym();
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
        //symbolStream.forward();
        return node;
    }

    public ParseNode VarDef(){
        ////symbolStream.back();nextSym();
        int head= symbolStream.getPointer()-1;
        ParseNode node=new ParseNode(VarDef);
        ParseNode syntax;
        if((syntax=terminator(IDENFR))==null){
//            error();
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
        //symbolStream.forward();
        return node;
    }

    public ParseNode InitVal(){
        ////symbolStream.back();nextSym();
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
//            error();
                symbolStream.setPointer(head);nextSym();
                return null;
            }
            node.child(syntax);
        }else{
//            error();
            symbolStream.setPointer(head);nextSym();
            return null;
        }
        //symbolStream.forward();
        return node;
    }
    public ParseNode FuncDef(){
        ////symbolStream.back();nextSym();
        int head= symbolStream.getPointer()-1;
        ParseNode node=new ParseNode(FuncDef);
        ParseNode syntax;

        if((syntax=FuncType())==null){
//            error();
            symbolStream.setPointer(head);nextSym();
            return null;
        }
        node.child(syntax);
        if((syntax=terminator(IDENFR))!=null)
            node.child(syntax);
        else {
//            error();
            symbolStream.setPointer(head);nextSym();
            return null;
        }
        if((syntax=terminator(LPARENT))==null){
//            error();
            symbolStream.setPointer(head);nextSym();
            return null;
        }
        node.child(syntax);
        
        if((syntax=FuncFParams())!=null)
            node.child(syntax);
        
        if((syntax=terminator(RPARENT))==null){
//            error();
            symbolStream.setPointer(head);nextSym();
            return null;
        }
        node.child(syntax);
        
        if((syntax=Block())==null){
//            error();
            symbolStream.setPointer(head);nextSym();
            return null;
        }
        node.child(syntax);
        //symbolStream.forward();
        return node;
    }
    public ParseNode MainFuncDef(){
        ////symbolStream.back();nextSym();
        int head= symbolStream.getPointer()-1;
        ParseNode node=new ParseNode(MainFuncDef);
        ParseNode syntax;

        if((syntax=terminator(INTTK))==null){
//            error();
            symbolStream.setPointer(head);nextSym();
            return null;
        }
        node.child(syntax);

        if((syntax=terminator(MAINTK))==null){
//            error();
            symbolStream.setPointer(head);nextSym();
            return null;
        }
        node.child(syntax);

        if((syntax=terminator(LPARENT))==null){
//            error();
            symbolStream.setPointer(head);nextSym();
            return null;
        }
        node.child(syntax);

        if((syntax=terminator(RPARENT))==null){
//            error();
            symbolStream.setPointer(head);nextSym();
            return null;
        }
        node.child(syntax);

        if((syntax=Block())==null){
//            error();
            symbolStream.setPointer(head);nextSym();
            return null;
        }
        node.child(syntax);
        //symbolStream.forward();
        return node;
    }
    public ParseNode FuncType(){
        ////symbolStream.back();nextSym();
        int head= symbolStream.getPointer()-1;
        ParseNode node=new ParseNode(FuncType);
        ParseNode syntax;

        if((syntax=terminator(VOIDTK))==null
                &&(syntax=terminator(INTTK))==null){
//            error();
            symbolStream.setPointer(head);nextSym();
            return null;
        }
        node.child(syntax);
        //symbolStream.forward();
        return node;
    }
    public ParseNode FuncFParams(){
        ////symbolStream.back();nextSym();
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
        //symbolStream.forward();
        return node;
    }
    public ParseNode FuncFParam(){
        ////symbolStream.back();nextSym();
        int head= symbolStream.getPointer()-1;
        ParseNode node=new ParseNode(FuncFParam);
        ParseNode syntax;
        if((syntax=BType())==null){
//            error();
            symbolStream.setPointer(head);nextSym();
            return null;
        }
        node.child(syntax);

        if((syntax=terminator(IDENFR))==null){
//            error();
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
        //symbolStream.forward();
        return node;
    }
    public ParseNode Block(){
        ////symbolStream.back();nextSym();
        int head= symbolStream.getPointer()-1;
        ParseNode node=new ParseNode(Block);
        ParseNode syntax;

        if((syntax=terminator(LBRACE))==null){
//            error();
            symbolStream.setPointer(head);nextSym();
            return null;
        }
        node.child(syntax);

        while((syntax=BlockItem())!=null)
            node.child(syntax);

        if((syntax=terminator(RBRACE))==null){
//            error();
            symbolStream.setPointer(head);nextSym();
            return null;
        }
        node.child(syntax);
        //symbolStream.forward();
        return node;
    }
    public ParseNode BlockItem(){
        ////symbolStream.back();nextSym();
        int head= symbolStream.getPointer()-1;
        ParseNode node=new ParseNode(BlockItem);
        ParseNode syntax;
        if((syntax=Stmt())==null
            &&(syntax=Decl())==null){
//            error();
            symbolStream.setPointer(head);nextSym();
            return null;
        }
        node.child(syntax);
        //symbolStream.forward();
        return node;
    }
//    public ParseNode terminator(IDENFR){
//        ////symbolStream.back();nextSym();
//        int head= symbolStream.getPointer()-1;
//        ParseNode node=new ParseNode(Ident);
//        ParseNode syntax;
//        if(sym.equals("IDENFR")) {
//            node.child(syntax);
//        }else {
//            symbolStream.setPointer(head);nextSym();
//            return null;
//        }
//        //symbolStream.forward();
//        return node;
//    }
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
        ////symbolStream.back();nextSym();
        int head= symbolStream.getPointer()-1;
        ParseNode node=new ParseNode(Exp);
        ParseNode syntax;
        if((syntax=AddExp())==null){
            symbolStream.setPointer(head);nextSym();
            return null;
        }
        node.child(syntax);
        //symbolStream.forward();
        return node;
    }
    public ParseNode Cond(){
        ////symbolStream.back();nextSym();
        int head= symbolStream.getPointer()-1;
        ParseNode node=new ParseNode(Cond);
        ParseNode syntax;
        if((syntax=LOrExp())==null){
            symbolStream.setPointer(head);nextSym();
            return null;
        }
        node.child(syntax);
        //symbolStream.forward();
        return node;
    }
    public ParseNode LVal(){
        ////symbolStream.back();nextSym();
        int head= symbolStream.getPointer()-1;
        ParseNode node=new ParseNode(LVal);
        ParseNode syntax;
        if((syntax=terminator(IDENFR))==null){
//            error();
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
        //symbolStream.forward();
        return node;
    }
    public ParseNode PrimaryExp(){
        ////symbolStream.back();nextSym();
        int head= symbolStream.getPointer()-1;
        ParseNode node=new ParseNode(PrimaryExp);
        ParseNode syntax;
        if((syntax=terminator(LPARENT))!=null){
            node.child(syntax);
            if((syntax=Exp())==null){
//            error();
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
//            error();
            symbolStream.setPointer(head);nextSym();
            return null;
        }
//        while((syntax=terminator(LBRACK))!=null){
//            node.child(new ParseNode(symbol));
//            nextSym();
//            if((syntax=Exp())!=null)
//                node.child(syntax);
//            else {
//                symbolStream.setPointer(head);nextSym();
//                return null;
//            }
//            if((syntax=terminator(RBRACK))!=null){
//                node.child(new ParseNode(symbol));
//                nextSym();
//            }else {
//                symbolStream.setPointer(head);nextSym();
//                return null;
//            }
//        }
        //symbolStream.forward();
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
        //symbolStream.forward();
        return node;
    }
    public ParseNode UnaryExp(){
        ////symbolStream.back();nextSym();
        int head= symbolStream.getPointer()-1;
        ParseNode node=new ParseNode(UnaryExp);
        ParseNode syntax;

        if((syntax=terminator(IDENFR))!=null){
            node.child(syntax);
            if((syntax=terminator(LPARENT))!=null) {
                node.child(syntax);
            }else{
//            error();
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
//            error();
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
        //symbolStream.forward();
        return node;
    }
    public ParseNode UnaryOp(){
        ////symbolStream.back();nextSym();
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
        //symbolStream.forward();
        return node;
    }
    public ParseNode FuncRParams(){
        ////symbolStream.back();nextSym();
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
        //symbolStream.forward();
        return node;
    }
    public ParseNode MulExp(){
        ////symbolStream.back();nextSym();
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
        //symbolStream.forward();
        return node;
    }
    public ParseNode AddExp(){
        ////symbolStream.back();nextSym();
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
        //symbolStream.forward();
        return node;
    }
    public ParseNode RelExp(){
        ////symbolStream.back();nextSym();
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
        //symbolStream.forward();
        return node;
    }
    public ParseNode EqExp(){
        ////symbolStream.back();nextSym();
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
        //symbolStream.forward();
        return node;
    }
    public ParseNode LAndExp(){
        ////symbolStream.back();nextSym();
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
        //symbolStream.forward();
        return node;
    }
    public ParseNode LOrExp(){
        ////symbolStream.back();nextSym();
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
        //symbolStream.forward();
        return node;
    }
    public ParseNode ConstExp(){
        ////symbolStream.back();nextSym();
        int head= symbolStream.getPointer()-1;
        ParseNode node=new ParseNode(ConstExp);
        ParseNode syntax;

        if((syntax=AddExp())==null){
            symbolStream.setPointer(head);nextSym();
            return null;
        }
        node.child(syntax);
        //symbolStream.forward();
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
