package com.focuskeeper.test;

import static org.junit.Assert.*;
import org.junit.Test;
import com.focuskeeper.DatabaseController;

public class TestDBEmptyUsage {

	@Test
	public void testEmptyTotalTime() {
		DatabaseController.connect();
		DatabaseController.createTable();
		int output = DatabaseController.getTotalTimeToday();
        //Test size is 0
		assertEquals(output, 0, 0);
	}
	
	@Test
	public void testEmptyTotalFocusTime() {
		DatabaseController.restartDB();
		DatabaseController.connect();
		DatabaseController.createTable();
		int output = DatabaseController.getTotalFocusTimeToday();
        //Test size is 0
		assertEquals(output, 0, 0);
	}
}
