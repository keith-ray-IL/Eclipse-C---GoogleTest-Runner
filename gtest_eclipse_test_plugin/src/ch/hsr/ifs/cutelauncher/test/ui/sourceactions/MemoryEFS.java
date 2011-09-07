package ch.hsr.ifs.cutelauncher.test.ui.sourceactions;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.cdt.core.CCProjectNature;
import org.eclipse.cdt.core.CCorePlugin;
import org.eclipse.cdt.core.CProjectNature;
import org.eclipse.cdt.core.dom.IPDOMManager;
import org.eclipse.cdt.core.model.ICProject;
import org.eclipse.cdt.core.testplugin.CProjectHelper;
import org.eclipse.cdt.internal.core.pdom.indexer.IndexerPreferences;
import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.filesystem.IFileSystem;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

public class MemoryEFS extends TestCase {
	
	public MemoryEFS(String name) {
		super(name);
 	}
	
	public void testCheckforMemoryOnlyEFS(){
		try {
			IFileSystem fileSystem = EFS.getFileSystem("memory");
			fileSystem.toString();
			//System.out.println(fileSystem.toString());
		} catch (CoreException e) {
			fail("no memory fs found:"+e);
		}
		assertTrue("found",true);
		
	}
	
	public void testCreateCProject(){
		//based on CProjectHelper.createCProject
		final IWorkspace ws = ResourcesPlugin.getWorkspace();
//		final ICProject newProject[] = new ICProject[1];
		final String projectName="memoryPrj";
		
		try {
		final java.net.URI loc=	new java.net.URI("memory:/");
		
		ws.run(new IWorkspaceRunnable() {

			public void run(IProgressMonitor monitor) throws CoreException {
				
			
			IWorkspaceRoot root = ws.getRoot();
			IProject project = root.getProject(projectName);
	
			//based on eclipse/org.eclipse.ui.ide/org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard.createNewProject()
			final IProjectDescription description = ws.newProjectDescription(projectName);
			description.setLocationURI(loc);
			
			String indexerID=IPDOMManager.ID_NO_INDEXER;
			if (indexerID != null) {
				IndexerPreferences.set(project, IndexerPreferences.KEY_INDEX_ALL_FILES, "true");
				IndexerPreferences.set(project, IndexerPreferences.KEY_INDEXER_ID, indexerID);
			}
					
			project.create(description,null);
			
			if (!project.isOpen()) {
				project.open(null);
			}
			
			if (!project.hasNature(CProjectNature.C_NATURE_ID)) {
//				String projectId = CTestPlugin.PLUGIN_ID + ".TestProject";
				CProjectHelper.addNatureToProject(project, CProjectNature.C_NATURE_ID, null);
//				CCorePlugin.getDefault().mapCProjectOwner(project, projectId, false);
			}
			CProjectHelper.addDefaultBinaryParser(project);
			
			ICProject cproject = CCorePlugin.getDefault().getCoreModel().create(project);
			if (!cproject.getProject().hasNature(CCProjectNature.CC_NATURE_ID)) {
				CProjectHelper.addNatureToProject(cproject.getProject(), CCProjectNature.CC_NATURE_ID, null);
			}
		
		}}, null);	
			
		
		IWorkspaceRoot root = ws.getRoot();
		IProject project = root.getProject(projectName);
		if (!project.exists()) {
			fail("project not exist.");
		}
		
//		final IPath containerPath = project.getFullPath();
//		IPath newFilePath = containerPath.append("B.cpp");
//		final IFile newFileHandle = createFileHandle(newFilePath);
//		final InputStream initialContents =null;
		
		IFile file = project.getProject().getFile("B.cpp");
		InputStream stream = new ByteArrayInputStream("Hello World".getBytes() );
		if( file.exists() )
		    file.setContents( stream, false, false, null );
		else
			file.create( stream, false, null);
		
		System.out.println(file.getFullPath());
		System.out.println(file.exists());
		
		//validation stage
		IFileSystem fileSystem = EFS.getFileSystem("memory");
		
		//FIXME: no idea why the full URI doesnt work???
//		IFileStore store=fileSystem.getStore(new java.net.URI("/"+projectName+"/"+"B.cpp"));
		
		IFileStore store=fileSystem.getStore(new java.net.URI("B.cpp"));
		InputStream is=store.openInputStream(0, null);
		BufferedReader br=new BufferedReader(new InputStreamReader(is));
		assertEquals(br.readLine(),"Hello World");
		
		} catch (CoreException e) {
			fail(""+e);
		}catch(URISyntaxException e){
			fail(""+e);
		}catch(IOException e){
			fail(""+e);
		}
		
	}

	// copy&paste org.eclipse.ui.dialogs.WizardNewFileCreationPage
//	IFile createFileHandle(IPath filePath) {
//		return IDEWorkbenchPlugin.getPluginWorkspace().getRoot().getFile(
//				filePath);
//	}
	
	
	public static Test suite(){
		TestSuite ts=new TestSuite("memoryOnly");
		ts.addTest(new MemoryEFS("testCheckforMemoryOnlyEFS"));
		ts.addTest(new MemoryEFS("testCreateCProject"));
		return ts;
	}
}
