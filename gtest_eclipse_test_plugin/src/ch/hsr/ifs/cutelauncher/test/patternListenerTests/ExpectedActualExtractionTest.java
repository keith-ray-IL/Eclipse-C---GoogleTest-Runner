package ch.hsr.ifs.cutelauncher.test.patternListenerTests;

import junit.framework.TestCase;
import ch.hsr.ifs.cutelauncher.model.TestFailure;

public class ExpectedActualExtractionTest extends TestCase{
	String expression = "\"John Tangney\" == \"John Tangny\"	 expected:	 John Tangney 	but was: 	John Tangny";	
	TestFailure testFailure = new TestFailure(expression);
	
	public void testExpected() throws Exception {
		assertEquals("John Tangney", testFailure.getExpected());
	}
	public void testActual() throws Exception {
		assertEquals("John Tangny", testFailure.getWas());
	}
	public void testMessage() throws Exception {
		assertEquals("evaluated: `\"John Tangney\" == \"John Tangny\"`, expected: <John Tangney> but was: <John Tangny>", testFailure.getMsg());
	}
}
