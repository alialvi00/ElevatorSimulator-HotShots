package floorSubsystem;

import input.InputBuffer;

import input.Reader;
import scheduler.Scheduler;
import scheduler.SchedulerRequest;

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
    
    /**Data that will be recieved from scheduler*/
    private SchedulerRequest newData;
    
    /**Data that will be recieved from the input buffer object*/
    private ArrayList<String> inputData;
    
    private SchedulerRequest dataSentToScheduler;
    
    /**Counts the number of requests present in the text file*/
    private int counter;

    /**
     * Constructor for the floor subsystem. Initializes
     * the scheduler buffer, input buffer, floor attributes and counter. 
     * @param buf Data connection to send to the scheduler. 
     * @param text Data connection to be recieved from the input file. 
     */
    public FloorSubsystem(Scheduler buf, InputBuffer text, int numFloors){
        this.buf = buf;
        this.text = text;
        this.floors = new ArrayList<>(numFloors);
        this.dataSentToScheduler = new SchedulerRequest();

        
        for(int i = 0; i < numFloors; i++)
        	this.floors.add(new FloorAttributes());
        
        this.counter = 0;
    }
    
    /**
     * Getter function for data to be recieved from scheduler. 
     * @return SchedulerRequest of new data. 
     */
    public SchedulerRequest getNewData() {
    	return newData;
    }
    
    /**
     * Getter function for data to be recieved from the input class. 
     * @return ArrayList of input data. 
     */
    public ArrayList<String> getInputData(){
    	return inputData;
    }
    
    /**
     * Getter function for data to be sent to scheduler. 
     * @return SchedulerRequest of sent data. 
     */
    public SchedulerRequest getDataSchedulerRequest() {
    	return dataSentToScheduler;
    }
    
    /**
     * Sets input data from text buffer. 
     * @param input ArrayList of input data. 
     */
    public void setInputData(ArrayList<String> input) {
    	this.inputData = input;
    }
    
    /**
     * Setter method for setting the buttons and directions on floors. Only called
     * when the input request is recieved from the input file.  
     */
    public void setLamps() {
    	
    	//System.out.println("Passenger pressed the " + input_data.get(2) + " button.");
    	
    	if(inputData.get(2).equals("Up")) {
    		
    		floors.get(Integer.parseInt(inputData.get(1)) - 1).setButtonLampUp(true);
    		floors.get(Integer.parseInt(inputData.get(1)) - 1).setButtonLampDown(false);
    		
    	} else {
    		
    		floors.get(Integer.parseInt(inputData.get(1)) - 1).setButtonLampUp(false);
    		floors.get(Integer.parseInt(inputData.get(1)) - 1).setButtonLampDown(true);
    	}
    	
    	//Sets the direction of the elevator on all floors (Iteration 2 assumes one operating elevator). 
    	setDirectionalLampsAllFloors(inputData.get(2));
    	
    	//System.out.println(Thread.currentThread().getName() + " is going " + input_data.get(2));
    }
    
    /**
     * Setter for resetting Button Lamps. This is called when the scheduler first moves the elevator
     * to the first floor. 
     */
    public void resetButtonLamps() {
    	
    	floors.get(Integer.parseInt(inputData.get(1)) - 1).setButtonLampUp(false);
		floors.get(Integer.parseInt(inputData.get(1)) - 1).setButtonLampDown(false);
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
    
    /**
     * Parse method that is used to handle data sent to the Scheduler 
     * much easier. 
     */
    public SchedulerRequest parseInputToRequestObject() {
    	
    	dataSentToScheduler.setArrivalTime(inputData.get(0));
    	dataSentToScheduler.setCurrentFloor(Integer.parseInt(inputData.get(1)));
    	if (inputData.get(2) == "Up") {
    		dataSentToScheduler.setDirection(1);
    	} else {
    		dataSentToScheduler.setDirection(0);
    	}
    	dataSentToScheduler.setDestinationFloor(Integer.parseInt(inputData.get(3)));
    	dataSentToScheduler.setSubsystem("floor");
    	
    	return dataSentToScheduler;
    }

    @Override
    public void run() {

        while(counter < Reader.getLineCounter()) {
        	
        	//Store recieved data in the input buffer. 
        	inputData = text.recieveFromInputBuffer();
        	//Set the buttons and lamps.
        	setLamps();
            System.out.println(Thread.currentThread().getName() + " is sending " + inputData + " to Scheduler.");
            //Send data to the scheduler. 

            buf.sendToScheduler(parseInputToRequestObject(), "floor");

            
            //Wait for 2 seconds. 
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            
            //Recieve data from the scheduler. This will update the elevator direction on all floors. 

            newData = buf.recieveFromScheduler("floor");
            System.out.println(Thread.currentThread().getName() + " has recieved new data from Scheduler.\n");

            
            counter++;

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
     }
}
