/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Stephan Michels, stephan@apache.org - 104944 [JUnit] Unnecessary code in JUnitProgressBar
 *     Institute for Software, Emanuel Graf - Adaption for CUTE
 *******************************************************************************/
package ch.hsr.ifs.cutelauncher.ui;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
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
 * A progress bar with a red/green indication for success or failure.
 */
public class CuteProgressBar extends Canvas implements ITestElementListener, ISessionListener, ITestCompositeListener{
	private static final int DEFAULT_WIDTH = 160;
	private static final int DEFAULT_HEIGHT = 18;

	private int fCurrentTickCount= 0;
	private int fMaxTickCount= 0;	
	private int fColorBarWidth= 0;
	private Color fOKColor;
	private Color fFailureColor;
	private Color fStoppedColor;
	private boolean fError;
	private boolean fStopped= false;
	
	private TestSession session;
	
	public CuteProgressBar(Composite parent) {
		super(parent, SWT.NONE);
		CuteLauncherPlugin.getModel().addListener(this);
		addControlListener(new ControlAdapter() {
			public void controlResized(ControlEvent e) {
				fColorBarWidth= scale(fCurrentTickCount);
				redraw();
			}
		});	
		addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {
				paint(e);
			}
		});
		addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				fFailureColor.dispose();
				fOKColor.dispose();
				fStoppedColor.dispose();
				CuteLauncherPlugin.getModel().removeListener(CuteProgressBar.this);
			}
		});
		Display display= parent.getDisplay();
		fFailureColor= new Color(display, 159, 63, 63);
		fOKColor= new Color(display, 95, 191, 95);
		fStoppedColor= new Color(display, 120, 120, 120);
	}

	public void setMaximum(int max) {
		fMaxTickCount= max;
	}
		
	private void reset() {
		fError= false;
		fStopped= false;
		fCurrentTickCount= session.getRun();
		fMaxTickCount= session.getTotalTests();
		fColorBarWidth= 0;
		redraw();
	}
	
	private void paintStep(int startX, int endX) {
		GC gc = new GC(this);	
		setStatusColor(gc);
		Rectangle rect= getClientArea();
		startX= Math.max(1, startX);
		gc.fillRectangle(startX, 1, endX-startX, rect.height-2);
		gc.dispose();		
	}

	private void setStatusColor(GC gc) {
		if (fStopped)
			gc.setBackground(fStoppedColor);
		else if (fError)
			gc.setBackground(fFailureColor);
		else
			gc.setBackground(fOKColor);
	}

	public void stopped() {
		fStopped= true;
		redraw();
	}
	
	private int scale(int value) {
		if (fMaxTickCount > 0) {
			Rectangle r= getClientArea();
			if (r.width != 0)
				return Math.max(0, value*(r.width-2)/fMaxTickCount);
		}
		return value; 
	}
	
	private void drawBevelRect(GC gc, int x, int y, int w, int h, Color topleft, Color bottomright) {
		gc.setForeground(topleft);
		gc.drawLine(x, y, x+w-1, y);
		gc.drawLine(x, y, x, y+h-1);
		
		gc.setForeground(bottomright);
		gc.drawLine(x+w, y, x+w, y+h);
		gc.drawLine(x, y+h, x+w, y+h);
	}
	
	private void paint(PaintEvent event) {
		GC gc = event.gc;
		Display disp= getDisplay();
			
		Rectangle rect= getClientArea();
		gc.fillRectangle(rect);
		drawBevelRect(gc, rect.x, rect.y, rect.width-1, rect.height-1,
			disp.getSystemColor(SWT.COLOR_WIDGET_NORMAL_SHADOW),
			disp.getSystemColor(SWT.COLOR_WIDGET_HIGHLIGHT_SHADOW));
		
		setStatusColor(gc);
		fColorBarWidth= Math.min(rect.width-2, fColorBarWidth);
		gc.fillRectangle(1, 1, fColorBarWidth, rect.height-2);
	}	
	
	public Point computeSize(int wHint, int hHint, boolean changed) {
		checkWidget();
		Point size= new Point(DEFAULT_WIDTH, DEFAULT_HEIGHT);
		if (wHint != SWT.DEFAULT) size.x= wHint;
		if (hHint != SWT.DEFAULT) size.y= hHint;
		return size;
	}
	
	private void update(int run, int failures) {
		fCurrentTickCount = run;
		int x= fColorBarWidth;

		fColorBarWidth= scale(fCurrentTickCount);

		if (!fError && failures > 0) {
			fError= true;
			x= 1;
		}
		if (fCurrentTickCount == fMaxTickCount)
			fColorBarWidth= getClientArea().width-1;
		paintStep(x, fColorBarWidth);
	}
	
	private void update(int run, int failures, int total) {
		fCurrentTickCount = run;
		fMaxTickCount= total;

		fColorBarWidth= scale(fCurrentTickCount);

		if (!fError && failures > 0) {
			fError= true;
		}
		if (fCurrentTickCount == fMaxTickCount)
			fColorBarWidth= getClientArea().width-1;
		redraw();
	}

	public void refresh(boolean hasErrors) {
		fError= hasErrors;
		redraw();
	}

	public void modelCanged(TestElement source, NotifyEvent event) {
		if(event.getType() == NotifyEvent.EventType.testFinished || event.getType() == NotifyEvent.EventType.suiteFinished) {
			UIJob job = new UIJob("Update Progessbar") {

				@Override
				public boolean belongsTo(Object family) {
					return CuteLauncherPlugin.PLUGIN_ID.equals(family);
				}
				
				@Override
				public IStatus runInUIThread(IProgressMonitor monitor) {
					update(session.getRun(), session.getError() + session.getFailure());
					return new Status(IStatus.OK,CuteLauncherPlugin.PLUGIN_ID,IStatus.OK,"", null);
				}

			};
			job.schedule();
		}
	}

	public void sessionStarted(TestSession session) {
		this.session = session;
		session.addListener(this);
		UIJob job = new UIJob("Reset Progessbar") {

			@Override
			public boolean belongsTo(Object family) {
				return CuteLauncherPlugin.PLUGIN_ID.equals(family);
			}
			
			@Override
			public IStatus runInUIThread(IProgressMonitor monitor) {
				reset();
				return new Status(IStatus.OK,CuteLauncherPlugin.PLUGIN_ID,IStatus.OK,"", null);
			}
			
		};
		job.schedule();
		
	}

	public void sessionFinished(TestSession session) {
		// Do nothing
	}

	public void newTestElement(ITestComposite source, TestElement newElement) {
		newElement.addTestElementListener(this);
		UIJob job = new UIJob("Update Progessbar") {

			@Override
			public boolean belongsTo(Object family) {
				return CuteLauncherPlugin.PLUGIN_ID.equals(family);
			}
			
			@Override
			public IStatus runInUIThread(IProgressMonitor monitor) {
				update(session.getRun(), session.getError() + session.getFailure(), session.getTotalTests());
				return new Status(IStatus.OK,CuteLauncherPlugin.PLUGIN_ID,IStatus.OK,"", null);
			}

		};
		job.schedule();
	}
	
}
