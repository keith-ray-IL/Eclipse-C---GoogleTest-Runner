/*******************************************************************************
 * Copyright (c) 2008, Industrial Logic, Inc. All Rights Reserved. 
 * 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 * 
 * Contributors: 
 * Industrial Logic, Inc.:  Mike Bria & John Tangney - initial implementation (based on ideas originating from the work of Emanuel Graff)
 ******************************************************************************/
package ch.hsr.ifs.cutelauncher.event;

import org.eclipse.jface.text.IRegion;

public class SuiteBeginEvent implements TestEvent {
	public IRegion reg;
	public String suiteName;
	public String suiteSize;
	
	public SuiteBeginEvent(IRegion reg, String suiteName, String suiteSize) {
		this.reg = reg;
		this.suiteName = suiteName;
		this.suiteSize = suiteSize;
	}
	
	public void appendToSuiteName(String tail){
		suiteName = suiteName.concat(tail);
	}
}