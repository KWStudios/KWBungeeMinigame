package org.kwstudios.play.kwbungeeminigame.toolbox;

public class ValueChecker {

	public static boolean isInt(String string) {
		try {
			Integer.parseInt(string);
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	public static boolean isDouble(String string) {
		try {
			Double.parseDouble(string);
		} catch (Exception e) {
			return false;
		}
		return true;
	}
}
