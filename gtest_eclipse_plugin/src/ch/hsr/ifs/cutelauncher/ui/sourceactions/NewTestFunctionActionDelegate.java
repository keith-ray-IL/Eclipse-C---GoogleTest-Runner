/*******************************************************************************
 * Copyright (c) 2007 Institute for Software, HSR Hochschule für Technik
 * Rapperswil, University of applied sciences and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Emanuel Graf - initial API and implementation
 ******************************************************************************/
package ch.hsr.ifs.cutelauncher.ui.sourceactions;

import org.eclipse.jface.text.link.LinkedModeUI;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.TextEdit;


/**
 * @author Emanuel Graf
 *
 */
public class NewTestFunctionActionDelegate extends AbstractFunctionActionDelegate {
	
	public NewTestFunctionActionDelegate(){
		super("newTestFunction",new NewTestFunctionAction());
	}
	@Override
	int getCursorEndPosition(TextEdit[] edits, String newLine) {
		int result=edits[0].getOffset() + edits[0].getLength();
		for (TextEdit textEdit : edits) {
			String insert = ((InsertEdit)textEdit).getText();
			if(insert.contains(NewTestFunctionAction.TEST_STMT.trim())) {

				if(functionAction.insertFileOffset==-1 || //error check
				   functionAction.pushbackOffset==-1 ||   //error check	
				   functionAction.insertFileOffset < functionAction.pushbackOffset) //before pushback
				{
					result=(textEdit.getOffset() + insert.indexOf(NewTestFunctionAction.TEST_STMT.trim()));
					break;
				}else{
					result=(textEdit.getOffset() + insert.indexOf(NewTestFunctionAction.TEST_STMT.trim())+functionAction.pushbackLength );
					break;
				}
			}
		}
		return result;
	}

	@Override
	int getExitPositionLength(){
		return NewTestFunctionAction.TEST_STMT.trim().length();
	}
	
	
	
	
	
	public static int testOnlyGetCursorEndPosition(TextEdit[] edits, String newLine, int i,int j,int k){
		NewTestFunctionActionDelegate ntfad=new NewTestFunctionActionDelegate();
		ntfad.functionAction.testOnlyParameter(i,j,k);
		return ntfad.getCursorEndPosition(edits,newLine);
	}
	
	public LinkedModeUI testOnlyGetLinkedMode(){
		return linkedModeUI;
	}
	
}
