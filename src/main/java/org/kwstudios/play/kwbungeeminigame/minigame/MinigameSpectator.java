package org.kwstudios.play.kwbungeeminigame.minigame;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.kwstudios.play.kwbungeeminigame.loader.PluginLoader;
import org.kwstudios.play.kwbungeeminigame.toolbox.ConstantHolder;

public class MinigameSpectator {

	public static void setSpectatorAndTeleport(Player player, Location location) {
		player.setMetadata(ConstantHolder.TELEPORT_METADATA_KEY,
				new FixedMetadataValue(PluginLoader.getInstance(), true));
		player.setGameMode(GameMode.SPECTATOR);
		player.teleport(location);
	}

}
