package com.focuskeeper.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ TestDBEmptyUsage.class, TestEmptyDB.class })
public class EmptyDBTestSuite {

}
