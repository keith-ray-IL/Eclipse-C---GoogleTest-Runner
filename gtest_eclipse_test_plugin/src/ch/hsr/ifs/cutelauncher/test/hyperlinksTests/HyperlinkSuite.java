package ch.hsr.ifs.cutelauncher.test.hyperlinksTests;

import junit.framework.Test;
import junit.framework.TestSuite;

public class HyperlinkSuite {

	public static Test suite() {
		TestSuite suite = new TestSuite(
				"Hyperlink Suite");
		//$JUnit-BEGIN$
		suite.addTestSuite(HyperlinkTest.class);
		//$JUnit-END$
		return suite;
	}

}
