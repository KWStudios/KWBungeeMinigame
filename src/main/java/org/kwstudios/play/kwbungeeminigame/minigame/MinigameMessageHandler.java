package org.kwstudios.play.kwbungeeminigame.minigame;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;
import org.kwstudios.play.kwbungeeminigame.enums.MinigameAction;
import org.kwstudios.play.kwbungeeminigame.json.LobbyResponse;
import org.kwstudios.play.kwbungeeminigame.loader.PluginLoader;
import org.kwstudios.play.kwbungeeminigame.sender.JedisMessageSender;

import com.google.gson.Gson;

public class MinigameMessageHandler {

	public static Thread sendRemoveMessage() {
		final int players = Bukkit.getOnlinePlayers().size();

		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				Gson gson = new Gson();
				LobbyResponse response = new LobbyResponse();
				response.setAction(MinigameAction.REMOVE.getText());
				response.setCurrentPlayers(players);
				response.setGameType(PluginLoader.getGamevalues().getGame_type());
				response.setMapName(PluginLoader.getGamevalues().getMap_name());
				response.setServerName(PluginLoader.getGamevalues().getServer_name());
				String message = gson.toJson(response);
				Thread innerThread = JedisMessageSender.sendMessageToChannel(PluginLoader.getJedisValues().getHost(),
						PluginLoader.getJedisValues().getPort(), PluginLoader.getJedisValues().getPassword(),
						PluginLoader.getJedisValues().getChannelToSend(), message);
				try {
					innerThread.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
		thread.start();
		return thread;
	}

	public static BukkitTask sendUpdateMessage(final int currentPlayers) {
		return Bukkit.getServer().getScheduler().runTaskAsynchronously(PluginLoader.getInstance(), new Runnable() {
			@Override
			public void run() {
				Gson gson = new Gson();
				LobbyResponse response = new LobbyResponse();
				response.setAction(MinigameAction.UPDATE.getText());
				response.setCurrentPlayers(currentPlayers);
				response.setGameType(PluginLoader.getGamevalues().getGame_type());
				response.setMapName(PluginLoader.getGamevalues().getMap_name());
				response.setServerName(PluginLoader.getGamevalues().getServer_name());
				String message = gson.toJson(response);
				JedisMessageSender.sendMessageToChannel(PluginLoader.getJedisValues().getHost(),
						PluginLoader.getJedisValues().getPort(), PluginLoader.getJedisValues().getPassword(),
						PluginLoader.getJedisValues().getChannelToSend(), message);
			}
		});
	}

}
