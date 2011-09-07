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

import org.eclipse.core.resources.IFile;

/**
 * @author Emanuel Graf
 *
 */
public class TestCase extends TestElement {
	
	private TestStatus status;
	
	private String name;
	
	private IFile file;
	
	private int lineNumber = -1;
	
	private TestResult result;
	
	private ITestComposite parent = null;

	public TestCase(String name) {
		super();
		this.name = name;
		status = TestStatus.running;
	}

	@Override
	public ITestComposite getParent() {
		return parent;
	}

	@Override
	public void setParent(ITestComposite parent) {
		this.parent = parent;
	}

	public IFile getFile() {
		return file;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public TestStatus getStatus() {
		return status;
	}

	public int getLineNumber() {
		return lineNumber;
	}
	
	public String getMessage() {
		if(result == null) {
			return "";
		}else {
			return result.getMsg();
		}
	}

	@Override
	public String toString() {
		return getName();
	}
	
	public void endTest(IFile file, int lineNumber, TestResult result, TestStatus status) {
		this.file = file;
		this.lineNumber = lineNumber;
		this.result = result;
		this.status = status;
		notifyListeners(new NotifyEvent(NotifyEvent.EventType.testFinished, this));
	}
	
	public TestResult getResult() {
		return result;
	}

}
