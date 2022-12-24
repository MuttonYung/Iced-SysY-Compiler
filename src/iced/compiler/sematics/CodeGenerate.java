package iced.compiler.sematics;

import iced.compiler.parser.ParseNode;
import iced.compiler.parser.ParseTree;

import java.util.ArrayList;
import java.util.List;

public class CodeGenerate extends Operator{
    private final List<Operator> operatorList;
    private final List<String> codeList;
    private final String STACK_BOTTOM="int",END="END_OF_TEXT";
    private int tempTag=0;

    public CodeGenerate(List<Operator> operatorList) {
        super(CODE_GENERATE);
        this.operatorList = operatorList;
        codeList=new ArrayList<>();
        codeList.add(".data");
        codeList.add(STACK_BOTTOM+": .word 0");
        codeList.add(".text");
    }
    public void build(){
        for(Operator operator:operatorList){
//            codeList.add("move $fp,$sp");
            if(operator.getType()==VAR){
                codeList.add("subiu $t0,$fp,"+(operator.getSymbol().getOffset()+4));
                codeList.add("subiu $sp,$sp,4");
                codeList.add("sw $t0,0($sp)");
            }else if(operator.getType()==GLOBAL){
                codeList.add("lw $t1,"+STACK_BOTTOM);
                codeList.add("subiu $t0,$t1,"+(operator.getSymbol().getOffset()+4));
                codeList.add("subiu $sp,$sp,4");
                codeList.add("sw $t0,0($sp)");
            }else if(operator.getType()==OUTER){
                codeList.add("lw $t1,-4($fp)");
                for(int i=1;i<operator.getDiffLayer();i++){
                    codeList.add("lw $t1,-4($t1)");
                }
                codeList.add("subiu $t0,$t1,"+(operator.getSymbol().getOffset()+4));
                codeList.add("subiu $sp,$sp,4");
                codeList.add("sw $t0,0($sp)");
            }else if(operator.getType()==CONST){
                codeList.add("li $t0,"+operator.getName());
                codeList.add("subiu $sp,$sp,4");
                codeList.add("sw $t0,0($sp)");
            }else if(operator.getType()==TAG){
                codeList.add(operator.getName()+':');
            }else if(operator.getType()==ADD
                    ||operator.getType()==SUB
                    ||operator.getType()==MUL
                    ||operator.getType()==DEV
                    ||operator.getType()==MOD
                    ||operator.getType()==GEQ
                    ||operator.getType()==LEQ
                    ||operator.getType()==GTR
                    ||operator.getType()==LES
                    ||operator.getType()==NEQ
                    ||operator.getType()==EQL
                    ||operator.getType()==AND
                    ||operator.getType()==OR)
            {
                codeList.add("lw $t0,4($sp)");
                codeList.add("lw $t1,0($sp)");
                switch (operator.getType()){
                    case ADD:
                        codeList.add("add $t2,$t0,$t1");
                        break;
                    case SUB:
                        codeList.add("sub $t2,$t0,$t1");
                        break;
                    case MUL:
                        codeList.add("mul $t2,$t0,$t1");
                        break;
                    case DEV:
                        codeList.add("div $t0,$t1");
                        codeList.add("mflo $t2");
                        break;
                    case MOD:
                        codeList.add("div $t0,$t1");
                        codeList.add("mfhi $t2");
                        break;
                    case GEQ:
                        codeList.add("sge $t2,$t0,$t1");
                        break;
                    case LEQ:
                        codeList.add("sle $t2,$t0,$t1");
                        break;
                    case GTR:
                        codeList.add("sgt $t2,$t0,$t1");
                        break;
                    case LES:
                        codeList.add("slt $t2,$t0,$t1");
                        break;
                    case NEQ:
                        codeList.add("sne $t2,$t0,$t1");
                        break;
                    case EQL:
                        codeList.add("seq $t2,$t0,$t1");
                        break;
                    case AND:
                        codeList.add("and $t2,$t0,$t1");
                        break;
                    case OR:
                        codeList.add("or $t2,$t0,$t1");
                        break;
                }
                codeList.add("addiu $sp,$sp,4");
                codeList.add("sw $t2,0($sp)");
            }else if(operator.getType()==JMP){
                codeList.add("j "+operator.getName());
            }else if(operator.getType()==ASSN){
                codeList.add("lw $t0,4($sp)");
                codeList.add("lw $t1,0($sp)");
                codeList.add("sw $t1,0($t0)");
                codeList.add("addiu $sp,$sp,4");
                codeList.add("sw $t1,0($sp)");
            }else if(operator.getType()==RASSN){
                codeList.add("lw $t0,0($sp)");
                codeList.add("lw $t1,4($sp)");
                codeList.add("sw $t1,0($t0)");
                codeList.add("addiu $sp,$sp,8");
            }else if(operator.getType()==NOT){
                codeList.add("lw $t0,0($sp)");
                codeList.add("not $t1,$t0");
                codeList.add("sw $t1,0($sp)");
            }else if(operator.getType()==NEG){
                codeList.add("lw $t0,0($sp)");
                codeList.add("negu $t1,$t0");
                codeList.add("sw $t1,0($sp)");
            }else if(operator.getType()==RET){
                codeList.add("lw $v0,0($sp)");
                codeList.add("lw $ra,-8($fp)");
                codeList.add("lw $sp,-16($fp)");
                codeList.add("lw $fp,-12($fp)");

                codeList.add("subiu $sp,$sp,4");
                codeList.add("sw $v0,0($sp)");
                codeList.add("jr $ra");
            }else if(operator.getType()==JAL){
                codeList.add("jal "+operator.getName());
            }else if(operator.getType()==VAL){
                codeList.add("lw $t0,0($sp)");
                codeList.add("lw $t1,0($t0)");
                codeList.add("sw $t1,0($sp)");
            }else if(operator.getType()==BACK){
                codeList.add("move $sp,$fp");
                codeList.add("lw $fp,-4($sp)");
            }else if(operator.getType()==SWRT){
                codeList.add("sw $sp,-16($sp)");
                codeList.add("sw $fp,-12($sp)");
                codeList.add("sw $ra,-8($sp)");

                codeList.add("sw $fp,-4($sp)");
                codeList.add("move $fp,$sp");
                codeList.add("subiu $sp,$sp,"+(new Block().getDataSize()+4));
            }else if(operator.getType()==PARAM){
                codeList.add("lw $t0,-16($fp)");

                codeList.add("lw $t1,0($t0)");
                codeList.add("addiu $t0,$t0,4");

                codeList.add("subiu $sp,$sp,4");
                codeList.add("sw $t1,0($sp)");

                codeList.add("sw $t0,-16($fp)");
            }else if(operator.getType()==DATA){
                codeList.add("lw $s0,-16($fp)");
                codeList.add("lw $s1,-12($fp)");
                codeList.add("lw $s2,-8($fp)");

                codeList.add("sw $s0,-16($sp)");
                codeList.add("sw $s1,-12($sp)");
                codeList.add("sw $s2,-8($sp)");
                codeList.add("sw $fp,-4($sp)");
                codeList.add("move $fp,$sp");

                codeList.add("subiu $sp,$sp,"+(operator.getBlock().getDataSize()+4));
            }else if(operator.getType()==PRINT){
                String string=operator.getName();
                StringBuilder result= new StringBuilder();
                int len=string.length(),paramNum=0;
                for(int i=1;i<len-1;i++){
                    char c=string.charAt(i);
                    if(c=='\\'){
                        i++;
                        c=string.charAt(i);
                        if(c=='n')
                            c='\n';
                    }else if(c=='%'){
                        i++;
                        c=string.charAt(i);
                        if(c=='d'){
                            paramNum++;
                            c='%';
                        }
                    }
                    result.append(c);
                }
                string=result.toString();
                len=string.length();
                int n=0;
                codeList.add("subiu $a0,$sp,"+(len+2));
                codeList.add("move $t0,$a0");
                for(int i=0;i<len;i++){
                    char c=string.charAt(i);
                    if(c=='%'){
                        n++;
                        codeList.add("sb $0,0($t0)");                       // end of string
                        codeList.add("li $v0,4");                           // syscall to print string
                        codeList.add("syscall");

                        codeList.add("move $t0,$a0");
                        codeList.add("lw $a0,"+((paramNum-n)*4)+"($sp)");   // load param from stack
                        codeList.add("li $v0,1");                           // syscall to print int
                        codeList.add("syscall");
                        codeList.add("move $a0,$t0");
                    }else{
                        codeList.add("li $t1,"+ (int) c);
                        codeList.add("sb $t1,0($t0)");
                        codeList.add("addiu $t0,$t0,1");
                    }
                }
                codeList.add("sb $0,0($t0)");
                codeList.add("li $v0,4");
                codeList.add("syscall");
            }else if(operator.getType()==GETINT){
                codeList.add("li $v0,5");
                codeList.add("syscall");
                codeList.add("subiu $sp,$sp,4");
                codeList.add("sw $v0,0($sp)");
            }else if(operator.getType()==BEQZ){
                codeList.add("lw $t0,0($sp)");
                codeList.add("beqz $t0,"+operator.getName());
            }else if(operator.getType()==INIT){
                codeList.add("sw $fp,"+STACK_BOTTOM);
            }
        }
        codeList.add(END+":");
    }

    public List<String> getCodeList() {
        return codeList;
    }
}
