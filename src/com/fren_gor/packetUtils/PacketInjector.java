package com.fren_gor.packetUtils;

import java.lang.reflect.Field;

import org.bukkit.entity.Player;

import io.netty.channel.Channel;

public class PacketInjector {

	private Field EntityPlayer_playerConnection;
	private Class<?> PlayerConnection;
	private Field PlayerConnection_networkManager;

	private Class<?> NetworkManager;
	private Field k;

	public PacketInjector() {
		try {
			EntityPlayer_playerConnection = Reflection.getField(Reflection.getClass("{nms}.EntityPlayer"),
					"playerConnection");

			PlayerConnection = Reflection.getClass("{nms}.PlayerConnection");
			PlayerConnection_networkManager = Reflection.getField(PlayerConnection, "networkManager");

			NetworkManager = Reflection.getClass("{nms}.NetworkManager");
			String s = "";
			switch (ReflectionUtil.getVersion()) {
			case "1_7_R2":
			case "1_7_R3":
			case "1_7_R4":
				s = "m";
				break;
			case "1_8_R1":
				s = "i";
				break;
			case "1_7_R1":
			case "1_8_R2":
				s = "k";
				break;

			default:
				s = "channel";
				break;
			}
			k = Reflection.getField(NetworkManager, s);

		} catch (Exception t) {
			t.printStackTrace();
		}
	}

	public PacketHandler addPlayer(Player p) {
		try {
			Channel ch = getChannel(getNetworkManager(Reflection.getNmsPlayer(p)));
			if (ch.pipeline().get("PacketInjector") == null) {
				PacketHandler h = new PacketHandler(p);
				ch.pipeline().addBefore("packet_handler", "PacketInjector", h);
				return h;
			}
		} catch (Exception t) {
			t.printStackTrace();
		}
		return null;
	}

	public void removePlayer(Player p) {
		try {
			Channel ch = getChannel(getNetworkManager(Reflection.getNmsPlayer(p)));
			if (ch.pipeline().get("PacketInjector") != null) {
				ch.pipeline().remove("PacketInjector");
			}
		} catch (Exception t) {
			t.printStackTrace();
		}
	}

	private Object getNetworkManager(Object ep) throws Exception {
		return Reflection.getFieldValue1(PlayerConnection_networkManager,
				Reflection.getFieldValue1(EntityPlayer_playerConnection, ep));
	}

	private Channel getChannel(Object networkManager) {
		Channel ch = null;
		try {
			ch = Reflection.getFieldValue1(k, networkManager);
		} catch (Exception t) {
			t.printStackTrace();
		}
		return ch;
	}
}
