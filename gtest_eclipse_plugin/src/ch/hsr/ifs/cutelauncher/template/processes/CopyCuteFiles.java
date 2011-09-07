/*******************************************************************************
 * Copyright (c) 2007 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Emanuel Graf - initial API and implementation
 ******************************************************************************/
package ch.hsr.ifs.cutelauncher.template.processes;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.eclipse.cdt.core.templateengine.TemplateCore;
import org.eclipse.cdt.core.templateengine.TemplateEngineHelper;
import org.eclipse.cdt.core.templateengine.process.ProcessArgument;
import org.eclipse.cdt.core.templateengine.process.ProcessFailureException;
import org.eclipse.cdt.core.templateengine.process.ProcessHelper;
import org.eclipse.cdt.core.templateengine.process.ProcessRunner;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;

/**
 * @author Emanuel Graf
 *
 */
public class CopyCuteFiles extends ProcessRunner {

	@Override
	public void process(TemplateCore template, ProcessArgument[] args,
			String processId, IProgressMonitor monitor)
	throws ProcessFailureException {

		String projectName = args[0].getSimpleValue();
		IProject projectHandle = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
		String sourceDir = args[1].getSimpleValue();
		String targetDir = args[2].getSimpleValue();

		URL path;
		try {
			path = TemplateEngineHelper.getTemplateResourceURLRelativeToTemplate(template, sourceDir);
			if (path == null) {
				throw new ProcessFailureException(getProcessMessage(processId, IStatus.ERROR, "Copy Cute files failure: template source not found:" + sourceDir)); //$NON-NLS-1$
			}
		} catch (IOException e1) {
			throw new ProcessFailureException("Copy Cute files failure: template source not found: " + sourceDir);
		}

		File[] filenames = getFiles(path);

		for (File file : filenames) {
			
			InputStream contents = null;
			try {
				contents = new FileInputStream(file);
				copyFile(projectHandle, targetDir, file, contents);
				projectHandle.refreshLocal(IResource.DEPTH_INFINITE, null);
			} catch (IOException e) {
				throw new ProcessFailureException(getProcessMessage(processId, IStatus.ERROR, "Could not open File: " + file.getAbsolutePath()));
			} catch (CoreException e) {
				throw new ProcessFailureException("Could not write File: " + e.getMessage(), e);
			}
		}
		
	}

	private void copyFile(IProject projectHandle, String targetDir, File file,
			InputStream contents) throws CoreException {
		IFile iFile = projectHandle.getFile(targetDir + "/" + file.getName());
		if (!iFile.getParent().exists()) {
			ProcessHelper.mkdirs(projectHandle, projectHandle.getFolder(iFile.getParent().getProjectRelativePath()));
		}

		if (iFile.exists()) {
				iFile.setContents(contents, true, true, null);
		} else {
			iFile.create(contents, true, null);
			iFile.refreshLocal(IResource.DEPTH_ONE, null);
		}
	}

	private File[] getFiles(URL path) throws ProcessFailureException {
		File file = new File(path.getFile());
		if(file.isDirectory()) {
			return file.listFiles(new FilenameFilter() {

				public boolean accept(File dir, String name) {
					return name.endsWith(".h");
				}});
		}else {
			throw new ProcessFailureException("Source is not a Direcotry");
		}
	}

}
