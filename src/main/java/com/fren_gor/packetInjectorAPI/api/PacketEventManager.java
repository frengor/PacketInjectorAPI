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

import com.fren_gor.packetInjectorAPI.api.events.PacketReceiveEvent;
import com.fren_gor.packetInjectorAPI.api.events.PacketSendEvent;
import com.fren_gor.packetInjectorAPI.api.listeners.PacketListener;
import com.fren_gor.packetInjectorAPI.api.listeners.PacketReceiveListener;
import com.fren_gor.packetInjectorAPI.api.listeners.PacketSendListener;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;

/**
 * Class used to handle packet events.
 *
 * @author fren_gor
 */
public final class PacketEventManager {

    private final Map<Plugin, Set<RegisteredListener>> MAP_LISTENERS = new HashMap<>();
    private final Set<RegisteredListener> SEND_LISTENERS = new HashSet<>(), RECEIVE_LISTENERS = new HashSet<>();

    /**
     * Registers a packet send listener.
     *
     * @param owner The plugin owning this listener.
     * @param listener The packet send listener.
     */
    public void registerPacketSendListener(Plugin owner, PacketSendListener listener) {
        registerPacketListener(owner, listener);
    }

    /**
     * Registers a packet receive listener.
     *
     * @param owner The plugin owning this listener.
     * @param listener The packet receive listener.
     */
    public void registerPacketReceiveListener(Plugin owner, PacketReceiveListener listener) {
        registerPacketListener(owner, listener);
    }

    /**
     * Registers a packet listener.
     * <p>The listener can either be a:
     * <ul>
     *   <li>{@link PacketListener} if you need to listen to both send and receive events;</li>
     *   <li>{@link PacketSendListener} if you only need to listen to send events;</li>
     *   <li>{@link PacketReceiveListener} if you only need to listen to receive events.</li>
     * </ul>
     *
     * @param owner The plugin owning the listener.
     * @param listener The packet listener.
     */
    public synchronized void registerPacketListener(Plugin owner, PacketListener listener) {
        Objects.requireNonNull(owner, "Plugin is null.");
        Objects.requireNonNull(listener, "PacketListener is null.");
        if (!owner.isEnabled())
            throw new IllegalArgumentException("Plugin is not enabled.");

        Set<RegisteredListener> list = MAP_LISTENERS.computeIfAbsent(owner, k -> new HashSet<>());

        RegisteredListener registeredListener = new RegisteredListener(listener);
        if (list.add(registeredListener)) {
            if (listener instanceof PacketSendListener) {
                SEND_LISTENERS.add(registeredListener);
            } else if (listener instanceof PacketReceiveListener) {
                RECEIVE_LISTENERS.add(registeredListener);
            } else {
                SEND_LISTENERS.add(registeredListener);
                RECEIVE_LISTENERS.add(registeredListener);
            }
        }
    }

    /**
     * Unregisters a packet listener.
     *
     * @param listener The packet listener.
     */
    public synchronized void unregisterPacketListener(PacketListener listener) {
        Objects.requireNonNull(listener, "PacketListener is null.");
        RegisteredListener registeredListener = new RegisteredListener(listener);

        // Check if listener is registered
        // Execute both the removes
        boolean send = SEND_LISTENERS.remove(registeredListener);
        boolean receive = RECEIVE_LISTENERS.remove(registeredListener);
        if (send || receive) {
            Iterator<Entry<Plugin, Set<RegisteredListener>>> it = MAP_LISTENERS.entrySet().iterator();
            while (it.hasNext()) {
                Entry<Plugin, Set<RegisteredListener>> l = it.next();
                if (l.getValue().remove(registeredListener) && l.getValue().size() == 0) {
                    it.remove(); // Free memory
                }
            }
        }
    }

    /**
     * Unregisters all the packet listeners of a plugin.
     * <p>This does nothing if the plugin isn't registered.
     *
     * @param owner The plugin owning the listeners to unregister.
     */
    public synchronized void unregisterPacketListener(Plugin owner) {
        Objects.requireNonNull(owner, "Plugin is null.");
        Set<RegisteredListener> list = MAP_LISTENERS.remove(owner);
        if (list != null)
            for (RegisteredListener p : list) {
                SEND_LISTENERS.remove(p);
                RECEIVE_LISTENERS.remove(p);
            }
    }

    /**
     * Unregisters all the registered packet listeners
     */
    public synchronized void unregisterEveryPacketListener() {
        MAP_LISTENERS.clear();
        SEND_LISTENERS.clear();
        RECEIVE_LISTENERS.clear();
    }

    /**
     * Calls a send event.
     *
     * @param event The event to trigger.
     */
    synchronized void callSendEvent(PacketSendEvent event) {
        Objects.requireNonNull(event, "PacketSendEvent is null.");
        for (RegisteredListener p : SEND_LISTENERS)
            p.listener.onSend(event);
    }

    /**
     * Calls a receive event.
     *
     * @param event The event to trigger.
     */
    synchronized void callReceiveEvent(PacketReceiveEvent event) {
        Objects.requireNonNull(event, "PacketReceiveEvent is null.");
        for (RegisteredListener l : RECEIVE_LISTENERS)
            l.listener.onReceive(event);
    }

    private static final class RegisteredListener {
        private final PacketListener listener;

        public RegisteredListener(PacketListener listener) {
            this.listener = listener;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            RegisteredListener that = (RegisteredListener) o;

            return listener == that.listener;
        }

        @Override
        public int hashCode() {
            return listener.hashCode();
        }
    }
}
