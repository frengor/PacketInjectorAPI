package com.fren_gor.packetUtils.v1_7;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import javax.annotation.Nullable;

import org.bukkit.entity.Player;

import com.fren_gor.packetUtils.PacketHandler;
import com.fren_gor.packetUtils.PacketInjector;
import com.fren_gor.packetUtils.PacketInjectorPlugin;
import com.fren_gor.packetUtils.ReflectionUtil;

import net.minecraft.util.io.netty.channel.Channel;
import net.minecraft.util.io.netty.channel.ChannelHandlerContext;

public class PacketInjector_v1_7 implements PacketInjector {

	private Field EntityPlayer_playerConnection;
	private Class<?> PlayerConnection;
	private Field PlayerConnection_networkManager;
	private Method CraftPlayer_getHandle;

	private Class<?> NetworkManager;
	private Field k;

	public PacketInjector_v1_7() {
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
				case "v1_7_R2":
				case "v1_7_R3":
				case "v1_7_R4":
					s = "m";
					break;
				case "v1_7_R1":
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
				PacketHandler_v1_7 h = new PacketHandler_v1_7(p);
				ch.pipeline().addBefore(PacketInjectorPlugin.CHANNEL_HANDLER_NAME,
						PacketInjectorPlugin.CHANNEL_HANDLER_NAME, h);
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

	@Override
	@Nullable
	public PacketHandler getHandler(Player p) {
		try {
			Channel ch = getChannel(getNetworkManager(CraftPlayer_getHandle.invoke(p)));
			return (PacketHandler) ch.pipeline().get(PacketInjectorPlugin.CHANNEL_HANDLER_NAME);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public Object getNetworkManager(Object ep) throws Exception {
		return PlayerConnection_networkManager.get(EntityPlayer_playerConnection.get(ep));
	}

	private Channel getChannel(Object networkManager) throws Exception {
		return (Channel) k.get(networkManager);
	}

	public ChannelHandlerContext getChannelhandler(Player p) {

		return ((PacketHandler_v1_7) getHandler(p)).getChannelHandlerContext();

	}

	@Override
	public Class<?> getNetworkManager() {
		return NetworkManager;
	}
}
