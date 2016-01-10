package org.kwstudios.play.kwbungeeminigame.minigames;

public enum MinigameType {

	BEDWARS("bedwars"), RAGEMODE("ragemode"), PAINTBALL("paintball"), HUNGERGAMES("hungergames"), MOBARENA("mobarena");

	private String text;

	MinigameType(String text) {
		this.text = text.toLowerCase();
	}

	public String getText() {
		return this.text;
	}

	public static MinigameType fromString(String text) {
		if (text != null) {
			for (MinigameType b : MinigameType.values()) {
				if (text.equalsIgnoreCase(b.text)) {
					return b;
				}
			}
		}
		return null;
	}

}
