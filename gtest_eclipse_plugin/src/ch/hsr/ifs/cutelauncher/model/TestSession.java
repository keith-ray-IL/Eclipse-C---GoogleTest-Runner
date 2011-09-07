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

import org.eclipse.debug.core.ILaunch;

public class TestSession implements ITestComposite{
	private Vector<TestElement> rootElements = new Vector<TestElement>();
	private Vector<ITestCompositeListener> listeners = new Vector<ITestCompositeListener>();;
	
	private ILaunch launch;

	public TestSession(ILaunch launch) {
		super();
		this.launch = launch;
	}

	public Vector<TestElement> getRootElements() {
		return rootElements;
	}

	public ILaunch getLaunch() {
		return launch;
	}

	public void addTestElement(TestElement element) {
		rootElements.add(element);
		element.setParent(this);
		for (ITestCompositeListener lis : listeners) {
			lis.newTestElement(this, element);
		}
	}

	public Vector<TestElement> getElements() {
		return rootElements;
	}

	public int getError() {
		int tot = 0;
		for (TestElement tElement : rootElements) {
			if (tElement instanceof ITestComposite) {
				ITestComposite testComp = (ITestComposite) tElement;
				tot += testComp.getError();
			}else if (tElement instanceof TestCase) {
				TestCase tCase = (TestCase) tElement;
				if(tCase.getStatus() == TestStatus.error) {
					++tot;
				}
			}
		}
		return tot;
	}

	public int getFailure() {
		int tot = 0;
		for (TestElement tElement : rootElements) {
			if (tElement instanceof ITestComposite) {
				ITestComposite testComp = (ITestComposite) tElement;
				tot += testComp.getFailure();
			}else if (tElement instanceof TestCase) {
				TestCase tCase = (TestCase) tElement;
				if(tCase.getStatus() == TestStatus.failure) {
					++tot;
				}
			}
		}
		return tot;
	}

	public int getRun() {
		int tot = 0;
		for (TestElement tElement : rootElements) {
			if (tElement instanceof ITestComposite) {
				ITestComposite testComp = (ITestComposite) tElement;
				tot += testComp.getRun();
			}else if (tElement instanceof TestCase) {
				TestCase tCase = (TestCase) tElement;
				if(tCase.getStatus() == TestStatus.error ||tCase.getStatus() == TestStatus.failure || tCase.getStatus() == TestStatus.success) {
					++tot;
				}
			}
		}
		return tot;
	}

	public int getSuccess() {
		int tot = 0;
		for (TestElement tElement : rootElements) {
			if (tElement instanceof ITestComposite) {
				ITestComposite testComp = (ITestComposite) tElement;
				tot += testComp.getSuccess();
			}else if (tElement instanceof TestCase) {
				TestCase tCase = (TestCase) tElement;
				if(tCase.getStatus() == TestStatus.success) {
					++tot;
				}
			}
		}
		return tot;
	}

	public int getTotalTests() {
		int tot = 0;
		for (TestElement tElement : rootElements) {
			if (tElement instanceof ITestComposite) {
				ITestComposite testComp = (ITestComposite) tElement;
				tot += testComp.getTotalTests();
			}else if (tElement instanceof TestCase) {
				++tot;
			}
		}
		return tot;
	}

	public boolean hasErrorOrFailure() {
		return getFailure() + getError() > 0;
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