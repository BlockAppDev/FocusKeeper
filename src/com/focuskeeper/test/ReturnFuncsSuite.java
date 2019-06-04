package com.focuskeeper.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.focuskeeper.DatabaseController;

@RunWith(Suite.class)
@SuiteClasses({ TestDBReturnFuncs.class, TestDBUsageTimes.class })
public class ReturnFuncsSuite {
	public static void cleanUp() {
		DatabaseController.restartDB();
	}
}
