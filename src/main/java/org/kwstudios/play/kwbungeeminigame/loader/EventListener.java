package org.kwstudios.play.kwbungeeminigame.loader;

import java.util.Collection;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.scheduler.BukkitTask;
import org.kwstudios.play.kwbungeeminigame.holders.GameVariables;
import org.kwstudios.play.kwbungeeminigame.json.LobbyResponse;
import org.kwstudios.play.kwbungeeminigame.json.MinigameAction;
import org.kwstudios.play.kwbungeeminigame.minigame.EndGame;
import org.kwstudios.play.kwbungeeminigame.minigame.MinigameMessageHandler;
import org.kwstudios.play.kwbungeeminigame.sender.JedisMessageSender;
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
	public void onPlayerJoin(final PlayerJoinEvent event) {

		if (PluginLoader.getTimeoutShutdown() != null) {
			PluginLoader.getTimeoutShutdown().cancel();
		}

		if (lobbyShutdown != null) {
			lobbyShutdown.cancel();
			System.out.println("the shutdown has been canceled");
		}

		final int players = Bukkit.getOnlinePlayers().size();

		/*
		 * List<World> worlds = Bukkit.getWorlds(); for (World w : worlds) { if
		 * (w.getName().toLowerCase().contains("lobby")) {
		 * event.getPlayer().setMetadata(ConstantHolder.TELEPORT_METADATA_KEY,
		 * new FixedMetadataValue(PluginLoader.getInstance(), true));
		 * event.getPlayer().teleport(w.getSpawnLocation());
		 * System.out.println("teleporting" + w.getName()); } else {
		 * System.out.println("not matching" + w.getName()); } }
		 */

		Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(PluginLoader.getInstance(), new Runnable() {
			@Override
			public void run() {
				Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(PluginLoader.getInstance(), new Runnable() {

					@Override
					public void run() {
						switch (PluginLoader.getGamevalues().getGame_type().toLowerCase()) {
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
						default:
							event.getPlayer().performCommand("kill");
							break;
						}

					}

				});
			}
		}, 1);

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
				JedisMessageSender.sendMessageToChannel(PluginLoader.getJedisValues().getHost(),
						PluginLoader.getJedisValues().getPort(), PluginLoader.getJedisValues().getPassword(),
						PluginLoader.getJedisValues().getChannelToSend(), message);
			}
		}, 1);
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		GameVariables.playersWithTPOutLobby.remove(event.getPlayer());
		GameVariables.playersWithTPOutAndInLobby.remove(event.getPlayer());
		if (Bukkit.getOnlinePlayers().size() <= 1) {
			System.out.println("the server is now empty");
			if (GameVariables.isRunning) {
				System.out.println("shuting down instantly");
				Bukkit.getServer().shutdown();
			} else {
				System.out.println("starting shutdowncountdown");
				lobbyShutdown = Bukkit.getScheduler().runTaskLaterAsynchronously(PluginLoader.getInstance(),
						new Runnable() {
							@Override
							public void run() {
								try {
									MinigameMessageHandler.sendRemoveMessage().join();
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
								Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(PluginLoader.getInstance(),
										new Runnable() {
									@Override
									public void run() {
										Bukkit.getServer().shutdown();
									}
								});
							}
						}, 2400);
			}
		} else {
			final int players = Bukkit.getOnlinePlayers().size() - 1;
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
					JedisMessageSender.sendMessageToChannel(PluginLoader.getJedisValues().getHost(),
							PluginLoader.getJedisValues().getPort(), PluginLoader.getJedisValues().getPassword(),
							PluginLoader.getJedisValues().getChannelToSend(), message);
				}
			}, 1);
		}
	}

	@EventHandler
	public void onTeleport(PlayerTeleportEvent event) {
		if (event.getCause() == TeleportCause.PLUGIN) {
			if (!event.getPlayer().hasMetadata(ConstantHolder.TELEPORT_METADATA_KEY)) {
				if ((!event.getTo().getWorld().getName().toLowerCase().contains("lobby"))
						&& event.getFrom().getWorld().getName().toLowerCase().contains("lobby"))
					GameVariables.playersWithTPOutLobby.add(event.getPlayer());

				if (event.getTo().getWorld().getName().toLowerCase().contains("lobby")
						&& (!event.getFrom().getWorld().getName().toLowerCase().contains("lobby"))
						&& GameVariables.playersWithTPOutLobby.contains(event.getPlayer())) {
					GameVariables.playersWithTPOutAndInLobby.add(event.getPlayer());
				}

				Collection<? extends Player> onp = Bukkit.getOnlinePlayers();
				if (GameVariables.playersWithTPOutAndInLobby.containsAll(onp)) {
					EndGame.stopFinishedGame();
				}

				if (!GameVariables.isRunning) {
					if (event.getFrom().getWorld().getName().toLowerCase().contains("lobby")) {
						if (!event.getTo().getWorld().getName().toLowerCase().contains("lobby")) {
							GameVariables.isRunning = true;
							MinigameMessageHandler.sendRemoveMessage();
							System.out.println("the game has been set running");
						}
					}
				}
			} else {
				event.getPlayer().removeMetadata(ConstantHolder.TELEPORT_METADATA_KEY, PluginLoader.getInstance());
			}
		}
	}

}
