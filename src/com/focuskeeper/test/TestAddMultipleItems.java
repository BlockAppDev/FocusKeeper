package com.focuskeeper.test;

import org.junit.Test;
import com.focuskeeper.DatabaseController;
import static org.junit.Assert.*;

public class TestAddMultipleItems {
	static final String URL = "www.facebook.com";
	static final String URL_2 = "www.instagram.com";
	
	public void setUp() {
		DatabaseController.connect();
	}
	
	@Test
	public void testGetMostUsed() {
		DatabaseController.restartDB();
		setUp();
		int output = DatabaseController.addItem(URL);
		assertEquals(1, output, 0);
	}
	
	@Test
	public void testGetMostUsed2() {
		//did not restart the database!
		setUp();
		int output = DatabaseController.addItem(URL_2);
		assertEquals(2, output, 0);
	}
	
}
