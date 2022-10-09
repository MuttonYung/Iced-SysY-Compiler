package iced.compiler.parser;

import iced.compiler.lexer.Symbol;
import iced.compiler.lexer.SymbolStream;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Parser {

    private String sym="";
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

    public String getSym() {
        return sym;
    }

    public String getWord() {
        return word;
    }

    String word="";

    private void nextSym(){
        symbol=symbolStream.nextSymbol();
        if(symbol==null){
            word="";
            sym="";
            return;
        }
        word= symbol.getName();
        sym= symbol.getType();
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
//            if(symbol.equals("<CompUnit>")){
//                syntax=analyze("<Decl>");
//                while(syntax!=null){
//                    node.child(syntax);
//                    syntax=analyze("<Decl>");
//                }
//                syntax=analyze("<ConstDecl>");
//                while(syntax!=null){
//                    node.child(syntax);
//                    syntax=analyze("<ConstDecl>");
//                }
//                syntax=analyze("<MainFuncDef>");
//                if(syntax==null){
//                    error();
//                    return null;
//                }
//            }else if(symbol.equals("<Decl>")){
//                syntax=analyze("<MainFuncDef>");
//            }else if(symbol.equals("<ConstDecl>")){
//                ConstDecl();
//            }else if(symbol.equals("<BType>")){
//                BType();
//            }else if(symbol.equals("<ConstDef>")){
//                ConstDef();
//            }else if(symbol.equals("<ConstInitVal>")){
//                ConstInitVal();
//            }else if(symbol.equals("<>")){
//
//            }else if(symbol.equals("<>")){
//
//            }else if(symbol.equals("<>")){
//
//            }else if(symbol.equals("<>")){
//
//            }else if(symbol.equals("<>")){
//
//            }else if(symbol.equals("<>")){
//
//            }else if(symbol.equals("<>")){
//
//            }else if(symbol.equals("<>")){
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
        throw new RuntimeException("sym="+sym);
    }
    public ParseNode CompUnit(){
        ////symbolStream.back();nextSym();
        int head= symbolStream.getPointer()-1;
        ParseNode node=new ParseNode("<CompUnit>");
        ParseNode syntax;
        while((syntax=Decl())!=null)
            node.child(syntax);
        while((syntax=FuncDef())!=null)
            node.child(syntax);
        if((syntax=MainFuncDef())!=null)
            node.child(syntax);
        else{
//            error();
            symbolStream.setPointer(head);nextSym();
            return null;
        }
        //symbolStream.forward();
        return node;
    }
    public ParseNode Decl(){
        ////symbolStream.back();nextSym();
        int head= symbolStream.getPointer()-1;
        ParseNode node=new ParseNode("<Decl>");
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
        ParseNode node=new ParseNode("<ConstDecl>");
        ParseNode syntax;

        if(!sym.equals("CONSTTK")){
            symbolStream.setPointer(head);nextSym();
            return null;
        }
        node.child(new ParseNode(symbol));
        nextSym();
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
        while(sym.equals("COMMA")){
            node.child(new ParseNode(symbol));
            nextSym();
            if((syntax=ConstDef())==null){
                symbolStream.setPointer(head);nextSym();
                return null;
            }
            node.child(syntax);
        }
        if(sym.equals("SEMICN")) {
            node.child(new ParseNode(symbol));
            nextSym();
        }else {
//            error();
            symbolStream.setPointer(head);nextSym();
            return null;
        }
        //symbolStream.forward();
        return node;
    }
    public ParseNode VarDecl(){
        ////symbolStream.back();nextSym();
        int head= symbolStream.getPointer()-1;
        ParseNode node=new ParseNode("<VarDecl>");
        ParseNode syntax;

        if((syntax=BType())==null){
//            error();
            symbolStream.setPointer(head);nextSym();
            return null;
        }
        node.child(syntax);
        if((syntax=VarDef())!=null)
            node.child(syntax);
        else {
//            error();
            symbolStream.setPointer(head);nextSym();
            return null;
        }
        while(sym.equals("COMMA")){
            node.child(new ParseNode(symbol));
            nextSym();
            if((syntax=VarDef())!=null)
                node.child(syntax);
            else {
                symbolStream.setPointer(head);nextSym();
                return null;
            }
        }
        if(sym.equals("SEMICN")) {
            node.child(new ParseNode(symbol));
            nextSym();
        }else {
//            error();
            symbolStream.setPointer(head);nextSym();
            return null;
        }
        //symbolStream.forward();
        return node;
    }

    public ParseNode BType(){
        ////symbolStream.back();nextSym();
        int head= symbolStream.getPointer()-1;
        ParseNode node=new ParseNode("<BType>");
        ParseNode syntax;
        if(!sym.equals("INTTK")){
            symbolStream.setPointer(head);nextSym();
            return null;
        }
        node.child(new ParseNode(symbol));
        nextSym();
        //symbolStream.forward();
        return node;
    }

    public ParseNode ConstDef(){
        ////symbolStream.back();nextSym();
        int head= symbolStream.getPointer()-1;
        ParseNode node=new ParseNode("<ConstDef>");
        ParseNode syntax;
        if((syntax=Ident())!=null)
            node.child(syntax);
        else {
//            error();
            symbolStream.setPointer(head);nextSym();
            return null;
        }
        while(sym.equals("LBRACK")){
            node.child(new ParseNode(symbol));
            nextSym();
            if((syntax=ConstExp())!=null)
                node.child(syntax);
            else {
                symbolStream.setPointer(head);nextSym();
                return null;
            }
            if(sym.equals("RBRACK")){
                node.child(new ParseNode(symbol));
                nextSym();
            }else {
                symbolStream.setPointer(head);nextSym();
                return null;
            }
        }
        if(sym.equals("ASSIGN")){
            node.child(new ParseNode(symbol));
            nextSym();
        }else {
            symbolStream.setPointer(head);nextSym();
            return null;
        }
        if((syntax=ConstInitVal())!=null)
            node.child(syntax);
        else {
            symbolStream.setPointer(head);nextSym();
            return null;
        }
        //symbolStream.forward();
        return node;
    }

    public ParseNode ConstInitVal(){
        ////symbolStream.back();nextSym();
        int head= symbolStream.getPointer()-1;
        ParseNode node=new ParseNode("<ConstInitVal>");
        ParseNode syntax;
        if((syntax=ConstExp())!=null)
            node.child(syntax);
        else if(sym.equals("LBRACE")){
            node.child(new ParseNode(symbol));
            nextSym();
            if((syntax=ConstInitVal())!=null){
                node.child(syntax);
                while(sym.equals("COMMA")){
                    node.child(new ParseNode(symbol));
                    nextSym();
                    if((syntax=ConstInitVal())!=null)
                        node.child(syntax);
                    else {
                        symbolStream.setPointer(head);nextSym();
                        return null;
                    }
                }
            }
            if(sym.equals("RBRACE")){
                node.child(new ParseNode(symbol));
                nextSym();
            }else {
                symbolStream.setPointer(head);nextSym();
                return null;
            }
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
        ParseNode node=new ParseNode("<VarDef>");
        ParseNode syntax;
        if((syntax=Ident())!=null)
            node.child(syntax);
        else {
//            error();
            symbolStream.setPointer(head);nextSym();
            return null;
        }
        while(sym.equals("LBRACK")){
            node.child(new ParseNode(symbol));
            nextSym();
            if((syntax=ConstExp())!=null)
                node.child(syntax);
            else {
                symbolStream.setPointer(head);nextSym();
                return null;
            }
            if(sym.equals("RBRACK")){
                node.child(new ParseNode(symbol));
                nextSym();
            }else {
                symbolStream.setPointer(head);nextSym();
                return null;
            }
        }
        if(sym.equals("ASSIGN")){
            node.child(new ParseNode(symbol));
            nextSym();
            if((syntax=InitVal())!=null)
                node.child(syntax);
            else {
                symbolStream.setPointer(head);nextSym();
                return null;
            }
        }
        //symbolStream.forward();
        return node;
    }

    public ParseNode InitVal(){
        ////symbolStream.back();nextSym();
        int head= symbolStream.getPointer()-1;
        ParseNode node=new ParseNode("<InitVal>");
        ParseNode syntax;
        if((syntax=Exp())!=null)
            node.child(syntax);
        else if(sym.equals("LBRACE")){
            node.child(new ParseNode(symbol));
            nextSym();
            if((syntax=InitVal())!=null){
                node.child(syntax);
                while(sym.equals("COMMA")){
                    node.child(new ParseNode(symbol));
                    nextSym();
                    if((syntax=InitVal())!=null)
                        node.child(syntax);
                    else {
                        symbolStream.setPointer(head);nextSym();
                        return null;
                    }
                }
            }
            if(sym.equals("RBRACE")) {
                node.child(new ParseNode(symbol));
                nextSym();
            }else{
//            error();
                symbolStream.setPointer(head);nextSym();
                return null;
            }
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
        ParseNode node=new ParseNode("<FuncDef>");
        ParseNode syntax;

        if((syntax=FuncType())!=null)
            node.child(syntax);
        else {
//            error();
            symbolStream.setPointer(head);nextSym();
            return null;
        }
        if((syntax=Ident())!=null)
            node.child(syntax);
        else {
//            error();
            symbolStream.setPointer(head);nextSym();
            return null;
        }
        if(sym.equals("LPARENT")) {
            node.child(new ParseNode(symbol));
            nextSym();
        }else{
//            error();
            symbolStream.setPointer(head);nextSym();
            return null;
        }
        if((syntax=FuncFParams())!=null)
            node.child(syntax);
        if(sym.equals("RPARENT")) {
            node.child(new ParseNode(symbol));
            nextSym();
        }else{
//            error();
            symbolStream.setPointer(head);nextSym();
            return null;
        }
        if((syntax=Block())!=null)
            node.child(syntax);
        else {
//            error();
            symbolStream.setPointer(head);nextSym();
            return null;
        }
        //symbolStream.forward();
        return node;
    }
    public ParseNode MainFuncDef(){
        ////symbolStream.back();nextSym();
        int head= symbolStream.getPointer()-1;
        ParseNode node=new ParseNode("<MainFuncDef>");
        ParseNode syntax;

        if(sym.equals("INTTK")) {
            node.child(new ParseNode(symbol));
            nextSym();
        }else{
//            error();
            symbolStream.setPointer(head);nextSym();
            return null;
        }
        if(sym.equals("MAINTK")) {
            node.child(new ParseNode(symbol));
            nextSym();
        }else{
//            error();
            symbolStream.setPointer(head);nextSym();
            return null;
        }
        if(sym.equals("LPARENT")) {
            node.child(new ParseNode(symbol));
            nextSym();
        }else{
//            error();
            symbolStream.setPointer(head);nextSym();
            return null;
        }
        if(sym.equals("RPARENT")) {
            node.child(new ParseNode(symbol));
            nextSym();
        }else{
//            error();
            symbolStream.setPointer(head);nextSym();
            return null;
        }
        if((syntax=Block())!=null)
            node.child(syntax);
        else {
//            error();
            symbolStream.setPointer(head);nextSym();
            return null;
        }
        //symbolStream.forward();
        return node;
    }
    public ParseNode FuncType(){
        ////symbolStream.back();nextSym();
        int head= symbolStream.getPointer()-1;
        ParseNode node=new ParseNode("<FuncType>");
        ParseNode syntax;

        if(!sym.equals("VOIDTK")&&!sym.equals("INTTK")){
//            error();
            symbolStream.setPointer(head);nextSym();
            return null;
        }
        node.child(new ParseNode(symbol));
        nextSym();
        //symbolStream.forward();
        return node;
    }
    public ParseNode FuncFParams(){
        ////symbolStream.back();nextSym();
        int head= symbolStream.getPointer()-1;
        ParseNode node=new ParseNode("<FuncFParams>");
        ParseNode syntax;

        if((syntax=FuncFParam())==null){
            symbolStream.setPointer(head);nextSym();
            return null;
        }
        node.child(syntax);
        while(sym.equals("COMMA")){
            node.child(new ParseNode(symbol));
            nextSym();
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
        ParseNode node=new ParseNode("<FuncFParam>");
        ParseNode syntax;
        if((syntax=BType())!=null)
            node.child(syntax);
        else {
//            error();
            symbolStream.setPointer(head);nextSym();
            return null;
        }
        if((syntax=Ident())!=null)
            node.child(syntax);
        else {
//            error();
            symbolStream.setPointer(head);nextSym();
            return null;
        }
        if(sym.equals("LBRACK")){
            node.child(new ParseNode(symbol));
            nextSym();
            if(sym.equals("RBRACK")){
                node.child(new ParseNode(symbol));
                nextSym();
            }else {
                symbolStream.setPointer(head);nextSym();
                return null;
            }
            while(sym.equals("LBRACK")){
                node.child(new ParseNode(symbol));
                nextSym();
                if((syntax=ConstExp())!=null)
                    node.child(syntax);
                else {
                    symbolStream.setPointer(head);nextSym();
                    return null;
                }
                if(sym.equals("RBRACK")){
                    node.child(new ParseNode(symbol));
                    nextSym();
                }else {
                    symbolStream.setPointer(head);nextSym();
                    return null;
                }
            }
        }
        //symbolStream.forward();
        return node;
    }
    public ParseNode Block(){
        ////symbolStream.back();nextSym();
        int head= symbolStream.getPointer()-1;
        ParseNode node=new ParseNode("<Block>");
        ParseNode syntax;

        if(sym.equals("LBRACE")){
            node.child(new ParseNode(symbol));
            nextSym();
        }
        else {
//            error();
            symbolStream.setPointer(head);nextSym();
            return null;
        }
        while((syntax=BlockItem())!=null)
            node.child(syntax);
        if(sym.equals("RBRACE")){
            node.child(new ParseNode(symbol));
            nextSym();
        }
        else {
//            error();
            symbolStream.setPointer(head);nextSym();
            return null;
        }
        //symbolStream.forward();
        return node;
    }
    public ParseNode BlockItem(){
        ////symbolStream.back();nextSym();
        int head= symbolStream.getPointer()-1;
        ParseNode node=new ParseNode("<BlockItem>");
        ParseNode syntax;
        if((syntax=Stmt())!=null)
            node.child(syntax);
        else if((syntax=Decl())!=null)
            node.child(syntax);
        else {
//            error();
            symbolStream.setPointer(head);nextSym();
            return null;
        }
        //symbolStream.forward();
        return node;
    }
    public ParseNode Ident(){
        ////symbolStream.back();nextSym();
        int head= symbolStream.getPointer()-1;
        ParseNode node=new ParseNode("<Ident>");
        ParseNode syntax;
        if(sym.equals("IDENFR")) {
            node.child(new ParseNode(symbol));
            nextSym();
        }else {
            symbolStream.setPointer(head);nextSym();
            return null;
        }
        //symbolStream.forward();
        return node;
    }
    public ParseNode Stmt(){
        int head= symbolStream.getPointer()-1;
        ParseNode node=new ParseNode("<Stmt>");
        ParseNode syntax;
        if((syntax=LVal())!=null){
            node.child(syntax);
            if(sym.equals("ASSIGN")){
                node.child(new ParseNode(symbol));
                nextSym();
            }else {
                symbolStream.setPointer(head);nextSym();
                node=new ParseNode("<Stmt>");
                if((syntax=Exp())!=null){
                    node.child(syntax);
                    if(sym.equals("SEMICN")){
                        node.child(new ParseNode(symbol));
                        nextSym();
                        return node;
                    }else {
                        symbolStream.setPointer(head);nextSym();
                        return null;
                    }
                }else{
                    symbolStream.setPointer(head);nextSym();
                    return null;
                }
            }
            if((syntax=Exp())!=null)
                node.child(syntax);
            else if(sym.equals("GETINTTK")){
                node.child(new ParseNode(symbol));
                nextSym();
                if(sym.equals("LPARENT")){
                    node.child(new ParseNode(symbol));
                    nextSym();
                }else {
                    symbolStream.setPointer(head);nextSym();
                    return null;
                }
                if(sym.equals("RPARENT")){
                    node.child(new ParseNode(symbol));
                    nextSym();
                }else {
                    symbolStream.setPointer(head);nextSym();
                    return null;
                }
            }else{
                symbolStream.setPointer(head);nextSym();
                return null;
            }
            if(sym.equals("SEMICN")){
                node.child(new ParseNode(symbol));
                nextSym();
            }else {
                symbolStream.setPointer(head);nextSym();
                return null;
            }
        }
        else if(sym.equals("SEMICN")){
            node.child(new ParseNode(symbol));
            nextSym();
        }else if((syntax=Exp())!=null){
            node.child(syntax);
            if(sym.equals("SEMICN")){
                node.child(new ParseNode(symbol));
                nextSym();
            }else {
                symbolStream.setPointer(head);nextSym();
                return null;
            }
        }else if((syntax=Block())!=null)
            node.child(syntax);
        else if(sym.equals("IFTK")){
            node.child(new ParseNode(symbol));
            nextSym();
            if(sym.equals("LPARENT")){
                node.child(new ParseNode(symbol));
                nextSym();
            }else {
                symbolStream.setPointer(head);nextSym();
                return null;
            }
            if((syntax=Cond())!=null)
                node.child(syntax);
            else {
                symbolStream.setPointer(head);nextSym();
                return null;
            }
            if(sym.equals("RPARENT")){
                node.child(new ParseNode(symbol));
                nextSym();
            }else {
                symbolStream.setPointer(head);nextSym();
                return null;
            }
            if((syntax=Stmt())!=null)
                node.child(syntax);
            else {
                symbolStream.setPointer(head);nextSym();
                return null;
            }
            if(sym.equals("ELSETK")){
                node.child(new ParseNode(symbol));
                nextSym();
                if((syntax=Stmt())!=null)
                    node.child(syntax);
                else {
                    symbolStream.setPointer(head);nextSym();
                    return null;
                }
            }
        }else if(sym.equals("WHILETK")){
            node.child(new ParseNode(symbol));
            nextSym();
            if(sym.equals("LPARENT")){
                node.child(new ParseNode(symbol));
                nextSym();
            }else {
                symbolStream.setPointer(head);nextSym();
                return null;
            }
            if((syntax=Cond())!=null)
                node.child(syntax);
            else {
                symbolStream.setPointer(head);nextSym();
                return null;
            }
            if(sym.equals("RPARENT")){
                node.child(new ParseNode(symbol));
                nextSym();
            }else {
                symbolStream.setPointer(head);nextSym();
                return null;
            }
            if((syntax=Stmt())!=null)
                node.child(syntax);
            else {
                symbolStream.setPointer(head);nextSym();
                return null;
            }
        }else if(sym.equals("BREAKTK")||sym.equals("CONTINUETK")){
            node.child(new ParseNode(symbol));
            nextSym();
            if(sym.equals("SEMICN")){
                node.child(new ParseNode(symbol));
                nextSym();
            }else {
                symbolStream.setPointer(head);nextSym();
                return null;
            }
        }else if(sym.equals("RETURNTK")){
            node.child(new ParseNode(symbol));
            nextSym();
            if((syntax=Exp())!=null)
                node.child(syntax);
            if(sym.equals("SEMICN")){
                node.child(new ParseNode(symbol));
                nextSym();
            }else {
                symbolStream.setPointer(head);nextSym();
                return null;
            }
        }else if(sym.equals("PRINTFTK")){
            node.child(new ParseNode(symbol));
            nextSym();
            if(sym.equals("LPARENT")){
                node.child(new ParseNode(symbol));
                nextSym();
            }else {
                symbolStream.setPointer(head);nextSym();
                return null;
            }
            if(sym.equals("STRCON")){
                node.child(new ParseNode(symbol));
                nextSym();
            }else {
                symbolStream.setPointer(head);nextSym();
                return null;
            }
            while(sym.equals("COMMA")){
                node.child(new ParseNode(symbol));
                nextSym();
                if((syntax=Exp())!=null)
                    node.child(syntax);
                else {
                    symbolStream.setPointer(head);nextSym();
                    return null;
                }
            }
            if(sym.equals("RPARENT")){
                node.child(new ParseNode(symbol));
                nextSym();
            }else {
                symbolStream.setPointer(head);nextSym();
                return null;
            }
            if(sym.equals("SEMICN")){
                node.child(new ParseNode(symbol));
                nextSym();
            }else {
                symbolStream.setPointer(head);nextSym();
                return null;
            }
        }else{
            symbolStream.setPointer(head);nextSym();
            return null;
        }
        return node;
    }
    public ParseNode Exp(){
        ////symbolStream.back();nextSym();
        int head= symbolStream.getPointer()-1;
        ParseNode node=new ParseNode("<Exp>");
        ParseNode syntax;
        if((syntax=AddExp())!=null)
            node.child(syntax);
        else {
            symbolStream.setPointer(head);nextSym();
            return null;
        }
        //symbolStream.forward();
        return node;
    }
    public ParseNode Cond(){
        ////symbolStream.back();nextSym();
        int head= symbolStream.getPointer()-1;
        ParseNode node=new ParseNode("<Cond>");
        ParseNode syntax;
        if((syntax=LOrExp())!=null)
            node.child(syntax);
        else {
            symbolStream.setPointer(head);nextSym();
            return null;
        }
        //symbolStream.forward();
        return node;
    }
    public ParseNode LVal(){
        ////symbolStream.back();nextSym();
        int head= symbolStream.getPointer()-1;
        ParseNode node=new ParseNode("<LVal>");
        ParseNode syntax;
        if((syntax=Ident())!=null)
            node.child(syntax);
        else {
//            error();
            symbolStream.setPointer(head);nextSym();
            return null;
        }
        while(sym.equals("LBRACK")){
            node.child(new ParseNode(symbol));
            nextSym();
            if((syntax=Exp())!=null)
                node.child(syntax);
            else {
                symbolStream.setPointer(head);nextSym();
                return null;
            }
            if(sym.equals("RBRACK")){
                node.child(new ParseNode(symbol));
                nextSym();
            }else {
                symbolStream.setPointer(head);nextSym();
                return null;
            }
        }
        //symbolStream.forward();
        return node;
    }
    public ParseNode PrimaryExp(){
        ////symbolStream.back();nextSym();
        int head= symbolStream.getPointer()-1;
        ParseNode node=new ParseNode("<PrimaryExp>");
        ParseNode syntax;
        if(sym.equals("LPARENT")){
            node.child(new ParseNode(symbol));
            nextSym();
            if((syntax=Exp())!=null)
                node.child(syntax);
            else{
//            error();
                symbolStream.setPointer(head);nextSym();
                return null;
            }
            if(sym.equals("RPARENT")){
                node.child(new ParseNode(symbol));
                nextSym();
            }else {
                symbolStream.setPointer(head);nextSym();
                return null;
            }
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
//        while(sym.equals("LBRACK")){
//            node.child(new ParseNode(symbol));
//            nextSym();
//            if((syntax=Exp())!=null)
//                node.child(syntax);
//            else {
//                symbolStream.setPointer(head);nextSym();
//                return null;
//            }
//            if(sym.equals("RBRACK")){
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
        //symbolStream.back();
//        nextSym();
        int head= symbolStream.getPointer()-1;
        ParseNode node = new ParseNode("<Number>");
        ParseNode syntax;
        if(sym.equals("INTCON")){
            node.child(new ParseNode(symbol));
            nextSym();
        }else {
            symbolStream.setPointer(head);nextSym();
            return null;
        }
        //symbolStream.forward();
        return node;
    }
    public ParseNode UnaryExp(){
        ////symbolStream.back();nextSym();
        int head= symbolStream.getPointer()-1;
        ParseNode node=new ParseNode("<UnaryExp>");
        ParseNode syntax;

        if((syntax=Ident())!=null){
            node.child(syntax);
            if(sym.equals("LPARENT")) {
                node.child(new ParseNode(symbol));
                nextSym();
            }else{
//            error();
                node=new ParseNode("<UnaryExp>");
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
            if(sym.equals("RPARENT")) {
                node.child(new ParseNode(symbol));
                nextSym();
            }else{
//            error();
                symbolStream.setPointer(head);nextSym();
                return null;
            }
        }else if((syntax=PrimaryExp())!=null)
            node.child(syntax);
        else if((syntax=UnaryOp())!=null){
            node.child(syntax);
            if((syntax=UnaryExp())!=null)
                node.child(syntax);
            else {
                symbolStream.setPointer(head);nextSym();
                return null;
            }
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
        ParseNode node=new ParseNode("<UnaryOp>");
        ParseNode syntax;
        if(sym.equals("PLUS")||sym.equals("MINU")||sym.equals("NOT")) {
            node.child(new ParseNode(symbol));
            nextSym();
        }else{
            symbolStream.setPointer(head);nextSym();
            return null;
        }
        //symbolStream.forward();
        return node;
    }
    public ParseNode FuncRParams(){
        ////symbolStream.back();nextSym();
        int head= symbolStream.getPointer()-1;
        ParseNode node=new ParseNode("<FuncRParams>");
        ParseNode syntax;

        if((syntax=Exp())!=null){
            node.child(syntax);
        }else {
            symbolStream.setPointer(head);nextSym();
            return null;
        }
        while(sym.equals("COMMA")){
            node.child(new ParseNode(symbol));
            nextSym();
            if((syntax=Exp())!=null)
                node.child(syntax);
            else {
                symbolStream.setPointer(head);nextSym();
                return null;
            }
        }
        //symbolStream.forward();
        return node;
    }
    public ParseNode MulExp(){
        ////symbolStream.back();nextSym();
        int head= symbolStream.getPointer()-1;
        ParseNode node=new ParseNode("<MulExp>");
        ParseNode syntax;

        if((syntax=UnaryExp())!=null){
            node.child(syntax);
        }else {
            symbolStream.setPointer(head);nextSym();
            return null;
        }
        while(sym.equals("MULT")||sym.equals("DIV")||sym.equals("MOD")){
            ParseNode node1=new ParseNode("<MulExp>");
            node1.child(node);
            node1.child(new ParseNode(symbol));
            nextSym();
            if((syntax=UnaryExp())!=null)
                node1.child(syntax);
            else {
                symbolStream.setPointer(head);nextSym();
                return null;
            }
            node=node1;
        }
        //symbolStream.forward();
        return node;
    }
    public ParseNode AddExp(){
        ////symbolStream.back();nextSym();
        int head= symbolStream.getPointer()-1;
        ParseNode node=new ParseNode("<AddExp>");
        ParseNode syntax;

        if((syntax=MulExp())!=null){
            node.child(syntax);
        }else {
            symbolStream.setPointer(head);nextSym();
            return null;
        }
        while(sym.equals("PLUS")||sym.equals("MINU")){
            ParseNode node1=new ParseNode("<AddExp>");
            node1.child(node);
            node1.child(new ParseNode(symbol));
            nextSym();
            if((syntax=MulExp())!=null)
                node1.child(syntax);
            else {
                symbolStream.setPointer(head);nextSym();
                return null;
            }
            node=node1;
        }
        //symbolStream.forward();
        return node;
    }
    public ParseNode RelExp(){
        ////symbolStream.back();nextSym();
        int head= symbolStream.getPointer()-1;
        ParseNode node=new ParseNode("<RelExp>");
        ParseNode syntax;

        if((syntax=AddExp())!=null){
            node.child(syntax);
        }else {
            symbolStream.setPointer(head);nextSym();
            return null;
        }
        while(sym.equals("LSS")||sym.equals("LEQ")||sym.equals("GRE")||sym.equals("GEQ")){
            ParseNode node1=new ParseNode("<RelExp>");
            node1.child(node);
            node1.child(new ParseNode(symbol));
            nextSym();
            if((syntax=AddExp())!=null)
                node1.child(syntax);
            else {
                symbolStream.setPointer(head);nextSym();
                return null;
            }
            node=node1;
        }
        //symbolStream.forward();
        return node;
    }
    public ParseNode EqExp(){
        ////symbolStream.back();nextSym();
        int head= symbolStream.getPointer()-1;
        ParseNode node=new ParseNode("<EqExp>");
        ParseNode syntax;

        if((syntax=RelExp())!=null){
            node.child(syntax);
        }else {
            symbolStream.setPointer(head);nextSym();
            return null;
        }
        while(sym.equals("EQL")||sym.equals("NEQ")){
            ParseNode node1=new ParseNode("<EqExp>");
            node1.child(node);
            node1.child(new ParseNode(symbol));
            nextSym();
            if((syntax=RelExp())!=null)
                node1.child(syntax);
            else {
                symbolStream.setPointer(head);nextSym();
                return null;
            }
            node=node1;
        }
        //symbolStream.forward();
        return node;
    }
    public ParseNode LAndExp(){
        ////symbolStream.back();nextSym();
        int head= symbolStream.getPointer()-1;
        ParseNode node=new ParseNode("<LAndExp>");
        ParseNode syntax;

        if((syntax=EqExp())!=null){
            node.child(syntax);
        }else {
            symbolStream.setPointer(head);nextSym();
            return null;
        }
        while(sym.equals("AND")){
            ParseNode node1=new ParseNode("<LAndExp>");
            node1.child(node);
            node1.child(new ParseNode(symbol));
            nextSym();
            if((syntax=EqExp())!=null)
                node1.child(syntax);
            else {
                symbolStream.setPointer(head);nextSym();
                return null;
            }
            node=node1;
        }
        //symbolStream.forward();
        return node;
    }
    public ParseNode LOrExp(){
        ////symbolStream.back();nextSym();
        int head= symbolStream.getPointer()-1;
        ParseNode node=new ParseNode("<LOrExp>");
        ParseNode syntax;

        if((syntax=LAndExp())!=null){
            node.child(syntax);
        }else {
            symbolStream.setPointer(head);nextSym();
            return null;
        }
        while(sym.equals("OR")){
            ParseNode node1=new ParseNode("<LOrExp>");
            node1.child(node);
            node1.child(new ParseNode(symbol));
            nextSym();
            if((syntax=LAndExp())!=null)
                node1.child(syntax);
            else {
                symbolStream.setPointer(head);nextSym();
                return null;
            }
            node=node1;
        }
        //symbolStream.forward();
        return node;
    }
    public ParseNode ConstExp(){
        ////symbolStream.back();nextSym();
        int head= symbolStream.getPointer()-1;
        ParseNode node=new ParseNode("<ConstExp>");
        ParseNode syntax;

        if((syntax=AddExp())!=null){
            node.child(syntax);
        }else {
            symbolStream.setPointer(head);nextSym();
            return null;
        }
        //symbolStream.forward();
        return node;
    }
}
