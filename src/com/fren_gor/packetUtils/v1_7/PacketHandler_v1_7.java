package com.fren_gor.packetUtils.v1_7;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.fren_gor.packetUtils.PacketHandler;
import com.fren_gor.packetUtils.events.PacketRetriveEvent;
import com.fren_gor.packetUtils.events.PacketSendEvent;

import net.minecraft.util.io.netty.channel.ChannelDuplexHandler;
import net.minecraft.util.io.netty.channel.ChannelHandlerContext;
import net.minecraft.util.io.netty.channel.ChannelPromise;

public class PacketHandler_v1_7 extends ChannelDuplexHandler implements PacketHandler {

	private Player p;

	private ChannelHandlerContext c;

	public synchronized ChannelHandlerContext getChannelHandlerContext() {
		return c;
	}

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

		PacketSendEvent event = new PacketSendEvent(p, e.getPacket());

		Bukkit.getPluginManager().callEvent(event);

		if (event.isCancelled()) {
			return;
		}

		super.write(c, m, promise);

	}

	@Override
	public void channelRead(ChannelHandlerContext c, Object m) throws Exception {

		synchronized (this) {

			this.c = c;

		}

		PacketRetriveEvent_v1_7 e = new PacketRetriveEvent_v1_7(p, c, m);

		Bukkit.getPluginManager().callEvent(e);

		if (e.isCancelled()) {
			return;
		}

		PacketRetriveEvent event = new PacketRetriveEvent(p, e.getPacket());

		Bukkit.getPluginManager().callEvent(event);

		if (event.isCancelled()) {
			return;
		}

		super.channelRead(c, m);
	}

}
