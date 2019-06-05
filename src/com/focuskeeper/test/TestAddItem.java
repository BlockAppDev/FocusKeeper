package com.focuskeeper.test;

import org.junit.Test;
import com.focuskeeper.DatabaseController;
import static org.junit.Assert.*;

public class TestAddItem {
	static final String URL = "www.facebook.com";
	static final String URL_2 = "www.instagram.com";
	
	public void setUp() {
		DatabaseController.restartDB();
		DatabaseController.connect();
	}
	@Test
	public void testGetMostUsed() {
        setUp();
		int output = DatabaseController.addItem(URL);
		assertEquals(1, output, 0);
	}
	@Test
	public void testGetMostUsed2() {
        setUp();
		int output = DatabaseController.addItem(URL_2);
		assertEquals(1, output, 0);
	}
	
}
