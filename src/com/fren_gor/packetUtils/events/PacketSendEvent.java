package com.fren_gor.packetUtils.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PacketSendEvent extends Event implements Cancellable {

	public final Player p;
	public final Object packet;

	public Player getPlayer() {
		return p;
	}

	public Object getPacket() {
		return packet;
	}

	public PacketSendEvent(Player p, Object packet) {
		this.p = p;
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