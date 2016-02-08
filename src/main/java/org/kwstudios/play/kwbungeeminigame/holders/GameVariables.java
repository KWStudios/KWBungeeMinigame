package org.kwstudios.play.kwbungeeminigame.holders;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.entity.Player;

public class GameVariables {
	public static final List<Player> playersWithTPOutLobby = new ArrayList<Player>();
	public static final List<Player> playersWithTPOutAndInLobby = new ArrayList<Player>();

	public static boolean isRunning = false;

	public static final Set<Player> queuedFriendsRequests = new HashSet<Player>();
}
