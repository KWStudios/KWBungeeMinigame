package org.kwstudios.play.kwbungeeminigame.loader;

import java.util.Collection;

import org.apache.logging.log4j.core.net.Priority;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.scheduler.BukkitTask;
import org.kwstudios.play.kwbungeeminigame.enums.MinigameType;
import org.kwstudios.play.kwbungeeminigame.holders.GameVariables;
import org.kwstudios.play.kwbungeeminigame.json.BungeeRequest;
import org.kwstudios.play.kwbungeeminigame.json.FriendsRequest;
import org.kwstudios.play.kwbungeeminigame.listener.KWChannelMessageListener;
import org.kwstudios.play.kwbungeeminigame.minigame.EndGame;
import org.kwstudios.play.kwbungeeminigame.minigame.MinigameMessageHandler;
import org.kwstudios.play.kwbungeeminigame.minigame.MinigameSpectator;
import org.kwstudios.play.kwbungeeminigame.toolbox.ConstantHolder;
import org.kwstudios.play.kwbungeeminigame.toolbox.OnlineNonSpectators;
import org.kwstudios.play.kwbungeeminigame.toolbox.RandomPlayerGetter;

import com.google.gson.Gson;

public final class EventListener implements Listener {

	private FileConfiguration fileConfiguration;

	public EventListener(PluginLoader plugin, FileConfiguration fileConfiguration) {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		this.fileConfiguration = fileConfiguration;
	}

	private BukkitTask lobbyShutdown = null;

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerJoin(final PlayerJoinEvent event) {

		if (GameVariables.isRunning) {
			/* if is is an infinite game, let the player join anyways*/
			MinigameType mt = MinigameType.fromString(PluginLoader.getGamevalues().getGame_type().toLowerCase());
			if(mt.isInfinite()) {
				event.getPlayer().performCommand(mt.getCommand());
			} else {
				MinigameSpectator.setSpectatorAndTeleport(event.getPlayer(), RandomPlayerGetter.getRandomOnlinePlayer().getLocation());
				final FriendsRequest friendsRequest = new FriendsRequest(event.getPlayer().getName(), new String[] {},
						true);
				final BungeeRequest bungeeRequest = new BungeeRequest(null, friendsRequest, true);
				Bukkit.getServer().getScheduler().runTaskLater(PluginLoader.getInstance(), new Runnable() {

					@Override
					public void run() {
						GameVariables.queuedFriendsRequests.add(event.getPlayer());
						Gson gson = new Gson();
						KWChannelMessageListener.sendMessage(gson.toJson(bungeeRequest), event.getPlayer());
					}
				}, 1);
			}
			
		} else {
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
							MinigameType mt = MinigameType.fromString(PluginLoader.getGamevalues().getGame_type().toLowerCase());

							if(mt.getCommand() != null) {
							event.getPlayer().performCommand(mt.getCommand());
							} else {
								event.getPlayer().performCommand("kill");
							}
						}
	
					});
				}
			}, 1);
	
			MinigameMessageHandler.sendUpdateMessage(players);
	
			Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(PluginLoader.getInstance(), new Runnable() {
				@Override
				public void run() {
					Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(PluginLoader.getInstance(), new Runnable() {
						@Override
						public void run() {
							for (Player player : Bukkit.getOnlinePlayers()) {
								player.hidePlayer(event.getPlayer());
							}
	
							Bukkit.getServer().getScheduler().runTaskLater(PluginLoader.getInstance(), new Runnable() {
								@Override
								public void run() {
									for (Player player : Bukkit.getOnlinePlayers()) {
										player.showPlayer(event.getPlayer());
									}
								}
							}, 5);
						}
	
					});
				}
			}, 5);
		}
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		GameVariables.playersWithTPOutLobby.remove(event.getPlayer());
		GameVariables.playersWithTPOutAndInLobby.remove(event.getPlayer());

		final int players = OnlineNonSpectators.get().size() - 1;

		if (players < 1) {
			System.out.println("the server is now empty");
			if (GameVariables.isRunning) {
				MinigameType mt = MinigameType.fromString(PluginLoader.getGamevalues().getGame_type().toLowerCase());
				if(mt.isInfinite()) {
					MinigameMessageHandler.sendUpdateMessage(players);
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
				} else {
					System.out.println("shuting down instantly");
					Bukkit.getServer().shutdown();
				}
			} else {
				MinigameMessageHandler.sendUpdateMessage(players);
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
			if (!GameVariables.isRunning) {
				MinigameMessageHandler.sendUpdateMessage(players);
			}
		}

		if (GameVariables.queuedFriendsRequests.contains(event.getPlayer())) {
			GameVariables.queuedFriendsRequests.remove(event.getPlayer());
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

				Collection<? extends Player> onp = OnlineNonSpectators.get();
				if (GameVariables.playersWithTPOutAndInLobby.containsAll(onp)) {
					EndGame.stopFinishedGame();
				}

				if (!GameVariables.isRunning) {
					if (event.getFrom().getWorld().getName().toLowerCase().contains("lobby")) {
						if (!event.getTo().getWorld().getName().toLowerCase().contains("lobby")) {
							GameVariables.isRunning = true;
							if(!MinigameType.fromString(PluginLoader.getGamevalues().getGame_type().toLowerCase()).isInfinite()) {
								MinigameMessageHandler.sendRemoveMessage();
							}
							System.out.println("the game has been set running");
						}
					}
				}
			} else {
				event.getPlayer().removeMetadata(ConstantHolder.TELEPORT_METADATA_KEY, PluginLoader.getInstance());
			}
		}
	}

	// Remove join and quit messages

	@EventHandler(priority = EventPriority.LOWEST)
	public void onJoinMessage(PlayerJoinEvent event) {
		event.setJoinMessage("");
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onQuitMessage(PlayerQuitEvent event) {
		event.setQuitMessage("");
	}

	// Block leave commands
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onCommand(PlayerCommandPreprocessEvent event) {
		String command = event.getMessage().toLowerCase();
		if (command.contains("leave")) {
			event.setMessage(command.split(" ")[0]);
		}
	}

}
