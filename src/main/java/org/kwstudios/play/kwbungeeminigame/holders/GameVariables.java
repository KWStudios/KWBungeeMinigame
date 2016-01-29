package org.kwstudios.play.kwbungeeminigame.holders;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

public class GameVariables {
	public static final List<Player> playersWithTPOutLobby = new ArrayList<Player>();
	public static final List<Player> playersWithTPOutAndInLobby = new ArrayList<Player>();
	
	public static boolean isRunning = false;
}
