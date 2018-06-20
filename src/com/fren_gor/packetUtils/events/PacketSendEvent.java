package com.fren_gor.packetUtils.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;

public class PacketSendEvent extends Event implements Cancellable {

	public final Player p;
	public final ChannelHandlerContext c;
	public final Object packet;
	public final ChannelPromise promise;

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

	public PacketSendEvent(Player p, ChannelHandlerContext c, Object packet, ChannelPromise promise) {
		this.p = p;
		this.c = c;
		this.packet = packet;
		this.promise = promise;
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
