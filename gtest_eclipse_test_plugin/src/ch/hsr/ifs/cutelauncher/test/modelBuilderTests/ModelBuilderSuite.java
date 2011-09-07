package ch.hsr.ifs.cutelauncher.test.modelBuilderTests;

import junit.framework.Test;
import junit.framework.TestSuite;

public class ModelBuilderSuite {

	public static Test suite() {
		TestSuite suite = new TestSuite(
				"Test for ch.hsr.ifs.cutelauncher.test.modelBuilderTests");
		// $JUnit-BEGIN$
		suite.addTest(ModelBuilderTest.suite("sessionTest.txt"));
		suite.addTest(ModelBuilderTest.suite("suiteTest.txt"));
		suite.addTest(ModelBuilderTest.suite("failedTest.txt"));
		suite.addTest(ModelBuilderTest.suite("failedEqualsTest.txt"));
		suite.addTest(ModelBuilderTest.suite("errorTest.txt"));
		suite.addTest(ModelBuilderTest.suite("successTest.txt"));
		// $JUnit-END$
		return suite;

		// WHAT ARE THESE TESTS TRYING TO DO? What value do they add? -Bria
		// suite.addTest(ModelBuilderTest.suite( "suiteTest2.txt"));
		// suite.addTest(ModelBuilderTest.suite( "suiteTest3.txt"));
		// suite.addTest(ModelBuilderTest.suite( "suiteTest4.txt"));
		// suite.addTest(ModelBuilderTest.suite( "suiteTest5.txt"));
		// suite.addTest(ModelBuilderTest.suite( "suiteTest6.txt"));
	}

}
