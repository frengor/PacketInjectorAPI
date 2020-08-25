package com.fren_gor.packetUtils.v1_14;

import java.lang.reflect.Field;

import javax.annotation.Nullable;

import org.bukkit.entity.Player;

import com.fren_gor.packetUtils.PacketHandler;
import com.fren_gor.packetUtils.PacketInjector;
import com.fren_gor.packetUtils.Reflection;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

public class PacketInjector_v1_14 implements PacketInjector {

	private Field EntityPlayer_playerConnection;
	private Class<?> PlayerConnection;
	private Field PlayerConnection_networkManager;

	private Class<?> NetworkManager;
	private Field k;

	public PacketInjector_v1_14() {
		try {
			EntityPlayer_playerConnection = Reflection.getField(Reflection.getClass("{nms}.EntityPlayer"),
					"playerConnection");

			PlayerConnection = Reflection.getClass("{nms}.PlayerConnection");
			PlayerConnection_networkManager = Reflection.getField(PlayerConnection, "networkManager");

			NetworkManager = Reflection.getClass("{nms}.NetworkManager");
			k = Reflection.getField(NetworkManager, "channel");

		} catch (Exception t) {
			t.printStackTrace();
		}
	}

	@Override
	public PacketHandler addPlayer(Player p) {
		try {
			Channel ch = getChannel(getNetworkManager(Reflection.getNmsPlayer(p)));
			if (ch.pipeline().get("PacketInjector") == null) {
				PacketHandler_v1_14 h = new PacketHandler_v1_14(p);
				ch.pipeline().addBefore("packet_handler", "PacketInjector", h);
				return h;
			}
		} catch (Exception t) {
			t.printStackTrace();
		}
		return null;
	}

	@Override
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

	@Nullable
	@Override
	public PacketHandler getHandler(Player p) {
		Channel ch = null;
		try {
			ch = getChannel(getNetworkManager(Reflection.getNmsPlayer(p)));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		return (PacketHandler) ch.pipeline().get("PacketInjector");
	}

	@Override
	public Object getNetworkManager(Object ep) throws Exception {
		return Reflection.getFieldValue1(PlayerConnection_networkManager,
				Reflection.getFieldValue1(EntityPlayer_playerConnection, ep));
	}

	public Channel getChannel(Object networkManager) {
		Channel ch = null;
		try {
			ch = Reflection.getFieldValue1(k, networkManager);
		} catch (Exception t) {
			t.printStackTrace();
		}
		return ch;
	}

	public ChannelHandlerContext getChannelhandler(Player p) {

		return ((PacketHandler_v1_14) getHandler(p)).getChannelHandlerContext();
	}

	@Override
	public Class<?> getNetworkManager() {
		return NetworkManager;
	}
}
