package org.kwstudios.play.bungeelobby.sender;

import org.bukkit.Bukkit;

import redis.clients.jedis.Jedis;

public class JedisMessageSender {

	public static void sendMessageToChannel(final String server, final String password, final String channel, final String message) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Bukkit.getConsoleSender().sendMessage("Jedis is connecting to the Redis Host!");
					Jedis jedis = new Jedis(server);
					jedis.auth(password);
					Bukkit.getConsoleSender().sendMessage("Jedis is publishing the given Message to the Redis Host!");
					jedis.publish(channel, message);
					jedis.quit();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

}
