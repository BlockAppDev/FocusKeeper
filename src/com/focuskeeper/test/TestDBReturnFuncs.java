package com.focuskeeper.test;

import static org.junit.Assert.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.Test;
import com.focuskeeper.DatabaseController;

public class TestDBReturnFuncs {
	static final String URL = "www.facebook.com";

	@Test
	public void testGetMostUsed() {
        LocalDate localDate = LocalDate.now();
        String date = DateTimeFormatter.ofPattern(DatabaseController.DATE_FORMAT).format(localDate);		
        DatabaseController.restartDB();
		DatabaseController.connect();
		DatabaseController.createTable();
		DatabaseController.addURLUsage(33, URL);
		Map<String, Integer> output = DatabaseController.getMostUsed(date, date);
        Map<String, Integer> expected = new LinkedHashMap<>();
        expected.put(URL, 33);
		//Test equal, ignore order
        assertThat(output, is(expected));
	}
	
	@Test
	public void testGetRecentlyUsed() {
		DatabaseController.restartDB();
		DatabaseController.connect();
		DatabaseController.createTable();
		DatabaseController.addURLUsage(33, URL);
		Map<String, Integer> output = DatabaseController.getRecentlyUsed();
        Map<String, Integer> expected = new LinkedHashMap<>();
        expected.put(URL, 33);
		//Test equal, ignore order
        assertThat(output, is(expected));
	}
	
}
