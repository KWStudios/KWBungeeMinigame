package org.kwstudios.play.kwbungeelobby.loader;

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
import org.kwstudios.play.kwbungeelobby.minigames.GetMaps;
import org.kwstudios.play.kwbungeelobby.signs.SignCreator;

public final class EventListener implements Listener {

	private FileConfiguration fileConfiguration;

	public EventListener(PluginLoader plugin, FileConfiguration fileConfiguration) {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		this.fileConfiguration = fileConfiguration;
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onRightClick(PlayerInteractEvent event) {
		if (event.getClickedBlock() != null && event.getClickedBlock().getState() != null) {
			if (event.getClickedBlock().getState() instanceof Sign) {
				if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
					Sign sign = (Sign) event.getClickedBlock().getState();
					if (SignCreator.isJoinSign(sign)) {
						Player player = event.getPlayer();
						if (player.hasPermission("kwbungee.sign.use")) {
							// TODO Move the layer to the server and do stuff...
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
				String[] allMaps = GetMaps.getMapNames(PluginLoader.getInstance().getConfig());
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
}
