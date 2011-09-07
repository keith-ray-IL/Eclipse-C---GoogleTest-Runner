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
public class PatternListenerTestSuccessTest extends PatternListenerBase {
	
	List<String> testNameStart = new ArrayList<String>();
	List<String> testNameEnd = new ArrayList<String>();
	List<String> msgEnd = new ArrayList<String>();
	
	final class TestSuccessHandler extends TestHandler{
		@Override
		protected void handleSuccess(IRegion reg, String testname, String msg) {
			testNameEnd.add(testname);
			msgEnd.add( msg);
		}

		@Override
		protected void handleTestStart(IRegion reg, String testname) {
			testNameStart.add(testname);
		}
	}

	@Override
	protected TestHandler handler() {
		return new TestSuccessHandler();
	}

	
	public void testTestStart() {
		assertEquals("xUnitTest", testNameStart.get(0));
	}
	
	public void testTestEnd() {
		assertEquals("xUnitTest", testNameEnd.get(0));
		assertEquals("OK", msgEnd.get(0));
	}

	public void testTypedTestStart() {
		assertEquals("typedTestOne", testNameStart.get(1));
	}
	
	public void testTypedTestEnd() {
		assertEquals("typedTestOne", testNameEnd.get(1));
		assertEquals("OK", msgEnd.get(1));
	}

	public void testConsoleCrrapStart() {
		assertEquals("detectTestEnd", testNameStart.get(2));
	}
	
	public void testConsoleCrrapEnd() {
		assertEquals("detectTestEnd", testNameEnd.get(2));
		assertEquals("OK", msgEnd.get(1));
	}
	
	@Override
	protected String getInputFileName() {
		return "successTest.txt";
	}

}
