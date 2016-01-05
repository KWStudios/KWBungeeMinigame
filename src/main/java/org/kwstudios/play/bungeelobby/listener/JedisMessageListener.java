package org.kwstudios.play.bungeelobby.listener;

import org.bukkit.Bukkit;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

public class JedisMessageListener {

	private JedisPubSub jedisPubSub;
	private String server;
	private String password;
	private String[] channel;

	public JedisMessageListener(String server, String password, String... channel) {
		this.server = server;
		this.password = password;
		this.channel = channel;
		jedisPubSub = setupSubscriber();
	}

	private JedisPubSub setupSubscriber() {
		final JedisPubSub jedisPubSub = new JedisPubSub() {
			@Override
			public void onUnsubscribe(String channel, int subscribedChannels) {
				Bukkit.getConsoleSender().sendMessage("Jedis unsubscribed successfully from the Redis Host!");
			}

			@Override
			public void onSubscribe(String channel, int subscribedChannels) {
				Bukkit.getConsoleSender().sendMessage("Jedis subscribed successfully at the Redis Host!");
			}

			@Override
			public void onPUnsubscribe(String pattern, int subscribedChannels) {
			}

			@Override
			public void onPSubscribe(String pattern, int subscribedChannels) {
			}

			@Override
			public void onPMessage(String pattern, String channel, String message) {
			}

			@Override
			public void onMessage(String channel, String message) {
				Bukkit.getConsoleSender().sendMessage("Jedis received a new message from the Redis Host!");
				Bukkit.getConsoleSender().sendMessage(channel);
				Bukkit.getConsoleSender().sendMessage(message);
			}
		};
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Bukkit.getConsoleSender().sendMessage("Jedis is connecting to the Redis Host!");
					Jedis jedis = new Jedis(server);
					jedis.auth(password);
					Bukkit.getConsoleSender().sendMessage("Jedis is subscribing for a channel at the Redis Host!");
					jedis.subscribe(jedisPubSub, channel);
					jedis.quit();
					// jedis.close();
				} catch (Exception e) {
					e.printStackTrace();
					// e.printStackTrace();
				}
			}
		}).start();
		return jedisPubSub;
	}

	public JedisPubSub getJedisPubSub() {
		return jedisPubSub;
	}

}
