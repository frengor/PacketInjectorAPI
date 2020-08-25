package com.fren_gor.packetUtils;

import java.lang.reflect.InvocationTargetException;

import org.bukkit.entity.Player;

import com.fren_gor.packetUtils.v1_14.PacketInjector_v1_14;
import com.fren_gor.packetUtils.v1_7.PacketInjector_v1_7;
import com.fren_gor.packetUtils.v1_8.PacketInjector_v1_8;

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

		if (!packet.getClass().getSimpleName().startsWith("PacketPlayOut")) {
			throw new IllegalArgumentException(packet.getClass().getName() + " is not a valid packet.");
		}

		try {
			if (ReflectionUtil.versionIs1_7()) {
				((PacketInjector_v1_7) PacketInjectorPlugin.getInstance().getPacketInjector()).getChannelhandler(player)
						.writeAndFlush(packet);
			} else if (ReflectionUtil.versionIsAtLeast1_14()) {
				((PacketInjector_v1_14) PacketInjectorPlugin.getInstance().getPacketInjector()).getChannelhandler(player)
						.writeAndFlush(packet);
			} else {
				((PacketInjector_v1_8) PacketInjectorPlugin.getInstance().getPacketInjector()).getChannelhandler(player)
						.writeAndFlush(packet);
			}
		} catch (Exception e) {
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
	 * @deprecated Use {@link PacketInjectorAPI#sendPacketToClient(Player, Object)} instead
	 */
	@Deprecated
	public static void sendPacket(Player player, Object packet) {

		if (!packet.getClass().getSimpleName().startsWith("PacketPlayOut")) {
			throw new IllegalArgumentException(packet.getClass().getName() + " is not a valid packet.");
		}

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

		if (!packet.getClass().getSimpleName().startsWith("PacketPlayIn")) {
			throw new IllegalArgumentException(packet.getClass().getName() + " is not a valid packet.");
		}

		try {
			if (ReflectionUtil.versionIs1_7()) {
				((PacketInjector_v1_7) PacketInjectorPlugin.getInstance().getPacketInjector()).getChannelhandler(player)
						.fireChannelRead(packet);
			} else if (ReflectionUtil.versionIsAtLeast1_14()) {
				((PacketInjector_v1_14) PacketInjectorPlugin.getInstance().getPacketInjector()).getChannelhandler(player)
						.fireChannelRead(packet);
			} else {
				((PacketInjector_v1_8) PacketInjectorPlugin.getInstance().getPacketInjector()).getChannelhandler(player)
						.fireChannelRead(packet);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
