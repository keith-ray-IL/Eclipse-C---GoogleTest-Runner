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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.jface.text.IRegion;

import ch.hsr.ifs.cutelauncher.CuteLauncherPlugin;
import ch.hsr.ifs.cutelauncher.event.TestEventHandler;

/**
 * @author egraf
 *
 */
public class ModellBuilder extends TestEventHandler {
	
	private CuteModel model = CuteLauncherPlugin.getModel();
	private IPath rtPath;
	private TestCase currentTestCase;
	private ILaunch launch;
		
	public ModellBuilder(IPath exePath, ILaunch launch) {
		super();
		this.rtPath = exePath;
		this.launch = launch; 
	}
	public ModellBuilder(IPath path) {
		this(path, null);
	}

	public void handleError(IRegion reg, String testName, String msg) {
		model.endCurrentTestCase(null, -1, msg, TestStatus.error, currentTestCase);
	}

	public void handleSuccess(IRegion reg, String name, String msg) {
		model.endCurrentTestCase(null, -1, msg, TestStatus.success, currentTestCase);	
	}

	public void handleSuiteEnding(IRegion reg, String suitename) {
		model.endSuite();
	}

	public void handleSuiteBeginning(IRegion reg, String suitename, String suitesize) {
		model.startSuite(new TestSuite(suitename, Integer.parseInt(suitesize), TestStatus.running));
	}

	public void handleFailure(IRegion reg, String testName, String fileName, String lineNo, String reason){
		IPath filePath=rtPath.append(fileName);
		IFile file = ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(filePath);
		int lineNumber = Integer.parseInt(lineNo);
		model.endCurrentTestCase(file, lineNumber, reason, TestStatus.failure, currentTestCase);
		
	}
	
	public void handleTestStart(IRegion reg, String suitename) {
		currentTestCase = new TestCase(suitename);
		model.addTest(currentTestCase);		
	}

	@Override
	public void handleSessionEnd() {
		model.endSession();
	}

	@Override
	public void handleSessionStart() {
		model.startSession(launch);
	}


}
