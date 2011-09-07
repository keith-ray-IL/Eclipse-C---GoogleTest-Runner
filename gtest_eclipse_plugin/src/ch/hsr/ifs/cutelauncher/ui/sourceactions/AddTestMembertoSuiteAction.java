package ch.hsr.ifs.cutelauncher.ui.sourceactions;

import java.util.ArrayList;

import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTFieldReference;
import org.eclipse.cdt.core.dom.ast.IASTFunctionCallExpression;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFieldReference;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTNamedTypeSpecifier;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeNodeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.console.MessageConsoleStream;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.part.FileEditorInput;

import ch.hsr.ifs.cutelauncher.CuteLauncherPlugin;
import ch.hsr.ifs.cutelauncher.EclipseConsole;

public class AddTestMembertoSuiteAction extends AbstractFunctionAction {

	@Override
	public MultiTextEdit createEdit(TextEditor ceditor,
			IEditorInput editorInput, IDocument doc, String funcName)
			throws CoreException {
		
		problemMarkerErrorLineNumber=0;
		ISelection sel = ceditor.getSelectionProvider().getSelection();
		if (sel != null && sel instanceof TextSelection) {
			TextSelection selection = (TextSelection) sel;
			if (editorInput instanceof FileEditorInput) {
				IFile editorFile = ((FileEditorInput) editorInput).getFile();
				IASTTranslationUnit astTu = getASTTranslationUnit(editorFile);
				
				NodeAtCursorFinder n= new NodeAtCursorFinder(selection.getOffset());
				astTu.accept(n);
		
				FunctionFinder ff=new FunctionFinder();
				astTu.accept(ff);
				ArrayList<IASTSimpleDeclaration> withoutTemplate =ASTHelper.removeTemplateClasses(ff.getClassStruct());
				ArrayList<IASTSimpleDeclaration> variablesList=ff.getVariables();
				ArrayList<IASTSimpleDeclaration> classStructInstances=ASTHelper.getClassStructVariables(variablesList);
				
				MultiTextEdit mEdit =Dialog(astTu, editorFile,doc, withoutTemplate, classStructInstances,editorInput);
				return mEdit;
				
			}
		}
		
		//return new MultiTextEdit();
		throw new CoreException(new Status(IStatus.ERROR, "ch.hsr.ifs.cutelauncher", 0,"human", null));
	}

	public void setUnitTestingMode(IAddMemberMethod unitTestingMockObject){
		unitTestingMode=true;
		this.unitTestingMockObject=unitTestingMockObject;
	}
	boolean unitTestingMode=false;
	IAddMemberMethod unitTestingMockObject=null;

	public MultiTextEdit Dialog(IASTTranslationUnit astTu,IFile editorFile,IDocument doc,
			ArrayList<IASTSimpleDeclaration> classStruct, ArrayList<IASTSimpleDeclaration> classStructInstances,IEditorInput editorInput){
		
		Object selectedObject;
		if(!unitTestingMode)
			selectedObject=showTreeUI(classStruct,classStructInstances);
		else 
			selectedObject=unitTestingMockObject;
		
		if(selectedObject == null)return new MultiTextEdit();
		
		IAddMemberMethod child=(IAddMemberMethod)selectedObject;
				
		SuitePushBackFinder suitPushBackFinder = new SuitePushBackFinder();
		astTu.accept(suitPushBackFinder);
		
		//TODO modify checkNameExist for detecting name with classes
		MultiTextEdit mEdit = new MultiTextEdit();
		
		StringBuilder builder = createPushBack(doc,suitPushBackFinder,child);
		
		if(!checkPushback(astTu, doc,suitPushBackFinder,child,builder)){
			mEdit.addChild(createPushBackEdit(editorFile, doc, astTu,
					suitPushBackFinder,builder));
		}else{
			createProblemMarker((FileEditorInput) editorInput, "Duplicate Pushback name "+builder.toString(), problemMarkerErrorLineNumber);
		}
		return mEdit;
	}
	
	//based on AddTestFunct_ION_OR.checkPushback
	private boolean checkPushback(IASTTranslationUnit astTu, IDocument doc,SuitePushBackFinder suitPushBackFinder,IAddMemberMethod child,StringBuilder builder){
		if(suitPushBackFinder.getSuiteDeclName() != null) {
			IASTName name = suitPushBackFinder.getSuiteDeclName();
			IBinding binding = name.resolveBinding();
			IASTName[] refs = astTu.getReferences(binding);

			String stripped=builder.substring(builder.indexOf("(")+1,builder.lastIndexOf(")"));
			for (IASTName name1 : refs) {
				try{
					IASTFieldReference fRef = (ICPPASTFieldReference) name1.getParent().getParent();
					if(fRef.getFieldName().toString().equals("push_back")) {
						IASTFunctionCallExpression callex=(IASTFunctionCallExpression)name1.getParent().getParent().getParent();
						IASTFunctionCallExpression innercallex=(IASTFunctionCallExpression)callex.getParameterExpression();
						
						IASTExpression thelist=innercallex.getParameterExpression();
						if(thelist!=null){
							int nodeOffset = innercallex.getFileLocation().getNodeOffset();
							int nodeLength = innercallex.getFileLocation().asFileLocation().getNodeLength();
							
							if(doc.get(nodeOffset, nodeLength).equals(stripped)){
								problemMarkerErrorLineNumber=name1.getFileLocation().getStartingLineNumber();
								return true;
							}
								
						}
					
					}
					
				}catch(ClassCastException e){}
				catch(org.eclipse.jface.text.BadLocationException be){}
			}
		}
		
		return false;
	}
	private StringBuilder createPushBack(IDocument doc,SuitePushBackFinder suitPushBackFinder,IAddMemberMethod child){
		StringBuilder builder=new StringBuilder();
		IAddMemberContainer parent=child.getParent();
		
		String insidePushback="";
		if(parent.isInstance()==IAddMemberContainer.InstanceType){
			insidePushback=("CUTE_MEMFUN("+parent.toString()+","+parent.getClassTypeName()+","+child.toString()+")");
		}
		if(parent.isInstance()==IAddMemberContainer.ClassType){
			insidePushback=("CUTE_SMEMFUN("+parent.toString()+","+child.toString()+")");
		}
		builder.append(PushBackString(suitPushBackFinder.getSuiteDeclName().toString(),insidePushback));
					
		return builder;
	}
	
	private ElementTreeSelectionDialog etsd;
	private myTree wcp;
	private Object showTreeUI(
			ArrayList<IASTSimpleDeclaration> classStruct,
			ArrayList<IASTSimpleDeclaration> classStructInstances) {
		
		internalInitTree(classStruct,classStructInstances);
		if(unitTestingMode){
			etsd.setBlockOnOpen(false);
			TreeViewer tv=((myETSD)etsd).getTreeViewer();
			Object[] containers=wcp.getElements(wcp.root);
			
			tv.setSelection(new StructuredSelection(containers[0]));
			
			ArrayList<IAddMemberMethod> al=((Container)containers[0]).getMethods();
			tv.setSelection(new StructuredSelection(al.get(0)));
		}else
			etsd.setBlockOnOpen(true);
		
		boolean allowToClose=false;
		while(allowToClose==false){
			int status=etsd.open();
			if(status==ElementTreeSelectionDialog.OK){
				Object selectedObject=etsd.getFirstResult();
				
				if(selectedObject instanceof IAddMemberContainer)continue;
				allowToClose=true;
				break;
			}
			if(status==ElementTreeSelectionDialog.CANCEL){
				allowToClose=true;
				break;
			}
		}
		return etsd.getFirstResult();
	}
	private void internalInitTree(ArrayList<IASTSimpleDeclaration> classStruct,ArrayList<IASTSimpleDeclaration> classStructInstances){
		LabelProvider lp=new LabelProvider(); 
		wcp=new myTree(classStruct, classStructInstances);
				
		etsd=new myETSD(new Shell(CuteLauncherPlugin.getDisplay()),lp,wcp);
		etsd.setTitle("Select Method to add to suite");
		etsd.setAllowMultiple(false);
		etsd.setInput(wcp.root);
		
		etsd.create();
		Button button=etsd.getOkButton();
		button.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
//				MessageConsoleStream stream=EclipseConsole.getConsole();
				//stream.println("selected");
			}
		});
		if(wcp.containers.size()==0)button.setEnabled(false);
	}
	public ArrayList<IAddMemberContainer> getRootContainer(){
		return wcp.containers;
	}
}

class myETSD extends ElementTreeSelectionDialog{
    
	public myETSD(Shell parent,
            ILabelProvider labelProvider, ITreeContentProvider contentProvider){
		super(parent,labelProvider,contentProvider);
	}
		
	// @see SelectionStatusDialog#updateButtonsEnableState
    @Override
	protected void updateButtonsEnableState(IStatus status) {
        Button okButton = getOkButton();
        Object selectedObject=getFirstResult();
        
        if (okButton != null && !okButton.isDisposed() && !(selectedObject instanceof IAddMemberContainer)) {
			okButton.setEnabled(!status.matches(IStatus.ERROR));
		}
        if(selectedObject instanceof IAddMemberContainer){
        	okButton.setEnabled(false);
        	getTreeViewer().setExpandedState(selectedObject, true);
        	getTreeViewer().refresh(selectedObject, false);
        }
    }
    @Override
	public TreeViewer getTreeViewer(){
    	return super.getTreeViewer();
    }
}

class myTree extends TreeNodeContentProvider{
	
	public ArrayList<IAddMemberContainer> containers=new ArrayList<IAddMemberContainer>();
	public final IAddMemberContainer root=new Container(null,true);
	
 	public myTree(	ArrayList<IASTSimpleDeclaration> classStruct, 
					ArrayList<IASTSimpleDeclaration> classStructInstances){
		MessageConsoleStream stream=EclipseConsole.getConsole();
		for(IASTSimpleDeclaration i:classStruct){
			//stream.println("class:"+ASTHelper.getClassStructName((i))+"");
			ArrayList<IASTDeclaration> publicMethods=ASTHelper.getPublicMethods(i);
			ArrayList<IASTDeclaration> nonStaticMethods=ASTHelper.getNonStaticMethods(publicMethods);
			ArrayList<IASTDeclaration> removedParameters=ASTHelper.getParameterlessMethods(nonStaticMethods);
			ArrayList<IASTDeclaration> onlyVoid=removedParameters;//ASTHelper.getVoidMethods(removedParameters);
			ArrayList<IASTDeclaration> removedUnion=ASTHelper.removeUnion(onlyVoid);
						
			IAddMemberContainer c=new Container(i,IAddMemberContainer.ClassType);
			for(IASTDeclaration j:removedUnion){
				IAddMemberMethod method=new Method(c,j);
				c.add(method);
			}
			
			if(removedUnion.size()!=0)
				containers.add(c);
		}
		
		for(IASTSimpleDeclaration i:classStructInstances){
			//stream.println("instances:"+ASTHelper.getVariableName((i))+"");
			if(i.getDeclSpecifier() instanceof ICPPASTNamedTypeSpecifier){
				ICPPASTNamedTypeSpecifier namedSpecifier=(ICPPASTNamedTypeSpecifier)i.getDeclSpecifier();
				String typename=namedSpecifier.getName().toString();
			
				//resolve to type
				IASTSimpleDeclaration targetType=null;
				//INCORRECT ASSUMATIONS as there are classes that are not being added above, 
				//but can be added via instance type
				for(IAddMemberContainer c:containers){
					if(ASTHelper.getClassStructName(c.getSimpleDeclaration()).equals(typename)){
						targetType=c.getSimpleDeclaration();
					}
				}
				if(targetType==null)continue;
							
				ArrayList<IASTDeclaration> publicMethods=ASTHelper.removeUnion(ASTHelper.getVoidMethods(ASTHelper.getParameterlessMethods(ASTHelper.getNonStaticMethods(ASTHelper.getPublicMethods(targetType)))));
				
				IAddMemberContainer c=new Container(i,IAddMemberContainer.InstanceType,ASTHelper.getClassStructName(targetType));
				for(IASTDeclaration j:publicMethods){
					IAddMemberMethod method=new Method(c,j);
					c.add(method);
				}
				if(publicMethods.size()!=0)containers.add(c);
			}
		}
	}
	
	@Override
	public Object[] getElements(Object inputElement){
		return getChildren(inputElement);
	}
	
	@Override
	public Object[] getChildren(Object parentElement){
		Object[] result=new Object[0];
		if(parentElement==root){
			return containers.toArray();
		}else if(parentElement instanceof IAddMemberContainer){
			IAddMemberContainer container=(IAddMemberContainer)parentElement;
			return container.getMethods().toArray();
		}
		return result;
	}
	@Override
	public boolean hasChildren(Object element){
		if(element instanceof IAddMemberContainer){
			if(((IAddMemberContainer)element).getMethods().size()>0)return true;
		}
		return false;
	}
	@Override
	public Object getParent(Object element){
		if(element==root)return root;//potential recursive loop
		if(element instanceof IAddMemberContainer){return root;}
		if(element instanceof Method){return ((IAddMemberMethod)element).getParent();}
		return "thisShouldntHappen";
	}
}

class Container implements IAddMemberContainer {
	private IASTSimpleDeclaration simpleDeclaration; 
	private final ArrayList<IAddMemberMethod> methods=new ArrayList<IAddMemberMethod>();
	private final boolean isInstance;
	public String classTypeName="";
	
	public Container(IASTSimpleDeclaration i,boolean isInstance){
		setSimpleDeclaration(i);
		this.isInstance=isInstance;
	}
	public Container(IASTSimpleDeclaration i,boolean isInstance, String classTypeName){
		this(i,isInstance);
		this.classTypeName=classTypeName;
	}
	
	public void add(Object element){getMethods().add((IAddMemberMethod)element);}
	@Override
	public String toString(){
		if(isInstance){
			return ASTHelper.getVariableName(getSimpleDeclaration());
		}else 
			return ASTHelper.getClassStructName(getSimpleDeclaration());
	}
	public void setSimpleDeclaration(IASTSimpleDeclaration simpleDeclaration) {
		this.simpleDeclaration = simpleDeclaration;
	}
	public IASTSimpleDeclaration getSimpleDeclaration() {
		return simpleDeclaration;
	}
	public void setMethods(ArrayList<IAddMemberMethod> methods) {
		//this.methods = methods;
	}
	public ArrayList<IAddMemberMethod> getMethods() {
		return methods;
	}
	public boolean isInstance(){
		return isInstance;
	}
	public String getClassTypeName(){
		return classTypeName;
	}
}
class Method implements IAddMemberMethod{
	public IASTDeclaration declaration;
	IAddMemberContainer container;
	
	public Method(IAddMemberContainer c, IASTDeclaration i){container=c;declaration=i;}
	public IAddMemberContainer getParent(){return container;}
	@Override
	public String toString(){return ASTHelper.getMethodName(declaration);}
}
// @see http://www.eclipse.org/articles/Article-TreeViewer/TreeViewerArticle.htm

// nested case of struct/class ?
