package com.fren_gor.packetUtils.v1_7;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import net.minecraft.util.io.netty.channel.ChannelHandlerContext;

public class PacketRetriveEvent_v1_7 extends Event implements Cancellable {

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
	
	public PacketRetriveEvent_v1_7(Player p, ChannelHandlerContext c, Object packet) {
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
