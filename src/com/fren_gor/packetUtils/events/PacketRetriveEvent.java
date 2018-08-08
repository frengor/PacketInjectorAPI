package com.fren_gor.packetUtils.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PacketRetriveEvent extends Event implements Cancellable {

	private final Player p;
	private final Object packet;
	private final String packetName;

	public Player getPlayer() {
		return p;
	}

	public Object getPacket() {
		return packet;
	}

	public PacketRetriveEvent(Player p, Object packet) {
		this.p = p;
		this.packet = packet;
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
