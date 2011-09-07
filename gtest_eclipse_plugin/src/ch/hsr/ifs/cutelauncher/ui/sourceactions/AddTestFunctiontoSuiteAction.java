package ch.hsr.ifs.cutelauncher.ui.sourceactions;

import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTParameterDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDeclarator;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.part.FileEditorInput;

public class AddTestFunctiontoSuiteAction extends AddTestFunct_ION_OR{
	
	IEditorInput editorInput;
	@Override
	public MultiTextEdit createEdit(TextEditor ceditor,
			IEditorInput editorInput, IDocument doc, String funcName)
			throws CoreException{
		
		this.editorInput=editorInput;
		MultiTextEdit mEdit = new MultiTextEdit();
		ISelection sel = ceditor.getSelectionProvider().getSelection();
		if (sel != null && sel instanceof TextSelection) {
			TextSelection selection = (TextSelection) sel;
			problemMarkerErrorLineNumber=selection.getStartLine()+1;
			
			if (editorInput instanceof FileEditorInput) {
				IFile editorFile = ((FileEditorInput) editorInput).getFile();
				IASTTranslationUnit astTu = getASTTranslationUnit(editorFile);
				SuitePushBackFinder suitPushBackFinder = new SuitePushBackFinder();
				astTu.accept(suitPushBackFinder);

				String fname=getFunctionNameAtCursor(astTu, selection,suitPushBackFinder);

				if(!dontAddFlag)
					if(!checkPushback(astTu,fname,suitPushBackFinder))
					{
					mEdit.addChild(createPushBackEdit(editorFile, doc, astTu,
						fname, suitPushBackFinder));
					}else{
						createProblemMarker((FileEditorInput) editorInput, 
								"unable to add test function. Duplicate Pushback name", problemMarkerErrorLineNumber);
					}
			}
		}
		return mEdit;
	}
	boolean dontAddFlag=false;
	//find function name within selected cursor location
	public String getFunctionNameAtCursor(IASTTranslationUnit astTu,TextSelection selection,
			SuitePushBackFinder suitPushBackFinder){
		IASTDeclaration selectedNode=getDeclarationAtCursor(astTu,selection);
		IASTNode node = selectedNode;
		dontAddFlag=false;
		while(node != null) {
			if (node instanceof IASTFunctionDefinition) {
				IASTFunctionDefinition functionDefinition=(IASTFunctionDefinition)node;
				ICPPASTFunctionDeclarator fdeclarator=(ICPPASTFunctionDeclarator)functionDefinition.getDeclarator();
				IASTParameterDeclaration fpara[]=fdeclarator.getParameters();
				
				IASTSimpleDeclSpecifier specifier=(IASTSimpleDeclSpecifier)functionDefinition.getDeclSpecifier();
				//check for 'void'
				if(specifier.getType()!=IASTSimpleDeclSpecifier.t_void ||
						//don't add the function that cute::suite was declared in, else recursive loop 
						node.contains(suitPushBackFinder.getSuiteNode())||
						fdeclarator.takesVarArgs() ||
						fpara.length>0
						){
//					EclipseConsole.print("Unable to perform AddTestFunctiontoSuite");
					
					boolean condition[]={
						specifier.getType()!=IASTSimpleDeclSpecifier.t_void,	
						node.contains(suitPushBackFinder.getSuiteNode()),
						fdeclarator.takesVarArgs(),
						fpara.length>0
					};
					postErrorMarker(condition);
					
					dontAddFlag=true;
					return "";
				}
				IASTName name=functionDefinition.getDeclarator().getName();
				return name.toString();
			}
			node = node.getParent();
		}
		dontAddFlag=true;
		return "";
	}

	protected void postErrorMarker(boolean condition[]){
		StringBuilder result=new StringBuilder();
		if(condition[0])result.append("(return not void)");
		if(condition[1])result.append("(unable to add function where cute::suite was declared)");
		if(condition[2])result.append("(function taking variable parameters)");
		if(condition[3])result.append("(function with parameters)");
		
		createProblemMarker((FileEditorInput) editorInput, "fail to AddTestFunctiontoSuite "+result, problemMarkerErrorLineNumber);
	}
	
	protected IASTDeclaration getDeclarationAtCursor(IASTTranslationUnit astTu, TextSelection selection) {
		int selOffset = selection.getOffset();
		IASTDeclaration[] decls = astTu.getDeclarations();
		for (IASTDeclaration declaration : decls) {
			int nodeOffset = declaration.getFileLocation().getNodeOffset();
			int nodeLength = declaration.getFileLocation().asFileLocation().getNodeLength();
			if(selOffset > nodeOffset && selOffset < (nodeOffset+ nodeLength)) {
				return declaration;
			}
		}
		return null;
	}
}