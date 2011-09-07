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
public class PatternListenerSuiteTest extends PatternListenerBase {
	List<Integer> suiteSize = new ArrayList<Integer>();
	List<String> suiteNameStart = new ArrayList<String>();
	List<String> suiteNameEnded = new ArrayList<String>();

	private final class ListenerTestHandler extends TestHandler {

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
	
	public void testFirst() {
		assertSuiteAt(0, "TestSuite1", 42);
	}
	
	public void testSecond() {
		assertSuiteAt(1, "TypedTest/0, where TypeParam = TestType", 1);
	}
	
	public void testThird() {
		assertSuiteAt(2, "TestSuite3", 3);
	}
	
	public void testMultine() {
		assertSuiteAt(3, "ContainerEqTest/0, where TypeParam =std::set<int, std::less<int>, std::allocator<int> >", 5);
	}

	private void assertSuiteAt(int index, String expectedSuiteName, int expectedSuiteSize) {
		assertEquals(expectedSuiteName, suiteNameStart.get(index));
		assertEquals(expectedSuiteName, suiteNameEnded.get(index));
		assertEquals(new Integer(expectedSuiteSize), suiteSize.get(index));
	}
	

	@Override
	protected TestHandler handler() {
		return new ListenerTestHandler();
	}


	@Override
	protected String getInputFileName() {
		return "suiteTest.txt";
	}

}
