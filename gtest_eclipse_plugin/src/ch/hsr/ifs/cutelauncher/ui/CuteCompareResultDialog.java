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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.eclipse.compare.CompareConfiguration;
import org.eclipse.compare.IEncodedStreamContentAccessor;
import org.eclipse.compare.ITypedElement;
import org.eclipse.compare.structuremergeviewer.DiffNode;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TrayDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ViewForm;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;

import ch.hsr.ifs.cutelauncher.model.TestCase;
import ch.hsr.ifs.cutelauncher.model.TestFailure;
import ch.hsr.ifs.cutelauncher.model.TestResult;

/**
 * @author Emanuel Graf
 *
 */
public class CuteCompareResultDialog extends TrayDialog {
	
	private static class CompareElement implements ITypedElement, IEncodedStreamContentAccessor {
	    private String fContent;
	    
	    public CompareElement(String content) {
	    	if(content == null) {
	    		fContent = "no Data";
	    	}else {
	    		fContent= content;
	    	}
	    }
	    public String getName() {
	        return "<no name>"; //$NON-NLS-1$
	    }
	    public Image getImage() {
	        return null;
	    }
	    public String getType() {
	        return "txt"; //$NON-NLS-1$
	    }
	    public InputStream getContents() {
		    try {
		        return new ByteArrayInputStream(fContent.getBytes("UTF-8")); //$NON-NLS-1$
		    } catch (UnsupportedEncodingException e) {
		        return new ByteArrayInputStream(fContent.getBytes());
		    }
	    }
        public String getCharset() throws CoreException {
            return "UTF-8"; //$NON-NLS-1$
        }

	}
	
	
	private CuteTextMergeViewer compareViewer;
    TestCase test;
//	private ToolBarManager tbm;
//	private ShowWhiteSpaceAction action;

	public CuteCompareResultDialog(Shell shell, TestCase test) {
		super(shell);
		this.test = test;
		setHelpAvailable(false);
		setShellStyle(SWT.DIALOG_TRIM | SWT.RESIZE | SWT.MAX);
		
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite composite = (Composite) super.createDialogArea(parent);
		ViewForm pane = new ViewForm(composite, SWT.BORDER | SWT.FLAT);
		Control control = createCompareViewer(pane);
		GridData data= new GridData(GridData.FILL_HORIZONTAL | GridData.FILL_VERTICAL);
		data.widthHint= convertWidthInCharsToPixels(120);
		data.heightHint= convertHeightInCharsToPixels(13);
		pane.setLayoutData(data);
		ToolBar tb = new ToolBar(pane, SWT.BORDER|SWT.FLAT);
		ToolBarManager tbm = new ToolBarManager(tb);
		ShowWhiteSpaceAction action = new ShowWhiteSpaceAction(compareViewer);
		tbm.add(action);
		tbm.update(true);;
		pane.setTopRight(tb);
		
		pane.setContent(control);
		GridData gd= new GridData(GridData.FILL_BOTH);
		control.setLayoutData(gd);
		return composite;
	}
	
	private Control createCompareViewer(ViewForm pane) {
		final CompareConfiguration compareConfiguration= new CompareConfiguration();
	    compareConfiguration.setLeftLabel("Expected:"); 
	    compareConfiguration.setLeftEditable(false);
	    compareConfiguration.setRightLabel("Actual:");	 
	    compareConfiguration.setRightEditable(false);
	    compareConfiguration.setProperty(CompareConfiguration.IGNORE_WHITESPACE, Boolean.FALSE);

	    compareViewer = new CuteTextMergeViewer(pane, SWT.NONE, compareConfiguration);
	    setCompareViewerInput(test);

	    Control control= compareViewer.getControl();
	    control.addDisposeListener(new DisposeListener() {
	        public void widgetDisposed(DisposeEvent e) {
                compareConfiguration.dispose();
	        }
	    });
	    return control;
		
	}
	

	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, "OK", true); 
	}
	
	public void setCompareViewerInput(TestCase test) {
		this.test = test;
		if (! compareViewer.getControl().isDisposed()) {
			TestResult result = test.getResult();
			if (result instanceof TestFailure) {
				TestFailure failure = (TestFailure) result;
				CompareElement expected = new CompareElement(failure.getExpected());
				CompareElement was = new CompareElement(failure.getWas());
				compareViewer.setInput(new DiffNode(expected, was));
			}
			
		}
	}
	
    protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Result Comparison");
	}

	public void refresh() {
		compareViewer.refresh();		
	}

	
}
