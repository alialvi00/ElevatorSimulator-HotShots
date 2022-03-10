package scheduler;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.*;
import java.util.ArrayList;
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
    PriorityQueue<FloorRequest> floorRequests;
    ArrayList<ElevatorRequest> bestElevators;
    ArrayList<Boolean> updatedElevReq;
    
    /**
     * Create the scheduler constructor.
     */
    public Scheduler(int floors, int numElevators){
    	
        fsm = new SchedulerStateMachine();
        this.floors = floors;
        this.numElevators = numElevators;
        
        receiveRequests = new ArrayList<>();
        elevatorRequests = new ArrayList<>();
        floorRequests = new PriorityQueue<>();
        updatedElevReq = new ArrayList<>();
        
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
        }
        catch(SocketException se) {
        	se.printStackTrace();
        	System.exit(1);
        }
        
        systemOnline = false;
        
        setUpElevators();
    }
    
    
    public void setUpElevators() {
    	for(int i =0; i<numElevators; i++) {
    		elevatorRequests.add(new ElevatorRequest(i+1));
    		updatedElevReq.add(i, true);
    	}
    	systemOnline = true;
    }
    
    
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
    
    public void createSendPacket() {
    	
    	if(!receiveRequests.isEmpty()) {
    		
    		for(DatagramPacket eachRequest: receiveRequests) {
    			
    			Object currentRequest = bytesToObj(eachRequest);
    			
    			if(currentRequest instanceof ElevatorRequest) {
    				
    				elevatorRequest = (ElevatorRequest)currentRequest;
    				elevatorAddress = eachRequest.getAddress();
    				elevatorRequests.add(elevatorRequest.getID(), elevatorRequest);
    				updateElevator();
    			}
    			
    			else if(currentRequest instanceof FloorRequest) {
    				
    				floorRequest = (FloorRequest)currentRequest;
    				floorAddress = eachRequest.getAddress();
    				floorPort = eachRequest.getPort();
    				if(!floorRequests.contains(floorRequest))
    					floorRequests.add(floorRequest);
    				sendFloor(new FloorRequest(-1, null, floors, -2)); //-2 means confirmation message sent to floor
    			}
    			while(!receiveRequests.isEmpty())
    				getBestElevator();
    		}
    	}
    }
    
    public void updateElevator() {
    	
    	ElevatorRequest schToElev = null;
    	
    	if(elevatorRequest.getRequestDir().equalsIgnoreCase("elevToSch")) {
    		
    		if(elevatorRequest.getElevatorStatus().equalsIgnoreCase("normal")) {
    			if(elevatorRequest.getDestinationFloor() == elevatorRequest.getElevCurrentFloor()) {
    				schToElev = new ElevatorRequest(elevatorRequest.getID(), null,"stop","schToElev");
    			}
    			else if(elevatorRequest.getDestinationFloor() != 0) {
    				
    				if(elevatorRequest.getElevCurrentFloor() > elevatorRequest.getDestinationFloor() && (elevatorRequest.getDoorsStatus() || !elevatorRequest.getMotorStatus())) {
    					schToElev = new ElevatorRequest(elevatorRequest.getID(), null, "down","schToElev");
    				}
    				else if(elevatorRequest.getElevCurrentFloor() < elevatorRequest.getDestinationFloor() && (elevatorRequest.getDoorsStatus() || !elevatorRequest.getMotorStatus())) {
    					schToElev = new ElevatorRequest(elevatorRequest.getID(), null, "up","schToElev");
    				}
    				else{
    					schToElev = new ElevatorRequest(elevatorRequest.getID(), null, "continue","schToElev");
    				}
    			}
    		}

    		else if(elevatorRequest.getElevatorStatus().equalsIgnoreCase("updateDoor")) {
    			if(elevatorRequest.getDoorsStatus()) {
    				schToElev = new ElevatorRequest(elevatorRequest.getID(), null, "closeDoor","schToElev");
    			}
    			else {
    				schToElev = new ElevatorRequest(elevatorRequest.getID(), null, "openDoor","schToElev");
    			}
    		}
    		
    		else if(elevatorRequest.getElevatorStatus().equalsIgnoreCase("offline")) {
    			//Do nothing ;) akkash ur hot
    		}
    	}
    	if(schToElev != null) {
    		sendElevator(schToElev);
    	}
    }
    
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
    
    public void getBestElevator() {
    	
    	bestElevators = new ArrayList<>();
    	ArrayList<FloorRequest> processedRequests = new ArrayList<>();
    	bestElevID = -1;
    	
    	for(FloorRequest eachFloor: floorRequests) {
    		
    		int floorNum = eachFloor.getID();
    		
    		if(sameFloorElev(floorNum) & ifIdle()) {
    			findIdleElev();
    			
    			if(!bestElevators.isEmpty())
    				bestElevID = bestElevators.get(0).getID();
    		}
    		else if(allElevAbove(floorNum)) {
    			
    			if(movingUp() && eachFloor.getElevatorDirection().equalsIgnoreCase("down")) {
    				
    				downElevs();
    				bestElevID = nearestElevator();
    			}
    			else if(ifIdle()) {
    				findIdleElev();
    				bestElevID = bestElevators.get(0).getID();
    			}	
    		}
    		else if(allElevBelow(floorNum)) {
    			if(movingUp() && eachFloor.getElevatorDirection().equalsIgnoreCase("up")) {
    				elevBelow(floorNum);
    				upElevs();
    				bestElevID = nearestElevator();
    			}
    			else if(ifIdle()) {
    				findIdleElev();
    				bestElevID = bestElevators.get(0).getID();
    			}
    		}
    		else {
    			bestElevators.clear();
    			
    			for(ElevatorRequest eachElev : elevatorRequests) {
    				if(eachElev.isElevOn())
    					bestElevators.add(eachElev);
    			}
    			
    			if(eachFloor.getElevatorDirection().equalsIgnoreCase("up") && movingUp()) {
    				upElevs();
    				elevBelow(floorNum);
    			}
    			else if(eachFloor.getElevatorDirection().equalsIgnoreCase("down") && movingDown()) {
    				downElevs();
    				elevAbove(floorNum);
    			}
    			
    			if(!bestElevators.isEmpty()) {
    				bestElevators.clear();
    				
    				for(ElevatorRequest eachElevatorRequest : elevatorRequests) {
    					if(eachElevatorRequest.isElevOn())
    						bestElevators.add(eachElevatorRequest);
    				}
    				if(ifIdle()) {
    					findIdleElev();
    					bestElevID = nearestElevator();
    				}
    			}
    			else {
    				bestElevID = -1;
    			}
    		}
    		if(bestElevID != -1) {
    			schToElev = new ElevatorRequest(bestElevID,"normal",null,"schToElev");
    			sendElevator(schToElev);
    			processedRequests.add(eachFloor);
    			updatedElevReq.add(bestElevID, false);
    		}
    	}
    	floorRequests.removeAll(processedRequests);
    }
    
    public boolean ifElevAbove(int floorNum) {
    	
    	bestElevators.clear();
    	boolean sameFloor = false;
    	
    	for(int i=0; i < elevatorRequests.size(); i++) {
    		if(elevatorRequests.get(i).getElevCurrentFloor() > floorNum && elevatorRequests.get(i).isElevOn()) {
    			sameFloor = true;
    			bestElevators.add(elevatorRequests.get(i));
    		}
    	}
    	return sameFloor;
    }
    
    public boolean ifElevBelow(int floorNum) {
    	
    	bestElevators.clear();
    	boolean sameFloor = false;
    	
    	for(int i=0; i < elevatorRequests.size(); i++) {
    		if(elevatorRequests.get(i).getElevCurrentFloor() < floorNum && elevatorRequests.get(i).isElevOn()) {
    			sameFloor = true;
    			bestElevators.add(elevatorRequests.get(i));
    		}
    	}
    	return sameFloor;
    }
    
    public boolean ifIdle() {
    	
    	for(ElevatorRequest eachReq: bestElevators) {
    		if(!eachReq.getMotorStatus())
    			return true;
    	}
    	return false;
    }
    
    public boolean sameFloorElev(int floorNum) {
    	
    	bestElevators.clear();
    	boolean sameFloor = false;
    	
    	for(int i=0; i < elevatorRequests.size(); i++) {
    		if(floorNum == elevatorRequests.get(i).getElevCurrentFloor() && updatedElevReq.get(i)) {
    			sameFloor = true;
    			bestElevators.add(elevatorRequests.get(i));
    		}
    	}
    	return sameFloor;
    }
    
    public boolean allElevAbove(int floorNum) {
    	
    	bestElevators.clear();
    	boolean sameFloor = false;
    	
    	for(int i=0; i < elevatorRequests.size(); i++) {
    		
    		bestElevators.add(elevatorRequests.get(i));
    		
    		if(elevatorRequests.get(i).getElevCurrentFloor() < floorNum && elevatorRequests.get(i).isElevOn()) {
    			return sameFloor;
    		}
    	}
    	return true;
    }
    
    public boolean allElevBelow(int floorNum) {
    	
    	bestElevators.clear();
    	boolean sameFloor = false;
    	
    	for(int i=0; i < elevatorRequests.size(); i++) {
    		
    		bestElevators.add(elevatorRequests.get(i));
    		
    		if(elevatorRequests.get(i).getElevCurrentFloor() > floorNum && elevatorRequests.get(i).isElevOn()) {
    			return sameFloor;
    		}
    	}
    	return true;
    }

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
    
    public boolean movingUp() {
    	
    	for(ElevatorRequest eachReq: bestElevators) {
    		if(eachReq.getSchStatus().equalsIgnoreCase("up"))
    			return true;
    	}
    	return false;
    }
    
    public boolean movingDown() {
    	
    	for(ElevatorRequest eachReq: bestElevators) {
    		if(eachReq.getSchStatus().equalsIgnoreCase("down"))
    			return true;
    	}
    	return false;
    }
    
    public void elevAbove(int floorNum) {
    	
    	ArrayList<ElevatorRequest> notAbove = new ArrayList<>();
    	
    	for(ElevatorRequest eachReq : bestElevators) {
    		if(eachReq.getElevCurrentFloor() >= floorNum)
    			notAbove.add(eachReq);
    	}
    	bestElevators.removeAll(notAbove);
    }
    
    public void elevBelow(int floorNum) {
    	
    	ArrayList<ElevatorRequest> notAbove = new ArrayList<>();
    	
    	for(ElevatorRequest eachReq : bestElevators) {
    		if(eachReq.getElevCurrentFloor() <= floorNum)
    			notAbove.add(eachReq);
    	}
    	bestElevators.removeAll(notAbove);
    }
    
    public void upElevs() {
    	
    	ArrayList<ElevatorRequest> notAbove = new ArrayList<>();
    	
    	for(ElevatorRequest eachReq : bestElevators) {
    		if(!eachReq.getSchStatus().equalsIgnoreCase("up"))
    			notAbove.add(eachReq);
    	}
    	bestElevators.removeAll(notAbove);
    }
    
    public void downElevs() {
    	
    	ArrayList<ElevatorRequest> notAbove = new ArrayList<>();
    	
    	for(ElevatorRequest eachReq : bestElevators) {
    		if(!eachReq.getSchStatus().equalsIgnoreCase("down"))
    			notAbove.add(eachReq);
    	}
    	bestElevators.removeAll(notAbove);
    }
    
    public void findIdleElev() {
    	
    	ArrayList<ElevatorRequest> notAbove = new ArrayList<>();
    	
    	for(ElevatorRequest eachReq : bestElevators) {
    		if(!eachReq.getMotorStatus())
    			notAbove.add(eachReq);
    	}
    	bestElevators.removeAll(notAbove);
    }
    
    
    
    
    public Object bytesToObj(DatagramPacket request) {
    	
    	ByteArrayInputStream inputStream = new ByteArrayInputStream(request.getData());
    	ObjectInputStream objectStream = null;
    	Object requestObject = null;
    	
    	try {
			new ObjectInputStream(new BufferedInputStream(inputStream));
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
				Thread.sleep(250);
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
