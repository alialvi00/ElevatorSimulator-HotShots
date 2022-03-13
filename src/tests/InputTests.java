package tests;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import input.*;

public class InputTests {
	
	/**
	 * This tests if the input file was read in correctly in the FloorSubsystem class
	 */
	@Test
	public void readInputTest() {
		
		InputBuffer buf = new InputBuffer();
		Reader reader = new Reader(buf);
		
		try {
			reader.readInput();
		} catch (IOException e) {
			fail("no input file");
		}

		ArrayList<String> inputList = buf.recieveFromInputBuffer();
		assertEquals(inputList.get(0), "14:05:56.0");
		assertEquals(inputList.get(1), "1");
		assertEquals(inputList.get(2), "Up");
		assertEquals(inputList.get(3), "4");
		
	}

	
}
