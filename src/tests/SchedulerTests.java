package tests;

import elevatorSubsystem.ElevatorRequest;
import floorSubsystem.FloorRequest;
import org.junit.Before;
import org.junit.Test;
import scheduler.Scheduler;

import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * This class contains JUnit tests to test the addition of Multiple elevators and system distribution
 * The scheduler class now has algorithms to determine the best elevator to use and
 * in this class the many algorithms and mechanisms will be unit tested to ensure
 * accuracy, correctness, and delivery.
 *
 * AREEB HAQ
 */
public class SchedulerTests {
    Scheduler buf;


    /**
     * This @Before method will setup the necessary fields required for every individual test
     * This way, we will minimize code duplication and can associate all the same instantiations into one setup method
     */
    @Before
    public void SetUp(){
        buf = new Scheduler(22);
    }

    /**
     * Basic functionality test to ensure that the program will run once we have setup the scheduler
     */
    @Test
    public void systemOnlineTest() {
        assertEquals(true, buf.isSystemOnline());  //after elevators are setup
    }


    /**
     * Best elevator functionality test to ensure that the program will chose the best elevator to service
     * This elevator is emulating how the scheduler is handling elevator and floor requests and making the best decision based on the given requests
     *
     *
     * Within this test, the entire scheduler subsystem is being tested as the getBestElevator method has all the different helper methods
     */
    @Test
    public void bestElevatorTest() {
        ElevatorRequest elev1 = new ElevatorRequest(1, 15, true, false);
        ElevatorRequest elev2 = new ElevatorRequest(2, 4, true, false); //we expect this elevator to go up
        buf.addElevatorRequest(elev1);
        buf.addElevatorRequest(elev2);

        FloorRequest floorRequest = new FloorRequest(5, "10:10:10", "up", 6, true); //since 5-4 < 15-5 we expect that elev2 goes to this floor
        buf.addFloorRequest(floorRequest);
        buf.getBestElevator();

        assertEquals(2, buf.getBestElevID());

    }

    /**
     * Similar to the bestElevatorTest this test will return test to see if the method produces the id of the closest elevator to the given floor
     */
    @Test
    public void nearestElevatorTest() {
        ElevatorRequest elev1 = new ElevatorRequest(1, 20, true, false); //closest elevator to the floor request (5)
        ElevatorRequest elev2 = new ElevatorRequest(2, 21, true, false);
        buf.addElevatorRequest(elev1);
        buf.addElevatorRequest(elev2);

        FloorRequest floorRequest = new FloorRequest(5, "10:10:10", "up", 6, true); //since 20-5 < 21-5 we expect that elev1 goes to 5th floor
        buf.addFloorRequest(floorRequest);
        buf.getBestElevator();

        assertEquals(1, buf.nearestElevator(floorRequest));

    }

    /**
     * Test to see if the methods can correctly check if any of the elevators are moving up or down or idle
     */
    @Test
    public void ifMovingUpOrDownOrIdleTest() {
        ElevatorRequest elev1 = new ElevatorRequest(1, 15, true, true);
        elev1.setElevDirection("up");
        ElevatorRequest elev2 = new ElevatorRequest(2, 4, true, true);
        elev2.setElevDirection("down");
        ElevatorRequest elev3 = new ElevatorRequest(2, 4, true, false); //idle since motor off
        elev2.setElevDirection("down");
        ArrayList<ElevatorRequest> elevatorRequests = new ArrayList<>();
        elevatorRequests.add(elev1);
        elevatorRequests.add(elev2);
        elevatorRequests.add(elev3);
        buf.setBestElevators(elevatorRequests);
        boolean elev1MoveUp = buf.movingUp();
        boolean elev2MoveDown = buf.movingDown();
        boolean isIdle = buf.ifIdle();

        assertTrue(elev1MoveUp);
        assertTrue(elev2MoveDown);
        assertTrue(isIdle);

    }

    /**
     * Test to see if the method correctly checks if there is a elevator currently at the same floor as the request
     */
    @Test
    public void ifSameFloorElev() {
        ElevatorRequest elev1 = new ElevatorRequest(1, 15, true, false);
        ArrayList<ElevatorRequest> bestElevators = new ArrayList<>();
        buf.setBestElevators(bestElevators);
        ArrayList<ElevatorRequest> elevatorRequests = new ArrayList<>();
        elevatorRequests.add(elev1);


        boolean check = buf.sameFloorElev(15, elevatorRequests);
        assertTrue(check);

    }

    /**
     * Test to see if the method correctly checks if there is a elevator above or below, and if all above or all below
     */
    @Test
    public void ifElevAboveAndBelowTest() {
        ElevatorRequest elev1 = new ElevatorRequest(1, 15, true, false);
        ElevatorRequest elev2 = new ElevatorRequest(2, 4, true, false);
        ArrayList<ElevatorRequest> elevatorRequests = new ArrayList<>();
        elevatorRequests.add(elev1);
        elevatorRequests.add(elev2);
        ArrayList<ElevatorRequest> bestElevators = new ArrayList<>();
        buf.setBestElevators(bestElevators);
        boolean above = buf.ifElevAbove(10, elevatorRequests);
        boolean below = buf.ifElevBelow(10, elevatorRequests);
        boolean allAbove = buf.allElevAbove(10, elevatorRequests);
        boolean allBelow = buf.allElevAbove(10, elevatorRequests);

        assertTrue(above);
        assertTrue(below);
        assertFalse(allAbove);
        assertFalse(allBelow);

    }


    /**
     * Test to see if the method correctly checks which elevators are idle
     */
    @Test
    public void findIdleElevTest() {
        ElevatorRequest elev1 = new ElevatorRequest(1, 15, true, false);//idle
        ElevatorRequest elev2 = new ElevatorRequest(2, 4, true, false);//idle
        ElevatorRequest elev3 = new ElevatorRequest(3, 12, true, true);
        ElevatorRequest elev4 = new ElevatorRequest(4, 9, true, false);//idle
        ElevatorRequest elev5 = new ElevatorRequest(5, 7, true, true);
        ElevatorRequest elev6 = new ElevatorRequest(6, 5, true, false);//idle


        ArrayList<ElevatorRequest> bestElevators = new ArrayList<>();
        bestElevators.add(elev1);
        bestElevators.add(elev2);
        bestElevators.add(elev3);
        bestElevators.add(elev4);
        bestElevators.add(elev5);
        bestElevators.add(elev6);

        buf.setBestElevators(bestElevators);

        buf.findIdleElev();
        bestElevators = buf.getBestElevators();

        ArrayList<ElevatorRequest> expectedIdleElevators = new ArrayList<>();
        expectedIdleElevators.add(elev1);
        expectedIdleElevators.add(elev2);
        expectedIdleElevators.add(elev4);
        expectedIdleElevators.add(elev6);


        assertEquals(bestElevators, expectedIdleElevators);

    }

    /**
     * Test to see if the method correctly checks and returns which elevators are moving up
     */
    @Test
    public void allElevUpTest() {
        ElevatorRequest elev1 = new ElevatorRequest(1, 15, true, true);
        elev1.setElevDirection("up");
        ElevatorRequest elev2 = new ElevatorRequest(2, 4, true, true);
        elev2.setElevDirection("down");
        ElevatorRequest elev3 = new ElevatorRequest(3, 12, true, true);
        elev3.setElevDirection("up");
        ElevatorRequest elev4 = new ElevatorRequest(4, 9, true, true);
        elev4.setElevDirection("down");
        ElevatorRequest elev5 = new ElevatorRequest(5, 7, true, true);
        elev5.setElevDirection("up");
        ElevatorRequest elev6 = new ElevatorRequest(6, 5, true, true);
        elev6.setElevDirection("down");


        ArrayList<ElevatorRequest> bestElevators = new ArrayList<>();
        bestElevators.add(elev1);
        bestElevators.add(elev2);
        bestElevators.add(elev3);
        bestElevators.add(elev4);
        bestElevators.add(elev5);
        bestElevators.add(elev6);

        buf.setBestElevators(bestElevators);

        buf.upElevs();
        bestElevators = buf.getBestElevators();

        ArrayList<ElevatorRequest> expectedUpElevators = new ArrayList<>();
        expectedUpElevators.add(elev1);
        expectedUpElevators.add(elev3);
        expectedUpElevators.add(elev5);


        assertEquals(bestElevators, expectedUpElevators);

    }

    /**
     * Test to see if the method correctly checks and returns which elevators are moving down
     */
    @Test
    public void allElevDownTest() {
        ElevatorRequest elev1 = new ElevatorRequest(1, 15, true, true);
        elev1.setElevDirection("up");
        ElevatorRequest elev2 = new ElevatorRequest(2, 4, true, true);
        elev2.setElevDirection("down");
        ElevatorRequest elev3 = new ElevatorRequest(3, 12, true, true);
        elev3.setElevDirection("up");
        ElevatorRequest elev4 = new ElevatorRequest(4, 9, true, true);
        elev4.setElevDirection("down");
        ElevatorRequest elev5 = new ElevatorRequest(5, 7, true, true);
        elev5.setElevDirection("up");
        ElevatorRequest elev6 = new ElevatorRequest(6, 5, true, true);
        elev6.setElevDirection("down");


        ArrayList<ElevatorRequest> bestElevators = new ArrayList<>();
        bestElevators.add(elev1);
        bestElevators.add(elev2);
        bestElevators.add(elev3);
        bestElevators.add(elev4);
        bestElevators.add(elev5);
        bestElevators.add(elev6);

        buf.setBestElevators(bestElevators);

        buf.downElevs();
        bestElevators = buf.getBestElevators();

        ArrayList<ElevatorRequest> expectedDownElevators = new ArrayList<>();
        expectedDownElevators.add(elev2);
        expectedDownElevators.add(elev4);
        expectedDownElevators.add(elev6);


        assertEquals(bestElevators, expectedDownElevators);

    }



}
