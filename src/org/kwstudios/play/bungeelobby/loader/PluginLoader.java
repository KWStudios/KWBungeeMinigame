package org.kwstudios.play.bungeelobby.loader;

import java.util.HashMap;
import java.util.logging.Logger;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.kwstudios.play.bungeelobby.commands.CommandParser;
import org.kwstudios.play.bungeelobby.toolbox.ConfigFactory;
import org.kwstudios.play.bungeelobby.toolbox.MotdListGetter;

public class PluginLoader extends JavaPlugin {

	private static PluginLoader instance = null;
	private static HashMap<String, String> headers = new HashMap<String, String>();
	private static HashMap<String, String> parameters = new HashMap<String, String>();

	@Override
	public void onEnable() {
		super.onEnable();

		PluginLoader.instance = this;

		MotdListGetter.getMotdsFromFile();

		PluginDescriptionFile pluginDescriptionFile = getDescription();
		Logger logger = Logger.getLogger("Minecraft");

		new EventListener(this, getConfig());

		setupApiHashMaps();

		logger.info(pluginDescriptionFile.getName() + " was loaded successfully! (Version: "
				+ pluginDescriptionFile.getVersion() + ")");
		// getConfig().options().copyDefaults(true);
		// saveConfig();
	}

	@Override
	public void onDisable() {
		super.onDisable();
		PluginDescriptionFile pluginDescriptionFile = getDescription();
		Logger logger = Logger.getLogger("Minecraft");

		logger.info(pluginDescriptionFile.getName() + " was unloaded successfully! (Version: "
				+ pluginDescriptionFile.getVersion() + ")");
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("You must be a Player!");
			return false;
		}

		Player player = (Player) sender;

		CommandParser commandParser = new CommandParser(player, command, label, args, getConfig());
		if (!commandParser.isCommand()) {
			return false;
		}

		saveConfig();

		return true;
	}

	public void setupApiHashMaps() {
		if (getConfig().isSet("settings.authorization")) {
			String authorization = ConfigFactory.getString("settings", "authorization", getConfig());
			headers.put("Authorization-Code", authorization);
		}
	}

	public static PluginLoader getInstance() {
		return PluginLoader.instance;
	}

	public static HashMap<String, String> getHeaders() {
		return headers;
	}

	public static HashMap<String, String> getParameters() {
		return parameters;
	}

}
