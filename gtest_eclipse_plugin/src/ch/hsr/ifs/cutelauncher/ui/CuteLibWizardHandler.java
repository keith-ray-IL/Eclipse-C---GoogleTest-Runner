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

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.eclipse.cdt.core.model.CoreModel;
import org.eclipse.cdt.core.settings.model.ICConfigurationDescription;
import org.eclipse.cdt.core.settings.model.ICOutputEntry;
import org.eclipse.cdt.core.settings.model.ICProjectDescription;
import org.eclipse.cdt.core.settings.model.ICSourceEntry;
import org.eclipse.cdt.managedbuilder.core.BuildException;
import org.eclipse.cdt.managedbuilder.core.IConfiguration;
import org.eclipse.cdt.managedbuilder.core.IManagedBuildInfo;
import org.eclipse.cdt.managedbuilder.core.IOption;
import org.eclipse.cdt.managedbuilder.core.ManagedBuildManager;
import org.eclipse.cdt.managedbuilder.ui.wizards.MBSCustomPageManager;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Emanuel Graf
 *
 */
public class CuteLibWizardHandler extends CuteWizardHandler {
	
	
	private final LibReferencePage libRefPage;
	public CuteLibWizardHandler(Composite p, IWizard w) {
		
		super( p, w);
		libRefPage = new LibReferencePage(getConfigPage(), getStartingPage(),w.getContainer());
		libRefPage.setPreviousPage(getStartingPage());
		libRefPage.setWizard(getWizard());
		MBSCustomPageManager.init();
		MBSCustomPageManager.addStockPage(libRefPage, libRefPage.getPageID());
	}

	@Override
	public void createProject(IProject project, boolean defaults)
			throws CoreException {
		super.createProject(project, defaults);
		createLibSetings(project);
//		createCDTProjectReference(project);
	}
	
	@Override
	public void createProject(IProject project, boolean defaults,
			boolean onFinish) throws CoreException {
		super.createProject(project, defaults, onFinish);
		createLibSetings(project);
//		createCDTProjectReference(project);
	}

	//https://bugs.eclipse.org/bugs/show_bug.cgi?id=229085
	@SuppressWarnings("unchecked")
	//@see org.eclipse.cdt.managedbuilder.core.tests/tests/org/eclipse/cdt/projectmodel/tests/ProjectModelTests.testReferences()
	protected void createCDTProjectReference(IProject project) throws CoreException {
		CoreModel coreModel = CoreModel.getDefault();
		ICProjectDescription des4 = coreModel.getProjectDescription(project);
		ICConfigurationDescription dess[] = des4.getConfigurations();
	
		Vector<IProject> projects = libRefPage.getCheckedProjects();

		Map map=null;
		for(int x=0;x<dess.length;x++){
			if(x==0){
				map= new HashMap();
				for(IProject p:projects){
					String prjname=p.getName();
					map.put(prjname, "");		
				}
			}
			dess[x].setReferenceInfo(map);
		}
		coreModel.setProjectDescription(project, des4);
		for(IProject p:projects){
			String prjname=p.getName();
			setLibName(prjname,project);	
		}
	}
	
	private void createLibSetings(IProject project) throws CoreException {
		Vector<IProject> projects = libRefPage.getCheckedProjects();
		for (IProject libProject : projects) {
			setToolChainIncludePath(project, libProject);
		}
		
		setProjectReference(project, projects);
		
		ManagedBuildManager.saveBuildInfo(project, true);
	}

	private void setProjectReference(IProject project, Vector<IProject> projects)
			throws CoreException {
		if(projects.size()>0){
			IProjectDescription desc = project.getDescription();
			IProject iproject[]=new IProject[projects.size()];
			
			for(int x=0;x<projects.size();x++){
				iproject[x]=projects.elementAt(x);
			}
			
			desc.setReferencedProjects(iproject);
			project.setDescription(desc, IResource.KEEP_HISTORY, new NullProgressMonitor());
		}
	}
	

	
	private void setToolChainIncludePath(IProject project, IProject libProject) throws CoreException {
		IManagedBuildInfo info = ManagedBuildManager.getBuildInfo(libProject);
		IConfiguration config = info.getDefaultConfiguration();
		ICSourceEntry[] sources = config.getSourceEntries();
		for (ICSourceEntry sourceEntry : sources) {
			IPath location = sourceEntry.getFullPath();
			if(location.segmentCount()== 0) {
				setIncludePaths(libProject.getFullPath(), project);
			}else {
				setIncludePaths(libProject.getFolder(location).getFullPath(), project);
			}
		}
		
		ICOutputEntry[]  dirs = config.getBuildData().getOutputDirectories();
		for (ICOutputEntry outputEntry : dirs) {
			IPath location = outputEntry.getFullPath();
			if(location.segmentCount()== 0){
				setLibraryPaths(libProject.getFullPath(), project);	
			}else{
				//IPath location1=location.removeFirstSegments(1);
				setLibraryPaths(libProject.getFolder(location).getFullPath(), project);	
			}
			setLibName(config.getArtifactName(), project);
		}
	}

	@Override
	public IWizardPage getSpecificPage() {
		
		return libRefPage;
	}

	protected void setLibraryPaths(IPath libFolder, IProject project)
			throws CoreException {
				String path = "\"${workspace_loc:" + libFolder.toPortableString() + "}\"";
				setOptionInAllConfigs(project, path, IOption.LIBRARY_PATHS);
			}
	
	protected void setLibName(String libName, IProject project) throws CoreException {
		setOptionInAllConfigs(project, libName, IOption.LIBRARIES);
	}

	
	
	@Override
	protected GetOptionsStrategy getStrategy(int optionType) {
		switch (optionType) {
		case IOption.LIBRARY_PATHS:
			return new LibraryPathsStrategy();
		
		case IOption.LIBRARIES:
			return new LibrariesStrategy();

		default:
			return super.getStrategy(optionType);
		}
		
	}

	//bugzilla #210116:on CDT spelling error
	@Override
	public boolean canFinish() {
		if(libRefPage ==null)return false;
		Vector<IProject> projects = libRefPage.getCheckedProjects();
		if(projects.size()<1)return false;
		return true;
	}

	private class LibraryPathsStrategy implements GetOptionsStrategy{

		public String[] getValues(IOption option) throws BuildException {
			return option.getBasicStringListValue();
		}
		
	}
	
	private class LibrariesStrategy implements GetOptionsStrategy{

		public String[] getValues(IOption option) throws BuildException {
			return option.getLibraries();
		}
		
	}
}
//to convert IFolder to IPath use (IResource)IFolder.getFullPath()
