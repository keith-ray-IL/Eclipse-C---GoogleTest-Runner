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

import org.eclipse.jface.action.Action;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Shell;

import ch.hsr.ifs.cutelauncher.model.TestCase;

/**
 * @author Emanuel Graf
 *
 */
public class CuteCompareResultAction extends Action {
	
	private TestCase test;
	private CuteCompareResultDialog dialog;
	private Shell shell;
	
	

	public CuteCompareResultAction(TestCase test, Shell shell) {
		super();
		this.test = test;
		this.shell = shell;
	}



	@Override
	public void run() {
		if (dialog != null) {
			dialog.setCompareViewerInput(test);
				
		} else {
			dialog= new CuteCompareResultDialog(shell, test);
			dialog.create();
			dialog.getShell().addDisposeListener(new DisposeListener() {
				public void widgetDisposed(DisposeEvent e) {
					dialog= null;
				}
			});
			dialog.setBlockOnOpen(false);
			dialog.open();
		}
	}
	

}
