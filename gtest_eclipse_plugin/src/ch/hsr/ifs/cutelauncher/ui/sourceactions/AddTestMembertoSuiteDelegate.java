package ch.hsr.ifs.cutelauncher.ui.sourceactions;

import org.eclipse.text.edits.TextEdit;

public class AddTestMembertoSuiteDelegate extends
		AbstractFunctionActionDelegate {
	public AddTestMembertoSuiteDelegate(){
		super("AddTestMember",new AddTestMembertoSuiteAction());
	}
	
	
	@Override
	int getCursorEndPosition(TextEdit[] edits, String newLine) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	int getExitPositionLength() {
		// TODO Auto-generated method stub
		return 0;
	}
	
}
/*http://www.eclipsezone.com/eclipse/forums/t107165.html*/
/*
a tree
recursively 
display classes/structs (static methods, or inner stuff) 

@see http://www.eclipse.org/articles/Article-TreeViewer/TreeViewerArticle.htm

* if static method of a class selected without parameters add it 
* if an instance is selected display the methods and superclass methods also
*/


