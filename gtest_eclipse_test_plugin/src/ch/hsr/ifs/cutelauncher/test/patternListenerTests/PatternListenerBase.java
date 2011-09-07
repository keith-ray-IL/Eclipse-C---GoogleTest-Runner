package ch.hsr.ifs.cutelauncher.test.patternListenerTests;

import ch.hsr.ifs.cutelauncher.test.ConsoleLikeDataTest;

public abstract class PatternListenerBase extends ConsoleLikeDataTest {
	@Override
	protected String getInputFilePath() {
		return "patternListenerTests/" + getInputFileName();
	}

	protected abstract String getInputFileName();
	
}