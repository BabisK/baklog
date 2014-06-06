package com.ckaidos.java.baklog;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

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

		// Getters used by template engine
		@SuppressWarnings("unused")
		public String getDescription() {
			return description;
		}

		@SuppressWarnings("unused")
		public String getResult() {
			return result;
		}

		@SuppressWarnings("unused")
		public String getName() {
			return name;
		}

		@SuppressWarnings("unused")
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
		private String logType;

		public TestLog(String name, String value, String type) {
			this.logName = name;
			this.logValue = value;
			this.logType = type;
		}
		
		// Getters used by template engine
		@SuppressWarnings("unused")
		public String getName() {
			return logName;
		}

		@SuppressWarnings("unused")
		public String getValue() {
			return logValue;
		}
		
		@SuppressWarnings("unused")
		public String getType(){
			return logType;
		}
	}

	// The instance of this object, it's a singleton after all
	private static BakLogListener instance;

	// Thymeleaf objects
	private FileTemplateResolver templateResolver;
	private TemplateEngine templateEngine;
	// Context for the index file
	private Context indexCtx;
	// Context for the test method files
	private Context currentTestMethodCtx;

	// The test methods run in this suite
	private List<TestMethod> testMethods;
	// The logs of the current method
	private List<TestLog> currentTestMethodLog;

	private String rootDirectory;
	private String indexFile;
	private String currentTestDirectory;
	private String currentTestMethodDirectory;
	private String currentTestMethodFile;

	private String templateCssFile;
	private String templateIndexFile;
	private String templateTestFile;

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
			templateCssFile = "src/main/resources/default.css";
			templateIndexFile = "src/main/resources/index.html";
			templateTestFile = "src/main/resources/Test/Method/Method.html";
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
		currentTestDirectory = rootDirectory + File.separator
				+ testContext.getName();

		File directory = new File(currentTestDirectory);
		if (!directory.mkdirs()) {
			System.err
					.println(currentTestDirectory
							+ " could not be created. Maybe it already exists? Contents will be overwritten.");
		}
	}

	@Override
	public void onTestFailedButWithinSuccessPercentage(ITestResult arg0) {

	}

	@Override
	public void onTestFailure(ITestResult testResult) {
		testMethods.add(new TestMethod(testResult.getName(), "desc", "skip",
				currentTestMethodFile));
		currentTestMethodCtx.setVariable("endDate", testResult.getEndMillis());
		currentTestMethodCtx.setVariable("logs", currentTestMethodLog);

		try {
			File testFile = new File(currentTestMethodFile);
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
		testMethods.add(new TestMethod(testResult.getName(), "desc", "skip",
				currentTestMethodFile));
		currentTestMethodCtx.setVariable("endDate", testResult.getEndMillis());
		currentTestMethodCtx.setVariable("logs", currentTestMethodLog);

		try {
			FileWriter testFw = new FileWriter(currentTestMethodFile);
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
		// Initialize the log for this method
		currentTestMethodLog = new ArrayList<TestLog>();

		// Create the directory for this test method
		currentTestMethodDirectory = currentTestDirectory + File.separator
				+ testResult.getName();
		File methodD = new File(currentTestMethodDirectory);
		if (!methodD.mkdirs()) {
			System.err
					.println(currentTestMethodDirectory
							+ " could not be created. Maybe it already exists? Contents will be overwritten.");
		}

		// Get the html file name
		currentTestMethodFile = currentTestMethodDirectory + File.separator
				+ testResult.getName() + ".html";

		// Create test method context
		currentTestMethodCtx = new Context();

		currentTestMethodCtx.setVariable("testName", testResult.getName());
		currentTestMethodCtx.setVariable("startDate",
				testResult.getStartMillis());
	}

	@Override
	public void onTestSuccess(ITestResult testResult) {
		testMethods.add(new TestMethod(testResult.getName(), "desc", "success",
				currentTestMethodFile));
		currentTestMethodCtx.setVariable("endDate", testResult.getEndMillis());
		currentTestMethodCtx.setVariable("logs", currentTestMethodLog);

		try {
			FileWriter testFw = new FileWriter(currentTestMethodFile);
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
		// Add data to the context
		indexCtx.setVariable("endDate", new Date().toString());
		indexCtx.setVariable("testMethods", testMethods);

		// Write the index file
		try {
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
		// Initialize listo of methods for this suite
		testMethods = new ArrayList<TestMethod>();
		// Initialize the index file context
		indexCtx = new Context();

		// Get the root directory for the site
		if (System.getProperty("BakLogOutput") == null) {
			System.err
					.println("BakLogOutput parameter not defined in JVM. Using the default output directory "
							+ suite.getOutputDirectory() + ".");
			rootDirectory = suite.getOutputDirectory() + File.separator;
		} else {
			rootDirectory = System.getProperty("BakLogOutput") + File.separator
					+ suite.getName();
		}

		// Create the root directory if it does not exist
		File directory = new File(rootDirectory);
		if (!directory.mkdirs()) {
			System.err
					.println(rootDirectory
							+ " could not be created. Maybe it already exists? Contents will be overwritten.");
		}

		// Get the index file
		indexFile = rootDirectory + File.separator + "index.html";

		// Put the css in place
		try {
			Files.copy(Paths.get(templateCssFile),
					Paths.get(rootDirectory + File.separator + "default.css"),
					StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e1) {
			System.err.println("Could not copy default.css to " + rootDirectory
					+ ". The site will mulfunction");
			e1.printStackTrace();
		}

		// Add some data to the context
		indexCtx.setVariable("startDate", new Date().toString());
	}

	public void addLog(String name, String value) {
		currentTestMethodLog.add(new TestLog(name, value, "text"));
	}
	
	public void addImage(String description, String image_path){
		String imagePath = currentTestMethodDirectory + File.separator + "images";
		File imageDirectory = new File(imagePath);
		imageDirectory.mkdirs();
		String imageName = description.replaceAll(" ", "") + currentTestMethodLog.size() + image_path.substring(image_path.lastIndexOf('.'));
		try {
			Files.copy(Paths.get(image_path), Paths.get(imageDirectory + File.separator + imageName), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			e.printStackTrace();
		}
		currentTestMethodLog.add(new TestLog(description, "images" + File.separator + imageName, "image"));
	}
}
