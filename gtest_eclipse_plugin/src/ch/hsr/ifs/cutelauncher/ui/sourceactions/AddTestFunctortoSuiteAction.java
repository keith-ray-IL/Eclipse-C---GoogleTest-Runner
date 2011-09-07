package ch.hsr.ifs.cutelauncher.ui.sourceactions;

import java.util.ArrayList;

import org.eclipse.cdt.core.dom.ast.IASTCompositeTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTCompositeTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTTemplateDeclaration;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTVisibilityLabel;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.console.MessageConsoleStream;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.part.FileEditorInput;

import ch.hsr.ifs.cutelauncher.EclipseConsole;

public class AddTestFunctortoSuiteAction extends AddTestFunct_ION_OR{
	private boolean constructorNeedParameterFlag=false;
	private final MessageConsoleStream stream = EclipseConsole.getConsole();
	
	@Override
	public MultiTextEdit createEdit(TextEditor ceditor,
			IEditorInput editorInput, IDocument doc, String funcName)
			throws CoreException{
		
		ISelection sel = ceditor.getSelectionProvider().getSelection();
		if (sel != null && sel instanceof TextSelection) {
			TextSelection selection = (TextSelection) sel;
			problemMarkerErrorLineNumber=selection.getStartLine()+1;
			
			if (editorInput instanceof FileEditorInput) {
				IFile editorFile = ((FileEditorInput) editorInput).getFile();
				IASTTranslationUnit astTu = getASTTranslationUnit(editorFile);
				
				//FIXME merge the visitors
				NodeAtCursorFinder n= new NodeAtCursorFinder(selection.getOffset());
				astTu.accept(n);
				
				OperatorParenthesesFinder o=new OperatorParenthesesFinder();
				astTu.accept(o);
				
//				ArrayList al=o.getAL();
								
				String fname=nameAtCursor(o.getAL(),n.getNode(),stream);
				if(fname.equals(""))return new MultiTextEdit();//FIXME potential bug point

				constructorNeedParameterFlag=checkForConstructorWithParameters(astTu,n.getNode());
				
				SuitePushBackFinder suitPushBackFinder = new SuitePushBackFinder();
				astTu.accept(suitPushBackFinder);
				
				if(!checkPushback(astTu,fname,suitPushBackFinder)){
					MultiTextEdit mEdit = new MultiTextEdit();
					
					StringBuilder builder = new StringBuilder();
					
					String insidePushback;
					if(constructorNeedParameterFlag)insidePushback=(fname+"(pArAmEtRs_ReQuIrEd)");
					else insidePushback=(fname+"()");
					builder.append(PushBackString(suitPushBackFinder.getSuiteDeclName().toString(),insidePushback));
					
					mEdit.addChild(createPushBackEdit(editorFile, doc, astTu,
							suitPushBackFinder,builder));
					return mEdit;
				}else{
					createProblemMarker((FileEditorInput) editorInput, 
							"unable to add test functor. Duplicate Pushback name", problemMarkerErrorLineNumber);
				}
			}
		}
		return new MultiTextEdit();
	}
	
	protected String nameAtCursor(ArrayList<IASTName> operatorParenthesesNode,IASTNode node,MessageConsoleStream stream ){
		if(node instanceof IASTDeclaration){
			if(node instanceof ICPPASTVisibilityLabel){
				//public: private: protected: for class
				node=node.getParent().getParent();
				//FIXME operator() is private,protected in a class/struct??
			}
			
			try{
			
				boolean flag=checkClassForPublicOperatorParentesis(node);
				if(!flag){stream.println("no public operator ()");return "";}
			
			}catch(NullPointerException npe){npe.printStackTrace();}
			catch(ClassCastException cce){cce.printStackTrace();}
			
			boolean flag=isTemplateClass(node);
			if(flag){stream.println("template class declarations selected, unable to add as functor. (2)");return "";}
	
			
			
			//check class, struct at cursor for operator()
			boolean operatorMatchFlag=false;
			for(IASTName i:operatorParenthesesNode){
				if(node.contains(i)){
					operatorMatchFlag=true;
					break;
				}
			}
						
			operatorMatchFlag = isVirtualOperatorDeclared(
					operatorParenthesesNode, node, operatorMatchFlag);
						
			if(!operatorMatchFlag){
				stream.println("no matching operator() found at current cursor location.");
				if(getWantedTypeParent(node).getParent() instanceof IASTTranslationUnit){
					stream.println("function selected.");//TODO trigger addfunctiontosuite
				}
				return "";
			}
			
			//TODO check also operator() doesnt have parameters, or at least default binded
			//TODO check for function not virtual and has a method body
			if(node instanceof IASTSimpleDeclaration){//simple class case
				/*class TFunctor{
					private:
					public:  
				 ***but cannot handle the methods 
				}*/
				IASTDeclSpecifier aa=(((IASTSimpleDeclaration)node).getDeclSpecifier());
				if(null!=aa && aa instanceof IASTCompositeTypeSpecifier){
					IASTName i=((IASTCompositeTypeSpecifier)aa).getName();
					return i.toString();
				}
			}	
		}
		
		//FIXME wouldnt be detected as preprocess statement NodeAtCursorFinder returns null
		/*if(node instanceof IASTPreprocessorStatement){
			stream.println("preprocessor statement selected, unable to add as functor.");
			return "";
		}*/

		IASTNode parentNode=getWantedTypeParent(node);
		if(parentNode instanceof IASTFunctionDefinition || 
				parentNode instanceof IASTSimpleDeclaration){
				//handle the simple class case, cursor at methods
			//if(!(parentNode.getParent() instanceof ICPPASTTranslationUnit))	
			return ((ICPPASTCompositeTypeSpecifier)(parentNode.getParent())).getName().toString();
		}
		stream.println("Unable to add as functor for cursor position.");
		return ""; 
	}

	//handle case of virtual operator not declared
	private boolean isVirtualOperatorDeclared(
			ArrayList<IASTName> operatorParenthesesNode, IASTNode node,
			boolean operatorMatchFlag) {
		if(node instanceof IASTSimpleDeclaration || node instanceof IASTFunctionDefinition){
			if(node.getParent() instanceof ICPPASTCompositeTypeSpecifier){
				IASTNode tmp=node.getParent();
				for(IASTName i:operatorParenthesesNode){
					if(tmp.contains(i)){
						operatorMatchFlag=true;
						break;
					}
				}
			}
		}
		return operatorMatchFlag;
	}

	/*if(node instanceof ICPPASTTemplateDeclaration){
	//template <class TClass> 
	//shouldnt happen as requires the template to be initialised
	stream.println("template class declarations selected, unable to add as functor.");
	//IASTName i=((IASTCompositeTypeSpecifier)(((IASTSimpleDeclaration)((ICPPASTTemplateDeclaration)node).getDeclaration()).getDeclSpecifier())).getName();
	//return i.toString();
	return "";
}*/
	private boolean isTemplateClass(IASTNode checkforTemplate) {
		while(!(checkforTemplate instanceof ICPPASTTranslationUnit)){
			if(checkforTemplate instanceof ICPPASTTemplateDeclaration){
				return true;
			}
			checkforTemplate=checkforTemplate.getParent();
		}
		return false;
	}

	private boolean checkClassForPublicOperatorParentesis(IASTNode node) {
		IASTNode tmp1=node;
		while(!(tmp1.getParent() instanceof ICPPASTCompositeTypeSpecifier) && !(tmp1.getParent() instanceof ICPPASTTranslationUnit)){
			tmp1=tmp1.getParent();
		}
		if(tmp1.getParent() instanceof ICPPASTCompositeTypeSpecifier)tmp1=tmp1.getParent().getParent();
		
		boolean publicOperatorExist=false;
		if(tmp1 instanceof IASTSimpleDeclaration){
			ArrayList<IASTDeclaration> al=ASTHelper.getPublicMethods((IASTSimpleDeclaration)tmp1);
			for(IASTDeclaration i:al){
				if(ASTHelper.getMethodName(i).equals("operator ()")){
					publicOperatorExist=true;break;
				}
			}
		}
		return publicOperatorExist;
	}
	
	public IASTNode getWantedTypeParent(IASTNode node){
		IASTNode parentNode=node, prevNode=node;
		while(!(parentNode instanceof IASTFunctionDefinition ||
				parentNode instanceof IASTSimpleDeclaration||
				parentNode instanceof ICPPASTTranslationUnit)){
			try{
			prevNode=parentNode;	
			parentNode=parentNode.getParent();
			}catch(NullPointerException npe){return prevNode;}
		}return parentNode;
	}

	public boolean checkForConstructorWithParameters(IASTTranslationUnit astTu,IASTNode node){
		FunctionFinder ff=new FunctionFinder();
		astTu.accept(ff);
		for(Object i:ff.getClassStruct()){
			//stream.println(ff.getSimpleDeclarationNodeName((IASTSimpleDeclaration)(i))+"");
			if(((IASTNode)i).contains(node)){
				ArrayList<IASTDeclaration> constructors=ASTHelper.getConstructors((IASTSimpleDeclaration)i);				
				return ASTHelper.haveParameters(constructors);
			}
		}
		return false;
	}
}
