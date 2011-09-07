package ch.hsr.ifs.cutelauncher.test;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.debug.core.ILaunchConfiguration;

import ch.hsr.ifs.cutelauncher.test.ui.sourceactions.MemoryFileStub;
import ch.hsr.ifs.cutelauncher.ui.CustomisedLaunchConfigTab;

public class SourceLookupPathTest extends TestCase {

	public SourceLookupPathTest(String m){
		super(m);
	}
	LaunchConfigurationStub lcs;
	ch.hsr.ifs.cutelauncher.CuteLauncherDelegate cld;
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		cld=new ch.hsr.ifs.cutelauncher.CuteLauncherDelegate();
	}

	public final void testSourcelookupCustomPath() {
		lcs=new LaunchConfigurationStub(true,"/someprj/src");
		IPath exePath=new org.eclipse.core.runtime.Path("c:/src/bin");
		IPath result=cld.sourcelookupPath(lcs, exePath);
		try{
			String rootpath=org.eclipse.core.runtime.Platform.getLocation().toOSString();
			String customSrcPath=lcs.getAttribute(CustomisedLaunchConfigTab.CUSTOM_SRC_PATH,"");
			String fileSeparator=System.getProperty("file.separator");
			IPath expected= new org.eclipse.core.runtime.Path(rootpath+customSrcPath+fileSeparator);
		assertEquals(expected, result);
		}catch(Exception e){fail(e.toString());}
	}
	
	public final void testSourcelookupDefaultPath(){
		lcs=new LaunchConfigurationStub(false,"d:/src");
		try{
			assertEquals(false, lcs.getAttribute("",false));
		}catch(CoreException e){}
				
		IPath exePath=new org.eclipse.core.runtime.Path("D:/runtime-EclipseApplication/sourcePathTestingPrj/src/bin");
		IPath result=cld.sourcelookupPath(lcs, exePath);
		
		IPath expected=new org.eclipse.core.runtime.Path("D:/runtime-EclipseApplication/sourcePathTestingPrj/src");
		assertEquals(expected, result);
	}
	public static Test suite(){
		TestSuite ts=new TestSuite("ch.hsr.ifs.cutelauncher.CuteLauncherDelegate");
		ts.addTest(new SourceLookupPathTest("testSourcelookupCustomPath"));
		ts.addTest(new SourceLookupPathTest("testSourcelookupDefaultPath"));
		return ts;
	}
}
//extend LaunchConfiguration as its constructor is protected 
class LaunchConfigurationStub extends org.eclipse.debug.internal.core.LaunchConfiguration implements ILaunchConfiguration{ 
	final boolean useCustomSrcPathProperty;
	final String customSrcPathProperty;
	public LaunchConfigurationStub(boolean value1, String value2) {
		super(new MemoryFileStub( new org.eclipse.core.runtime.Path(""), null));
		useCustomSrcPathProperty=value1;customSrcPathProperty=value2;
	}
	@Override
	public boolean getAttribute(String attributeName, boolean defaultValue) throws CoreException {
		return useCustomSrcPathProperty;
	}
	@Override
	public String getAttribute(String attributeName, String defaultValue) throws CoreException {
		return customSrcPathProperty;
	}
	
}