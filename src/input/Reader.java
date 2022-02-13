package input;

import java.io.BufferedReader;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * This class reads the text input and sends it to the
 * Input Buffer class. 
 * @author Akaash Kapoor
 *
 */
public class Reader implements Runnable {
	
	/**Instance variable for reading the input file.*/
    private BufferedReader readerFile;
    /**Buffer for storing input data read from text file. */
    private InputBuffer buffer;
    /**Keeps track of the lines read in the input file. */
    private static int lineCounter;

    /**
     * Constructor for initializing Reader Object
     * @param buffer A blocking queue used to send data to the Floor Subsystem.
     */
    public Reader(InputBuffer buffer){
    	
    	this.buffer = buffer;
        lineCounter = 0;
        try{
            this.readerFile = new BufferedReader(new FileReader("Inputs/test.txt"));
        } catch(IOException e){
            System.exit(0);
        }
    }
    
    /**
     * Getter function for extracting line counter. 
     * @return lineCounter
     */
    public static int getLineCounter(){
        return lineCounter;
    }
    
    /**
     * Runs the Reader thread. 
     */
    @Override
    public void run() {

        try{
            while(readerFile.ready())
                readInput();
        } catch (IOException e){
            e.printStackTrace();
        }

    }
    
    /**
     * Helper function to read input. 
     * @throws IOException This is thrown if reader cannot read file. 
     */
    public void readInput() throws IOException {
    	ArrayList<String> dataInfo = new ArrayList<>();
    	StringTokenizer st = new StringTokenizer(readerFile.readLine(), " ");
        
    	for(int i = 0; i < 4; i++)
            dataInfo.add(st.nextToken());
    	
    	//Inputs info into the InputBuffer.
        buffer.sendToInputBuffer(dataInfo);
        lineCounter++;
    }
    
}
