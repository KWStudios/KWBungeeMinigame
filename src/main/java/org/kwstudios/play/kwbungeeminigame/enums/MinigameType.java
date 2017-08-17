package org.kwstudios.play.kwbungeeminigame.enums;

import org.kwstudios.play.kwbungeeminigame.loader.PluginLoader;

public enum MinigameType {

	BEDWARS("bedwars", "bw join $m"), RAGEMODE("ragemode", "rm join $m"), PAINTBALL("paintball", "pb join $m"), HUNGERGAMES("hungergames", "hg join $m"), MOBARENA("mobarena", null);

	private String text;
	private String command;

	MinigameType(String text, String command) {
		this.text = text.toLowerCase();
		this.command = command;
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
	
	public boolean isInfinite() {
		switch(this) {
		case PAINTBALL:
			return true;
		default:
			return false;
		}
	}

	public String getCommand() {
		return command.replace("$m", PluginLoader.getGamevalues().getMap_name());
	}
}
