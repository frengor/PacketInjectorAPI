package com.fren_gor.packetUtils.v1_8;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.annotation.Nullable;

import org.bukkit.entity.Player;

import com.fren_gor.packetUtils.PacketHandler;
import com.fren_gor.packetUtils.PacketInjector;
import com.fren_gor.packetUtils.Reflection;
import com.fren_gor.packetUtils.ReflectionUtil;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.DefaultChannelPipeline;

public class PacketInjector_v1_8 implements PacketInjector {

	private Field EntityPlayer_playerConnection;
	private Class<?> PlayerConnection;
	private Field PlayerConnection_networkManager;

	private Class<?> NetworkManager;
	private Field k;

	public PacketInjector_v1_8() {
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

	@Override
	public PacketHandler addPlayer(Player p) {
		try {
			Channel ch = getChannel(getNetworkManager(Reflection.getNmsPlayer(p)));
			if (ch.pipeline().get("PacketInjector") == null) {
				PacketHandler_v1_8 h = new PacketHandler_v1_8(p);
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

	private static Method newContext;

	static {

		for (Method m : DefaultChannelPipeline.class.getDeclaredMethods()) {

			if (m.getName().equals("newContext")) {
				newContext = m;
				m.setAccessible(true);
				break;
			}

		}

	}

	public ChannelHandlerContext getChannelhandler(Player p) {

		return ((PacketHandler_v1_8)getHandler(p)).getChannelHandlerContext();
	}

	@Override
	public Class<?> getNetworkManager() {
		return NetworkManager;
	}
}
