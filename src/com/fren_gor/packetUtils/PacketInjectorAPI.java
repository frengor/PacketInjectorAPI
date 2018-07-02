package com.fren_gor.packetUtils;

import java.lang.reflect.InvocationTargetException;

import org.bukkit.entity.Player;

/**
 * Util class by fren_gor
 * Give me credits if you use it in one of your plugin
 * 
 * @author fren_gor
 *
 */
public class PacketInjectorAPI {

	/**
	 * Send packet to a player
	 * @param player The packet will be sent to this player
	 * @param packet The NMS packet
	 */
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

}
