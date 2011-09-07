/*******************************************************************************
 * Copyright (c) 2007 Institute for Software, HSR Hochschule für Technik
 * Rapperswil, University of applied sciences and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Emanuel Graf - initial API and implementation
 ******************************************************************************/
package ch.hsr.ifs.cutelauncher.ui;

import org.eclipse.cdt.core.model.IBinary;
import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;

/**
 * @author Emanuel Graf
 *
 */
public class ProjectNaturePropertyTester extends PropertyTester {

	/**
	 * 
	 */
	public ProjectNaturePropertyTester() {
//		System.out.println("PropTester K'tor");
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.expressions.IPropertyTester#test(java.lang.Object, java.lang.String, java.lang.Object[], java.lang.Object)
	 */
	public boolean test(Object receiver, String property, Object[] args,
			Object expectedValue) {
		
//		System.out.println("test "+receiver+" "+property+" "+expectedValue);
		
		if (property.equals("projectNature1")) {
			try {
				if(receiver instanceof IResource){
					IResource res = (IResource) receiver;
					IProject proj = res.getProject();
					boolean result=proj != null;
					boolean result1=proj.isAccessible();
					boolean result2= proj.hasNature(toString(expectedValue));
					return result && result1 && result2;
				}
				if(receiver instanceof IBinary){
					IBinary bin=(IBinary)receiver;
					IProject proj = bin.getCProject().getProject();
					boolean result=proj != null;
					boolean result1=proj.isAccessible();
					boolean result2= proj.hasNature(toString(expectedValue));
					return result && result1 && result2;
				}
			} catch (CoreException e) {
				return false;
			}
		}
		
		return false;
	}

	/**
	 * Converts the given expected value to a <code>String</code>.
	 * 
	 * @param expectedValue
	 *            the expected value (may be <code>null</code>).
	 * @return the empty string if the expected value is <code>null</code>,
	 *         otherwise the <code>toString()</code> representation of the
	 *         expected value
	 */
	protected String toString(Object expectedValue) {
		return expectedValue == null ? "" : expectedValue.toString(); //$NON-NLS-1$
	}	
		
}
//@see org.eclipse.core.internal.propertytester.ResourcePropertyTester