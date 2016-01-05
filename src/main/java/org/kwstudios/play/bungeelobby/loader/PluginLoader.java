package org.kwstudios.play.bungeelobby.loader;

import java.util.HashMap;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.kwstudios.play.bungeelobby.commands.CommandParser;
import org.kwstudios.play.bungeelobby.listener.BungeeMessageListener;
import org.kwstudios.play.bungeelobby.listener.JedisMessageListener;
import org.kwstudios.play.bungeelobby.sender.JedisMessageSender;
import org.kwstudios.play.bungeelobby.toolbox.ConfigFactory;
import org.kwstudios.play.bungeelobby.toolbox.ConstantHolder;

public class PluginLoader extends JavaPlugin {

	private static PluginLoader instance = null;
	
	@Override
	public void onEnable() {
		super.onEnable();

		PluginLoader.instance = this;

		PluginDescriptionFile pluginDescriptionFile = getDescription();
		Logger logger = Logger.getLogger("Minecraft");

		new EventListener(this, getConfig());

		logger.info(pluginDescriptionFile.getName() + " was loaded successfully! (Version: "
				+ pluginDescriptionFile.getVersion() + ")");
		// getConfig().options().copyDefaults(true);
		// saveConfig();
		
		// TODO Use BungeeCord messaging for Player-save actions
		//this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
		//new BungeeMessageListener();
		
		// Jedis Listener Setup
		
		final String password = ConfigFactory.getString("config", "password", getConfig());
		
		new JedisMessageListener(ConstantHolder.JEDIS_SERVER, password, "lobby");
		Bukkit.getServer().getScheduler().runTaskLater(this, new Runnable() {
			
			@Override
			public void run() {
				JedisMessageSender.sendMessageToChannel(ConstantHolder.JEDIS_SERVER, password, "lobby", "This is the first Jedis Test!!!");
				
			}
		}, 100);
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

	public static PluginLoader getInstance() {
		return PluginLoader.instance;
	}

}
