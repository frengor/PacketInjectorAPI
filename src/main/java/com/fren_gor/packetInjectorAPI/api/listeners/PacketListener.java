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

package com.fren_gor.packetInjectorAPI.api.listeners;

import com.fren_gor.packetInjectorAPI.api.events.PacketReceiveEvent;
import com.fren_gor.packetInjectorAPI.api.events.PacketSendEvent;

/**
 * Listener to {@link PacketSendEvent}s and  {@link PacketReceiveEvent}s.
 *
 * @author fren_gor
 */
public interface PacketListener {

    /**
     * Called when a packet is sent to a player.
     *
     * @param event The {@link PacketSendEvent}.
     */
    default void onSend(PacketSendEvent event) {
    }

    /**
     * Called when a player sends a packet to the server.
     *
     * @param event The {@link PacketReceiveEvent}.
     */
    default void onReceive(PacketReceiveEvent event) {
    }
}
