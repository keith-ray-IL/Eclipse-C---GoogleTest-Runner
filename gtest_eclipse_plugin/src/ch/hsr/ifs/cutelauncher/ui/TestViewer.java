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

import java.util.AbstractList;
import java.util.ListIterator;
import java.util.Vector;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.progress.UIJob;

import ch.hsr.ifs.cutelauncher.CuteLauncherPlugin;
import ch.hsr.ifs.cutelauncher.model.ISessionListener;
import ch.hsr.ifs.cutelauncher.model.ITestComposite;
import ch.hsr.ifs.cutelauncher.model.ITestCompositeListener;
import ch.hsr.ifs.cutelauncher.model.ITestElementListener;
import ch.hsr.ifs.cutelauncher.model.NotifyEvent;
import ch.hsr.ifs.cutelauncher.model.TestCase;
import ch.hsr.ifs.cutelauncher.model.TestElement;
import ch.hsr.ifs.cutelauncher.model.TestSession;
import ch.hsr.ifs.cutelauncher.model.TestStatus;
import ch.hsr.ifs.cutelauncher.model.TestSuite;

public class TestViewer extends Composite implements ITestElementListener, ISessionListener, ITestCompositeListener{
	
	private final class UpdateTestElement extends UIJob {
		private UpdateTestElement(String name, TestElement element, boolean reveal) {
			super(name);
			this.element = element;
			this.reveal = reveal;
		}
		
		private TestElement element;
		private boolean reveal;
		
		@Override
		public IStatus runInUIThread(IProgressMonitor monitor) {
			treeViewer.refresh(element, true);
			if(reveal && viewPart.isAutoScroll()) {
				treeViewer.reveal(element);
			}
			return new Status(IStatus.OK, CuteLauncherPlugin.PLUGIN_ID, IStatus.OK,"OK",null);
		}
	}
	
	private final class ShowNewTest extends UIJob {
		private ShowNewTest(String name, ITestComposite composite, TestElement newElement) {
			super(name);
			this.parent = composite;
			this.element = newElement;
		}
		
		private ITestComposite parent;
		private TestElement element;

		@Override
		public IStatus runInUIThread(IProgressMonitor monitor) {
			treeViewer.refresh(parent, true);
			if(viewPart.isAutoScroll()){
				treeViewer.reveal(element);
			}
			return new Status(IStatus.OK, CuteLauncherPlugin.PLUGIN_ID, IStatus.OK,"OK",null);
		}
	}

	private class TestResultViewer extends StyledText {
		private class TestResultDClickListener extends MouseAdapter{

			@Override
			public void mouseDoubleClick(MouseEvent e) {
				TestCase test = getTreeSelection();
				if(test != null){
					CuteCompareResultAction action = new CuteCompareResultAction(test, TestViewer.this.getShell());
					action.run();
				}
			}

					
		}
		
		TestCase test;

		public TestResultViewer(Composite parent, int style) {
			super(parent, style);
			addMouseListener(new TestResultDClickListener());
		}
		
		public void showTestDetail(TestElement test) {
			if (test instanceof TestCase) {
				TestCase tCase = (TestCase) test;
				this.test = tCase;
				testResultViewer.setText(tCase.getMessage());
				redraw();
			}else if (test instanceof TestSuite) {
				testResultViewer.setText("");
				redraw();
			}
		}
		
	}
	
	private final class FailuresOnlyFilter extends ViewerFilter{

		@Override
		public boolean select(Viewer viewer, Object parentElement, Object element) {
			if (element instanceof TestElement) {
				TestElement testElement = (TestElement) element;
				switch(testElement.getStatus()) {
				case running:
				case error:
				case failure:
					return true;
				default:
					return false;
				}
			}else {
				return true;
			}
		}
		
	}
	
	private final class ReverseVector<T> extends Vector<T> {

		private static final long serialVersionUID = -7493342763899946849L;
		
		private Vector<? extends T> vec;
		
		public ReverseVector(Vector<? extends T> vec){
			this.vec = vec;
		}

		@Override
		public synchronized T get(int index) {
			return vec.get(vec.size() - index -1 );
		}

		@Override
		public synchronized int size() {
			return vec.size();
		}
		
		
		
	}

	private SashForm sashForm = null;
	private TreeViewer treeViewer = null;
	private TestResultViewer testResultViewer = null;
	
	private TestSession session;
	private Vector<TestElement> elemets = new Vector<TestElement>();
	
	private TestRunnerViewPart viewPart;
	
	private boolean failureOnly = false;
	private FailuresOnlyFilter failuresOnlyFilter = new FailuresOnlyFilter();
	private CuteTestDClickListener cuteTestDClickListener;;
	
	public TestViewer(Composite parent, int style, TestRunnerViewPart viewPart) {
		super(parent, style);
		this.viewPart = viewPart;
		CuteLauncherPlugin.getModel().addListener(this);
		initialize();
		addDisposeListener(new DisposeListener() {

			public void widgetDisposed(DisposeEvent e) {
				CuteLauncherPlugin.getModel().removeListener(TestViewer.this);
			}
		});
	}

	public void reset(TestSession session) {
		testResultViewer.setText("");
		treeViewer.setInput(session);
	}
	
	public void showTestDetails(TestElement testElement) {
		testResultViewer.showTestDetail(testElement);
	}

	private void initialize() {
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		gridLayout.marginWidth = 0;
		gridLayout.horizontalSpacing = 0;
		gridLayout.marginWidth = 1;
		gridLayout.marginHeight = 0;
		createSashForm();
		this.setLayout(gridLayout);
//		setSize(new Point(300, 200));
	}

	/**
	 * This method initializes sashForm
	 *
	 */
	private void createSashForm() {
		sashForm = new SashForm(this, SWT.HORIZONTAL);
		sashForm.setLayoutData(new GridData(GridData.FILL_BOTH));
		treeViewer = new TreeViewer(sashForm, SWT.FLAT);
		treeViewer.setContentProvider(new CuteTestTreeContentProvieder());
		treeViewer.setLabelProvider(new CuteTestLabelProvider());
		treeViewer.setAutoExpandLevel(TreeViewer.ALL_LEVELS);
		treeViewer.addSelectionChangedListener(new CuteTestSelecetionListener(this));
		cuteTestDClickListener = new CuteTestDClickListener(session);
		treeViewer.addDoubleClickListener(cuteTestDClickListener);
		testResultViewer = new TestResultViewer(sashForm, SWT.FLAT);
		testResultViewer.setEditable(false);
		testResultViewer.setIndent(5);
	}

	public void modelCanged(TestElement source, NotifyEvent event) {
		UIJob job = null;
		switch(event.getType()) {
		case suiteFinished:
			job = new UpdateTestElement("Show new Test", event.getElement(), false);
			break;
		case testFinished:
			job = new UpdateTestElement("Update Test", event.getElement(), true);
			break;
		}

		if(job != null) {
			job.schedule();
		}
	}

	public void sessionStarted(TestSession session) {
		this.session = session;
		session.addListener(this);
		cuteTestDClickListener.setSession(session);
		UIJob job = new UIJob("Reset TestViewer") {
			
			@Override
			public boolean belongsTo(Object family) {
				return CuteLauncherPlugin.PLUGIN_ID.equals(family);
			}

			@Override
			public IStatus runInUIThread(IProgressMonitor monitor) {
				reset(TestViewer.this.session);
				return new Status(IStatus.OK, CuteLauncherPlugin.PLUGIN_ID, IStatus.OK,"OK",null);
			}
			
		};
		job.schedule();
	}
	
	public void sessionFinished(TestSession session) {
		
	}
	
	public void setFailuresOnly(boolean failureOnly) {
		this.failureOnly = failureOnly;
		updateFilters();
	}

	private void updateFilters() {
		if(failureOnly) {
			treeViewer.addFilter(failuresOnlyFilter);
		}else {
			treeViewer.removeFilter(failuresOnlyFilter);
		}
	}

	public void selectNextFailure() {
		if(getSession().hasErrorOrFailure()) {
			Object firstElement = getSelectedElement();
			if (firstElement instanceof TestCase) {
				TestCase tCase = (TestCase) firstElement;
				treeViewer.setSelection(new StructuredSelection(findNextFailure(tCase)), true);
			}else {
				treeViewer.setSelection(new StructuredSelection(findFirstFailure()));
			}
			
		}
	}

	private Object getSelectedElement() {
		StructuredSelection selection = (StructuredSelection) treeViewer.getSelection();
		Object firstElement = selection.getFirstElement();
		return firstElement;
	}
	
	protected TestCase getTreeSelection() {
		ISelection sel = treeViewer.getSelection();
		if (sel instanceof TreeSelection) {
			TreeSelection treeSel = (TreeSelection) sel;
			if (treeSel.getFirstElement() instanceof TestCase) {
				TestCase testCase = (TestCase) treeSel.getFirstElement();
				return testCase;
			}
		}
		return null;
	}

	public void selectFirstFailure() {
		treeViewer.setSelection(new StructuredSelection(findFirstFailure()), true);
	}
	
	private TestElement findFirstFailure() {
		Vector<TestElement> elements = getSession().getElements();
		for (TestElement element : elements) {
			if(element.getStatus() == TestStatus.failure || element.getStatus() == TestStatus.error) {
				if (element instanceof ITestComposite) {
					ITestComposite composite = (ITestComposite) element;
					return findNextChildFailure(composite, false);
				}else {
					return element;
				}
			}
		}
		return null;
	}

	private TestSession getSession() {
		if(session == null) {
			session = CuteLauncherPlugin.getModel().getSession();
		}
		return session;
	}

	private TestElement findNextChildFailure(ITestComposite composite, boolean revese) {
		Vector<? extends TestElement> elements = composite.getElements();
		if(revese) {
			elements = new ReverseVector<TestElement>(elements);
		}
		for (TestElement element : elements) {
			if(element.getStatus() == TestStatus.failure || element.getStatus() == TestStatus.error) {
				if (element instanceof ITestComposite) {
					ITestComposite newComposite = (ITestComposite) element;
					return findNextChildFailure(newComposite, false);
				}else {
					return element;
				}
			}
		}
		return null;
	}

	private TestElement findNextFailure(TestCase selected) {
		TestElement nextFailure = findNextSiblingFailure(selected, false);
		if(nextFailure != null) {
			return nextFailure;
		}else {
		return findNextFailureInParent(selected.getParent(), selected, false);
		}
	}

	private TestElement findNextFailureInParent(ITestComposite current, TestCase selected, boolean reverse) {
		if (current instanceof TestElement) {
			TestElement tElement = (TestElement) current;
			TestElement nextFailure = findNextSiblingFailure(tElement, reverse);
			if(nextFailure != null) {
				return nextFailure;
			}else {
				return findNextFailureInParent(tElement.getParent(), selected, reverse);
			}
		}else {
			return selected;
		}
	}

	private TestElement findNextSiblingFailure(TestElement tElement, boolean reverse) {
		Vector<? extends TestElement> tests = tElement.getParent().getElements();
		int index = tests.indexOf(tElement) + 1;
		if(reverse) {
			tests = new ReverseVector<TestElement>(tests);
			index = tests.size() - index + 1;
		}
		ListIterator<? extends TestElement> it = ((AbstractList<? extends TestElement>)tests).listIterator(index);
		TestElement nextFailure;
		while(it.hasNext()) {
			nextFailure = it.next();
			if(nextFailure.getStatus() == TestStatus.failure || nextFailure.getStatus() == TestStatus.error) {
				if (nextFailure instanceof ITestComposite) {
					ITestComposite composite = (ITestComposite) nextFailure;
					return findNextChildFailure(composite, reverse);
				}else {
					return nextFailure;
				}
			}
		}
		return null;
	}
	
	protected void setOrientation(boolean horizontal) {
		if ((sashForm == null) || sashForm.isDisposed())
			return;
		sashForm.setOrientation(horizontal ? SWT.HORIZONTAL : SWT.VERTICAL);
	}
	
	public void selectPrevFailure() {
		if(getSession().hasErrorOrFailure()) {
			Object firstElement = getSelectedElement();
			if (firstElement instanceof TestCase) {
				TestCase tCase = (TestCase) firstElement;
				treeViewer.setSelection(new StructuredSelection(findPrevFailure(tCase)), true);
			}else { //show first Failure
				treeViewer.setSelection(new StructuredSelection(findFirstFailure()), true);
			}
		}
	}
	
	
	private TestElement findPrevFailure(TestCase selected) {
		TestElement nextFailure = findNextSiblingFailure(selected, true);
		if(nextFailure != null) {
			return nextFailure;
		}else {
		return findNextFailureInParent(selected.getParent(), selected, true);
		}
	}

	public void newTestElement(ITestComposite source, TestElement newElement) {
		newElement.addTestElementListener(this);
		if (newElement instanceof ITestComposite) {
			ITestComposite composite = (ITestComposite) newElement;
			composite.addListener(this);
		}
		elemets.add(newElement);
		UIJob job = new ShowNewTest("Show new Test", newElement.getParent(), newElement);
		job.schedule();
	}

}
