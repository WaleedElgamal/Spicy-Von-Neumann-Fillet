import java.io.BufferedReader;
import java.io.*;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;

public class Processor {
    MainMemory mainMemory;
    RegisterFile registers;
    ALU alu;
    ArrayList<Instruction> currentInstructions;
    int numOfInstructions;
    int cycles; //counts clk cycle for printing... need prints after incrementation...
    int pc; //should be global
    String updateRegisters;
    String updateMemory;

    boolean fetch,decode,execute,memory,writeBack;
    public Processor(){
        mainMemory = new MainMemory();
        registers = new RegisterFile();
        alu = new ALU();
        currentInstructions = new ArrayList<>();
        pc = 0;
        updateRegisters = "";
        updateMemory = "";
    }

    public void nextStageWithFetch() {
        // TODO handle fetch boolean and incrementing
        //TODO execute and decode in second clock cycle?
        updateRegisters= "";
        String instDetails = "";
        String stage = "";

        for (int i = 0; i < currentInstructions.size(); i++) {
            instDetails = "Instruction " + (i+1) + " in ";

            if(currentInstructions.get(i).getStage()[0]==0) //Fetch cycle 1
            {
               // System.out.println("first cycle");
                stage = "fetch";
                currentInstructions.get(i).setStage(0,1);
            }
          /* else if(currentInstructions.get(i).getStage()[0]==1) //Decode cycle 1
            {
                     currentInstructions.get(i).setStage(1,1);
            }*/
            else if(currentInstructions.get(i).getStage()[1]==1) //Decode cycle 2
            {
               // System.out.println("third cycle");
                stage = "decode";
                currentInstructions.get(i).decode(registers);
                currentInstructions.get(i).setStage(1,2);
            }
           /* else if(currentInstructions.get(i).getStage()[1]==2) //Execute cycle 1
            {
                    currentInstructions.get(i).setStage(2,1);
            }*/
            else if(currentInstructions.get(i).getStage()[2]==1) // Execute cycle 2
            {
               // System.out.println("fifth cycle");
                stage = "execute";
                int oldPC= currentInstructions.get(i).pc;     //registers.getPc();
                currentInstructions.get(i).execute(registers,alu); //wrong types?
                int newPC=  currentInstructions.get(i).pc;     //registers.getPc();
                currentInstructions.get(i).setStage(2,2);
                if(oldPC!=newPC)
                {
                  //  int oldsize= currentInstructions.size();
                    pc = newPC;
                    flushInstructions();
                    updateRegisters += "Updates in Registers: \n" +
                            "Old PC: " + oldPC + ", New PC: " + newPC + "\n";
                  //  int newsize= currentInstructions.size();
                    /*if(oldsize!=newsize)
                    {
                        i-=oldsize-newsize;
                    }
                    */
                    //TODO ^^ do we need this, or actually in the logic can we just remove every instruction after the one we are
                    //TODO at since the arraylist functions as a queue and no other instruction can be executing in cycle 1

                }
            }
            else if(currentInstructions.get(i).getStage()[4]==0)//WriteBack cycle 1
            {
                   // System.out.println("seventh cycle");
                    stage = "write back";
                    int oldRegValue = currentInstructions.get(i).valR1;
                    currentInstructions.get(i).writeBack(currentInstructions.get(i).getOpcode(),registers); //wrong types?
                    int newRegValue = currentInstructions.get(i).valR1;
                    currentInstructions.get(i).setStage(4,1);
                    if(oldRegValue!=newRegValue){
                        int regAddress = currentInstructions.get(i).r1;
                        updateRegisters += "Old R" + regAddress + " :" + oldRegValue + ", New R" + regAddress+ " :" + newRegValue;
                    }
            }
           /* else if(currentInstructions.get(i).getStage()[4]==1) //Finished execution
            {
                currentInstructions.remove(i);
            }*/
            instDetails += stage + " stage";
            System.out.println(instDetails);
        }
        if(!updateRegisters.equals(""))
            System.out.println(updateRegisters);
    }

    public void nextStageWithoutFetch() {
        updateRegisters = "";
        updateMemory = "";
        String instDetails = "";
        String stage = "";
        for (int i = 0; i < currentInstructions.size(); i++) {

            instDetails = "Instruction " + (i+1) + " in ";
            if(currentInstructions.get(i).getStage()[0]==1) //Decode cycle 1
            {
               // System.out.println("second cycle");
                stage = "decode";
                currentInstructions.get(i).setStage(1,1);
                currentInstructions.get(i).setStage(0,-1);
            }
            /*else if(currentInstructions.get(i).getStage()[1]==1) //Decode cycle 2
            {
                currentInstructions.get(i).decode(registers);
                currentInstructions.get(i).setStage(1,2);
            }*/
            else if(currentInstructions.get(i).getStage()[1]==2) //Execute cycle 1
            {
               // System.out.println("fourth cycle");
                stage = "execute";
                currentInstructions.get(i).setStage(2,1);
                currentInstructions.get(i).setStage(1,-1);
            }
           /* else if(currentInstructions.get(i).getStage()[2]==1) // Execute cycle 2
            {
                int oldPC= currentInstructions.get(i).pc;      //registers.getPc();
                currentInstructions.get(i).execute(registers,alu); //wrong types?
                int newPC=  currentInstructions.get(i).pc;     //registers.getPc();
                currentInstructions.get(i).setStage(2,2);
                if(oldPC!=newPC)
                {
                    pc = newPC;
                    flushInstructions();
                    updateRegisters += "Updates in Registers: \n" +
                            "Old PC: " + oldPC + ", New PC: " + newPC + "\n";
                }
            }*/
            else if(currentInstructions.get(i).getStage()[3]==0) // Memory cycle 1
            {
               // System.out.println("sixth cycle");
                stage = "memory";
                int memAddress = currentInstructions.get(i).valR2;
                int oldMemValue = mainMemory.getMainMemory(currentInstructions.get(i).valR2);
                currentInstructions.get(i).memory(mainMemory,registers); //wrong types?
                int newMemValue  = mainMemory.getMainMemory(currentInstructions.get(i).valR2);
                currentInstructions.get(i).setStage(3,-1);
                if(oldMemValue!=newMemValue){
                    updateMemory = "Updates in Memory: \n" +
                            "Change in address: " + memAddress + " from value: " + oldMemValue + " to value: " + newMemValue;
                }
            }
            /*else if(currentInstructions.get(i).getStage()[4]==0) //WriteBack cycle 1
            {
                int oldRegValue = currentInstructions.get(i).valR1;
                System.out.println(oldRegValue);
                currentInstructions.get(i).writeBack(currentInstructions.get(i).getOpcode(),registers); //wrong types?
                int newRegValue = currentInstructions.get(i).valR1;
                System.out.println(newRegValue);
                currentInstructions.get(i).setStage(4,1);
                if(oldRegValue!=newRegValue){
                    int regAddress = currentInstructions.get(i).r1;
                    updateRegisters += "Old R" + regAddress + " :" + oldRegValue + ", New R" + regAddress+ " :" + newRegValue;
                }
            }*/
            else if(currentInstructions.get(i).getStage()[4]==1) //Finished execution
            {
                currentInstructions.remove(i);
                i--; //will always remove instruction in even cycle,
                    // so need to decrement to access next instructions in same cycle
            }

            instDetails += stage + " stage";
            System.out.println(instDetails);
        }
        if(!updateRegisters.equals(""))
            System.out.println(updateRegisters);
        if(!updateMemory.equals(""))
            System.out.println(updateMemory);
    }

    public void flushInstructions(){
        for(int i=0;i<currentInstructions.size(); i++){
            if(currentInstructions.get(i).stage[2]==0){ // if instruction is in execute stage
                currentInstructions.remove(i);
                i--;
            }
        }
    }

    public void processorSim()  // TODO handle input output and initilisation , change to reading from memory, if on size condition
     {
          //  Filereader filereader = new Filereader();
         //   String s= filereader.read();
           // while(s!=null)
            {
               // for(int i=1;i<fr.size();i++)
                {   // if(i%2==1) //time to fetch
                    {
                        //Instruction instruction = new Instruction(Integer.parseInt(s));
                       // currentInstructions.add(instruction);
                    }

                //TODO call the next stage function depending on the iteration
                 }
               // s=filereader.read();
            }
     }


    private void parseFileIntoMemory(MainMemory memory) throws IOException {
        // todo use correct path for instruction file, use relative src/
        File file = new File("/Users/waleed/Desktop/test.txt");
        BufferedReader br = new BufferedReader(new FileReader(file));
        String st;
        int i = 0;
        while ((st = br.readLine()) != null) {
            // todo print value after parsing
            String[] stValues = st.split(" ");
            for(int j=0; j<stValues.length; j++){
                stValues[j]=stValues[j].toUpperCase();
            }
            String res = "";
            String opcode = parseOpcode(stValues[0]);
            res += opcode;
            if (stValues[0].equals("J")){
                String temp = Integer.toBinaryString(Integer.parseInt(stValues[1]));
                temp = String.format("%28s", temp).replaceAll(" ", "0");
                res += temp;
            }
            else {
                res += parseRegister(stValues[1]);
                res += parseRegister(stValues[2]);
                if (stValues[0].equals("ADD") || stValues[0].equals("SUB")) { //r type
                    res += parseRegister(stValues[3]); //R3
                    res += "0000000000000";
                } else if (stValues[0].equals("SLL") || stValues[0].equals("SRL")) {
                    res += "00000";
                    String temp = Integer.toBinaryString(Integer.parseInt(stValues[4]));
                    temp = String.format("%13s", temp).replaceAll(" ", "0");
                    res += temp;
                } else { //I type
                    String temp = Integer.toBinaryString(Integer.parseInt(stValues[3]));
                    temp = String.format("%18s", temp).replaceAll(" ", "0");
                    res += temp;
                }
            }
            memory.setMainMemory(new BigInteger(res, 2).intValue(),i);
            i++;
        }
        numOfInstructions = i; // storing the number of instructions in instruction file
        // will be used to calculate number of clock cycles
    }



    private String parseRegister(String stValue) {
        //TODO test the return statement by printing
        int val = Integer.parseInt(stValue.substring(1)) ;
        String temp = Integer.toBinaryString(val);
        temp = String.format("%5s", temp).replaceAll(" ", "0");
        return temp;
    }

    private String parseOpcode(String stValue) {
        switch (stValue){
            case "ADD": return "0000";
            case "SUB" : return "0001";
            case "MULI" : return "0010";
            case "ADDI" : return "0011";
            case "BNE" : return "0100";
            case "ANDI" : return "0101";
            case "OR" : return "0110";
            case "J" : return "0111";
            case "SLL" : return "1000";
            case "SRL" : return "1001";
            case "LW" : return "1010";
            case "SW" : return "1011";
        }
        return "";
    }

    private void printings() //Idk use this so we get print statements everytime??
    {
        //clk cycle, then the pipeline stages(Instruction & stage, input/value), register updates,
        // memory updates, registers after last clk, memory after last
        //System.out.println("Current clk cycle " + cycles);
        pipelineSeq();


    }

    private void pipelineSeq()
    {
        String[] prints = new String[5];
        Instruction[] stages = new Instruction[5];

        for(int i =0; i< currentInstructions.size(); i++) {
            Instruction inst = currentInstructions.get(i);
            //instruction & stage
            if (inst.getStage()[0] == 1) {
                prints[0] += "Instruction " + (inst.getAddress() + 1) + ". Parameters: ";
                stages[0] = inst;
                prints[0] += inst.printInstruction();
            } else if (inst.getStage()[1] == 1 || inst.getStage()[1] == 2) {
                prints[1] += "Instruction " + (inst.getAddress() + 1) + ". Parameters: ";
                stages[1] = inst;
                prints[1] += inst.printInstruction();
            } else if (inst.getStage()[2] == 1 || inst.getStage()[2] == 2) {
                prints[2] += "Instruction " + (inst.getAddress() + 1) + ". Parameters: ";
                stages[2] = inst;
                prints[2] += inst.printInstruction();
            } else if (inst.getStage()[3] == 1) {
                prints[3] += "Instruction " + (inst.getAddress() + 1) + ". Parameters: ";
                stages[3] = inst;
                prints[3] += inst.printInstruction();
            } else if (inst.getStage()[4] == 1) {
                prints[4] += "Instruction " + (inst.getAddress() + 1) + ". Parameters: ";
                stages[4] = inst;
                prints[4] += inst.printInstruction();
            }
        }
        for(int i=0; i<5; i++){
            if( stages[i]== null){
                prints[i] += "None";
            }
        }

        System.out.println("Pipeline Stages: ");
        System.out.println("Instruction Fetch: " + prints[0]+
                "\n Instruction Decode: " + prints[1] +
                "\n Execute: " + prints[2]+
                "\n Memory: "+ prints[3]+
                "\n Write back: " + prints[4]);
    }



   // either we call fetch after calling nextStage with fetch so all the others get executed
    // or we call fetch in nextSTagewithFetch and put the if condition that if i+1=size then i call fetch else the other if conditions




    public void fetch(){
        int res= mainMemory.getMainMemory(pc);       //this.mainMemory.getMainMemory(this.registers.getPc());
        //this.registers.setPc(this.registers.getPc()+1);
        pc++;
        Instruction instruction = new Instruction(res,pc);
        currentInstructions.add(instruction);
    }

    public static void main(String[] args) throws IOException{ //TODO will we be given the clock cycle/ instruction count?
        Processor processor = new Processor();
        processor.parseFileIntoMemory(processor.mainMemory);
        int numOfClockCycles = 7 + ((processor.numOfInstructions-1)*2);
        int clockCycle = 1;

        while(clockCycle<=numOfClockCycles){
            System.out.println("Current clock cycle: " + clockCycle);
          //  processor.printings();
            if(clockCycle%2==1) {
                if (processor.currentInstructions.size() < 5) {//TODO ask ta that this should automatically be satisfied{
                    if (processor.numOfInstructions > 0){
                        processor.fetch();
                        processor.numOfInstructions--;
                    }
                    processor.nextStageWithFetch(); //TODO would this cause me to add more instructions than i can handle
                }
            }
            else {
                processor.nextStageWithoutFetch();
            }
            clockCycle++;
        }

        System.out.println("Register contents after last clock cycle: \n" +
                Arrays.toString(processor.registers.getGeneralPurposeRegisters()));
        System.out.println("Memory contents after last clock cycle: \n" +
                Arrays.toString(processor.mainMemory.getMainMemory()));
    }
}

/*Addi r2 r0 6
Add r3 r1 r2
Muli r4 r2 6*/
