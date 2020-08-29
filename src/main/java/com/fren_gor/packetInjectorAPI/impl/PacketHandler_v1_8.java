package com.fren_gor.packetInjectorAPI.impl;

import org.bukkit.entity.Player;

import com.fren_gor.packetInjectorAPI.api.PacketEventManager;
import com.fren_gor.packetInjectorAPI.api.PacketHandler;
import com.fren_gor.packetInjectorAPI.api.events.PacketRetriveEvent;
import com.fren_gor.packetInjectorAPI.api.events.PacketSendEvent;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PacketHandler_v1_8 extends  PacketHandler {

	@Getter
	private final Player player;

	@Override
	public void write(ChannelHandlerContext c, Object m, ChannelPromise promise) throws Exception {

		PacketSendEvent event = new PacketSendEvent(player, m);

		PacketEventManager.callSendEvent(event);

		if (event.isCancelled() && !event.getPacketName().contains("Disconnect")) {
			return;
		}

		super.write(c, event.getPacket(), promise);

	}

	@Override
	public void channelRead(ChannelHandlerContext c, Object m) throws Exception {

		PacketRetriveEvent event = new PacketRetriveEvent(player, m);

		PacketEventManager.callRetriveEvent(event);

		if (event.isCancelled()) {
			return;
		}

		super.channelRead(c, event.getPacket());
	}

}
