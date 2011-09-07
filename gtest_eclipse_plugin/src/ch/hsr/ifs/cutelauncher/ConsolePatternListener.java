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
 * Industrial Logic, Inc.:  Mike Bria & John Tangney - enhancements to support additional C++ unit testing frameworks, such as GTest
 ******************************************************************************/
package ch.hsr.ifs.cutelauncher;

import java.util.List;
import java.util.Vector;
import java.util.regex.Pattern;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.ui.console.IPatternMatchListener;
import org.eclipse.ui.console.PatternMatchEvent;
import org.eclipse.ui.console.TextConsole;

import ch.hsr.ifs.cutelauncher.event.ConsoleEventParser;
import ch.hsr.ifs.cutelauncher.event.TestEvent;
import ch.hsr.ifs.cutelauncher.event.TestEventHandler;

public class ConsolePatternListener implements IPatternMatchListener {
	private TextConsole console;
	private ConsoleEventParser eventParser;
	private Vector<TestEventHandler> handlers;

	public ConsolePatternListener(ConsoleEventParser consoleEventParser) {
		eventParser = consoleEventParser;
		handlers = new Vector<TestEventHandler>();
	}

	public int getCompilerFlags() {
		return Pattern.UNIX_LINES;
	}

	public String getLineQualifier() {
		return eventParser.getLineQualifier();
	}

	public String getPattern() {
		return eventParser.getComprehensiveLinePattern();
	}

	public void connect(TextConsole console) {
		this.console = console;
		for (TestEventHandler handler : handlers) {
			handler.handleSessionStart();
		}
	}

	public void disconnect() {
		for (TestEventHandler handler : handlers) {
			handler.handleSessionEnd();
		}
		console = null;
	}

	public void addHandler(TestEventHandler handler) {
		handlers.add(handler);
	}

	public void removeHandler(TestEventHandler handler) {
		handlers.remove(handler);
	}

	public void matchFound(PatternMatchEvent event) {
		try {
			IDocument doc = console.getDocument();
			IRegion reg = doc.getLineInformation(doc.getLineOfOffset(event
					.getOffset()));
			String line = doc.get(reg.getOffset(), reg.getLength());

			processTestEventsFrom(reg, line);
		} catch (BadLocationException e) {
			throw new RuntimeException(e);			
		}
	}

	private void processTestEventsFrom(IRegion reg, String line) {
		List<TestEvent> testEvents = eventParser.eventsFrom(reg, line);
		for (TestEvent testEvent : testEvents) {
			process(testEvent);
		}
	}

	private void process(TestEvent testEvent) {
		for (TestEventHandler handler : handlers) {
			handler.handle(testEvent);
		}
	}
}
