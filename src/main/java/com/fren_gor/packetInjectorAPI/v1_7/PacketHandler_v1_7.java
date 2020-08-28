package com.fren_gor.packetInjectorAPI.v1_7;

import org.bukkit.entity.Player;

import com.fren_gor.packetInjectorAPI.PacketHandler;
import com.fren_gor.packetInjectorAPI.events.PacketEventManager;
import com.fren_gor.packetInjectorAPI.events.PacketRetriveEvent;
import com.fren_gor.packetInjectorAPI.events.PacketSendEvent;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.util.io.netty.channel.ChannelDuplexHandler;
import net.minecraft.util.io.netty.channel.ChannelHandlerContext;
import net.minecraft.util.io.netty.channel.ChannelPromise;

@RequiredArgsConstructor
public class PacketHandler_v1_7 extends ChannelDuplexHandler implements PacketHandler {

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
