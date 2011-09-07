package ch.hsr.ifs.cutelauncher.ui.sourceactions;

import org.eclipse.cdt.core.CCorePlugin;
import org.eclipse.cdt.core.dom.ast.IASTFieldReference;
import org.eclipse.cdt.core.dom.ast.IASTFileLocation;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTStatement;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFieldReference;
import org.eclipse.cdt.core.index.IIndex;
import org.eclipse.cdt.core.model.CoreModelUtil;
import org.eclipse.cdt.core.model.ITranslationUnit;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.part.FileEditorInput;

public abstract class AbstractFunctionAction {
	protected int insertFileOffset=-1; //for NewTestFunctionAction use only, need to reset value in createEdit
	protected int pushbackOffset=-1;   //for NewTestFunctionAction use only, need to reset value in createEdit
	protected int pushbackLength=-1;   //for NewTestFunctionAction use only, need to reset value in createEdit
	int problemMarkerErrorLineNumber=0;
	
	public abstract MultiTextEdit createEdit(TextEditor ceditor,
			IEditorInput editorInput, IDocument doc, String funcName)
			throws CoreException;
	String newLine;
	public void setNewline(String newline){this.newLine=newline;}
	
	//return the CDT representation of the file under modification 
	protected IASTTranslationUnit getASTTranslationUnit(IFile editorFile)
			throws CoreException {
		ITranslationUnit tu = CoreModelUtil.findTranslationUnit(editorFile);
		IIndex index = CCorePlugin.getIndexManager().getIndex(tu.getCProject());	
		IASTTranslationUnit astTu = tu.getAST(index, ITranslationUnit.AST_SKIP_INDEXED_HEADERS);
		return astTu;
	}

	protected TextEdit createPushBackEdit(IFile editorFile, IDocument doc, IASTTranslationUnit astTu, String funcName, SuitePushBackFinder suitPushBackFinder) {
		StringBuilder builder = new StringBuilder();
		builder.append(PushBackString(suitPushBackFinder.getSuiteDeclName().toString(),"CUTE("+funcName+")"));
		return createPushBackEdit(editorFile,doc,astTu,suitPushBackFinder,builder);
	}
	protected String PushBackString(String suite, String insidePushback){
		StringBuilder builder = new StringBuilder();
		builder.append(newLine+"\t");
		builder.append(suite.toString());
		builder.append(".push_back(");
		builder.append(insidePushback);
		builder.append(");");
		return builder.toString();
	}
	
	protected TextEdit createPushBackEdit(IFile editorFile, IDocument doc, IASTTranslationUnit astTu, SuitePushBackFinder suitPushBackFinder, StringBuilder builder) {
				
		if(suitPushBackFinder.getSuiteDeclName() != null) {
			IASTName name = suitPushBackFinder.getSuiteDeclName();
			IBinding binding = name.resolveBinding();
			IASTName[] refs = astTu.getReferences(binding);
			IASTStatement lastPushBack = getLastPushBack(refs);

			IASTFileLocation fileLocation; 
			if(lastPushBack != null) {
				fileLocation = lastPushBack.getFileLocation();
			}else {//case where no push_back was found, use cute::suite location 
				fileLocation = suitPushBackFinder.getSuiteNode().getParent().getFileLocation();
			}
			pushbackOffset=fileLocation.getNodeOffset() + fileLocation.getNodeLength();
			InsertEdit edit = new InsertEdit(pushbackOffset, builder.toString());
			pushbackLength=builder.toString().length();
			
			return edit;
		}else {
			//TODO case of no cute::suite found
			
			return null;
		}
	}
	
	/*find the point of last "push_back" */
	protected IASTStatement getLastPushBack(IASTName[] refs) {
		IASTName lastPushBack = null;
		for (IASTName name : refs) {
			if(name.getParent().getParent() instanceof ICPPASTFieldReference) {
				IASTFieldReference fRef = (ICPPASTFieldReference) name.getParent().getParent();
				if(fRef.getFieldName().toString().equals("push_back")) {
					lastPushBack = name;
				}
			}
		}
		return getParentStatement(lastPushBack);
	}

	protected IASTStatement getParentStatement(IASTName lastPushBack) {
		IASTNode node = lastPushBack;
		while(node != null) {
			if (node instanceof IASTStatement) {
				return (IASTStatement) node;
			}
			node = node.getParent();
		}
		return null;
	}
	
	public void createProblemMarker(FileEditorInput editorInput,String message,int lineNo){
		
		try {
			IFile editorFile = (editorInput).getFile();
			IMarker marker = editorFile.createMarker("org.eclipse.cdt.core.problem");
		    marker.setAttribute(IMarker.MESSAGE, "cute:"+message);
		    marker.setAttribute(IMarker.PRIORITY, IMarker.PRIORITY_HIGH);
		    marker.setAttribute(IMarker.TRANSIENT, true);
		    if(lineNo!=0)marker.setAttribute(IMarker.LINE_NUMBER, lineNo);
		    marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
	   } catch (CoreException e) {
	      // You need to handle the cases where attribute value is rejected
	   }
	}
	
	
	
	public void testOnlyParameter(int insertFileOffset,int pushbackLength,int pushbackOffset ){
		this.insertFileOffset=insertFileOffset;
		this.pushbackOffset=pushbackOffset;
		this.pushbackLength=pushbackLength;
	}
	
}
//http://www.ibm.com/developerworks/library/os-ecl-cdt3/index.html?S_TACT=105AGX44&S_CMP=EDU
//Building a CDT-based editor