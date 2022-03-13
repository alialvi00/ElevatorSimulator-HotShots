/**
 * 
 */
package utils;

import java.io.Serializable;
import java.time.LocalTime;
import java.time.ZoneId;

/**
 * Timer class used for measuring Elevator Arrival timings. 
 * @author Akaash Kapoor
 *
 */
public class Timer implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 16784L;

	/**Stores current time in milliseconds*/
	private long initialTime;
	
	/**Stores arrival time in milliseconds*/
	private long arrivalTime;
	
	/**Gets local time based on Eastern Time Zone.*/
	private LocalTime localTime;
	
	/**
	 * Constructor for timer class. 
	 */
	public Timer() {
		this.localTime = LocalTime.now(ZoneId.systemDefault());
	}
	
	/**
	 * Getter method for getting start time. 
	 * @return initial time. 
	 */
	public long getInitialTime() {
		return initialTime;
	}
	
	/**
	 * Setter method for starting the timer. 
	 */
	public void setInitialTime() {
		initialTime = System.currentTimeMillis();
	}
	
	/**
	 * Getter method for getting the elapsed time. 
	 * @return arrival time. 
	 */
	public long getArrivalTime() {
		return arrivalTime;
	}
	
	/**
	 * Setter method for stoping the timer and recording elapsed time. 
	 */
	public void setArrivalTime() {
		arrivalTime = System.currentTimeMillis() - initialTime;
	}
	
	/**
	 * Getter method for getting the local time based on EST time zone. 
	 * @return local time. 
	 */
	public LocalTime getLocalTime() {
		return localTime;
	}
	
	
}
