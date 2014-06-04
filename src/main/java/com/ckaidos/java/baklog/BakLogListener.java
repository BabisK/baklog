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
	private class TestMethod {
		private String name;
		private String description;
		private String result;
		private String path;

		public String getDescription() {
			return description;
		}

		public String getResult() {
			return result;
		}

		public String getName() {
			return name;
		}

		public String getPath() {
			return path;
		}

		public TestMethod(String name, String desc, String res, String path) {
			this.name = name;
			this.description = desc;
			this.result = res;
			this.path = path;
		}
	}

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

	//The instance of this object, it's a singleton after all
	private static BakLogListener instance;

	//
	private FileTemplateResolver templateResolver;
	private TemplateEngine templateEngine;
	
	private Context indexCtx;
	private Context currentTestMethodCtx;

	private List<TestMethod> testMethods;
	private List<TestLog> currentTestMethodLog;

	public static synchronized BakLogListener getInstance() {
		return (instance != null) ? instance : new BakLogListener();
	}

	public BakLogListener() {
		synchronized (BakLogListener.class) {
			if (instance != null) {
				throw new IllegalStateException();
			}
			instance = this;

			initializeTemplateResolver();

			initializaTemplateEngine();

			testMethods = new ArrayList<TestMethod>();
			currentTestMethodLog = new ArrayList<TestLog>();
		}
	}

	private final void initializeTemplateResolver() {
		templateResolver = new FileTemplateResolver();
		templateResolver.setTemplateMode("HTML5");
		templateResolver.setPrefix("src/main/resources/");
		templateResolver.setSuffix(".html");
		templateResolver.setCacheTTLMs(3600000L);
	}

	private final void initializaTemplateEngine() {
		templateEngine = new TemplateEngine();
		templateEngine.setTemplateResolver(templateResolver);
	}

	@Override
	public void onFinish(ITestContext testContext) {

	}

	@Override
	public void onStart(ITestContext testContext) {
		String testDirectory;
		if (System.getProperty("BakLogOutput") == null) {
			testDirectory = testContext.getOutputDirectory();
		} else {
			testDirectory = System.getProperty("BakLogOutput") + File.separator
					+ testContext.getSuite().getName() + File.separator
					+ testContext.getName();
		}

		File directory = new File(testDirectory);
		directory.mkdirs();
	}

	@Override
	public void onTestFailedButWithinSuccessPercentage(ITestResult arg0) {

	}

	@Override
	public void onTestFailure(ITestResult testResult) {
		String testMethodFile;
		if (System.getProperty("BakLogOutput") == null) {
			testMethodFile = testResult.getTestContext().getOutputDirectory()
					+ File.separator + testResult.getName() + File.separator
					+ testResult.getName() + ".html";
		} else {
			testMethodFile = System.getProperty("BakLogOutput")
					+ File.separator
					+ testResult.getTestContext().getSuite().getName()
					+ File.separator + testResult.getTestContext().getName()
					+ File.separator + testResult.getName() + File.separator
					+ testResult.getName() + ".html";
		}

		testMethods.add(new TestMethod(testResult.getName(), "desc", "skip",
				testResult.getTestContext().getName() + File.separator+ testResult.getName() + File.separator
						+ testResult.getName() + ".html"));
		currentTestMethodCtx.setVariable("endDate", testResult.getEndMillis());
		currentTestMethodCtx.setVariable("logs", currentTestMethodLog);

		try {
			File testFile = new File(testMethodFile);
			testFile.createNewFile();
			FileWriter testFw = new FileWriter(testFile);
			templateEngine.process("Test" + File.separator + "Method"
					+ File.separator + "Method", currentTestMethodCtx, testFw);
			testFw.flush();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}

	@Override
	public void onTestSkipped(ITestResult testResult) {
		String testMethodFile;
		if (System.getProperty("BakLogOutput") == null) {
			testMethodFile = testResult.getTestContext().getOutputDirectory()
					+ File.separator + testResult.getName() + File.separator
					+ testResult.getName() + ".html";
		} else {
			testMethodFile = System.getProperty("BakLogOutput")
					+ File.separator
					+ testResult.getTestContext().getSuite().getName()
					+ File.separator + testResult.getTestContext().getName()
					+ File.separator + testResult.getName() + File.separator
					+ testResult.getName() + ".html";
		}

		testMethods.add(new TestMethod(testResult.getName(), "desc", "skip",
				testResult.getTestContext().getName() + File.separator
						+ testResult.getName() + File.separator
						+ testResult.getName() + ".html"));
		currentTestMethodCtx.setVariable("endDate", testResult.getEndMillis());
		currentTestMethodCtx.setVariable("logs", currentTestMethodLog);

		try {
			File testFile = new File(testMethodFile);
			testFile.createNewFile();
			FileWriter testFw = new FileWriter(testFile);
			templateEngine.process("Test" + File.separator + "Method"
					+ File.separator + "Method", currentTestMethodCtx, testFw);
			testFw.flush();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}

	@Override
	public void onTestStart(ITestResult testResult) {
		String testMethodDirectory;
		if (System.getProperty("BakLogOutput") == null) {
			testMethodDirectory = testResult.getTestContext()
					.getOutputDirectory()
					+ File.separator
					+ testResult.getName();
		} else {
			testMethodDirectory = System.getProperty("BakLogOutput")
					+ File.separator
					+ testResult.getTestContext().getSuite().getName()
					+ File.separator + testResult.getTestContext().getName()
					+ File.separator + testResult.getName();
		}

		File methodD = new File(testMethodDirectory);
		methodD.mkdirs();

		currentTestMethodCtx = new Context();

		currentTestMethodCtx.setVariable("testName", testResult.getName());
		currentTestMethodCtx.setVariable("startDate", testResult.getStartMillis());
	}

	@Override
	public void onTestSuccess(ITestResult testResult) {
		String testMethodFile;
		if (System.getProperty("BakLogOutput") == null) {
			testMethodFile = testResult.getTestContext().getOutputDirectory()
					+ File.separator + testResult.getName() + File.separator
					+ testResult.getName() + ".html";
		} else {
			testMethodFile = System.getProperty("BakLogOutput")
					+ File.separator
					+ testResult.getTestContext().getSuite().getName()
					+ File.separator + testResult.getTestContext().getName()
					+ File.separator + testResult.getName() + File.separator
					+ testResult.getName() + ".html";
		}

		testMethods.add(new TestMethod(testResult.getName(), "desc", "success",
				testResult.getTestContext().getName() + File.separator+ testResult.getName() + File.separator
						+ testResult.getName() + ".html"));
		currentTestMethodCtx.setVariable("endDate", testResult.getEndMillis());
		currentTestMethodCtx.setVariable("logs", currentTestMethodLog);

		try {
			File testFile = new File(testMethodFile);
			testFile.createNewFile();
			FileWriter testFw = new FileWriter(testFile);
			templateEngine.process("Test" + File.separator + "Method"
					+ File.separator + "Method", currentTestMethodCtx, testFw);
			testFw.flush();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}

	@Override
	public void onFinish(ISuite suite) {
		String suiteDirectory;
		if (System.getProperty("BakLogOutput") == null) {
			suiteDirectory = suite.getOutputDirectory();
		} else {
			suiteDirectory = System.getProperty("BakLogOutput")
					+ File.separator + suite.getName();
		}

		indexCtx.setVariable("endDate", new Date().toString());
		indexCtx.setVariable("testMethods", testMethods);

		try {
			File indexFile = new File(suiteDirectory + File.separator
					+ "index.html");
			FileWriter indexFw = new FileWriter(indexFile);
			templateEngine.process("index", indexCtx, indexFw);
			indexFw.flush();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}

	/**
	 * @author Charalampos Kaidos
	 * @param suite
	 *            The suite object containing information about the suite
	 *            currently processing.
	 */
	@Override
	public void onStart(ISuite suite) {
		String suiteDirectory;
		if (System.getProperty("BakLogOutput") == null) {
			suiteDirectory = suite.getOutputDirectory();
		} else {
			suiteDirectory = System.getProperty("BakLogOutput")
					+ File.separator + suite.getName();
		}

		indexCtx = new Context();
		indexCtx.setVariable("startDate", new Date().toString());

		File directory = new File(suiteDirectory);
		directory.mkdirs();
		try {
			Files.copy(Paths.get("src/main/resources/default.css"),
					Paths.get(suiteDirectory + File.separator + "default.css"),
					StandardCopyOption.REPLACE_EXISTING);
			File indexFile = new File(suiteDirectory + File.separator
					+ "index.html");
			indexFile.createNewFile();
			FileWriter indexFw = new FileWriter(indexFile);
			templateEngine.process("index", indexCtx, indexFw);
			indexFw.flush();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}

	public void addLog(String name, String value) {
		currentTestMethodLog.add(new TestLog(name, value));
	}
}
