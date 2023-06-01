public class RegisterFile {
    int[] generalPurposeRegisters;

    public RegisterFile(){ //check
        generalPurposeRegisters = new int[32];
    }

    public int[] getGeneralPurposeRegisters() {
        return generalPurposeRegisters;
    }

    public void setGeneralPurposeRegisters(int[] generalPurposeRegisters) {
        this.generalPurposeRegisters = generalPurposeRegisters;
    }

    public int getRegisterValue(int register){
        if(register>=0 && register<=31)
            return generalPurposeRegisters[register];
        else
            return -1;
    }
    public void saveRegisterValue(int value,int register){
        if (register>=1 && register<=31)
            generalPurposeRegisters[register]=value;
    }


}
