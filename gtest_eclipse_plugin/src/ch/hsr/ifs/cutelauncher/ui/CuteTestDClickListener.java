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
 * Peter Sommerlad - navigation for successful test cases
 ******************************************************************************/
package ch.hsr.ifs.cutelauncher.ui;

import java.util.regex.Pattern;

import org.eclipse.cdt.core.CCorePlugin;
import org.eclipse.cdt.core.dom.IName;
import org.eclipse.cdt.core.dom.ast.IASTFileLocation;
import org.eclipse.cdt.core.index.IIndex;
import org.eclipse.cdt.core.index.IIndexBinding;
import org.eclipse.cdt.core.index.IndexFilter;
import org.eclipse.cdt.core.model.CModelException;
import org.eclipse.cdt.core.model.CoreModel;
import org.eclipse.cdt.core.model.ICProject;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.content.IContentDescription;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorRegistry;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;

import ch.hsr.ifs.cutelauncher.CuteLauncherPlugin;
import ch.hsr.ifs.cutelauncher.model.TestCase;
import ch.hsr.ifs.cutelauncher.model.TestSession;
import ch.hsr.ifs.cutelauncher.model.TestStatus;

/**
 * @author Emanuel Graf
 *
 */
public class CuteTestDClickListener implements IDoubleClickListener {
	
	private TestSession session = null;

	public CuteTestDClickListener(TestSession session) {
		super();
		this.session = session;
	}


	public void doubleClick(DoubleClickEvent event) {
		if (event.getSelection() instanceof TreeSelection) {
			TreeSelection treeSel = (TreeSelection) event.getSelection();
			if (treeSel.getFirstElement() instanceof TestCase) {
				TestCase tCase = (TestCase) treeSel.getFirstElement();
				if(tCase.getStatus() == TestStatus.failure) {
					openEditor(tCase.getFile(), tCase.getLineNumber(), false);
				} else {
					openEditorForNonFailingTestCase(tCase.getName());
				}
			}
		}

	}
	
	public void setSession(TestSession session) {
		this.session = session;
	}


	// TODO could this be done simpler?
	private void openEditorForNonFailingTestCase(String testCaseName) {
		try {
			ICProject[] projects = CoreModel.getDefault().getCModel().getCProjects();
			for (int i = 0; i < projects.length; ++i) {
				if(!projects[i].getElementName().equals(session.getLaunch().getLaunchConfiguration().getName()))
					continue;
				IIndex index = CCorePlugin.getIndexManager().getIndex(projects[i]);
				Pattern p = Pattern.compile(testCaseName);
				IIndexBinding[] bindings = index.findBindings(p, false, IndexFilter.ALL, new NullProgressMonitor());
				for (int bi = 0; bindings != null && bi < bindings.length; ++bi) {
					IIndexBinding binding = bindings[bi];
					if (binding == null)
						continue;
					
					//TODO Doesn't work for test methods in an anonymous namespace
					IName[] definition = index.findDefinitions(index.adaptBinding(binding));
					if (definition == null || definition.length == 0)
						continue;
					IASTFileLocation loc = definition[0].getFileLocation();
					IPath filePath = new Path(loc.getFileName());
					IFile file = ResourcesPlugin.getWorkspace().getRoot()
					.getFileForLocation(filePath);
					if (file==null)continue;
					openEditor(file, loc.getNodeOffset(), true);
					return;
				}

			}
		} catch (CModelException e) {
			CuteLauncherPlugin.log(e);
		} catch (CoreException e) {
			CuteLauncherPlugin.log(e);
		}
	}

	private void openEditor(IFile file, int lineNumberOrOffset, boolean isOffset) {
		IWorkbenchWindow window = CuteLauncherPlugin.getActiveWorkbenchWindow();
		if (window != null) {
			IWorkbenchPage page = window.getActivePage();
			if (page != null) {
				try {
					IEditorPart editorPart = page.openEditor(new FileEditorInput(file), getEditorId(file) , false);
					if (lineNumberOrOffset > 0 && editorPart instanceof ITextEditor) { // TODO definition might start on ofset 0, if not linenumber
						ITextEditor textEditor = (ITextEditor)editorPart;
					IEditorInput input = editorPart.getEditorInput();
					IDocumentProvider provider = textEditor.getDocumentProvider();
					try {
						provider.connect(input);
					} catch (CoreException e) {
						// unable to link
						CuteLauncherPlugin.log(e);
						return;
					}
					IDocument document = provider.getDocument(input);
					try {
						IRegion region= isOffset?
								document.getLineInformationOfOffset(lineNumberOrOffset)
								:document.getLineInformation(lineNumberOrOffset - 1);
								textEditor.selectAndReveal(region.getOffset(), region.getLength());
					} catch (BadLocationException e) {
						// unable to link
						CuteLauncherPlugin.log(e);
					}
					provider.disconnect(input);
					}
				} catch (PartInitException e) {
					CuteLauncherPlugin.log(e);
				}
			}
		}
	}
	
	private String getEditorId(IFile file) {
			IWorkbench workbench= CuteLauncherPlugin.getDefault().getWorkbench();
			// If there is a registered editor for the file use it.
			IEditorDescriptor desc = workbench.getEditorRegistry().getDefaultEditor(file.getName(), getFileContentType(file));
			if (desc == null) {
				//default editor
				desc= workbench.getEditorRegistry().findEditor(IEditorRegistry.SYSTEM_EXTERNAL_EDITOR_ID);
			}
		return desc.getId();
	}

    private IContentType getFileContentType(IFile file) {
        try {
            IContentDescription description= file.getContentDescription();
            if (description != null) {
                return description.getContentType();
            }
        } catch (CoreException e) {
        }
        return null;
    }

}
