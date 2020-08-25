package com.fren_gor.packetInjectorAPI.v1_8;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.fren_gor.packetInjectorAPI.PacketHandler;
import com.fren_gor.packetInjectorAPI.events.PacketRetriveEvent;
import com.fren_gor.packetInjectorAPI.events.PacketSendEvent;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;

public class PacketHandler_v1_8 extends ChannelDuplexHandler implements PacketHandler {

	private Player p;

	private ChannelHandlerContext c;

	public synchronized ChannelHandlerContext getChannelHandlerContext() {
		return c;
	}

	public PacketHandler_v1_8(final Player p) {
		this.p = p;
	}

	@Override
	public void write(ChannelHandlerContext c, Object m, ChannelPromise promise) throws Exception {

		PacketSendEvent_v1_8 e = new PacketSendEvent_v1_8(p, c, m, promise);

		Bukkit.getPluginManager().callEvent(e);

		if (e.isCancelled() && !e.getPacketName().contains("Disconnect")) {
			return;
		}

		PacketSendEvent event = new PacketSendEvent(p, m);

		Bukkit.getPluginManager().callEvent(event);

		if (event.isCancelled() && !event.getPacketName().contains("Disconnect")) {
			return;
		}

		super.write(c, event.getPacket(), promise);

	}

	@Override
	public void channelRead(ChannelHandlerContext c, Object m) throws Exception {

		synchronized (this) {
			this.c = c;
		}

		PacketRetriveEvent_v1_8 e = new PacketRetriveEvent_v1_8(p, c, m);

		Bukkit.getPluginManager().callEvent(e);

		if (e.isCancelled() && !e.getPacketName().contains("Disconnect")) {
			return;
		}

		PacketRetriveEvent event = new PacketRetriveEvent(p, m);

		Bukkit.getPluginManager().callEvent(event);

		if (event.isCancelled() && !event.getPacketName().contains("Disconnect")) {
			return;
		}

		super.channelRead(c, event.getPacket());
	}

}
