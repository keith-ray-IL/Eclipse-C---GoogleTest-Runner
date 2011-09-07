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
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.progress.UIJob;

import ch.hsr.ifs.cutelauncher.CuteLauncherPlugin;
import ch.hsr.ifs.cutelauncher.model.ISessionListener;
import ch.hsr.ifs.cutelauncher.model.ITestComposite;
import ch.hsr.ifs.cutelauncher.model.ITestCompositeListener;
import ch.hsr.ifs.cutelauncher.model.ITestElementListener;
import ch.hsr.ifs.cutelauncher.model.NotifyEvent;
import ch.hsr.ifs.cutelauncher.model.TestElement;
import ch.hsr.ifs.cutelauncher.model.TestSession;

/**
 * @author egraf
 *
 */
public class CounterPanel extends Composite implements ITestElementListener, ISessionListener, ITestCompositeListener {

	private final class UpdateCounterPanelJob extends UIJob {
		private UpdateCounterPanelJob(String name) {
			super(name);
		}

		@Override
		public IStatus runInUIThread(IProgressMonitor monitor) {
			updateNumbers();
			return new Status(IStatus.OK, CuteLauncherPlugin.PLUGIN_ID, IStatus.OK,"OK",null);
		}
	}

	private Label runLabel = null;
	private Label runText = null;
	private Image errorImage = CuteLauncherPlugin.getImageDescriptor("tcr/error.gif").createImage();  //  @jve:decl-index=0:
	private Image failedImage = CuteLauncherPlugin.getImageDescriptor("tcr/failed.gif").createImage();  //  @jve:decl-index=0:
	private Label errorImageLabel = null;
	private Label errorLabel = null;
	private Label errorText = null;
	private Label failedImageLabel = null;
	private Label failedLabel = null;
	private Label failedText = null;
	
	private TestSession session;
	
	private int total;

	public CounterPanel(Composite parent, int style) {
		super(parent, style);
		CuteLauncherPlugin.getModel().addListener(this);
		initialize();
	}

	private void initialize() {
		GridData gridData7 = new GridData();
		gridData7.grabExcessHorizontalSpace = false;
		gridData7.horizontalIndent = 7;
		GridData gridData6 = new GridData();
		gridData6.grabExcessHorizontalSpace = false;
		GridData gridData5 = new GridData();
		gridData5.grabExcessHorizontalSpace = true;
		gridData5.horizontalAlignment = org.eclipse.swt.layout.GridData.END;
		GridData gridData4 = new GridData();
		gridData4.grabExcessHorizontalSpace = false;
		gridData4.horizontalIndent = 7;
		GridData gridData3 = new GridData();
		gridData3.grabExcessHorizontalSpace = false;
		GridData gridData2 = new GridData();
		gridData2.grabExcessHorizontalSpace = true;
		gridData2.horizontalAlignment = org.eclipse.swt.layout.GridData.END;
		GridData gridData1 = new GridData();
		gridData1.grabExcessHorizontalSpace = false;
		gridData1.horizontalIndent = 7;
		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = false;
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 8;
		runLabel = new Label(this, SWT.NONE);
		runLabel.setText("Runs:");
		runLabel.setLayoutData(gridData);
		runText = new Label(this, SWT.READ_ONLY);
		runText.setText("0/0");
		runText.setLayoutData(gridData1);
		errorImageLabel = new Label(this, SWT.NONE);
		errorImage.setBackground(errorImageLabel.getBackground());
		errorImageLabel.setImage(errorImage);
		errorImageLabel.setLayoutData(gridData2);
		errorLabel = new Label(this, SWT.NONE);
		errorLabel.setText("Errors:");
		errorLabel.setLayoutData(gridData3);
		errorText = new Label(this, SWT.READ_ONLY);
		errorText.setText("0");
		errorText.setLayoutData(gridData4);
		failedImageLabel = new Label(this, SWT.NONE);
		failedImage.setBackground(failedImageLabel.getBackground());
		failedImageLabel.setImage(failedImage);
		failedImageLabel.setLayoutData(gridData5);
		failedLabel = new Label(this, SWT.NONE);
		failedLabel.setText("Failures:");
		failedLabel.setLayoutData(gridData6);
		failedText = new Label(this, SWT.READ_ONLY);
		failedText.setText("0");
		failedText.setLayoutData(gridData7);
		
		addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				disposeIcons();
				CuteLauncherPlugin.getModel().removeListener(CounterPanel.this);
			}
		});
		
		this.setLayout(gridLayout);
		this.setSize(new Point(342, 30));
	}
	
	private void disposeIcons() {
		errorImage.dispose();
		failedImage.dispose();
	}
	
	public void setTotal(int total) {
		this.total = total;
		layout();
	}
	
	public void setRun(int run) {
		runText.setText(Integer.toString(run) + "/" + Integer.toString(total));
		runText.pack(true);
		layout();
		redraw();
	}
	
	public void setErrors(int errors) {
		errorText.setText(Integer.toString(errors));
		errorText.pack(true);
		layout();
		redraw();
	}
	
	public void setFailures(int failures) {
		failedText.setText(Integer.toString(failures));
		failedText.pack(true);
		redraw();
	}

	public void reset(TestSession session) {		
		updateNumbers();
	}

	private void updateNumbers() {
		setTotal(session.getTotalTests());
		setRun(session.getRun());
		setFailures(session.getFailure());
		setErrors(session.getError());
	}

	public void modelCanged(TestElement source, NotifyEvent event) {
		UIJob job = new UpdateCounterPanelJob("Update CounterPanel Job");
		job.schedule();
	}

	public void sessionStarted(TestSession session) {
		this.session = session;
		session.addListener(this);
		UIJob job = new UpdateCounterPanelJob("Update CounterPanel Job");
		job.schedule();
	}

	public void sessionFinished(TestSession session) {
		// Do nothing
	}

	public void newTestElement(ITestComposite source, TestElement newElement) {
		UIJob job = new UpdateCounterPanelJob("Update CounterPanel Job");
		newElement.addTestElementListener(this);
		job.schedule();
	}

}  //  @jve:decl-index=0:visual-constraint="10,21"
