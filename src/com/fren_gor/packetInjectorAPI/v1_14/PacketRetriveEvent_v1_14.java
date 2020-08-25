package com.fren_gor.packetInjectorAPI.v1_14;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

import com.fren_gor.packetInjectorAPI.ReflectionUtil;

import io.netty.channel.ChannelHandlerContext;

public class PacketRetriveEvent_v1_14 implements Cancellable {

	private final Player p;
	private final ChannelHandlerContext c;
	private Object packet;
	private final String packetName;

	public Player getPlayer() {
		return p;
	}

	public Object setPacket(Object packet) {

		if (packet.getClass().getName().equals(this.packet.getClass().getName())) {
			Object p = this.packet;
			this.packet = packet;
			return p;
		}

		throw new IllegalArgumentException(
				"Old packet class doesn't match the new one: " + this.packet.getClass().getName());

	}

	public ChannelHandlerContext getChannelHandlerContext() {
		return c;
	}

	public Object getPacket() {
		return packet;
	}

	public PacketRetriveEvent_v1_14(Player p, ChannelHandlerContext c, Object packet) {
		this.p = p;
		this.c = c;
		this.packet = packet;
		this.packetName = packet.getClass().getSimpleName();
	}

	public String getPacketName() {
		return packetName;
	}

	private boolean cancelled = false;;

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean cancel) {
		cancelled = cancel;

	}

	public void setValue(String field, Object value) {

		ReflectionUtil.setField(packet, field, value);

	}

	public Object getValue(String field) {

		return ReflectionUtil.getField(packet, field);

	}

}
