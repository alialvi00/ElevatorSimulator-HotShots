package input;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

import floorSubsystem.FloorSubsystem;

public class Reader implements Runnable {
    private BufferedReader reader_file;
    private ArrayList<String> dataInfo;
    private static int lineCounter;

    public Reader(){
        dataInfo = new ArrayList<>();
        lineCounter = 0;
        try{
            this.reader_file = new BufferedReader(new FileReader("Inputs/test.txt"));
        } catch(IOException e){
            System.exit(0);
        }
    }

    public static int getLineCounter(){
        return lineCounter;
    }
    
    public ArrayList<String> getDataInfo(){
    	return dataInfo;
    }
    
    public void readInput() throws IOException {
    	
    	StringTokenizer st = new StringTokenizer(reader_file.readLine(), " ");
        for(int i = 0; i < 4; i++)
            dataInfo.add(st.nextToken());

        FloorSubsystem.sendData(dataInfo);
        lineCounter++;
    }

    @Override
    public void run() {

        try{
            while(reader_file.ready())
                readInput();
        } catch (IOException e){
            e.printStackTrace();
        }

    }
}
