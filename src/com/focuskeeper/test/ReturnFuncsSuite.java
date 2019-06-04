package com.focuskeeper.test;

import org.junit.runner.RunWith;
import com.focuskeeper.DatabaseController;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ TestDBEmptyUsage.class, TestDBReturnFuncs.class, TestDBUsageTimes.class, TestEmptyDB.class })
public class ReturnFuncsSuite {
	public static void cleanUp() {
		DatabaseController.restartDB();
	}
}
