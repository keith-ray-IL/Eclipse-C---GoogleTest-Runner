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

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.actions.ActionDelegate;

import ch.hsr.ifs.cutelauncher.CuteLauncherPlugin;
import ch.hsr.ifs.cutelauncher.CuteNature;

/**
 * @author Emanuel Graf
 *
 */
public class ConvertProjectAction extends ActionDelegate implements
		IObjectActionDelegate {
	
	private IProject project; 

	/**
	 * 
	 */
	public ConvertProjectAction() {
		// TODO Auto-generated constructor stub
	}


	public void setActivePart(IAction action, IWorkbenchPart targetPart) {

	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection sel = (IStructuredSelection) selection;
			Object obj = sel.getFirstElement();
			if (obj instanceof IProject) {
				IProject project = (IProject)obj;
				 setSelectedProject(project);
				 return;
			}
		}
		setSelectedProject(null);
	}


	private void setSelectedProject(IProject project) {
		this.project = project;		
	}


	@Override
	public void run(IAction action) {
		Shell shell = CuteLauncherPlugin.getActiveWorkbenchWindow().getShell();
		
		try {
			String[] natureIds = project.getDescription().getNatureIds();
			if(hasCuteNature(natureIds)) {
				MessageDialog.openInformation(shell, "Project is already converted", "This project is already a CUTE 1.2 project.");
			}else if(checkProject(project)){
				CuteNature.addCuteNature(project, new NullProgressMonitor());
				project.close(new NullProgressMonitor());
				project.open(new NullProgressMonitor());
			}else {
				MessageDialog.openError(shell, "No CUTE Project", "This is not a valid CUTE Project.\nCUTE header files should be located in "+project.getName()+"/cute/");
			}
		} catch (CoreException e) {
			MessageDialog.openError(shell, "Can't convert Project", "Can't convert Project. " + e.getMessage());
		}
	}


	private boolean checkProject(IProject project2) {
		IPath cutePath = project2.getLocation().append("cute").append("cute.h");
		return cutePath.makeAbsolute().toFile().exists();
	}


	private boolean hasCuteNature(String[] natureIds) {
		for (String string : natureIds) {
			if(string.equals(CuteNature.CUTE_NATURE_ID))return true;
		}
		return false;
	}
	

}
