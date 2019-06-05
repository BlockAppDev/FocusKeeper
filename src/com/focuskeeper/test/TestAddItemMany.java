package com.focuskeeper.test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Test;

import com.focuskeeper.DatabaseController;

public class TestAddItemMany {

	public void setUp() {
        DatabaseController.restartDB();
		DatabaseController.connect();
	}
	
	@Test
	public void testAddItems() {
		setUp();
		int output = 0;
		for (int i = 0; i < 1000; i++) {
			output = DatabaseController.addItem("www." + Integer.toString(i) + ".com");
		}
		assertEquals(1000, output, 0);
	}
	
	@Test
	public void testManyMoreItems() {
		//restart the database!
        setUp();
		String url = "";
		for (int i = 0; i < 1000; i++) {
			url = "www." + Integer.toString(i) + ".com";
			DatabaseController.addItem(url);
		}
		DatabaseController.addURLUsage(1000, url);
		Map<String, Integer> output = DatabaseController.getRecentlyUsed();
        Map<String, Integer> expected = new LinkedHashMap<>();
        expected.put(url, 1000);
		//Test equal, ignore order
        assertThat(output, is(expected));
	}
	
}
