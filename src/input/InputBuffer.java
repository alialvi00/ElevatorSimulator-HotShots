
package input;

import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;

/**
 * @author Akaash Kapoor
 *
 */
public class InputBuffer {
	
	private LinkedBlockingQueue<ArrayList<String>> buffer;
	
	public InputBuffer() {
		buffer = new LinkedBlockingQueue<>();
	}
	
	public void sendToInputBuffer(ArrayList<String> data){
        try {
            buffer.put(data);
            System.out.println("Reader placed " + data + " in the queue.");
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }
    
    public ArrayList<String> recieveFromInputBuffer(){
    	
    	ArrayList<String> data = new ArrayList<>();
    	
        try {
            data = buffer.take();
            System.out.println("Floor Removed " + data + " from the queue.");
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        return data;
    }

}
