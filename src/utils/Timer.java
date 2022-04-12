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
	public static final int THRESHOLD_TIME = 2850;
	
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
	 * Getter method for getting the elapsed time in nanoseconds. 
	 * @return elapsed time. 
	 */
	public long getElapsedTime() {
		return stopTime-initialTime;
	}
	
	/**
	 * Getter method for getting the elapsed time in seconds format. 
	 * @return elapsed time in seconds (double format)
	 */
	public double getSeconds() {
		return ((getElapsedTime())/(double)1000000000);
	}
	
	/**
	 * Getter method for getting the elapsed time in minutes format. 
	 * @return elapsed time in minutes (double format)
	 */
	public double getMinutes() {
		return getSeconds()/(double)60;
	}
	
	/**
	 * Checks if elevator time passes its deadline. 
	 * @return true - a fault has occured.
	 * 		   false - a fault did not occured. 
	 * 
	 */
	public boolean checkFault() {
		
		if((getElapsedTime()/ (double)1000000) > (double)THRESHOLD_TIME)
			return true;
		
		return false;
	}
	
	/**
	 * Logs the elapsed time that the timer recorded. 
	 */
	public void logRequestTime() {
		timeStops.add(getElapsedTime());
	}
	
	/**
	 * Method that returns the average time in an average seconds format.
	 * @return average time in double format. 
	 */
	public double returnAvgTimeInSecs() {
		double sum = 0;
		
		if(timeStops.isEmpty()) {
			return 0;
		}
		//Calculates average time from all the times logged.
		for(Long time : timeStops) {
			double secs;
			secs = time / (double) 1000000000;
			sum += secs;
		}
		return (sum / timeStops.size());
			
	}
	
	/**
	 * Getter function for extracting the number of requests a 
	 * specific elevator handled. 
	 * @return an int for number of requests handled. 
	 */
	public int getNumRequestsHandled() {
		return timeStops.size();
	}
	
	
}
