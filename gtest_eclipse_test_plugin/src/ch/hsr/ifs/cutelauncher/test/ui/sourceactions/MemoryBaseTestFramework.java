//direct copy&paste org.eclipse.cdt.core.tests.BaseTestFramework
//To use this class would require org.eclipse.ui.examples.filesystem.zip
//and patched_CdtMacroSupplier.zip Line211-18
package ch.hsr.ifs.cutelauncher.test.ui.sourceactions;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URISyntaxException;

import junit.framework.TestCase;

import org.eclipse.cdt.core.CCProjectNature;
import org.eclipse.cdt.core.CCorePlugin;
import org.eclipse.cdt.core.CProjectNature;
import org.eclipse.cdt.core.dom.IPDOMManager;
import org.eclipse.cdt.core.model.ICProject;
import org.eclipse.cdt.core.testplugin.CProjectHelper;
import org.eclipse.cdt.core.testplugin.CTestPlugin;
import org.eclipse.cdt.core.testplugin.FileManager;
import org.eclipse.cdt.internal.core.pdom.indexer.IndexerPreferences;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;

public class MemoryBaseTestFramework extends TestCase {
	static protected NullProgressMonitor monitor;
	static protected IWorkspace workspace;
	static protected IProject project;
	static protected ICProject cproject;
	static protected FileManager fileManager;
	static protected boolean indexDisabled = false;

	static {
		if (CCorePlugin.getDefault() != null
				&& CCorePlugin.getDefault().getCoreModel() != null) {
			//(CCorePlugin.getDefault().getCoreModel().getIndexManager()).reset(
			// );
			monitor = new NullProgressMonitor();

			workspace = ResourcesPlugin.getWorkspace();

			// final ICProject newProject[] = new ICProject[1];
			// final String projectName="memoryPrj";

			try {
				cproject = myCreateCCProject(
						"MemRegressionTestProject", "bin", IPDOMManager.ID_NO_INDEXER); //$NON-NLS-1$ //$NON-NLS-2$

				project = cproject.getProject();

				/*
				 * project.setSessionProperty(SourceIndexer.activationKey,
				 * Boolean.FALSE ); //Set the id of the source indexer extension
				 * point as a session property to allow //index manager to
				 * instantiate it
				 * project.setSessionProperty(IndexManager.indexerIDKey,
				 * sourceIndexerID);
				 */

				//System.out.println("static initializer"+project.getLocationURI
				// ());
			} catch (CoreException e) {
				/* boo */
			}
			// catch(URISyntaxException e){
			// fail(""+e);
			// }

			if (project == null)
				fail("Unable to create project"); //$NON-NLS-1$

			// Create file manager
			fileManager = new FileManager();
		}
	}

	public static ICProject myCreateCCProject(final String projectName,
			final String binFolderName, final String indexerID)
			throws CoreException {
		final IWorkspace ws = ResourcesPlugin.getWorkspace();
		final ICProject newProject[] = new ICProject[1];
		ws.run(new IWorkspaceRunnable() {

			public void run(IProgressMonitor monitor) throws CoreException {
				ICProject cproject = myCreateCProject(projectName,
						binFolderName, indexerID);
				if (!cproject.getProject().hasNature(
						CCProjectNature.CC_NATURE_ID)) {
					CProjectHelper.addNatureToProject(cproject.getProject(),
							CCProjectNature.CC_NATURE_ID, null);
				}
				newProject[0] = cproject;
			}
		}, null);
		return newProject[0];
	}

	public static ICProject myCreateCProject(final String projectName,
			String binFolderName, final String indexerID) throws CoreException {
		final IWorkspace ws = ResourcesPlugin.getWorkspace();
		final ICProject newProject[] = new ICProject[1];
		ws.run(new IWorkspaceRunnable() {

			public void run(IProgressMonitor monitor) throws CoreException {
				IWorkspaceRoot root = ws.getRoot();
				IProject project = root.getProject(projectName);

				java.net.URI loc = null;

				try {
					loc = new java.net.URI("memory:/" + projectName);
				} catch (URISyntaxException e) {
					fail("" + e);
				}

				// based on
				// eclipse/org.eclipse.ui.ide/org.eclipse.ui.wizards.newresource
				// .BasicNewProjectResourceWizard.createNewProject()
				final IProjectDescription description = workspace
						.newProjectDescription(projectName);
				description.setLocationURI(loc);

				if (indexerID != null) {
					IndexerPreferences.set(project,
							IndexerPreferences.KEY_INDEX_ALL_FILES, "true");
					IndexerPreferences.set(project,
							IndexerPreferences.KEY_INDEXER_ID, indexerID);
				}
				if (!project.exists()) {
					project.create(description, null);
					// project.create(null);
				} else {
					project.refreshLocal(IResource.DEPTH_INFINITE, null);
				}
				if (!project.isOpen()) {
					project.open(null);
				}
				if (!project.hasNature(CProjectNature.C_NATURE_ID)) {
					String projectId = CTestPlugin.PLUGIN_ID + ".TestProject";
					CProjectHelper.addNatureToProject(project,
							CProjectNature.C_NATURE_ID, null);
					CCorePlugin.getDefault().mapCProjectOwner(project,
							projectId, false);
				}
				CProjectHelper.addDefaultBinaryParser(project);
				newProject[0] = CCorePlugin.getDefault().getCoreModel().create(
						project);
			}
		}, null);

		return newProject[0];
	}

	public MemoryBaseTestFramework() {
		super();
	}

	public MemoryBaseTestFramework(String name) {
		super(name);
	}

	public void cleanupProject() throws Exception {
		try {
			project.delete(true, false, monitor);
			project = null;
		} catch (Throwable e) {
			/* boo */
		}
	}

	@Override
	protected void tearDown() throws Exception {
		if (project == null || !project.exists())
			return;

		// IResource [] members = project.members();
		// for( int i = 0; i < members.length; i++ ){
		//            if( members[i].getName().equals( ".project" ) || members[i].getName().equals( ".cproject" ) ) //$NON-NLS-1$ //$NON-NLS-2$
		// continue;
		// if (members[i].getName().equals(".settings"))
		// continue;
		// try{
		// members[i].delete( false, monitor );
		// } catch( Throwable e ){
		// /*boo*/
		// }
		// }
	}

	public static IFile importFile(String fileName, String contents)
			throws Exception {
		// Obtain file handle
		// System.out.println(project.getLocationURI());
		IFile file = project.getProject().getFile(fileName);

		InputStream stream = new ByteArrayInputStream(contents.getBytes());

		if (file.exists())
			file.setContents(stream, false, false, monitor);
		else
			file.create(stream, false, monitor);

		fileManager.addFile(file);

		return file;
	}

}
