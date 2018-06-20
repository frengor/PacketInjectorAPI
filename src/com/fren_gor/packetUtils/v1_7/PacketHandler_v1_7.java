package com.fren_gor.packetUtils.v1_7;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import net.minecraft.util.io.netty.channel.ChannelHandlerContext;
import net.minecraft.util.io.netty.channel.ChannelPromise;
import net.minecraft.util.io.netty.channel.ChannelDuplexHandler;

public class PacketHandler_v1_7 extends ChannelDuplexHandler {

	private Player p;

	public PacketHandler_v1_7(final Player p) {
		this.p = p;
	}

	@Override
	public void write(ChannelHandlerContext c, Object m, ChannelPromise promise) throws Exception {

		PacketSendEvent_v1_7 e = new PacketSendEvent_v1_7(p, c, m, promise);

		Bukkit.getPluginManager().callEvent(e);

		if (e.isCancelled()) {
			return;
		}

		super.write(c, m, promise);

	}

	@Override
	public void channelRead(ChannelHandlerContext c, Object m) throws Exception {

		PacketRetriveEvent_v1_7 e = new PacketRetriveEvent_v1_7(p, c, m);

		Bukkit.getPluginManager().callEvent(e);

		if (e.isCancelled()) {
			return;
		}

		super.channelRead(c, m);
	}

}
