package org.kwstudios.play.kwbungeeminigame.loader;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;

public final class EventListener implements Listener {

	private FileConfiguration fileConfiguration;

	public EventListener(PluginLoader plugin,
			FileConfiguration fileConfiguration) {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		this.fileConfiguration = fileConfiguration;
	}

}
