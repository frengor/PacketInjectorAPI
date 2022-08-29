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
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import net.minecraft.network.NetworkManager;
import net.minecraft.server.network.ServerConnection;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_19_R1.CraftServer;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.Plugin;

import java.net.InetAddress;
import java.net.SocketAddress;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

public class NewPackethandler implements Listener {

    private static final String IDENTIFIER = "PacketInjectorAPI";
    private static final String NOT_UNIQUE_ERROR_MESSAGE = "Identifier \"" + IDENTIFIER + "\" is not unique, cannot inject.";

    private final Plugin plugin;
    private final PacketEventManager packetManager;

    private final List<NetworkManager> networkManagers;

    private final Map<UUID, PacketHandler> networkManagerCache = Collections.synchronizedMap(new HashMap<>());

    public NewPackethandler(Plugin plugin, PacketEventManager manager) {
        if (!Bukkit.isPrimaryThread()) {
            throw new IllegalStateException(this.getClass().getSimpleName() + " must be constructed in the main thread.");
        }

        this.plugin = plugin;
        this.packetManager = manager;

        final ServerConnection conn = ((CraftServer) Bukkit.getServer()).getServer().ad();

        if (conn == null) {
            throw new RuntimeException("This shouldn't have happened."); // Should never happen
        }

        this.networkManagers = conn.e();

        Bukkit.getPluginManager().registerEvents(this, plugin);
        for (Player p : Bukkit.getOnlinePlayers()) {
            try {
                injectPlayer(p).player = p;
            } catch (Exception ignored) {
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void onAsyncPlayerPreLoginEvent(AsyncPlayerPreLoginEvent event) {
        NetworkManager manager = getNetworkManager(event.getAddress());
        if (manager == null) {
            plugin.getLogger().warning("Cannot get NetworkManager, cannot inject.");
            return;
        }

        UUID uuid = event.getUniqueId();

        networkManagerCache.remove(uuid); // Remove already-present UUID (if there's any)
        PacketHandler handler = injectPlayer(manager, uuid);
        networkManagerCache.put(uuid, handler); // Cache our handler for later
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void onPlayerLoginEvent(PlayerLoginEvent event) {
        // Get the handler from cache
        PacketHandler packetHandler = networkManagerCache.remove(event.getPlayer().getUniqueId());
        if (packetHandler == null) {
            // Don't print error message, it should have already been printed in onAsyncPlayerPreLoginEvent
            return;
        }

        // Set player
        packetHandler.player = event.getPlayer();
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void onPlayerJoinEvent(PlayerJoinEvent event) {
        // At worst, if player haven't successfully been injected in the previous steps, it's injected now
        // At this point the Player's PlayerConnection field should have been initialized to a non-null value
        NetworkManager manager = ((CraftPlayer) event.getPlayer()).getHandle().b.b;
        ChannelHandler channelHandler = manager.m.pipeline().get(IDENTIFIER);
        if (channelHandler != null) {
            // A channel handler named IDENTIFIER has been found
            if (channelHandler instanceof PacketHandler) {
                // The player have already been injected, only set the player as a backup in the eventuality
                // that onPlayerLoginEvent failed to set it previously
                ((PacketHandler) channelHandler).player = event.getPlayer();
            }
            return; // Don't inject again
        }

        plugin.getLogger().info("Late injection for player " + event.getPlayer().getName());
        injectPlayer(manager, event.getPlayer().getUniqueId()).player = event.getPlayer();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void onPluginDisableEvent(PluginDisableEvent event) {
        if (plugin.equals(event.getPlugin())) {
            // We need to unregister the channel handlers
            synchronized (networkManagers) {
                for (NetworkManager manager : networkManagers) {
                    try {
                        manager.m.pipeline().remove(IDENTIFIER);
                    } catch (Exception exception) {
                        plugin.getLogger().log(Level.SEVERE, "An error occurred while uninjecting a player:", exception);
                    }
                }
            }
        }
    }

    private PacketHandler injectPlayer(Player player) throws RuntimeException {
        return injectPlayer(((CraftPlayer) player).getHandle().b.b, player.getUniqueId());
    }

    private PacketHandler injectPlayer(NetworkManager manager, UUID uuid) throws RuntimeException {
        PacketHandler handler = new PacketHandler(uuid);
        try {
            manager.m.pipeline().addBefore("packet_handler", IDENTIFIER, handler);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(NOT_UNIQUE_ERROR_MESSAGE);
        }
        return handler;
    }

    private NetworkManager getNetworkManager(final InetAddress addr) {
        synchronized (networkManagers) {
            // Address search
            // Iterating backwards is better since NetworkManagers are added at the end of the list
            ListIterator<NetworkManager> iterator = networkManagers.listIterator(networkManagers.size());
            while (iterator.hasPrevious()) {
                NetworkManager manager = iterator.previous();
                SocketAddress o = manager.n;
                if (o instanceof java.net.InetSocketAddress && addr.equals(((java.net.InetSocketAddress) o).getAddress())) {
                    return manager;
                }
            }

            // No NetworkManager has been found with address search

            // Try to get the first NetworkManager without a ChannelHandler named IDENTIFIER. If (at least) two NetworkManager(s)
            // are found in such state, then return null since we cannot be sure of which is the correct NetworkManager.
            NetworkManager savedManager = null;
            iterator = networkManagers.listIterator(networkManagers.size());
            while (iterator.hasPrevious()) {
                NetworkManager manager = iterator.previous();
                ChannelHandler handler = manager.m.pipeline().get(IDENTIFIER);
                if (handler == null) {
                    if (savedManager == null) {
                        savedManager = manager;
                    } else {
                        return null;
                    }
                }
            }
            return savedManager;
        }
    }

    private class PacketHandler extends ChannelDuplexHandler {
        volatile Player player;
        private final UUID uuid;

        public PacketHandler(UUID uuid) {
            this.uuid = uuid;
        }

        @Override
        public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
            if (player == null) // If player hasn't been set, then the map needs to be cleaned up
                networkManagerCache.remove(uuid);

            super.channelUnregistered(ctx);
        }

        @Override
        public void write(ChannelHandlerContext ctx, Object packet, ChannelPromise promise) throws Exception {
            try {
                PacketSendEvent event = new PacketSendEvent(player, ctx.channel(), packet);
                packetManager.callSendEvent(event);
                if (!event.isCancelled())
                    super.write(ctx, packet, promise);
            } catch (OutOfMemoryError error) {
                // Out of memory, re-throw and return immediately
                throw error;
            } catch (Throwable throwable) {
                plugin.getLogger().log(Level.SEVERE, "An error occurred while calling PacketSendEvent:", throwable);
                throwable.printStackTrace();
                super.write(ctx, packet, promise);
            }
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object packet) throws Exception {
            try {
                PacketReceiveEvent event = new PacketReceiveEvent(player, ctx.channel(), packet);
                packetManager.callReceiveEvent(event);
                if (!event.isCancelled())
                    super.channelRead(ctx, packet);
            } catch (OutOfMemoryError error) {
                // Out of memory, re-throw and return immediately
                throw error;
            } catch (Throwable throwable) {
                plugin.getLogger().log(Level.SEVERE, "An error occurred while calling PacketReceiveEvent:", throwable);
                throwable.printStackTrace();
                super.channelRead(ctx, packet);
            }
        }
    }
}
