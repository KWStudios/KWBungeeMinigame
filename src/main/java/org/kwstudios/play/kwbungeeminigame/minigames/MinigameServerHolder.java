package org.kwstudios.play.kwbungeeminigame.minigames;

import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.kwstudios.play.kwbungeeminigame.json.MiniGameResponse;
import org.kwstudios.play.kwbungeeminigame.loader.PluginLoader;
import org.kwstudios.play.kwbungeeminigame.signs.SignCreator;
import org.kwstudios.play.kwbungeeminigame.signs.SignData;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.google.gson.Gson;

public class MinigameServerHolder {

	private String channel;
	private HashMap<String, MinigameServer> connectedServers = new HashMap<String, MinigameServer>();

	public MinigameServerHolder(String channel) {
		this.channel = channel;
	}

	public synchronized void parseMessage(String message) {
		Gson gson = new Gson();
		MiniGameResponse miniGameResponse = gson.fromJson(message,
				MiniGameResponse.class);

		if (connectedServers.containsKey(miniGameResponse.getServerName())) {
			// TODO Handle the Sign update and other stuff or remove this server
			// if it wants to
			if (MinigameAction.fromString(miniGameResponse.getAction()) == MinigameAction.REMOVE) {
				connectedServers.remove(miniGameResponse.getServerName());
				// TODO Create method to reset the sign
				SignCreator.removeSign(connectedServers.get(
						miniGameResponse.getServerName()).getMiniGameSign());
			} else if (MinigameAction.fromString(miniGameResponse.getAction()) == MinigameAction.UPDATE) {
				SignCreator.updateSign(
						connectedServers.get(miniGameResponse.getServerName())
								.getMiniGameSign(), miniGameResponse
								.getCurrentPlayers());
			}
		} else {
			// TODO Create new MiniGameServer if there is a need for one (Check
			// the HashMap with signs which requested a server with this
			// minigame type)
			if (MinigameAction.fromString(miniGameResponse.getAction()) == MinigameAction.CREATE) {
				Sign miniGameSign = MinigameRequests
						.getQueuedSignForType(MinigameType
								.fromString(miniGameResponse.getGameType()));
				if (miniGameSign != null) {
					MinigameServer server = new MinigameServer(
							miniGameResponse, miniGameSign,
							System.currentTimeMillis());
					for (Entry<Player, Sign> player : SignData
							.getWaitingPlayers().entrySet()) {
						if (player.getValue().equals(miniGameSign)) {
							ByteArrayDataOutput out = ByteStreams
									.newDataOutput();
							out.writeUTF("Connect");
							out.writeUTF(server.getMiniGameResponse()
									.getServerName());
							player.getKey().sendPluginMessage(
									PluginLoader.getInstance(), "BungeeCord",
									out.toByteArray());
						}
					}
					MinigameRequests.removeQueuedRequest(miniGameSign);
					connectedServers.put(miniGameResponse.getServerName(),
							server);

				}
			}
		}
	}

	public boolean hasSignActiveServer(Sign sign) {
		for (Entry<String, MinigameServer> server : connectedServers.entrySet()) {
			if (server.getValue().getMiniGameSign().equals(sign)) {
				return true;
			}
		}
		return false;
	}

	public MinigameServer getActiveServerForSign(Sign sign) {
		for (Entry<String, MinigameServer> server : connectedServers.entrySet()) {
			if (server.getValue().getMiniGameSign().equals(sign)) {
				return server.getValue();
			}
		}
		return null;
	}

	public String getChannel() {
		return channel;
	}

}
