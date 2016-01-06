package org.kwstudios.play.bungeelobby.sender;

import org.bukkit.Bukkit;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Protocol;

public class JedisMessageSender {

	public static void sendMessageToChannel(String server, String password, String channel, String message) {
		JedisMessageSender.sendMessageToChannel(server, Protocol.DEFAULT_PORT, password, channel, message);
	}

	public static void sendMessageToChannel(String server, String channel, String message) {
		JedisMessageSender.sendMessageToChannel(server, Protocol.DEFAULT_PORT, null, channel, message);
	}

	public static void sendMessageToChannel(String server, int port, String channel, String message) {
		JedisMessageSender.sendMessageToChannel(server, port, null, channel, message);
	}

	public static void sendMessageToChannel(final String server, final int port, final String password,
			final String channel, final String message) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Bukkit.getConsoleSender().sendMessage("Jedis is connecting to the Redis Host!");
					Jedis jedis = new Jedis(server, port);
					if (password != null) {
						jedis.auth(password);
					}
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
