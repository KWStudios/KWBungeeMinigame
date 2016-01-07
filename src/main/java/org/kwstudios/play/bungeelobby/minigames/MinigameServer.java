package org.kwstudios.play.bungeelobby.minigames;

import org.kwstudios.play.bungeelobby.json.MiniGameResponse;

public class MinigameServer {

	private MiniGameResponse miniGameResponse;
	private long lastMessageReceived;

	public MinigameServer(MiniGameResponse miniGameResponse, long lastMessageReceived) {
		this.miniGameResponse = miniGameResponse;
		this.lastMessageReceived = lastMessageReceived;
	}

	public MiniGameResponse getMiniGameResponse() {
		return miniGameResponse;
	}

	public void setMiniGameResponse(MiniGameResponse miniGameResponse) {
		this.miniGameResponse = miniGameResponse;
	}

	public long getLastMessageReceived() {
		return lastMessageReceived;
	}

	public void setLastMessageReceived(long lastMessageReceived) {
		this.lastMessageReceived = lastMessageReceived;
	}

}
