package org.kwstudios.play.kwbungeeminigame.listener;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.kwstudios.play.kwbungeeminigame.enums.BungeeMessageAction;
import org.kwstudios.play.kwbungeeminigame.holders.GameVariables;
import org.kwstudios.play.kwbungeeminigame.json.BungeeRequest;
import org.kwstudios.play.kwbungeeminigame.json.FriendsRequest;
import org.kwstudios.play.kwbungeeminigame.loader.PluginLoader;
import org.kwstudios.play.kwbungeeminigame.minigame.MinigameSpectator;
import org.kwstudios.play.kwbungeeminigame.toolbox.ConstantHolder;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import net.md_5.bungee.api.ChatColor;

public class KWChannelMessageListener implements PluginMessageListener {

	public KWChannelMessageListener() {
		PluginLoader.getInstance().getServer().getMessenger().registerOutgoingPluginChannel(PluginLoader.getInstance(),
				ConstantHolder.KW_CHANNEL_NAME);
		PluginLoader.getInstance().getServer().getMessenger().registerIncomingPluginChannel(PluginLoader.getInstance(),
				ConstantHolder.KW_CHANNEL_NAME, this);
	}

	@Override
	public void onPluginMessageReceived(String channel, Player player, byte[] message) {
		if (!channel.equals(ConstantHolder.KW_CHANNEL_NAME)) {
			return;
		}
		ByteArrayInputStream stream = new ByteArrayInputStream(message);
		DataInputStream input = new DataInputStream(stream);

		try {
			parseMessage(input.readUTF());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void parseMessage(String message) {
		Gson gson = new Gson();
		BungeeRequest response = null;
		try {
			response = gson.fromJson(message, BungeeRequest.class);
		} catch (JsonSyntaxException e) {
			throw new JsonSyntaxException("The message received was corrupt. It should be JSON Syntax");
		}
		if (response == null) {
			return;
		}
		if (response.isRequest()) {
			return;
		}

		for (BungeeMessageAction action : response.getActions()) {
			if (action == BungeeMessageAction.FRIENDS) {
				FriendsRequest friendsResponse = response.getFriendsRequest();

				Player player = Bukkit.getPlayerExact(friendsResponse.getPlayer());
				if (player == null) {
					return;
				}
				if (!player.isOnline()) {
					return;
				}
				if (!GameVariables.queuedFriendsRequests.contains(player)) {
					return;
				}
				GameVariables.queuedFriendsRequests.remove(player);

				if (friendsResponse.getFriends() == null || friendsResponse.getFriends().length == 0) {
					// TODO Fancy message which should explain the situation as
					// simple as possible
					player.sendMessage(ChatColor.RED + "You should not be here, sorry...");
					ByteArrayDataOutput out = ByteStreams.newDataOutput();
					out.writeUTF("Connect");
					out.writeUTF("lobby");
					player.sendPluginMessage(PluginLoader.getInstance(), "BungeeCord", out.toByteArray());
				} else {
					for (String friendsName : friendsResponse.getFriends()) {
						Player friend = Bukkit.getPlayerExact(friendsName);
						if (friend == null) {
							continue;
						}
						if (friend.isOnline()) {
							MinigameSpectator.setSpectatorAndTeleport(player, friend.getLocation());
							return;
						}
					}
				}
			}
		}
	}

	public static void sendMessage(String message, Player player) {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(stream);
		try {
			out.writeUTF(message);
		} catch (IOException e) {
			e.printStackTrace();
		}

		player.sendPluginMessage(PluginLoader.getInstance(), ConstantHolder.KW_CHANNEL_NAME, stream.toByteArray());
	}

}
