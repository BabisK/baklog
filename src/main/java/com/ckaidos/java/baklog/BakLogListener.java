package com.ckaidos.java.baklog;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;

import org.testng.ISuite;
import org.testng.ISuiteListener;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class BakLogListener implements ITestListener, ISuiteListener {

	@Override
	public void onFinish(ITestContext arg0) {
		// TODO Auto-generated method stub

	}

	@SuppressWarnings("unused")
	@Override
	public void onStart(ITestContext arg0) {
		String s = arg0.getOutputDirectory() + File.separator + arg0.getName();
		File f = new File(s + File.separator + "index2.html");
		try {
			f.mkdirs();
			f.createNewFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// TODO Auto-generated method stub

	}

	@Override
	public void onTestFailedButWithinSuccessPercentage(ITestResult arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTestFailure(ITestResult arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTestSkipped(ITestResult arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTestStart(ITestResult arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTestSuccess(ITestResult arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onFinish(ISuite arg0) {
		String suitName = arg0.getOutputDirectory();
		File indexFile = new File(suitName + File.separator + "index.html");
		FileWriter fw;
		try {
			fw = new FileWriter(indexFile, true);
			String html = new String("<h3>End: " + new Date().toString() + "</body>" +
					"</html>");
			fw.write(html);
			fw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onStart(ISuite arg0) {
		String suitName = arg0.getOutputDirectory();
		File directory = new File(suitName);
		directory.mkdirs();
		File indexFile = new File(suitName + File.separator + "index.html");
		FileWriter fw;
		try {
			indexFile.createNewFile();
			Files.copy(Paths.get("src/main/resources/default.css"), Paths.get(suitName + File.separator + "default.css"));
			fw = new FileWriter(indexFile);
			String html = new String(
					"<!DOCTYPE html>" +
					"<html>" +
					"<head>" +
					"<meta charset=\"UTF-8\">" +
					"<title>TestNG Results</title>" +
					"<link rel=\"stylesheet\" type=\"text/css\" href=\"default.css\">" +
					"</head>" +
					"<body>" +
					"<header>" +
					"<div>" +
					"<h1>TestNG Results</h1>" +
					"</div>" +
					"<div>" +
					"<h3>Start: " + new Date().toString() + "</h3>" +
					"</div>" +
					"</header>");
			fw.write(html);
			fw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
