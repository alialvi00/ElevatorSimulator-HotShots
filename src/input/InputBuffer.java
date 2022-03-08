
package input;

import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;


/**
 * @author Akaash Kapoor
 *
 */

/**
 * This class acts as an intermediate between the Reader 
 * and the FloorSubsystem classes by storing and retrieving
 * text data. 
 * @author Akaash Kapoor
 *
 */
public class InputBuffer {
	
	/**The linked blocking queue buffer.*/
	private LinkedBlockingQueue<ArrayList<String>> buffer;
	
	/**
	 * Constructor for InputBuffer class. Instantiates blocking queue. 
	 */
	public InputBuffer() {
		buffer = new LinkedBlockingQueue<>();
	}
	
	/**
	 * Getter function that returns the data inside the queue. 
	 * @return an ArrayList of the data in the queue. 
	 */
	public ArrayList<String> getDataFromInputBuffer(){
		return buffer.peek();
	}
	
	/**
	 * This method inputs data into the buffer. 
	 * @param data - The data needed to be inserted and passed to the 
	 * floor subsystem.
	 */
	public void sendToInputBuffer(ArrayList<String> data){
        try {
            buffer.put(data);
            System.out.println("Reader placed " + data + " in the Reader queue.");
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }
    
	/**
	 * This method extracts data from the buffer. 
	 * @return An array list that contains stored data. 
	 */
    public ArrayList<String> recieveFromInputBuffer(){
    	
    	ArrayList<String> data = new ArrayList<>();
    	
        try {
            data = buffer.take();
            System.out.println("Floor Removed " + data + " from the Reader queue.");
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        return data;
    }

}
