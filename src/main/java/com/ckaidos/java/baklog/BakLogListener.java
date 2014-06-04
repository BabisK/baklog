package com.ckaidos.java.baklog;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.testng.ISuite;
import org.testng.ISuiteListener;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.FileTemplateResolver;

public class BakLogListener implements ITestListener, ISuiteListener {
	private class TestcaseResult {
		private String name;
		private String description;
		private String result;

		public String getDescription() {
			return description;
		}

		public String getResult() {
			return result;
		}

		public String getName() {
			return name;
		}

		public TestcaseResult(String name, String desc, String res) {
			this.name = name;
			this.description = desc;
			this.result = res;
		}
	}

	private class TestCase {
		private class TestLog {
			private String logName;
			private String logValue;

			public TestLog(String name, String value) {
				this.logName = name;
				this.logValue = value;
			}

			public String getName() {
				return logName;
			}

			public String getValue() {
				return logValue;
			}
		}

		private String name;
		private List<TestLog> logs = new ArrayList<TestLog>();

		public TestCase() {
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public void addLog(String name, String value) {
			logs.add(new TestLog(name, value));
		}

		public List<TestLog> getTestLog() {
			return logs;
		}
	}

	private static BakLogListener instance;

	FileTemplateResolver templateResolver;
	private static TemplateEngine templateEngine;
	Context indexCtx;
	Context testcaseCtx;

	List<TestcaseResult> testcases;
	TestCase currentTestCase;

	public static synchronized BakLogListener getInstance() {
		return (instance != null) ? instance : new BakLogListener();
	}

	public BakLogListener() {
		synchronized (BakLogListener.class) {
			if (instance != null) {
				throw new IllegalStateException();
			}
			instance = this;
		}
	}

	@Override
	public void onFinish(ITestContext context) {

	}

	@Override
	public void onStart(ITestContext arg0) {
		String testDirectory = arg0.getOutputDirectory() + File.separator + arg0.getName();
		File testDir = new File(testDirectory);
		testDir.mkdirs();
	}

	@Override
	public void onTestFailedButWithinSuccessPercentage(ITestResult arg0) {

	}

	@Override
	public void onTestFailure(ITestResult arg0) {
		testcases.add(new TestcaseResult(arg0.getMethod().getMethodName(),
				"desc", "failure"));

	}

	@Override
	public void onTestSkipped(ITestResult arg0) {
		testcases.add(new TestcaseResult(arg0.getMethod().getMethodName(),
				"desc", "skip"));

	}

	@Override
	public void onTestStart(ITestResult arg0) {
		testcaseCtx = new Context();

		currentTestCase = new TestCase();
		currentTestCase.setName(arg0.getName());
		testcaseCtx.setVariable("testName", arg0.getName());
		testcaseCtx.setVariable("startDate", arg0.getStartMillis());
	}

	@Override
	public void onTestSuccess(ITestResult arg0) {
		testcases.add(new TestcaseResult(arg0.getMethod().getMethodName(),
				"desc", "success"));
		testcaseCtx.setVariable("endDate", arg0.getEndMillis());
		testcaseCtx.setVariable("logs", currentTestCase.getTestLog());

		printTemplateToFile(templateEngine, "TestCase", testcaseCtx,
				arg0.getName() + ".html", arg0.getTestContext()
						.getOutputDirectory());
	}

	@Override
	public void onFinish(ISuite arg0) {
		indexCtx.setVariable("endDate", new Date().toString());
		indexCtx.setVariable("testcases", testcases);

		printTemplateToFile(templateEngine, "index", indexCtx, "index.html",
				arg0.getOutputDirectory());
	}

	@Override
	public void onStart(ISuite arg0) {
		testcases = new ArrayList<TestcaseResult>();
		templateResolver = new FileTemplateResolver();
		templateResolver.setTemplateMode("HTML5");
		templateResolver.setPrefix("src/main/resources/");
		templateResolver.setSuffix(".html");
		templateResolver.setCacheTTLMs(3600000L);
		templateEngine = new TemplateEngine();
		templateEngine.setTemplateResolver(templateResolver);

		indexCtx = new Context();
		indexCtx.setVariable("startDate", new Date().toString());

		printTemplateToFile(templateEngine, "index", indexCtx, "index.html",
				arg0.getOutputDirectory());
	}

	private void printTemplateToFile(TemplateEngine te, String template,
			Context ctx, String filename, String path) {
		File directory = new File(path);
		directory.mkdirs();
		try {
			Files.copy(Paths.get("src/main/resources/default.css"),
					Paths.get(path + File.separator + "default.css"),
					StandardCopyOption.REPLACE_EXISTING);
			File indexFile = new File(path + File.separator + filename);
			indexFile.createNewFile();
			FileWriter indexFw = new FileWriter(indexFile);
			te.process(template, ctx, indexFw);
			indexFw.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void addLog(String name, String value){
		currentTestCase.addLog(name, value);
	}
}
