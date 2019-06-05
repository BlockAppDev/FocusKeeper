package com.focuskeeper.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ TestDBReturnFuncs.class, TestDBUsageTimes.class })
public class ReturnFuncsSuite {
}
