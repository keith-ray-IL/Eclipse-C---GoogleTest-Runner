package ch.hsr.ifs.cutelauncher.test.ui.sourceactions;

import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.core.runtime.Platform;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.TextEdit;

import ch.hsr.ifs.cutelauncher.ui.sourceactions.NewTestFunctionAction;
import ch.hsr.ifs.cutelauncher.ui.sourceactions.NewTestFunctionActionDelegate;

public class NewTestFunctionActionDelegateTest extends TestCase {

	String newLine=System.getProperty("line.separator");
	
	public NewTestFunctionActionDelegateTest(String name){
		super(name);
	}
	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	public final void testGetCursorEndPosition_beforePushback() {
		MultiTextEdit mEdit = new MultiTextEdit();
		
		TextEdit funct=NewTestFunctionAction.testOnlyCreatedEdit(72,newLine);
		TextEdit pushback=NewTestFunctionAction.testOnlyPushBackString(238,newLine);
		mEdit.addChild(funct);
		mEdit.addChild(pushback);
		TextEdit[] edits = mEdit.getChildren();
		
		int result=NewTestFunctionActionDelegate.testOnlyGetCursorEndPosition(edits,newLine,72,38,328);
				
		assertEquals(98,result);
	}

	public final void testGetCursorEndPosition_afterPushback() {
		MultiTextEdit mEdit = new MultiTextEdit();
				
		TextEdit funct=NewTestFunctionAction.testOnlyCreatedEdit(347,newLine);
		TextEdit pushback=NewTestFunctionAction.testOnlyPushBackString(238,newLine);
		mEdit.addChild(funct);
		mEdit.addChild(pushback);
		TextEdit[] edits = mEdit.getChildren();
				
		int result=NewTestFunctionActionDelegate.testOnlyGetCursorEndPosition(edits,newLine,347,38,238);
		
		String os = Platform.getOS();
		if(os.equals(Platform.OS_WIN32))assertEquals(411,result);
		if(os.equals(Platform.OS_LINUX))assertEquals(410,result);
		if(os.equals(Platform.OS_MACOSX))assertEquals(410,result);
	}
	public static TestSuite suite(){
		TestSuite ts=new TestSuite("NewTestFunctionActionDelegate");
		ts.addTest(new NewTestFunctionActionDelegateTest("testGetCursorEndPosition_beforePushback"));
		ts.addTest(new NewTestFunctionActionDelegateTest("testGetCursorEndPosition_afterPushback"));
		return ts;
	}
}
