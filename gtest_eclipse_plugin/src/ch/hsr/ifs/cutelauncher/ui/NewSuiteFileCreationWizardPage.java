package ch.hsr.ifs.cutelauncher.ui;

import org.eclipse.cdt.core.CConventions;
import org.eclipse.cdt.core.CCorePlugin;
import org.eclipse.cdt.core.dom.ILinkage;
import org.eclipse.cdt.core.index.IIndex;
import org.eclipse.cdt.core.index.IIndexFile;
import org.eclipse.cdt.core.index.IndexLocationFactory;
import org.eclipse.cdt.core.model.CoreModelUtil;
import org.eclipse.cdt.core.model.ITranslationUnit;
import org.eclipse.cdt.internal.corext.codemanipulation.StubUtility;
import org.eclipse.cdt.internal.ui.dialogs.StatusInfo;
import org.eclipse.cdt.internal.ui.wizards.dialogfields.DialogField;
import org.eclipse.cdt.internal.ui.wizards.dialogfields.IDialogFieldListener;
import org.eclipse.cdt.internal.ui.wizards.dialogfields.LayoutUtil;
import org.eclipse.cdt.internal.ui.wizards.dialogfields.SelectionButtonDialogField;
import org.eclipse.cdt.internal.ui.wizards.dialogfields.StringDialogField;
import org.eclipse.cdt.internal.ui.wizards.filewizard.AbstractFileCreationWizardPage;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.text.templates.Template;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

public class NewSuiteFileCreationWizardPage extends
		AbstractFileCreationWizardPage {

	private ITranslationUnit fNewFileTU = null;
	private final StringDialogField fNewFileDialogField;
	private final SelectionButtonDialogField fSelection;
	
	@SuppressWarnings("restriction")
	public NewSuiteFileCreationWizardPage(){
		super("Custom Suite");
		
		setDescription("Create a new Suite");
		
		fNewFileDialogField = new StringDialogField();
		fNewFileDialogField.setDialogFieldListener(new IDialogFieldListener() {
			public void dialogFieldChanged(DialogField field) {
				handleFieldChanged(NEW_FILE_ID);
			}
		});
		fNewFileDialogField.setLabelText("Suite name:");
		
		fSelection=new SelectionButtonDialogField(SWT.CHECK);
		fSelection.setLabelText("Link to runner ");
		//generate list of runners
		//prompt selection
	}
	@SuppressWarnings("restriction")
	@Override
	public void createFile(IProgressMonitor monitor) throws CoreException {
        IPath filePath = getFileFullPath();
        if (filePath != null) {
            if (monitor == null)
	            monitor = new NullProgressMonitor();
            try {
	            fNewFileTU = null;
	            IPath folderPath = getSourceFolderFullPath();
	            if(folderPath!=null){
//	            	IProject project=getCurrentProject();
	            	IWorkspace workspace = ResourcesPlugin.getWorkspace();
	            	IWorkspaceRoot root = workspace.getRoot();

	            	String suitename=fNewFileDialogField.getText();
	            	
	            	IFile cppFile;
	            	if(folderPath.segmentCount()==1){
	            		IProject folder=root.getProject(folderPath.toPortableString());
	            		SuiteTemplateCopyUtil.copyFile(folder, monitor, "$suitename$.cpp", suitename+".cpp", suitename);		
	            		SuiteTemplateCopyUtil.copyFile(folder, monitor, "$suitename$.h", suitename+".h", suitename);
		            	cppFile=folder.getFile(suitename+".cpp");
		            	if(cppFile!=null)fNewFileTU =CoreModelUtil.findTranslationUnit(cppFile);
	            	}else{
	            		IFolder folder=root.getFolder(folderPath);	
	            		SuiteTemplateCopyUtil.copyFile(folder, monitor, "$suitename$.cpp", suitename+".cpp", suitename);		
	            		SuiteTemplateCopyUtil.copyFile(folder, monitor, "$suitename$.h", suitename+".h", suitename);
		            	cppFile=folder.getFile(suitename+".cpp");
		            	if(cppFile!=null)fNewFileTU =CoreModelUtil.findTranslationUnit(cppFile);
	            	}
	            	
	            	/*
	            	if(cppFile!=null){// && fSelection.isSelected()){
	            		//@see org.eclipse.cdt.core.tests/parser/org.eclipse.cdt.internal.index.tests.IndexBugsTests
	            		fNewFileTU =CoreModelUtil.findTranslationUnit(cppFile);
	            		ICProject fCProject=fNewFileTU.getCProject();
//	            		CCorePlugin.getIndexManager().reindex(fCProject);
	            		IIndex index = CCorePlugin.getIndexManager().getIndex(fCProject);
	            		String a=CCorePlugin.getIndexManager().getIndexerId(fCProject);
	            		System.out.println(a);         		
	            		try{
	            			IProgressMonitor subMonitor = new SubProgressMonitor(monitor,1);
	            			waitUntilFileIsIndexed(index, cppFile, 8000,subMonitor);
	            			index.acquireReadLock();
		            		try {
		            			IProgressMonitor sub = new SubProgressMonitor(monitor,1);
		    	            	
			            		IIndexBinding[] bindings= index.findBindings("theX".toCharArray(),IndexFilter.ALL,sub);	
			            		System.out.println("binding"+bindings.length);
							} catch (Exception e) {
								e.printStackTrace();
							}finally{
								index.releaseReadLock();
							}
	            		}catch(InterruptedException ie){
	            			ie.printStackTrace();
	            		}catch(Exception e){
	            			e.printStackTrace();
	            		}
	            		
	            	}*/
	            	/*
	            	IFile cppFile=folder.getFile(suitename+".cpp");
	            	if(cppFile!=null){// && fSelection.isSelected()){
	            		fNewFileTU =CoreModelUtil.findTranslationUnit(cppFile);
	            		
	            		IIndex index = CCorePlugin.getIndexManager().getIndex(fNewFileTU.getCProject());
	            		
	            		IProgressMonitor sub = new SubProgressMonitor(monitor,1);

	            		IIndexBinding[] bindings= index.findBindings("runner".toCharArray(),IndexFilter.ALL,sub);
	            		sub.done();
//	            		IName name=
//	            		IIndexBinding binding=index.findBinding(name);
//	            		
	            		
	            		NewSuiteFileGenerator nsfg=new NewSuiteFileGenerator(cppFile);
		            	nsfg.parse();	
	            	}
	            	core indexer 

find binding 
get binding 
after location translation unit 

	            	*
	            	*/
	            }
	        } finally {
	            monitor.done();
	        }
        }
	}
	
	public static void waitUntilFileIsIndexed(IIndex index, IFile file, int maxmillis,IProgressMonitor p) throws Exception {
		long endTime= System.currentTimeMillis() + maxmillis;
		int timeLeft= maxmillis;
		while (timeLeft >= 0) {
//			Assert.assertTrue(CCorePlugin.getIndexManager().joinIndexer(timeLeft, new NullProgressMonitor()));
			System.out.println("joinIndexer"+CCorePlugin.getIndexManager().joinIndexer(timeLeft, p));
			index.acquireReadLock();
			try {
				IIndexFile pfile= index.getFile(ILinkage.CPP_LINKAGE_ID, IndexLocationFactory.getWorkspaceIFL(file));
				if (pfile != null && pfile.getTimestamp() >= file.getLocalTimeStamp()) {
					return;
				}
			}
			finally {
				index.releaseReadLock();
			}
			
			Thread.sleep(50);
			timeLeft= (int) (endTime-System.currentTimeMillis());
		}
//		Assert.fail("Indexing " + file.getFullPath() + " did not complete in time!");
		System.out.println("Indexing " + file.getFullPath() + " did not complete in time!");
	}
	
	
	@SuppressWarnings("restriction")
	@Override
	protected void createFileControls(Composite parent, int nColumns) {
		fNewFileDialogField.doFillIntoGrid(parent, nColumns);
		Text textControl = fNewFileDialogField.getTextControl(null);
		LayoutUtil.setWidthHint(textControl, getMaxFieldWidth());
		textControl.addFocusListener(new StatusFocusListener(NEW_FILE_ID));
		/*
		Composite p=new Composite(parent,SWT.NO_FOCUS);
		GridData gd= new GridData(GridData.BEGINNING);
		gd.horizontalSpan= 1;
		p.setLayoutData(gd);*/
		createSeparator(parent,nColumns);
		fSelection.doFillIntoGrid(parent, nColumns);
		
	}

	@SuppressWarnings("restriction")
	@Override
	protected IStatus fileNameChanged() {
		StatusInfo status = new StatusInfo();
		
		IPath filePath = getFileFullPath();
		if (filePath == null) {
			status.setError("Enter Suite Name"); 
			return status;
		}
		
		IPath sourceFolderPath = getSourceFolderFullPath();
		if (sourceFolderPath == null || !sourceFolderPath.isPrefixOf(filePath)) {
			status.setError("File must be inside source folder.");
			return status;
		}
		
		// check if file already exists
		IResource file = getWorkspaceRoot().findMember(filePath);
		if (file != null && file.exists()) {
	    	if (file.getType() == IResource.FILE) {
	    		status.setError("File already exists.");
	    	} else if (file.getType() == IResource.FOLDER) {
	    		status.setError("A folder with the same name already exists.");
	    	} else {
	    		status.setError("A resource with the same name already exists.");
	    	}
			return status;
		}
		
		// check if folder exists
		IPath folderPath = filePath.removeLastSegments(1).makeRelative();
		IResource folder = getWorkspaceRoot().findMember(folderPath);
		if (folder == null || !folder.exists() || (folder.getType() != IResource.PROJECT && folder.getType() != IResource.FOLDER)) {
		    status.setError("Folder " + folderPath + " does not exist." );
			return status;
		}

		IStatus convStatus = CConventions.validateSourceFileName(getCurrentProject(), filePath.lastSegment());
		if (convStatus.getSeverity() == IStatus.ERROR) {
			status.setError("File name is not valid " + convStatus.getMessage() + ".");
			return status;
		} /*else if (convStatus.getSeverity() == IStatus.WARNING) {
			status.setWarning(NewFileWizardMessages.getFormattedString("NewSourceFileCreationWizardPage.warning.FileNameDiscouraged", convStatus.getMessage())); //$NON-NLS-1$
		}*/
		if(!fNewFileDialogField.getText().matches("\\w+")){
			status.setError("Invalid identifier. Only letters, digits and underscore are accepted."); //$NON-NLS-1$
			return status;
		}
		return status;
	}

	@Override
	public ITranslationUnit getCreatedFileTU() {
		return fNewFileTU; //used to create an editor window to show to the user
	}

	@SuppressWarnings("restriction")
	@Override
	public IPath getFileFullPath() {
		String str = fNewFileDialogField.getText();
        IPath path = null;
	    if (str.length() > 0) {
	        path = new Path(str);
	        if (!path.isAbsolute()) {
	            IPath folderPath = getSourceFolderFullPath();
	        	if (folderPath != null)
	        	    path = folderPath.append(path);
	        }
	    }
	    return path;
	}

	@SuppressWarnings("restriction")
	@Override
	protected void setFocus() {
		fNewFileDialogField.setFocus();
	}
	@SuppressWarnings("restriction")
	@Override
	protected Template[] getApplicableTemplates() {
		return StubUtility.getFileTemplatesForContentTypes(
				new String[] { CCorePlugin.CONTENT_TYPE_CXXHEADER, CCorePlugin.CONTENT_TYPE_CHEADER, CCorePlugin.CONTENT_TYPE_CXXSOURCE }, null);
	}
	public String getLastUsedTemplateName() {
		// TODO Auto-generated method stub
		return null;
	}

	public void saveLastUsedTemplateName(String name) {
		// TODO Auto-generated method stub
		
	}

	public String getDefaultTemplateName() {
		// TODO Auto-generated method stub
		return null;
	}

}
