package com.focuskeeper.testDB;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ testDBEmptyUsage.class, testDBReturnFuncs.class, testDBUsageTimes.class, testEmptyDB.class })
public class TestReturnsSuite {

}
