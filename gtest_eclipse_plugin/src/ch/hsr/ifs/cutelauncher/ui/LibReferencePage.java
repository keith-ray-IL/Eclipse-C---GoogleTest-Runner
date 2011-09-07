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
package ch.hsr.ifs.cutelauncher.ui;

import java.util.Vector;

import org.eclipse.cdt.managedbuilder.buildproperties.IBuildProperty;
import org.eclipse.cdt.managedbuilder.core.IConfiguration;
import org.eclipse.cdt.managedbuilder.core.IManagedBuildInfo;
import org.eclipse.cdt.managedbuilder.core.ManagedBuildManager;
import org.eclipse.cdt.managedbuilder.ui.wizards.CDTConfigWizardPage;
import org.eclipse.cdt.managedbuilder.ui.wizards.MBSCustomPage;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.wizard.IWizardContainer;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.model.WorkbenchLabelProvider;

/**
 * @author Emanuel Graf
 *
 */
public class LibReferencePage extends MBSCustomPage implements ICheckStateListener{
	
	private Composite composite;
	private final CDTConfigWizardPage configPage;

    private CheckboxTableViewer listViewer;
	private Vector<IProject> libProjects;
	private final IWizardPage startingWizardPage;
	private final IWizardContainer wizardDialog;
	
	public LibReferencePage(CDTConfigWizardPage configWizardPage, IWizardPage staringWizardPage, IWizardContainer wc) {
		pageID = "ch.hsr.ifs.cutelauncher.ui.LibRefPage";
		this.configPage = configWizardPage;
		this.startingWizardPage = staringWizardPage;
		wizardDialog=wc;
	}

	@Override
	protected boolean isCustomPageComplete() {
		// TODO Auto-generated method stub
		if(getCheckedProjects().size()<1)return false;
		return true;
	}

	public String getName() {
		return "Reference to Library Page";
	}

	public void createControl(Composite parent) {
		libProjects = getLibProjects();
		composite = new Composite(parent, SWT.NULL);
		composite.setLayout(new GridLayout());
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));

		listViewer = CheckboxTableViewer.newCheckList(composite, SWT.TOP
                | SWT.BORDER);
        GridData data = new GridData(GridData.FILL_BOTH);
        data.grabExcessHorizontalSpace = true;
        listViewer.getTable().setLayoutData(data);
        
        listViewer.setLabelProvider(WorkbenchLabelProvider
                .getDecoratingWorkbenchLabelProvider());
        listViewer.setContentProvider(getContentProvider());
        listViewer.setComparator(new ViewerComparator());
		listViewer.setInput(libProjects);
        listViewer.addCheckStateListener(this);
	}
	
	private IContentProvider getContentProvider() {
		return new IStructuredContentProvider() {

			
			public void dispose() {
				// TODO Auto-generated method stub
				
			}

			public void inputChanged(Viewer viewer, Object oldInput,
					Object newInput) {
				// TODO Auto-generated method stub
				
			}

			@SuppressWarnings("unchecked")
			public Object[] getElements(Object inputElement) {
				if (inputElement instanceof Vector) {
					Vector vec = (Vector) inputElement;
					return vec.toArray();
				}
				return null;
			}
			
		};
	}

	private Vector<IProject> getLibProjects() {
		Vector<IProject> libProjects = new Vector<IProject>();
		IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		for (IProject project : projects) {
			IManagedBuildInfo info = ManagedBuildManager.getBuildInfo(project);
			if(info != null) {
				IConfiguration[] configs = info.getManagedProject().getConfigurations();
				IBuildProperty artifactType = configs[0].getBuildProperties().getProperty(ManagedBuildManager.BUILD_ARTEFACT_TYPE_PROPERTY_ID);
				if(artifactType != null) {
					String artifactTypeName = artifactType.getValue().getId();
					if(artifactTypeName.equals(ManagedBuildManager.BUILD_ARTEFACT_TYPE_PROPERTY_SHAREDLIB)||
							artifactTypeName.equals(ManagedBuildManager.BUILD_ARTEFACT_TYPE_PROPERTY_STATICLIB)) {
						libProjects.add(project);
					}
				}
			}
		}
		return libProjects;
	}

	public void dispose()
	{
		composite.dispose();

	}

	public Control getControl()
	{
		return composite;
	}

	public String getDescription()
	{
		return new String("Choose the Library Project to test");
	}

	boolean errorMessageFlag=false;
	public String getErrorMessage()
	{
		if(errorMessageFlag)return "Please Select Library for testing";
		return null;
	}

	public Image getImage()
	{
		return wizard.getDefaultPageImage();
	}

	public String getMessage()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public String getTitle()
	{
		return new String("Library Project Test");
	}

	public void performHelp()
	{
		// do nothing

	}

	public void setDescription(String description)
	{
		// do nothing

	}

	public void setImageDescriptor(ImageDescriptor image)
	{
		// do nothing

	}

	public void setTitle(String title)
	{
		// do nothing

	}

	public void setVisible(boolean visible)
	{
		composite.setVisible(visible);

	}

	@Override
	public IWizardPage getNextPage() {
		return configPage;
	}

	@Override
	public IWizardPage getPreviousPage() {
		return startingWizardPage;
	}

	public Vector<IProject> getCheckedProjects() {
		Vector<IProject> checkedProjects = new Vector<IProject>();
		if(listViewer==null)return checkedProjects;
		
		for(Object obj :listViewer.getCheckedElements()) {
			if (obj instanceof IProject) {
				checkedProjects.add((IProject) obj);
				
			}
		}
		return checkedProjects;
	}
	
	public void checkStateChanged(CheckStateChangedEvent event){
		Vector<IProject> list=getCheckedProjects();
		if(list.size()<1)errorMessageFlag=true;
		else errorMessageFlag=false;
		
		wizardDialog.updateMessage();
		wizardDialog.updateButtons();
	}

}
