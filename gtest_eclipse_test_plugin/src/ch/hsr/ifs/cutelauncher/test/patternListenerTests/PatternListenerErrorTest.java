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
public class PatternListenerErrorTest extends PatternListenerBase {
	List<String> messages = new ArrayList<String>();
	List<String> testNameStarted = new ArrayList<String>();
	List<String> testNameEnded = new ArrayList<String>();
	
	
	final class ErrorHandler extends TestHandler {

		@Override
		protected void handleError(IRegion reg, String testName, String msg) {
			testNameEnded.add(testName);
			messages.add(msg);
		}
		
		@Override
		protected void handleTestStart(IRegion reg, String testname) {
			testNameStarted.add(testname);
		}
		
	}
	
	public void testOldGTestStart() {
		assertEquals("Teststart name", "xUnitOldGTest", testNameStarted.get(0));
	}
	
	public void testOldGTestEnd() {
		assertEquals("Testend name", "xUnitOldGTest", testNameEnded.get(0));
		assertEquals("Message", "exception of unknown type", messages.get(0));
	}
	
	public void testNewGTestStart() {
		assertEquals("Teststart name", "xUnitNewGTest", testNameStarted.get(1));
	}
	
	public void testNewGTestEnd() {
		assertEquals("Testend name", "xUnitNewGTest", testNameEnded.get(1));
		assertEquals("Message", "St9exception in the test body", messages.get(1));
	}

	protected TestHandler handler() {
		return new ErrorHandler();
	}

	@Override
	protected String getInputFileName() {
		return "errorTest.txt";
	}

}
