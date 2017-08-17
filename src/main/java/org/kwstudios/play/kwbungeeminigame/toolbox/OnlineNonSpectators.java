package org.kwstudios.play.kwbungeeminigame.toolbox;

import java.util.ArrayList;
import java.util.Iterator;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

public class OnlineNonSpectators {
	public static ArrayList<? extends Player> get() {
		ArrayList<? extends Player> pls = new ArrayList<>(Bukkit.getOnlinePlayers());
		Iterator<? extends Player> it = pls.iterator();
		while(true) {
			if(it.hasNext()) {
				if(it.next().getGameMode().equals(GameMode.SPECTATOR)) {
					it.remove();	
				}
			} else {
				break;
			}
		}
		return pls;
	}
}
