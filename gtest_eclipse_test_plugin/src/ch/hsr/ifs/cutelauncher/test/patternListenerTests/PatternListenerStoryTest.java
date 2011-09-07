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
 * @author Mike Bria
 *
 */
public class PatternListenerStoryTest extends PatternListenerBase {

	int startOffset = -1;
	int endLineNo = -1;
	
	List<Integer> suiteSize = new ArrayList<Integer>();
	List<String> suiteNameStart = new ArrayList<String>();
	List<String> suiteNameEnded = new ArrayList<String>();

	private final class ListenerTestHandler extends TestHandler{

		@Override
		protected void handleSuiteBeginning(IRegion reg, String suitename, String suitesize) {
			suiteNameStart.add(suitename);
			suiteSize.add(Integer.parseInt(suitesize));
		}

		@Override
		protected void handleSuiteEnding(IRegion reg, String suitename) {
			suiteNameEnded.add(suitename);
		}
		
	}
	
	public void testTODO() throws Exception {
		
	}
	
//	public void testFirstStarted() {
//		assertEquals("Suite Name Test", "xUnitTest1", suiteNameStart.get(0));
//		assertEquals("Suite Size", new Integer(42), suiteSize.get(0));
//	}
//	
//	public void testFirstEnded() {
//		assertEquals("Suite Name Test", "xUnitTest1", suiteNameEnded.get(0));
//	}
//	
//	public void testLastStarted() {
//		assertEquals("Suite Name Test", "xUnitTest2", suiteNameStart.get(1));
//		assertEquals("Suite Size", new Integer(6), suiteSize.get(1));
//	}
//	
//	public void testLastEnded() {
//		assertEquals("Suite Name Test", "xUnitTest2", suiteNameEnded.get(1));
//	}

	@Override
	protected String getInputFileName() {
		return "storytest.txt";
	}

	@Override
	protected TestHandler handler() {
		return new ListenerTestHandler();
	}

}
