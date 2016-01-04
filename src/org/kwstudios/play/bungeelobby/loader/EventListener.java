package org.kwstudios.play.bungeelobby.loader;

import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.ServerListPingEvent;
import org.kwstudios.play.bungeelobby.api.PlayerJSON;
import org.kwstudios.play.bungeelobby.api.RequesterThread;
import org.kwstudios.play.bungeelobby.toolbox.ConfigFactory;
import org.kwstudios.play.bungeelobby.toolbox.ConstantHolder;
import org.kwstudios.play.bungeelobby.toolbox.MotdListGetter;

import com.google.gson.Gson;

public final class EventListener implements Listener {

	private static final String WORLD_COMMANDS_PATH = "settings.CommandsOnWorldChanged";
	private static final String LOCKED_WORLDS_PATH = "settings.LockedWorlds";
	private static final String COMMAND_STRING = "command";

	private FileConfiguration fileConfiguration;

	public EventListener(PluginLoader plugin, FileConfiguration fileConfiguration) {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		this.fileConfiguration = fileConfiguration;
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onWorldChange(PlayerChangedWorldEvent event) {
		String originWorld = event.getFrom().getName();
		boolean isConfiguredForCommand = false;
		boolean isPathSet = false;
		try {
			isPathSet = fileConfiguration.getConfigurationSection(WORLD_COMMANDS_PATH).isSet(originWorld);
		} catch (Exception e) {
			// System.out.println("The " + originWorld + " key was not set yet
			// in the configs! Skipping that...");
			return;
		}

		if (isPathSet) {
			// Set<String> worldList =
			// ConfigFactory.getKeysUnderPath(WORLD_COMMANDS_PATH, false,
			// fileConfiguration);
			boolean isCommandSet = false;
			try {
				isCommandSet = fileConfiguration.getConfigurationSection(WORLD_COMMANDS_PATH + "." + originWorld)
						.isSet(COMMAND_STRING);
			} catch (Exception e) {
				// System.out.println("The command key inside the " +
				// originWorld
				// + " key was not set yet in the configs! Skipping that...");
				return;
			}
			if (isCommandSet) {
				isConfiguredForCommand = true;
			}

			// for (String world : worldList) {
			// if (world.equalsIgnoreCase(originWorld)) {
			// isConfiguredForCommand = true;
			// break;
			// }
			// }
		}

		if (isConfiguredForCommand) {
			String command = ConfigFactory.getString(WORLD_COMMANDS_PATH + "." + originWorld, COMMAND_STRING,
					fileConfiguration);
			if (event.getPlayer().hasMetadata("lobbyCommandTriggered")) {
				event.getPlayer().performCommand(command);
				event.getPlayer().removeMetadata("lobbyCommandTriggered", PluginLoader.getInstance());
			}
		}

		// Our fix for the Lobby MultiVerse-Inventories problem
		if (ConfigFactory.getKeysUnderPath("settings.lobbyCommandsOnWorldChange", false,
				PluginLoader.getInstance().getConfig()) != null) {
			Set<String> allConfiguredWorlds = ConfigFactory.getKeysUnderPath("settings.lobbyCommandsOnWorldChange",
					false, PluginLoader.getInstance().getConfig());
			for (String world : allConfiguredWorlds) {
					final List<String> allConsoleCommands = PluginLoader.getInstance().getConfig()
							.getStringList("settings.lobbyCommandsOnWorldChange." + world.trim() + "." + "console");
					final List<String> allPlayerCommands = PluginLoader.getInstance().getConfig()
							.getStringList("settings.lobbyCommandsOnWorldChange." + world.trim() + "." + "player");
					final PlayerChangedWorldEvent finalEvent = event;
					final String finalWorld = world;

					Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(PluginLoader.getInstance(),
							new Runnable() {

								@Override
								public void run() {
									if (finalEvent.getPlayer().getWorld().getName().equalsIgnoreCase(finalWorld.trim())) {
										for (String command : allConsoleCommands) {
											Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(),
													command.replace("$PLAYER$", finalEvent.getPlayer().getName()).replace(
															"$WORLD$", finalEvent.getPlayer().getWorld().getName()));
										}
	
										for (String command : allPlayerCommands) {
											finalEvent.getPlayer().performCommand(
													command.replace("$PLAYER$", finalEvent.getPlayer().getName()).replace(
															"$WORLD$", finalEvent.getPlayer().getWorld().getName()));
										}
									}
								}
							}, 80);
			}
		}
	}

	@EventHandler
	public void onPingEvent(ServerListPingEvent event) {
		Random random = new Random();
		try {
			if (MotdListGetter.getMotdList() != null) {
				int number = random.nextInt(MotdListGetter.getMotdList().size() - 1);
				String motd = MotdListGetter.getMotdList().get(number);
				if (motd != "") {
					event.setMotd(ConstantHolder.MOTD_PREFIX + ChatColor.translateAlternateColorCodes('§', motd));
				}
			} else {

			}
		} catch (Exception e) {

		}
	}

	@EventHandler
	public void onInventoryInteractEvent(InventoryClickEvent event) {
		String lockedWorld = event.getWhoClicked().getWorld().getName();
		boolean isLockedWorld = false;
		boolean isPathSet = false;
		try {
			isPathSet = fileConfiguration.getConfigurationSection(LOCKED_WORLDS_PATH).isSet(lockedWorld);
		} catch (Exception e) {
			// System.out.println("The " + lockedWorld + " key was not set yet
			// in the configs! Skipping that...");
			return;
		}

		if (isPathSet) {
			// Set<String> worldList =
			// ConfigFactory.getKeysUnderPath(WORLD_COMMANDS_PATH, false,
			// fileConfiguration);
			boolean isCommandSet = false;
			try {
				isCommandSet = fileConfiguration.getConfigurationSection(LOCKED_WORLDS_PATH + "." + lockedWorld)
						.isSet("inventory");
				isCommandSet = ConfigFactory.getBoolean(LOCKED_WORLDS_PATH + "." + lockedWorld, "inventory",
						fileConfiguration);
			} catch (Exception e) {
				// System.out.println("The command key inside the " +
				// lockedWorld
				// + " key was not set yet in the configs! Skipping that...");
				return;
			}
			if (isCommandSet) {
				isLockedWorld = true;
			}

			// for (String world : worldList) {
			// if (world.equalsIgnoreCase(originWorld)) {
			// isConfiguredForCommand = true;
			// break;
			// }
			// }
		}

		if (isLockedWorld) {
			if (!event.getWhoClicked().hasPermission("kw.interact")) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onItemPickedUpEvent(PlayerPickupItemEvent event) {
		String lockedWorld = event.getPlayer().getWorld().getName();
		boolean isLockedWorld = false;
		boolean isPathSet = false;
		try {
			isPathSet = fileConfiguration.getConfigurationSection(LOCKED_WORLDS_PATH).isSet(lockedWorld);
		} catch (Exception e) {
			// System.out.println("The " + lockedWorld + " key was not set yet
			// in the configs! Skipping that...");
			return;
		}

		if (isPathSet) {
			// Set<String> worldList =
			// ConfigFactory.getKeysUnderPath(WORLD_COMMANDS_PATH, false,
			// fileConfiguration);
			boolean isCommandSet = false;
			try {
				isCommandSet = fileConfiguration.getConfigurationSection(LOCKED_WORLDS_PATH + "." + lockedWorld)
						.isSet("items");
				isCommandSet = ConfigFactory.getBoolean(LOCKED_WORLDS_PATH + "." + lockedWorld, "items",
						fileConfiguration);
			} catch (Exception e) {
				// System.out.println("The command key inside the " +
				// lockedWorld
				// + " key was not set yet in the configs! Skipping that...");
				return;
			}
			if (isCommandSet) {
				isLockedWorld = true;
			}

			// for (String world : worldList) {
			// if (world.equalsIgnoreCase(originWorld)) {
			// isConfiguredForCommand = true;
			// break;
			// }
			// }
		}

		if (isLockedWorld) {
			if (!event.getPlayer().hasPermission("kw.items")) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		String lockedWorld = event.getPlayer().getWorld().getName();
		boolean isLockedWorld = false;
		boolean isPathSet = false;
		try {
			isPathSet = fileConfiguration.getConfigurationSection(LOCKED_WORLDS_PATH).isSet(lockedWorld);
		} catch (Exception e) {
			// System.out.println("The " + lockedWorld + " key was not set yet
			// in the configs! Skipping that...");
			return;
		}

		if (isPathSet) {
			// Set<String> worldList =
			// ConfigFactory.getKeysUnderPath(WORLD_COMMANDS_PATH, false,
			// fileConfiguration);
			boolean isCommandSet = false;
			try {
				isCommandSet = fileConfiguration.getConfigurationSection(LOCKED_WORLDS_PATH + "." + lockedWorld)
						.isSet("blocks");
				isCommandSet = ConfigFactory.getBoolean(LOCKED_WORLDS_PATH + "." + lockedWorld, "blocks",
						fileConfiguration);
			} catch (Exception e) {
				// System.out.println("The command key inside the " +
				// lockedWorld
				// + " key was not set yet in the configs! Skipping that...");
				return;
			}
			if (isCommandSet) {
				isLockedWorld = true;
			}

			// for (String world : worldList) {
			// if (world.equalsIgnoreCase(originWorld)) {
			// isConfiguredForCommand = true;
			// break;
			// }
			// }
		}

		if (isLockedWorld) {
			if (!event.getPlayer().hasPermission("kw.blocks")) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onItemSpawn(PlayerDropItemEvent event) {
		String lockedWorld = event.getPlayer().getWorld().getName();
		boolean isLockedWorld = false;
		boolean isPathSet = false;
		try {
			isPathSet = fileConfiguration.getConfigurationSection(LOCKED_WORLDS_PATH).isSet(lockedWorld);
		} catch (Exception e) {
			// System.out.println("The " + lockedWorld + " key was not set yet
			// in the configs! Skipping that...");
			return;
		}

		if (isPathSet) {
			// Set<String> worldList =
			// ConfigFactory.getKeysUnderPath(WORLD_COMMANDS_PATH, false,
			// fileConfiguration);
			boolean isCommandSet = false;
			try {
				isCommandSet = fileConfiguration.getConfigurationSection(LOCKED_WORLDS_PATH + "." + lockedWorld)
						.isSet("items");
				isCommandSet = ConfigFactory.getBoolean(LOCKED_WORLDS_PATH + "." + lockedWorld, "items",
						fileConfiguration);
			} catch (Exception e) {
				// System.out.println("The command key inside the " +
				// lockedWorld
				// + " key was not set yet in the configs! Skipping that...");
				return;
			}
			if (isCommandSet) {
				isLockedWorld = true;
			}

			// for (String world : worldList) {
			// if (world.equalsIgnoreCase(originWorld)) {
			// isConfiguredForCommand = true;
			// break;
			// }
			// }
		}

		if (isLockedWorld) {
			if (!event.getPlayer().hasPermission("kw.items")) {
				event.setCancelled(true);
			}
		}
	}

	// ******* API events, store data on player join and leave *******
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		PlayerJSON playerJSON = new PlayerJSON(player.getName(), player.getUniqueId().toString(),
				player.getFirstPlayed(), player.getLastPlayed(), player.isOnline(), player.isBanned());
		Gson gson = new Gson();
		String jsonParam = gson.toJson(playerJSON);
		String url = ConstantHolder.API_URL.replace(":server", "play.kwstudios.org").replace(":player",
				player.getName());
		HashMap<String, String> parameters = new HashMap<String, String>();
		parameters.put("data", jsonParam);

		Thread thread = new Thread(new RequesterThread(url, PluginLoader.getHeaders(), parameters));
		thread.start();
	}

	@EventHandler
	public void onPlayerLeave(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		PlayerJSON playerJSON = new PlayerJSON(player.getName(), player.getUniqueId().toString(),
				player.getFirstPlayed(), player.getLastPlayed(), false, player.isBanned());
		Gson gson = new Gson();
		String jsonParam = gson.toJson(playerJSON);
		String url = ConstantHolder.API_URL.replace(":server", "play.kwstudios.org").replace(":player",
				player.getName());
		HashMap<String, String> parameters = new HashMap<String, String>();
		parameters.put("data", jsonParam);

		Thread thread = new Thread(new RequesterThread(url, PluginLoader.getHeaders(), parameters));
		thread.start();
	}

	@EventHandler
	public void onChat(AsyncPlayerChatEvent event) {
		Player player = event.getPlayer();
		Set<Player> recipient = event.getRecipients();
		//World pWorld = player.getWorld();
		boolean onlyInWorldChat = true;
		List<String> okWorlds = null;
		if(ConfigFactory.getKeysUnderPath("settings.chat.worlds", false, PluginLoader.getInstance().getConfig()).contains(player.getWorld().getName())) {
			onlyInWorldChat = false;
			okWorlds = PluginLoader.getInstance().getConfig().getStringList("settings.chat.worlds." + player.getWorld().getName());
		}
		int i = 0;
		Player[] recipients = recipient.toArray(new Player[1]);
		
		while(i < recipient.size()) {					
			if(onlyInWorldChat) {
				if(!recipients[i].getWorld().equals(player.getWorld())){
					recipient.remove(recipients[i]);
				}
			}
			else if(!(okWorlds.contains(recipients[i].getWorld().getName()) || recipients[i].getWorld().equals(player.getWorld()))) {
				recipient.remove(recipients[i]);
			}
			i++;
		}
		//for (Player loopPlayer : recipient) {
		//	if (pWorld != loopPlayer.getWorld()) {
		//		recipient.remove(loopPlayer);
		//	}
		//}
	}

}
