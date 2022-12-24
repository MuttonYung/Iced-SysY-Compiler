package iced.compiler.sematics;

import java.util.ArrayList;
import java.util.List;

public class Function extends Symbol{
    private final List<Symbol> paramList=new ArrayList<>();
    public Function(String name, int type, int paramNum){
        super(name,type,paramNum);
    }
    public void addParam(Symbol symbol){
        paramList.add(symbol);
    }
    public List<Symbol> getParamList() {
        return paramList;
    }
}
