package ch.hsr.ifs.cutelauncher;

import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;
public class EclipseConsole {
	
	static MessageConsoleStream stream;
	static private MessageConsole console;
	
	public static MessageConsoleStream getConsole(){
		if(null == stream) {
			console = new MessageConsole("Cute Plugin Console",null);
			//,CuteLauncherPlugin.getImageDescriptor("obj16/cute_app.gif")); //FIXME
			//console.activate();
			ConsolePlugin.getDefault().getConsoleManager().addConsoles(new IConsole[]{ console });
			MessageConsoleStream s = console.newMessageStream();
			stream=s;
			
		}
		return stream;
	}
	
	public static void showConsole(){
		console.activate();
	}
	public static void print(String s){
		MessageConsoleStream stream1=getConsole();
		stream1.print(s);
		showConsole();
	}
	public static void println(String s){
		MessageConsoleStream stream1=getConsole();
		stream1.println(s);
		showConsole();
	}
	//stream.println("Hello, world!");
}
//@see http://www.jevon.org/wiki/Writing_to_a_Console_in_Eclipse