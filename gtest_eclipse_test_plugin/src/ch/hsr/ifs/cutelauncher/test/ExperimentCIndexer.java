package ch.hsr.ifs.cutelauncher.test;

import junit.framework.Assert;
import junit.framework.TestSuite;

import org.eclipse.cdt.core.CCorePlugin;
import org.eclipse.cdt.core.dom.ILinkage;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPClassType;
import org.eclipse.cdt.core.index.IIndex;
import org.eclipse.cdt.core.index.IIndexFile;
import org.eclipse.cdt.core.index.IndexFilter;
import org.eclipse.cdt.core.index.IndexLocationFactory;
import org.eclipse.cdt.internal.ui.editor.CEditor;
import org.eclipse.cdt.ui.tests.text.EditorTestHelper;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.ui.IEditorPart;

import ch.hsr.ifs.cutelauncher.test.ui.sourceactions.ReadTestCase;

public class ExperimentCIndexer extends IndexerBaseTestFramework{
	static final IProgressMonitor NPM= new NullProgressMonitor();
	
	public ExperimentCIndexer(String s){
		super(s);
	}
	
	public void testTryCindexer(){
		final ReadTestCase rtc1=new ReadTestCase("testDefs/TryCindexer.cpp");
		
		try {
			CCorePlugin.getIndexManager().reindex(cproject);
			IIndex fIndex= CCorePlugin.getIndexManager().getIndex(cproject);
			
			
			IFile inputFile=importFile("A.cpp",rtc1.expected.get(0));
			IEditorPart editor= EditorTestHelper.openInEditor(inputFile, true);
			assertNotNull(editor);
			assertTrue(editor instanceof CEditor);
			
			waitUntilFileIsIndexed(fIndex,inputFile,8000);
			
			IIndex index= CCorePlugin.getIndexManager().getIndex(cproject);
			index.acquireReadLock();
			
			try {
				IBinding[] bs= index.findBindings("s1".toCharArray(), IndexFilter.ALL, NPM);
				assertEquals(1, bs.length); 
				assertTrue(bs[0] instanceof ICPPClassType);
				assertEquals(2, ((ICPPClassType)bs[0]).getDeclaredMethods().length);
			} finally {
				index.releaseReadLock();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
	public static TestSuite suite(){
		TestSuite ts=new TestSuite("ch.hsr.ifs.cutelauncher.test.Cindexer");
		ts.addTest(new ExperimentCIndexer("testTryCindexer"));
		return ts;
	}

	/** 
	 * @see org.eclipse.cdt.core.testplugin.util.TestSourceReader
	 * Waits until the given file is indexed. Fails if this does not happen within the
	 * given time. 
	 * @param file
	 * @param maxmillis
	 * @throws Exception
	 * @since 4.0
	 */
	public static void waitUntilFileIsIndexed(IIndex index, IFile file, int maxmillis) throws Exception {
		long endTime= System.currentTimeMillis() + maxmillis;
		int timeLeft= maxmillis;
		while (timeLeft >= 0) {
			Assert.assertTrue(CCorePlugin.getIndexManager().joinIndexer(timeLeft, new NullProgressMonitor()));
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
		Assert.fail("Indexing " + file.getFullPath() + " did not complete in time!");
	}
	
}
