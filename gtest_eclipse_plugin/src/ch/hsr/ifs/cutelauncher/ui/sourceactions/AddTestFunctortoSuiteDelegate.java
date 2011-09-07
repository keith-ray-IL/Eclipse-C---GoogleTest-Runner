package ch.hsr.ifs.cutelauncher.ui.sourceactions;

import org.eclipse.text.edits.TextEdit;
public class AddTestFunctortoSuiteDelegate extends
		AbstractFunctionActionDelegate {
	public AddTestFunctortoSuiteDelegate(){
		super("AddTestFunctortoSuite",new AddTestFunctortoSuiteAction());
	}
	
	@Override
	int getCursorEndPosition(TextEdit[] edits, String newLine) {
		return 0;
	}

	@Override
	int getExitPositionLength() {
		return 0;
	}

}
//required to split action & delegate into separate physical file else 
//junit wouldnt be able to access the action class
//@see http://rcpquickstart.wordpress.com/2007/06/20/unit-testing-plug-ins-with-fragments/