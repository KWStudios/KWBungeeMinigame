package org.kwstudios.play.kwbungeeminigame.loader;

import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.kwstudios.play.kwbungeeminigame.minigames.GetMaps;
import org.kwstudios.play.kwbungeeminigame.minigames.MinigameRequests;
import org.kwstudios.play.kwbungeeminigame.minigames.MinigameServer;
import org.kwstudios.play.kwbungeeminigame.minigames.MinigameServerHolder;
import org.kwstudios.play.kwbungeeminigame.minigames.MinigameType;
import org.kwstudios.play.kwbungeeminigame.signs.SignCreator;
import org.kwstudios.play.kwbungeeminigame.signs.SignData;
import org.kwstudios.play.kwbungeeminigame.toolbox.ConfigFactory;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

public final class EventListener implements Listener {

	private FileConfiguration fileConfiguration;

	public EventListener(PluginLoader plugin,
			FileConfiguration fileConfiguration) {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		this.fileConfiguration = fileConfiguration;
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onRightClick(PlayerInteractEvent event) {
		if (event.getClickedBlock() != null
				&& event.getClickedBlock().getState() != null) {
			if (event.getClickedBlock().getState() instanceof Sign) {
				if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
					Sign sign = (Sign) event.getClickedBlock().getState();
					if (SignCreator.isJoinSign(sign)) {
						Player player = event.getPlayer();
						if (player.hasPermission("kwbungee.sign.use")) {
							if (SignData.getWaitingPlayers()
									.containsKey(player)) {
								// TODO Tell the Player he is already waiting
								// for a game.
								return;
							}

							HashMap<String, MinigameServerHolder> severHolders = PluginLoader
									.getServerHolders();
							for (Entry<String, MinigameServerHolder> serverHolder : severHolders
									.entrySet()) {
								MinigameServer server = serverHolder.getValue()
										.getActiveServerForSign(sign);
								if (server == null) {
									MinigameRequests
											.createRequest(
													MinigameType
															.fromString(ConfigFactory
																	.getValueOrSetDefault(
																			"settings.maps."
																					+ SignCreator
																							.getMapFromSign(sign),
																			"type",
																			"bedwars",
																			fileConfiguration)),
													sign);

									if (SignData.getSignPlayerCount()
											.containsKey(sign)) {
										int i = SignData.getSignPlayerCount()
												.get(sign);
										if (i >= (ConfigFactory
												.getValueOrSetDefault(
														"settings.maps."
																+ SignCreator
																		.getMapFromSign(sign),
														"teams", 1,
														PluginLoader
																.getInstance()
																.getConfig()) * ConfigFactory
												.getValueOrSetDefault(
														"settings.maps."
																+ SignCreator
																		.getMapFromSign(sign),
														"players-per-team", 1,
														PluginLoader
																.getInstance()
																.getConfig()))) {
											// TODO Tell the player that the
											// Server is full.
										} else {
											i++;
											SignData.getSignPlayerCount()
													.remove(sign);
											SignData.getSignPlayerCount().put(
													sign, i);
											SignData.getWaitingPlayers().put(
													player, sign);
										}

									} else
										SignData.getSignPlayerCount().put(sign,
												1);
								} else {
									if (server.getMiniGameResponse()
											.getCurrentPlayers() <= (ConfigFactory
											.getValueOrSetDefault(
													"settings.maps."
															+ SignCreator
																	.getMapFromSign(sign),
													"teams", 1, PluginLoader
															.getInstance()
															.getConfig()) * ConfigFactory
											.getValueOrSetDefault(
													"settings.maps."
															+ SignCreator
																	.getMapFromSign(sign),
													"players-per-team", 1,
													PluginLoader.getInstance()
															.getConfig()))) {
										ByteArrayDataOutput out = ByteStreams
												.newDataOutput();
										out.writeUTF("Connect");
										out.writeUTF(server
												.getMiniGameResponse()
												.getServerName());
										player.sendPluginMessage(PluginLoader.getInstance(), "BungeeCord", out.toByteArray());
									} else {
										// TODO Tell the player the server is
										// full.
									}
								}
							}

						} else {
							// TODO Tell the player he has insufficient
							// permissions
						}
					}
				}
			}
		}
	}

	@EventHandler
	public void onSignChange(SignChangeEvent event) {
		Sign sign = (Sign) event.getBlock().getState();

		if (event.getPlayer().hasPermission("kwbungee.signs.create")) {
			if (event.getLine(1).trim().equalsIgnoreCase("[kwbungee]")) {
				// TODO Create the Data for the Sign.
				String[] allMaps = GetMaps.getMapNames(PluginLoader
						.getInstance().getConfig());
				for (String map : allMaps) {
					if (event.getLine(2).trim().equalsIgnoreCase(map.trim())) {
						SignCreator.createNewSign(sign, map);
						SignCreator.resetSign(event);
					}
				}
			}
		}
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		if (event.getBlock().getState() instanceof Sign && !event.isCancelled()) {
			SignCreator.removeSign((Sign) event.getBlock().getState());
		}
	}

	@EventHandler
	public void onDisconnet(PlayerQuitEvent event) {
		if (SignData.getWaitingPlayers().containsKey(event.getPlayer())) {
			int i = SignData.getSignPlayerCount().get(
					SignData.getWaitingPlayers().get(event.getPlayer()));
			i--;
			Sign sign = SignData.getWaitingPlayers().get(event.getPlayer());
			SignData.getSignPlayerCount().remove(
					SignData.getWaitingPlayers().get(event.getPlayer()));
			SignData.getSignPlayerCount().put(sign, i);
			SignData.getWaitingPlayers().remove(event.getPlayer());
		}
	}
}
