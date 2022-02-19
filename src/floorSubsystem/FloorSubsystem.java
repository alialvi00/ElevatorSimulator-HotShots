package floorSubsystem;

import input.InputBuffer;

import input.Reader;
import scheduler.Scheduler;

import java.util.ArrayList;


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
    
    /**All floors listed in the building.*/
    private ArrayList<FloorAttributes> floors;
    
    /**Data that will be sent to the scheduler*/
    private ArrayList<String> new_data;
    
    /**Data that will be recieved from the input buffer object*/
    private ArrayList<String> input_data;
    
    /**Counts the number of requests present in the text file*/
    private int counter;

    /**
     * Constructor for the floor subsystem. Initializes
     * the scheduler buffer, input buffer, floor attributes and counter. 
     * @param buf Data connection to send to the scheduler. 
     * @param text Data connection to be recieved from the input file. 
     */
    public FloorSubsystem(Scheduler buf, InputBuffer text, int floors){
        this.buf = buf;
        this.text = text;
        this.floors = new ArrayList<>(floors);
        
        for(int i = 0; i < floors; i++)
        	this.floors.add(new FloorAttributes());
        
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
    
    public void setInputData(ArrayList<String> input) {
    	this.input_data = input;
    }
    
    /**
     * Setter method for setting the buttons and directions on floors. Only called
     * when the input request is recieved from the input file.  
     */
    public void setLamps() {
    	
    	//System.out.println("Passenger pressed the " + input_data.get(2) + " button.");
    	
    	if(input_data.get(2).equals("Up")) {
    		
    		floors.get(Integer.parseInt(input_data.get(1)) - 1).setButtonLampUp(true);
    		floors.get(Integer.parseInt(input_data.get(1)) - 1).setButtonLampDown(false);
    		
    	} else {
    		
    		floors.get(Integer.parseInt(input_data.get(1)) - 1).setButtonLampUp(false);
    		floors.get(Integer.parseInt(input_data.get(1)) - 1).setButtonLampDown(true);
    	}
    	
    	//Sets the direction of the elevator on all floors (Iteration 2 assumes one operating elevator). 
    	setDirectionalLampsAllFloors(input_data.get(2));
    	
    	//System.out.println(Thread.currentThread().getName() + " is going " + input_data.get(2));
    }
    
    /**
     * Setter for resetting Button Lamps. This is called when the scheduler first moves the elevator
     * to the first floor. 
     */
    public void resetButtonLamps() {
    	
    	floors.get(Integer.parseInt(input_data.get(1)) - 1).setButtonLampUp(false);
		floors.get(Integer.parseInt(input_data.get(1)) - 1).setButtonLampDown(false);
    }
    
    /**
     * Setter method for setting the direction lamps on all floors. 
     * Only called when the passenger first requests an elevator on that
     * floor and when the scheduler sends the location of the elevator back
     * to the floor. 
     */
    public void setDirectionalLampsAllFloors(String direction) {
    	
    	for(int i = 0; i < floors.size(); i++) {
    		
    		if(direction.equals("Up")) {
    			floors.get(i).setDirectionLampUp(true);
    			floors.get(i).setDirectionLampDown(false);
    		} else if (direction.equals("Down")){
    			floors.get(i).setDirectionLampUp(false);
    			floors.get(i).setDirectionLampDown(true);
    		} else {
    			floors.get(i).setDirectionLampUp(false);
    			floors.get(i).setDirectionLampDown(false);
    		}
    	}
    	
    	
    }

    @Override
    public void run() {

        while(counter < Reader.getLineCounter()) {
        	
        	//Store recieved data in the input buffer. 
        	input_data = text.recieveFromInputBuffer();
        	//Set the buttons and lamps.
        	setLamps();
            System.out.println(Thread.currentThread().getName() + " is sending " + input_data + " to Scheduler.");
            //Send data to the scheduler. 
            buf.sendToScheduler(input_data);
            
            //Wait for 1 second. 
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            
            //Recieve data from the scheduler. This will update the elevator direction on all floors. 
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
