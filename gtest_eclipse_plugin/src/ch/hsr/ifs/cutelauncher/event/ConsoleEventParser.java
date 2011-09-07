/*******************************************************************************
 * Copyright (c) 2008, Industrial Logic, Inc. All Rights Reserved. 
 * 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 * 
 * Contributors: 
 * Industrial Logic, Inc.:  Mike Bria & John Tangney - initial implementation (based on ideas originating from the work of Emanuel Graff)
 ******************************************************************************/
package ch.hsr.ifs.cutelauncher.event;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.text.IRegion;

import ch.hsr.ifs.cutelauncher.CuteLauncherPlugin;

public abstract class ConsoleEventParser {
	protected List<TestEvent> testEvents;

	public abstract String getComprehensiveLinePattern();

	public abstract String getLineQualifier();

	protected abstract void extractTestEventsFor(IRegion reg, String line)
			throws CoreException;

	public List<TestEvent> eventsFrom(IRegion reg, String line) {
		freshTestEventCollection();
		try {
			extractTestEventsFor(reg, line);
		} catch (CoreException e) {
			CuteLauncherPlugin.getDefault().getLog().log(e.getStatus());
			throwLineParsingException(reg, line, e);
		} catch (Exception e) {
			throwLineParsingException(reg, line, e);
		}		
		
		return testEvents;
	}

	protected void freshTestEventCollection() {
		testEvents = new ArrayList<TestEvent>();
	}
	
	protected void addNewEvent(TestEvent testEvent) {
		testEvents.add(testEvent);
	}
	
	protected void throwLineParsingException(IRegion reg, String line,
			Exception e) {
		throw new RuntimeException("Failure parsing console event {<line="
				+ line + ">, <Reg=" + reg
				+ ">} into TestEvent.  Check log for more information.", e);
	}

	protected Matcher matcherFor(Pattern pattern, String line)
			throws CoreException {
		Matcher m = pattern.matcher(line);
		if (!m.matches()) {
			throw new CoreException(new Status(Status.ERROR,
					CuteLauncherPlugin.PLUGIN_ID, 1, "Pattern doesn't match",
					null));
		}
		return m;
	}

	protected static String regExUnion(String[] fragments) {
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < fragments.length; i++) {
			if (i > 0)
				buffer.append("|");
			buffer.append(fragments[i]);
		}
		return buffer.toString();
	}

	protected static String escapeForRegex(String string) {
		return string.replace("]", "\\]").replace("[", "\\[");
	}
}