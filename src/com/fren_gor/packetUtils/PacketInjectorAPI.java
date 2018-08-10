package com.fren_gor.packetUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.bukkit.entity.Player;

import com.fren_gor.packetUtils.v1_7.PacketHandler_v1_7;
import com.fren_gor.packetUtils.v1_7.PacketInjector_v1_7;
import com.fren_gor.packetUtils.v1_8.PacketHandler_v1_8;
import com.fren_gor.packetUtils.v1_8.PacketInjector_v1_8;

import io.netty.util.concurrent.GenericFutureListener;

/**
 * Util class by fren_gor Give me credits if you use it in one of your plugin
 * 
 * @author fren_gor
 *
 */
public class PacketInjectorAPI {

	/**
	 * Send packet to a player
	 * 
	 * @param player
	 *            The packet will be sent to this player
	 * @param packet
	 *            The NMS packet
	 */
	public static void sendPacketToClient(Player player, Object packet) {

		Object crp = ReflectionUtil.cast(player, ReflectionUtil.getCBClass("entity.CraftPlayer"));

		Object ep = ReflectionUtil.invoke(crp, "getHandle");
		Object playerConnection = ReflectionUtil.getField(ep, "playerConnection");

		try {
			playerConnection.getClass().getMethod("sendPacket", ReflectionUtil.getNMSClass("Packet"))
					.invoke(playerConnection, packet);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
				| SecurityException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Send packet to a player
	 * 
	 * @param player
	 *            The packet will be sent to this player
	 * @param packet
	 *            The NMS packet
	 * @deprecated Use
	 *             {@link PacketInjectorAPI#sendPacketToClient(Player, Object)}
	 *             instead
	 */
	@Deprecated
	public static void sendPacket(Player player, Object packet) {

		Object crp = ReflectionUtil.cast(player, ReflectionUtil.getCBClass("entity.CraftPlayer"));

		Object ep = ReflectionUtil.invoke(crp, "getHandle");
		Object playerConnection = ReflectionUtil.getField(ep, "playerConnection");

		try {
			playerConnection.getClass().getMethod("sendPacket", ReflectionUtil.getNMSClass("Packet"))
					.invoke(playerConnection, packet);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
				| SecurityException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Send packet to the server
	 * 
	 * @param player
	 *            The player that will send the packet
	 * @param packet
	 *            The NMS packet
	 */
	public static void sendPacketToServer(Player player, Object packet) {

		PacketHandler ph = Main.getInstance().getPacketInjector().getHandler(player);

		if (ReflectionUtil.versionIs1_7()) {
			try {
				((PacketHandler_v1_7) ph).channelRead(
						((PacketInjector_v1_7) Main.getInstance().getPacketInjector()).getChannelhandler(player), packet);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {

			try {
				((PacketHandler_v1_8) ph).channelRead(
						((PacketInjector_v1_8) Main.getInstance().getPacketInjector()).getChannelhandler(player),
						packet);
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

}
