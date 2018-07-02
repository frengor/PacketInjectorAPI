package com.fren_gor.packetUtils.v1_8;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.fren_gor.packetUtils.events.PacketRetriveEvent;
import com.fren_gor.packetUtils.events.PacketSendEvent;
import com.fren_gor.packetUtils.v1_8.PacketRetriveEvent_v1_8;
import com.fren_gor.packetUtils.v1_8.PacketSendEvent_v1_8;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;

public class PacketHandler_v1_8 extends ChannelDuplexHandler {

	private Player p;

	public PacketHandler_v1_8(final Player p) {
		this.p = p;
	}

	@Override
	public void write(ChannelHandlerContext c, Object m, ChannelPromise promise) throws Exception {

		PacketSendEvent_v1_8 e = new PacketSendEvent_v1_8(p, c, m, promise);

		Bukkit.getPluginManager().callEvent(e);

		if (e.isCancelled()) {
			return;
		}
		
		PacketSendEvent event = new PacketSendEvent(p, m);

		Bukkit.getPluginManager().callEvent(event);

		if (event.isCancelled()) {
			return;
		}

		super.write(c, m, promise);

	}

	@Override
	public void channelRead(ChannelHandlerContext c, Object m) throws Exception {

		PacketRetriveEvent_v1_8 e = new PacketRetriveEvent_v1_8(p, c, m);

		Bukkit.getPluginManager().callEvent(e);

		if (e.isCancelled()) {
			return;
		}
		
		PacketRetriveEvent event = new PacketRetriveEvent(p, m);

		Bukkit.getPluginManager().callEvent(event);

		if (event.isCancelled()) {
			return;
		}

		super.channelRead(c, m);
	}

}
