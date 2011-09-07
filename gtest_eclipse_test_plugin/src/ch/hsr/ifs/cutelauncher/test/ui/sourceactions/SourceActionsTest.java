package ch.hsr.ifs.cutelauncher.test.ui.sourceactions;

import junit.framework.Test;
import junit.framework.TestSuite;
public class SourceActionsTest {

	public static Test suite(){
		TestSuite ts=new TestSuite("ch.hsr.ifs.cutelauncher.ui.sourceactions");

		ts.addTest(MemoryEFS.suite());
		
		boolean speedupMode=false;
		ts.addTest(TestNewFunction.suite(speedupMode));
		ts.addTest(TestAddFunction.suite(speedupMode));
		ts.addTest(TestAddFunctor.suite(speedupMode));
				
		TestSuite addMemberTS=TestAddMemberTree.suite();
		TestAddMember.generateMemberTest(addMemberTS);
		ts.addTest(addMemberTS);
		
//		ts.addTest(new TestAddFunction("testDisplayDynamicProxyRecordedResult"));
		
		ts.addTest(NewTestFunctionActionDelegateTest.suite());
		ts.addTest(TestProblemMarkers.suite());
		ts.addTest(TestBugFixes.suite());
		
		return ts;
	}
	
}
//future instead of testing text, parsing AST might bring more flexibility 
/* Random possible useful snippet 
	ITranslationUnit tu= CoreModelUtil.findTranslationUnit(inputFile);
	IIndex index = CCorePlugin.getIndexManager().getIndex(tu.getCProject());	
	IASTTranslationUnit astTu = tu.getAST(index, ITranslationUnit.AST_SKIP_INDEXED_HEADERS);

		CCorePlugin.getIndexManager().setIndexerId(cproject,
				IPDOMManager.ID_FAST_INDEXER);
		//TestPlugin.getDefault().getLog().addLogListener(this);
		//Activator.getDefault().getLog().addLogListener(this);
		CCorePlugin.getIndexManager().reindex(cproject);
		boolean joined = CCorePlugin.getIndexManager().joinIndexer(
				IIndexManager.FOREVER, NULL_PROGRESS_MONITOR);
		assertTrue(joined);

*/
//////////////////////////////////////////////
// Design document
/* This piece of code will generate and run the test, but on first failure stop.
 * advantage is that there is only one test case, instead of 20 thus reduced scrolling in the junit window. 
 * this is faster in performance then create test case. which introduces about 2 extra sec.
 */ /*
@deprecated
public final void testAddTestFunctorAll(){
	rtc=new ReadTestCase("testDefs/sourceActions/addTestfunctor.cpp");
	//AddTestFunctiontoSuiteAction functionAction=new AddTestFunctiontoSuiteAction();
	AddTestFunctortoSuiteAction functionAction=new AddTestFunctortoSuiteAction();
	for(int i=0;i<rtc.testname.size();i++){
		generateTest(rtc.testname.get(i),rtc.test.get(i),rtc.cursorpos.get(i).intValue(),rtc.expected.get(i),functionAction);
	}
}

*/
//FIXME JUnit dblclick not working as tests doesnt have direct src mapping, if double click jmp to test file?

/*class mySourceRange extends SourceRange{//for setting current cursor position
public mySourceRange(int offset){
	super(offset,0);
}
}*/
