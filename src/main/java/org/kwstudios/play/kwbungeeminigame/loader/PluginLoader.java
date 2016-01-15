package org.kwstudios.play.kwbungeeminigame.loader;

import java.util.List;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.kwstudios.play.kwbungeeminigame.commands.CommandParser;
import org.kwstudios.play.kwbungeeminigame.holders.JedisValues;
import org.kwstudios.play.kwbungeeminigame.json.LobbyResponse;
import org.kwstudios.play.kwbungeeminigame.json.MinigameAction;
import org.kwstudios.play.kwbungeeminigame.listener.BungeeMessageListener;
import org.kwstudios.play.kwbungeeminigame.listener.JedisMessageListener;
import org.kwstudios.play.kwbungeeminigame.sender.JedisMessageSender;
import org.kwstudios.play.kwbungeeminigame.toolbox.ConfigFactory;
import org.kwstudios.play.kwbungeeminigame.toolbox.ConstantHolder;

import com.google.gson.Gson;

import redis.clients.jedis.Protocol;

public class PluginLoader extends JavaPlugin {

	private static PluginLoader instance = null;

	private static JedisMessageListener lobbyChannelListener = null;
	private static JedisValues jedisValues = new JedisValues();

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
		this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
		new BungeeMessageListener();

		// Jedis Listener Setup

		reloadJedisConfig();

		setupJedisListener();

		saveConfig();
		
		final String type = ConfigFactory.getValueOrSetDefault("settings", "minigame-type", "bedwars", getConfig());
		final String map = ConfigFactory.getValueOrSetDefault("settings", "minigame-map", "Bedwars_1", getConfig());
		final String server = ConfigFactory.getValueOrSetDefault("settings", "minigame-server", "minigame_1", getConfig());
		final int players = Bukkit.getOnlinePlayers().size();

		Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(this, new Runnable() {
			@Override
			public void run() {
				Gson gson = new Gson();
				LobbyResponse response = new LobbyResponse();
				response.setAction(MinigameAction.CREATE.getText());
				response.setCurrentPlayers(players);
				response.setGameType(type);
				response.setMapName(map);
				response.setServerName(server);
				String message = gson.toJson(response);
				JedisMessageSender.sendMessageToChannel(jedisValues.getHost(), jedisValues.getPort(),
						jedisValues.getPassword(), jedisValues.getChannelToSend(), message);
			}
		}, 1);
	}

	@Override
	public void onDisable() {
		super.onDisable();

		// Jedis stuff
		lobbyChannelListener.getJedisPubSub().unsubscribe();

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

	public void reloadJedisConfig() {
		String host = ConfigFactory.getValueOrSetDefault("settings.jedis", "host", Protocol.DEFAULT_HOST, getConfig());
		jedisValues.setHost(host);

		int port = ConfigFactory.getValueOrSetDefault("settings.jedis", "port", Protocol.DEFAULT_PORT, getConfig());
		jedisValues.setPort(port);

		String password = ConfigFactory.getValueOrSetDefault("settings.jedis", "password", null, getConfig());
		jedisValues.setPassword(password);

		List<String> channelsToListen = getConfig().getStringList("settings.jedis.channelsToListen");
		if (channelsToListen.isEmpty()) {
			channelsToListen.add("lobby");
			channelsToListen.add("anotherChannelToListen");
			getConfig().set("settings.jedis.channelsToListen", channelsToListen);
		}
		jedisValues.setChannelsToListen(channelsToListen.toArray(new String[channelsToListen.size()]));

		String channelToSend = ConfigFactory.getValueOrSetDefault("settings.jedis", "channelToSend", "minigame",
				getConfig());
		jedisValues.setChannelToSend(channelToSend);
	}

	private void setupJedisListener() {
		PluginLoader.lobbyChannelListener = new JedisMessageListener(jedisValues.getHost(), jedisValues.getPort(),
				jedisValues.getPassword(), jedisValues.getChannelsToListen()) {
			@Override
			public synchronized void taskOnMessageReceive(String channel, String message) {

			}
		};
	}

	public static JedisValues getJedisValues() {
		return jedisValues;
	}

	public static PluginLoader getInstance() {
		return PluginLoader.instance;
	}

}