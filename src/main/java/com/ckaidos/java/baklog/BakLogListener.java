package com.ckaidos.java.baklog;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

import org.testng.ISuite;
import org.testng.ISuiteListener;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.FileTemplateResolver;

public class BakLogListener implements ITestListener, ISuiteListener {

	@Override
	public void onFinish(ITestContext context) {
		// TODO Auto-generated method stub
		
	}
	
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

	}

	private static TemplateEngine templateEngine;
	
	@Override
	public void onStart(ISuite arg0) {
		FileTemplateResolver templateResolver = new FileTemplateResolver();
        // XHTML is the default mode, but we will set it anyway for better understanding of code
        templateResolver.setTemplateMode("HTML5");
        // This will convert "home" to "/WEB-INF/templates/home.html"
        templateResolver.setPrefix("src/main/resources/");
        templateResolver.setSuffix(".html");
        // Set template cache TTL to 1 hour. If not set, entries would live in cache until expelled by LRU
        templateResolver.setCacheTTLMs(3600000L);
        
        templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);
        
        Context ctx = new Context();
        ctx.setVariable("startDate", new Date().toString());
		String suitName = arg0.getOutputDirectory();
		File directory = new File(suitName);
		directory.mkdirs();
		File indexFile = new File(suitName + File.separator + "index.html");
		FileWriter fw;
        try {
			indexFile.createNewFile();
			fw = new FileWriter(indexFile);
			templateEngine.process("index", ctx, fw);
			fw.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
