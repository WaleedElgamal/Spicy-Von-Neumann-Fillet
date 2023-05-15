public class RegisterFile {
    int[] generalPurposeRegisters;
   // final int r0 = 0; i think we'll need to have it as part of the registers array
                    //since we can use it as source/ destination in instructions
    //int pc; should be global

    public RegisterFile(){ //check
        generalPurposeRegisters = new int[32];
        //pc=0;
    }

    public int[] getGeneralPurposeRegisters() {
        return generalPurposeRegisters;
    }

    public void setGeneralPurposeRegisters(int[] generalPurposeRegisters) {
        this.generalPurposeRegisters = generalPurposeRegisters;
    }

  /*  public int getR0() {
        return r0;
    }

    public int getPc() {
        return pc;
    }

    public void setPc(int pc) {
        this.pc = pc;
    } */

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
