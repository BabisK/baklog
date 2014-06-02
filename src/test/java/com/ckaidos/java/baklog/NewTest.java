package com.ckaidos.java.baklog;

import org.testng.ITestContext;
import org.testng.TestNG;
import org.testng.annotations.Test;

public class NewTest {
	@SuppressWarnings("deprecation")
	@Test
	public void f(ITestContext m) {
		// m.get
		TestNG.getDefault().getSuiteListeners().get(0).onStart(null);

	}
}
