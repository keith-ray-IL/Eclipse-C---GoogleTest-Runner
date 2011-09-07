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

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.progress.UIJob;

import ch.hsr.ifs.cutelauncher.CuteLauncherPlugin;
import ch.hsr.ifs.cutelauncher.model.ISessionListener;
import ch.hsr.ifs.cutelauncher.model.TestSession;

public class TestRunnerViewPart extends ViewPart implements ISessionListener {
	
	private enum Orientation{horizontal, vertical}; 

	public static final String ID = "ch.hsr.ifs.cutelauncher.ui.TestRunnerViewPart";

	private Composite top = null;

	private Composite TopPanel = null;
	
	protected boolean autoScroll = true;

	private CounterPanel counterPanel = null;

	private CuteProgressBar cuteProgressBar = null;

	private TestViewer testViewer = null;
	
	private Composite parent;
	
	private Orientation currentOrientation = Orientation.horizontal;

	private ScrollLockAction scrollLockAction;
	private FailuresOnlyFilterAction failureOnlyAction;
	private Action showNextFailureAction;
	private Action showPreviousFailureAction;
	private RerunLastTestAction rerunLastTestAction;

	private TestSession session;

	private StopAction stopAction;	
	

	public TestRunnerViewPart() {
		super();
		CuteLauncherPlugin.getModel().addListener(this);		
	}

	@Override
	public void createPartControl(Composite parent) {
		this.parent = parent;
		addResizeListener(parent);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		gridLayout.marginWidth = 0;
		gridLayout.horizontalSpacing = 0;
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		GridData gdata = new GridData();
		gdata.grabExcessHorizontalSpace = true;
		top = new Composite(parent, SWT.NONE);
		top.setLayout(gridLayout);
		top.setLayoutData(gdata);
		createTopPanel();
		createTestViewer();
		configureToolbar();
	}
	
	private void addResizeListener(Composite parent) {
		parent.addControlListener(new ControlListener() {
			public void controlMoved(ControlEvent e) {
			}
			public void controlResized(ControlEvent e) {
				computeOrientation();
			}
		});
	}
	
	public boolean isCreated() {
		return counterPanel != null;
	}
	
	private void configureToolbar() {
		IActionBars actionBars= getViewSite().getActionBars();
		IToolBarManager toolBar= actionBars.getToolBarManager();
		
		scrollLockAction= new ScrollLockAction(this);
		scrollLockAction.setChecked(!autoScroll);
		
		failureOnlyAction = new FailuresOnlyFilterAction();
		failureOnlyAction.setChecked(false);
		
		showNextFailureAction = new ShowNextFailureAction(this);
		showNextFailureAction.setEnabled(false);
		showPreviousFailureAction = new ShowPreviousFailureAction(this);
		showPreviousFailureAction.setEnabled(false);
		
		rerunLastTestAction = new RerunLastTestAction();
		rerunLastTestAction.setEnabled(false);
		
		stopAction = new StopAction();
		stopAction.setEnabled(false);
		
		toolBar.add(showNextFailureAction);
		toolBar.add(showPreviousFailureAction);
		toolBar.add(failureOnlyAction);
		toolBar.add(scrollLockAction);
		toolBar.add(new Separator());
		toolBar.add(rerunLastTestAction);
		toolBar.add(stopAction);
	}

	/**
	 * This method initializes TopPSanel	
	 *
	 */
	private void createTopPanel() {
		GridLayout gridLayout1 = new GridLayout();
		gridLayout1.numColumns = 2;
		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.verticalAlignment = org.eclipse.swt.layout.GridData.FILL;
		gridData.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
		TopPanel = new Composite(top, SWT.NONE);
		createCounterPanel();
		TopPanel.setLayout(gridLayout1);
		TopPanel.setLayoutData(gridData);
		createCuteProgressBar();
	}

	/**
	 * This method initializes counterPanel	
	 *
	 */
	private void createCounterPanel() {
		GridData gridData1 = new org.eclipse.swt.layout.GridData();
		gridData1.grabExcessHorizontalSpace = true;
		gridData1.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
		counterPanel = new CounterPanel(TopPanel, SWT.NONE);
		counterPanel.setLayoutData(gridData1);
	}

	/**
	 * This method initializes cuteProgressBar	
	 *
	 */
	private void createCuteProgressBar() {
		GridData gridData2 = new GridData();
		gridData2.grabExcessHorizontalSpace = true;
		gridData2.horizontalIndent = 35;
		gridData2.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
		cuteProgressBar = new CuteProgressBar(TopPanel);
		cuteProgressBar.setLayoutData(gridData2);
	}



	/**
	 * This method initializes testViewer	
	 *
	 */
	private void createTestViewer() {
		GridData gridData3 = new org.eclipse.swt.layout.GridData();
		gridData3.grabExcessHorizontalSpace = true;
		gridData3.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
		gridData3.verticalAlignment = org.eclipse.swt.layout.GridData.FILL;
		gridData3.grabExcessVerticalSpace = true;
		testViewer = new TestViewer(top, SWT.NONE, this);
		testViewer.setLayoutData(gridData3);
	}
	
	private void computeOrientation() {
			Point size= parent.getSize();
			if (size.x != 0 && size.y != 0) {
				if (size.x > size.y) 
					setOrientation(Orientation.horizontal);
				else 
					setOrientation(Orientation.vertical);
			}
	}

	private void setOrientation(Orientation orientation) {
		testViewer.setOrientation(orientation == Orientation.horizontal);
		currentOrientation = orientation;
		GridLayout layout= (GridLayout) TopPanel.getLayout();
		setCounterColumns(layout); 
		parent.layout();
	}

	private void setCounterColumns(GridLayout layout) {
		if (currentOrientation == Orientation.horizontal)
			layout.numColumns= 2; 
		else
			layout.numColumns= 1;
	}

	@Override
	public void setFocus() {
	}

	public boolean isAutoScroll() {
		return autoScroll;
	}

	public void setAutoScroll(boolean autoScroll) {
		this.autoScroll = autoScroll;
	}
	
	private final class SessionFinishedUIJob extends UIJob {
		private SessionFinishedUIJob(String name) {
			super(name);
		}

		@Override
		public IStatus runInUIThread(IProgressMonitor monitor) {
			rerunLastTestAction.setEnabled(true);
			stopAction.setEnabled(false);
			if(TestRunnerViewPart.this.session.hasErrorOrFailure()) {
				showNextFailureAction.setEnabled(true);
				showPreviousFailureAction.setEnabled(true);
				if(isAutoScroll()) {
					testViewer.selectFirstFailure();
				}
			}
			return new Status(IStatus.OK, CuteLauncherPlugin.PLUGIN_ID, IStatus.OK,"OK",null);
		}
	}

	private class FailuresOnlyFilterAction extends Action {
		public FailuresOnlyFilterAction() {
			super("Show Failures Only", AS_CHECK_BOX);
			setToolTipText("Show Failures Only");
			setImageDescriptor(CuteLauncherPlugin.getImageDescriptor("obj16/failures.gif")); //$NON-NLS-1$
		}

		public void run() {
			setShowFailuresOnly(isChecked());
		}
	}
	
	private class RerunLastTestAction extends Action{
		public RerunLastTestAction() {
			setText("Rerun Test"); 
			setToolTipText("Rerun Test");
			setDisabledImageDescriptor(CuteLauncherPlugin.getImageDescriptor("dlcl16/relaunch.gif")); //$NON-NLS-1$
			setHoverImageDescriptor(CuteLauncherPlugin.getImageDescriptor("obj16/relaunch.gif")); //$NON-NLS-1$
			setImageDescriptor(CuteLauncherPlugin.getImageDescriptor("obj16/relaunch.gif")); //$NON-NLS-1$
			setEnabled(false);
		}
		
		public void run(){
			rerunTestRun();
		}

	}
	
	private class StopAction extends Action{
		public StopAction() {
			setText("Stop JUnit Test");
			setToolTipText("Stop CuTe Test Run");
			setDisabledImageDescriptor(CuteLauncherPlugin.getImageDescriptor("dlcl16/stop.gif")); //$NON-NLS-1$
			setHoverImageDescriptor(CuteLauncherPlugin.getImageDescriptor("obj16/stop.gif")); //$NON-NLS-1$
			setImageDescriptor(CuteLauncherPlugin.getImageDescriptor("obj16/stop.gif")); //$NON-NLS-1$
		}

		public void run() {
			stopTest();
//			setEnabled(false);
		}
	}

	public void setShowFailuresOnly(boolean b) {
		testViewer.setFailuresOnly(b);		
	}

	public void stopTest() {
		if(session != null) {
			try {
				for(IProcess process : session.getLaunch().getProcesses()) {
					process.terminate();
				}
//				new SessionFinishedUIJob("Process Stopped").schedule();
			}catch(DebugException de) {
				
			}
		}
	}

	public void rerunTestRun() {
		if (session != null && session.getLaunch().getLaunchConfiguration() != null) {
			ILaunchConfiguration configuration= session.getLaunch().getLaunchConfiguration();
			DebugUITools.launch(configuration, session.getLaunch().getLaunchMode());
		}
		
	}

	public void selectNextFailure() {
		testViewer.selectNextFailure();		
	}
	
	public void selectPrevFailure() {
		testViewer.selectPrevFailure();
	}

	public void sessionFinished(TestSession session) {
		SessionFinishedUIJob sessionFinishedUIJob = new SessionFinishedUIJob("Session Over");
		sessionFinishedUIJob.schedule();
	}

	public void sessionStarted(TestSession session) {
		this.session = session;
		showNextFailureAction.setEnabled(false);
		showPreviousFailureAction.setEnabled(false);
		rerunLastTestAction.setEnabled(false);
		stopAction.setEnabled(true);
	}

}  //  @jve:decl-index=0:visual-constraint="148,36,771,201"