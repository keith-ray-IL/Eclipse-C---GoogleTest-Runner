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
package ch.hsr.ifs.cutelauncher.test.modelBuilderTests;

import java.util.Vector;

import junit.framework.Test;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;

import ch.hsr.ifs.cutelauncher.CuteLauncherPlugin;
import ch.hsr.ifs.cutelauncher.ConsolePatternListener;
import ch.hsr.ifs.cutelauncher.model.ModellBuilder;
import ch.hsr.ifs.cutelauncher.model.TestCase;
import ch.hsr.ifs.cutelauncher.model.TestElement;
import ch.hsr.ifs.cutelauncher.model.TestFailure;
import ch.hsr.ifs.cutelauncher.model.TestResult;
import ch.hsr.ifs.cutelauncher.model.TestSession;
import ch.hsr.ifs.cutelauncher.model.TestSuite;
import ch.hsr.ifs.cutelauncher.test.ConsoleTest;

/**
 * @author Emanuel Graf
 *
 */
public class ModelBuilderTest extends ConsoleTest {
	
	private static final String SEPARATOR = ", ";
	private String inputFile;

	public ModelBuilderTest(String inputFile) {
		super();
		this.inputFile = inputFile;
	}
	
	public static Test suite(String inputFile) {
		String testName = inputFile.split("\\.")[0];
		junit.framework.TestSuite suite = new junit.framework.TestSuite(testName);
		suite.addTest(new ModelBuilderTest(inputFile));
		return suite;
	}

	@Override
	protected void addTestEventHandler(ConsolePatternListener lis) {
		lis.addHandler(new ModellBuilder(new Path("")));
	}
	
	protected String getExpected() throws CoreException {
		return firstConsoleLine();
	}

	@Override
	public String getName() {
		return inputFile;
	}

	@Override
	protected void runTest() throws Throwable {
		TestSession session = CuteLauncherPlugin.getModel().getSession();
		assertEquals(getExpected(), getSessionString(session));
	}

	private String getSessionString(TestSession session) {
		StringBuffer sb = new StringBuffer();
		sb.append("Session{");
		Vector<TestElement> rootElements = session.getRootElements();
		writeElements(sb, rootElements);
		sb.append('}');
		return sb.toString();
	}

	private void writeTestCase(TestCase tcase, StringBuffer sb) {
		sb.append("Testcase(");
		sb.append(tcase.getName());
		sb.append(SEPARATOR);
		sb.append(tcase.getStatus().toString());
		sb.append(SEPARATOR);
		sb.append(tcase.getFile());
		sb.append(SEPARATOR);
		sb.append(tcase.getLineNumber());
		sb.append(SEPARATOR);
		writeTestResult(tcase.getResult(), sb);
		sb.append(')');
	}

	private void writeTestResult(TestResult result, StringBuffer sb) {
		sb.append("Result(");
		sb.append(result.getMsg());
		if (result instanceof TestFailure) {
			TestFailure failure = (TestFailure) result;
			sb.append(SEPARATOR);
			sb.append(failure.getExpected());
			sb.append(SEPARATOR);
			sb.append(failure.getWas());
		}
		sb.append(')');
		
	}

	private void writeSuite(TestSuite suite, StringBuffer sb) {
		sb.append("Suite(");
		sb.append(suite.getName());
		sb.append(SEPARATOR);
		sb.append(suite.getStatus().toString());
		sb.append(SEPARATOR);
		sb.append(suite.getTotalTests());
		sb.append(SEPARATOR);
		sb.append(suite.getRun());
		sb.append(SEPARATOR);
		sb.append(suite.getSuccess());
		sb.append(SEPARATOR);
		sb.append(suite.getFailure());
		sb.append(SEPARATOR);
		sb.append(suite.getError());
		sb.append("){");
		Vector<TestElement> elements = suite.getElements();
		writeElements(sb, elements);
		sb.append('}');
		
	}

	private void writeElements(StringBuffer sb, Vector<TestElement> elements) {
		for (TestElement element : elements) {
			if (element instanceof TestSuite) {
				TestSuite suite1 = (TestSuite) element;
				writeSuite(suite1, sb);
			}else if (element instanceof TestCase) {
				TestCase tcase = (TestCase) element;
				writeTestCase(tcase, sb);
			}
		}
	}

	@Override
	public String getInputFilePath() {
		return "modelBuilderTests/" + inputFile;
	}

}
