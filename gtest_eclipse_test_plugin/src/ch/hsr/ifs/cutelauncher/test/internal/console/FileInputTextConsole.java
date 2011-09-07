/*******************************************************************************
 * Copyright (c) 2007 Institute for Software, HSR Hochschule für Technik  
 * Rapperswil, University of applied sciences
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 * 
 * Contributors: 
 * Emanuel Graf & Guido Zgraggen- initial API and implementation 
 ******************************************************************************/
package ch.hsr.ifs.cutelauncher.test.internal.console;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.console.IConsoleDocumentPartitioner;
import org.eclipse.ui.console.IConsoleView;
import org.eclipse.ui.console.TextConsole;
import org.eclipse.ui.part.IPageBookViewPage;
import org.osgi.framework.Bundle;

import ch.hsr.ifs.cutelauncher.test.TestPlugin;

/**
 * @author Emanuel Graf
 *
 */
public class FileInputTextConsole extends TextConsole {
	
	private String inputFile;
	
	public FileInputTextConsole(String inputFile) {
		super(inputFile, "FileInputTextConsole", null, true);
		this.inputFile = inputFile;
	}

	private String getFileText(String inputFile) throws CoreException{
		Bundle bundle = TestPlugin.getDefault().getBundle();
		Path path = new Path(inputFile);
		try {
			String file2 = FileLocator.toFileURL(FileLocator.find(bundle, path, null)).getFile();
			BufferedReader br = new BufferedReader(new FileReader(file2));
			StringBuffer buffer = new StringBuffer();
			String line;
			while((line = br.readLine()) != null) {
				buffer.append(line);
				buffer.append('\n');
			}
			br.close();
			
			return buffer.toString();
		} catch (IOException e) {
			throw new CoreException(new Status(IStatus.ERROR, TestPlugin.PLUGIN_ID, 0,e.getMessage(), e));
		}
	}
	
	public void startTest() {
		IDocument doc = getDocument();
		try {
			doc.replace(0, doc.getLength(), getFileText(inputFile));
		} catch (CoreException e) {
			e.printStackTrace();
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public IPageBookViewPage createPage(IConsoleView view) {
		// TODO Auto-generated method stub
		return null;
	}
	
	protected  IConsoleDocumentPartitioner getPartitioner() {
		return null;
	}
	
	public void end() {
		dispose();
	}
}
