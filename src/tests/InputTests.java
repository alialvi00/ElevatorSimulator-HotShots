package tests;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import elevatorSubsystem.ElevatorSubsystem;
import floorSubsystem.FloorSubsystem;
import scheduler.Scheduler;
import input.Reader;

class InputTests {

	/**
	 * This tests if the input file was read in correctly in the FloorSubsystem class
	 */
	@Test
	void readInputTest() {
		Reader reader = new Reader();
		
		try {
			reader.readInput();
		} catch (IOException e) {
			fail("no input file");
		}
		
		ArrayList<String> inputList = reader.getDataInfo();
		assertEquals(inputList.get(0), "14:05:15.0");
		assertEquals(inputList.get(1), "1");
		assertEquals(inputList.get(2), "Up");
		assertEquals(inputList.get(3), "4");	
	}

	/**
	 *This tests the communication between floor and scheduler
	 */
	@Test
	void floorToSchedulerTest() {
		Scheduler scheduler = new Scheduler();
		Reader reader = new Reader();
		FloorSubsystem floorSubsystem = new FloorSubsystem(scheduler);
		
		try {
			reader.readInput();
		} catch (IOException e) {
			fail("no input file");
		}
		
		ArrayList<String> inputList = floorSubsystem.getData();
		scheduler.sendToScheduler(inputList, null);
		assertEquals(inputList, scheduler.getBuffer().peek());
	}
	
	/**
	 * This tests the communication between scheduler, floor, and elevator 
	 */
	@Test
	void floorToElevatorTest() {
		Scheduler scheduler = new Scheduler();
		Reader reader = new Reader();
		FloorSubsystem floorSubsystem = new FloorSubsystem(scheduler);
		
		try {
			reader.readInput();
		} catch (IOException e) {
			fail("no input file");
		}
		
		ArrayList<String> inputList = floorSubsystem.getData();
		scheduler.sendToScheduler(inputList, null);
		ArrayList<String> elevatorData = scheduler.recieveFromScheduler(null);
		assertEquals(inputList, elevatorData);
		
		scheduler.sendToScheduler(elevatorData, null);
		ArrayList<String> floorDataArrayList = scheduler.recieveFromScheduler(null);
		assertEquals(inputList, floorDataArrayList);
	}
}
