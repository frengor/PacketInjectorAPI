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

package com.fren_gor.packetInjectorAPI.api.events;

import com.fren_gor.packetInjectorAPI.ReflectionUtil;
import io.netty.channel.Channel;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * Event fired when a packet is received from a {@link Player}.
 *
 * @author fren_gor
 */
public final class PacketReceiveEvent extends PacketEvent {

    private final Player player;
    private final Channel channel;
    private final String packetName;
    private Object packet;

    /**
     * Creates a new {@code PacketReceiveEvent}.
     *
     * @param player The player who sent the packet, or {@code null} if the packet is a login, status, or handshake packet.
     * @param channel The netty channel.
     * @param packet The packet.
     */
    public PacketReceiveEvent(@Nullable Player player, Channel channel, Object packet) {
        this.player = player;
        this.channel = Objects.requireNonNull(channel, "Channel is null.");
        this.packet = Objects.requireNonNull(packet, "Packet is null.");
        this.packetName = packet.getClass().getSimpleName();
    }

    /**
     * Gets the player who sent the packet.
     *
     * @return The player who sent the packet, or {@code null} if the packet is a login, status, or handshake packet.
     */
    @Nullable
    public Player getPlayer() {
        return player;
    }

    /**
     * Gets the netty channel.
     *
     * @return The netty channel.
     */
    public Channel getChannel() {
        return channel;
    }

    /**
     * Gets the packet.
     *
     * @return The packet.
     */
    public Object getPacket() {
        return packet;
    }

    /**
     * Gets the packet name as returned by {@code getPacket().getClass().getSimpleName()}.
     *
     * @return The packet name as returned by {@code getPacket().getClass().getSimpleName()}.
     * @see Class#getSimpleName()
     */
    public String getPacketName() {
        return packetName;
    }

    /**
     * Sets the packet to be received instead of the old one.
     *
     * @param packet The packet. Must be not {@code null}.
     * @return The old packet.
     */
    public Object setPacket(Object packet) {
        Object tmp = this.packet;
        this.packet = Objects.requireNonNull(packet, "Packet is null.");
        return tmp;
    }

    /**
     * Sets the value of a field of the packet.
     *
     * @param field The field name.
     * @param value The new value of the field.
     * @return {@code true} if the value has been set correctly, {@code false} otherwise.
     * @see ReflectionUtil#setField(Object, String, Object)
     */
    public boolean setValue(String field, Object value) {
        return ReflectionUtil.setField(packet, Objects.requireNonNull(field, "Field is null."), value);
    }

    /**
     * Gets the value of a field of the packet.
     *
     * @param field The field name.
     * @return The value of the field. {@code null} if
     * @see ReflectionUtil#getField(Object, String)
     */
    public Object getValue(String field) {
        return ReflectionUtil.getField(packet, Objects.requireNonNull(field, "Field is null."));
    }
}
