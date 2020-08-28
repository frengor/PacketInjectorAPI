package com.fren_gor.packetInjectorAPI.v1_8;

import org.bukkit.entity.Player;

import com.fren_gor.packetInjectorAPI.PacketHandler;
import com.fren_gor.packetInjectorAPI.events.PacketEventManager;
import com.fren_gor.packetInjectorAPI.events.PacketRetriveEvent;
import com.fren_gor.packetInjectorAPI.events.PacketSendEvent;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PacketHandler_v1_8 extends ChannelDuplexHandler implements PacketHandler {

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
