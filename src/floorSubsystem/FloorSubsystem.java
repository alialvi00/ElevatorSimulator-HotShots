package floorSubsystem;

import input.InputBuffer;
import input.Reader;
import scheduler.Scheduler;

import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * This class represents the floor subsystem of the 
 * elevator project. 
 * @author Akaash Kapoor
 *
 */
public class FloorSubsystem implements Runnable{

	/**The buffer to host the scheduler methods*/
    private Scheduler buf;
    /**The buffer to host the input methods*/
    private InputBuffer text;
    /**Data that will be sent to the scheduler*/
    private ArrayList<String> new_data;
    /**Data that will be recieved from the input buffer object*/
    private ArrayList<String> input_data;
    /**Counts the number of requests present in the text file*/
    private int counter;

    /**
     * Constructor for the floor subsystem. Initializes
     * the scheduler buffer and input buffer and counter. 
     * @param buf Data connection to send to the scheduler. 
     * @param text Data connection to be recieved from the input file. 
     */
    public FloorSubsystem(Scheduler buf, InputBuffer text){
        this.buf = buf;
        this.text = text;
        this.counter = 0;
    }
    
    /**
     * Getter function for returning data to be sent to scheduler. 
     * @return ArrayList of new data. 
     */
    public ArrayList<String> getNewData() {
    	return new_data;
    }
    
    /**
     * Getter function for returning data to be recieved from the input class. 
     * @return ArrayList of input data. 
     */
    public ArrayList<String> getInputData(){
    	return input_data;
    }

    @Override
    public void run() {

        while(counter < Reader.getLineCounter()) {
        	
        	//Store recieved data in the input buffer. 
        	input_data = text.recieveFromInputBuffer();
            System.out.println(Thread.currentThread().getName() + " is sending " + input_data + " to Scheduler.");
            //Send data to the scheduler. 
            buf.sendToScheduler(input_data);
            
            //Wait for 1 second. 
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            
            //Recieve data from the scheduler
            new_data = buf.recieveFromScheduler();
            System.out.println(Thread.currentThread().getName() + " has recieved " + new_data + " from Scheduler.\n");
            
            counter++;

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        
        	}
       }
}
