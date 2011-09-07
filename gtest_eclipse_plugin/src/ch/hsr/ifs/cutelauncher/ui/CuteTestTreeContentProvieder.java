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
package ch.hsr.ifs.cutelauncher.ui;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import ch.hsr.ifs.cutelauncher.model.ITestComposite;
import ch.hsr.ifs.cutelauncher.model.TestElement;

/**
 * @author egraf
 *
 */
public class CuteTestTreeContentProvieder implements ITreeContentProvider {

	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof ITestComposite) {
			ITestComposite composite = (ITestComposite) parentElement;
			return composite.getElements().toArray();
		}
		return null;
	}

	public Object getParent(Object element) {
		if (element instanceof TestElement) {
			TestElement tElement = (TestElement) element;
			return tElement.getParent();
		}
		return null;
	}

	public boolean hasChildren(Object element) {
		if (element instanceof ITestComposite) {
			ITestComposite composite = (ITestComposite) element;
			boolean ret = composite.getElements().size() > 0;
			return ret;
		}
		return false;
	}

	public Object[] getElements(Object inputElement) {
		if (inputElement instanceof ITestComposite) {
			ITestComposite composite = (ITestComposite) inputElement;
			return composite.getElements().toArray();
		}
		return null;
	}

	public void dispose() {
		// TODO Auto-generated method stub

	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}

}
