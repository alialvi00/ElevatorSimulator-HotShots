/**
 * 
 */
package floorSubsystem;

/**
 * @author Akaash Kapoor
 *
 */
public class FloorAttributes {
	
	/** Determines if elevator is going up and notifies floor. */
	private boolean buttonLampUp;
	
	/** Determines if elevator is going down and notifies floor. */
	private boolean buttonLampDown;
	
	/** Lights up the up lamp. */
	private boolean directionLampUp;
	
	/** Lights up the down lamp. */
	private boolean directionLampDown;
	
	
	public FloorAttributes() {
		
		this.buttonLampUp = false;
		this.buttonLampDown= false;
		this.directionLampUp = false;
		this. directionLampDown = false;
	}
	
	/**
	 * Getter function for retrieving the up button.
	 * @return Status of up button in the form of a boolean.
	 */
	public boolean getButtonLampUp() {
		return buttonLampUp;
	}
	
	/**
	 * Setter function for configuring the up button. 
	 * @param buttonLamp The boolean status of the button lamp (on/off). 
	 */
	public void setButtonLampUp(boolean buttonLamp) {
		buttonLampUp = buttonLamp;
	}
	
	/**
	 * Getter function for retrieving the down button.
	 * @return Status of down button in the form of a boolean.
	 */
	public boolean getButtonLampDown() {
		return buttonLampDown;
	}
	
	/**
	 * Setter function for configuring the down button. 
	 * @param buttonLamp The boolean status of the button lamp (on/off). 
	 */
	public void setButtonLampDown(boolean buttonLamp) {
		buttonLampDown = buttonLamp;
	}
	
	/**
	 * Getter function for retrieving the status of lamp in the up direction. 
	 * @return Status of the lamp in the upwards direction in the form of a boolean.
	 */
	public boolean getDirectionLampUp() {
		return directionLampUp;
	}
	
	/**
	 * Setter function for configuring the lamp in the up direction. 
	 * @param directionLamp The boolean status of the lamp (on/off). 
	 */
	public void setDirectionLampUp(boolean directionLamp) {
		directionLampUp = directionLamp;
	}
	
	/**
	 * Getter function for retrieving the status of lamp in the down direction. 
	 * @return Status of the lamp in the downwards direction in the form of a boolean.
	 */
	public boolean getDirectionLampDown(){
		return directionLampDown;
	}
	
	/**
	 * Setter function for configuring the lamp in the downward direction.
	 * @param directionLamp The boolean status of the lamp (on/off). 
	 */
	public void setDirectionLampDown(boolean directionLamp) {
		directionLampDown = directionLamp;
	}
	
	
}
