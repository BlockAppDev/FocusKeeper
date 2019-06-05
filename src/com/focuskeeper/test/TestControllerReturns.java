package com.focuskeeper.test;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Test;

import com.focuskeeper.FocusController;

public class TestControllerReturns {

	@Test
	public void testGetAllBlockItems() {
		
		FocusController a = new FocusController();	
		Set<String> b = a.getAllBlockItems();
		Set<String> c = new TreeSet<String>();
		
		assertEquals(c, b);
	}
	
	@Test
	public void testCheckDistracting() {
		
		FocusController a = new FocusController();
		List<String> b = new ArrayList<String>();
		Map<String, Boolean> c = a.checkDistracting(b);
		Map<String, Boolean> d = new HashMap<String, Boolean>();
		
		assertEquals(c,d);
	}
	
	
}
