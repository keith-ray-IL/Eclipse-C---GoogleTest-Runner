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

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.cdt.core.CCorePlugin;
import org.eclipse.cdt.core.settings.model.CSourceEntry;
import org.eclipse.cdt.core.settings.model.ICConfigurationDescription;
import org.eclipse.cdt.core.settings.model.ICProjectDescription;
import org.eclipse.cdt.core.settings.model.ICSourceEntry;
import org.eclipse.cdt.core.settings.model.WriteAccessException;
import org.eclipse.cdt.managedbuilder.core.BuildException;
import org.eclipse.cdt.managedbuilder.core.IConfiguration;
import org.eclipse.cdt.managedbuilder.core.IHoldsOptions;
import org.eclipse.cdt.managedbuilder.core.IManagedBuildInfo;
import org.eclipse.cdt.managedbuilder.core.IOption;
import org.eclipse.cdt.managedbuilder.core.ITool;
import org.eclipse.cdt.managedbuilder.core.IToolChain;
import org.eclipse.cdt.managedbuilder.core.ManagedBuildManager;
import org.eclipse.cdt.managedbuilder.ui.wizards.MBSWizardHandler;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

import ch.hsr.ifs.cutelauncher.CuteLauncherPlugin;
import ch.hsr.ifs.cutelauncher.CuteNature;

/**
 * @author Emanuel Graf
 *
 */
public class CuteWizardHandler extends MBSWizardHandler {
	public CuteWizardHandler(Composite p, IWizard w) {
		super(new CuteBuildPropertyValue(), p, w);
	}
	
	@Override
	public void createProject(IProject project, boolean defaults,
			boolean onFinish) throws CoreException {
		super.createProject(project, defaults, onFinish);
		createCuteProject(project);
	}

	@Override
	public void createProject(IProject project, boolean defaults)
			throws CoreException {
		super.createProject(project, defaults);
		createCuteProject(project);
	}

	private void createCuteProject(IProject project) throws CoreException {
		CuteNature.addCuteNature(project, new NullProgressMonitor());
		createCuteProjectFolders(project);
	}
	
	protected void createCuteProjectFolders(IProject project)
			throws CoreException {
		IFolder srcFolder = createFolder(project, "src");
		addTestFiles(srcFolder, new NullProgressMonitor());
		IFolder cuteFolder = createFolder(project, "cute");
		addCuteFiles(cuteFolder, new NullProgressMonitor());
		setIncludePaths(cuteFolder.getFullPath(), project);
		
		ManagedBuildManager.saveBuildInfo(project, true);
		IDE.openEditor(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage(),
				getTestMainFile(project), true);
	}
	
	private IFolder createFolder(IProject project, String relPath)
			throws CoreException {
		IFolder folder= project.getProject().getFolder(relPath);
		if (!folder.exists()) {
			createFolder(folder, true, true, new NullProgressMonitor());
		}
				
		if(CCorePlugin.getDefault().isNewStyleProject(project.getProject())){
			ICSourceEntry newEntry = new CSourceEntry(folder, null, 0);
			ICProjectDescription des = CCorePlugin.getDefault().getProjectDescription(project.getProject(), true);
			addEntryToAllCfgs(des, newEntry, false);
			CCorePlugin.getDefault().setProjectDescription(project.getProject(), des, false, new NullProgressMonitor());
		}
		return folder;
	}

	private void createFolder(IFolder folder, boolean force, boolean local, IProgressMonitor monitor) throws CoreException {
		if (!folder.exists()) {
			IContainer parent= folder.getParent();
			if (parent instanceof IFolder) {
				createFolder((IFolder)parent, force, local, null);
			}
			folder.create(force, local, monitor);
		}
	}
	
	private void addEntryToAllCfgs(ICProjectDescription des, ICSourceEntry entry, boolean removeProj) throws WriteAccessException, CoreException{
		ICConfigurationDescription cfgs[] = des.getConfigurations();
		for(int i = 0; i < cfgs.length; i++){
			ICConfigurationDescription cfg = cfgs[i];
			ICSourceEntry[] entries = cfg.getSourceEntries();
			entries = addEntry(entries, entry, removeProj);
			cfg.setSourceEntries(entries);
		}
	}
		
	private ICSourceEntry[] addEntry(ICSourceEntry[] entries, ICSourceEntry entry, boolean removeProj){
		Set<ICSourceEntry> set = new HashSet<ICSourceEntry>();
		for(int i = 0; i < entries.length; i++){
			if(removeProj && new Path(entries[i].getValue()).segmentCount() == 1)
				continue;
			
			set.add(entries[i]);
		}
		set.add(entry);
		return set.toArray(new ICSourceEntry[set.size()]);
	}
	
	@SuppressWarnings("unchecked")
	private void addCuteFiles(IFolder folder, IProgressMonitor monitor) throws CoreException {
		Enumeration en = CuteLauncherPlugin.getDefault().getBundle().findEntries("templates/projecttemplates/cute", "*.h", false);
		while(en.hasMoreElements()) {
			URL url = (URL)en.nextElement();
			String[] elements = url.getFile().split("/");
			String filename = elements[elements.length-1];
			IFile targetFile = folder.getFile(filename);
			try {
				targetFile.create(url.openStream(),IResource.FORCE , new SubProgressMonitor(monitor,1));
			} catch (IOException e) {
				throw new CoreException(new Status(IStatus.ERROR,CuteLauncherPlugin.PLUGIN_ID,42,e.getMessage(), e));
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	protected void addTestFiles(IFolder folder, IProgressMonitor monitor) throws CoreException {
		Enumeration en = CuteLauncherPlugin.getDefault().getBundle().findEntries("templates/projecttemplates/src", "*.cpp", false);
		while(en.hasMoreElements()) {
			URL url = (URL)en.nextElement();
			String[] elements = url.getFile().split("/");
			String filename = elements[elements.length-1];
			IFile targetFile = folder.getFile(filename);
			try {
				targetFile.create(url.openStream(),IResource.FORCE , new SubProgressMonitor(monitor,1));
			} catch (IOException e) {
				throw new CoreException(new Status(IStatus.ERROR,CuteLauncherPlugin.PLUGIN_ID,42,e.getMessage(), e));
			}
		}
	}
	
	protected void setIncludePaths(IPath cuteFolder, IProject project) throws CoreException {
		String path = "\"${workspace_loc:" + cuteFolder.toPortableString() + "}\"";
		setOptionInAllConfigs(project, path, IOption.INCLUDE_PATH);
	}

	protected void setOptionInAllConfigs(IProject project, String value, int optionType)
			throws CoreException {
		IManagedBuildInfo info = ManagedBuildManager.getBuildInfo(project);
		IConfiguration[] configs = info.getManagedProject().getConfigurations();
		try{
			for(IConfiguration conf : configs){
				IToolChain toolChain = conf.getToolChain();
				setOptionInConfig(value, conf, toolChain.getOptions(), toolChain, optionType);

				ITool[] tools = conf.getTools();
				for(int j=0; j<tools.length; j++) {
					setOptionInConfig(value, conf, tools[j].getOptions(), tools[j], optionType);
				}
			}
		}catch (BuildException be){
			throw new CoreException(new Status(IStatus.ERROR,CuteLauncherPlugin.PLUGIN_ID,42,be.getMessage(), be));
		}
	}
	
	protected void setOptionInConfig(String value, IConfiguration config,
			IOption[] options, IHoldsOptions optionHolder, int optionType) throws BuildException {
		for (int i = 0; i < options.length; i++) {
			IOption option = options[i];
			if (option.getValueType() == optionType) {
				String[] includePaths = getStrategy(optionType).getValues(option);
				String[] newPaths = new String[includePaths.length + 1];
				System.arraycopy(includePaths, 0, newPaths, 0, includePaths.length);
				newPaths[includePaths.length] = value;
				ManagedBuildManager.setOption(config, optionHolder, option, newPaths);
			}
		}
	}

	protected IFile getTestMainFile(IProject project) {
		return project.getFile("src/Test.cpp");
	}
	
	protected GetOptionsStrategy getStrategy(int optionType) {
		switch (optionType) {
		case IOption.INCLUDE_PATH:
			return new IncludePathStrategy();

		default:
			throw new IllegalArgumentException("Illegal Argument: "+optionType);
		}
	}

	private class IncludePathStrategy implements GetOptionsStrategy{

		public String[] getValues(IOption option) throws BuildException {
			return option.getIncludePaths();
		}
		
	}


}
