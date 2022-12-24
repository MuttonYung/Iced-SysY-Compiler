package iced.compiler.sematics;

public class Operator {
    private static final int OP_OFFSET=256;
    protected static final int VAR=OP_OFFSET+1,CONST=OP_OFFSET+2,TAG=OP_OFFSET+3,
            ADD=OP_OFFSET+4,SUB=OP_OFFSET+5,POS=OP_OFFSET+6,
            NEG=OP_OFFSET+7,MUL=OP_OFFSET+8,DEV=OP_OFFSET+9,
            MOD=OP_OFFSET+10,GTR=OP_OFFSET+11,LES=OP_OFFSET+12,
            EQL=OP_OFFSET+13,GEQ=OP_OFFSET+14,LEQ=OP_OFFSET+15,
            NEQ=OP_OFFSET+16,AND=OP_OFFSET+17,OR=OP_OFFSET+18,
    //      jump,            -,        stack[top-1]=stack[top]
            JMP=OP_OFFSET+19,FUNC=OP_OFFSET+20,ASSN=OP_OFFSET+21,
            EXP=OP_OFFSET+22,NOT=OP_OFFSET+23,LW=OP_OFFSET+24,
            JAL=OP_OFFSET+25,BEQZ=OP_OFFSET+26,JR=OP_OFFSET+27,
    //      get value,       return,          save return address,
            VAL=OP_OFFSET+28,BACK=OP_OFFSET+29,SWRT=OP_OFFSET+30,
    //      build data stack, reset data stack    getint()
            DATA=OP_OFFSET+31,UNDATA=OP_OFFSET+32,GETINT=OP_OFFSET+33,
    //      not local variety,  *stack[top]=stack[top-1]
            GLOBAL=OP_OFFSET+34,RASSN=OP_OFFSET+35,CODE_GENERATE=OP_OFFSET+36,
            PRINT=OP_OFFSET+37,INIT=OP_OFFSET+38,OUTER=OP_OFFSET+39,
            RET=OP_OFFSET+40,PARAM=OP_OFFSET+41;
    public static final Operator Add=new Operator(ADD),
            Sub=new Operator(SUB),
            Pos=new Operator(POS),
            Neg=new Operator(NEG),
            Mul=new Operator(MUL),
            Dev=new Operator(DEV),
            Mod=new Operator(MOD),
            Gtr=new Operator(GTR),
            Les=new Operator(LES),
            Eql=new Operator(EQL),
            Geq=new Operator(GEQ),
            Leq=new Operator(LEQ),
            Neq=new Operator(NEQ),
            And=new Operator(AND),
            Or=new Operator(OR),
            Jmp=new Operator(JMP),
            Not=new Operator(NOT),
            Lw=new Operator(LW),
            Val=new Operator(VAL),
            Swrt=new Operator(SWRT),
            Back=new Operator(BACK),
            Getint=new Operator(GETINT),
            Rassn=new Operator(RASSN),
            Init=new Operator(INIT),
            Ret=new Operator(RET),
            Param=new Operator(PARAM),
            Assn=new Operator(ASSN);
    private final String name;
    private final int type;
    private Operator left,right;
    private Symbol symbol;
    private Block block;
    private int diffLayer;
    private int val;

//    public Operator(Symbol symbol) {
//        this.symbol = symbol;
//        name=symbol.getName();
//        type=VAR;
//    }


    public Operator(String name, int type) {
        this.name = name;
        this.type = type;
    }
    public Operator(int type){
        this.name = "";
        this.type = type;
    }

    public static Operator Var(Symbol symbol) {
        Operator op=new Operator(symbol.getName(),VAR);
        op.symbol = symbol;
        return op;
    }

    public static Operator Global(Symbol symbol) {
        Operator op=new Operator(symbol.getName(),GLOBAL);
        op.symbol = symbol;
        return op;
    }

    public static Operator Outer(Symbol symbol,int diffLayer) {
        Operator op=new Operator(symbol.getName(),OUTER);
        op.symbol = symbol;
        op.diffLayer=diffLayer;
        return op;
    }
    public static Operator Print(String name){
        return new Operator(name,PRINT);
    }

    public static Operator Tag(String name){
        return new Operator(name,TAG);
    }

    public static Operator Const(String name){
        return new Operator(name,CONST);
    }

    public static Operator Jal(String name){
        return new Operator(name,JAL);
    }

    public static Operator Beqz(String name){
        return new Operator(name,BEQZ);
    }

    public static Operator Jmp(String name){
        return new Operator(name,JMP);
    }

    public static Operator Data(Block block){
        Operator op=new Operator(DATA);
        op.block=block;
        return op;
    }

    public static Operator UnData(){
        Operator op=new Operator(UNDATA);
        return op;
    }
    public void left(Operator op){
        left=op;
    }

    public void right(Operator op){
        right=op;
    }

    public String getName() {
        return name;
    }

    public int getType() {
        return type;
    }

    public Symbol getSymbol() {
        return symbol;
    }

    public Block getBlock() {
        return block;
    }

    public int getDiffLayer() {
        return diffLayer;
    }

    public int getVal() {
        return val;
    }
}
