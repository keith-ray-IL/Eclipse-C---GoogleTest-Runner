package ch.hsr.ifs.cutelauncher.test.ui.sourceactions;

import java.lang.reflect.Method;
import java.util.ArrayList;

import junit.framework.TestSuite;

import org.eclipse.cdt.core.CCorePlugin;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.index.IIndex;
import org.eclipse.cdt.core.model.CoreModelUtil;
import org.eclipse.cdt.core.model.ITranslationUnit;
import org.eclipse.cdt.core.tests.BaseTestFramework;
import org.eclipse.cdt.internal.ui.editor.CEditor;
import org.eclipse.cdt.ui.tests.text.EditorTestHelper;
import org.eclipse.core.resources.IFile;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.part.FileEditorInput;

import ch.hsr.ifs.cutelauncher.ui.sourceactions.ASTHelper;
import ch.hsr.ifs.cutelauncher.ui.sourceactions.AddTestMembertoSuiteAction;
import ch.hsr.ifs.cutelauncher.ui.sourceactions.FunctionFinder;
import ch.hsr.ifs.cutelauncher.ui.sourceactions.IAddMemberContainer;
import ch.hsr.ifs.cutelauncher.ui.sourceactions.IAddMemberMethod;

public class TestAddMemberTree extends BaseTestFramework {
	
	static ReadTestCase rtc1;
	
	public TestAddMemberTree(String name) {
		super(name);
		if(rtc1==null){
			rtc1=new ReadTestCase("testDefs/sourceActions/addTestMember.tree.cpp");
		}
 	}
	
	private static CEditor ceditor;
	@Override
	protected void tearDown () throws Exception {
		EditorTestHelper.closeEditor(ceditor);
		/*if (fCProject != null)
			CProjectHelper.delete(fCProject);
		if (fNonCProject != null) {
			ResourceHelper.delete(fNonCProject);
		}*/
		super.tearDown();
	}
	
	public final void treeTest1(){
		testTree(rtc1.test.get(0),rtc1.expected.get(0),"func");
	}
	public final void treeTest2(){
		testTree(rtc1.test.get(1),rtc1.expected.get(1),"operator ()");
	}
	public final void testTree(String srcCodes,String expectedTree, String firstObjName){
//		final ReadTestCase rtc1=new ReadTestCase("testDefs/sourceActions/addTestMember.tree.cpp");
		try{
			IFile inputFile222=importFile("A.cpp",srcCodes);
			//**********
			IFile inputFile = (IFile)MyDynamicProxyClass.newInstance(inputFile222, new Class[]
			{ IFile.class });
			
			//**********
			IEditorPart editor= EditorTestHelper.openInEditor(inputFile, true);
			ceditor= (CEditor) editor;
			IEditorInput editorInput = ceditor.getEditorInput();
			
			IFile editorFile = ((FileEditorInput) editorInput).getFile();
			
			ITranslationUnit tu = CoreModelUtil.findTranslationUnit(editorFile);
			IIndex index = CCorePlugin.getIndexManager().getIndex(tu.getCProject());	
			IASTTranslationUnit astTu = tu.getAST(index, ITranslationUnit.AST_SKIP_INDEXED_HEADERS);
			
			FunctionFinder ff=new FunctionFinder();
			astTu.accept(ff);
			ArrayList<IASTSimpleDeclaration> withoutTemplate =ASTHelper.removeTemplateClasses(ff.getClassStruct());
			ArrayList<IASTSimpleDeclaration> variablesList=ff.getVariables();
			ArrayList<IASTSimpleDeclaration> classStructInstances=ASTHelper.getClassStructVariables(variablesList);
			
			
			AddTestMembertoSuiteAction atms=new AddTestMembertoSuiteAction();
			atms.setUnitTestingMode(null);
			
			/*final Field fields[] =AddTestMembertoSuiteAction.class.getDeclaredFields();
			for (int i = 0; i < fields.length; i++) {
			      System.out.println("Field: " + fields[i]);
			}*/
			
			final Method[] methods = AddTestMembertoSuiteAction.class.getDeclaredMethods();
		    for (int i = 0; i < methods.length; ++i) {
		      if (methods[i].getName().equals("showTreeUI")) {
		        final Object params[] = {withoutTemplate,classStructInstances};
		        methods[i].setAccessible(true);
		        Object ret = methods[i].invoke(atms, params);
		        //System.out.println(ret);
		        assertEquals("Selecting the first object in tree",ret.toString(),firstObjName);
		      }
		    }
		    
		    ArrayList<IAddMemberContainer> al=atms.getRootContainer();
		    StringBuilder builder=new StringBuilder();
		    builder.append("[");
		    for(IAddMemberContainer i:al){
		    	builder.append(i.toString()+"(");
		    	for(IAddMemberMethod j:i.getMethods()){
		    		builder.append(j.toString()+", ");
		    	}
		    	//remove last comma, case of empty methods is not possible as removed in myTree
		    	builder.deleteCharAt(builder.length()-1);
		    	builder.deleteCharAt(builder.length()-1); 
		    	builder.append("), ");
		    }
		    builder.deleteCharAt(builder.length()-1);
		    builder.deleteCharAt(builder.length()-1);
		    
		    builder.append("]");
		    builder.append(System.getProperty("line.separator"));
		    assertEquals("",expectedTree,builder.toString());
		    		    
		    //MyDynamicProxyClass.printUniqueCall();
		    
		}catch(Exception e){e.printStackTrace();fail("testTree\n"+e.getMessage());}
	}
	
	@SuppressWarnings("unchecked")
	public final void treeTest3(){
		try{
			IFile inputFile=importFile("A.cpp",rtc1.test.get(2));
			
			IEditorPart editor= EditorTestHelper.openInEditor(inputFile, true);
			ceditor= (CEditor) editor;
			IEditorInput editorInput = ceditor.getEditorInput();
			
			IFile editorFile = ((FileEditorInput) editorInput).getFile();
			
			ITranslationUnit tu = CoreModelUtil.findTranslationUnit(editorFile);
			IIndex index = CCorePlugin.getIndexManager().getIndex(tu.getCProject());	
			IASTTranslationUnit astTu = tu.getAST(index, ITranslationUnit.AST_SKIP_INDEXED_HEADERS);
			
			FunctionFinder ff=new FunctionFinder();
			astTu.accept(ff);
			ArrayList<IASTSimpleDeclaration> withoutTemplate =ASTHelper.removeTemplateClasses(ff.getClassStruct());
			ArrayList<IASTSimpleDeclaration> variablesList=ff.getVariables();
			ArrayList<IASTSimpleDeclaration> classStructInstances=ASTHelper.getClassStructVariables(variablesList);
			
			
			AddTestMembertoSuiteAction atms=new AddTestMembertoSuiteAction();
			atms.setUnitTestingMode(null);
			
			
			final Method[] methods = AddTestMembertoSuiteAction.class.getDeclaredMethods();
		    for (int i = 0; i < methods.length; ++i) {
		      if (methods[i].getName().equals("internalInitTree")) {
		        final Object params[] = {withoutTemplate,classStructInstances};
		        methods[i].setAccessible(true);
		        Object ret = methods[i].invoke(atms, params);

		      }
		    }
			
			assertTrue(true);
		}catch(ClassCastException e){fail("Unhandled Cast");}
		catch(Exception e){e.printStackTrace();fail("testTree\n"+e.getMessage());}
		
		
	}
	
	public static TestSuite suite(){
		TestSuite addMemberTS=new TestSuite("addMembertoSuite Tests");
		addMemberTS.addTest(new TestAddMemberTree("treeTest1"));
		addMemberTS.addTest(new TestAddMemberTree("treeTest2"));
		//addMemberTS.addTest(new TestAddMemberTree("treeTest3"));
		return addMemberTS;
	}
}

/*public final void testTree(){
final ReadTestCase rtc1=new ReadTestCase("testDefs/sourceActions/addTestMember.tree.cpp");
try{
	IFile fileToBeOpened = null;
	IEditorInput editorInput = new myEditorInput();
	IWorkbenchWindow window=PlatformUI.getWorkbench().getActiveWorkbenchWindow();
	IWorkbenchPage page = window.getActivePage();
	page.openEditor(editorInput, "org.eclipse.ui.DefaultTextEdtior"); 
	
	
	
}catch(Exception e){e.printStackTrace();fail("testTree\n"+e.getMessage());}
}
class myEditorInput implements IStorageEditorInput{
public IStorage getStorage() throws CoreException{
	return null;
}

}*/