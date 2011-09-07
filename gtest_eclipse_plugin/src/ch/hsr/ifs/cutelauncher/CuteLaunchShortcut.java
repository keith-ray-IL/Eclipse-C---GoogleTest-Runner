/*******************************************************************************
 * Copyright (c) 2005, 2006 QNX Software Systems and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * QNX Software Systems - Initial API and implementation
 * Institue for Software Emanuel Graf - Adaption for CUTE
 *******************************************************************************/
package ch.hsr.ifs.cutelauncher;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.cdt.core.model.CModelException;
import org.eclipse.cdt.core.model.CoreModel;
import org.eclipse.cdt.core.model.IBinary;
import org.eclipse.cdt.core.model.ICProject;
import org.eclipse.cdt.debug.core.CDebugCorePlugin;
import org.eclipse.cdt.debug.core.ICDTLaunchConfigurationConstants;
import org.eclipse.cdt.debug.core.ICDebugConfiguration;
import org.eclipse.cdt.launch.AbstractCLaunchDelegate;
import org.eclipse.cdt.launch.internal.ui.LaunchMessages;
import org.eclipse.cdt.launch.internal.ui.LaunchUIPlugin;
import org.eclipse.cdt.ui.CElementLabelProvider;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.debug.ui.IDebugModelPresentation;
import org.eclipse.debug.ui.ILaunchShortcut;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.eclipse.ui.dialogs.TwoPaneElementSelector;

public class CuteLaunchShortcut implements ILaunchShortcut {

	public void launch(IEditorPart editor, String mode) {
		searchAndLaunch(new Object[] { editor.getEditorInput()}, mode);
	}
	//project explorer exe
	public void launch(ISelection selection, String mode) {
		if (selection instanceof IStructuredSelection) {
			searchAndLaunch(((IStructuredSelection) selection).toArray(), mode);
		}
	}
	//internal to CuteLaunchShortcut
	public void launch(IBinary bin, String mode) {
        ILaunchConfiguration config = findLaunchConfiguration(bin, mode);
        if (config != null) {
            DebugUITools.launch(config, mode);
        }
    }

	/**
	 * Locate a configuration to relaunch for the given type.  If one cannot be found, create one.
	 * 
	 * @return a re-useable config or <code>null</code> if none
	 */
	protected ILaunchConfiguration findLaunchConfiguration(IBinary bin, String mode) {
		ILaunchConfiguration configuration = null;
		ILaunchConfigurationType configType = getCuteLaunchConfigType();
		
		List<ILaunchConfiguration> candidateConfigs = Collections.emptyList();
		try {
			ILaunchConfiguration[] configs = DebugPlugin.getDefault().getLaunchManager().getLaunchConfigurations(configType);
			candidateConfigs = createCandidateConfigList(bin, configs);
		} catch (CoreException e) {
			LaunchUIPlugin.log(e);
		}

		// If there are no existing configs associated with the IBinary, create one.
		// If there is exactly one config associated with the IBinary, return it.
		// Otherwise, if there is more than one config associated with the IBinary, prompt the
		// user to choose one.
		int candidateCount = candidateConfigs.size();
		if (candidateCount < 1) {
			String programCPU = bin.getCPU();
			// Try default debugger first
			ICDebugConfiguration defaultConfig = CDebugCorePlugin.getDefault().getDefaultDebugConfiguration();
			String os = Platform.getOS();
			ICDebugConfiguration debugConfig = null;
			if ( defaultConfig != null ) {
				String platform = defaultConfig.getPlatform();
				if (defaultConfig.supportsMode(ICDTLaunchConfigurationConstants.DEBUGGER_MODE_RUN)) {
					if (platform.equals("*") || platform.equals(os)) { //$NON-NLS-1$
						if (defaultConfig.supportsCPU(programCPU))
							debugConfig = defaultConfig;
					}
				}
			}
			if ( debugConfig == null ) {
				// Prompt the user if more then 1 debugger.
				ICDebugConfiguration[] debugConfigs = CDebugCorePlugin.getDefault().getActiveDebugConfigurations();
				List<ICDebugConfiguration> debugList = new ArrayList<ICDebugConfiguration>(debugConfigs.length);
				for (int i = 0; i < debugConfigs.length; i++) {
					String platform = debugConfigs[i].getPlatform();
					if (debugConfigs[i].supportsMode(ICDTLaunchConfigurationConstants.DEBUGGER_MODE_RUN)) {
						if (platform.equals("*") || platform.equals(os)) { //$NON-NLS-1$
							if (debugConfigs[i].supportsCPU(programCPU))
								debugList.add(debugConfigs[i]);
						}
					}
				}
				debugConfigs = debugList.toArray(new ICDebugConfiguration[0]);
				if (debugConfigs.length == 1) {
					debugConfig = debugConfigs[0];
				} else if (debugConfigs.length > 1) {
					debugConfig = chooseDebugConfig(debugConfigs, mode);
				}
			}
			if (debugConfig != null) {
				configuration = createConfiguration(bin, debugConfig);
			}
		} else if (candidateCount == 1) {
			configuration = candidateConfigs.get(0);
		} else {
			// Prompt the user to choose a config.  A null result means the user
			// cancelled the dialog, in which case this method returns null,
			// since cancelling the dialog should also cancel launching anything.
			configuration = chooseConfiguration(candidateConfigs, mode);
		}
		return configuration;
	}

	private List<ILaunchConfiguration> createCandidateConfigList(IBinary bin,
			ILaunchConfiguration[] configs) throws CoreException {
		
		List<ILaunchConfiguration> candidateConfigs;
		candidateConfigs = new ArrayList<ILaunchConfiguration>(configs.length);
		
		for (int i = 0; i < configs.length; i++) {
			ILaunchConfiguration config = configs[i];
			IPath programPath = AbstractCLaunchDelegate.getProgramPath(config);
			String projectName = AbstractCLaunchDelegate.getProjectName(config);
			IPath name = bin.getResource().getProjectRelativePath();
			if (programPath != null && programPath.equals(name)) {
				if (projectName != null && projectName.equals(bin.getCProject().getProject().getName())) {
					candidateConfigs.add(config);
				}
			}
		}
		return candidateConfigs;
	}

	/**
	 * Method createConfiguration.
	 * @param bin
	 * @return ILaunchConfiguration
	 */
	private ILaunchConfiguration createConfiguration(IBinary bin, ICDebugConfiguration debugConfig) {
		ILaunchConfiguration config = null;
		try {
			String projectName = bin.getResource().getProjectRelativePath().toString();
			ILaunchConfigurationType configType = getCuteLaunchConfigType();
			ILaunchConfigurationWorkingCopy wc =
				configType.newInstance(null, getLaunchManager().generateUniqueLaunchConfigurationNameFrom(bin.getElementName()));
			wc.setAttribute(ICDTLaunchConfigurationConstants.ATTR_PROGRAM_NAME, projectName);
			wc.setAttribute(ICDTLaunchConfigurationConstants.ATTR_PROJECT_NAME, bin.getCProject().getElementName());
			wc.setAttribute(ICDTLaunchConfigurationConstants.ATTR_WORKING_DIRECTORY, (String) null);
			wc.setAttribute(ICDTLaunchConfigurationConstants.ATTR_DEBUGGER_STOP_AT_MAIN, true);
			wc.setAttribute(
				ICDTLaunchConfigurationConstants.ATTR_DEBUGGER_START_MODE,
				ICDTLaunchConfigurationConstants.DEBUGGER_MODE_RUN);
			wc.setAttribute(ICDTLaunchConfigurationConstants.ATTR_DEBUGGER_ID, debugConfig.getID());
			
			ICProject project=bin.getCProject();

			LaunchEnvironmentVariables.apply(wc,project);
						
			config = wc.doSave();
		} catch (CoreException ce) {
			CuteLauncherPlugin.log(ce);
		}
		return config;
	}
	
	/**
	 * Method getCLaunchConfigType.
	 * @return ILaunchConfigurationType
	 */
	protected ILaunchConfigurationType getCuteLaunchConfigType() {
		return getLaunchManager().getLaunchConfigurationType("ch.hsr.ifs.cutelauncher.launchConfig");
	}

	protected ILaunchManager getLaunchManager() {
		return DebugPlugin.getDefault().getLaunchManager();
	}

	/**
	 * Convenience method to get the window that owns this action's Shell.
	 */
	protected Shell getShell() {
		return LaunchUIPlugin.getActiveWorkbenchShell();
	}

	/**
	 * Method chooseDebugConfig.
	 * @param debugConfigs
	 * @param mode
	 * @return ICDebugConfiguration
	 */
	private ICDebugConfiguration chooseDebugConfig(ICDebugConfiguration[] debugConfigs, String mode) {
		ILabelProvider provider = new LabelProvider() {
			/**
			 * The <code>LabelProvider</code> implementation of this
			 * <code>ILabelProvider</code> method returns the element's <code>toString</code>
			 * string. Subclasses may override.
			 */
			@Override
			public String getText(Object element) {
				if (element == null) {
					return ""; //$NON-NLS-1$
				} else if (element instanceof ICDebugConfiguration) {
					return ((ICDebugConfiguration) element).getName();
				}
				return element.toString();
			}
		};
		ElementListSelectionDialog dialog = new ElementListSelectionDialog(getShell(), provider);
		dialog.setElements(debugConfigs);
		dialog.setTitle(getDebugConfigDialogTitleString(debugConfigs, mode));
		dialog.setMessage(getDebugConfigDialogMessageString(debugConfigs, mode));
		dialog.setMultipleSelection(false);
		int result = dialog.open();
		provider.dispose();
		if (result == Window.OK) {
			return (ICDebugConfiguration) dialog.getFirstResult();
		}
		return null;
	}

	protected String getDebugConfigDialogTitleString(ICDebugConfiguration [] configList, String mode) {
		return LaunchMessages.getString("CApplicationLaunchShortcut.LaunchDebugConfigSelection");  //$NON-NLS-1$
	}
	
	protected String getDebugConfigDialogMessageString(ICDebugConfiguration [] configList, String mode) {
		if (mode.equals(ILaunchManager.DEBUG_MODE)) {
			return LaunchMessages.getString("CApplicationLaunchShortcut.ChooseConfigToDebug");  //$NON-NLS-1$
		} else if (mode.equals(ILaunchManager.RUN_MODE)) {
			return LaunchMessages.getString("CApplicationLaunchShortcut.ChooseConfigToRun");  //$NON-NLS-1$
		}
		return LaunchMessages.getString("CApplicationLaunchShortcut.Invalid_launch_mode_1"); //$NON-NLS-1$
	}


	/**
	 * Show a selection dialog that allows the user to choose one of the specified
	 * launch configurations.  Return the chosen config, or <code>null</code> if the
	 * user cancelled the dialog.
	 */
	@SuppressWarnings("unchecked")
	protected ILaunchConfiguration chooseConfiguration(List configList, String mode) {
		IDebugModelPresentation labelProvider = DebugUITools.newDebugModelPresentation();
		ElementListSelectionDialog dialog = new ElementListSelectionDialog(getShell(), labelProvider);
		dialog.setElements(configList.toArray());
		dialog.setTitle(getLaunchSelectionDialogTitleString(configList, mode));
		dialog.setMessage(getLaunchSelectionDialogMessageString(configList, mode));
		dialog.setMultipleSelection(false);
		int result = dialog.open();
		labelProvider.dispose();
		if (result == Window.OK) {
			return (ILaunchConfiguration) dialog.getFirstResult();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	protected String getLaunchSelectionDialogTitleString(List configList, String mode) {
		return LaunchMessages.getString("CApplicationLaunchShortcut.LaunchConfigSelection");  //$NON-NLS-1$
	}
	
	@SuppressWarnings("unchecked")
	protected String getLaunchSelectionDialogMessageString(List binList, String mode) {
		if (mode.equals(ILaunchManager.DEBUG_MODE)) {
			return LaunchMessages.getString("CApplicationLaunchShortcut.ChooseLaunchConfigToDebug");  //$NON-NLS-1$
		} else if (mode.equals(ILaunchManager.RUN_MODE)) {
			return LaunchMessages.getString("CApplicationLaunchShortcut.ChooseLaunchConfigToRun");  //$NON-NLS-1$
		}
		return LaunchMessages.getString("CApplicationLaunchShortcut.Invalid_launch_mode_2"); //$NON-NLS-1$
	}

	/**
	 * Prompts the user to select a  binary
	 * 
	 * @return the selected binary or <code>null</code> if none.
	 */
	@SuppressWarnings("unchecked")
	protected IBinary chooseBinary(List binList, String mode) {
		ILabelProvider programLabelProvider = new CElementLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof IBinary) {
					IBinary bin = (IBinary)element;
					StringBuffer name = new StringBuffer();
					name.append(bin.getPath().lastSegment());
					return name.toString();
				}
				return super.getText(element);
			}
		};

		ILabelProvider qualifierLabelProvider = new CElementLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof IBinary) {
					IBinary bin = (IBinary)element;
					StringBuffer name = new StringBuffer();
					name.append(bin.getCPU() + (bin.isLittleEndian() ? "le" : "be")); //$NON-NLS-1$ //$NON-NLS-2$
					name.append(" - "); //$NON-NLS-1$
					name.append(bin.getPath().toString());
					return name.toString();
				}
				return super.getText(element);
			}
		};
		
		TwoPaneElementSelector dialog = new TwoPaneElementSelector(getShell(), programLabelProvider, qualifierLabelProvider);
		dialog.setElements(binList.toArray());
		dialog.setTitle(getBinarySelectionDialogTitleString(binList, mode));
		dialog.setMessage(getBinarySelectionDialogMessageString(binList, mode));
		dialog.setUpperListLabel(LaunchMessages.getString("Launch.common.BinariesColon")); //$NON-NLS-1$
		dialog.setLowerListLabel(LaunchMessages.getString("Launch.common.QualifierColon")); //$NON-NLS-1$
		dialog.setMultipleSelection(false);
		if (dialog.open() == Window.OK) {
			return (IBinary) dialog.getFirstResult();
		}

		return null;
	}
	
	@SuppressWarnings("unchecked")
	protected String getBinarySelectionDialogTitleString(List binList, String mode) {
		return LaunchMessages.getString("CApplicationLaunchShortcut.CLocalApplication");  //$NON-NLS-1$
	}
	
	@SuppressWarnings("unchecked")
	protected String getBinarySelectionDialogMessageString(List binList, String mode) {
		if (mode.equals(ILaunchManager.DEBUG_MODE)) {
			return LaunchMessages.getString("CApplicationLaunchShortcut.ChooseLocalAppToDebug");  //$NON-NLS-1$
		} else if (mode.equals(ILaunchManager.RUN_MODE)) {
			return LaunchMessages.getString("CApplicationLaunchShortcut.ChooseLocalAppToRun");  //$NON-NLS-1$
		}
		return LaunchMessages.getString("CApplicationLaunchShortcut.Invalid_launch_mode_3"); //$NON-NLS-1$
	}

	/**
	 * Method searchAndLaunch.
	 * @param objects
	 * @param mode
	 */
	private void searchAndLaunch(final Object[] elements, String mode) {
		if (elements != null && elements.length > 0) {
			IBinary bin = null;
			if (elements.length == 1 && elements[0] instanceof IBinary) {
				bin = (IBinary)elements[0];
			} else {
				final List<IBinary> results = new ArrayList<IBinary>();
				ProgressMonitorDialog dialog = new ProgressMonitorDialog(getShell());
				IRunnableWithProgress runnable =new RunnableWithProgressToScanForExecutableImpl(elements,results);
				
				try {
					dialog.run(true, true, runnable);
				} catch (InterruptedException e) {
					return;
				} catch (InvocationTargetException e) {
					MessageDialog.openError(getShell(), LaunchMessages.getString("CApplicationLaunchShortcut.Application_Launcher"), e.getMessage()); //$NON-NLS-1$
					return;
				}
				
				int count = results.size();
				if (count == 0) {
					MessageDialog.openError(getShell(), LaunchMessages.getString("CApplicationLaunchShortcut.Application_Launcher"), LaunchMessages.getString("CApplicationLaunchShortcut.Launch_failed_no_binaries")); //$NON-NLS-1$ //$NON-NLS-2$
				} else if (count > 1) {
					bin = chooseBinary(results, mode);
				} else {
					bin = results.get(0);
				}
			}
			
			if (bin != null) {
				launch(bin, mode);
			}
		} else {
			MessageDialog.openError(getShell(), LaunchMessages.getString("CApplicationLaunchShortcut.Application_Launcher"), LaunchMessages.getString("CApplicationLaunchShortcut.Launch_failed_no_project_selected")); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

}

class RunnableWithProgressToScanForExecutableImpl implements IRunnableWithProgress{
	final List<IBinary> results;
	final Object[] elements;
	
	public RunnableWithProgressToScanForExecutableImpl(Object[] elements, List<IBinary> results){
		this.elements=elements;
		this.results=results;
	}
	public void run(IProgressMonitor pm) throws InterruptedException {
		int nElements = elements.length;
		pm.beginTask("Looking for executables", nElements); //$NON-NLS-1$
		try {
			IProgressMonitor sub = new SubProgressMonitor(pm, 1);
			for (int i = 0; i < nElements; i++) {
				if (elements[i] instanceof IAdaptable) {
					IResource r = (IResource) ((IAdaptable) elements[i]).getAdapter(IResource.class);
					if (r != null) {
						ICProject cproject = CoreModel.getDefault().create(r.getProject());
						if (cproject != null) {
							try {
								getExecutable(cproject,results);
							} catch (CModelException e) {}
						}
					}
				}
				if (pm.isCanceled()) {
					throw new InterruptedException();
				}
				sub.done();
			}
		} finally {
			pm.done();
		}
	}
	
	private void getExecutable(ICProject cproject, List<IBinary> results) throws CModelException{
		IBinary[] bins = cproject.getBinaryContainer().getBinaries();

		for (int j = 0; j < bins.length; j++) {
			if (bins[j].isExecutable()) {
				results.add(bins[j]);
				System.out.println("CuteLaunchShortcut>bin results:"+bins[j]);
			}
		}						
	}
	
}

//code snip for getting variables from workingconfig 
//Map m=null;
//try {
//	m = wc.getAttribute(ILaunchManager.ATTR_ENVIRONMENT_VARIABLES, (Map) null);
//	//ILaunchManager.getNativeEnvironment()
//} catch (CoreException e) {
//	//DebugUIPlugin.log(new Status(IStatus.ERROR, DebugUIPlugin.getUniqueIdentifier(), IStatus.ERROR, "Error reading configuration", e)); //$NON-NLS-1$
//	CuteLauncherPlugin.log(e);
//}
//if (m != null && !m.isEmpty()) {
//	elements = new EnvironmentVariable[m.size()];
//	String[] varNames = new String[m.size()];
//	m.keySet().toArray(varNames);
//	for (int i = 0; i < m.size(); i++) {
//		elements[i] = new EnvironmentVariable(varNames[i], (String) m.get(varNames[i]));
//	}
//	
//}