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
    private int floors, numElevators;
    private FloorRequest floorRequest;
    private ElevatorRequest elevatorRequest;
    
    private boolean systemOnline;
    
    DatagramSocket sendSocket, receiveSocket;
    DatagramPacket receivePacket, floorPacket, elevatorPacket;

    ArrayList<DatagramPacket> receiveRequests;
    ArrayList<ElevatorRequest> elevatorRequests;
    PriorityQueue<FloorRequest> floorRequests;
    
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
    			
    			Object currentRequest = bytesToPacket(eachRequest);
    			
    			if(currentRequest instanceof ElevatorRequest) {
    				
    				elevatorRequest = (ElevatorRequest)currentRequest;
    				elevatorAddress = eachRequest.getAddress();
    				elevatorRequests.add(elevatorRequest.getID(), elevatorRequest);
    			}
    			
    			else if(currentRequest instanceof FloorRequest) {
    				
    				floorRequest = (FloorRequest)currentRequest;
    				floorAddress = eachRequest.getAddress();
    				floorPort = eachRequest.getPort();
    				if(!floorRequests.contains(floorRequest))
    					floorRequests.add(floorRequest);
    			}
    		}
    	}
    }
    
    public Object bytesToPacket(DatagramPacket request) {
    	
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

    /**
     * Method which gets scheduler request.
     * @return scheduler request blocking queue.
     */
    public LinkedBlockingQueue<FloorRequest> getFloorBuffer(){
    	return floorBuffer;
    }
    
    public LinkedBlockingQueue<ElevatorRequest> getElevatorBuffer(){
    	return elevatorBuffer;
    }
    
    /**
     * Method which will send data through a blocking queue and prints the current state of scheduler.
     * @param data the data to be sent through the blocking queue.
     * @param subsystem represents the specific subsystem to be notified.
     */

    public void sendToScheduler(SchedulerRequest data, String subsystem){
        try {
        	
        	System.out.println("Current state of scheduler is: " + fsm.getCurrentState());
            buffer.put(data);
            if(subsystem.equalsIgnoreCase("elevator")) {
            	System.out.println("Elevator has arrived and informed scheduler ");
            }
            else if(subsystem.equalsIgnoreCase("floor"))
            	System.out.println("Floor has now received passengers and sent a request to Scheduler ");
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        fsm.transition(Event.RECEIVED_REQUEST);
    	System.out.println("Current state of scheduler is: " + fsm.getCurrentState() + "\n");
        System.out.println("*************************** \n");
    }
    
    /**
     * Method which sends requests to the different subsystems and prints current state of scheduler.
     * @param subsystem the specified subsystem to receive information.
     * @return
     */
    
    public SchedulerRequest recieveFromScheduler(String subsystem){
        try {
            schRequest = buffer.take();  
            
            if(subsystem.equalsIgnoreCase("elevator")) {
            	System.out.println("Scheduler has sent the request to elevator");
            	schRequest.setSubsystem("elevator");
            }
            else if(subsystem.equalsIgnoreCase("floor")) {
            	System.out.println("Scheduler has sent the request to floor");
            	schRequest.setSubsystem("floor");
            }
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        fsm.transition(Event.SCHEDULED_REQUEST);
    	System.out.println("Current state of scheduler is: " + fsm.getCurrentState() + "\n");
    	System.out.println("*************************** \n");
        return schRequest;
        
    }
    
    /**
     * Method which will checks if the scheduler request is empty.
     * @return null if the scheduler request is empty.
     */
    
    public SchedulerRequest alertElevator() {
    	return schRequest.isEmpty() ? schRequest : null;
    }
    
    /**
     * 
     * inform scheduler of arrival
     */
    public void elevatorArrived(String newArrivalTime, int currentFloor) {
    	System.out.println("Elevator has arrived at its destination at floor: " + currentFloor + " at time: " 
    											+ newArrivalTime + "\n");
    }
    
    /**
     * returns the buffer queue
     * @return queue of type LinkedBlockingQueue
     */
    public LinkedBlockingQueue<SchedulerRequest> getRequestQue() {
		return buffer;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(systemOnline) {
			handleRequests();
			createSendPacket();
			
			Thread.sleep(250);
		}
	}
    
}
