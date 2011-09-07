/*******************************************************************************
 * Copyright (c) 2007 Institute for Software, HSR Hochschule für Technik  
 * Rapperswil, University of applied sciences
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 * 
 * Contributors: 
 * Emanuel Graf - initial API and implementation 
 ******************************************************************************/
package ch.hsr.ifs.cutelauncher.model;

import java.util.Vector;

/**
 * @author egraf
 *
 */
public class TestSuite extends TestElement implements ITestComposite, ITestElementListener {
	
	private String name = "";
	
	private ITestComposite parent;
	
	private int totalTests = 0; 
	private int success = 0;
	private int failure = 0;
	private int error = 0;
	
	private TestStatus status;
	
	private Vector<TestElement> cases = new Vector<TestElement>();
	private Vector<ITestCompositeListener> listeners = new Vector<ITestCompositeListener>();
	
	

	public TestSuite(String name, int totalTests, TestStatus status) {
		super();
		this.name = name;
		this.totalTests = totalTests;
		this.status = status;
	}

	public String getName() {
		return name;
	}

	public TestStatus getStatus() {
		return status;
	}
		
	protected void endTest(TestElement tCase) {
		switch(tCase.getStatus()) {
		case success:
			++success;
			break;
		case failure:
			++failure;
			break;
		case error:
			++error;
			break;
		}
		notifyListeners(new NotifyEvent(NotifyEvent.EventType.testFinished, tCase));
	}
	
	private void setEndStatus() {
		if(cases.size() == 0) {
			status = TestStatus.success;
		}else {
			for (TestElement tCase : cases) {
				switch (status) {
				case running:
					status = tCase.getStatus();
					break;
				case success:
					if(tCase.getStatus() != TestStatus.success) {
						status = tCase.getStatus();
					}
					break;
				case failure:
					if(tCase.getStatus() == TestStatus.error) {
						status = tCase.getStatus();
					}
					break;
				default:
					//nothing
				}
			}
		}
	}

	public int getError() {
		return error;
	}

	public int getFailure() {
		return failure;
	}

	public int getSuccess() {
		return success;
	}

	public int getTotalTests() {
		return totalTests;
	}
	
	public boolean hasErrorOrFailure() {
		return failure + error > 0;
	}

	public int getRun() {
		return success + failure + error;
	}
	@Override
	public String toString() {
		return getName();
	}

	public void end() {
		setEndStatus();
		notifyListeners(new NotifyEvent(NotifyEvent.EventType.suiteFinished, this));
	}

	public void addTestElement(TestElement element) {
		cases.add(element);
		element.setParent(this);
		element.addTestElementListener(this);
		for (ITestCompositeListener lis : listeners) {
			lis.newTestElement(this, element);
		}
	}

	public Vector<TestElement> getElements() {
		return cases;
	}

	@Override
	public ITestComposite getParent() {
		return parent;
	}

	@Override
	public void setParent(ITestComposite parent) {
		this.parent = parent;
	}

	public void modelCanged(TestElement source, NotifyEvent event) {
		if(event.getType() == NotifyEvent.EventType.testFinished) {
			endTest(source);
		}
		
	}

	public void addListener(ITestCompositeListener listener) {
		if(!listeners.contains(listener)) {
			listeners.add(listener);
		}
	}

	public void removeListener(ITestCompositeListener listener) {
		listeners.remove(listener);
	}

}
