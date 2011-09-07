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

import ch.hsr.ifs.cutelauncher.CuteLauncherPlugin;

/**
 * @author Emanuel Graf
 *
 */
public class ScrollLockAction extends Action {
	
	private TestRunnerViewPart view;
	
	public ScrollLockAction(TestRunnerViewPart view) {
		super("Scroll Lock");
		this.view = view;
		setToolTipText("Scroll Lock"); 
		setDisabledImageDescriptor(CuteLauncherPlugin.getImageDescriptor("dlcl16/lock.gif")); //$NON-NLS-1$
		setHoverImageDescriptor(CuteLauncherPlugin.getImageDescriptor("obj16/lock.gif")); //$NON-NLS-1$
		setImageDescriptor(CuteLauncherPlugin.getImageDescriptor("obj16/lock.gif")); //$NON-NLS-1$
		setChecked(false);		
	}

	@Override
	public void run() {
		view.setAutoScroll(!isChecked());
	}
	
	

}
