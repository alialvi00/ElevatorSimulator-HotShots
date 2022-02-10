package input;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

import floorSubsystem.FloorSubsystem;

public class Reader implements Runnable {
    private BufferedReader reader_file;
    private InputBuffer buffer;
    private static int lineCounter;

    public Reader(InputBuffer buffer){
    	
    	this.buffer = buffer;
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
    
    @Override
    public void run() {

        try{
            while(reader_file.ready())
                readInput();
        } catch (IOException e){
            e.printStackTrace();
        }

    }
    
    public void readInput() throws IOException {
    	ArrayList<String> dataInfo = new ArrayList<>();
    	StringTokenizer st = new StringTokenizer(reader_file.readLine(), " ");
        for(int i = 0; i < 4; i++)
            dataInfo.add(st.nextToken());

        buffer.sendToInputBuffer(dataInfo);
        lineCounter++;
    }
    
}
