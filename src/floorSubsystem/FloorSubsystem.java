package floorSubsystem;

import input.InputBuffer;



import input.Reader;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;


/**
 * This class represents the floor subsystem of the 
 * elevator project. 
 * @author Akaash Kapoor
 *
 */
public class FloorSubsystem implements Runnable{

	
    /**The buffer to host the input methods*/
    private InputBuffer text;
    
    /**All floors and elevators listed in the building.*/
    private ArrayList<ArrayList<FloorAttributes>> floors;
    
    /**Request data to be sent to the scheduler.*/
    private FloorRequest floorData;
    
    /**Data that will be recieved from the input buffer object*/
    private ArrayList<String> inputData;
    
    /**Web socket that will communicate with the Scheduler.*/
    private DatagramSocket floorToScheduler;
    
    private DatagramSocket schedulerToFloor;
    
    /**The data to be sent to the scheduler.*/
    private DatagramPacket sendData;
    
    /**Scheduler response for sucessful data transfer. */
    private DatagramPacket receiveResponse;
    

    /**
     * Constructor for the floor subsystem. Initializes
     * the scheduler buffer, input buffer, floor attributes and counter. 
     * @param buf Data connection to send to the scheduler. 
     * @param text Data connection to be recieved from the input file. 
     * @param numFloors the number of floors to be instantiated for floor subsystem.
     */
    public FloorSubsystem(int numFloors, int numElevators){
    	
        this.floors = new ArrayList<>();
        this.text = new InputBuffer();
        
        //Set up data socket connection. 
        try {
        	this.floorToScheduler = new DatagramSocket();
        	this.schedulerToFloor = new DatagramSocket(23);
        	//this.floorToScheduler.setSoTimeout(30000);
        }catch (SocketException e) {
			e.printStackTrace();
			System.exit(1);
		}

        
        //Sets the floor layout based on number of floors and elevators operating. 
        for(int i = 0; i < numFloors; i++) {
        	this.floors.add(new ArrayList<>());
        	for(int j = 0 ; j < numElevators; j ++) {
        		this.floors.get(i).add(new FloorAttributes());
        	} 	
        }
        
        //Initializes the arrival sensors
        for(int i =0; i < numElevators; i++) {
        	this.floors.get(0).get(i).setArrivalSensor(true);
        }
        
        
        //Reader thread will read in all requests and store them in an input queue. 
        Thread reader = new Thread(new Reader(this.text), "Reader Thread");
        reader.start();
        try {
            reader.join();
        } catch (Exception e){
        	e.printStackTrace();
        }
        
             
    }
    
    /**
     * Getter function for data to be recieved from the input class. 
     * @return ArrayList of input data. 
     */
    public ArrayList<String> getInputData(){
    	return inputData;
    }
    
     
    
    /**
     * Setter method for setting the buttons and directions on floors. Only called
     * when the input request is recieved from the input file.  
     */
    public void setLamps() {
    	
    	System.out.println("Passenger on floor " + inputData.get(1) + " pressed the " + inputData.get(2) + " button.");
    	
    	if(inputData.get(2).equalsIgnoreCase("Up")) {
    		//Sets all button lamps on the floor to up. Scheduler will find nearest available elevator. 
    		for(int i = 0; i < this.floors.get(0).size(); i++) {
    			this.floors.get(Integer.parseInt(inputData.get(1)) - 1).get(i).setButtonLampUp(true);
    		}
    	} else {
    		//Sets all button lamps on the floor to down. Scheduler will find nearest available elevator. 
    		for(int i = 0; i < this.floors.get(0).size(); i++) {
    			this.floors.get(Integer.parseInt(inputData.get(1)) - 1).get(i).setButtonLampDown(true);
    		}
    	}
    	
    	
    	
    }
    
    /**
     * Setter for resetting Button Lamps. This is called when the scheduler first moves the elevator
     * to another floor. 
     */
    public void resetButtonLamps(int pickUpFloor) {
    	
    	for(int i = 0; i < this.floors.get(pickUpFloor).size(); i++) {
    		this.floors.get(pickUpFloor).get(i).setButtonLampUp(false);
    		this.floors.get(pickUpFloor).get(i).setButtonLampDown(false);
    	}
    	
    }
    
    /**
     * Setter method for setting the direction lamps on all floors. 
     * Only called when the scheduler sends the location of the elevator back
     * to the floor. 
     */
    public void setDirectionalLampsAllFloors(String direction, int elevatorID) {
    	
    	for(int i = 0; i < this.floors.size(); i++) {
    		
    		if(direction.equalsIgnoreCase("Up")) {
    			this.floors.get(i).get(elevatorID-1).setDirectionLampUp(true);
    			this.floors.get(i).get(elevatorID-1).setDirectionLampDown(false);
    		} else if (direction.equals("Down")){
    			this.floors.get(i).get(elevatorID-1).setDirectionLampUp(false);
    			this.floors.get(i).get(elevatorID-1).setDirectionLampDown(true);
    		} else {
    			this.floors.get(i).get(elevatorID-1).setDirectionLampUp(false);
    			this.floors.get(i).get(elevatorID-1).setDirectionLampDown(false);
    		}
    	}
    	
    	
    }
    
    /**
     * Sets the arrival sensor of the elevator to that particular floor. 
     * @param elevatorID
     * @param currentFloor
     */
    public void setArrivalSensor(int elevatorID, int currentFloor, String direction) {
    	
    	this.floors.get(currentFloor).get(elevatorID-1).setArrivalSensor(true);
    	if(direction.equals("Up")) {
    		this.floors.get(currentFloor-1).get(elevatorID-1).setArrivalSensor(false);
    	} else {
    		this.floors.get(currentFloor+1).get(elevatorID-1).setArrivalSensor(false);
    	}
    	
    }
    
    public void updateFloors() {
    	
    }
    
    /**
     * Instantiates a new request for sending data to the scheduler.
     * @param isLastRequest
     */
    public void createNewRequest(boolean isLastRequest) {
    	this.floorData = new FloorRequest(Integer.parseInt(inputData.get(1)),
    										inputData.get(0),inputData.get(2),
    										Integer.parseInt(inputData.get(3)), 
    										isLastRequest); 	
    }
    
    /**
     * Method that sends datagram packet to scheduler. 
     */
    public void sendDataToScheduler(boolean isLastRequest) {
    	
    	//Creates two packets. One for sending data, and one for receiving a success message. 
    	try {
    		createNewRequest(isLastRequest);
    		sendData = new DatagramPacket(floorData.byteRepresentation(),floorData.byteRepresentation().length, InetAddress.getLocalHost(), 69);
    		//Response saying it was a successful message pass. 
    		receiveResponse = new DatagramPacket(new byte[700],700);
    	} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
    	
    	sendFloorMessage();
    	
    }
    
    /**
     * Method that represents the RPC to Scheduler. 
     */
    public void sendFloorMessage() {
    	
    	//Send data via the socket using an RPC request. 
	    try {
	    	//System.out.println("Floor Sending Passenger Data to Scheduler");
			floorToScheduler.send(sendData);
			Thread.sleep(2000);
			schedulerToFloor.receive(receiveResponse);
			//System.out.println("Scheduler sent " + new String(receiveResponse.getData())+ " to Floor.");
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	      
	    
    }
    
    
    
    
    @Override
    public void run() {
    	
    	while(text.getDataFromInputBuffer() != null) {
	    	LocalTime l1 = null, l2 = null;
	    	//Store recieved data in the input buffer. 
	    	inputData = text.recieveFromInputBuffer();
	    	
	    	l1 = LocalTime.parse(inputData.get(0));
	    	
	    	//Set the buttons lamps.
	    	setLamps();
	        System.out.println(Thread.currentThread().getName() + " is sending " + inputData + " to Scheduler.");
	        
	        
	        //Send data to the scheduler based on last request. 
	        if(text.getDataFromInputBuffer() == null) {
	        	sendDataToScheduler(true);
	        } else {
	        	sendDataToScheduler(false);
	        }
	        
	        
	        //Emulates the requests sent to scheduler in real time by having a varying sleep function. 
	        if (text.getDataFromInputBuffer() != null) {
	        	
	        	l2 = LocalTime.parse(text.getDataFromInputBuffer().get(0));
	            try {
	                Thread.sleep(Duration.between(l1, l2).toMillis());
	            } catch (InterruptedException e) {
	                e.printStackTrace();
	            }
	            
	        }
    	}
        
     }
    
    
    
    public static void main(String[] args) {
    	FloorSubsystem floorSubsystem = new FloorSubsystem(4, 2);
    	Thread floor = new Thread(floorSubsystem, "Floor Thread.");
    	floor.start();
    }
    
    
    
}
