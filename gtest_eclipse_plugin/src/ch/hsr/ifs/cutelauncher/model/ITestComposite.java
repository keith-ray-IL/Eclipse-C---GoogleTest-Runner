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
 * @author Emanuel Graf
 *
 */
public interface ITestComposite {

	public abstract int getError();

	public abstract int getFailure();

	public abstract int getSuccess();

	public abstract int getTotalTests();

	public abstract int getRun();
	
	public abstract Vector<? extends TestElement> getElements();
	
	public void addTestElement(TestElement element);
	
	public boolean hasErrorOrFailure();
	
	public void addListener(ITestCompositeListener listener);
	
	public void removeListener(ITestCompositeListener listener);

}