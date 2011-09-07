package ch.hsr.ifs.cutelauncher;

import java.util.Map;
import java.util.TreeMap;

import org.eclipse.cdt.core.envvar.IEnvironmentVariable;
import org.eclipse.cdt.core.model.ICProject;
import org.eclipse.cdt.core.settings.model.ICOutputEntry;
import org.eclipse.cdt.managedbuilder.core.IConfiguration;
import org.eclipse.cdt.managedbuilder.core.IManagedBuildInfo;
import org.eclipse.cdt.managedbuilder.core.ManagedBuildManager;
import org.eclipse.cdt.managedbuilder.envvar.IEnvironmentVariableProvider;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;

public class LaunchEnvironmentVariables {
	public static void apply(ILaunchConfigurationWorkingCopy wc,
			ICProject project) throws CoreException{
		
		String os = Platform.getOS();
		if(os.equals(Platform.OS_WIN32))setWin32PATH(wc, project);
		if(os.equals(Platform.OS_LINUX))setLinuxLD_LIBRARY_PATH(wc, project);
		if(os.equals(Platform.OS_MACOSX))setMacDYLD_LIBRARY_PATH(wc, project);
	}
	
	
	private static void setMacDYLD_LIBRARY_PATH(ILaunchConfigurationWorkingCopy wc,
			ICProject project) throws CoreException{
		setPathEnvironmentVariable(wc,project,"DYLD_LIBRARY_PATH");
	}
	private static void setLinuxLD_LIBRARY_PATH(ILaunchConfigurationWorkingCopy wc,
			ICProject project) throws CoreException{
		setPathEnvironmentVariable(wc,project,"LD_LIBRARY_PATH");
	}
	private static void setWin32PATH(ILaunchConfigurationWorkingCopy wc,
			ICProject project) throws CoreException{
		setPathEnvironmentVariable(wc,project,"PATH");
	}
	
	//caveat:not for generic environment variable
	@SuppressWarnings("unchecked")
	private static void setPathEnvironmentVariable(ILaunchConfigurationWorkingCopy wc,
			ICProject project,String environmentVariableName) throws CoreException {
		String path=getBuildEnvironmentVariable(environmentVariableName,project);
		String pathSeparator=System.getProperty("path.separator");//assumption that it is only 1 char wide
		if(!path.equals(""))
			if( !(path.charAt(path.length()-1)+"").equals(pathSeparator))
				path+=pathSeparator;
		
		IProject[] libProject=getReferencedProjects(project);
		if(libProject.length==0)return;
		String libPath=generateLibPath(libProject,pathSeparator);

//		Map m3=project.getOptions(true);//get information abt formatter etc
//		Map m3=getBuildEnvironmentVariables(project);//get the entire build env variables
		Map map=new TreeMap();
		map.put(environmentVariableName, path+libPath);
		wc.setAttribute(ILaunchManager.ATTR_ENVIRONMENT_VARIABLES, map);
//		wc.setAttribute(ILaunchManager.ATTR_APPEND_ENVIRONMENT_VARIABLES, true);
	}

	private static String getBuildEnvironmentVariable(String key,ICProject project) {
		String result="";
		IManagedBuildInfo info = ManagedBuildManager.getBuildInfo(project.getUnderlyingResource());
		if (info != null) {
			IConfiguration ic=info.getDefaultConfiguration();
			IEnvironmentVariableProvider evp=ManagedBuildManager.getEnvironmentVariableProvider();
			IEnvironmentVariable ev=evp.getVariable(key, ic, false);
			if(ev!=null)
				result=ev.getValue();	
		}
		return result;
	}

	private static IProject[] getReferencedProjects(ICProject project) throws CoreException {
		IProject prj=project.getProject();
		IProjectDescription desc = prj.getDescription();
		IProject ref[]=desc.getReferencedProjects();
		return ref;
	}
	
	private static String generateLibPath(IProject[] libProject, String pathSeparator) {
		if(libProject.length<1)return "";
		
		String result="";
		
		for(int x=0;x<libProject.length;x++){
			IManagedBuildInfo info = ManagedBuildManager.getBuildInfo(libProject[x]);
			IConfiguration config = info.getDefaultConfiguration();
//			ICSourceEntry[] sources = config.getSourceEntries();
			ICOutputEntry[]  dirs = config.getBuildData().getOutputDirectories();	
			for (ICOutputEntry outputEntry : dirs) {
				IPath location = outputEntry.getFullPath();
				IPath parameter;
				if(location.segmentCount()== 0){
					parameter=libProject[x].getFullPath();
				}else{
					parameter=libProject[x].getFolder(location).getFullPath();	
				}
				result+= "${workspace_loc:" + parameter.toPortableString() + "}"+pathSeparator;
			}	
		}
		
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public static Map getBuildEnvironmentVariables(ICProject project) {
		Map result=new TreeMap();
		IManagedBuildInfo info = ManagedBuildManager.getBuildInfo(project.getUnderlyingResource());
		if (info != null) {
			IConfiguration ic=info.getDefaultConfiguration();
			IEnvironmentVariableProvider evp=ManagedBuildManager.getEnvironmentVariableProvider();
			IEnvironmentVariable[] ev=evp.getVariables(ic, false);
			
			for(IEnvironmentVariable iev:ev){
				result.put(iev.getName(), iev.getValue());
			}
			
		}
		return result;
	}
	
}
