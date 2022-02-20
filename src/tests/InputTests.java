package tests;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.ArrayList;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import floorSubsystem.*;
import scheduler.*;
import input.*;

class InputTests {
	
	/**
	 * This tests if the input file was read in correctly in the FloorSubsystem class
	 */
	@Test
	void readInputTest() {
		
		InputBuffer buf = new InputBuffer();
		Reader reader = new Reader(buf);
		
		try {
			reader.readInput();
		} catch (IOException e) {
			fail("no input file");
		}
		
		
		ArrayList<String> inputList = buf.recieveFromInputBuffer();
		assertEquals(inputList.get(0), "14:05:56.0");
		assertEquals(inputList.get(1), "4");
		assertEquals(inputList.get(2), "Down");
		assertEquals(inputList.get(3), "2");
		
		
	}

	/**
	 *This tests the communication between floor and scheduler
	 */
	@Test
	void floorToSchedulerTest() {
		
		Scheduler scheduler = new Scheduler();
		InputBuffer buf = new InputBuffer();
		Reader reader = new Reader(buf);
		FloorSubsystem floorSubsystem = new FloorSubsystem(scheduler, buf, 4);
		
		try {
			reader.readInput();
		} catch (IOException e) {
			fail("no input file");
		}
		


		floorSubsystem.setInputData(buf.recieveFromInputBuffer());
		scheduler.sendToScheduler(floorSubsystem.parseInputToRequestObject(), "floor");
		assertEquals(floorSubsystem.getDataSchedulerRequest(), scheduler.getBuffer().peek());


	}
	
	/**
	 * This tests the communication between scheduler, floor, and elevator 
	 */
	@Test
	void floorToElevatorTest() {
		InputBuffer buf = new InputBuffer();
		Scheduler scheduler = new Scheduler();
		Reader reader = new Reader(buf);
		FloorSubsystem floorSubsystem = new FloorSubsystem(scheduler, buf, 4);
		
		try {
			reader.readInput();
		} catch (IOException e) {
			fail("no input file");
		}
		


		floorSubsystem.setInputData(buf.recieveFromInputBuffer());
		scheduler.sendToScheduler(floorSubsystem.parseInputToRequestObject(), "floor");
		SchedulerRequest elevatorData = scheduler.recieveFromScheduler("elevator");
		assertEquals(floorSubsystem.getDataSchedulerRequest(),elevatorData);
		
	}
	
}
