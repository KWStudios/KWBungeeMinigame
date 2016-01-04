package org.kwstudios.play.bungeelobby.listener;

import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.kwstudios.play.bungeelobby.loader.PluginLoader;

public class MessageListener implements PluginMessageListener {

	public MessageListener() {
		PluginLoader.getInstance().getServer().getMessenger().registerIncomingPluginChannel(PluginLoader.getInstance(),
				"kwLobby", this);
	}

	@Override
	public void onPluginMessageReceived(String channel, Player player, byte[] message) {

	}

}
