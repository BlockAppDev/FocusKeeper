package com.focuskeeper.testDB;

import static org.junit.Assert.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.Test;
import com.focuskeeper.DatabaseController;

public class testDBReturnFuncs {

	@Test
	public void testGetMostUsed() {
        LocalDate localDate = LocalDate.now();
        String date = DateTimeFormatter.ofPattern(DatabaseController.dateFormat).format(localDate);		DatabaseController.restartDB();
		DatabaseController.connect();
		DatabaseController.createTable();
		DatabaseController.addURLUsage(33, "www.facebook.com");
		Map<String, Integer> output = DatabaseController.getMostUsed(date, date);
        Map<String, Integer> expected = new LinkedHashMap<>();
        expected.put("www.facebook.com", 33);
		//Test equal, ignore order
        assertThat(output, is(expected));
	}
	
	@Test
	public void testGetRecentlyUsed() {
		DatabaseController.restartDB();
		DatabaseController.connect();
		DatabaseController.createTable();
		DatabaseController.addURLUsage(33, "www.facebook.com");
		Map<String, Integer> output = DatabaseController.getRecentlyUsed();
        Map<String, Integer> expected = new LinkedHashMap<>();
        expected.put("www.facebook.com", 33);
		//Test equal, ignore order
        assertThat(output, is(expected));
	}
	
}
