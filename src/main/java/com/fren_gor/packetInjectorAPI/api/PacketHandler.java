// MIT License
//
// Copyright (c) 2021 fren_gor
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.

package com.fren_gor.packetInjectorAPI.api;

import com.comphenix.tinyprotocol.TinyProtocol;
import com.fren_gor.packetInjectorAPI.api.events.PacketReceiveEvent;
import com.fren_gor.packetInjectorAPI.api.events.PacketSendEvent;
import io.netty.channel.Channel;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.atomic.AtomicInteger;

final class PacketHandler extends TinyProtocol {

    private static final AtomicInteger COUNTER = new AtomicInteger();

    private final PacketEventManager manager;

    PacketHandler(Plugin plugin, PacketEventManager manager) {
        super(plugin);
        this.manager = manager;
    }

    @Override
    public Object onPacketOutAsync(@Nullable Player receiver, Channel channel, Object packet) {
        PacketSendEvent event = new PacketSendEvent(receiver, channel, packet);
        manager.callSendEvent(event);

        return event.isCancelled() ? null : event.getPacket();
    }

    @Override
    public Object onPacketInAsync(@Nullable Player sender, Channel channel, Object packet) {
        PacketReceiveEvent event = new PacketReceiveEvent(sender, channel, packet);
        manager.callReceiveEvent(event);

        return event.isCancelled() ? null : event.getPacket();
    }

    @Override
    protected String getHandlerName() {
        return "PacketInjectorAPI-" + COUNTER.getAndIncrement();
    }
}
