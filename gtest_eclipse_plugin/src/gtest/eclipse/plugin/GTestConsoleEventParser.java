/*******************************************************************************
 * Copyright (c) 2008, Industrial Logic, Inc. All Rights Reserved. 
 * 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 * 
 * Contributors: 
 * Industrial Logic, Inc.:  Mike Bria & John Tangney - initial implementation (based on ideas originating from the work of Emanuel Graff)
 ******************************************************************************/
package gtest.eclipse.plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.text.IRegion;

import ch.hsr.ifs.cutelauncher.CuteLauncherPlugin;
import ch.hsr.ifs.cutelauncher.event.ConsoleEventParser;
import ch.hsr.ifs.cutelauncher.event.NonEvent;
import ch.hsr.ifs.cutelauncher.event.SessionEndEvent;
import ch.hsr.ifs.cutelauncher.event.SuiteBeginEvent;
import ch.hsr.ifs.cutelauncher.event.SuiteEndEvent;
import ch.hsr.ifs.cutelauncher.event.TestErrorEvent;
import ch.hsr.ifs.cutelauncher.event.TestFailureEvent;
import ch.hsr.ifs.cutelauncher.event.TestStartEvent;
import ch.hsr.ifs.cutelauncher.event.TestSuccessEvent;

public class GTestConsoleEventParser extends ConsoleEventParser {
	private static final String LINE_QUALIFIER = "";
	private static final int LINEPREFIXLENGTH = LINE_QUALIFIER.length();
	private static final String BEGINNING = "[----------]";
	private static final String FAILURE = "[  FAILED  ]";
	private static final String SUCCESS = "[       OK ]";
	private static final String ERROR_backwardCompatible = "exception of unknown type";
	private static final String STARTTEST = "[ RUN      ]";

	private static final String SESSIONSTART = "[==========] Running";
	private static final String SESSIONEND = "[==========]";

	private static final String EXPECTED_VALUE_LABEL = "Expected: ";
	private static final String WHICH_IS_EXPECTED_VALUE_LABEL = "Which is: ";	
	private static final String VALUE_OF_LABEL = "Value of: ";

	private static final Pattern TYPED_TEST_SUITE_PATTERN = Pattern.compile("(.*)/(\\d+), where TypeParam =(.*)");
	private static final Pattern SUITEBEGINNINGLINE = suitePatternWithTailOf("(.*)$");

	private static final Pattern suitePatternWithTailOf(String tail) {
		return Pattern.compile(escapeForRegex(LINE_QUALIFIER + BEGINNING)	+ " (\\d+) tests? from " + tail);
	}

	private String currentSuiteName;
	private String currentTestName;
	
	private RunningTestOutput runningTestOutput = new RunningTestOutput();
	private SuiteBeginEvent pendingSuiteBeginEvent;

	public String getLineQualifier() {
		return escapeForRegex(LINE_QUALIFIER);
	}

	public final String getComprehensiveLinePattern() {
		return LINE_QUALIFIER + "(.*)(.*)(\\n)";
	}

	protected void extractTestEventsFor(IRegion reg, String line) throws CoreException {
		consoleShattingDebug(line);
		if (testStarting(line))
			testStarted(reg, line);
		else if (testInProgress()) {
			runningTestOutput.add(line);
			testMayHaveEnded(reg, line);
		} else if (suiteEnding(line))
			suiteEnded(reg, line);
		else if (suiteStarting(line))
			suiteStarted(reg, line);
		else if (sessionEnding(line))
			sessionEnded(reg);
		else if (collectingSuiteName())
			updateSuiteNameWith(line);
	}

	private boolean collectingSuiteName() {
		return pendingSuiteBeginEvent != null;
	}
	
	private void updateSuiteNameWith(String line) {
		pendingSuiteBeginEvent.appendToSuiteName(line);
		currentSuiteName = pendingSuiteBeginEvent.suiteName;
	}

	private boolean testStarting(String line) {
		return line.startsWith(STARTTEST, LINEPREFIXLENGTH);
	}

	private void testStarted(IRegion reg, String line) throws CoreException {
		postSuiteBeginEventIfNecesary();
		
		String fullestPossiblePrefix = LINE_QUALIFIER + STARTTEST + " " + currentSuiteName;
		int indexOfTestName = (chomp(fullestPossiblePrefix, line)).length()+1;
		currentTestName = line.substring(indexOfTestName);

		TestStartEvent event = new TestStartEvent(reg, currentTestName);
		testEvents.add(event);
	}

	private void postSuiteBeginEventIfNecesary() {
		if (!collectingSuiteName())
			return;
		testEvents.add(pendingSuiteBeginEvent);
		pendingSuiteBeginEvent = null;
	}

	private boolean testInProgress() {
		return currentTestName != null;
	}

	private void testMayHaveEnded(IRegion reg, String line)	throws CoreException {
		if (testSucceeded(line))
			testEvents.add(new TestSuccessEvent(reg, currentTestName, "OK"));
		else if (testFailed(line))
			testFailure(reg);
		else if (testErrored(line))
			testError(reg);
		else {
			testEvents.add(new NonEvent());
			return;
		}

		currentTestName = null;
		runningTestOutput = new RunningTestOutput();
	}

	private void testError(IRegion reg) {
		Pattern exceptionPattern = Pattern.compile("Exception thrown (.*).");
		Matcher matcher = exceptionPattern.matcher(runningTestOutput.lineAt(1));
		String message = ERROR_backwardCompatible;
		if (matcher.matches())
			message = matcher.group(1);
		testEvents.add(new TestErrorEvent(reg, currentTestName, message));
	}

	private void consoleShattingDebug(String line) {
		testlogDetails(line);
	}

	private void testlogDetails(String s){
		CuteLauncherPlugin plugin = CuteLauncherPlugin.getDefault();
		if (plugin == null) 
			return;
		
		ILog log = plugin.getLog();
		System.out.println("!String->" + s);
		log.log(new org.eclipse.core.runtime.Status(Status.ERROR, "RGB String", s));
	}
	
	private boolean testSucceeded(String line) {
		return line.startsWith(SUCCESS, LINEPREFIXLENGTH);
	}

	private boolean testFailed(String line) {
		return currentTestName != null && line.startsWith(FAILURE, LINEPREFIXLENGTH);
	}

	private boolean testErrored(String line) {
		return line.startsWith(ERROR_backwardCompatible, LINEPREFIXLENGTH)
			|| line.startsWith("Exception thrown", LINEPREFIXLENGTH);
	}

	private void testFailure(IRegion reg) {
		String[] firstLine = runningTestOutput.lineAt(0).split(":");
		String fileName = firstLine[0].trim();
		String lineNumber = firstLine[1].trim();

		String failureMessage = failureMessage();

		addNewEvent(new TestFailureEvent(reg, currentTestName, fileName,	lineNumber, failureMessage));
	}

	private String failureMessage() {
		if (runningTestOutput.lineLabelIs(1, VALUE_OF_LABEL)) {
			String evaluatedExpression = runningTestOutput.valueAt(1);
			
			if (runningTestOutput.lineLabelIs(2, EXPECTED_VALUE_LABEL))
				return "expected: "
						+ runningTestOutput.valueAt(2)
						+ " but was: " + evaluatedExpression;
			
			String actual = runningTestOutput.valueAt(2);
			String expected = runningTestOutput.valueAt(3);
			
			if (runningTestOutput.lineLabelIs(4, WHICH_IS_EXPECTED_VALUE_LABEL))
				expected = runningTestOutput.valueAt(4);

			return evaluatedExpression + " expected: " + expected + " but was: " + actual;

		} else if (runningTestOutput.lineLabelIs(1, "Failed")) {
			String thirdLine = runningTestOutput.lineAt(2);
			return !thirdLine.startsWith(FAILURE) ? thirdLine : "FAIL() invoked, but no message has been provided";
		} else {
			StringBuilder expectedMessage = new StringBuilder(runningTestOutput.lineAt(1));
			for (int i = 2; i < runningTestOutput.lines.size()-1; i++)
				expectedMessage.append(" " + runningTestOutput.lineAt(i));
			return expectedMessage.toString();
		}
	}

	private class RunningTestOutput {
		private List<String> lines = new ArrayList<String>();

		public void add(String line) {
			lines.add(line);
		}

		public String lineAt(int i) {
			return lines.get(i);
		}

		boolean lineLabelIs(int index, String label) {
			return lineAt(index).startsWith(label);
		}

		String valueAt(int index) {
			return lineAt(index).split(":")[1].trim();
		}

	}
	
	private boolean suiteEnding(String line) {
		if (noActiveSuite())
			return false;
		
		Pattern pattern = suitePatternWithTailOf(suiteNameForEndTag() + " \\((\\d+) ms total\\)");
		Matcher m = pattern.matcher(line);
		return m.matches();		
	}

	private String suiteNameForEndTag() {
		Matcher matcher = TYPED_TEST_SUITE_PATTERN.matcher(currentSuiteName);
		if (matcher.matches())
			return matcher.group(1) + "/" + matcher.group(2);

		return currentSuiteName;
	}
	
	private boolean noActiveSuite() {
		return currentSuiteName == null;
	}

	private void suiteEnded(IRegion reg, String line) {
		postSuiteBeginEventIfNecesary();
		testEvents.add(new SuiteEndEvent(reg, currentSuiteName));
		currentSuiteName = null;
	}

	private boolean suiteStarting(String line) {
		return line.startsWith(BEGINNING, LINEPREFIXLENGTH)
				&& !line.startsWith(BEGINNING + " Global", LINEPREFIXLENGTH);
	}

	private void suiteStarted(IRegion reg, String line) throws CoreException {
		if (currentSuiteName != null)
			suiteEnded(reg, line);

		Matcher m = matcherFor(SUITEBEGINNINGLINE, line);
		String suiteName = m.group(2);
		String testCount = m.group(1);
		
		currentSuiteName = suiteName;		
		pendingSuiteBeginEvent = new SuiteBeginEvent(reg, currentSuiteName, testCount);
	}

	private boolean sessionEnding(String line) {
		return line.startsWith(SESSIONEND, LINEPREFIXLENGTH)
				&& !line.startsWith(SESSIONSTART, LINEPREFIXLENGTH);
	}

	private void sessionEnded(IRegion reg) {
		testEvents.add(new SuiteEndEvent(reg, currentSuiteName));
		testEvents.add(new SessionEndEvent());
	}

	private String chomp(String one, String two) {
		StringBuffer common = new StringBuffer();
		int x = one.length() > two.length() ? two.length() : one.length();
		for (int i = 0; i < x; i++) {
			if (one.charAt(i) == two.charAt(i))
				common.append(one.charAt(i));
			else
				break;
		}
		return common.toString();
	}
}