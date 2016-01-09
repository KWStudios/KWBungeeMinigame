package org.kwstudios.play.bungeelobby.minigames;

import org.bukkit.block.Sign;
import org.kwstudios.play.bungeelobby.json.MiniGameResponse;

public class MinigameServer {

	private MiniGameResponse miniGameResponse;
	private Sign miniGameSign;
	private long lastMessageReceived;

	public MinigameServer(MiniGameResponse miniGameResponse, Sign miniGameSign, long lastMessageReceived) {
		this.miniGameResponse = miniGameResponse;
		this.miniGameSign = miniGameSign;
		this.lastMessageReceived = lastMessageReceived;
	}

	public MiniGameResponse getMiniGameResponse() {
		return miniGameResponse;
	}

	public Sign getMiniGameSign() {
		return miniGameSign;
	}

	public long getLastMessageReceived() {
		return lastMessageReceived;
	}

	public void setLastMessageReceived(long lastMessageReceived) {
		this.lastMessageReceived = lastMessageReceived;
	}

}
