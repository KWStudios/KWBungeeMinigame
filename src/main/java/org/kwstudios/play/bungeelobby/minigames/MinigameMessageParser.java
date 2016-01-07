package org.kwstudios.play.bungeelobby.minigames;

import java.util.HashMap;

import org.kwstudios.play.bungeelobby.json.MiniGameResponse;

import com.google.gson.Gson;

public class MinigameMessageParser {

	private HashMap<String, MinigameServer> connectedServers = new HashMap<String, MinigameServer>();

	public MinigameMessageParser(String channel) {

	}

	public void parseMessage(String message) {
		Gson gson = new Gson();
		MiniGameResponse miniGameResponse = gson.fromJson(message, MiniGameResponse.class);

		if (connectedServers.containsKey(miniGameResponse.getServerName())) {
			// TODO Handle the Sign update and other stuff or remove this server
			// if it wants to
		} else {
			// TODO Create new MiniGameServer if there is a need for one (Check
			// the HashMap with signs which requested a server with this
			// minigame type)
		}
	}

}
