package com.fren_gor.packetUtils.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import io.netty.channel.ChannelHandlerContext;

public class PacketRetriveEvent extends Event implements Cancellable {

	private final Player p;
	private final ChannelHandlerContext c;
	private final Object packet;

	public Player getPlayer() {
		return p;
	}

	public ChannelHandlerContext getChannelHandlerContext() {
		return c;
	}

	public Object getPacket() {
		return packet;
	}
	
	public PacketRetriveEvent(Player p, ChannelHandlerContext c, Object packet) {
		this.p = p;
		this.c = c;
		this.packet = packet;
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
