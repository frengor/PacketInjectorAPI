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

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.Plugin;

import java.util.Objects;

/**
 * Class to send packets easily to clients or the server itself.
 *
 * @author fren_gor
 */
public final class PacketInjectorAPI {

    private final Plugin owner;
    //private final PacketHandler handler;
    private final PacketEventManager eventManager;

    private final NewPackethandler newpacketHandler;

    /**
     * Creates a new {@code PacketInjectorAPI}.
     *
     * @param plugin The plugin creator of this API instance.
     */
    public PacketInjectorAPI(final Plugin plugin) {
        this.owner = Objects.requireNonNull(plugin, "Plugin is null.");
        if (!plugin.isEnabled())
            throw new IllegalArgumentException("Plugin is not enabled.");

        this.eventManager = new PacketEventManager();
        //this.handler = new PacketHandler(plugin, eventManager);

        this.newpacketHandler = new NewPackethandler(plugin, eventManager);

        Bukkit.getPluginManager().registerEvents(new Listener() {

            @EventHandler(priority = EventPriority.MONITOR)
            public void onDisable(PluginDisableEvent event) {
                if (event.getPlugin() == owner) {
                    eventManager.unregisterEveryPacketListener();
                }
            }

        }, plugin);
    }

    /**
     * Gets the {@link PacketEventManager} of this API instance.
     *
     * @return The {@link PacketEventManager} of this API instance.
     */
    public PacketEventManager getEventManager() {
        return eventManager;
    }

    /**
     * Gets the {@link Plugin} which created this API instance.
     *
     * @return The {@link Plugin} which created this API instance.
     */
    public Plugin getOwner() {
        return owner;
    }

    /**
     * Sends the provided packet to a player.
     *
     * @param player The packet will be sent to this player.
     * @param packet The NMS packet.
     */
    public void sendPacketToClient(Player player, Object packet) {
        Objects.requireNonNull(player, "Player is null.");
        Objects.requireNonNull(packet, "Packet is null.");

        //handler.sendPacket(player, packet);
    }

    /**
     * Sends the provided packet to the server.
     *
     * @param player The player that is supposed to send the packet.
     * @param packet The NMS packet.
     */
    public void sendPacketToServer(Player player, Object packet) {
        Objects.requireNonNull(player, "Player is null.");
        Objects.requireNonNull(packet, "Packet is null.");

        //handler.receivePacket(player, packet);
    }
}
