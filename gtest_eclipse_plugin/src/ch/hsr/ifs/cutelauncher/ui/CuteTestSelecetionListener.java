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

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeSelection;

import ch.hsr.ifs.cutelauncher.model.TestElement;

/**
 * @author egraf
 *
 */
public class CuteTestSelecetionListener implements ISelectionChangedListener {
	
	private TestViewer viewer;
	
	

	public CuteTestSelecetionListener(TestViewer viewer) {
		super();
		this.viewer = viewer;
	}



	public void selectionChanged(SelectionChangedEvent event) {
		if (event.getSelection() instanceof TreeSelection) {
			TreeSelection treeSel = (TreeSelection) event.getSelection();
			if (treeSel.getFirstElement() instanceof TestElement) {
				TestElement testElement = (TestElement) treeSel.getFirstElement();
				viewer.showTestDetails(testElement);
			}
		}
	}

}
