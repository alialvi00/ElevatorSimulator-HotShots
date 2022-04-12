package floorSubsystem;

import input.InputBuffer;



import input.Reader;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.JOptionPane;

import elevatorSubsystem.ElevatorRequest;


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
    private CopyOnWriteArrayList<ArrayList<FloorAttributes>> floors;
    
    /**Request data to be sent to the scheduler.*/
    private FloorRequest floorData;
    
    /**Data that will be recieved from the input buffer object*/
    private ArrayList<String> inputData;
    
    /**Data socket that will communicate with the Scheduler.*/
    private DatagramSocket floorToScheduler;
    
    /**Data socket that will communicate with the Floor.*/
    private DatagramSocket schedulerToFloor;
    
    /**The data to be sent to the scheduler.*/
    private DatagramPacket sendData;
    
    /**Scheduler response for sucessful data transfer. */
    private DatagramPacket receiveResponse;
    
    /**Data response to update the 2-D array list.*/
    private DatagramSocket updateFloors; 
    
    private static final int FLOORNUM = 22;
    private static final int ELEVATORNUM = 4;

    /**
     * Constructor for the floor subsystem. Initializes
     * the input buffer, floor attributes, and data sockets. 
     * @param numFloors - the number of floors to be instantiated for floor subsystem.
     * @param numElevators - the number of elevators to be instantiated for floor subsystem. 
     */
    public FloorSubsystem(int numFloors, int numElevators){
    	
        this.floors = new CopyOnWriteArrayList<>();
        this.text = new InputBuffer();
        
        //Set up data socket connection. 
        try {
        	this.floorToScheduler = new DatagramSocket();
        	this.schedulerToFloor = new DatagramSocket(23);
        	this.updateFloors = new DatagramSocket(37);
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
     * Setter method for setting the buttons on floors. Only called
     * when the input request is recieved from the input file.  
     */
    public void setLamps() {
    	
    	//System.out.println("Passenger on floor " + inputData.get(1) + " pressed the " + inputData.get(2) + " button.");
    	
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
     * @param pickUpFloor - Pickup floor of the passenger. 
     */
    public void resetButtonLamps(int pickUpFloor) {
    	
    	for(int i = 0; i < this.floors.get(pickUpFloor-1).size(); i++) {
    		this.floors.get(pickUpFloor-1).get(i).setButtonLampUp(false);
    		this.floors.get(pickUpFloor-1).get(i).setButtonLampDown(false);
    	}
    	
    }
    
    /**
     * Setter method for setting the direction lamps on all floors. 
     * Only called when the scheduler sends the location of the elevator back
     * to the floor. 
     * @param direction - the direction of where the elevator is going. 
     * @param elevatorID - ID of the optimal elevator. 
     */
    public void setDirectionalLampsAllFloors(String direction, int elevatorID, boolean isMotorOn) {
    	
    	for(int i = 0; i < this.floors.size(); i++) {
    		
    		if(isMotorOn) {
    			if(direction.equalsIgnoreCase("Up")) {
        			this.floors.get(i).get(elevatorID-1).setDirectionLampUp(true);
        			this.floors.get(i).get(elevatorID-1).setDirectionLampDown(false);
        		} else if (direction.equals("Down")){
        			this.floors.get(i).get(elevatorID-1).setDirectionLampUp(false);
        			this.floors.get(i).get(elevatorID-1).setDirectionLampDown(true);
        		} else {      			
        			return;       			
        		}
    		} else {
    			this.floors.get(i).get(elevatorID-1).setDirectionLampUp(false);
    			this.floors.get(i).get(elevatorID-1).setDirectionLampDown(false);
    		}
    		
    	}
    	
    	
    }
    
    /**
     * Sets the arrival sensor of the elevator to that particular floor. 
     * @param elevatorID - ID of the optimal elevator. 
     * @param currentFloor - current floor of the elevator. 
     * @param direction - the direction of where the elevator is going. 
     */
    public void setArrivalSensor(int elevatorID, int currentFloor, String direction) {
    	
    	if(currentFloor > 1 && currentFloor < floors.size()) {
    		
        	if(direction.equalsIgnoreCase("Up")) {
        		this.floors.get(currentFloor).get(elevatorID-1).setArrivalSensor(true);
        		this.floors.get(currentFloor-1).get(elevatorID-1).setArrivalSensor(false);
        	} else if(direction.equalsIgnoreCase("Down")){
        		this.floors.get(currentFloor-2).get(elevatorID-1).setArrivalSensor(true);
        		this.floors.get(currentFloor-1).get(elevatorID-1).setArrivalSensor(false);
        	} else {
        		return;
        	}
    	} else {
    		if(currentFloor == 1) {
    			if(direction.equalsIgnoreCase("Up")) {
            		this.floors.get(currentFloor-1).get(elevatorID-1).setArrivalSensor(false);
            		this.floors.get(currentFloor).get(elevatorID-1).setArrivalSensor(true);
            	} 
    		} else if(currentFloor == floors.size()) {
            	} else if(direction.equalsIgnoreCase("Down")){
            		this.floors.get(currentFloor-1).get(elevatorID-1).setArrivalSensor(false);
            		this.floors.get(currentFloor-2).get(elevatorID-1).setArrivalSensor(true);
            	}
    		}
    	
    	
    	
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
     * @param isLastRequest
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
    
    /**
     * This method updates the the 2-D ArrayList which keeps tracks
     * of all the running elevators and floors.  
     */
    public void updateFloors() {
    	
    	//Retrieve the data from the updateFloor handler. 
    	DatagramPacket updatePacket = null;
    	try {
    		updatePacket = new DatagramPacket(new byte[700], 700, InetAddress.getLocalHost(), 20);
    	}catch (Exception e) {
			e.printStackTrace();
		}
    	
    	try {
			updateFloors.receive(updatePacket);
		} catch (IOException e) {
			e.printStackTrace();
		}
    	
    	//Gets the status of the elevator. 
    	ElevatorRequest elevatorData = (ElevatorRequest) bytesToObj(updatePacket);
    	
    	//sets the directional lamps accordingly.
    	setDirectionalLampsAllFloors(elevatorData.getElevDirection(), elevatorData.getID(), elevatorData.getIsMotorOn());
	    
    	//Sets the arrival sensor inside the 2-D array list. 
    	setArrivalSensor(elevatorData.getID(), elevatorData.getElevCurrentFloor(), elevatorData.getElevDirection());
    	if(elevatorData.isPickedUp()) {
    		resetButtonLamps(elevatorData.getElevCurrentFloor());
    	}
    	
    }
    
    /**
     * Converts the incoming request from byte format to 
     * it's corresponsding object. 
     * @param request - request sent via a socket and datagram packet. 
     * @return Object  - that was previous encoded in byte form. 
     */
    public Object bytesToObj(DatagramPacket request) {

        ByteArrayInputStream inputStream = new ByteArrayInputStream(request.getData());
        ObjectInputStream objectStream = null;
        Object requestObject = null;

        try {
            objectStream = new ObjectInputStream(new BufferedInputStream(inputStream));
            requestObject = objectStream.readObject();
            objectStream.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.exit(1);
        } catch(ClassNotFoundException ce) {
            ce.printStackTrace();
            System.exit(1);
        }
        return requestObject;
    }
    
    
    
    @Override
    public void run() {
    	
    	//Starts the update floor thread. 
    	Thread updateAllFloors = new Thread(new UpdateFloors(this), "Update-Floors Thread.");
    	updateAllFloors.start();
    	
    	//Check if text buffer is not empty. 
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
    	
    	//Wait until all floors ends. This ensures that when requests are complete,
    	//the 2-D array list is still being updated from the scheduler and elevator ends.
    	try {
			updateAllFloors.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
        
     }
    
    
    //Main method. 
    public static void main(String[] args) {
    	
    	//Options for user to choose
    	String[] defaultOrCustom = {"Choose default values (22 floors 4 elevators)", "Choose your own values"};
    	
    	//Error message if elevator is 0
    	String[] errorMessage = {"Zero is not a valid number of elevators"};
    	
    	//Default values for floor and elev
        int floorNum = FLOORNUM;
        int elevatorNum = ELEVATORNUM;
    	
        //Get user response
		int getUserResponse = JOptionPane.showOptionDialog(null, "Would you like to choose your own values for floor and elevator",
				"ENTER", JOptionPane.INFORMATION_MESSAGE, 0, null, defaultOrCustom, defaultOrCustom[0]);
		
		//If default
		if(getUserResponse == 0) {
			floorNum = FLOORNUM;
			elevatorNum = ELEVATORNUM;
		}
		
		//If not default
		else if(getUserResponse == 1) {
			floorNum = Integer.parseInt(JOptionPane.showInputDialog("How many floors would you like? "));
			elevatorNum = Integer.parseInt(JOptionPane.showInputDialog("How many elevators would you like? "));
			
			//If elevators chosen by user is 0, display error message and ask again
			while(elevatorNum == 0) {
				JOptionPane.showMessageDialog(null, errorMessage);
				elevatorNum = Integer.parseInt(JOptionPane.showInputDialog("How many elevators would you like? "));
			}
		}
		else
			System.exit(0);
		
    	FloorSubsystem floorSubsystem = new FloorSubsystem(floorNum, elevatorNum);
    	Thread floor = new Thread(floorSubsystem, "Floor Thread.");
    	floor.start();
    }
    
    
    
}
