/*******************************************************************************
 * Copyright (c) 2007 Institute for Software, HSR Hochschule fuer Technik  
 * Rapperswil, University of applied sciences
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 * 
 * Contributors: 
 * Emanuel Graf - initial API and implementation 
 * Industrial Logic, Inc.:  Mike Bria & John Tangney - enhancements to support additional C++ unit testing frameworks, such as GTest
 ******************************************************************************/
package ch.hsr.ifs.cutelauncher.event;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IRegion;

public class CuteConsoleEventParser extends ConsoleEventParser {

	private static final String LINE_QUALIFIER = "#";
	private static final int LINEPREFIXLENGTH = LINE_QUALIFIER.length();
	private static final String BEGINNING = "beginning";
	private static final String ENDING = "ending";
	private static final String STARTTEST = "starting";
	private static final String SUCCESS = "success";
	private static final String FAILURE = "failure";
	private static final String ERROR = "error";

	private static Pattern SUITEBEGINNINGLINE = Pattern.compile(LINE_QUALIFIER
			+ BEGINNING + " (.*) (\\d+)$");
	private static Pattern SUITEENDINGLINE = Pattern.compile(LINE_QUALIFIER
			+ ENDING + " (.*)$");
	private static Pattern TESTSTARTLINE = Pattern.compile(LINE_QUALIFIER
			+ STARTTEST + " (.*)$");
	private static Pattern TESTFAILURELINE = Pattern.compile(LINE_QUALIFIER
			+ FAILURE + " (.*) (.*):(\\d+) (.*)$");
	private static Pattern TESTSUCESSLINE = Pattern.compile(LINE_QUALIFIER
			+ SUCCESS + " (.*) (.*)$");
	private static Pattern TESTERRORLINE = Pattern.compile(LINE_QUALIFIER
			+ ERROR + " (.*?) (.*)$");

	public String getLineQualifier() {
		return escapeForRegex(LINE_QUALIFIER);
	}

	public final String getComprehensiveLinePattern() {
		return escapeForRegex(LINE_QUALIFIER
				+ "("
				+ regExUnion(new String[] { LINE_QUALIFIER, BEGINNING, ENDING,
						SUCCESS, STARTTEST, FAILURE, ERROR, }))
				+ ")(.*)(\\n)";
	}

	protected void extractTestEventsFor(IRegion reg, String line)
			throws CoreException {
		if (testStarting(line))
			testStart(reg, line);
		else if (testSucceeded(line))
			testSuccess(reg, line);
		else if (testFailed(line))
			testFailure(reg, line);
		else if (suiteStarting(line))
			suiteStarted(reg, line);
		else if (suiteEnding(line))
			suiteEnded(reg, line);
		else if (testErrored(line))
			testError(reg, line);
	}

	private boolean testStarting(String line) {
		return line.startsWith(STARTTEST, LINEPREFIXLENGTH);
	}

	private void testStart(IRegion reg, String line) throws CoreException {
		Matcher m = matcherFor(TESTSTARTLINE, line);
		testEvents.add(new TestStartEvent(reg, m.group(1)));
	}

	private boolean testSucceeded(String line) {
		return line.startsWith(SUCCESS, LINEPREFIXLENGTH);
	}

	private void testSuccess(IRegion reg, String line) throws CoreException {
		Matcher m = matcherFor(TESTSUCESSLINE, line);
		testEvents.add(new TestSuccessEvent(reg, m.group(1), m.group(2)));
	}

	private boolean testFailed(String line) {
		return line.startsWith(FAILURE, LINEPREFIXLENGTH);
	}

	private void testFailure(IRegion reg, String line) throws CoreException {
		Matcher m = matcherFor(TESTFAILURELINE, line);
		testEvents.add(new TestFailureEvent(reg, m.group(1), m.group(2), m
				.group(3), m.group(4)));
	}

	private boolean suiteStarting(String line) {
		return line.startsWith(BEGINNING, LINEPREFIXLENGTH);
	}

	private void suiteStarted(IRegion reg, String line) throws CoreException {
		Matcher m = matcherFor(SUITEBEGINNINGLINE, line);
		testEvents.add(new SuiteBeginEvent(reg, m.group(1), m.group(2)));
	}

	private boolean suiteEnding(String line) {
		return line.startsWith(ENDING, LINEPREFIXLENGTH);
	}

	private void suiteEnded(IRegion reg, String line) throws CoreException {
		Matcher m = matcherFor(SUITEENDINGLINE, line);
		testEvents.add(new SuiteEndEvent(reg, m.group(1)));
	}

	private boolean testErrored(String line) {
		return line.startsWith(ERROR, LINEPREFIXLENGTH);
	}

	private void testError(IRegion reg, String line) throws CoreException {
		Matcher m = matcherFor(TESTERRORLINE, line);
		testEvents.add(new TestErrorEvent(reg, m.group(1), m.group(2)));
	}

}