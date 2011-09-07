package ch.hsr.ifs.cutelauncher.ui.sourceactions;

import org.eclipse.text.edits.TextEdit;
public class AddTestFunctiontoSuiteDelegate extends AbstractFunctionActionDelegate {
	
	public AddTestFunctiontoSuiteDelegate(){
		super("AddTestFunctiontoSuite",new AddTestFunctiontoSuiteAction());
	}
	@Override
	int getCursorEndPosition(TextEdit[] edits, String newLine) {return 0;}
	@Override
	int getExitPositionLength(){return 0;}
}
