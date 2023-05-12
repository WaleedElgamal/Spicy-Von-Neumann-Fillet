import java.util.ArrayList;

public class Processor {
    MainMemory mainMemory;
    RegisterFile registers;
    ALU alu;
    ArrayList<Instruction> currentInstructions;
    int numOfInstructions;
    int cycles; //counts clk cycle for printing... need prints after incrementation...

    boolean fetch,decode,execute,memory,writeBack;
    public Processor(){
        mainMemory = new MainMemory();
        registers = new RegisterFile();
        alu = new ALU();
        currentInstructions = new ArrayList<>();
    }

    public void nextStageWithFetch() {
        // TODO handle fetch boolean and incrementing
        //TODO execute and decode in second clock cycle?

        for (int i = 0; i < currentInstructions.size(); i++) {
            if(currentInstructions.get(i).getStage()[0]==0) //Fetch cycle 1
            {
                currentInstructions.get(i).setStage(0,1);
            }
           else if(currentInstructions.get(i).getStage()[0]==1) //Decode cycle 1
            {
                     currentInstructions.get(i).setStage(1,1);
            }
            else if(currentInstructions.get(i).getStage()[1]==1) //Decode cycle 2
            {
                currentInstructions.get(i).decode(registers);
                currentInstructions.get(i).setStage(1,2);
            }
            else if(currentInstructions.get(i).getStage()[1]==2) //Execute cycle 1
            {
                    currentInstructions.get(i).setStage(2,1);
            }
            else if(currentInstructions.get(i).getStage()[2]==1) // Execute cycle 2
            {
                int oldPC= registers.getPc();
                currentInstructions.get(i).execute(registers,alu); //wrong types?
                int newPC= registers.getPc();
                currentInstructions.get(i).setStage(2,2);
                if(oldPC!=newPC)
                {
                  //  int oldsize= currentInstructions.size();
                    flushInstructions();
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
                    currentInstructions.get(i).writeBack(currentInstructions.get(i).getOpcode(),registers); //wrong types?
                    currentInstructions.get(i).setStage(4,1);
            }
            else if(currentInstructions.get(i).getStage()[4]==1) //Finished execution
            {
                currentInstructions.remove(i);
            }
        }
    }

    public void nextStageWithoutFetch() {
        for (int i = 0; i < currentInstructions.size(); i++) {

            if(currentInstructions.get(i).getStage()[0]==1) //Decode cycle 1
            {
                currentInstructions.get(i).setStage(1,1);
            }
            else if(currentInstructions.get(i).getStage()[1]==1) //Decode cycle 2
            {
                currentInstructions.get(i).decode(registers);
                currentInstructions.get(i).setStage(1,2);
            }
            else if(currentInstructions.get(i).getStage()[1]==2) //Execute cycle 1
            {
                currentInstructions.get(i).setStage(2,1);
            }
            else if(currentInstructions.get(i).getStage()[2]==1) // Execute cycle 2
            {
                int oldPC= registers.getPc();
                currentInstructions.get(i).execute(registers,alu); //wrong types?
                int newPC= registers.getPc();
                currentInstructions.get(i).setStage(2,2);
                if(oldPC!=newPC)
                {
                    flushInstructions();
                }
            }
            else if(currentInstructions.get(i).getStage()[3]==0) // Memory cycle 1
            {
                currentInstructions.get(i).memory(mainMemory,registers); //wrong types?
                currentInstructions.get(i).setStage(3,1);
            }
            else if(currentInstructions.get(i).getStage()[4]==0) //WriteBack cycle 1
            {
                currentInstructions.get(i).writeBack(currentInstructions.get(i).getOpcode(),registers); //wrong types?
                currentInstructions.get(i).setStage(4,1);
            }
            else if(currentInstructions.get(i).getStage()[4]==1) //Finished execution
            {
                currentInstructions.remove(i);
            }


        }
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
        File file = new File("C:\\Users\\pankaj\\Desktop\\test.txt");
        BufferedReader br = new BufferedReader(new FileReader(file));
        String st;
        int i = 0;
        while ((st = br.readLine()) != null) {
            // todo print value after parsing
            String[] stValues = st.split(" ");
            String res = "";
            String opcode = parseOpcode(stValues[0]);
            res += opcode;
            if (stValues[0].equals("J")){
                String temp = (Integer.parseInt(stValues[1]) & 0b00001111111111111111111111111111) + "";
                res += temp.substring(4,temp.length());
            }
            else {
                res += parseRegister(stValues[1]);
                res += parseRegister(stValues[2]);
                if (stValues[0].equals("ADD") || stValues[0].equals("SUB")) { //r type
                    res += parseRegister(stValues[3]); //R3
                    res += "0000000000000";
                } else if (stValues[0].equals("SLL") || stValues[0].equals("SRL")) {
                    res += "00000";
                    String temp = (Integer.parseInt(stValues[4]) & 0b00000000000000000001111111111111) + "";
                    res += temp.substring(18, temp.length());
                } else { //I type
                    String temp = (Integer.parseInt(stValues[3]) & 0b00000000000000111111111111111111) + "";
                    res += temp.substring(13, temp.length());
                }
            }
            memory.setMainMemory(Integer.parseInt(res),i);
            i++;
        }
        numOfInstructions = i; // storing the number of instructions in instruction file
        // will be used to calculate number of clock cycles
    }



    private String parseRegister(String stValue) {
        //TODO test the return statement by printing
        String temp = stValue.substring(1);
        int val = Integer.parseInt(temp) -1;
        temp =(val & 0b00000000000000000000000000011111) + "";
        return temp.substring(27,temp.length());
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
        System.out.println("Current clk cycle " + cycles);
        piplineSeq();


    }

    private void piplineSeq()
    {
        String[] prints = new String[5];
        Instruction[] stages = new Instruction[5];

        for(int i =0; i< currentInstructions.size(); i++) {
            Instruction inst = currentInstructions.get(i);
            //instruction & stage
            if (inst.getStage()[0] == 1) {
                prints[0] += "Instruction " + (inst.getAddress() + 1) + ". Parameters: ";
                stages[0] = inst;
                switch(inst.getInstructionType()){
                    case I:
                    case J: prints[0] += "RS: " + inst.getValR2() + " RT: " + inst.getValR3() ; break;
                    case R: prints[0] += "No inputs"; break;
                }
            } else if (inst.getStage()[1] == 1 || inst.getStage()[1] == 2) {
                prints[1] += "Instruction " + (inst.getAddress() + 1) + ". Parameters: ";
                stages[1] = inst;
                switch(inst.getInstructionType()){
                    case I:
                    case J: prints[1]+= "RS: " + inst.getValR2() + " RT: " + inst.getValR3() ; break;
                    case R: prints[1]+= "No inputs"; break;
                }
            } else if (inst.getStage()[2] == 1 || inst.getStage()[2] == 2) {
                prints[2] += "Instruction " + (inst.getAddress() + 1) + ". Parameters: ";
                stages[2] = inst;
                switch(inst.getInstructionType()){
                    case I:
                    case J: prints[2]+= "RS: " + inst.getValR2() + " RT: " + inst.getValR3() ; break;
                    case R: prints[2]+= "No inputs"; break;
                }
            } else if (inst.getStage()[3] == 1) {
                prints[3] += "Instruction " + (inst.getAddress() + 1) + ". Parameters: ";
                stages[3] = inst;
                switch(inst.getInstructionType()){
                    case I:
                    case J: prints[3]+= "RS: " + inst.getValR2() + " RT: " + inst.getValR3() ; break;
                    case R: prints[3]+= "No inputs"; break;
                }
            } else if (inst.getStage()[4] == 1) {
                prints[4] += "Instruction " + (inst.getAddress() + 1) + ". Parameters: ";
                stages[4] = inst;
                switch(inst.getInstructionType()){
                    case I:
                    case J: prints[4]+= "RS: " + inst.getValR2() + " RT: " + inst.getValR3() ; break;
                    case R: prints[4]+= "No inputs"; break;
                }
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
        int res= this.mainMemory.getMainMemory(this.registers.getPc());
        this.registers.setPc(this.registers.getPc()+1);
        Instruction instruction = new Instruction(res);
        currentInstructions.add(instruction);
    }

    public static void main(String[] args) throws IOException{ //TODO will we be given the clock cycle/ instruction count?
        Processor processor = new Processor();
        processor.parseFileIntoMemory(processor.mainMemory);
        int numOfClockCycles = 7 + ((processor.numOfInstructions-1)*2);
        int clockCycle = 0;

        while(clockCycle<=numOfClockCycles){
            if(clockCycle%2==1) {
                if (processor.currentInstructions.size() < 5) {//TODO ask ta that this should automatically be satisfied{
                    processor.fetch();
                    processor.nextStageWithFetch(); //TODO would this cause me to add more instructions than i can handle

                }
            }
            else {
                processor.nextStageWithoutFetch();
            }
            clockCycle++;
        }

    }
}

