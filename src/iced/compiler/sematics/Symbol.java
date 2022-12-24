package iced.compiler.sematics;

import java.util.Objects;

public class Symbol {
    public static final int TYPE_OFFSET=128,INT=TYPE_OFFSET+1,VOID=TYPE_OFFSET+2,ARRAY=TYPE_OFFSET+3;
    public final boolean isConst;
    private final String name;
    private final int type;
    // Not a function if paramNum==-1
    private final int paramNum;
    // Simple variety of function if size1==0
    private final int size1;
    private final int size2;
    private final int size;
    private int layer;
    private long address;
    private int offset;
    private int value,array1[],array2[][];

    public Symbol(String name, int type, int paramNum, int size1, int size2, int layer, long address, int[][] value) {
        this.name = name;
        this.type = type;
        this.paramNum = paramNum;
        this.size1 = size1;
        this.size2 = size2;
        this.layer = layer;
        this.address = address;
        this.size=size1*size2;
        this.isConst=false;
    }
    // Var
    public Symbol(String name) {
        this.name = name;
        this.type = INT;
        this.paramNum = -1;
        this.size1 = 0;
        this.size2 = 0;
        this.size=4;
        this.isConst=false;
//        this.value[0][0] = value;
    }
    // Const
    public Symbol(String name,boolean isConst) {
        this.name = name;
        this.type = INT;
        this.paramNum = -1;
        this.size1 = 0;
        this.size2 = 0;
        this.size=4;
        this.isConst=isConst;
    }
    // Function
    public Symbol(String name, int type, int paramNum) {
        this.name = name;
        this.type = type;
        this.paramNum = paramNum;
        this.size1 = 0;
        this.size2 = 0;
        this.size=4;
        this.isConst=true;
    }
    // Array[]
    public Symbol(String name,int size1, int[] array) {
        this.name = name;
        this.type = ARRAY;
        this.paramNum = -1;
        this.size1 = size1;
        this.size2 = 0;
        this.array1 = array!=null?array:new int[size1];
        this.size=size1;
        this.isConst=false;
    }
    // Array[][]
    public Symbol(String name,int size1, int size2, int[][] array) {
        this.name = name;
        this.type = ARRAY;
        this.paramNum = -1;
        this.size1 = size1;
        this.size2 = size2;
        this.array2 = array!=null?array:new int[size1][size2];
        this.size=size1*size2;
        this.isConst=false;
    }
    // const Array[]
    public Symbol(String name,int size1, int[] array,boolean isConst) {
        this.name = name;
        this.type = ARRAY;
        this.paramNum = -1;
        this.size1 = size1;
        this.size2 = 0;
        this.size=size1;
        this.array1 = array!=null?array:new int[size1];
        this.isConst=isConst;
    }
    // const Array[][]
    public Symbol(String name,int size1, int size2, int[][] array,boolean isConst) {
        this.name = name;
        this.type = ARRAY;
        this.paramNum = -1;
        this.size1 = size1;
        this.size2 = size2;
        this.array2 = array!=null?array:new int[size1][size2];
        this.size=size1*size2;
        this.isConst=isConst;
    }

    public long getAddress() {
        return address;
    }

    public void setAddress(long address) {
        this.address = address;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getSize() {
        return size;
    }

    public String getName() {
        return name;
    }

    public int getType() {
        return type;
    }

    public int getParamNum() {
        return paramNum;
    }

    public int getSize1() {
        return size1;
    }

    public int getSize2() {
        return size2;
    }

    public int getLayer() {
        return layer;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int[][] getArray2() {
        return array2;
    }

    public void setArray2(int[][] array) {
        this.array2 = array;
    }
    public int[] getArray1() {
        return array1;
    }

    public void setArray1(int[] array) {
        this.array1 = array;
    }

    public void setLayer(int layer) {
        this.layer = layer;
    }

    public boolean isConst() {
        return isConst;
    }
}
