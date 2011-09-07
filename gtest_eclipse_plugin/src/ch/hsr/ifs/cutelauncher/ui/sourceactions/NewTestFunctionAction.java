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

import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTExpressionList;
import org.eclipse.cdt.core.dom.ast.IASTFieldReference;
import org.eclipse.cdt.core.dom.ast.IASTFunctionCallExpression;
import org.eclipse.cdt.core.dom.ast.IASTIdExpression;
import org.eclipse.cdt.core.dom.ast.IASTLiteralExpression;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorStatement;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.IASTUnaryExpression;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFieldReference;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.part.FileEditorInput;

/**
 * @author Emanuel Graf
 *
 */
public class NewTestFunctionAction extends AbstractFunctionAction{
	
	protected static final String TEST_STMT = "\tASSERTM(\"start writing tests\", false);";
	int problemMarkerErrorLineNumber=0;
	
	@Override
	public MultiTextEdit createEdit(TextEditor ceditor,
			IEditorInput editorInput, IDocument doc, String funcName)
			throws CoreException {
		
		insertFileOffset=-1;
		pushbackOffset=-1;
		pushbackLength=-1;
		problemMarkerErrorLineNumber=0;
		
		MultiTextEdit mEdit = new MultiTextEdit();
		ISelection sel = ceditor.getSelectionProvider().getSelection();
		if (sel != null && sel instanceof TextSelection) {
			TextSelection selection = (TextSelection) sel;

			if (editorInput instanceof FileEditorInput) {
				IFile editorFile = ((FileEditorInput) editorInput).getFile();
				IASTTranslationUnit astTu = getASTTranslationUnit(editorFile);
				insertFileOffset = getInsertOffset(astTu, selection, doc);

				SuitePushBackFinder suitPushBackFinder = new SuitePushBackFinder();
				astTu.accept(suitPushBackFinder);
				
				mEdit.addChild(createdEdit(insertFileOffset, doc, funcName));

				if(!checkPushback(astTu,funcName,suitPushBackFinder))
				mEdit.addChild(createPushBackEdit(editorFile, doc, astTu,
						funcName, suitPushBackFinder));
				else{
					createProblemMarker((FileEditorInput) editorInput, "Duplicate Pushback name", problemMarkerErrorLineNumber);
				}
			}
		}
		return mEdit;
	}

	//adding the new test function
	private TextEdit createdEdit(int insertTestFuncFileOffset, IDocument doc, String funcName) {
		StringBuilder builder = new StringBuilder();
		builder.append("void ");
		builder.append(funcName);
		builder.append("(){");
		builder.append(newLine);
		builder.append(TEST_STMT);
		builder.append(newLine);
		builder.append("}");
		builder.append(newLine);
		builder.append(newLine);
		TextEdit iedit = new InsertEdit(insertTestFuncFileOffset, builder.toString());
		return iedit;
	}

	//checking existing suite for the name of the function
	//ensure it is not already added into suite
	private boolean checkPushback(IASTTranslationUnit astTu,String fname,SuitePushBackFinder suitPushBackFinder){
		if(suitPushBackFinder.getSuiteDeclName() != null) {
			IASTName name = suitPushBackFinder.getSuiteDeclName();
			IBinding binding = name.resolveBinding();
			IASTName[] refs = astTu.getReferences(binding);
			for (IASTName name1 : refs) {
				try{
					IASTFieldReference fRef = (ICPPASTFieldReference) name1.getParent().getParent();
					if(fRef.getFieldName().toString().equals("push_back")) {
						IASTFunctionCallExpression callex=(IASTFunctionCallExpression)name1.getParent().getParent().getParent();
						IASTExpression innercallex=callex.getParameterExpression();
						IASTFunctionCallExpression innercallex1=(IASTFunctionCallExpression)innercallex;
						IASTExpression thelist=innercallex1.getParameterExpression();
						String theName="";
						if(thelist!=null){
							if(thelist instanceof IASTExpressionList){//known issue:path executed during normal program run
								//**** block not executed in UNIT Test
								IASTExpression innerlist[]=((IASTExpressionList)thelist).getExpressions();
								IASTUnaryExpression unaryex=(IASTUnaryExpression)innerlist[1];
								IASTLiteralExpression literalex=(IASTLiteralExpression)unaryex.getOperand();
								theName=literalex.toString();
							}else{//path executed during unit testing
								theName=((IASTIdExpression)thelist).getName().toString();
							}
						}
						if(theName.equals(fname)){
							problemMarkerErrorLineNumber=name1.getFileLocation().getStartingLineNumber();
							return true;
						}
					}
					
				}catch(ClassCastException e){}
			}	
		}else{//TODO need to create suite
			
			//@see AbstractFunctionAction.getLastPushBack() for adding the very 1st push back
		}
		
		return false;
	}
	
	//shift the insertion point out syntactical block, relative to user(selection point/current cursor)location
	protected int getInsertOffset(IASTTranslationUnit astTu, TextSelection selection, IDocument doc) {
		int selOffset = selection.getOffset();
		IASTDeclaration[] decls = astTu.getDeclarations();
		for (IASTDeclaration declaration : decls) {
			int nodeOffset = declaration.getFileLocation().getNodeOffset();
			int nodeLength = declaration.getFileLocation().asFileLocation().getNodeLength();
			if(selOffset > nodeOffset && selOffset < (nodeOffset+ nodeLength)) {
				return (nodeOffset);
			}
		}

		//Shift out of preprocessor statements
		// >#include "cute.h<"
		IASTPreprocessorStatement[] listPreprocessor=astTu.getAllPreprocessorStatements();
		for(int x=0;x<listPreprocessor.length;x++){
			int nodeOffset = listPreprocessor[x].getFileLocation().getNodeOffset();
			int nodeLength = listPreprocessor[x].getFileLocation().asFileLocation().getNodeLength();
			if(selOffset > nodeOffset && selOffset < (nodeOffset+ nodeLength)) {
				return nodeOffset;
			}
		}

		try{
		int selectedLineNo=selection.getStartLine();
		IRegion iregion= doc.getLineInformation(selectedLineNo);
		String text=doc.get(iregion.getOffset(), iregion.getLength());
		if(text.startsWith("#include")){
			return iregion.getOffset();
		}
		
		}catch(org.eclipse.jface.text.BadLocationException be){}
		
		//just use the user selection if no match, it could possibly mean that the cursor at the 
		//very end of the source file
		return selOffset;
	}
	
	
	
	
	public static TextEdit testOnlyCreatedEdit(int insertTestFuncFileOffset,String newLine){
		NewTestFunctionAction ntfa=new NewTestFunctionAction();
		ntfa.setNewline(newLine);
		return ntfa.createdEdit(insertTestFuncFileOffset, null, "newTestFunction");
	}
	public static TextEdit testOnlyPushBackString(int insertloc,String newLine){
		NewTestFunctionAction ntfa=new NewTestFunctionAction();
		ntfa.setNewline(newLine);
		
		String s=ntfa.PushBackString("s","CUTE(newTestFunction)");
		StringBuilder builder = new StringBuilder();
		builder.append(s);
		
		InsertEdit edit = new InsertEdit(insertloc, builder.toString());
		return edit;
	}
}