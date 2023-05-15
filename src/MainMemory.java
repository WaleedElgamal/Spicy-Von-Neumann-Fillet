public class MainMemory {
    int[] mainMemory;

    public MainMemory(){
        mainMemory = new int[2048];
    }

    public int[] getMainMemory(){
        return mainMemory;
    }
    public int getMainMemory(int i)
    {
        return mainMemory[i];
    }

    public void setMainMemory(int value, int position)
    {
        mainMemory[position]=value;
    }
}
