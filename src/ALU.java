public class ALU {

    public ALU(){ //check

    }

    public int execute(int opcode, int val1, int val2){
        switch (opcode){
            case 0:
            case 3:
            case 10:
            case 11: return val1 + val2;
            case 1:
            case 4: return val1 - val2;
            case 2: return val1 * val2;
            case 5: return (val1 & val2);
            case 6: case 7: return (val1 | val2);
            case 8: return (val1 << val2);
            case 9: return (val1 >> val2);
        }
        return -1; //?
    }
}
