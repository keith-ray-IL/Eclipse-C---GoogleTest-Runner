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

import org.eclipse.jface.text.IRegion;


/**
 * @author Emanuel Graf, Mike Bria
 */
public class PatternListenerSessionStartEndTest extends PatternListenerBase {

	boolean sessionStarted = false;
	boolean sessionEnded = false;
	
	
	final class SessionStartEndHandler extends TestHandler{

		@Override
		public void handleSessionEnd() {
			sessionEnded = true;
		}

		@Override
		public void handleSessionStart() {
			sessionStarted = true;
			tc.removePatternMatchListener(cpl);
		}
	}

	public void testSessionStart() {
//		assertTrue("No session Start", sessionStarted);
	}
	
	public void testSessionEnd() {
		assertTrue("No session End", sessionEnded);
	}

	protected TestHandler handler() {
		return new SessionStartEndHandler();
	}

	@Override
	protected String getInputFileName() {
		return "sessionTest.txt";
	}
	
}
