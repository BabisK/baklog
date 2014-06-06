package com.ckaidos.java.baklog;

import org.testng.ITestContext;
import org.testng.annotations.Test;

public class NewTest {
	@Test
	public void F2() {
		BakLogListener.getInstance().addLog("Teeeeeeest", "1764521734572154872154521863548126538451297236549712534971562");
		BakLogListener.getInstance().addLog("Teeeee333eest", "sssssssssssssss");
		BakLogListener.getInstance().addImage("My image", "/home/ckaidos/Documents/phi.png");
		
	}
	@Test
	public void f3() {

	}
	@Test
	public void f4() {
		BakLogListener.getInstance().addLog("Teeeeeeest", "1764521734572154872154521863548126538451297236549712534971562");
		assert false;
	}
	@Test
	public void f5() {

	}
	@Test
	public void f6() {

	}
	@Test
	public void f7() {

	}
}
