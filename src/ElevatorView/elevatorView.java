package ElevatorView;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class elevatorView extends JFrame{

	private static final int FLOORNUM = 22;
	private static final int ELEVATORNUM = 4;
	private int floorNum;
	private int elevatorNum;
	
	String[] defaultOrCustom = {"Default values", "Choose your own values"};
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -767119722479073077L;

	
	public elevatorView() {
		
		int getUserResponse = JOptionPane.showOptionDialog(null, "Would you like to choose your own values for floor and elevator? ",
				"ENTER", JOptionPane.INFORMATION_MESSAGE, 0, null, defaultOrCustom, defaultOrCustom[0]);
		
		if(getUserResponse == 0) {
			floorNum = FLOORNUM;
			elevatorNum = ELEVATORNUM;
		}
		else if(getUserResponse == 1) {
			floorNum = Integer.parseInt(JOptionPane.showInputDialog("How many floors would you like? "));
			elevatorNum = Integer.parseInt(JOptionPane.showInputDialog("How many elevators would you like? "));
		}
		else
			System.exit(0);
		
		setUpGui();
	}
	
	public void setUpGui() {
		
	}
}
