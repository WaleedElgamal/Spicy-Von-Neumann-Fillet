public class RegisterFile {
    int[] generalPurposeRegisters;
    final int r0 = 0;
    int pc;

    public RegisterFile(){ //check
        generalPurposeRegisters = new int[31];
        pc=0;
    }

    public int[] getGeneralPurposeRegisters() {
        return generalPurposeRegisters;
    }

    public void setGeneralPurposeRegisters(int[] generalPurposeRegisters) {
        this.generalPurposeRegisters = generalPurposeRegisters;
    }

    public int getR0() {
        return r0;
    }

    public int getPc() {
        return pc;
    }

    public void setPc(int pc) {
        this.pc = pc;
    }

    public int getRegisterValue(int register){
        if(register==0){
            return 0;
        }
        else if (register>=1 && register<=32){
            return generalPurposeRegisters[register-1];
        }
        return -1;
    }
    public void saveRegisterValue(int value,int register){
        if (register!=0)
        generalPurposeRegisters[register-1]=value;
    }


}
