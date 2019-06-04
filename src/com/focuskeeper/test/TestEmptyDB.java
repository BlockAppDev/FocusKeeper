package com.focuskeeper.test;

import static org.junit.Assert.*;
import java.util.Map;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.Test;
import com.focuskeeper.DatabaseController;

public class TestEmptyDB {
	@Test
	public void testEmptyMostUsed() {        
		DatabaseController.restartDB();
		DatabaseController.connect();
		DatabaseController.createTable();
		Map<String, Integer> output = DatabaseController.getMostUsed("2019/05/31", "2019/05/31");
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
