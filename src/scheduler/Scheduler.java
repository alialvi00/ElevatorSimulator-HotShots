package scheduler;

import java.net.*;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

import elevatorSubsystem.ElevatorRequest;
import scheduler.SchedulerState.Event;

/**
 * This class represents the scheduler system which maintains the current state of the scheduler and allows
 * for alerting the elevator.
 * @author Ali Alvi Raj Sandhu
 * @version 1.0
 */

public class Scheduler extends Thread{
	
    private LinkedBlockingQueue<SchedulerRequest> buffer;
    private SchedulerRequest schRequest;
    private SchedulerStateMachine fsm;
    
    InetAddress elevatorAddress;
    InetAddress floorAddress;
    
    private int floorPort, elevatorPort;
    
    DatagramSocket sendSocket, receiveSocket;
    DatagramPacket receivePacket, floorPacket, elevatorPacket;

    ArrayList<DatagramPacket> receiveRequests;
    ArrayList<ElevatorRequest> elevatorRequests;
    
    /**
     * Create the scheduler constructor.
     */
    public Scheduler(){
        buffer = new LinkedBlockingQueue<>();
        schRequest = new SchedulerRequest();
        fsm = new SchedulerStateMachine();
    }
    
    /**
     * Method which gets scheduler request.
     * @return scheduler request blocking queue.
     */
    public LinkedBlockingQueue<SchedulerRequest> getBuffer(){
    	return buffer;
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
    
}
