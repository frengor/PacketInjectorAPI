package com.fren_gor.packetUtils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.fren_gor.packetUtils.events.PacketRetriveEvent;
import com.fren_gor.packetUtils.events.PacketSendEvent;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;

public class PacketHandler extends ChannelDuplexHandler {

	private Player p;

	public PacketHandler(final Player p) {
		this.p = p;
	}

	@Override
	public void write(ChannelHandlerContext c, Object m, ChannelPromise promise) throws Exception {

		PacketSendEvent e = new PacketSendEvent(p, c, m, promise);

		Bukkit.getPluginManager().callEvent(e);

		if (e.isCancelled()) {
			return;
		}

		super.write(c, m, promise);

	}

	@Override
	public void channelRead(ChannelHandlerContext c, Object m) throws Exception {

		PacketRetriveEvent e = new PacketRetriveEvent(p, c, m);

		Bukkit.getPluginManager().callEvent(e);

		if (e.isCancelled()) {
			return;
		}

		super.channelRead(c, m);
	}

}
