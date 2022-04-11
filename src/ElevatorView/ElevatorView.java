package ElevatorView;

import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class ElevatorView{

	private static final int FLOORNUM = 22;
	private int floorNum;
	
	String[] defaultOrCustom = {"Choose Default Values", "Choose your own values"};
	String[] errorMessage = {"Zero is not a valid number of elevators"};
	
	JLabel totalFloors; 
	JLabel currentFloor;
	JLabel doorStatus;
	JLabel motorStatus;
	JLabel passengerStatus;
	JLabel elevatorStatus;
	JLabel faultStatus;
	JTextArea consoleOutput;
	JScrollPane consoleDisplay;


	
	public ElevatorView(int elevatorNum) {
		
		int getUserResponse = JOptionPane.showOptionDialog(null, "Would you like to choose your own values for floor",
				"ENTER", JOptionPane.INFORMATION_MESSAGE, 0, null, defaultOrCustom, defaultOrCustom[0]);
		
		if(getUserResponse == 0) {
			floorNum = FLOORNUM;
		}
		else if(getUserResponse == 1) {
			floorNum = Integer.parseInt(JOptionPane.showInputDialog("How many floors would you like? "));
		}
		else
			System.exit(0);
		
		setUpGui(elevatorNum);
	}
	
	public void setUpGui(int elevatorNum) {
		
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(300, 600);
		frame.setLayout(new GridLayout(9,1));
		frame.setTitle("Elevator " + elevatorNum);
		
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

		
		switch(elevatorNum) {
		
		case 1:
			frame.getContentPane().setBackground(new Color(227, 127, 120));
			break;
		case 2:
			frame.getContentPane().setBackground(new Color(189, 247, 141));
			break;
		case 3:
			frame.getContentPane().setBackground(new Color(135, 162, 199));
			break;
		default:
			frame.getContentPane().setBackground(new Color(217, 132, 67));
			break;
			
		}
		
		frame.add(totalFloors);
		frame.add(currentFloor);
		frame.add(doorStatus);
		frame.add(motorStatus);
		frame.add(passengerStatus);
		frame.add(elevatorStatus);
		frame.add(faultStatus);
		frame.add(consoleDisplay);
		
		
		frame.setVisible(true);
		
	}
	
	public void updateCurrentFloor(int floor) {
		currentFloor.setText("Current Floor: " + floor);
	}
	
	public void updateDoorStatus(boolean ifOpen) {
		if(ifOpen)
			doorStatus.setText("Doors Status: Doors Open");
		else
			doorStatus.setText("Doors Status: Doors Closed");
	}
	
	public void updateMotorStatus(boolean ifOn) {
		if(ifOn)
			motorStatus.setText("Motor Status: Motor Open");
		else
			motorStatus.setText("Motor Status: Motor Closed");
	}
	
	public void updatePassengerStatus(boolean pickedUp) {
		if(pickedUp)
			passengerStatus.setText("Passenger Status: Passengers have been picked up");
		else
			passengerStatus.setText("Passenger Status: Passengers have been dropped off");
	}
	
	public void updateElevatorDirection(String elevDirection) {
		elevatorStatus.setText("Elevator Direction: " + elevDirection);
	}
	
	public void updateFaultStatus(boolean fault) {
		if(fault)
			faultStatus.setText("Fault Status: Decommissioned");
	}
	
	public void writeToConsole(String output) {
		consoleOutput.append(output);
		consoleOutput.append("\n");
	}
	
	public static void main(String[] args) {
		ElevatorView view = new ElevatorView(1);
	}
}
