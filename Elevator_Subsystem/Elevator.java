

public class Elevator implements Runnable{

	private boolean upFloorButton;
	private boolean downFloorButton;
	private boolean upDirectionLamp;
	private boolean downDirectionLamp;
	private Scheduler scheduler;
	private static int numOfElevators = 0;
	private String[] arrOfMessagesFromScheduler;
	private int currentFloor;

	public ElevatorSubsystem(Scheduler scheduler, int currentFloor){
		this.scheduler = scheduler;
		upFloorButton = false;
		downFloorButton = false;
		upDirectionLamp = false;
		downDirectionLamp = false;
		this.currentFloor = currentFloor;

		numOfElevators++;
	}

	/**
	 * The method used to return the message back to the scheduler
	 * @param msg1
	 */
	public String[] sendToScheduler(String[] msg1){
		System.out.println("Elevator sent data to Scheduler");
		return msg1;
	}


	public boolean isUpFloorButton() {
		return upFloorButton;
	}

	public boolean isDownFloorButton() {
		return downFloorButton;
	}

	public boolean isUpDirectionLamp() {
		return upDirectionLamp;
	}

	public boolean isDownDirectionLamp() {
		return downDirectionLamp;
	}

	/**
	 * This is a static counter for the number of instances of elevators made so the floor subsystem knows how many elevators we have.
	 * We can keep track by incrementing the numOfElevators by 1 everytime the constructor is called (i.e a new instance is made)
	 * @return numOfElevators
	 */
	public static int getNumOfElevators() {
		return numOfElevators;
	}

	/**
	 * The runnable override method that will retrieve data from the scheduler and return it to the scheduler.
	 */
	@Override
	public void run() {
		//temporary just to send a message back to scheduler, I will replace it with appropraite messages when the scheduler synchronized methods are done
		String[] stringArr = new String[1];
		stringArr[0] ="MESSAGE";
		while(true){
			arrOfMessagesFromScheduler = scheduler.call(); //.call temporary name for method to retrieve data from scheduler
			while(this.arrOfMessagesFromScheduler.length == 0){
				try{
					wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			}
			sendToScheduler(stringArr);
		}
	}
}
