//  MIT License
//  
//  Copyright (c) 2020 fren_gor
//  
//  Permission is hereby granted, free of charge, to any person obtaining a copy
//  of this software and associated documentation files (the "Software"), to deal
//  in the Software without restriction, including without limitation the rights
//  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
//  copies of the Software, and to permit persons to whom the Software is
//  furnished to do so, subject to the following conditions:
//  
//  The above copyright notice and this permission notice shall be included in all
//  copies or substantial portions of the Software.
//  
//  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
//  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
//  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
//  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
//  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
//  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
//  SOFTWARE.

package com.fren_gor.packetInjectorAPI;

import org.bukkit.entity.Player;

/**
 * Util class by fren_gor Give me credits if you use it in one of your plugin
 * 
 * @author fren_gor
 *
 */
public class PacketInjectorAPI {

	private static final Class<?> packetClass = ReflectionUtil.getNMSClass("Packet");

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
			PacketInjectorPlugin.getInstance().getPacketInjector().sendPacket(player, packet);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * Send packet to the server
	 * 
	 * @param player
	 *            The player that is supposed to send the packet
	 * @param packet
	 *            The NMS packet
	 */
	public static void sendPacketToServer(Player player, Object packet) {

		if (!packet.getClass().getSimpleName().startsWith("PacketPlayIn")) {
			throw new IllegalArgumentException(packet.getClass().getName() + " is not a valid packet.");
		}

		try {
			PacketInjectorPlugin.getInstance().getPacketInjector().sendPacketToServer(player, packet);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
