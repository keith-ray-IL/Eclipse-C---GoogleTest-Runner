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

import org.eclipse.jface.text.IRegion;

public abstract class TestEventHandler {

	public void handle(TestEvent testEvent) {
		if (testEvent instanceof TestStartEvent) {
			TestStartEvent event = (TestStartEvent) testEvent;
			handleTestStart(event.reg, event.testName);
		} else if (testEvent instanceof TestSuccessEvent) {
			TestSuccessEvent event = (TestSuccessEvent) testEvent;
			handleSuccess(event.reg, event.testName, event.msg);
		} else if (testEvent instanceof TestFailureEvent) {
			TestFailureEvent event = (TestFailureEvent) testEvent;
			handleFailure(event.reg, event.testName, event.fileName,
					event.lineNo, event.reason);
		} else if (testEvent instanceof TestErrorEvent) {
			TestErrorEvent event = (TestErrorEvent) testEvent;
			handleError(event.reg, event.testName, event.msg);
		} else if (testEvent instanceof SuiteBeginEvent) {
			SuiteBeginEvent event = (SuiteBeginEvent) testEvent;
			handleSuiteBeginning(event.reg, event.suiteName, event.suiteSize);
		} else if (testEvent instanceof SuiteEndEvent) {
			SuiteEndEvent event = (SuiteEndEvent) testEvent;
			handleSuiteEnding(event.reg, event.suitename);
		} else if (testEvent instanceof SessionStartEvent) {
			handleSessionStart();
		} else if (testEvent instanceof SessionEndEvent) {
			handleSessionEnd();
		}
	}

	protected abstract void handleSuiteBeginning(IRegion reg, String suitename,
			String suitesize);
	
	protected abstract void handleTestStart(IRegion reg, String testName);
	
	protected abstract void handleError(IRegion reg, String testName, String msg);
	
	protected abstract void handleSuccess(IRegion reg, String name, String msg);
	
	protected abstract void handleSuiteEnding(IRegion reg, String suitename);
	
	protected abstract void handleFailure(IRegion reg, String testName,
			String fileName, String lineNo, String reason);
	
	public abstract void handleSessionStart();
	
	public abstract void handleSessionEnd();
}