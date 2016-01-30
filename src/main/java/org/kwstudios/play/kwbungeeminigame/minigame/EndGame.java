package org.kwstudios.play.kwbungeeminigame.minigame;

import org.apache.commons.lang3.text.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.kwstudios.play.kwbungeeminigame.loader.PluginLoader;
import org.kwstudios.play.kwbungeeminigame.toolbox.ConstantHolder;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

public class EndGame {

	public static void stopFinishedGame() {
		for (Player player : Bukkit.getOnlinePlayers()) {
			player.sendMessage(ConstantHolder.KWSTUDIOS_PREFIX + "The "
					+ WordUtils.capitalizeFully(PluginLoader.getGamevalues().getGame_type())
					+ " Server will be shut down in 10 seconds!");
		}

		Bukkit.getServer().getScheduler().runTaskLater(PluginLoader.getInstance(), new Runnable() {
			@Override
			public void run() {
				ByteArrayDataOutput out = ByteStreams.newDataOutput();
				out.writeUTF("Connect");
				out.writeUTF("lobby");
				for (Player player : Bukkit.getOnlinePlayers()) {
					player.sendPluginMessage(PluginLoader.getInstance(), "BungeeCord", out.toByteArray());
				}

				Bukkit.getServer().getScheduler().runTaskLater(PluginLoader.getInstance(), new Runnable() {
					public void run() {
						Bukkit.getServer().shutdown();
					}
				}, 20);
			}
		}, 200);
	}

}
