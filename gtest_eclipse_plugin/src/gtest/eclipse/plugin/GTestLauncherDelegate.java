package gtest.eclipse.plugin;

import ch.hsr.ifs.cutelauncher.CuteLauncherDelegate;
import ch.hsr.ifs.cutelauncher.event.ConsoleEventParser;

public class GTestLauncherDelegate extends CuteLauncherDelegate {
	@Override
	protected ConsoleEventParser getConsoleEventParser() {
		return new GTestConsoleEventParser();
	}
}
