package iced.compiler;
import java.util.ArrayList;
import iced.compiler.error.Error;
import java.util.HashMap;
import java.util.List;

public class SysY {
    private static final List<Error> errorList=new ArrayList<>();
    public static final int NOT=10,MULT=11,ASSIGN=12,AND=13,DIV=14,SEMICN=15,
            OR=16,MOD=17,COMMA=18,MAINTK=19,WHILETK=20,LSS=21,LPARENT=22,
            CONSTTK=23,GETINTTK=24,LEQ=25,RPARENT=26,INTTK=27,PRINTFTK=28,
            GRE=29,LBRACK=30,BREAKTK=31,RETURNTK=32,GEQ=33,RBRACK=34,
            CONTINUETK=35,PLUS=36,EQL=37,LBRACE=38,IFTK=39,MINU=40,NEQ=41,
            RBRACE=42,ELSETK=43,VOIDTK=44,IDENFR=45,INTCON=46,STRCON=47;
    public static final int ConstDecl=STRCON+1,Decl=STRCON+2,BType=STRCON+3,
            ConstDef=STRCON+4,ConstInitVal=STRCON+5,VarDecl=STRCON+6,VarDef=STRCON+7,
            InitVal=STRCON+8,FuncDef=STRCON+9,MainFuncDef=STRCON+10,FuncType=STRCON+11,
            FuncFParams=STRCON+12,FuncFParam=STRCON+13,Block=STRCON+14,BlockItem=STRCON+15,
            Stmt=STRCON+16,Exp=STRCON+17,Cond=STRCON+18,LVal=STRCON+19,PrimaryExp=STRCON+20,
            Number=STRCON+21,UnaryExp=STRCON+22,UnaryOp=STRCON+23,FuncRParams=STRCON+24,MulExp=STRCON+25,
            AddExp=STRCON+26,RelExp=STRCON+27,EqExp=STRCON+28,LAndExp=STRCON+29,LOrExp=STRCON+30
            ,ConstExp=STRCON+31,CompUnit=STRCON+32;
    private static HashMap<Integer,String> codeType;
    public static boolean isTerminator(int a){
        return a>=NOT&&a<=STRCON;
    }
    public static String getType(int a){
        if(isTerminator(a))
            return codeType.get(a);
        else
            return "<"+codeType.get(a)+">";
    }
    public SysY(){
        codeType=new HashMap<>();
        codeType.put(ConstDecl,"ConstDecl");
        codeType.put(Decl,"Decl");
        codeType.put(BType,"BType");
        codeType.put(ConstDef,"ConstDef");
        codeType.put(ConstInitVal,"ConstInitVal");
        codeType.put(VarDecl,"VarDecl");
        codeType.put(VarDef,"VarDef");
        codeType.put(InitVal,"InitVal");
        codeType.put(FuncDef,"FuncDef");
        codeType.put(MainFuncDef,"MainFuncDef");
        codeType.put(FuncType,"FuncType");
        codeType.put(FuncFParams,"FuncFParams");
        codeType.put(FuncFParam,"FuncFParam");
        codeType.put(Block,"Block");
        codeType.put(BlockItem,"BlockItem");
        codeType.put(Stmt,"Stmt");
        codeType.put(Exp,"Exp");
        codeType.put(Cond,"Cond");
        codeType.put(LVal,"LVal");
        codeType.put(PrimaryExp,"PrimaryExp");
        codeType.put(Number,"Number");
        codeType.put(UnaryExp,"UnaryExp");
        codeType.put(UnaryOp,"UnaryOp");
        codeType.put(FuncRParams,"FuncRParams");
        codeType.put(MulExp,"MulExp");
        codeType.put(AddExp,"AddExp");
        codeType.put(RelExp,"RelExp");
        codeType.put(EqExp,"EqExp");
        codeType.put(LAndExp,"LAndExp");
        codeType.put(LOrExp,"LOrExp");
        codeType.put(ConstExp,"ConstExp");
        codeType.put(CompUnit,"CompUnit");
        codeType.put(NOT,"NOT");
        codeType.put(MULT,"MULT");
        codeType.put(ASSIGN,"ASSIGN");
        codeType.put(AND,"AND");
        codeType.put(DIV,"DIV");
        codeType.put(SEMICN,"SEMICN");
        codeType.put(OR,"OR");
        codeType.put(MOD,"MOD");
        codeType.put(COMMA,"COMMA");
        codeType.put(MAINTK,"MAINTK");
        codeType.put(WHILETK,"WHILETK");
        codeType.put(LSS,"LSS");
        codeType.put(LPARENT,"LPARENT");
        codeType.put(CONSTTK,"CONSTTK");
        codeType.put(GETINTTK,"GETINTTK");
        codeType.put(LEQ,"LEQ");
        codeType.put(RPARENT,"RPARENT");
        codeType.put(INTTK,"INTTK");
        codeType.put(PRINTFTK,"PRINTFTK");
        codeType.put(GRE,"GRE");
        codeType.put(LBRACK,"LBRACK");
        codeType.put(BREAKTK,"BREAKTK");
        codeType.put(RETURNTK,"RETURNTK");
        codeType.put(GEQ,"GEQ");
        codeType.put(RBRACK,"RBRACK");
        codeType.put(CONTINUETK,"CONTINUETK");
        codeType.put(PLUS,"PLUS");
        codeType.put(EQL,"EQL");
        codeType.put(LBRACE,"LBRACE");
        codeType.put(IFTK,"IFTK");
        codeType.put(MINU,"MINU");
        codeType.put(NEQ,"NEQ");
        codeType.put(RBRACE,"RBRACE");
        codeType.put(ELSETK,"ELSETK");
        codeType.put(VOIDTK,"VOIDTK");
        codeType.put(IDENFR,"IDENFR");
        codeType.put(INTCON,"INTCON");
        codeType.put(STRCON,"STRCON");
    }

    public static List<Error> getErrorList() {
        return errorList;
    }
}
