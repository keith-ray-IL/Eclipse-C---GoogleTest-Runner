package ch.hsr.ifs.cutelauncher.test.ui.sourceactions;

import java.util.Map;

import junit.framework.TestSuite;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.part.FileEditorInput;

import ch.hsr.ifs.cutelauncher.ui.sourceactions.AbstractFunctionAction;
import ch.hsr.ifs.cutelauncher.ui.sourceactions.AddTestFunctiontoSuiteAction;
import ch.hsr.ifs.cutelauncher.ui.sourceactions.AddTestFunctortoSuiteAction;
import ch.hsr.ifs.cutelauncher.ui.sourceactions.AddTestMembertoSuiteAction;
import ch.hsr.ifs.cutelauncher.ui.sourceactions.IAddMemberContainer;
import ch.hsr.ifs.cutelauncher.ui.sourceactions.IAddMemberMethod;
import ch.hsr.ifs.cutelauncher.ui.sourceactions.NewTestFunctionAction;

public class TestProblemMarkers extends Test1Skeleton {
	static ReadTestCase rtc=new ReadTestCase("testDefs/sourceActions/ProblemMarkers.cpp");

	public TestProblemMarkers(String name) {
		super(name);
	}
	
	public final void testNewTestFunction(){
		final NewTestFunctionAction functionAction=new NewTestFunctionAction();
		int i=0;
		
		generateProblemMarkerTest(rtc, functionAction, i);
		
	}
	public final void testAddTestFunction(){
		final AddTestFunctiontoSuiteAction functionAction=new AddTestFunctiontoSuiteAction();
		int i=1;
		
		generateProblemMarkerTest(rtc, functionAction, i);
	}
	public final void testAddTestFunctor(){
		final AddTestFunctortoSuiteAction functionAction=new AddTestFunctortoSuiteAction();
		int i=2;
		
		generateProblemMarkerTest(rtc, functionAction, i);
	}
	
	
	public final void testAddMember(){
		final AddTestMembertoSuiteAction functionAction=new AddTestMembertoSuiteAction();
		int i=3;
		
		functionAction.setUnitTestingMode(makeMockObject(rtc.parameter.get(i)));
		
		String parameters[]=rtc.parameter.get(i).trim().split(" ");
		generateProblemMarkerTest(rtc, functionAction, i,Integer.valueOf(parameters[3]));
	}
	public final static IAddMemberMethod makeMockObject(String parameter){
		System.out.println("["+parameter +"]");
		String parameters[]=parameter.trim().split(" ");
		StubContainer container=new StubContainer(parameters[0],parameters[2].equals("C")?IAddMemberContainer.ClassType:IAddMemberContainer.InstanceType);
		StubMethod method=new StubMethod(parameters[1],container);
		return method;
	}
	
	
	
	protected void generateProblemMarkerTest(ReadTestCase rtc,
			AbstractFunctionAction functionAction, int i){
		
		int expectedLineNumber=Integer.valueOf(rtc.parameter.get(i).trim()).intValue();
		generateProblemMarkerTest(rtc, functionAction, i, expectedLineNumber);
	}
	
	protected void generateProblemMarkerTest(ReadTestCase rtc,
			AbstractFunctionAction functionAction, int i,int expectedLineNumber) {
		
		generateTest(rtc.testname.get(i),rtc.test.get(i),rtc.cursorpos.get(i).intValue(),rtc.expected.get(i),functionAction);
				
		IEditorInput editorInput = ceditor.getEditorInput();
		IFile editorFile = ((FileEditorInput)editorInput).getFile();
		
		boolean flag=false;
		IMarker[] problems = null;
		int depth = IResource.DEPTH_INFINITE;
		try {
		   problems = editorFile.findMarkers(IMarker.PROBLEM, true, depth);
		   
		   for(IMarker marker:problems){
			   String msg=(String)marker.getAttribute(IMarker.MESSAGE);
			   Map map=marker.getAttributes();
			  
			   if(msg!=null && msg.startsWith("cute:")){
				   int lineno=((Integer)marker.getAttribute(IMarker.LINE_NUMBER,-1)).intValue();
				   
				   //check at least one problem marker is on the expected line no
				   if(expectedLineNumber==lineno)flag=true;
			   }
		   }
		} catch (CoreException e) {
			fail("CoreException"+e.getMessage());
		}
		assertTrue(flag);
	}
	
	@Override
	public void tearDown() throws Exception{
		IEditorInput editorInput = ceditor.getEditorInput();
		IFile editorFile = ((FileEditorInput)editorInput).getFile();
		
		IMarker[] problems = null;
		int depth = IResource.DEPTH_INFINITE;
		
		problems = editorFile.findMarkers(IMarker.PROBLEM, true, depth);
		   
		for(IMarker marker:problems){
			marker.delete();
		}
		
		super.tearDown();
	}
	
	public static TestSuite suite(){
		TestSuite result=new TestSuite("Problem markers test");
		
		result.addTest(new TestProblemMarkers("testNewTestFunction"));
		result.addTest(new TestProblemMarkers("testAddTestFunction"));
		result.addTest(new TestProblemMarkers("testAddTestFunctor"));
		result.addTest(new TestProblemMarkers("testAddMember"));
		return result;
	}
	
}
