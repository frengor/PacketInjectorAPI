package com.fren_gor.packetUtils.v1_8;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;

public class PacketSendEvent_v1_8 extends Event implements Cancellable {

	private final Player p;
	private final ChannelHandlerContext c;
	private final Object packet;
	private final ChannelPromise promise;
	private final String packetName;

	public Player getPlayer() {
		return p;
	}

	public ChannelHandlerContext getChannelHandlerContext() {
		return c;
	}

	public Object getPacket() {
		return packet;
	}

	public ChannelPromise getChannelPromise() {
		return promise;
	}

	public PacketSendEvent_v1_8(Player p, ChannelHandlerContext c, Object packet, ChannelPromise promise) {
		this.p = p;
		this.c = c;
		this.packet = packet;
		this.promise = promise;
		this.packetName = packet.getClass().getSimpleName();
	}

	public String getPacketName() {
		return packetName;
	}

	private static final HandlerList handlers = new HandlerList();

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
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

}
