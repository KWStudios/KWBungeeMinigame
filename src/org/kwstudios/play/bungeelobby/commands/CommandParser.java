package org.kwstudios.play.bungeelobby.commands;

import net.minecraft.server.v1_8_R3.Vec3D;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;
import org.kwstudios.play.bungeelobby.loader.PluginLoader;

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

	public boolean isCommand() {
		return isCommand;
	}

}
