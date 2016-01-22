package org.kwstudios.play.kwbungeeminigame.toolbox;

import org.bukkit.ChatColor;

public class ConstantHolder {

	public static final String MOTD_PREFIX = ChatColor.DARK_PURPLE.toString() + ChatColor.BOLD.toString() + "KWStudios"
			+ ChatColor.RESET + ":" + " " + ChatColor.GOLD + ChatColor.ITALIC;
	
	public static final String KWSTUDIOS_PREFIX = ChatColor.AQUA.toString() + ChatColor.BOLD.toString() + "["
			+ ChatColor.YELLOW.toString() + ChatColor.BOLD.toString() + "KWStudios" + ChatColor.AQUA.toString()
			+ ChatColor.BOLD.toString() + "] " + ChatColor.RESET;
	
	public static final String API_URL = "https://api.kwstudios.org/minecraft/server/:server/players/:player/storedata";
	
	public static final String JEDIS_SERVER = "46.101.230.181";
	
	public static final String TELEPORT_METADATA_KEY = "KWStudios_Teleport";

}
