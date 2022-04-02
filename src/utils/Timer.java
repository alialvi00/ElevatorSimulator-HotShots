/**
 * 
 */
package utils;


import java.util.ArrayList;

/**
 * Timer class used for measuring Elevator Arrival timings. 
 * @author Akaash Kapoor
 *
 */
public class Timer{

	/**Sets the max time an elevator should reach the floor.*/
	public static final int THRESHOLD_TIME = 2500;
	
	/**Stops all the recorded elapsed times of the elevator. Maybe used in Iteration 5.*/
	private ArrayList<Long> timeStops = new ArrayList<>();
	
	/**Stores current time in nanoseconds.*/
	private long initialTime;
	
	/**Stores arrival time in nanoseconds.*/
	private long stopTime;
	
	/**
	 * Constructor for initializing the timer class. 
	 */
	public Timer() {
		timeStops = new ArrayList<>();
	}
	
	/**
	 * Getter method for getting start time. 
	 * @return initial time. 
	 */
	public long getStartTime() {
		return initialTime;
	}
	
	/**
	 * Setter method for starting the timer. 
	 */
	public void startTime() {
		initialTime = System.nanoTime();
	}
	
	/**
	 * Getter method for getting the stop time. 
	 * @return arrival time. 
	 */
	public long getStopTime() {
		return stopTime;
	}
	
	/**
	 * Setter method for stoping the timer and recording elapsed time. 
	 */
	public void stopTime() {
		stopTime = System.nanoTime();
	}
	
	/**
	 * Getter method for getting the elapsed time. 
	 * @return elapsed time. 
	 */
	public long getElapsedTime() {
		return stopTime-initialTime;
	}
	
	public double getSeconds() {
		return ((getElapsedTime())/(double)1000000000);
	}
	
	public double getMinutes() {
		return getSeconds()/(double)60;
	}
	
	/**
	 * Checks if elevator time passes its deadline. 
	 * @return true - it reaches its deadline.
	 * 		   false - didnt reach its deadline. 
	 * 
	 */
	public boolean checkFault() {
		
		if((getElapsedTime()/ (double)1000000) > (double)THRESHOLD_TIME)
			return true;
		
		return false;
	}
	
	public void logRequestTime() {
		timeStops.add(getElapsedTime());
	}
	
	public double returnAvgTimeInSecs() {
		double sum = 0;
		
		if(timeStops.isEmpty()) {
			return 0;
		}
		
		for(Long time : timeStops) {
			sum += time;
		}
		return (sum / timeStops.size()) / (double) 1000000000;
			
	}
	
	
}
