package com.focuskeeper.test;

import static org.junit.Assert.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.Test;
import com.focuskeeper.DatabaseController;

public class TestEmptyDB {
	@Test
	public void testEmptyMostUsed() {  
        LocalDate localDate = LocalDate.now();
        String date = DateTimeFormatter.ofPattern(DatabaseController.DATE_FORMAT).format(localDate);
		DatabaseController.restartDB();
		DatabaseController.connect();
		DatabaseController.createTable();
		Map<String, Integer> output = DatabaseController.getMostUsed(date, date);
        //Test size is 0
        assertThat(output.size(), is(0));
	}
	
	@Test
	public void testEmptyRecentlyUsed() {
        DatabaseController.restartDB();
        DatabaseController.connect();
		DatabaseController.createTable();
		Map<String, Integer> output = DatabaseController.getRecentlyUsed();
        //Test size is 0
		assertThat(output.size(), is(0));
	}
	
	
}
