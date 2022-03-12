package scheduler;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.concurrent.LinkedBlockingQueue;

import elevatorSubsystem.ElevatorRequest;
import floorSubsystem.FloorRequest;
import scheduler.SchedulerState.Event;

/**
 * This class represents the scheduler system which maintains the current state of the scheduler and allows
 * for alerting the elevator.
 * @author Ali Alvi Raj Sandhu
 * @version 1.0
 */

public class Scheduler implements Runnable{

    private SchedulerStateMachine fsm;
    
    InetAddress elevatorAddress;
    InetAddress floorAddress;
    
    private int floorPort, elevatorPort;
    private int floors, numElevators, bestElevID;
    private FloorRequest floorRequest;
    private ElevatorRequest elevatorRequest, schToElev;
    
    private boolean systemOnline;
    
    DatagramSocket sendSocket, receiveSocket;
    DatagramPacket receivePacket, floorPacket, elevatorPacket;

    ArrayList<DatagramPacket> receiveRequests;
    ArrayList<ElevatorRequest> elevatorRequests;
    ArrayList<FloorRequest> floorRequests;
    ArrayList<ElevatorRequest> bestElevators;
    ArrayList<Boolean> updatedElevReq;
    HashMap<String, Integer> floorMapping;
    private int pickUp[];
    private int dest[];

	private DatagramSocket elevatorReceiveSocket;
    
    /**
     * Create the scheduler constructor.
     */
    public Scheduler(int floors, int numElevators){
    	
        fsm = new SchedulerStateMachine();
        this.floors = floors;
        this.numElevators = numElevators;
        
        receiveRequests = new ArrayList<>();
        elevatorRequests = new ArrayList<>();
        floorRequests = new ArrayList<>();
        updatedElevReq = new ArrayList<>();
        floorMapping = new HashMap<>();
        pickUp = new int[floors];
        dest = new int[floors];
        
        try {
        	elevatorAddress = InetAddress.getLocalHost();
        	floorAddress = InetAddress.getLocalHost();
        }
        catch(UnknownHostException he) {
        	he.printStackTrace();
        	System.exit(1);
        }
        
        try {
        	sendSocket = new DatagramSocket();
        	receiveSocket = new DatagramSocket(69);
        	elevatorReceiveSocket = new DatagramSocket(79);
        }
        catch(SocketException se) {
        	se.printStackTrace();
        	System.exit(1);
        }
        
        systemOnline = true;
        
        
    }
    
    
    
    
    
    //Receive either floor or elevator requests from same socket
    public void handleRequests() {
    	
    	receivePacket = new DatagramPacket(new byte[700], 700);
    	
    	try {
    		receiveSocket.receive(receivePacket);
    		receiveRequests.add(receivePacket);
    	}
    	catch(IOException ie) {
    		ie.printStackTrace();
    		System.exit(1);
    	}
    }
    
    //Send a packet depending on what type of request
    public synchronized void createSendPacket() {
    	
    	if(!receiveRequests.isEmpty()) {
    		
    		for(DatagramPacket eachRequest: receiveRequests) {
    			
    			Object currentRequest = bytesToObj(eachRequest);
    			
    			if(currentRequest instanceof ElevatorRequest) {
    				
    				elevatorRequest = (ElevatorRequest)currentRequest;
    				elevatorAddress = eachRequest.getAddress();
    				elevatorPort = eachRequest.getPort();
    				elevatorRequests.add(elevatorRequest);
    				updateElevator();
    			}
    			
    			//If floor req, update floor fields then send the best elevator
    			else if(currentRequest instanceof FloorRequest) {
    				
    				floorRequest = (FloorRequest)currentRequest;
    				floorAddress = eachRequest.getAddress();
    				floorPort = eachRequest.getPort();
    				updateFloorReq(); //store destination floors
    			}
    			
    			if(!elevatorRequests.isEmpty())
    				getBestElevator();
    		}
    	}
    }
    
    public void updateFloorReq() {
    	if(!floorRequests.contains(floorRequest)) {
    		floorRequests.add(floorRequest);	
    		pickUp[floorRequest.getID()-1] = floorRequest.getID();
    		dest[floorRequest.getID()-1] = floorRequest.getDestinationFloor();
    		//floorMapping.put("pickup", floorRequest.getID());
    		//floorMapping.put("destination", floorRequest.getDestinationFloor());
    	}
    }
    
    
    
    
    //if elev above destination floor
    public boolean aboveDestination(int floorToReach, int currentFloor) {
    	return floorToReach < currentFloor;
    }
    
    //if elev below destination floor
    public boolean belowDestination(int floorToReach, int currentFloor) {
    	return floorToReach > currentFloor;
    }
    
    //clear any pending floor req
    public void clearFloorReq() {
    	floorRequests.clear();
    }
    
    
    public boolean sameFloor(int floor, int[] floors) {
    	for(int i=0; i< floors.length; i++) {
    		if(floors[i] == floor) {
    			return true;
    		}
    	}
    	return false;
    }
    
    public boolean aboveDest(int floor, int[] floors) {
    	for(int i=0; i< floors.length; i++) {
    		if(floors[i] < floor) {
    			return true;
    		}
    	}
    	return false;
    }
    
    public boolean belowDest(int floor, int[] floors) {
    	for(int i=0; i< floors.length; i++) {
    		if(floors[i] > floor) {
    			return true;
    		}
    	}
    	return false;
    }

    //update any working elevator
    public void updateElevator() {
    	
    	ElevatorRequest toElev = elevatorRequest;
    	ElevatorRequest schReq = null;
    	//int floorToReach = floorMapping.get("pickup");
    	//int currentFloor = toElev.getElevCurrentFloor();
    	
    	if(toElev.isPickedUp()) {
    		
    		if(sameFloor(elevatorRequest.getElevCurrentFloor(), dest)) {
    			schReq = new ElevatorRequest(toElev.getID(),true,false);
    		}
    		else if(dest.length != 0) {
	    		if(aboveDest(elevatorRequest.getElevCurrentFloor(), dest)) {
	    			schReq = new ElevatorRequest(toElev.getID(), false, true);
	    			schReq.setElevDirection("down");
	    		}
	    		else if(belowDest(elevatorRequest.getElevCurrentFloor(), dest)) {
	    			schReq = new ElevatorRequest(toElev.getID(), false, true);
	    			schReq.setElevDirection("up");
	    		} 
	    	}
    	}
    	else {
    	
    		
    		if(sameFloor(elevatorRequest.getElevCurrentFloor(), pickUp)) {
    			schReq = new ElevatorRequest(toElev.getID(),true,false);
    			schReq.isPickedUp();
    		}
    		else if(dest.length != 0) {
	    		if(aboveDest(elevatorRequest.getElevCurrentFloor(), pickUp)) {
	    			schReq = new ElevatorRequest(toElev.getID(), false, true);
	    			schReq.setElevDirection("down");
	    		}
	    		else if(belowDest(elevatorRequest.getElevCurrentFloor(), pickUp)) {
	    			schReq = new ElevatorRequest(toElev.getID(), false, true);
	    			schReq.setElevDirection("up");
	    		} 
	    	}
    	}
    	
    	if(schReq != null)
    		sendElevator(schReq);
    	
    }	
    
    //send the elevator the request to pickup passenger
    public void sendElevator(ElevatorRequest schToElev) {
    	
    	this.schToElev = schToElev;
    	byte[] msg = this.schToElev.byteRepresentation();
    	elevatorPacket = new DatagramPacket(msg, msg.length, elevatorAddress, 20);
    	
    	try {
    		sendSocket.send(elevatorPacket);
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
    
    //send confirmation to floor
    public void sendFloor(FloorRequest schToFloor) {
    	
    	byte[] msg = schToFloor.byteRepresentation();
    	floorPacket = new DatagramPacket(msg, msg.length,floorAddress,floorPort);
    	
    	try {
    		sendSocket.send(floorPacket);
    	}
    	catch(IOException ie) {
    		ie.printStackTrace();
    		System.exit(1);
    	}
    }
    
    //send most ideal elev
    public void getBestElevator() {
    	
    	bestElevators = new ArrayList<>();
    	ArrayList<FloorRequest> processedRequests = new ArrayList<>();
    	bestElevID = -1;
    	String elevDir = "";
    	
    	for(FloorRequest eachFloor: floorRequests) {
    		
    		int floorNum = eachFloor.getID();
    		
    		if(sameFloorElev(floorNum) & ifIdle()) {
    			findIdleElev();
    			
    			if(!bestElevators.isEmpty()) {
    				bestElevID = bestElevators.get(0).getID();
    				elevDir = "";
    			}
    				
    		}
    		else if(allElevAbove(floorNum)) {
    			
    			if(movingDown() && eachFloor.getElevatorDirection().equalsIgnoreCase("down")) {
    				
    				downElevs();
    				bestElevID = nearestElevator();
    				elevDir = "down";
    			}
    			else if(ifIdle()) {
    				findIdleElev();
    				bestElevID = bestElevators.get(0).getID();
    				elevDir = "down";
    			}	
    		}
    		else if(allElevBelow(floorNum)) {
    			if(movingUp() && eachFloor.getElevatorDirection().equalsIgnoreCase("up")) {
    				upElevs();
    				bestElevID = nearestElevator();
    				elevDir = "up";
    			}
    			else if(ifIdle()) {
    				findIdleElev();
    				bestElevID = bestElevators.get(0).getID();
    				elevDir = "up";
    			}
    	   }
    	   else if(ifElevAbove(floorNum) & ifElevBelow(floorNum)){
    			
    			for(ElevatorRequest elevReq : elevatorRequests) {
    				bestElevators.add(elevReq);
    			}
    			
    			if(eachFloor.getElevatorDirection().equalsIgnoreCase("up") && movingUp()) {
    				upElevs();
    				bestElevID = nearestElevator();
    				elevDir = "up";
    			}
    			else if(eachFloor.getElevatorDirection().equalsIgnoreCase("down") && movingDown()) {
    				downElevs();
    				bestElevID = nearestElevator();
    				elevDir = "down";
    			}
    			else if(ifIdle()) {
    				findIdleElev();
    				bestElevID = bestElevators.get(0).getID();
    				elevDir = "";
    			}
    			
    			bestElevID = nearestElevator();
    	   }
    	   if(bestElevID != -1) {
    		   if(elevDir.equalsIgnoreCase("")) {
    			   schToElev = new ElevatorRequest(bestElevID, true, false);
    		   } else {
    			   schToElev = new ElevatorRequest(bestElevID, false, true);
    		   }
    		   schToElev.setElevDirection(elevDir);
    		   processedRequests.add(eachFloor);
			   sendElevator(schToElev);
			   
		   }
    	}		
    	floorRequests.removeAll(processedRequests);
    }
    
    //if any elev above desti floor
    public boolean ifElevAbove(int floorNum) {
    	
    	bestElevators.clear();
    	boolean sameFloor = false;
    	
    	for(int i=0; i < elevatorRequests.size(); i++) {
    		if(elevatorRequests.get(i).getElevCurrentFloor() > floorNum) {
    			sameFloor = true;
    			bestElevators.add(elevatorRequests.get(i));
    		}
    	}
    	return sameFloor;
    }
    
    //if any elev below dest floor
    public boolean ifElevBelow(int floorNum) {
    	
    	bestElevators.clear();
    	boolean sameFloor = false;
    	
    	for(int i=0; i < elevatorRequests.size(); i++) {
    		if(elevatorRequests.get(i).getElevCurrentFloor() < floorNum) {
    			sameFloor = true;
    			bestElevators.add(elevatorRequests.get(i));
    		}
    	}
    	return sameFloor;
    }
    
    //if any elev idle
    public boolean ifIdle() {
    	
    	for(ElevatorRequest eachReq: bestElevators) {
    		if(!eachReq.getIsMotorOn())
    			return true;
    	}
    	return false;
    }
    
    //if any elev on same floor as dest
    public boolean sameFloorElev(int floorNum) {
    	
    	bestElevators.clear();
    	boolean sameFloor = false;
    	
    	for(int i=0; i < elevatorRequests.size(); i++) {
    		if(floorNum == elevatorRequests.get(i).getElevCurrentFloor()) {
    			sameFloor = true;
    			bestElevators.add(elevatorRequests.get(i));
    		}
    	}
    	return sameFloor;
    }
    
    //if all elevs above dest
    public boolean allElevAbove(int floorNum) {
    	
    	bestElevators.clear();
    	boolean sameFloor = false;
    	
    	for(int i=0; i < elevatorRequests.size(); i++) {
    		
    		bestElevators.add(elevatorRequests.get(i));
    		
    		if(elevatorRequests.get(i).getElevCurrentFloor() < floorNum) {
    			return sameFloor;
    		}
    	}
    	return true;
    }
    
    //if all elevs below dest
    public boolean allElevBelow(int floorNum) {
    	
    	bestElevators.clear();
    	boolean sameFloor = false;
    	
    	for(int i=0; i < elevatorRequests.size(); i++) {
    		
    		bestElevators.add(elevatorRequests.get(i));
    		
    		if(elevatorRequests.get(i).getElevCurrentFloor() > floorNum) {
    			return sameFloor;
    		}
    	}
    	return true;
    }

    //find the nearest elev to dest
    public int nearestElevator() {
    	
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
    
    //if any elev moving up
    public boolean movingUp() {
    	
    	for(ElevatorRequest eachReq: bestElevators) {
    		if(eachReq.getElevDirection().equalsIgnoreCase("up"))
    			return true;
    	}
    	return false;
    }
    
    //if any elev moving down
    public boolean movingDown() {
    	
    	for(ElevatorRequest eachReq: bestElevators) {
    		if(eachReq.getElevDirection().equalsIgnoreCase("down"))
    			return true;
    	}
    	return false;
    }
    
    //find all the elevs above dest floor and store it in list
    public void elevAbove(int floorNum) {
    	
    	ArrayList<ElevatorRequest> notAbove = new ArrayList<>();
    	
    	for(ElevatorRequest eachReq : bestElevators) {
    		if(eachReq.getElevCurrentFloor() >= floorNum)
    			notAbove.add(eachReq);
    	}
    	bestElevators.removeAll(notAbove);
    }
    
    //find all the elevs below dest floor and store it in list
    public void elevBelow(int floorNum) {
    	
    	ArrayList<ElevatorRequest> notAbove = new ArrayList<>();
    	
    	for(ElevatorRequest eachReq : bestElevators) {
    		if(eachReq.getElevCurrentFloor() <= floorNum)
    			notAbove.add(eachReq);
    	}
    	bestElevators.removeAll(notAbove);
    }
    
    //find all elevs that are moving up
    public void upElevs() {
    	
    	ArrayList<ElevatorRequest> notAbove = new ArrayList<>();
    	
    	for(ElevatorRequest eachReq : bestElevators) {
    		if(!eachReq.getElevDirection().equalsIgnoreCase("up"))
    			notAbove.add(eachReq);
    	}
    	bestElevators.removeAll(notAbove);
    }
    
    //find all elevs that are moving down
    public void downElevs() {
    	
    	ArrayList<ElevatorRequest> notAbove = new ArrayList<>();
    	
    	for(ElevatorRequest eachReq : bestElevators) {
    		if(!eachReq.getElevDirection().equalsIgnoreCase("down"))
    			notAbove.add(eachReq);
    	}
    	bestElevators.removeAll(notAbove);
    }
    
    //find any idle elev
    public void findIdleElev() {
    	
    	ArrayList<ElevatorRequest> notAbove = new ArrayList<>();
    	
    	for(ElevatorRequest eachReq : bestElevators) {
    		if(eachReq.getIsMotorOn())
    			notAbove.add(eachReq);
    	}
    	bestElevators.removeAll(notAbove);
    }
    
    
    
    //convert bytes mssg to object
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
		// TODO Auto-generated method stub
		while(systemOnline) {
			handleRequests();
			createSendPacket();
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static void main(String args[]) {
		Thread s = new Thread(new Scheduler(22,4));
		s.start();
	}
    
}
