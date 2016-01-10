package org.kwstudios.play.kwbungeeminigame.json;

public class MiniGameResponse {

	private String server_name;
	private String game_type;
	private String map_name;
	private String action;

	private int current_players;

	public String getServerName() {
		return server_name;
	}

	public void setServerName(String serverName) {
		this.server_name = serverName;
	}

	public String getGameType() {
		return game_type;
	}

	public void setGameType(String gameType) {
		this.game_type = gameType;
	}

	public String getMapName() {
		return map_name;
	}

	public void setMapName(String mapName) {
		this.map_name = mapName;
	}

	public int getCurrentPlayers() {
		return current_players;
	}

	public void setCurrentPlayers(int currentPlayers) {
		this.current_players = currentPlayers;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

}
