public class Instruction {
    int[] stage;
    int instruction;
    int opcode;
    int r1, r2, r3;
    int valR1, valR2, valR3;
    int shamt;
    int immediate;
    int address;
    int tempValue;
    int pc; //need to store pc value and pass it through pipeline registers in case of a branch or jump instruction
    InstructionType instructionType;
    static int instructionCount =0;
    boolean flush=false;
    int instructionID;
    int checkBit;

    public Instruction(int instruction, int pc) { //check
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
        tempValue = -1;
        this.pc = pc;
        instructionCount+=1;
        instructionID= instructionCount;
    }
    public static int getInstructionCount() {
        return instructionCount;
    }

    public static void setInstructionCount(int instructionCount) {
        Instruction.instructionCount = instructionCount;
    }
    public int getTempValue() {
        return tempValue;
    }

    public void setTempValue(int tempValue) {
        this.tempValue = tempValue;
    }

    public boolean isFlush() {
        return flush;
    }

    public void setFlush(boolean flush) {
        this.flush = flush;
    }

    public int getInstructionID() {
        return instructionID;
    }

    public void setInstructionID(int instructionID) {
        this.instructionID = instructionID;
    }

    public void decode(RegisterFile registerFile) {
        opcode = (instruction & 0b11110000000000000000000000000000) >>> 28;
        switch (opcode) {
            case 0:
            case 1:
            case 8:
            case 9: {
                instructionType = InstructionType.R;
                break;
            }
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 10:
            case 11: {
                instructionType = InstructionType.I;
                break;
            }
            case 7: {
                instructionType = InstructionType.J;
                break;
            }
        }

        r1 = (instruction & 0b00001111100000000000000000000000) >>> 23;
        r2 = (instruction & 0b00000000011111000000000000000000) >>> 18;
        r3 = (instruction & 0b00000000000000111110000000000000) >>> 13;
        shamt = (instruction & 0b00000000000000000001111111111111);
        checkBit = (instruction & 0b000000000000001000000000000000) >>> 15;
        immediate = (instruction & 0b00000000000000111111111111111111);
        if(checkBit== 1) {

            immediate = (instruction | 0b11111111111111000000000000000000);
        }
        //todo print immediate here to check if value is negative after bitmasking, might have to do sign-extend
        address = (instruction & 0b00001111111111111111111111111111);

        switch (instructionType) {
            case R: {
                valR1 = registerFile.getRegisterValue(r1);
                valR2 = registerFile.getRegisterValue(r2);
                valR3 = registerFile.getRegisterValue(r3);
                break;
            }
            case I: {
                valR1 = registerFile.getRegisterValue(r1);
                valR2 = registerFile.getRegisterValue(r2);
                break;
            }
        }
    }

    public void execute(RegisterFile registerFile, ALU alu) {
        // int result=0;
        switch (opcode) {
            case 0:
            case 1:
                tempValue = alu.execute(opcode, valR2, valR3); //add,subtract
                break;
            case 2:
            case 3:
            case 5:
            case 6:
            case 10:
            case 11:
                tempValue = alu.execute(opcode, valR2, immediate); //multi,addi, andi, ori
                break;
            case 4:
                tempValue = alu.execute(opcode, valR2, immediate); //bne i
                if (tempValue != 0) {
                    int currPCValue = pc;  //registerFile.getPc();
                    tempValue = alu.execute(0, currPCValue, immediate);
                    pc = tempValue;
                }
                break;
            case 7:
                int currPCValue = pc;  //registerFile.getPc(); //jump
                int temp = currPCValue & 0b11110000000000000000000000000000; // ask about this
                tempValue = alu.execute(opcode, temp, address); //print to check + fix bit issue
                pc = tempValue;
                break;
            case 8:
            case 9:
                tempValue = alu.execute(opcode, valR2, shamt); //sll,srl
                break;
           /* case 10:
            case 11:
                tempValue = alu.execute(opcode, valR2, immediate); //storing summation in both in valR2
                break;*/  //repeated statement
            //default: break;
        }
    }

    public void memory(MainMemory mainMemory, RegisterFile registerFile) {
        switch (opcode) {
            case 10:
                tempValue = mainMemory.getMainMemory(tempValue);
                break;
            case 11:
                mainMemory.setMainMemory(valR1, tempValue);
                break;
        }
    }

    public void writeBack(int opcode, RegisterFile registerFile) {
        switch (opcode) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 5:
            case 6:
            case 8:
            case 9:
            case 10: {
                registerFile.saveRegisterValue(tempValue, r1);
                if (r1 != 0)
                    valR1 = tempValue;
                break;
            }
        }
    }
    public String printInstruction()
    {

        switch(this.instructionType)
        {
            case R:
                    return ("RS: " + this.getValR2() + ", RT: " + this.getValR3() +
                    ", RD: " + this.getValR1() + ", SHAMT: " + this.getShamt() );
            case I:
                return ("RS: " + this.getValR2() + ", RT: " + this.getValR1() +
                    ", IMMEDIATE: " +this.getImmediate());
            case J:
                return ("ADDRESS: " + this.getAddress());

        }
        return "";
    }

    public static int twosComplement(final int value) {
        final int mask = 0xffff_ffff;
        return (value ^ mask) + 1;
    }

    public int[] getStage() {
        return stage;
    }

    public void setStage(int[] stage) {
        this.stage = stage;
    }

    public void setStage(int stage, int value){
        this.stage[stage] = value;
    }

    public int getInstruction() {
        return instruction;
    }

    public void setInstruction(int instruction) {
        this.instruction = instruction;
    }

    public int getOpcode() {
        return opcode;
    }

    public void setOpcode(int opcode) {
        this.opcode = opcode;
    }

    public int getR1() {
        return r1;
    }

    public void setR1(int r1) {
        this.r1 = r1;
    }

    public int getR2() {
        return r2;
    }

    public void setR2(int r2) {
        this.r2 = r2;
    }

    public int getR3() {
        return r3;
    }

    public void setR3(int r3) {
        this.r3 = r3;
    }

    public int getValR1() {
        return valR1;
    }

    public void setValR1(int valR1) {
        this.valR1 = valR1;
    }

    public int getValR2() {
        return valR2;
    }

    public void setValR2(int valR2) {
        this.valR2 = valR2;
    }

    public int getValR3() {
        return valR3;
    }

    public void setValR3(int valR3) {
        this.valR3 = valR3;
    }

    public int getShamt() {
        return shamt;
    }

    public void setShamt(int shamt) {
        this.shamt = shamt;
    }

    public int getImmediate() {
        return immediate;
    }

    public void setImmediate(int immediate) {
        this.immediate = immediate;
    }

    public int getAddress() {
        return address;
    }

    public void setAddress(int address) {
        this.address = address;
    }

    public int getPc() {
        return pc;
    }

    public void setPc(int pc) {
        this.pc = pc;
    }

    public InstructionType getInstructionType() {
        return instructionType;
    }

    public void setInstructionType(InstructionType instructionType) {
        this.instructionType = instructionType;
    }
}



