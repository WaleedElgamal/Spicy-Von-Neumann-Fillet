public class Instruction {
    int[] stage;
    int instruction;
    int opcode;
    int r1,r2,r3;
    int valR1,valR2,valR3;
    int shamt;
    int immediate;
    int address;
    InstructionType instructionType;

    public Instruction(int instruction){
        this.instruction = instruction;
        stage = new int[5];
        opcode = -1;
        r1 = -1;
        r2 = -1;
        r3 = -1;
        valR1 = -1;
        valR2 = -1;
        valR3 = -1;
        shamt = -1;
        immediate = -1;
        address = -1;
    }

    public void decode(RegisterFile registerFile){
        opcode = (instruction & 0b11110000000000000000000000000000) >>> 28;
        switch (opcode){
            case 0:
            case 1:
            case 8:
            case 9: {instructionType = InstructionType.R;
                    r1 = (instruction & 0b00001111100000000000000000000000) >>> 23;
                    r2 = (instruction & 0b00000000011111000000000000000000) >>> 18;
                    r3 = (instruction & 0b00000000000000111110000000000000) >>> 13;
                    shamt = (instruction & 0b00000000000000000001111111111111);
                    break;}
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 10:
            case 11: {instructionType = InstructionType.I;
                    r1 = (instruction & 0b00001111100000000000000000000000) >>> 23;
                    r2 = (instruction & 0b00000000011111000000000000000000) >>> 18;
                    immediate = (instruction & 0b00000000000000111111111111111111);
                    break;}
            case 7: {instructionType = InstructionType.J;
                    address = (instruction & 0b00001111111111111111111111111111);
                    break;}
        }

        switch (instructionType){
            case R: {valR1= registerFile.getRegisterValue(r1);
                    valR2= registerFile.getRegisterValue(r2);
                    valR3= registerFile.getRegisterValue(r3);
                    break;}
            case I: {valR1= registerFile.getRegisterValue(r1);
                    valR2= registerFile.getRegisterValue(r2);
                    break;}
        }
    }

    public void execute(RegisterFile registerFile, ALU alu){

    }


}
