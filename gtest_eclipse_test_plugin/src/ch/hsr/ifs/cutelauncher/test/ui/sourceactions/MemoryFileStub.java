/*
 * The point of this class is emulate a physical file using memory. It is for speeding up unit testing
 * A generator to automatically log the method called would be good.
 * 
 */
package ch.hsr.ifs.cutelauncher.test.ui.sourceactions;

import java.io.InputStream;
import java.io.Reader;
import java.net.URI;

import org.eclipse.core.internal.resources.File;
import org.eclipse.core.internal.resources.Workspace;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFileState;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResourceProxy;
import org.eclipse.core.resources.IResourceProxyVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourceAttributes;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.content.IContentDescription;
import org.eclipse.core.runtime.jobs.ISchedulingRule;

public class MemoryFileStub extends File implements IFile {
		
	MemoryFileStubDefaultException mfsde=new MemoryFileStubDefaultException();
	
	public MemoryFileStub(IPath path, Workspace container) {
		super(path, container);
	}
	
	@Override
	public void appendContents(InputStream source, int updateFlags,
			IProgressMonitor monitor) throws CoreException {
		mfsde.kill();

	}

	@Override
	public void appendContents(InputStream source, boolean force,
			boolean keepHistory, IProgressMonitor monitor) throws CoreException {
		mfsde.kill();

	}

	@Override
	public void create(InputStream source, boolean force,
			IProgressMonitor monitor) throws CoreException {
		mfsde.kill();

	}

	@Override
	public void create(InputStream source, int updateFlags,
			IProgressMonitor monitor) throws CoreException {
		mfsde.kill();

	}

	@Override
	public void createLink(IPath localLocation, int updateFlags,
			IProgressMonitor monitor) throws CoreException {
		mfsde.kill();

	}

	@Override
	public void createLink(URI location, int updateFlags,
			IProgressMonitor monitor) throws CoreException {
		mfsde.kill();

	}

	@Override
	public void delete(boolean force, boolean keepHistory,
			IProgressMonitor monitor) throws CoreException {
		mfsde.kill();

	}

	@Override
	public String getCharset() throws CoreException {
		mfsde.kill();
		return null;
	}

	@Override
	public String getCharset(boolean checkImplicit) throws CoreException {
		mfsde.kill();
		return null;
	}

	@Override
	public String getCharsetFor(Reader reader) throws CoreException {
		mfsde.kill();
		return null;
	}

	@Override
	public IContentDescription getContentDescription() throws CoreException {
		return super.getContentDescription();
	}

	@Override
	public InputStream getContents() throws CoreException {
		mfsde.kill();
		return null;
	}

	@Override
	public InputStream getContents(boolean force) throws CoreException {
		mfsde.kill();
		return null;
	}

	@Override
	public int getEncoding() throws CoreException {
		mfsde.kill();
		return 0;
	}

	@Override
	public IPath getFullPath() {
		return super.getFullPath();
	}

	@Override
	public IFileState[] getHistory(IProgressMonitor monitor){
		return super.getHistory(monitor);
	}

	@Override
	public String getName() {
//		mfsde.kill();
		return "A.cpp";
	}

	@Override
	public boolean isReadOnly() {
		mfsde.kill();
		return false;
	}

	@Override
	public void move(IPath destination, boolean force, boolean keepHistory,
			IProgressMonitor monitor) throws CoreException {
		mfsde.kill();

	}

	@Override
	public void setCharset(String newCharset) throws CoreException {
		mfsde.kill();

	}

	@Override
	public void setCharset(String newCharset, IProgressMonitor monitor)
			throws CoreException {
		mfsde.kill();

	}

	@Override
	public void setContents(InputStream source, int updateFlags,
			IProgressMonitor monitor) throws CoreException {
		mfsde.kill();

	}

	@Override
	public void setContents(IFileState source, int updateFlags,
			IProgressMonitor monitor) throws CoreException {
		mfsde.kill();

	}

	@Override
	public void setContents(InputStream source, boolean force,
			boolean keepHistory, IProgressMonitor monitor) throws CoreException {
		mfsde.kill();

	}

	@Override
	public void setContents(IFileState source, boolean force,
			boolean keepHistory, IProgressMonitor monitor) throws CoreException {
		mfsde.kill();

	}

	@Override
	public void accept(IResourceVisitor visitor) throws CoreException {
		mfsde.kill();

	}

	@Override
	public void accept(IResourceProxyVisitor visitor, int memberFlags)
			throws CoreException {
		mfsde.kill();

	}

	@Override
	public void accept(IResourceVisitor visitor, int depth,
			boolean includePhantoms) throws CoreException {
		mfsde.kill();

	}

	@Override
	public void accept(IResourceVisitor visitor, int depth, int memberFlags)
			throws CoreException {
		mfsde.kill();

	}

	@Override
	public void clearHistory(IProgressMonitor monitor){
		super.clearHistory(monitor);
	}

	@Override
	public void copy(IPath destination, boolean force, IProgressMonitor monitor)
			throws CoreException {
		mfsde.kill();

	}

	@Override
	public void copy(IPath destination, int updateFlags,
			IProgressMonitor monitor) throws CoreException {
		mfsde.kill();

	}

	@Override
	public void copy(IProjectDescription description, boolean force,
			IProgressMonitor monitor) throws CoreException {
		mfsde.kill();

	}

	@Override
	public void copy(IProjectDescription description, int updateFlags,
			IProgressMonitor monitor) throws CoreException {
		mfsde.kill();

	}

	@Override
	public IMarker createMarker(String type) throws CoreException {
		mfsde.kill();
		return null;
	}

	@Override
	public IResourceProxy createProxy() {
		mfsde.kill();
		return null;
	}

	@Override
	public void delete(boolean force, IProgressMonitor monitor)
			throws CoreException {
		mfsde.kill();

	}

	@Override
	public void delete(int updateFlags, IProgressMonitor monitor)
			throws CoreException {
		mfsde.kill();

	}

	@Override
	public void deleteMarkers(String type, boolean includeSubtypes, int depth)
			throws CoreException {
		mfsde.kill();

	}

	@Override
	public boolean exists() {
		return true;
	}

	@Override
	public IMarker findMarker(long id){
		return findMarker(id);
	}

	@Override
	public IMarker[] findMarkers(String type, boolean includeSubtypes, int depth)
			throws CoreException {
		return null;
	}

	@Override
	public int findMaxProblemSeverity(String type, boolean includeSubtypes,
			int depth) throws CoreException {
		mfsde.kill();
		return 0;
	}

	@Override
	public String getFileExtension() {
		mfsde.kill();
		return null;
	}

	@Override
	public long getLocalTimeStamp() {
		mfsde.kill();
		return 0;
	}

	@Override
	public IPath getLocation() {
		return super.getLocation();
	}

	@Override
	public URI getLocationURI() {
		mfsde.kill();
		return null;
	}

	@Override
	public IMarker getMarker(long id) {
		mfsde.kill();
		return null;
	}

	@Override
	public long getModificationStamp() {
		mfsde.kill();
		return 0;
	}

	@Override
	public IContainer getParent() {
		return super.getParent();
	}

	@Override
	public String getPersistentProperty(QualifiedName key) throws CoreException {
		return super.getPersistentProperty(key);
	}

	@Override
	public IProject getProject() {
		return super.getProject();
	}

	@Override
	public IPath getProjectRelativePath() {
		mfsde.kill();
		return null;
	}

	@Override
	public IPath getRawLocation() {
		mfsde.kill();
		return null;
	}

	@Override
	public URI getRawLocationURI() {
		mfsde.kill();
		return null;
	}

	@Override
	public ResourceAttributes getResourceAttributes() {
		mfsde.kill();
		return null;
	}

	@Override
	public Object getSessionProperty(QualifiedName key) throws CoreException {
		mfsde.kill();
		return null;
	}

	@Override
	public int getType() {
		return super.getType();
	}

	@Override
	public IWorkspace getWorkspace() {
		mfsde.kill();
		return null;
	}

	@Override
	public boolean isAccessible() {
		mfsde.kill();
		return false;
	}

	@Override
	public boolean isDerived() {
		mfsde.kill();
		return false;
	}

	@Override
	public boolean isLinked() {
		mfsde.kill();
		return false;
	}

	@Override
	public boolean isLinked(int options) {
		mfsde.kill();
		return false;
	}

	@Override
	public boolean isLocal(int depth) {
		mfsde.kill();
		return false;
	}

	@Override
	public boolean isPhantom() {
		mfsde.kill();
		return false;
	}

	@Override
	public boolean isSynchronized(int depth) {
		mfsde.kill();
		return false;
	}

	@Override
	public boolean isTeamPrivateMember() {
		mfsde.kill();
		return false;
	}

	@Override
	public void move(IPath destination, boolean force, IProgressMonitor monitor)
			throws CoreException {
		mfsde.kill();

	}

	@Override
	public void move(IPath destination, int updateFlags,
			IProgressMonitor monitor) throws CoreException {
		mfsde.kill();

	}

	@Override
	public void move(IProjectDescription description, int updateFlags,
			IProgressMonitor monitor) throws CoreException {
		mfsde.kill();

	}

	@Override
	public void move(IProjectDescription description, boolean force,
			boolean keepHistory, IProgressMonitor monitor) throws CoreException {
		mfsde.kill();

	}

	@Override
	public void refreshLocal(int depth, IProgressMonitor monitor)
			throws CoreException {
		mfsde.kill();

	}

	@Override
	public void revertModificationStamp(long value) throws CoreException {
		mfsde.kill();

	}

	@Override
	public void setDerived(boolean isDerived) throws CoreException {
		mfsde.kill();

	}

	@Override
	public void setLocal(boolean flag, int depth, IProgressMonitor monitor)
			throws CoreException {
		mfsde.kill();

	}

	@Override
	public long setLocalTimeStamp(long value) throws CoreException {
		mfsde.kill();
		return 0;
	}

	@Override
	public void setPersistentProperty(QualifiedName key, String value)
			throws CoreException {
		mfsde.kill();

	}

	@Override
	public void setReadOnly(boolean readOnly) {
		mfsde.kill();

	}

	@Override
	public void setResourceAttributes(ResourceAttributes attributes)
			throws CoreException {
		mfsde.kill();

	}

	@Override
	public void setSessionProperty(QualifiedName key, Object value)
			throws CoreException {
		mfsde.kill();

	}

	@Override
	public void setTeamPrivateMember(boolean isTeamPrivate)
			throws CoreException {
		mfsde.kill();

	}

	@Override
	public void touch(IProgressMonitor monitor) throws CoreException {
		mfsde.kill();

	}

	@Override
	public Object getAdapter(Class adapter) {
		return super.getAdapter(adapter);
	}

	@Override
	public boolean contains(ISchedulingRule rule) {
		mfsde.kill();
		return false;
	}

	@Override
	public boolean isConflicting(ISchedulingRule rule) {
		mfsde.kill();
		return false;
	}

}
