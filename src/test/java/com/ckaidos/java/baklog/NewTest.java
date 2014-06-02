package com.ckaidos.java.baklog;

import java.lang.reflect.Method;

import org.testng.ITestContext;
import org.testng.SuiteRunner;
import org.testng.TestNG;
import org.testng.TestNGUtils;
import org.testng.annotations.Test;
import org.testng.internal.TestNGProperty;

public class NewTest {
  @Test
  public void f(ITestContext m) {
	  //m.get
	  TestNG.getDefault().getSuiteListeners().get(0).onStart(null);
	  
  }
}
