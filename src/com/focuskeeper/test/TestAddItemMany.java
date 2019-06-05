package com.focuskeeper.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.focuskeeper.DatabaseController;

public class TestAddItemMany {

	@Test
	public void testGetMostUsed3() {
        DatabaseController.restartDB();
		DatabaseController.connect();
		int output = 0;
		for (int i = 0; i < 1000; i++) {
			output = DatabaseController.addItem("www." + new Integer(i).toString() + ".com");
		}
		assertEquals(1000, output, 0);
	}
	
	@Test
	public void testGetMostUsed4() {
		//restart the database!
        DatabaseController.restartDB();
		DatabaseController.connect();
		int output = 0;
		for (int i = 0; i < 1000; i++) {
			output = DatabaseController.addItem("www." + new Integer(i).toString() + ".com");
		}
		assertEquals(1000, output, 0);
	}
	
}
