package com.focuskeeper.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.focuskeeper.DatabaseController;

@RunWith(Suite.class)
@SuiteClasses({ TestDBNoReturns.class, TestDBSetup.class })
public class NoReturnFuncsSuiteDB {
}