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

import org.eclipse.ui.console.IHyperlink;

/**
 * @author Emanuel Graf
 *
 */
public class TestHyperlinks {
	
	private IHyperlink link;
	
	private int offset;
	
	private int length;

	public TestHyperlinks(IHyperlink link, int offset, int length) {
		super();
		this.link = link;
		this.offset = offset;
		this.length = length;
	}

	public int getLength() {
		return length;
	}

	public IHyperlink getLink() {
		return link;
	}

	public int getOffset() {
		return offset;
	}
	
	

}
