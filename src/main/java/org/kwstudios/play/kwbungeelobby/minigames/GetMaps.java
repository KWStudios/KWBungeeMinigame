package org.kwstudios.play.kwbungeelobby.minigames;

import org.bukkit.configuration.file.FileConfiguration;
import org.kwstudios.play.kwbungeelobby.toolbox.ConfigFactory;

public class GetMaps {

	public static int getConfigMapsCount(FileConfiguration fileConfiguration) {
		int n = 0;
		if (ConfigFactory.getKeysUnderPath("settings.maps", false, fileConfiguration) == null) {
			return 0;
		}
		n = ConfigFactory.getKeysUnderPath("settings.maps", false, fileConfiguration).size();
		return n;
	}

	public static String[] getMapNames(FileConfiguration fileConfiguration) {
		String[] names = new String[getConfigMapsCount(fileConfiguration)];
		if (ConfigFactory.getKeysUnderPath("settings.maps", false, fileConfiguration) != null) {
			ConfigFactory.getKeysUnderPath("settings.maps", false, fileConfiguration).toArray(names);
		}
		return names;
	}

}
