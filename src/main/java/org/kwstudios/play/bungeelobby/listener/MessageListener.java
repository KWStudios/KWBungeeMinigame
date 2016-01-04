package org.kwstudios.play.bungeelobby.listener;

import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.kwstudios.play.bungeelobby.loader.PluginLoader;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;

public class MessageListener implements PluginMessageListener {

	public MessageListener() {
		PluginLoader.getInstance().getServer().getMessenger().registerIncomingPluginChannel(PluginLoader.getInstance(),
				"kwLobby", this);
	}

	@Override
	public void onPluginMessageReceived(String channel, Player player, byte[] message) {
		if (channel.equals("kwLobby")){
			ByteArrayDataInput in = ByteStreams.newDataInput(message);
			String subchannel = in.readUTF();
			if(subchannel.equals("signupdate")){
				
			}
		}
	}

}
