package tests;

import elevatorSubsystem.*;
import org.junit.Before;
import org.junit.Test;

import elevatorStates.ElevatorState;
import elevatorStates.MovingDown;
import elevatorStates.MovingUp;
import elevatorStates.Stationary;
import scheduler.Scheduler;
import scheduler.SchedulerRequest;

import static org.junit.Assert.*;

/**
 * This class contains JUnit tests to test the addition of state machines and their mechanics.
 */
public class StateTests {
    SchedulerRequest scheduledRequestsRequest;
    Scheduler buf;
    ElevatorSubsystem elevatorSubsystem;

    @Before
    public void SetUp(){
        scheduledRequestsRequest = new SchedulerRequest();
        buf = new Scheduler();
        elevatorSubsystem = new ElevatorSubsystem(buf);

        ElevatorState.stationary = new Stationary(elevatorSubsystem);
        ElevatorState.movingUp = new MovingUp(elevatorSubsystem);
        ElevatorState.movingDown = new MovingDown(elevatorSubsystem);
        ElevatorState.current = ElevatorState.stationary;   //we start at stationary

    }

    /**
     * Basic test to see if the stationary enterState() method is behaving as expected
     */
    @Test
    public void ElevatorEnterStateTest() {
        ElevatorState.current.enterState();
        assertEquals(ElevatorState.current, ElevatorState.stationary );
    }


    /**
     * Test to see if given a request, the elevator goes into the correct state.
     * Given a current floor of 1 and a destination floor of 2, we expect the state to change to moving up.
     * After moving the floors, the elevator shall return to the stationary state.
     */
    @Test
    public void ElevatorUpdateStateTest() {
        ElevatorState.current.enterState();
        scheduledRequestsRequest.setCurrentFloor(1);
        scheduledRequestsRequest.setDirection(1);
        scheduledRequestsRequest.setArrivalTime("1:00:00");
        scheduledRequestsRequest.setDestinationFloor(2);
        buf.sendToScheduler(scheduledRequestsRequest, "floor");
        //simulating the back end where the floor would be sending the request to the scheduler which will then send a request to elevator resulting in state change

        ElevatorState.current.updateState();

        assertEquals(ElevatorState.current, ElevatorState.movingUp );

        ElevatorState.current.updateState();
        //We want to update the state after the elevator has moved up, now we can see that the elevator becomes stationary to let passengers off

        assertEquals(ElevatorState.current, ElevatorState.stationary );
    }




}