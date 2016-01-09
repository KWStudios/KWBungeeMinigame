package org.kwstudios.play.bungeelobby.minigames;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.block.Sign;
import org.kwstudios.play.bungeelobby.loader.PluginLoader;
import org.kwstudios.play.bungeelobby.signs.SignCreator;
import org.kwstudios.play.bungeelobby.toolbox.ConfigFactory;

public class MinigameRequests {

	private static HashMap<Sign, MinigameType> queuedRequests = new HashMap<Sign, MinigameType>();

	public boolean createRequest(MinigameType type, Sign sign) {
		if (queuedRequests.containsKey(sign)) {
			return false;
		}

		String command = ConfigFactory.getValueOrSetDefault("settings.minigames", "command", "ruby test.rb",
				PluginLoader.getInstance().getConfig());

		ProcessBuilder builder = new ProcessBuilder(command);
		Map<String, String> map = builder.environment();
		map.put("type", type.getText());
		map.put("map", SignCreator.getMapFromSign(sign));
		try {
			builder.start();
		} catch (IOException e) {
			e.printStackTrace();
		}

		queuedRequests.put(sign, type);

		return true;
	}

	public static void removeQueuedRequest(Sign sign) {
		if (queuedRequests.containsKey(sign)) {
			queuedRequests.remove(sign);
		}
	}

	public static boolean isQueuedForRequest(Sign sign) {
		if (queuedRequests.containsKey(sign)) {
			return true;
		} else {
			return false;
		}
	}

}
