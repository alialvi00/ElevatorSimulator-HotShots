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

    private int bestElevID;
    private ElevatorRequest schToElev;



	private boolean systemOnline;
    
    DatagramSocket sendSocket, receiveSocket;
    DatagramPacket elevatorPacket;

	CopyOnWriteArrayList<ElevatorRequest> elevatorRequests;
	CopyOnWriteArrayList<FloorRequest> floorRequests;
    ArrayList<ElevatorRequest> bestElevators;
    ArrayList<Boolean> updatedElevReq;
    HashMap<String, Integer> floorMapping;
	HashMap<Integer, FloorRequest> servicingRequests;
    
    /**
     * Create the scheduler constructor.
     */
    public Scheduler(int floors){
    	
        fsm = new SchedulerStateMachine();

        elevatorRequests = new CopyOnWriteArrayList<>();
        floorRequests = new CopyOnWriteArrayList<>();
        updatedElevReq = new ArrayList<>();

        floorMapping = new HashMap<>();
		servicingRequests = new HashMap<>(); //this will be used to keep track of requests being serviced
        
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
        }
        catch(SocketException se) {
        	se.printStackTrace();
        	System.exit(1);
        }
        
        systemOnline = true;
        
        
    }


    //update any working elevator
    public void updateElevator(ElevatorRequest elevatorRequest) {

    	ElevatorRequest schReq = null;
		int destinationFloor = servicingRequests.get(elevatorRequest.getID()).getDestinationFloor();
		int pickUpFloor = servicingRequests.get(elevatorRequest.getID()).getID();
		int floorToReach = pickUpFloor;

		if (elevatorRequest.isPickedUp()){
			floorToReach = destinationFloor;
		}

		if(elevatorRequest.getElevCurrentFloor() == floorToReach) {
			schReq = new ElevatorRequest(elevatorRequest.getID(),true,false);
			if(floorToReach == destinationFloor){
				servicingRequests.remove(elevatorRequest.getID());
				schReq.setPickedUp(false);
			} else {
				schReq.setPickedUp(true);
			}
		}
		else if(elevatorRequest.getElevCurrentFloor() > floorToReach) {
			schReq = new ElevatorRequest(elevatorRequest.getID(), false, true);
			schReq.setElevDirection("down");
			if(floorToReach == destinationFloor) {
				schReq.setPickedUp(true);
			}
		}
		else if(elevatorRequest.getElevCurrentFloor() < destinationFloor) {
			schReq = new ElevatorRequest(elevatorRequest.getID(), false, true);
			schReq.setElevDirection("up");
			if(floorToReach == destinationFloor) {
				schReq.setPickedUp(true);
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
    
    //send most ideal elev
    public void getBestElevator() {
    	
    	bestElevators = new ArrayList<>();
    	ArrayList<FloorRequest> processedRequests = new ArrayList<>();
    	bestElevID = -1;
    	String elevDir = "";



    	for(FloorRequest eachFloor: floorRequests) {
    		
    		ArrayList<ElevatorRequest> availableElevators = new ArrayList<>();
			//filter out the elevators that are already servicing floor requests
			for(ElevatorRequest elevator : elevatorRequests){
				if (!servicingRequests.containsKey(elevator.getID())){
					availableElevators.add(elevator);
				}
			}
    		
			if(!availableElevators.isEmpty()) {
	    		int floorNum = eachFloor.getID();
	    		
	    		if(sameFloorElev(floorNum, availableElevators) & ifIdle()) {
	    			findIdleElev();
	    			
	    			if(!bestElevators.isEmpty()) {
	    				bestElevID = bestElevators.get(0).getID();
	    				elevDir = "";
	    			}
	    		}
	    		else if(allElevAbove(floorNum, availableElevators)) {
	    			
	    			if(movingDown() && eachFloor.getElevatorDirection().equalsIgnoreCase("down")) {
	    				
	    				downElevs();
	    				bestElevID = nearestElevator(eachFloor);
	    				elevDir = "down";
	    			}
	    			else if(ifIdle()) {
	    				findIdleElev();
	    				bestElevID = bestElevators.get(0).getID();
	    				elevDir = "down";
	    			}	
	    		}
	    		else if(allElevBelow(floorNum, availableElevators)) {
	    			if(movingUp() && eachFloor.getElevatorDirection().equalsIgnoreCase("up")) {
	    				upElevs();
	    				bestElevID = nearestElevator(eachFloor);
	    				elevDir = "up";
	    			}
	    			else if(ifIdle()) {
	    				findIdleElev();
	    				bestElevID = bestElevators.get(0).getID();
	    				elevDir = "up";
	    			}
	    	   }
	    	   else if(ifElevAbove(floorNum, availableElevators) & ifElevBelow(floorNum, availableElevators)){
	    			
	    			for(ElevatorRequest elevReq : elevatorRequests) {
	    				bestElevators.add(elevReq);
	    			}
	    			
	    			if(eachFloor.getElevatorDirection().equalsIgnoreCase("up") && movingUp()) {
	    				upElevs();
	    				bestElevID = nearestElevator(eachFloor);
	    				elevDir = "up";
	    			}
	    			else if(eachFloor.getElevatorDirection().equalsIgnoreCase("down") && movingDown()) {
	    				downElevs();
	    				bestElevID = nearestElevator(eachFloor);
	    				elevDir = "down";
	    			}
	    			else if(ifIdle()) {
	    				findIdleElev();
	    				bestElevID = bestElevators.get(0).getID();
	    				elevDir = "";
	    			}
	    			
	    			bestElevID = nearestElevator(eachFloor);
	    	   }
	    	   if(bestElevID != -1) {
	    		   if(elevDir.equalsIgnoreCase("")) {
	    			   schToElev = new ElevatorRequest(bestElevID, true, false);
	    		   } else {
	    			   schToElev = new ElevatorRequest(bestElevID, false, true);
	    		   }
	    		   schToElev.setElevDirection(elevDir);
	    		   servicingRequests.put(bestElevID, eachFloor);
	    		   for(ElevatorRequest request : elevatorRequests) {
	    			   if(request.getID() == bestElevID)
	    				   elevatorRequests.remove(request);
	    		   }
				   //Optional<ElevatorRequest> requestToRemove = elevatorRequests.stream().filter(request -> request.getID() == bestElevID).findFirst();
				   //elevatorRequests.remove(requestToRemove);
	    		   processedRequests.add(eachFloor);
	    		   floorRequests.remove(eachFloor);
				   sendElevator(schToElev);
			   }
			}
    	}		
    	//floorRequests.remove(processedRequests);
    }
    
    //if any elev above desti floor
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
    
    //if any elev below dest floor
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
    
    //if any elev idle
    public boolean ifIdle() {
    	
    	for(ElevatorRequest eachReq: bestElevators) {
    		if(!eachReq.getIsMotorOn())
    			return true;
    	}
    	return false;
    }
    
    //if any elev on same floor as dest
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
    
    //if all elevs above dest
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
    
    //if all elevs below dest
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

    //find the nearest elev to dest
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


	@Override
	public void run() {
		FloorHandler floorHandler = new FloorHandler(this);
		ElevHandler elevHandler = new ElevHandler(this);

		Thread floorHandlerThread = new Thread(floorHandler);
		Thread elevHandlerThread = new Thread(elevHandler);
		
		floorHandlerThread.start();
		elevHandlerThread.start();
		
		// TODO Auto-generated method stub
		while(systemOnline) {

			while(floorRequests.isEmpty()){
				//nothing to do
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			while(elevatorRequests.isEmpty()){
				//do nothing
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			while(true){
				//will match new elevator request with floor requests
				getBestElevator();
				
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
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
	public int getBestElevID() {
		return bestElevID;
	}


	public boolean isSystemOnline() {
		return systemOnline;
	}


	public ArrayList<ElevatorRequest> getBestElevators() {
		return bestElevators;
	}

	public void setBestElevators(ArrayList<ElevatorRequest> bestElevators) {
		this.bestElevators = bestElevators;
	}
	public void addFloorRequest(FloorRequest request){
		floorRequests.add(request);
	}

	public void addElevatorRequest(ElevatorRequest request){
		elevatorRequests.add(request);
	}
	
	public static void main(String args[]) {
		Thread s = new Thread(new Scheduler(22));
		s.start();
	}
    
}
