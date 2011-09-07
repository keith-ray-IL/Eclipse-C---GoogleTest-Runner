package ch.hsr.ifs.cutelauncher.test.ui.sourceactions;

import junit.framework.TestSuite;
import ch.hsr.ifs.cutelauncher.ui.sourceactions.NewTestFunctionAction;

public class TestNewFunction extends Test1Skeleton {
	public TestNewFunction(String name) {
		super(name);
 	}
	public final void testNewTestFunctionAll(){
		ReadTestCase rtc=new ReadTestCase("testDefs/sourceActions/newTestfunction.txt");
		NewTestFunctionAction functionAction=new NewTestFunctionAction();
		for(int i=0;i<rtc.testname.size();i++){
			//if(i<rtc.testname.size()-1)continue;
			generateTest(rtc.testname.get(i),rtc.test.get(i),rtc.cursorpos.get(i).intValue(),rtc.expected.get(i),functionAction);
		}//skipped "at end2 with pushback duplicated" at position4 @see NewTestFunctionAction#createEdit 
	}
	public final static TestSuite generateNewFunctionTest(){
		TestSuite functorTS=new TestSuite("newTestFunction Tests");
		final ReadTestCase rtc1=new ReadTestCase("testDefs/sourceActions/newTestfunction.txt");
		final NewTestFunctionAction functionAction=new NewTestFunctionAction();
		for(int i=0;i<rtc1.testname.size();i++){
			final int j=i;
			String displayname=rtc1.testname.get(j).replaceAll("[()]", "*");//JUnit unable to display () as name
			junit.framework.TestCase test = new TestNewFunction("newTestFunction"+i+displayname) {
				@Override
				public void runTest() {
					generateTest(rtc1.testname.get(j),rtc1.test.get(j),rtc1.cursorpos.get(j).intValue(),rtc1.expected.get(j),functionAction);
				}
			};
			functorTS.addTest(test);
		}
		return functorTS;
	}
	
	public static TestSuite suite(boolean speedupMode){
		TestSuite result;
		if(speedupMode){
			result=new TestSuite("newTestFunction Tests");
			result.addTest(new TestNewFunction("testNewTestFunctionAll"));
		}else{
			result=generateNewFunctionTest();
		}
		//insert additional test here
		return result;
	}
	
}
