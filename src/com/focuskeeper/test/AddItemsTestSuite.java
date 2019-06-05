package com.focuskeeper.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ TestAddItem.class, TestAddMultipleItems.class, TestEmptyDB.class, TestLoop1.class, TestLoop2.class })
public class AddItemsTestSuite {

}
