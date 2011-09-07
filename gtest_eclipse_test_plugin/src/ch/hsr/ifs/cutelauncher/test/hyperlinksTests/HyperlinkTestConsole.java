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
package ch.hsr.ifs.cutelauncher.test.hyperlinksTests;

import java.util.Vector;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.ui.console.IHyperlink;

import ch.hsr.ifs.cutelauncher.test.internal.console.FileInputTextConsole;

/**
 * @author Emanuel Graf
 *
 */
public class HyperlinkTestConsole extends FileInputTextConsole {
	
	private Vector<TestHyperlinks> links = new Vector<TestHyperlinks>();

	public HyperlinkTestConsole(String inputFile) {
		super(inputFile);
	}

	@Override
	public void addHyperlink(IHyperlink hyperlink, int offset, int length) throws BadLocationException {
		super.addHyperlink(hyperlink, offset, length);
		links.add(new TestHyperlinks(hyperlink, offset, length));
	}

	public Vector<TestHyperlinks> getLinks() {
		return links;
	}
	
	

}
