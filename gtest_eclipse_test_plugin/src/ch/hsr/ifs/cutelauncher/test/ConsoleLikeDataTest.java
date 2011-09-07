package ch.hsr.ifs.cutelauncher.test;

import gtest.eclipse.plugin.GTestConsoleEventParser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.text.IRegion;

import ch.hsr.ifs.cutelauncher.ConsolePatternListener;
import ch.hsr.ifs.cutelauncher.event.ConsoleEventParser;
import ch.hsr.ifs.cutelauncher.event.CuteConsoleEventParser;
import ch.hsr.ifs.cutelauncher.event.TestEvent;
import ch.hsr.ifs.cutelauncher.event.TestEventHandler;
import ch.hsr.ifs.cutelauncher.test.internal.console.FileInputTextConsole;

public abstract class ConsoleLikeDataTest extends TestCase {
	private ConsoleEventParser consoleEventParser;
	protected String filePathRoot;

	protected FileInputTextConsole tc;
	protected ConsolePatternListener cpl;

	@Override
	protected void setUp() throws Exception {
//		 useCUTE();
		useGTest();
		runTheTest();
	}

	private void runTheTest() throws Exception {
		IRegion reg = null;
		BufferedReader consoleLineReader = consoleLineReader();
		String line;
		while ((line = consoleLineReader.readLine()) != null){
			processTestEventsFrom(reg, line);
		}
	}
	
	private void processTestEventsFrom(IRegion reg, String line) {
		List<TestEvent> testEvents = consoleEventParser.eventsFrom(reg, line);
		for (TestEvent testEvent : testEvents) {
			process(testEvent);
		}
	}

	private void process(TestEvent testEvent) {
		handler().handle(testEvent);
	}
	
	protected abstract TestHandler handler(); 

	@Override
	protected void tearDown() throws Exception {
	}

	private void useCUTE() {
		consoleEventParser = new CuteConsoleEventParser();
		filePathRoot = "testDefs/cute/";
	}

	private void useGTest() {
		consoleEventParser = new GTestConsoleEventParser();
		filePathRoot = "testDefs/gtest/";
	}

	protected abstract String getInputFilePath();

	private BufferedReader consoleLineReader() throws CoreException {
		BufferedReader br;
		try {
			File file = new File(fullFilePath());
			br = new BufferedReader(new FileReader(file));
		} catch (IOException e) {
			throw new CoreException(new Status(IStatus.ERROR,
					TestPlugin.PLUGIN_ID, 0, e.getMessage(), e));
		}
		return br;
	}

	private String fullFilePath() {
		return filePathRoot + getInputFilePath();
	}

	protected abstract class TestHandler extends TestEventHandler {

		@Override
		protected void handleSuiteBeginning(IRegion reg, String suitename,
				String suitesize) {
			// Do nothing
		}

		@Override
		protected void handleSuiteEnding(IRegion reg, String suitename) {
			// Do nothing
		}

		@Override
		protected void handleError(IRegion reg, String testName, String msg) {
			// Do nothing
		}

		@Override
		protected void handleFailure(IRegion reg, String testName, String fileName, String lineNo, String reason) {
			// Do nothing
		}

		@Override
		public void handleSessionEnd() {
			// Do nothing
		}

		@Override
		public void handleSessionStart() {
			// Do nothing
		}

		@Override
		protected void handleSuccess(IRegion reg, String testname, String msg) {
			// Do nothing
		}

		@Override
		protected void handleTestStart(IRegion reg, String testname) {
			// Do nothing
		}

	}
}