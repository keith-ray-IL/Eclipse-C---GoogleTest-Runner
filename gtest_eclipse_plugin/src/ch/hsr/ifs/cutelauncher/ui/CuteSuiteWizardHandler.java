/*******************************************************************************
 * Copyright (c) 2008 Institute for Software, HSR Hochschule für Technik  
 * Rapperswil, University of applied sciences
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 * 
 ******************************************************************************/
package ch.hsr.ifs.cutelauncher.ui;

import org.eclipse.cdt.managedbuilder.ui.wizards.MBSCustomPageManager;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.widgets.Composite;
public class CuteSuiteWizardHandler extends CuteWizardHandler {
	private final NewCuteSuiteWizardCustomPage suitewizPage;
	
	//for unit testing
	public CuteSuiteWizardHandler(String suitename){
		this(null,null);
		this.suitename=suitename;
	}
	public CuteSuiteWizardHandler(Composite p, IWizard w) {
		super( p, w);
		suitewizPage = new NewCuteSuiteWizardCustomPage(getConfigPage(), getStartingPage());
		suitewizPage.setPreviousPage(getStartingPage());
		suitewizPage.setWizard(getWizard());
		MBSCustomPageManager.init();
		MBSCustomPageManager.addStockPage(suitewizPage, suitewizPage.getPageID());
	}
	@Override
	public IWizardPage getSpecificPage() {
		return suitewizPage;
	}

	String suitename;
	
	@Override
	public void addTestFiles(IFolder folder, IProgressMonitor monitor) throws CoreException {
		suitename=suitewizPage.getSuiteName();
		copyFiles(folder,monitor);
	}
	//extract method for unit testing
	public void copyFiles(IFolder folder, IProgressMonitor monitor) throws CoreException {
		SuiteTemplateCopyUtil.copyFile(folder,monitor,"Test.cpp","Test.cpp",suitename);
		SuiteTemplateCopyUtil.copyFile(folder,monitor,"$suitename$.cpp",suitename+".cpp",suitename);
		SuiteTemplateCopyUtil.copyFile(folder,monitor,"$suitename$.h",suitename+".h",suitename);
	}
}
