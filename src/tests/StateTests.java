package tests;

import elevatorSubsystem.*;
import ElevatorView.*;
import org.junit.Before;
import org.junit.Test;

import elevatorStates.ElevatorState;
import elevatorStates.MovingDown;
import elevatorStates.MovingUp;
import elevatorStates.Stationary;
import scheduler.Scheduler;

import static org.junit.Assert.*;

/**
 * This class contains JUnit tests to test the addition of state machines and their mechanics.
 */
public class StateTests {
    Scheduler buf;
    ElevatorSubsystem elevatorSubsystem = new ElevatorSubsystem();
    ElevatorView elevatorView = new ElevatorView();



    /**
     * Test to see if given a request, the elevator goes into the correct state.
     * Once the elevator direction is up it must be moving up so we move in to the movingUp state.
     */
    @Test
    public void ElevatorUpdateStateTest() {
        elevatorView.setUpGui(20);
        Elevator elevator = new Elevator(1, elevatorSubsystem, elevatorView);
        buf = new Scheduler(22,4);


        ElevatorState.stationary = new Stationary(elevator);
        ElevatorState.movingUp = new MovingUp(elevator);
        ElevatorState.movingDown = new MovingDown(elevator);
        ElevatorState.current = ElevatorState.stationary;   //we start at stationary

        ElevatorState.current.enterState();


        ElevatorRequest elev1 = new ElevatorRequest(1, 1, false, true); //close the doors and the motor is running = moving

        elev1.setElevDirection("Up"); //moving up

        ElevatorState.current = ElevatorState.current.updateState(elev1);

        assertEquals(ElevatorState.current.getClass(), ElevatorState.movingUp.getClass());
    }




}