package scheduler;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.lang.reflect.Array;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

import elevatorSubsystem.ElevatorRequest;
import floorSubsystem.FloorRequest;
import scheduler.SchedulerState.Event;
import utils.Timer;

/**
 * This class represents the scheduler system which maintains the current state of the scheduler and allows
 * for alerting the elevator.
 * @author Ali Alvi Raj Sandhu
 * @version 1.0
 */

public class Scheduler implements Runnable{

    private SchedulerStateMachine fsm; //state machine
    
    public InetAddress elevatorAddress; //elevator address
    private InetAddress floorAddress; //floor address

    private int bestElevID; //bestElevator's ID to use
    private ElevatorRequest schToElev; //request from scheduler to elev

	  private boolean systemOnline; //to check if system is online
    
    public DatagramSocket sendSocket, receiveSocket; //send and receive socket 
    public DatagramPacket elevatorPacket; //elevator packet to send

    public CopyOnWriteArrayList<ElevatorRequest> elevatorRequests; //this list represents elevator requests to be handled
    public CopyOnWriteArrayList<FloorRequest> floorRequests; //this list represents floor requests to be handled
    public ArrayList<ElevatorRequest> bestElevators; //this list represents best elevators to use
    public HashMap<String, Integer> floorMapping; //hash map used to map floors


	public HashMap<Integer, FloorRequest> servicingRequests; //hash map to map floor requests
    public HashMap<Integer, Timer> elevatorTimers;
    
    /**
     * Create the scheduler constructor.
     */
    public Scheduler(int floors){
    	

        elevatorRequests = new CopyOnWriteArrayList<>();
        floorRequests = new CopyOnWriteArrayList<>();

        floorMapping = new HashMap<>();
		servicingRequests = new HashMap<>(); //this will be used to keep track of requests being serviced
        elevatorTimers = new HashMap<>();
        
        try {
        	
        	//set elevator and floor address as local host
        	elevatorAddress = InetAddress.getLocalHost(); 
        	floorAddress = InetAddress.getLocalHost();
        }
        catch(UnknownHostException he) {
        	he.printStackTrace();
        	System.exit(1);
        }
        
        try {
        	sendSocket = new DatagramSocket();
        }
        catch(SocketException se) {
        	se.printStackTrace();
        	System.exit(1);
        }
        
        systemOnline = true; //system is now online
        
        
    }


    /**
     * This method is invoked when elevator request is received and then 
     * it updates it
     * @param elevatorRequest is the request to be updated
     */
    public void updateElevator(ElevatorRequest elevatorRequest) {

    	ElevatorRequest schReq = null; //scheduler request to be used
    	
    	
    	if(elevatorRequest.getIsMotorOn()) {
    		elevatorTimers.get(elevatorRequest.getID()).stopTime();
    		if(elevatorTimers.get(elevatorRequest.getID()).checkFault()) {
    			//elevator has failed. Send back failure request to elevator. 
    			elevatorRequest.setFailure();
    			removeFailedElev(elevatorRequest);
    			sendElevator(elevatorRequest);
    			return;
    		}
    	}
    	
    	//destination and pickup floors
		int destinationFloor = servicingRequests.get(elevatorRequest.getID()).getDestinationFloor();
		int pickUpFloor = servicingRequests.get(elevatorRequest.getID()).getID();
		
		int floorToReach = pickUpFloor; //floor to reach is by default pickup floor

		if (elevatorRequest.isPickedUp()){
			floorToReach = destinationFloor; //if passenger was picked up, now destination floor is to be serviced
		}

		if(elevatorRequest.getElevCurrentFloor() == floorToReach) { //if elevator is already on the floor to reach
			schReq = new ElevatorRequest(elevatorRequest.getID(),true,false); //create scheduler req
			if(floorToReach == destinationFloor){ //if destination floor
				elevatorTimers.remove(elevatorRequest.getID());
				servicingRequests.remove(elevatorRequest.getID()); //request is serviced and removed
				schReq.setPickedUp(false); //pickedUp is now false
			} else {
				schReq.setPickedUp(true); //else its true
			}
		}
		else if(elevatorRequest.getElevCurrentFloor() > floorToReach) { //if elevator is above the requested floor
			elevatorTimers.get(elevatorRequest.getID()).startTime();
			schReq = new ElevatorRequest(elevatorRequest.getID(), false, true);
			schReq.setElevDirection("down"); //send it down
			if(floorToReach == destinationFloor) {
				schReq.setPickedUp(true);
			}
		}
		else if(elevatorRequest.getElevCurrentFloor() < floorToReach) { //if elevator below the requested floor
			elevatorTimers.get(elevatorRequest.getID()).startTime();
			schReq = new ElevatorRequest(elevatorRequest.getID(), false, true);
			schReq.setElevDirection("up"); //send it up
			if(floorToReach == destinationFloor) {
				schReq.setPickedUp(true);
			}
		}

    	if(schReq != null)
    		sendElevator(schReq); //finally send the scheduler request
    	
    }	
    
    /**
     * This method sends the elevator request to elevator
     * @param schToElev
     */
    public void sendElevator(ElevatorRequest schToElev) {
    	
    	this.schToElev = schToElev;
    	byte[] msg = this.schToElev.byteRepresentation(); //convert request to bytes
    	elevatorPacket = new DatagramPacket(msg, msg.length, elevatorAddress, 20);
    	
    	try {
    		sendSocket.send(elevatorPacket); //send the request
    	}
    	catch(IOException ie) {
    		ie.printStackTrace();
    		System.exit(1);
    	}
    	
    	try {
    		Thread.sleep(300);
    	}
    	catch(InterruptedException ie) {
    		ie.printStackTrace();
    		System.exit(1);
    	}
    }
    
    /**
     * This method gets the best elevator possible for each floor request
     */
    public void getBestElevator() {
    	
    	bestElevators = new ArrayList<>();
    	ArrayList<FloorRequest> processedRequests = new ArrayList<>(); //list to keep track of requests fulfilled
    	bestElevID = -1;
    	String elevDir = ""; //empty string represents stationary elevator



    	for(FloorRequest eachFloor: floorRequests) { //iterate through each floor request
    		
    		ArrayList<ElevatorRequest> availableElevators = new ArrayList<>();
			//filter out the elevators that are already servicing floor requests
			for(ElevatorRequest elevator : elevatorRequests){ //for each elevator request
				if (!servicingRequests.containsKey(elevator.getID())){ //check if they are already serviced
					availableElevators.add(elevator); //if not, add them to available lists
				}
			}
    		
			if(!availableElevators.isEmpty()) { //if we have elevators available
	    		int floorNum = eachFloor.getID(); //get floor number for current floor requests being handled
	    		
	    		if(sameFloorElev(floorNum, availableElevators) & ifIdle()) { //if elevator on same floor and idle
	    			findIdleElev(); //find the idle one
	    			
	    			if(!bestElevators.isEmpty()) { //if best elevator is found
	    				bestElevID = bestElevators.get(0).getID(); //by default send first one if theres more than one
	    				elevDir = ""; //set the direction stationary
	    			}
	    		}
	    		else if(allElevAbove(floorNum, availableElevators)) { //if all avail elevators are above the requested floor
	    			
	    			if(movingDown() && eachFloor.getElevatorDirection().equalsIgnoreCase("down")) { //if any elev is moving down
	    				
	    				downElevs(); //find the moving down elevs
	    				bestElevID = nearestElevator(eachFloor); //get the closest elevator among the moving down ones
	    				elevDir = "down"; //set direction down
	    			}
	    			else if(ifIdle()) { //if idle elevators
	    				findIdleElev();
	    				bestElevID = bestElevators.get(0).getID(); //just send first one
	    				elevDir = "down"; //set direction down
	    			}	
	    		}
	    		else if(allElevBelow(floorNum, availableElevators)) { //if all avail elevators are below
	    			if(movingUp() && eachFloor.getElevatorDirection().equalsIgnoreCase("up")) { //if any of them going up
	    				upElevs();
	    				bestElevID = nearestElevator(eachFloor); //get the closest one going up
	    				elevDir = "up"; //set direction up
	    			}
	    			else if(ifIdle()) { //if theyre idle
	    				findIdleElev();
	    				bestElevID = bestElevators.get(0).getID(); //send first one by default
	    				elevDir = "up"; //elev dir is now up
	    			}
	    	   }
	    		
	    	   //Else if some elevators are above and some are down (most likely scenario)
	    	   else if(ifElevAbove(floorNum, availableElevators) & ifElevBelow(floorNum, availableElevators)){ 
	    			
	    			for(ElevatorRequest elevReq : elevatorRequests) { //iterate through each elevator request
	    				bestElevators.add(elevReq); //add them in best elevator list
	    			}
	    			
	    			//if floor is above and elevator is moving up
	    			if(eachFloor.getElevatorDirection().equalsIgnoreCase("up") && movingUp()) {
	    				upElevs();
	    				bestElevID = nearestElevator(eachFloor); //send the nearest elev to the floor
	    				elevDir = "up"; //set elev direction up
	    			}
	    			
	    			//if floor is below and elevator going down
	    			else if(eachFloor.getElevatorDirection().equalsIgnoreCase("down") && movingDown()) {
	    				downElevs();
	    				bestElevID = nearestElevator(eachFloor); //send the nearest elev to the floor
	    				elevDir = "down"; //set elev direction down
	    			}
	    			else if(ifIdle()) { //if any of them are idle
	    				findIdleElev();
	    				bestElevID = bestElevators.get(0).getID(); //send first one by default
	    				elevDir = "";
	    			}
	    			
	    			bestElevID = nearestElevator(eachFloor); //get the nearest elevator
	    	   }
	    	   if(bestElevID != -1) { //if best elevator is found
	    		   
	    		   
	    		   if(elevDir.equalsIgnoreCase("")) { //if its stationary
	    			   schToElev = new ElevatorRequest(bestElevID, true, false);
	    		   } else {
	    			   schToElev = new ElevatorRequest(bestElevID, false, true);
	    		   }
	    		   schToElev.setElevDirection(elevDir); //set the elevator direction
	    		   servicingRequests.put(bestElevID, eachFloor); //put the best elevator ID in the servicing list
	    		   for(ElevatorRequest request : elevatorRequests) {
	    			   if(request.getID() == bestElevID)
	    				   elevatorRequests.remove(request); //remove the processed requests
	    		   }
				   //Optional<ElevatorRequest> requestToRemove = elevatorRequests.stream().filter(request -> request.getID() == bestElevID).findFirst();
				   //elevatorRequests.remove(requestToRemove);
	    		   
	    		   processedRequests.add(eachFloor);
	    		   floorRequests.remove(eachFloor);
	    		   
	    		   elevatorTimers.put(schToElev.getID(), new Timer());
	    		   if(schToElev.getIsMotorOn()) {
	    			   elevatorTimers.get(schToElev.getID()).startTime();
	    		   }
				   sendElevator(schToElev); //send best elevator request to the elevator
			   }
			}
    	}		
    	//floorRequests.remove(processedRequests);
    }
    
    /**
     * If any elevator above
     * @param floorNum is int value of the floor to go 
     * @param availableElevators is an arraylist of available elevs
     * @return boolean if any elevator is above the floor
     */
    public boolean ifElevAbove(int floorNum, ArrayList<ElevatorRequest> availableElevators) {
    	
    	bestElevators.clear();
    	boolean sameFloor = false;
    	
    	for(int i=0; i < availableElevators.size(); i++) {
    		if(availableElevators.get(i).getElevCurrentFloor() > floorNum) {
    			sameFloor = true;
    			bestElevators.add(availableElevators.get(i));
    		}
    	}
    	return sameFloor;
    }
    
    /**
     * If any elevator below
     * @param floorNum is int value of the floor to go 
     * @param availableElevators is an arraylist of available elevs
     * @return boolean if any elevator is below the floor
     */
    public boolean ifElevBelow(int floorNum, ArrayList<ElevatorRequest> availableElevators) {
    	
    	bestElevators.clear();
    	boolean sameFloor = false;
    	
    	for(int i=0; i < availableElevators.size(); i++) {
    		if(availableElevators.get(i).getElevCurrentFloor() < floorNum) {
    			sameFloor = true;
    			bestElevators.add(availableElevators.get(i));
    		}
    	}
    	return sameFloor;
    }
    
    /**
     * If any elevator idle
     * @return boolean if any elevator is idle
     */
    public boolean ifIdle() {
    	
    	for(ElevatorRequest eachReq: bestElevators) {
    		if(!eachReq.getIsMotorOn())
    			return true;
    	}
    	return false;
    }
    
    /**
     * If any elevator on the same floor
     * @param floorNum is int value of the floor to go 
     * @param availableElevators is an arraylist of available elevs
     * @return boolean if any elevator is on the same floor as destination floor
     */
    public boolean sameFloorElev(int floorNum, ArrayList<ElevatorRequest> availableElevators) {
    	
    	bestElevators.clear();
    	boolean sameFloor = false;
    	
    	for(int i=0; i < availableElevators.size(); i++) {
    		if(floorNum == availableElevators.get(i).getElevCurrentFloor()) {
    			sameFloor = true;
    			bestElevators.add(availableElevators.get(i));
    		}
    	}
    	return sameFloor;
    }
    
    /**
     * If all elevators above
     * @param floorNum is int value of the floor to go 
     * @param availableElevators is an arraylist of available elevs
     * @return boolean if all elevators are above the floor
     */
    public boolean allElevAbove(int floorNum, ArrayList<ElevatorRequest> availableElevators) {
    	
    	bestElevators.clear();
    	boolean sameFloor = false;
    	
    	for(int i=0; i < availableElevators.size(); i++) {
    		
    		bestElevators.add(availableElevators.get(i));
    		
    		if(availableElevators.get(i).getElevCurrentFloor() < floorNum) {
    			return sameFloor;
    		}
    	}
    	return true;
    }
    
    /**
     * If all elevators below
     * @param floorNum is int value of the floor to go 
     * @param availableElevators is an arraylist of available elevs
     * @return boolean if all elevators are below the floor
     */
    public boolean allElevBelow(int floorNum, ArrayList<ElevatorRequest> availableElevators) {
    	
    	bestElevators.clear();
    	boolean sameFloor = false;
    	
    	for(int i=0; i < availableElevators.size(); i++) {
    		
    		bestElevators.add(availableElevators.get(i));
    		
    		if(availableElevators.get(i).getElevCurrentFloor() > floorNum) {
    			return sameFloor;
    		}
    	}
    	return true;
    }

	/**
	 * Calculate the nearest elevator
	 * @param floorRequest is the request to check nearest elevator to it
	 * @return the nearest elevators ID
	 */
    public int nearestElevator(FloorRequest floorRequest) {
    	
    	if(!bestElevators.isEmpty()) {
    		ElevatorRequest nearest = bestElevators.get(0);
    		
    		for(ElevatorRequest eachElevReq : bestElevators) {
    			if(Math.abs((eachElevReq.getElevCurrentFloor() - floorRequest.getID())) < Math.abs((nearest.getElevCurrentFloor() - floorRequest.getID()))){
    				nearest = eachElevReq;
    			}
    		}
    		return nearest.getID();
    	}
    	return -1;
    }
    
    /**
     * If any elevator moving up
     * @return boolean if any elevator moving up
     */
    public boolean movingUp() {
    	
    	for(ElevatorRequest eachReq: bestElevators) {
    		if(eachReq.getElevDirection().equalsIgnoreCase("up"))
    			return true;
    	}
    	return false;
    }
    
    /**
     * If any elevator moving down
     * @return boolean if any elevator moving down
     */
    public boolean movingDown() {
    	
    	for(ElevatorRequest eachReq: bestElevators) {
    		if(eachReq.getElevDirection().equalsIgnoreCase("down"))
    			return true;
    	}
    	return false;
    }
    
    /**
     * Find all the elevators not above the floor and remove them from the bestElevators list
     * @param floorNum is the floor to reach
     */
    public void elevAbove(int floorNum) {
    	
    	ArrayList<ElevatorRequest> notAbove = new ArrayList<>();
    	
    	for(ElevatorRequest eachReq : bestElevators) {
    		if(eachReq.getElevCurrentFloor() >= floorNum)
    			notAbove.add(eachReq);
    	}
    	bestElevators.removeAll(notAbove);
    }
    
    /**
     * Find all the elevators not below the floor and remove them from the bestElevators list
     * @param floorNum is the floor to reach
     */
    public void elevBelow(int floorNum) {
    	
    	ArrayList<ElevatorRequest> notAbove = new ArrayList<>();
    	
    	for(ElevatorRequest eachReq : bestElevators) {
    		if(eachReq.getElevCurrentFloor() <= floorNum)
    			notAbove.add(eachReq);
    	}
    	bestElevators.removeAll(notAbove);
    }
    
    /**
     * Find all the elevators not moving up and remove them from the bestElevators list
     */
    public void upElevs() {
    	
    	ArrayList<ElevatorRequest> notAbove = new ArrayList<>();
    	
    	for(ElevatorRequest eachReq : bestElevators) {
    		if(!eachReq.getElevDirection().equalsIgnoreCase("up"))
    			notAbove.add(eachReq);
    	}
    	bestElevators.removeAll(notAbove);
    }
    
    /**
     * Find all the elevators not moving down and remove them from the bestElevators list
     */
    public void downElevs() {
    	
    	ArrayList<ElevatorRequest> notAbove = new ArrayList<>();
    	
    	for(ElevatorRequest eachReq : bestElevators) {
    		if(!eachReq.getElevDirection().equalsIgnoreCase("down"))
    			notAbove.add(eachReq);
    	}
    	bestElevators.removeAll(notAbove);
    }
    
    /**
     * Find any elev not idle and remove it from the bestElevators list
     */
    public void findIdleElev() {
    	
    	ArrayList<ElevatorRequest> notAbove = new ArrayList<>();
    	
    	for(ElevatorRequest eachReq : bestElevators) {
    		if(eachReq.getIsMotorOn())
    			notAbove.add(eachReq);
    	}
    	bestElevators.removeAll(notAbove);
    }


    /**
     * This method runs the thread
     */
	@Override
	public void run() {
		
		//Create the handlers for each subsystem
		FloorHandler floorHandler = new FloorHandler(this);
		ElevHandler elevHandler = new ElevHandler(this);

		Thread floorHandlerThread = new Thread(floorHandler);
		Thread elevHandlerThread = new Thread(elevHandler);
		
		
		//Start the threads
		floorHandlerThread.start();
		elevHandlerThread.start();
		
		// TODO Auto-generated method stub
		while(systemOnline) {

			while(floorRequests.isEmpty()){ //if no floor requests, sleep
				//nothing to do
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			while(elevatorRequests.isEmpty()){ //if no elevator requests, sleep
				//do nothing
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			while(true){
				//removeFailedElev();

				//will match new elevator request with floor requests
				getBestElevator();
				
				/*
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				*/
				
				//We want to isolate the elevators that are already servicing a floor request
				ArrayList<ElevatorRequest> inServiceRequests = (ArrayList<ElevatorRequest>) elevatorRequests
						.stream()
						.filter(request -> servicingRequests.containsKey(request.getID()))
						.collect(Collectors.toList());

				for (ElevatorRequest request : inServiceRequests){
					elevatorRequests.remove(request);
					updateElevator(request);
				}

				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * Getter method for bestElevator ID
	 * @return int the ID of the best elevator
	 */
	public int getBestElevID() {
		return bestElevID;
	}

	/**
	 * Getter method to check if system is online
	 * @return boolean if system online
	 */
	public boolean isSystemOnline() {
		return systemOnline;
	}

	/**
	 * Getter method to return the list of best elevators
	 * @return Arraylist of best elevators
	 */
	public ArrayList<ElevatorRequest> getBestElevators() {
		return bestElevators;
	}

	/**
	 * Setter method for setting best elevators lists
	 * @param bestElevators arrayList of best elevs
	 */
	public void setBestElevators(ArrayList<ElevatorRequest> bestElevators) {
		this.bestElevators = bestElevators;
	}
	
	/**
	 * to add floor request
	 * @param request is FloorRequest to be added
	 */
	public void addFloorRequest(FloorRequest request){
		floorRequests.add(request);
	}

	/**
	 * to add elevator request
	 * @param request is ElevatorRequest to be added
	 */
	public void addElevatorRequest(ElevatorRequest request){
		elevatorRequests.add(request);
	}
	
	/**
	 * Main method
	 * @param args
	 */

	/**
	 * Setter for servicing requests
	 * @param servicingRequests
	 */
	public void setServicingRequests(HashMap<Integer, FloorRequest> servicingRequests) {
		this.servicingRequests = servicingRequests;
	}


	public static void main(String args[]) {
		Thread s = new Thread(new Scheduler(22));
		s.start();
	}

	/**
	 * remove the elevator from the requests list
	 * and adds back the floor request for reassignment
	 */
	public void removeFailedElev(ElevatorRequest request){
		 
		if (request.getFailure()){
			//add the servicing floor request back to floor requests list for reassignment
			System.out.println("Removing failed elevator " + request.getID());
			floorRequests.add(servicingRequests.get(request.getID()));
			servicingRequests.remove(request.getID());
		}
		
	}
    
}
