package com.fren_gor.packetInjectorAPI.impl;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import com.fren_gor.packetInjectorAPI.PacketInjectorPlugin;
import com.fren_gor.packetInjectorAPI.ReflectionUtil;
import com.fren_gor.packetInjectorAPI.api.PacketHandler;
import com.fren_gor.packetInjectorAPI.api.PacketInjector;

import io.netty.channel.Channel;

public class PacketInjector_v1_8 implements PacketInjector {

	private Field EntityPlayer_playerConnection;
	private Class<?> PlayerConnection;
	private Field PlayerConnection_networkManager;
	private Method CraftPlayer_getHandle;

	private Class<?> NetworkManager;
	private Field k;

	public PacketInjector_v1_8() {
		try {
			CraftPlayer_getHandle = ReflectionUtil.getCBClass("entity.CraftPlayer").getDeclaredMethod("getHandle");
			EntityPlayer_playerConnection = ReflectionUtil.getNMSClass("EntityPlayer")
					.getDeclaredField("playerConnection");
			EntityPlayer_playerConnection.setAccessible(true);
			PlayerConnection = ReflectionUtil.getNMSClass("PlayerConnection");
			PlayerConnection_networkManager = PlayerConnection.getDeclaredField("networkManager");
			PlayerConnection_networkManager.setAccessible(true);

			NetworkManager = ReflectionUtil.getNMSClass("NetworkManager");
			String s = "";
			switch (ReflectionUtil.getCompleteVersion()) {
				case "v1_8_R1":
					s = "i";
					break;
				case "v1_8_R2":
					s = "k";
					break;

				default:
					s = "channel";
					break;
			}
			k = NetworkManager.getDeclaredField(s);
			k.setAccessible(true);

		} catch (Exception t) {
			t.printStackTrace();
		}
	}

	@Override
	public PacketHandler addPlayer(Player p) {
		try {
			Channel ch = getChannel(getNetworkManager(CraftPlayer_getHandle.invoke(p)));
			if (ch.pipeline().get(PacketInjectorPlugin.CHANNEL_HANDLER_NAME) == null) {
				PacketHandler_v1_8 h = new PacketHandler_v1_8(p);
				ch.pipeline().addBefore("packet_handler", PacketInjectorPlugin.CHANNEL_HANDLER_NAME, h);
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
			Channel ch = getChannel(getNetworkManager(CraftPlayer_getHandle.invoke(p)));
			if (ch.pipeline().get(PacketInjectorPlugin.CHANNEL_HANDLER_NAME) != null) {
				ch.pipeline().remove(PacketInjectorPlugin.CHANNEL_HANDLER_NAME);
			}
		} catch (Exception t) {
			t.printStackTrace();
		}
	}

	@Nullable
	@Override
	public PacketHandler getHandler(Player p) {
		try {
			Channel ch = getChannel(getNetworkManager(CraftPlayer_getHandle.invoke(p)));
			return (PacketHandler) ch.pipeline().get(PacketInjectorPlugin.CHANNEL_HANDLER_NAME);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private Object getNetworkManager(Object ep) throws Exception {
		return PlayerConnection_networkManager.get(EntityPlayer_playerConnection.get(ep));
	}

	private Channel getChannel(Object networkManager) throws Exception {
		return (Channel) k.get(networkManager);
	}

	@Override
	public void sendPacket(Player p, Object packet) throws Exception {
		getChannel(getNetworkManager(CraftPlayer_getHandle.invoke(p))).pipeline().writeAndFlush(packet);
	}

	@Override
	public void sendPacketToServer(Player p, Object packet) throws Exception {
		getChannel(getNetworkManager(CraftPlayer_getHandle.invoke(p))).pipeline().fireChannelRead(packet);
	}
}
