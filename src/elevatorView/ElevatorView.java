package elevatorView;

import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;


/**
 * 
 * @author Ali Alvi
 * 
 * This class represents the visual aspect of each elevator
 *
 */
public class ElevatorView{

	private static final int FLOORNUM = 22; //default floor is 22
	private int floorNum; //total floors
	
	String[] defaultOrCustom = {"Choose Default Values", "Choose your own values"}; //options to choose in the dialog
	
	JLabel totalFloors; //label to display total floors
	JLabel currentFloor; //label to display current floor
	JLabel doorStatus; //label to display door status
	JLabel motorStatus; //label to display motor status
	JLabel passengerStatus; //label to display passenger status
	JLabel elevatorStatus; //label to display elevator status
	JLabel faultStatus; //label to display fault status
	JTextArea consoleOutput; //label to display console output
	JScrollPane consoleDisplay; //scroll pane that will hold consoel output


	/**
	 * Instantiates the ElevatorView class
	 * @param elevatorNum is the id of each elevator
	 */
	public ElevatorView(int elevatorNum) {
		
		//check if user wants default values or custom
		int getUserResponse = JOptionPane.showOptionDialog(null, "Would you like to choose your own values for floor",
				"ENTER", JOptionPane.INFORMATION_MESSAGE, 0, null, defaultOrCustom, defaultOrCustom[0]);
		
		if(getUserResponse == 0) { //if default values
			floorNum = FLOORNUM;
		}
		else if(getUserResponse == 1) { //if custome values
			floorNum = Integer.parseInt(JOptionPane.showInputDialog("How many floors would you like? ")); //store custom values
		}
		else
			System.exit(0);
		
		setUpGui(elevatorNum); //set up the gui with elevator id
	}

	/**
	 * Default ElevatorView constructor
	 */
    public ElevatorView() {
    }

    /**
	 * This method creates a JFrame and sets it with all the 
	 * necessary labels and formats it nicely for the user
	 * @param elevatorNum is the elevator id
	 */
	public void setUpGui(int elevatorNum) {
		
		//Create a JFrame 
		JFrame frame = new JFrame();
		
		//Set exit button, size, layout and title for the frame
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(300, 600);
		frame.setLayout(new GridLayout(9,1));
		frame.setTitle("Elevator " + elevatorNum);
		
		//Instantiate all the labels with a default message
		totalFloors = new JLabel("Total floors: " + floorNum);
		currentFloor = new JLabel("Current Floor : N/A");
		doorStatus = new JLabel("Doors Status: N/A");
		motorStatus = new JLabel("Motor Status: N/A");
		passengerStatus = new JLabel("Passenger Status: N/A");
		elevatorStatus = new JLabel("Elevator Direction: N/A");
		faultStatus = new JLabel("Fault Status: Working");
		consoleOutput = new JTextArea();
		consoleOutput.setEditable(false);
		consoleDisplay = new JScrollPane(consoleOutput);

		//Check the elevator id
		switch(elevatorNum) {
		
		case 1: //if first elevator, set a distinct color
			frame.getContentPane().setBackground(new Color(227, 127, 120));
			break;
		case 2: //if second elev, set a distinct color
			frame.getContentPane().setBackground(new Color(189, 247, 141));
			break;
		case 3: //if third elev, set a distinct color
			frame.getContentPane().setBackground(new Color(135, 162, 199));
			break;
		default: //more than 3 elevs then set the same color for rest of them
			frame.getContentPane().setBackground(new Color(217, 132, 67));
			break;
			
		}
		
		//add all the labels to the frame
		frame.add(totalFloors);
		frame.add(currentFloor);
		frame.add(doorStatus);
		frame.add(motorStatus);
		frame.add(passengerStatus);
		frame.add(elevatorStatus);
		frame.add(faultStatus);
		frame.add(consoleDisplay);
		
		//make it visible
		frame.setVisible(true);
		
	}
	
	/**
	 * Updates the current floor of the elevator
	 * @param floor is the new current floor
	 */
	public void updateCurrentFloor(int floor) {
		currentFloor.setText("Current Floor: " + floor);
	}
	
	/**
	 * Updates the door status of the elevator
	 * @param ifOpen is the new door status
	 */
	public void updateDoorStatus(boolean ifOpen) {
		
		if(ifOpen) //if door is open
			doorStatus.setText("Doors Status: Doors Open"); 
		else
			doorStatus.setText("Doors Status: Doors Closed");
	}
	
	/**
	 * Updates the motor status of the elevator
	 * @param ifOn is the new motor status
	 */
	public void updateMotorStatus(boolean ifOn) {
		if(ifOn) //if motor is on
			motorStatus.setText("Motor Status: Motor On");
		else
			motorStatus.setText("Motor Status: Motor Off");
	}
	
	/**
	 * Update the passenger status for the elevator
	 * @param pickedUp is the status of passengers
	 */
	public void updatePassengerStatus(boolean pickedUp) {
		if(pickedUp) //if passengers picked up
			passengerStatus.setText("Passenger Status: Passengers have been picked up");
		else
			passengerStatus.setText("Passenger Status: Passengers have been dropped off");
	}
	
	/**
	 * Updates the direction of the elevator
	 * @param elevDirection is the direction of the elevator
	 */
	public void updateElevatorDirection(String elevDirection) {
		elevatorStatus.setText("Elevator Direction: " + elevDirection);
	}
	
	/**
	 * Updates the fault status of the elevator
	 * @param fault is the boolean value of if elevator is not working
	 */
	public void updateFaultStatus(boolean fault) {
		if(fault)
			faultStatus.setText("Fault Status: Decommissioned");
	}
	
	/**
	 * This method writes to the console in the gui to 
	 * show live status of elevator
	 * @param output
	 */
	public void writeToConsole(String output) {
		consoleOutput.append(output);
		consoleOutput.append("\n");
	}
	
	//Testing purposes
	public static void main(String[] args) {
		ElevatorView view = new ElevatorView(1);
	}
}
