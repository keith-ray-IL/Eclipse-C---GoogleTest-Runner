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


/**
 * @author Emanuel Graf
 *
 */
public class TestResult {
	
	protected String msg;
	
	protected TestResult() {
		msg = "";
	}

	public TestResult(String msg) {
		super();
		this.msg = msg;
	}

	public String getMsg() {
		return msg;
	}
	
	
	
	
}
