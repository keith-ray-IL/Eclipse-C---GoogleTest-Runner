package ch.hsr.ifs.cutelauncher.ui.sourceactions;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.internal.EditorSite;
import org.eclipse.ui.internal.PopupMenuExtender;
import org.eclipse.ui.part.FileEditorInput;

import ch.hsr.ifs.cutelauncher.CuteLauncherPlugin;

public class Trigger extends AbstractFunctionActionDelegate {

	public Trigger(){
		super("Trigger",null);
	}
	
	@Override
	public void run(IAction action) {
		System.out.println("Trigger");
		getProjectNature();
//		test2();
	}
	
	private IProject project; 
	
	public void getProjectNature(){
		try {
			IWorkbenchWindow w=CuteLauncherPlugin.getActiveWorkbenchWindow();
			IWorkbenchPage p=CuteLauncherPlugin.getActivePage();
			IEditorReference[] er=p.getEditorReferences();
			
			IFile f=((FileEditorInput)(p.getActiveEditor().getEditorInput())).getFile();
			project=f.getProject();	
			String[] natureIds = project.getDescription().getNatureIds();
			for(String s:natureIds){
				System.out.println(s);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	public void test3(){
		//delete all cute marker 
		final IWorkspace ws = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot resource = ws.getRoot();
		IMarker[] problems = null;
		int depth = IResource.DEPTH_INFINITE;
		try {
		   problems = resource.findMarkers(IMarker.PROBLEM, true, depth);
		   for(IMarker i:problems){
			   String cc=(String)i.getAttribute(IMarker.MESSAGE);
			   if(cc.startsWith("cute:"))
				   i.delete();
		   }
		} catch (CoreException e) {
		   // something went wrong
		}
	}
	public void test1(){
		CuteLauncherPlugin.getDefault().getLog()
		.log(new Status(IStatus.ERROR, CuteLauncherPlugin.PLUGIN_ID, IStatus.ERROR, "trigger Error", null));
		
		TextEditor ceditor = (TextEditor) editor;
		IEditorInput editorInput = ceditor.getEditorInput();
		IFile editorFile = ((FileEditorInput) editorInput).getFile();
		
	   try {
	      //IMarker marker = editorFile.createMarker("ch.hsr.ifs.cutelauncher.cuteProblem");
		  IMarker marker = editorFile.createMarker("org.eclipse.cdt.core.problem");
	      marker.setAttribute(IMarker.MESSAGE, "A sample marker message");
	      marker.setAttribute(IMarker.PRIORITY, IMarker.PRIORITY_HIGH);
	      marker.setAttribute(IMarker.LINE_NUMBER, 2);
	      marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
	   } catch (CoreException e) {
	      // You need to handle the cases where attribute value is rejected
	   }
	}
	public void test2(){
		TextEditor ceditor = (TextEditor) editor;
		IEditorInput editorInput = ceditor.getEditorInput();
		ISelection sel = ceditor.getSelectionProvider().getSelection();
		TextSelection selection = (TextSelection) sel;
		System.out.println("selected pos"+selection.getOffset()+" "+selection.getLength());
		
		IWorkbenchWindow workbench = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		
		IWorkbenchPart part = page.getActivePartReference().getPart(true);
		
		IWorkbenchPartSite site=part.getSite();
	
		EditorSite es=(EditorSite)site;
		
		es.activateActionBars(true);
		
		IActionBars bars= es.getActionBars();
		
		IMenuManager menuManager=bars.getMenuManager();
		//remember to called update() to commit the changes
		
		final IContributionItem[] items = menuManager.getItems();
		

		//AbstractTextEditor.fTextContextMenu
		//within CEditor
		
		// Create a fake PopupMenuExtender so we can get some data back.
		final MenuManager fakeMenuManager = new MenuManager();
		fakeMenuManager.add(new GroupMarker(
				org.eclipse.ui.IWorkbenchActionConstants.MB_ADDITIONS));
		final PopupMenuExtender extender = new PopupMenuExtender(null,
				fakeMenuManager, null, part);
		
		extender.menuAboutToShow(fakeMenuManager);

		fakeMenuManager.setVisible(true);
		System.out.println();
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
