package org.kwstudios.play.kwbungeeminigame.toolbox;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class RandomPlayerGetter {
	public static Player getRandomOnlinePlayer() {
		Random rand = new Random();
		int i = rand.nextInt(Bukkit.getOnlinePlayers().size());
		Player[] players = {};
		players = Bukkit.getOnlinePlayers().toArray(players);
		return players[i];
	}
}
