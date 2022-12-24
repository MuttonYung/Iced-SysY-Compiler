package iced.compiler.sematics;

import iced.compiler.SysY;
import iced.compiler.error.Error;
import iced.compiler.lexer.Token;
import iced.compiler.parser.ParseNode;
import iced.compiler.parser.ParseTree;

import java.util.ArrayList;
import java.util.List;

public class SemanticsAnalyser extends SysY {
    private ParseTree parseTree;
    private Block base,blkPointer;
    private int whileNO=0,ifNO=0;
    private List<Operator> operatorList=new ArrayList<>();
    private List<Function> functions=new ArrayList<>();
    private boolean start=false;
    private int loopLayer=0;
    private boolean returnVal=false;
    public SemanticsAnalyser(ParseTree parseTree) {
        this.parseTree = parseTree;
    }

    public void build(){
        base=new Block();
        blkPointer=base;
        traverse(parseTree.getRoot(),false);
        int top=operatorList.size();
//        if(operatorList.get(top-1).getType()==Operator.BACK)
//            operatorList.remove(top-1);
        if(operatorList.get(top-2).getType()==Operator.RET)
            operatorList.remove(top-2);
    }
    private Function FuncDef(ParseNode node){
//        if(node.getChildren().size()<5){
//            // TODO: Exception
//            return null;
//        }
        String name=node.getChildren().get(1).getToken().getName();
        ParseNode funcType=node.getChildren().get(0);
        if(!funcType.isLeaf())
            funcType=funcType.getChildren().get(0);
        int paramNum=0,typeCode=funcType.getToken().getCode(),type;
        Function func;
        List<Symbol> fParams=null;
        boolean dup=false,returnVal=false;

        for(Function function:functions){
            if(function.getName().equals(name))
                dup=true;
        }
        if(typeCode==INTTK)
            type=Symbol.INT;
        else if(typeCode==VOIDTK)
            type=Symbol.VOID;
        else{
            // TODO: Exception
            type=0;
        }
        if(node.getChildren().size()>3&&node.getChildren().get(3).getToken().getCode()==FuncFParams) {
            fParams=FuncFParams(node.getChildren().get(3));
            paramNum = fParams.size();
            func=new Function(name,type,paramNum);
            for(Symbol symbol:fParams){
                func.addParam(symbol);
            }
        }else
            func=new Function(name,type,0);

        if(dup){
            getErrorList().add(new Error(node.getChildren().get(1).getToken(),'b'));
        }
        functions.add(func);
        if(!start){
            operatorList.add(Operator.Jmp("main"));
            start=true;
        }
        operatorList.add(Operator.Tag(name));
        operatorList.add(Operator.Swrt);
        Block(node.getChildren().get(node.getChildren().size()-1),fParams,type!=Symbol.VOID);
        operatorList.add(Operator.Ret);
        return func;
    }
    private void FuncCall(ParseNode node){
        ParseNode ident=node.getChildren().get(0);
        String name=ident.getToken().getName();
        Function function=null;
        for(Function func:functions){
            if(func.getName().equals(name))
                function=func;
        }
        if(function==null)
            getErrorList().add(new Error(ident.getToken(),'c'));
        else{
            if(node.getChildren().size()>2&&node.getChildren().get(2).getToken().getCode()==FuncRParams) {
                List<Symbol> rParams=FuncRParams(node.getChildren().get(2));
                int paramNum = rParams.size();
                if(paramNum!=function.getParamNum())
                    getErrorList().add(new Error(ident.getToken(),'d'));
                else
                    for(int i=0;i<paramNum;i++){
                        Symbol fParam=function.getParamList().get(i);
                        Symbol rParam=rParams.get(i);
                        if(fParam.getType()!=rParam.getType()||
                            fParam.getSize2()!=rParam.getSize2())
                            getErrorList().add(new Error(ident.getToken(),'e'));
                    }
                traverse(node.getChildren().get(2),false);
            }else {
                if(0!=function.getParamNum())
                    getErrorList().add(new Error(ident.getToken(),'d'));
            }
        }
        operatorList.add(Operator.Jal(ident.getToken().getName()));
    }
    private List<Symbol> FuncFParams(ParseNode node){
        List<Symbol> params=new ArrayList<>();
        for(ParseNode child:node.getChildren()){
            if(child.getToken().getCode()==FuncFParam){
                Symbol param=FuncFParam(child);
                for(Symbol cur:params)
                    if(cur.getName().equals(param.getName())){
                        ParseNode ident=child.getChildren().get(1);
                        getErrorList().add(new Error(ident.getToken(),'b'));
                    }
                params.add(param);
            }
        }
        return params;
    }
    private List<Symbol> FuncRParams(ParseNode node){
        List<Symbol> params=new ArrayList<>();
        for(ParseNode child:node.getChildren()){
            if(child.getToken().getCode()==Exp){
                Symbol param=Exp(child);
                params.add(param);
            }
        }
        return params;
    }
    private Symbol FuncFParam(ParseNode node){
        Symbol b=null;
        boolean array=false;
        for(ParseNode child:node.getChildren()){
            if(child.getToken().getCode()==LBRACK)
                array=true;
            else if(child.getToken().getCode()==ConstExp)
                b=Exp(child);
        }
        if(array){
            if(b!=null){
                return new Symbol(node.getChildren().get(1).getToken().getName(),0,b.getValue(),null);
            }else{
                return new Symbol(node.getChildren().get(1).getToken().getName(),0,null);
            }
        }else{
            return new Symbol(node.getChildren().get(1).getToken().getName());
        }
    }
    private void Block(ParseNode node,List<Symbol> fParams,boolean returnVal){
        Block block=new Block();
        block.setFather(blkPointer);
        blkPointer=block;
        operatorList.add(Operator.Data(block));
        boolean outVal=false;
        if(fParams!=null&&fParams.size()>0){
            for(Symbol symbol:fParams)
                blkPointer.addSymbol(symbol);
            int i=fParams.size();
            for(i--;i>=0;i--){
                Symbol symbol=fParams.get(i);
                operatorList.add(Operator.Var(symbol));
                operatorList.add(Operator.Param);
                operatorList.add(Operator.Assn);
            }
        }
        for(ParseNode child:node.getChildren()){
            int code=child.getToken().getCode();
            if(traverse(child,returnVal))
                outVal=true;
        }
        List<ParseNode> items=node.getChildren();
        if(returnVal){
            if(items.size()<2||items.get(items.size()-2).isLeaf()||
                    items.get(items.size()-2).getChildren().get(0).isLeaf())
                getErrorList().add(new Error(items.get(items.size()-1).getToken(),'g'));
            else{
                ParseNode ret=items.get(items.size()-2).getChildren().get(0).getChildren().get(0);
                if(ret.getToken().getCode()!=RETURNTK)
                    getErrorList().add(new Error(items.get(items.size()-1).getToken(),'g'));
            }
        }
        blkPointer=blkPointer.getFather();
        operatorList.add(Operator.Back);
    }
    private Symbol Exp(ParseNode node){
        Symbol result = null;
        int code=node.getToken().getCode();
        if(code== LVal){
            ParseNode firstElement=node.getChildren().get(0);
            int diffLayer=0;
            Symbol symbol= blkPointer.getSymbol(firstElement.getToken().getName(),true);
            if(symbol!=null)
                operatorList.add(Operator.Var(symbol));
            else{
                symbol= blkPointer.getSymbol(firstElement.getToken().getName());
                if(symbol!=null){
                    if(symbol.getLayer()==0){
                        operatorList.add(Operator.Global(symbol));
                    }else{
                        diffLayer=blkPointer.getLayer()-symbol.getLayer();
                        operatorList.add(Operator.Outer(symbol,diffLayer));
                    }
                }else{
                    getErrorList().add(new Error(firstElement.getToken(),'c'));
                }
            }
            if(symbol!=null){
                boolean isConst=symbol.isConst;
                Symbol a=null,b=null;
                for(ParseNode child:node.getChildren()){
                    if(child.getToken().getCode()==Exp){
                        if(a==null)
                            a=Exp(child);
                        else{
                            b=Exp(child);
                            break;
                        }
                    }
                }
                if(a!=null){
                    //Array[]
                    if(!a.isConst)
                        isConst=false;
                    if(b!=null){
                        //Array[][]
                        if(!b.isConst)
                            isConst=false;
                        result=new Symbol("Exp",isConst);
                        if(isConst)
                            result.setValue(symbol.getArray2()[a.getValue()][b.getValue()]);
                        return result;
                    }else{
                        if(symbol.getSize2()==0) {
                            result = new Symbol("Exp", isConst);
                            if(isConst)
                                result.setValue(symbol.getArray1()[a.getValue()]);
                        }else
                            if(isConst)
                                result=new Symbol("Exp",symbol.getSize2(),symbol.getArray2()[a.getValue()],isConst);
                            else
                                result=new Symbol("Exp",symbol.getSize2(),null);
                        return result;
                    }
                }else{
                    return symbol;
                }
            }else{
                // TODO: Exception
                return new Symbol("Exp");
            }
        }else if(code== PrimaryExp){
            // LVal is usually an address,
            // But here needs its value.
            ParseNode firstElement=node.getChildren().get(0);
            if(node.getChildren().size()>1){
                return Exp(node.getChildren().get(1));
            }else {
                Symbol val=Exp(firstElement);
                if(firstElement.getToken().getCode()==LVal){
                    operatorList.add(Operator.Val);
                }
                return val;
            }
        }else if(code== INTCON){
            result=new Symbol("Exp",true);
            result.setValue(Integer.parseInt(node.getToken().getName()));
            operatorList.add(Operator.Const(node.getToken().getName()));
            return result;
        }else if(code== UnaryExp){
            ParseNode firstElement=node.getChildren().get(0);
            if(node.getChildren().size()<2)
                return Exp(firstElement);
            if(firstElement.getToken().getCode()==IDENFR){
                // Function
                FuncCall(node);
                String name=firstElement.getToken().getName();
                for(Function func:functions){
                    if(func.getName().equals(name))
                        return func;
                }
                return new Symbol("Exp");
            }else if(firstElement.getToken().getCode()==UnaryOp){
                Symbol a=Exp(node.getChildren().get(1));
                if(a.isConst)
                    result=new Symbol("Exp",true);
                else
                    result=new Symbol("Exp");
                // Unary Operator
                int op=firstElement.getChildren().get(0).getToken().getCode();
                if(op==MINU){
                    result.setValue(-a.getValue());
                    operatorList.add(Operator.Neg);
                }else if(op==NOT){
                    result.setValue(a.getValue()==0?1:0);
                    operatorList.add(Operator.Not);
                }else if(op==PLUS){
                    result.setValue(a.getValue());
                    // Do nothing
                }
            }
            return result;
        }else if(code==AddExp||code==MulExp
                ||code==EqExp||code==RelExp
                ||code==LOrExp||code==LAndExp)
        {
            if(node.getChildren().size()<3)
                return Exp(node.getChildren().get(0));
            Symbol a=Exp(node.getChildren().get(0)),b=Exp(node.getChildren().get(2));
            if(a.isConst&&b.isConst)
                result=new Symbol("Exp",true);
            else
                result=new Symbol("Exp");
            int op=node.getChildren().get(1).getToken().getCode();
            if(op==MINU){
                result.setValue(a.getValue()-b.getValue());
                operatorList.add(Operator.Sub);
            }else if(op==PLUS){
                result.setValue(a.getValue()+b.getValue());
                operatorList.add(Operator.Add);
            }else if(op==MULT){
                result.setValue(a.getValue()*b.getValue());
                operatorList.add(Operator.Mul);
            }else if(op==DIV){
                result.setValue(a.getValue()/b.getValue());
                operatorList.add(Operator.Dev);
            }else if(op==MOD){
                result.setValue(a.getValue()%b.getValue());
                operatorList.add(Operator.Mod);
            }else if(op==GEQ){
                result.setValue(a.getValue()>=b.getValue()?1:0);
                operatorList.add(Operator.Geq);
            }else if(op==LEQ){
                result.setValue(a.getValue()<=b.getValue()?1:0);
                operatorList.add(Operator.Leq);
            }else if(op==GRE){
                result.setValue(a.getValue()>b.getValue()?1:0);
                operatorList.add(Operator.Gtr);
            }else if(op==LSS){
                result.setValue(a.getValue()<b.getValue()?1:0);
                operatorList.add(Operator.Les);
            }else if(op==EQL){
                result.setValue(a.getValue()==b.getValue()?1:0);
                operatorList.add(Operator.Eql);
            }else if(op==NEQ){
                result.setValue(a.getValue()!=b.getValue()?1:0);
                operatorList.add(Operator.Neq);
            }else if(op==AND){
                result.setValue(a.getValue()>0&&b.getValue()>0?1:0);
                operatorList.add(Operator.And);
            }else if(op==OR){
                result.setValue(a.getValue()>0||b.getValue()>0?1:0);
                operatorList.add(Operator.Or);
            }
            return result;
        }else if(code==Number||code==ConstExp
                ||code==Exp||code==Cond
                ||code==ConstInitVal||code==InitVal)
        {
            return Exp(node.getChildren().get(0));
        }
        return null;
    }
    private List<Integer> ConstInitValList(ParseNode node){
        List<Integer> val=new ArrayList<>();
        if(node.getToken().getCode()==ConstExp){
            val.add(Exp(node).getValue());
            return val;
        }else if(node.getToken().getCode()==ConstInitVal){
            for(ParseNode child:node.getChildren()){
                List<Integer> buf=ConstInitValList(child);
                for(Integer i:buf){
                    val.add(i);
                }
            }
            return val;
        }else
            return new ArrayList<>();
    }
    private boolean traverse(ParseNode node,boolean returnVal){
        int code=node.getToken().getCode();
        boolean out=false;

        if(code== FuncDef){
            FuncDef(node);
            return false;
        }else if(code== CompUnit){
            operatorList.add(Operator.Data(base));
            operatorList.add(Operator.Init);
        }else if(code== MainFuncDef){
            FuncDef(node);
            return false;
        }else if(code== Block){
            Block block=new Block();
            block.setFather(blkPointer);
            blkPointer=block;
            operatorList.add(Operator.Data(block));
        }else if(code== Stmt){
            ParseNode firstElement=node.getChildren().get(0);
            if(firstElement.getToken().getCode()==LVal){
                String name=firstElement.getChildren().get(0).getToken().getName();
                Symbol ident=blkPointer.getSymbol(name);
                if(ident!=null&&ident.isConst)
                    getErrorList().add(new Error(firstElement.getChildren().get(0).getToken(),'h'));
                else{
                    Exp(firstElement);
                    Exp(node.getChildren().get(2));
                    operatorList.add(Operator.Assn);
                }
                return false;
            }else if(firstElement.getToken().getCode()==IFTK){
                // if
                int ifno=ifNO++;
                out|=traverse(node.getChildren().get(2),returnVal);
                operatorList.add(Operator.Beqz("ELSE"+ifno));
                out|=traverse(node.getChildren().get(4),returnVal);
                operatorList.add(Operator.Jmp("ENDIF"+ifno));
                operatorList.add(Operator.Tag("ELSE"+ifno));
                if(node.getChildren().size()>6){
                    out|=traverse(node.getChildren().get(6),returnVal);
                }
                operatorList.add(Operator.Tag("ENDIF"+ifno));
                return out;
            }else if(firstElement.getToken().getCode()==WHILETK){
                // while
                int whileno=whileNO++;
                loopLayer++;
                operatorList.add(Operator.Tag("WHILE"+whileno));
                out|=traverse(node.getChildren().get(2),returnVal);
                operatorList.add(Operator.Beqz("ENDWHILE"+whileno));
                out|=traverse(node.getChildren().get(4),returnVal);
                operatorList.add(Operator.Jmp("WHILE"+whileno));
                operatorList.add(Operator.Tag("ENDWHILE"+whileno));
                loopLayer--;
                return out;
            }else if(firstElement.getToken().getCode()==BREAKTK){
                // break while
                int whileno=whileNO-1;
                if(loopLayer<1)
                    getErrorList().add(new Error(firstElement.getToken(),'m'));
                operatorList.add(Operator.Jmp("ENDWHILE"+whileno));
            }else if(firstElement.getToken().getCode()==CONTINUETK){
                // continue while
                int whileno=whileNO-1;
                if(loopLayer<1)
                    getErrorList().add(new Error(firstElement.getToken(),'m'));
                operatorList.add(Operator.Jmp("WHILE"+whileno));
            }

        }else if(code== GETINTTK){
            operatorList.add(Operator.Getint);
        }else if(code== VarDef){
            String name=node.getChildren().get(0).getToken().getName();
            Symbol symbol= blkPointer.getSymbol(name,true);
            if(symbol!=null) {
                getErrorList().add(new Error(node.getChildren().get(0).getToken(),'b'));
            }else{
                Symbol a=null,b=null;
                boolean assn=false;
                for(ParseNode child:node.getChildren()){
                    if(child.getToken().getCode()==ConstExp){
                        if(a==null)
                            a=Exp(child);
                        else{
                            b=Exp(child);
                            break;
                        }
                    }else if(child.getToken().getCode()==ASSIGN)
                        assn=true;
                }
                if(a!=null){
                    if(b!=null){
                        symbol=new Symbol(name,a.getValue(),b.getValue(),null);
                    }else{
                        symbol=new Symbol(name,a.getValue(),null);
                    }
                }else{
                    symbol=new Symbol(name);
                }
                blkPointer.addSymbol(symbol);
                operatorList.add(Operator.Var(symbol));
                if(assn){
                    operatorList.add(Operator.Assn);
                    traverse(node.getChildren().get(node.getChildren().size()-1),false);
                }
            }
            return false;
        }else if(code== ConstDef){
            String name=node.getChildren().get(0).getToken().getName();
            Symbol symbol= blkPointer.getSymbol(name,true);
            List<Integer> val = new ArrayList<>();
            if(symbol!=null) {
                getErrorList().add(new Error(node.getChildren().get(0).getToken(),'b'));
            }else{
                Symbol a=null,b=null;
                boolean assn=false;
                for(ParseNode child:node.getChildren()){
                    if(child.getToken().getCode()==ConstExp){
                        if(a==null)
                            a=Exp(child);
                        else if(b==null){
                            b=Exp(child);
                        }
                    }else if(child.getToken().getCode()==ConstInitVal){
                        val=ConstInitValList(child);
                    }
                }
                if(a!=null){
                    if(b!=null){
                        final int n=a.getValue(),m=b.getValue();
                        int v[][]=new int[n][m],t=0;
                        for(int i=0;i<n;i++)
                            for(int j=0;j<m;j++) {
                                v[i][j]=val.get(t);
                                t++;
                            }
                        symbol=new Symbol(name,a.getValue(),b.getValue(),v,true);
                    }else{
                        final int n=a.getValue();
                        int v[]=new int[n];
                        for(int i=0;i<n;i++){
                                v[i]=val.get(i);
                        }
                        symbol=new Symbol(name,a.getValue(),v,true);
                    }
                }else{
                    symbol=new Symbol(name,true);
                    symbol.setValue(val.get(0));
                }
                blkPointer.addSymbol(symbol);
            }
            return false;
        }else if(code==Number||code==ConstExp
                ||code==Exp||code==Cond
                ||code==ConstInitVal||code==InitVal){
            Exp(node);
            return false;
        }

        if(!node.isLeaf()){
            for(ParseNode child:node.getChildren())
                out|=traverse(child,returnVal);
        }

        if(code== Block){
            blkPointer=blkPointer.getFather();
            operatorList.add(Operator.Back);
        }else if(code== Stmt){
            ParseNode firstElement=node.getChildren().get(0);
            if(firstElement.getToken().getCode()==LVal){
                String name=firstElement.getChildren().get(0).getToken().getName();
                Symbol ident=blkPointer.getSymbol(name);
                if(ident!=null&&ident.isConst)
                    getErrorList().add(new Error(firstElement.getChildren().get(0).getToken(),'h'));
                else
                    operatorList.add(Operator.Assn);
            }else if(firstElement.getToken().getCode()==PRINTFTK){
                // TODO: error l
                Token format=node.getChildren().get(2).getToken();
                String string=format.getName();
                int paramNum=(node.getChildren().size()-5)/2;
                if(paramNum<0){
                    paramNum=0;
                }
                int len=string.length(),fParamNum=0;
                boolean stringErr=false;
                for(int i=1;i<len-1;i++){
                    char c=string.charAt(i),n=string.charAt(i+1);
                    if(c=='%'&&n=='d')
                        fParamNum++;
                    else if(c=='%')
                        stringErr=true;
                    else if(c=='\\'&&n!='n')
                        stringErr=true;
                    else if((c<40||c>126)&&c!=32&&c!=33)
                        stringErr=true;
                }
                if(stringErr)
                    getErrorList().add(new Error(format,'a'));
                if(fParamNum!=paramNum)
                    getErrorList().add(new Error(firstElement.getToken(),'l'));
                operatorList.add(Operator.Print(string));
            }else if(firstElement.getToken().getCode()==RETURNTK){
                operatorList.add(Operator.Ret);
                if(node.getChildren().size()>1&&node.getChildren().get(1).getToken().getCode()==Exp){
                    if(!returnVal)
                        getErrorList().add(new Error(firstElement.getToken(),'f'));
                    return true;
                }
            }
        }else if(code== LVal){
            ParseNode firstElement=node.getChildren().get(0);
            int diffLayer=0;
            Symbol symbol= blkPointer.getSymbol(firstElement.getToken().getName(),true);
            if(symbol!=null)
                operatorList.add(Operator.Var(symbol));
            else{
                symbol= blkPointer.getSymbol(firstElement.getToken().getName());
                if(symbol!=null){
                    if(symbol.getLayer()==0){
                        operatorList.add(Operator.Global(symbol));
                    }else{
                        diffLayer=blkPointer.getLayer()-symbol.getLayer();
                        operatorList.add(Operator.Outer(symbol,diffLayer));
                    }
                }else{
                    getErrorList().add(new Error(firstElement.getToken(),'c'));
                }
            }
            //  TODO: Array
        }else if(code== PrimaryExp){
            // LVal is usually an address,
            // But here needs its value.
            ParseNode firstElement=node.getChildren().get(0);
            if(firstElement.getToken().getCode()==LVal)
                operatorList.add(Operator.Val);
        }else if(code== INTCON){
            operatorList.add(Operator.Const(node.getToken().getName()));
        }else if(code== UnaryExp){
            if(node.getChildren().size()<2)
                return false;
            ParseNode firstElement=node.getChildren().get(0);
            if(firstElement.getToken().getCode()==IDENFR){
                // Function
                FuncCall(node);
            }else if(firstElement.getToken().getCode()==UnaryOp){
                // Unary Operator
                int op=firstElement.getChildren().get(0).getToken().getCode();
                if(op==MINU){
                    operatorList.add(Operator.Neg);
                }else if(op==NOT){
                    operatorList.add(Operator.Not);
                }else if(op==PLUS){
                    // Do nothing
                }
            }
        }else if(code==AddExp||code==MulExp
                ||code==EqExp||code==RelExp
                ||code==LOrExp||code==LAndExp)
        {
            if(node.getChildren().size()<2)
                return false;
            int op=node.getChildren().get(1).getToken().getCode();
            if(op==MINU){
                operatorList.add(Operator.Sub);
            }else if(op==PLUS){
                operatorList.add(Operator.Add);
            }else if(op==MULT){
                operatorList.add(Operator.Mul);
            }else if(op==DIV){
                operatorList.add(Operator.Dev);
            }else if(op==MOD){
                operatorList.add(Operator.Mod);
            }else if(op==GEQ){
                operatorList.add(Operator.Geq);
            }else if(op==LEQ){
                operatorList.add(Operator.Leq);
            }else if(op==GRE){
                operatorList.add(Operator.Gtr);
            }else if(op==LSS){
                operatorList.add(Operator.Les);
            }else if(op==EQL){
                operatorList.add(Operator.Eql);
            }else if(op==NEQ){
                operatorList.add(Operator.Neq);
            }else if(op==AND){
                operatorList.add(Operator.And);
            }else if(op==OR){
                operatorList.add(Operator.Or);
            }
        }
        return out;
    }
    public ParseTree getParseTree() {
        return parseTree;
    }

    public List<Operator> getOperatorList() {
        return operatorList;
    }
}
