/*******************************************************************************
 * Copyright (c) 2007 Institute for Software, HSR Hochschule für Technik  
 * Rapperswil, University of applied sciences
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 * 
 * Contributors: 
 * Emanuel Graf & Guido Zgraggen- initial API and implementation 
 ******************************************************************************/
package ch.hsr.ifs.cutelauncher.test.patternListenerTests;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.IRegion;

/**
 * @author Emanuel Graf, Mike Bria
 */
public class PatternListenerTestEqualsFailed extends PatternListenerBase {

	private List<String> testNameStart = new ArrayList<String>();
	private List<String> testNameEnd = new ArrayList<String>();
	private List<String> msg = new ArrayList<String>();
	private List<String> testFileName = new ArrayList<String>();
	private List<Integer> lineNr = new ArrayList<Integer>();
	
	final class TestFailedHandler extends TestHandler{

		@Override
		protected void handleFailure(IRegion reg, String testName, String fileName, String lineNo, String reason) {
			testNameEnd.add( testName);
			testFileName.add( fileName);
			lineNr.add( Integer.parseInt(lineNo));
			msg.add( reason);
		}

		@Override
		protected void handleTestStart(IRegion reg, String testname) {
			testNameStart.add(testname);
		}
		
	}
	
	public void testTestStartForValuedExpected() {
		assertEquals("Teststart name", "ExpectedIsValueTest", testNameStart.get(0));
	}
	
	public void testTestEndForValuedExpected() {
		assertEquals("Testend name", "ExpectedIsValueTest", testNameEnd.get(0));
		assertEquals("Message", "Factorial(0) expected: 3 but was: 1", msg.get(0));
		assertEquals("Filename", "../src/sample1_unittest.cc", testFileName.get(0));
		assertEquals("Line", 104, (int)lineNr.get(0));
	}
	
	public void testTestStartForReferencedExpected() {
		assertEquals("Teststart name", "ExpectedIsSymbolTest", testNameStart.get(1));
	}
	
	public void testTestEndForReferencedExpected() {
		assertEquals("Testend name", "ExpectedIsSymbolTest", testNameEnd.get(1));
		assertEquals("Message", "Factorial(1) expected: \"<orders>\" but was: \"<crrap>\"", msg.get(1));
		assertEquals("Filename", "../src/sample1_unittest.cc", testFileName.get(1));
		assertEquals("Line", 120, (int)lineNr.get(1));
	}

	
	public void testTestStartForStringEq() {
		assertEquals("Teststart name", "StringEqTst", testNameStart.get(2));
	}
	
	public void testTestEndForStringEq() {
		assertEquals("Testend name", "StringEqTst", testNameEnd.get(2));
		assertEquals("Message", "expected: \"Harry\" but was: \"Bob\"", msg.get(2));
		assertEquals("Filename", "../src/sample1_unittest.cc", testFileName.get(2));
		assertEquals("Line", 125, (int)lineNr.get(2));
	}
	
	public void testTestStartForSingleLineMessage() {
		assertEquals("Teststart name", "SingleLineMessageTest", testNameStart.get(3));
	}
	
	public void testTestEndForSingleLineMessage() {
		assertEquals("Testend name", "SingleLineMessageTest", testNameEnd.get(3));
		assertEquals("Message", "Expected: (1+1) != (1+1+0), actual: 2 vs 2", msg.get(3));
		assertEquals("Filename", "../src/sample1_unittest.cc", testFileName.get(3));
		assertEquals("Line", 130, (int)lineNr.get(3));
	}

	
	public void testTestStartForMultiLineGenericMessage() {
		assertEquals("Teststart name", "MultilineGenericMessage", testNameStart.get(4));
	}
	
	public void testTestEndForMultiLineGenericMessage() {
		assertEquals("Testend name", "MultilineGenericMessage", testNameEnd.get(4));
		
		StringBuilder expectedMessage = new StringBuilder("The difference between 1+1 and 2+1 is 1, which exceeds 0.0001 + 0.0001, where");
		expectedMessage.append(" 1+1 evaluates to 2,");
		expectedMessage.append(" 2+1 evaluates to 3, and");
		expectedMessage.append(" 0.0001 + 0.0001 evaluates to 0.0002.");
		
		assertEquals("Message", expectedMessage.toString(), msg.get(4));
		assertEquals("Filename", "../src/sample1_unittest.cc", testFileName.get(4));
		assertEquals("Line", 150, (int)lineNr.get(4));
	}

	protected TestHandler handler() {
		return new TestFailedHandler();
	}

	@Override
	protected String getInputFileName() {
		return "failedEqualsTest.txt";
	}

}
