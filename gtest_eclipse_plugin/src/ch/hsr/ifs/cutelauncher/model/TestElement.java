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

public abstract class TestElement {
	
	protected Vector<ITestElementListener> listeners = new Vector<ITestElementListener>();
	
	public abstract ITestComposite getParent();
	
	public abstract void setParent(ITestComposite parent);

	public abstract String getName();

	public abstract TestStatus getStatus();
	
	public void addTestElementListener(ITestElementListener lis) {
		if(!listeners.contains(lis)) {
			listeners.add(lis);
		}
	}
	
	public void removeTestElementListener(ITestElementListener lis) {
		listeners.remove(lis);
	}
	
	protected void notifyListeners(NotifyEvent event) {
		for (ITestElementListener lis : listeners) {
			lis.modelCanged(this, event);
		}
	}

}