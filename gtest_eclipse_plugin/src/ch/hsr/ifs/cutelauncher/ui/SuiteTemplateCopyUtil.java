package ch.hsr.ifs.cutelauncher.ui;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Enumeration;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;

import ch.hsr.ifs.cutelauncher.CuteLauncherPlugin;

public class SuiteTemplateCopyUtil {
	public static void copyFile(IProject folder, IProgressMonitor monitor,String templateFilename, String targetFilename,String suitename)throws CoreException{
		IFile targetFile = folder.getFile(targetFilename);
		copyFile(targetFile,monitor,templateFilename,suitename);
	}
	
	public static void copyFile(IFolder folder, IProgressMonitor monitor,String templateFilename, String targetFilename,String suitename)throws CoreException{
		IFile targetFile = folder.getFile(targetFilename);
		copyFile(targetFile,monitor,templateFilename,suitename);
	}

	@SuppressWarnings("unchecked")
	public static void copyFile (IFile targetFile, IProgressMonitor monitor,String templateFilename,String suitename)throws CoreException{
		Enumeration en = CuteLauncherPlugin.getDefault().getBundle().findEntries("templates/projecttemplates/suite", templateFilename, false);
		if(en.hasMoreElements()){
			URL url = (URL)en.nextElement();
			
			try {				
				ByteArrayInputStream str=implantActualsuitename(url,suitename);
				
				targetFile.create(str,IResource.FORCE , new SubProgressMonitor(monitor,1));
			} catch (IOException e) {
				throw new CoreException(new Status(IStatus.ERROR,CuteLauncherPlugin.PLUGIN_ID,42,e.getMessage(), e));
			}
		}else{
			throw new CoreException(new Status(IStatus.ERROR,CuteLauncherPlugin.PLUGIN_ID,42,"missing suite template files", null));
		}
	}
	
	//parse the template source file for $suitename$ and replace it with the user's entry
	public static ByteArrayInputStream implantActualsuitename(URL url, String suitename)throws IOException{
		BufferedReader br=new BufferedReader(new InputStreamReader(url.openStream()));
		StringBuilder buffer = new StringBuilder();
		String linesep=System.getProperty("line.separator");
		while(br.ready()){
			String a=br.readLine();
			buffer.append(a.replaceAll("[$]suitename[$]", suitename)+linesep);
		}
		br.close();
		return new ByteArrayInputStream(buffer.toString().getBytes());
	}
}
