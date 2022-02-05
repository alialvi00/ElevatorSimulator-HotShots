package tests;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import elevatorSubsystem.ElevatorSubsystem;
import floorSubsystem.FloorSubsystem;
import scheduler.Scheduler;

class InputTests {

	/**
	 * This tests if the input file was read in correctly in the FloorSubsystem class
	 */
	@Test
	void readInputTest() {
		Scheduler scheduler = new Scheduler();
		int floor = 1;
		FloorSubsystem floorSubsystem = new FloorSubsystem(scheduler, floor);
		
		try {
			floorSubsystem.readInput();
		} catch (IOException e) {
			fail("no input file");
		}
		
		ArrayList<String> inputList = floorSubsystem.getFloorToSchedulerData();
		assertEquals(inputList.get(0), "14:05:15.0");
		assertEquals(inputList.get(1), "1");
		assertEquals(inputList.get(2), "Up");
		assertEquals(inputList.get(3), "4");	
	}

	/**
	 *This tests the communication between floor and scheduler
	 */
	@Test
	void elevatorDataTest() {
		Scheduler scheduler = new Scheduler();
		int floor = 1;
		FloorSubsystem floorSubsystem = new FloorSubsystem(scheduler, floor);
		
		try {
			floorSubsystem.readInput();
		} catch (IOException e) {
			fail("no input file");
		}
		
		ArrayList<String> inputList = floorSubsystem.getFloorToSchedulerData();
		scheduler.sendFloorData(inputList);
		ArrayList<String> elevatorData = scheduler.getElevatorData();
		assertEquals(inputList, elevatorData);
	}
	
	/**
	 * This tests the communication between scheduler, floor, and elevator 
	 */
	@Test
	void floorDataTest() {
		Scheduler scheduler = new Scheduler();
		int floor = 1;
		FloorSubsystem floorSubsystem = new FloorSubsystem(scheduler, floor);
		
		try {
			floorSubsystem.readInput();
		} catch (IOException e) {
			fail("no input file");
		}
		
		ArrayList<String> inputList = floorSubsystem.getFloorToSchedulerData();
		scheduler.sendFloorData(inputList);
		ArrayList<String> elevatorData = scheduler.getElevatorData();
		scheduler.sendElevatorData(elevatorData);
		ArrayList<String> floorDataArrayList = scheduler.getFloorData();
		assertEquals(inputList, floorDataArrayList);
	}
}
