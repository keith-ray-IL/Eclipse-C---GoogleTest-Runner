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

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.text.IRegion;

public class CountsStoryTest extends PatternListenerBase {
	private Set<String> testsRun = new HashSet<String>();
	private Set<String> testsPassed = new HashSet<String>();
	private Set<String> testsFailed = new HashSet<String>();
	private Set<String> testsErrored = new HashSet<String>();
	
	private final class ListenerTestHandler extends TestHandler{
		@Override
		protected void handleError(IRegion reg, String testname, String msg) {
			testsErrored.add(testname);			
		}

		@Override
		protected void handleFailure(IRegion reg, String testname, String fileName, String lineNo, String reason) {
			testsFailed.add(testname);			
		}

		@Override
		protected void handleSuccess(IRegion reg, String testname, String msg) {
			testsPassed.add(testname);			
		}

		@Override
		protected void handleTestStart(IRegion reg, String testname) {
			testsRun.add(testname);			
		}
	}
	
	public void testCounts() throws Exception {
//		assertEquals(1558, testsRun.size());
//		assertEquals(1539, testsPassed.size());
//		assertEquals(0, testsErrored.size());
//		assertEquals(19, testsFailed.size());
	}

	@Override
	protected String getInputFileName() {
		return "countsstorytest.txt";
	}

	@Override
	protected TestHandler handler() {
		return new ListenerTestHandler();
	}

}
