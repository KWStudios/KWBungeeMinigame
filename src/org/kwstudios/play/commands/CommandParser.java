package org.kwstudios.play.commands;

import net.minecraft.server.v1_8_R3.Vec3D;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.kwstudios.play.api.ApiInitializer;
import org.kwstudios.play.loader.PluginLoader;

public class CommandParser {

	private Player player;
	private Command command;
	private String label;
	private String[] args;
	private FileConfiguration fileConfiguration;

	private boolean isCommand = false;

	public CommandParser(Player player, Command command, String label, String[] args,
			FileConfiguration fileConfiguration) {
		this.player = player;
		this.command = command;
		this.label = label;
		this.args = args;
		this.fileConfiguration = fileConfiguration;
		checkCommand();
	}

	private void checkCommand() {
		switch (label.toLowerCase()) {
		case "hallofreunde:D":
			player.setVelocity(Vector.getRandom());
			break;

		default:
			isCommand = false;
			break;
		}
	}

	private void onLobbyCommandIssued() {
		player.setMetadata("lobbyCommandTriggered", new FixedMetadataValue(PluginLoader.getInstance(), true));
		LobbyTeleporter lobbyTeleporter = new LobbyTeleporter(player, fileConfiguration);
		if (!lobbyTeleporter.isLobbySet()) {
			player.sendMessage(ChatColor.BLUE + "The lobby was not set yet! Set the lobby with " + ChatColor.DARK_RED
					+ "/setlobby");
		} else {
			lobbyTeleporter.teleportToLobby();
			player.sendMessage(ChatColor.GREEN + ChatColor.BOLD.toString() + "-------Welcome to the Lobby-------");
		}
	}

	public boolean isCommand() {
		return isCommand;
	}

	private class Initializer implements Runnable {

		@Override
		public void run() {
			new ApiInitializer();
		}

	}

}
