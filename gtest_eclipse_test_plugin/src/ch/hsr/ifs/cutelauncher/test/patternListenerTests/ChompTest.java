package ch.hsr.ifs.cutelauncher.test.patternListenerTests;

import junit.framework.TestCase;


public class ChompTest extends TestCase{
	public void testChomp2EqualStrings() throws Exception {
		String one = "Word";
		assertEquals(one, chomp(one, one));
	}

	public void testChompingTwoUniqueStrings() throws Exception {
		assertEquals("", chomp("Word", "Bug"));
	}

	public void testChompingNoSpacesFirstBiggerThanSecond() throws Exception {
		assertEquals("Word", chomp("Wordy", "Word"));
	}

	public void testChompingNoSpacesFirstSmallerThanSecond() throws Exception {
		assertEquals("Word", chomp("Word", "Wordy"));
	}

	public void testChompingFirstIsEmpty() throws Exception {
		assertEquals("", chomp("", "Word"));
	}

	public void testChompingSecondIsEmpty() throws Exception {
		assertEquals("", chomp("Word", ""));
	}

	public void testChompingBothAreEmpty() throws Exception {
		assertEquals("", chomp("", ""));
	}

	public void testChompingWithSpaces() throws Exception {
		assertEquals("Word To Your ", chomp("Word To Your Momma", "Word To Your Poppa"));
		assertEquals("Word", chomp("Wordy Up", "Word To Your Poppa"));
	}

	private String chomp(String one, String two) {
		StringBuffer common = new StringBuffer();
		int x = one.length() > two.length() ? two.length() : one.length();
		for (int i=0; i<x;i++){
			if (one.charAt(i) == two.charAt(i))
				common.append(one.charAt(i));
			else
				break;
		}
		return common.toString();
	}
}
