package scheduler;

import java.util.concurrent.LinkedBlockingQueue;

import scheduler.SchedulerState.Event;

public class Scheduler {
    private LinkedBlockingQueue<SchedulerRequest> buffer;
    private SchedulerRequest schRequest;
    private SchedulerStateMachine fsm;

    public Scheduler(){
        buffer = new LinkedBlockingQueue<>();
        schRequest = new SchedulerRequest();
        fsm = new SchedulerStateMachine();
    }
    
    public LinkedBlockingQueue<SchedulerRequest> getBuffer(){
    	return buffer;
    }

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
    public void processFloorRequest() {
    	try {
			schRequest.setArrivalTime(buffer.take().get(0));
			schRequest.setCurrentFloor(Integer.parseInt(buffer.take().get(1)));
			schRequest.setDestinationFloor(Integer.parseInt(buffer.take().get(3)));
			
			if(buffer.take().get(2).equalsIgnoreCase("up"))
				schRequest.setDirection(1);
			else if(buffer.take().get(2).equalsIgnoreCase("down"))
				schRequest.setDirection(0);
				
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    **/
    
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
     * @return queque of type LinkedBlockingQueue
     */
    public LinkedBlockingQueue<SchedulerRequest> getRequestQue() {
		return buffer;
	}
    
}
