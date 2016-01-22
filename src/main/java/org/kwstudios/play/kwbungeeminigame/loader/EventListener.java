package org.kwstudios.play.kwbungeeminigame.loader;

import java.util.Collection;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.scheduler.BukkitTask;
import org.kwstudios.play.kwbungeeminigame.holders.GameVariables;
import org.kwstudios.play.kwbungeeminigame.json.GameValues;
import org.kwstudios.play.kwbungeeminigame.json.LobbyResponse;
import org.kwstudios.play.kwbungeeminigame.json.MinigameAction;
import org.kwstudios.play.kwbungeeminigame.minigame.EndGame;
import org.kwstudios.play.kwbungeeminigame.minigame.MinigameMessageHandler;
import org.kwstudios.play.kwbungeeminigame.sender.JedisMessageSender;
import org.kwstudios.play.kwbungeeminigame.toolbox.ConfigFactory;
import org.kwstudios.play.kwbungeeminigame.toolbox.ConstantHolder;

import com.google.gson.Gson;

public final class EventListener implements Listener {

	private FileConfiguration fileConfiguration;

	public EventListener(PluginLoader plugin, FileConfiguration fileConfiguration) {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		this.fileConfiguration = fileConfiguration;
	}

	private BukkitTask lobbyShutdown = null;
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		final int players = Bukkit.getOnlinePlayers().size();

		List<World> worlds = Bukkit.getWorlds();
		for (World w : worlds) {
			if (w.getName().toLowerCase().contains("lobby")) {
				event.getPlayer().setMetadata(ConstantHolder.TELEPORT_METADATA_KEY,
						new FixedMetadataValue(PluginLoader.getInstance(), true));
				event.getPlayer().teleport(w.getSpawnLocation());
			}
		}

		switch(PluginLoader.getGamevalues().getGame_type().toLowerCase()) {
		case "bedwars":
			event.getPlayer().performCommand("bw join " + PluginLoader.getGamevalues().getMap_name());
			break;
		case "ragemode":
			event.getPlayer().performCommand("rm join " + PluginLoader.getGamevalues().getMap_name());
			break;
		case "paintball":
			event.getPlayer().performCommand("pb join " + PluginLoader.getGamevalues().getMap_name());
			break;
		case "hungergames":
			event.getPlayer().performCommand("hg join " + PluginLoader.getGamevalues().getMap_name());
			break;
		default :
			event.getPlayer().performCommand("kill");
			break;
		}


		Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(PluginLoader.getInstance(), new Runnable() {
			@Override
			public void run() {
				Gson gson = new Gson();
				LobbyResponse response = new LobbyResponse();
				response.setAction(MinigameAction.UPDATE.getText());
				response.setCurrentPlayers(players);
				response.setGameType(PluginLoader.getGamevalues().getGame_type());
				response.setMapName(PluginLoader.getGamevalues().getMap_name());
				response.setServerName(PluginLoader.getGamevalues().getServer_name());
				String message = gson.toJson(response);
				JedisMessageSender.sendMessageToChannel(PluginLoader.getJedisValues().getHost(), PluginLoader
						.getJedisValues().getPort(), PluginLoader.getJedisValues().getPassword(), PluginLoader
						.getJedisValues().getChannelToSend(), message);
			}
		}, 1);
		
		if(lobbyShutdown != null) {
			lobbyShutdown.cancel();
		}
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		GameVariables.playersWith1LobbyTP.remove(event.getPlayer());
		GameVariables.playersWith2LobbyTP.remove(event.getPlayer());
		if(Bukkit.getOnlinePlayers().size() <= 1) {
			if(GameVariables.isRunning) {
				MinigameMessageHandler.sendRemoveMessage();
				Bukkit.getServer().shutdown();	
			} else {
				lobbyShutdown = Bukkit.getScheduler().runTaskLater(PluginLoader.getInstance(), new Runnable() {
					
					@Override
					public void run() {
						MinigameMessageHandler.sendRemoveMessage();
						Bukkit.getServer().shutdown();				
					}
				}, 2400);
			}
		}
	}

	@EventHandler
	public void onTeleport(PlayerTeleportEvent event) {
		if (!event.getPlayer().hasMetadata(ConstantHolder.TELEPORT_METADATA_KEY)) {
			if (event.getTo().getWorld().getName().toLowerCase().contains("lobby")) {
				if (GameVariables.playersWith1LobbyTP.contains(event.getPlayer())) {
					GameVariables.playersWith2LobbyTP.add(event.getPlayer());
				} else {
					GameVariables.playersWith1LobbyTP.add(event.getPlayer());
				}
				Collection<? extends Player> onp = Bukkit.getOnlinePlayers();
				if (GameVariables.playersWith2LobbyTP.containsAll(onp)) {
					EndGame.stopFinishedGame();
				}
			}
			if (!GameVariables.isRunning) {
				if (event.getFrom().getWorld().getName().toLowerCase().contains("lobby")) {
					if (!event.getTo().getWorld().getName().toLowerCase().contains("lobby")) {
						GameVariables.isRunning = true;
						// TODO Remove the server in the lobby
					}
				}
			}
		} else {
			event.getPlayer().removeMetadata(ConstantHolder.TELEPORT_METADATA_KEY, PluginLoader.getInstance());
		}
	}

}
