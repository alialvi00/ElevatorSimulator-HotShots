package tests;

import ElevatorView.ElevatorView;
import elevatorStates.*;
import elevatorSubsystem.*;
import floorSubsystem.FloorRequest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import scheduler.Scheduler;
import utils.Timer;

import java.util.HashMap;


import static org.junit.Assert.*;

/**
 * This class contains JUnit tests to test the timer and the elevator + scheduler failure mechanics.
 * AREEB HAQ
 */
public class TimerAndFailureTests {
    Timer timer;
    Scheduler buf;
    ElevatorSubsystem elevatorSubsystem;
    ElevatorView elevatorView;

    /**
     * This @Before method will setup the necessary fields required for every individual test
     * This way, we will minimize code duplication and can associate all the same instantiations into one setup method
     */
    @Before
    public void SetUp(){
        buf = new Scheduler(22,4);
        timer = new Timer();
        elevatorView = new ElevatorView();
        elevatorSubsystem = new ElevatorSubsystem();

    }

    @After
    public void TearDown(){
        elevatorSubsystem.shutDown();
    }

    /**
     * Test to check if timer returns a fault after elapsed time.
     */
    @Test
    public void timerTest() throws InterruptedException {
        System.out.println("\n" + "-----------------------------TIMER TESTS-----------------------------");
        timer.startTime();
        System.out.println("START TIME:   " + timer.getStartTime()/ (double)1000000);
        Thread.sleep(100); //there should be no fault with this little wait time.

        timer.stopTime();
        System.out.println("STOP TIME:    " +timer.getStopTime()/ (double)1000000);
        System.out.println("ELAPSED TIME: " +timer.getElapsedTime()/ (double)1000000 + " (APPROX 100ms AS SET IN TEST)");
        assertFalse(timer.checkFault());
    }


    /**
     * Test to see if the removal of a failed elev is successful
     */
    @Test
    public void removeFailedElevTest(){
        ElevatorRequest elev1 = new ElevatorRequest(1, 15, false, true);
        elev1.setElevDirection("up");

        HashMap<Integer, FloorRequest> servicingRequests = new HashMap<>();
        FloorRequest floorRequest = new FloorRequest(5, "10:10:10", "up", 6, true);
        servicingRequests.put(1, floorRequest);
        buf.setServicingRequests(servicingRequests);

        assertNotNull(servicingRequests.get(1)); //here we dont expect the servicing request to not be null since the elev 1 is not a failure yet.
        elev1.setFailure();
        assertTrue(elev1.getFailure()); //elev 1 has failed
        System.out.println("-----------------------REMOVE FAILED ELEV TEST-----------------------");
        buf.removeFailedElev(elev1);


        assertNull(servicingRequests.get(1)); //here we dont expect the servicing request to be null since the elev 1 is now a failure

    }


    /**
     * Basic test on the updateState in a failure State
     *
     */
    @Test
    public void FailureStateTest(){
        elevatorView.setUpGui(22);
        Elevator elevator = new Elevator(1, elevatorSubsystem, elevatorView);
        ElevatorState failureState = new Failure(elevator);
        System.out.println("\n" + "--------------------------FAILURE STATE TEST-------------------------");
        failureState.enterState();

        ElevatorRequest elev1 = new ElevatorRequest(1, 15, false, true);

        assertNull(failureState.updateState(elev1)); // we should be returned a null after an update after a failure state

    }


    /**
     * Basic test on the updateState in any other state: i.e movingdown state and failure has been set.
     *
     */
    @Test
    public void OtherStateFailureTest(){
        elevatorView.setUpGui(22);
        Elevator elevator = new Elevator(1, elevatorSubsystem, elevatorView);
        ElevatorState failure = new Failure(elevator);
        ElevatorState movingDown = new MovingDown(elevator);

        System.out.println("\n" + "------------------------FAILED AFTER UPDATE STATE--------------------");
        movingDown.enterState();

        ElevatorRequest elev1 = new ElevatorRequest(1, 15, false, true);
        elev1.setFailure();
        movingDown.updateState(elev1);

        assertEquals(movingDown.updateState(elev1).getClass() , failure.getClass()); //the failure has been set to true so we should now go into a failure state.
    }



}