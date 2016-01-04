package org.kwstudios.play.bungeelobby.signs;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.kwstudios.play.bungeelobby.toolbox.ConfigFactory;

public class SignCreator {

	public synchronized static boolean createNewSign(Sign sign, String game) {
//	TODO Check the way the signs are stored in the SignConfiguration
		File file = SignConfiguration.getYamlSignsFile();
		FileConfiguration fileConfiguration = SignConfiguration.getSignConfiguration();

		Set<String> signs = ConfigFactory.getKeysUnderPath("signs", false, fileConfiguration);
		if (signs != null) {
			if (signs.contains(Integer.toString(sign.getX()) + Integer.toString(sign.getY())
					+ Integer.toString(sign.getZ()) + sign.getWorld().getName())) {
				return false;
			}
		}

		int x = sign.getX();
		int y = sign.getY();
		int z = sign.getZ();
		String world = sign.getWorld().getName();
		String path = "signs." + Integer.toString(x) + Integer.toString(y) + Integer.toString(z) + world;

		ConfigFactory.setString(path, "game", game, fileConfiguration);
		ConfigFactory.setString(path, "world", world, fileConfiguration);
		ConfigFactory.setInt(path, "x", x, fileConfiguration);
		ConfigFactory.setInt(path, "y", y, fileConfiguration);
		ConfigFactory.setInt(path, "z", z, fileConfiguration);

		try {
			fileConfiguration.save(file);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

	}

	public synchronized static boolean removeSign(Sign sign) {
		File file = SignConfiguration.getYamlSignsFile();
		FileConfiguration fileConfiguration = SignConfiguration.getSignConfiguration();

		int x = sign.getX();
		int y = sign.getY();
		int z = sign.getZ();
		String world = sign.getWorld().getName();
		String path = "signs." + Integer.toString(x) + Integer.toString(y) + Integer.toString(z) + world;

		if (fileConfiguration.contains(path)) {
			fileConfiguration.set(path, null);
			try {
				fileConfiguration.save(file);
				return true;
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}
		return false;
	}


	/**
	 * Checks whether the given Sign is properly configured to be an JoinSign or
	 * not.
	 * 
	 * @param sign
	 *            The Sign for which the check should be done.
	 * @return True if the signs.yml contains a correct value for the given Sign
	 *         instance.
	 */
	public static boolean isJoinSign(Sign sign) {
// TODO Validate sign via its location and a list of sign locations.

		return false;
	}

}
