/*******************************************************************************
 * Copyright (c) 2007 Institute for Software, HSR Hochschule für Technik  
 * Rapperswil, University of applied sciences
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 * 
 ******************************************************************************/
package ch.hsr.ifs.cutelauncher.ui;

import org.eclipse.cdt.launch.ui.CLaunchConfigurationTab;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.internal.ui.DebugUIPlugin;
import org.eclipse.debug.internal.ui.SWTFactory;
import org.eclipse.debug.internal.ui.launchConfigurations.LaunchConfigurationsMessages;
import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ContainerSelectionDialog;

import ch.hsr.ifs.cutelauncher.CuteLauncherPlugin;
import ch.hsr.ifs.cutelauncher.model.CuteModel;

public class CustomisedLaunchConfigTab extends CLaunchConfigurationTab {
	public static final String CUSTOM_SRC_PATH = "customSrcPath";
	public static final String USE_CUSTOM_SRC_PATH = "useCustomSrcPath";
	protected Label descriptionLabel;
	private Button fLocalRadioButton;
	private Button fCustomSrcRadioButton;
	private Text fCustomSrcLocationText;
	private Button fCustomSrcLocationButton;
	
	protected CuteModel cm=CuteLauncherPlugin.getModel();
	
	public void createControl(Composite parent) {
	    Font font = parent.getFont();
	    Composite comp = new Composite(parent, SWT.NONE);
	    createVerticalSpacer(comp, 1);
	    
	    GridLayout layout = new GridLayout(1, true);
		layout.numColumns = 2;
		layout.marginHeight = 0;
		layout.marginWidth = 5;
		comp.setLayout(layout);
	    comp.setFont(font);
	    GridData gd;
	    	    
	    Group group = SWTFactory.createGroup(comp, "Source Folder Selection", 3, 2, GridData.FILL_HORIZONTAL);
		Composite n_comp = SWTFactory.createComposite(group, parent.getFont(), 3, 3, GridData.FILL_BOTH, 0, 0);
		descriptionLabel=new Label(n_comp,SWT.NONE);
	    descriptionLabel.setText("This is for unmanaged project to set the source folder for Cute Plug-in to scan.");
	    gd = new GridData();
		gd.horizontalSpan = 3;
		descriptionLabel.setLayoutData(gd);
	    
		fLocalRadioButton = createRadioButton(n_comp, "Default");
		gd = new GridData();
		gd.horizontalSpan = 3;
		fLocalRadioButton.setLayoutData(gd);
		
		fCustomSrcRadioButton = createRadioButton(n_comp, "Custom path:");
		fCustomSrcRadioButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent evt) {
				handleSharedRadioButtonSelected();
			}
		});
		fCustomSrcLocationText = SWTFactory.createSingleText(n_comp, 1);
		fCustomSrcLocationText.addModifyListener(fBasicModifyListener);
		fCustomSrcLocationButton = createPushButton(n_comp, LaunchConfigurationsMessages.CommonTab__Browse_6, null);	 
		fCustomSrcLocationButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent evt) {
				handleSharedLocationButtonSelected();
			}
		});	
	    setControl(comp);
	}
	/**
	 * Modify listener that simply updates the owning launch configuration dialog.
	 */
	private final ModifyListener fBasicModifyListener = new ModifyListener() {
		public void modifyText(ModifyEvent evt) {
			updateLaunchConfigurationDialog();
		}
	};
	/**
	 * handles the shared radio button being selected
	 */
	private void handleSharedRadioButtonSelected() {
		setSharedEnabled(isShared());
		updateLaunchConfigurationDialog();
	}
	/**
	 * Sets the widgets for specifying that a launch configuration is to be shared to the enable value
	 * @param enable the enabled value for 
	 */
	private void setSharedEnabled(boolean enable) {
		fCustomSrcLocationText.setEnabled(enable);
		fCustomSrcLocationButton.setEnabled(enable);
	}
	/**
	 * if the shared radio button is selected, indicating that the launch configuration is to be shared
	 * @return true if the radio button is selected, false otherwise
	 */
	private boolean isShared() {
		return fCustomSrcRadioButton.getSelection();
	}

	//////////////////////////////
	/// based on CommonTab
//	@see org.eclipse.debug.ui.AbstractLaunchConfigurationTabGroup
//	@see org.eclipse.cdt.launch.internal.ui.LocalRunLaunchConfigurationTabGroup
//	@see org.eclipse.debug.ui.CommonTab
//	see extension org.eclipse.debug.ui.launchConfigurationTabGroups
	/**
	 * Handles the shared location button being selected
	 */
	private void handleSharedLocationButtonSelected() { 
		String currentContainerString = fCustomSrcLocationText.getText();
		IContainer currentContainer = getContainer(currentContainerString);
		SharedLocationSelectionDialog dialog = new SharedLocationSelectionDialog(getShell(),
				   currentContainer,
				   false,
				   LaunchConfigurationsMessages.CommonTab_Select_a_location_for_the_launch_configuration_13);
		dialog.showClosedProjects(false);
		dialog.open();
		Object[] results = dialog.getResult();	
		if ((results != null) && (results.length > 0) && (results[0] instanceof IPath)) {
			IPath path = (IPath)results[0];
			String containerName = path.toOSString();
			fCustomSrcLocationText.setText(containerName);
		}		
	}
	/**
	 * gets the container form the specified path
	 * @param path the path to get the container from
	 * @return the container for the specified path or null if one cannot be determined
	 */
	private IContainer getContainer(String path) {
		Path containerPath = new Path(path);
		return (IContainer) ResourcesPlugin.getWorkspace().getRoot().findMember(containerPath);
	}
	/**
	 * Provides a persistable dialog for selecting the shared project location
	 * @since 3.2
	 */
	class SharedLocationSelectionDialog extends ContainerSelectionDialog {
		private final String SETTINGS_ID = IDebugUIConstants.PLUGIN_ID + ".SHARED_LAUNCH_CONFIGURATON_DIALOG"; //$NON-NLS-1$
		
		public SharedLocationSelectionDialog(Shell parentShell, IContainer initialRoot, boolean allowNewContainerName, String message) {
			super(parentShell, initialRoot, allowNewContainerName, message);
		}

		@Override
		protected IDialogSettings getDialogBoundsSettings() {
			IDialogSettings settings = DebugUIPlugin.getDefault().getDialogSettings();
			IDialogSettings section = settings.getSection(SETTINGS_ID);
			if (section == null) {
				section = settings.addNewSection(SETTINGS_ID);
			} 
			return section;
		}
	}
	//////////////////////////////
	public String getName() {
		return "Source lookup path";
	}

	public void initializeFrom(ILaunchConfiguration configuration) {
		try{
		boolean flag=configuration.getAttribute(USE_CUSTOM_SRC_PATH, false);
		if(flag){
			fCustomSrcRadioButton.setSelection(true);
			setSharedEnabled(true);
			fCustomSrcLocationText.setText(configuration.getAttribute(CUSTOM_SRC_PATH,""));
		}else{
			fLocalRadioButton.setSelection(true);
			setSharedEnabled(false);
		}
		}catch(CoreException ce){CuteLauncherPlugin.getDefault().getLog().log(ce.getStatus());}
	}
	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		configuration.setAttribute(USE_CUSTOM_SRC_PATH,isShared());
		configuration.setAttribute(CUSTOM_SRC_PATH,fCustomSrcLocationText.getText());
	}
	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
		configuration.setAttribute(USE_CUSTOM_SRC_PATH,false);
	}
	@Override
	public boolean isValid(ILaunchConfiguration launchConfig) {
		if(isShared() && fCustomSrcLocationText.getText().equals("")){
			setErrorMessage("No source path selected.");
			return false;
		}
		return true;
	}
}
