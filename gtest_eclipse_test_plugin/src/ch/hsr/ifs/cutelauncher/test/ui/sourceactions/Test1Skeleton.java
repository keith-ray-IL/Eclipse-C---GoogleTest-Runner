package ch.hsr.ifs.cutelauncher.test.ui.sourceactions;

import org.eclipse.cdt.internal.ui.editor.CEditor;
import org.eclipse.cdt.ui.tests.text.EditorTestHelper;
import org.eclipse.cdt.core.tests.BaseTestFramework;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.RewriteSessionEditProcessor;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.text.TextUtilities;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.texteditor.IDocumentProvider;

import ch.hsr.ifs.cutelauncher.ui.sourceactions.AbstractFunctionAction;
public abstract class Test1Skeleton BaseTestFramework {
	
	public Test1Skeleton() {
		super("");
 	}
	public Test1Skeleton(String name) {
		super(name);
 	}
	
	protected static final NullProgressMonitor NULL_PROGRESS_MONITOR = new NullProgressMonitor();

	protected static CEditor ceditor;
	@Override
	protected void tearDown() throws Exception {
		EditorTestHelper.closeEditor(ceditor);
		/*if (fCProject != null)
			CProjectHelper.delete(fCProject);
		if (fNonCProject != null) {
			ResourceHelper.delete(fNonCProject);
		}*/
		super.tearDown();
	}
	
	// org.eclipse.cdt.ui.tests/ui/org.eclipse.cdt.ui.tests.text/BasicCeditor
	//@see org.eclipse.cdt.ui.tests.text.BasicCeditorTest#setUpEditor
	public final void generateTest(String testname,String testSrcCode, int cursorpos, String expectedOutput,AbstractFunctionAction functionAction){
		try{
		//**********
		//Original CDT based hdd test, (require this class to extend BaseTestFramework)	
		IFile inputFile=importFile("A.cpp",testSrcCode);

		//**********
		//Memory only EFS based
//		IFile inputFile=MemoryBaseTestFramework.importFile("A.cpp",testSrcCode);

		//**********
		//Reflection based to see which method are called 
//		IFile inputFile222=importFile("A.cpp",testSrcCode);
//		IFile inputFile = (IFile)MyDynamicProxyClass.newInstance(inputFile222, new Class[]
//		{ IFile.class });
//		
		//**********
		//Exception based Test to find candidate for overwriting the methods called 
//		IFile file = project.getProject().getFile("A.cpp");
//		InputStream stream = new ByteArrayInputStream( "".getBytes() );
//		file.create( stream, false, monitor );
//		IFile inputFile=new MemoryFileStub(file.getFullPath(),(Workspace)file.getWorkspace());
		//**********
		
		IEditorPart editor= EditorTestHelper.openInEditor(inputFile, true);
		assertNotNull(editor);
		assertTrue(editor instanceof CEditor);
		ceditor= (CEditor) editor; //dup err
		IEditorInput editorInput = ceditor.getEditorInput();
		/*StyledText fTextWidget= ceditor.getViewer().getTextWidget();
		assertNotNull(fTextWidget);
		Accessor fAccessor= new Accessor(fTextWidget, StyledText.class);*/
		
		Object ele=(editor).getEditorInput();
		IDocumentProvider idp=ceditor.getDocumentProvider();
		IDocument fDocument= idp.getDocument(ele);
		//IDocument fDocument= ceditor.getDocumentProvider().getDocument(((IEditorPart)ceditor).getEditorInput());
		assertNotNull(fDocument);

		String newLine = TextUtilities.getDefaultLineDelimiter(fDocument);
		functionAction.setNewline(newLine);
		
		ceditor.getSelectionProvider().setSelection(new TextSelection(cursorpos, 0));
				
		// execute actions 
		MultiTextEdit mEdit = functionAction.createEdit(ceditor, editorInput, fDocument, "newTestFunction");
		
		assertNotNull(fDocument);
		assertNotNull(mEdit);
		RewriteSessionEditProcessor processor = new RewriteSessionEditProcessor(fDocument, mEdit, TextEdit.CREATE_UNDO);
		processor.performEdits();
		
		//retrieve the edited source
		String results=fDocument.get();
		//handling save dialog prompt 
		ceditor.doSave(new NullProgressMonitor());
		//compare it 
		assertEquals("result unexpected."+testname+"("+cursorpos+")",expectedOutput,results);
		//TODO discarding the changes as clean up, instead of writing to disk and then deleting it
		}catch(Exception e){e.printStackTrace();fail(testname+"\n"+e.getMessage());}
		
//		System.out.println(testname);
//		MyDynamicProxyClass.printUniqueCall();
//		System.out.println("####################");
//		Recorder.store(MyDynamicProxyClass.getUniqueCall());
		
	}
	
	public final void testDisplayDynamicProxyRecordedResult(){
		Recorder.printUniqueCall();
	}
}
