package com.focuskeeper.testDB;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import com.focuskeeper.DatabaseController;

public class testDBUsageTimes {
	
	@Test
	public void testGetTotalTimeToday() {
		DatabaseController.restartDB();
		DatabaseController.connect();
		DatabaseController.createTable();
		DatabaseController.addURLUsage(33, "www.facebook.com");
		int output = DatabaseController.getTotalTimeToday();
		assertEquals(33, output, 0);
	}
	
	@Test
	public void testGetTotalFocusTimeToday() {
		DatabaseController.restartDB();
		DatabaseController.connect();
		DatabaseController.createTable();
		DatabaseController.addURLUsage(33, "www.facebook.com");
		int output = DatabaseController.getTotalFocusTimeToday();
		assertEquals(0, output, 0);	
	}
}
