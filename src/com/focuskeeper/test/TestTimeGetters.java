package com.focuskeeper.test;

import static com.focuskeeper.DatabaseController.getCon;
import static org.junit.Assert.assertEquals;

import java.util.Calendar;
import java.util.Set;

import org.junit.Test;

import com.focuskeeper.DatabaseController;
import com.focuskeeper.FocusController;

import java.time.LocalTime;
import java.time.ZoneId;

public class TestTimeGetters {

	@Test
	public void testGetWeekday() {
		
		FocusController.getWeekday();
		Calendar calendar = Calendar.getInstance();
		int day = calendar.get(Calendar.DAY_OF_WEEK);
		
		day = day - 2;
		if (day == -1) day = 6;
		
		assertEquals(day, FocusController.getWeekday());
	}
	
	@Test
	public void testMinutesSinceDayStart() {
		
		LocalTime now = LocalTime.now(ZoneId.systemDefault());
        long minutes = now.toSecondOfDay() / 60;
		
		assertEquals(minutes, FocusController.minutesSinceDayStart());
	}
}
