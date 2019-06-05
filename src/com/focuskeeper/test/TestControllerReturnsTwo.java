package com.focuskeeper.test;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

import com.focuskeeper.FocusController;

public class TestControllerReturnsTwo {

	@Test
	public void testCheckDistracting() {
		FocusController a = new FocusController();	
		Boolean b = a.checkDistracting("www.fake.com");
		assertEquals(true, b);
	}
	
	@Test
	public void testMinutesSinceDayStart() {
		FocusController a = new FocusController();	
		String b = a.checkListsToBlock().toString();
		assertEquals("[]", b);
	}
}
