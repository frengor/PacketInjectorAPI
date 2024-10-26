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

package com.fren_gor.packetInjectorAPI;

import com.fren_gor.packetInjectorAPI.api.events.PacketReceiveEvent;
import com.fren_gor.packetInjectorAPI.api.events.PacketSendEvent;
import com.fren_gor.packetInjectorAPI.api.listeners.PacketListener;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.StringJoiner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

class Commands implements CommandExecutor, TabCompleter {

    private static final List<String> DEFAULT_ALLOW_LIST = Collections.emptyList();
    private static final List<String> DEFAULT_DENY_LIST = Collections.unmodifiableList(Arrays.asList(
            "PacketPlayOutKeepAlive",
            "PacketPlayOutEntityTeleport",
            "PacketPlayInKeepAlive",
            "PacketPlayOutEntityStatus",
            "PacketPlayOutEntityLook",
            "PacketPlayInPosition",
            "PacketPlayOutSpawnEntityLiving",
            "PacketPlayOutEntityEquipment",
            "PacketPlayInLook",
            "PacketPlayOutEntityDestroy",
            "PacketPlayOutEntityHeadRotation",
            "PacketPlayOutRelEntityMoveLook",
            "PacketPlayOutEntityMetadata",
            "PacketPlayOutEntityVelocity",
            "PacketPlayOutLightUpdate",
            "PacketPlayOutRelEntityMove",
            "PacketPlayOutUpdateAttributes",
            "PacketPlayOutUpdateTime",
            "PacketPlayInPositionLook",
            "PacketPlayOutMapChunk",
            "PacketPlayOutMultiBlockChange",
            "PacketPlayOutUnloadChunk",
            "PacketPlayOutBlockChange",
            "ClientboundLevelChunkWithLightPacket",
            "ClientboundChunkBatchStartPacket",
            "ServerboundChunkBatchReceivedPacket",
            "ClientboundChunkBatchFinishedPacket"
            ));
    private static final List<String> PACKET_LOGGER_TAB_COMPLETER = Arrays.asList("start", "stop", "status", "filter");
    private static final List<String> FILTER_TAB_COMPLETER = Arrays.asList("allow", "deny", "list", "reset", "clear");
    private static final List<String> LIST_OPTIONS_TAB_COMPLETER = Arrays.asList("add", "remove", "clear", "reset");

    private static final Method BUNDLE_PACKETS_AS_ITERABLE;

    static {
        Method m = null;
        Class<?> bundlePacket = null;
        if (ReflectionUtil.VERSION > 19 || (ReflectionUtil.VERSION == 19 && ReflectionUtil.MINOR_VERSION >= 4)) {
            bundlePacket = ReflectionUtil.getNMSClass("BundlePacket", "BundlePacket", "network.protocol");
        }
        if (bundlePacket != null) {
            m = Arrays.stream(bundlePacket.getDeclaredMethods())
                    .filter(method -> method.getParameterCount() == 0 && method.getReturnType() == Iterable.class)
                    .findFirst()
                    .orElse(null);

            if (m != null) {
                m.setAccessible(true);
            }
        }
        BUNDLE_PACKETS_AS_ITERABLE = m;
    }

    private final PacketInjectorPlugin plugin;
    private final PacketListener packetListener;
    private final Set<String> allow = new HashSet<>();
    private final Set<String> deny = new HashSet<>(DEFAULT_DENY_LIST);
    private final Object LOCK = new Object();

    private boolean started = false;

    public Commands(PacketInjectorPlugin plugin) {
        this.plugin = Objects.requireNonNull(plugin, "Plugin is null.");
        final Logger logger = plugin.getLogger();
        packetListener = new PacketListener() {
            @Override
            public void onSend(PacketSendEvent event) {
                if (isToSkip(event.getPacketName())) {
                    return;
                }
                StringBuilder b = new StringBuilder();
                if (event.getPlayer() != null) {
                    b.append(event.getPlayer().getName()).append(' ');
                }
                b.append("<- ");
                if (BUNDLE_PACKETS_AS_ITERABLE != null && "ClientboundBundlePacket".equals(event.getPacketName())) {
                    // Bundle, print also the packets it contains
                    try {
                        Iterable<?> packets = (Iterable<?>) BUNDLE_PACKETS_AS_ITERABLE.invoke(event.getPacket());
                        b.append(event.getPacketName());
                        StringJoiner joiner = new StringJoiner(", ", " (", ")");
                        for (Object packet : packets) {
                            appendPacketName(packet.getClass().getSimpleName(), packet, joiner);
                        }
                        b.append(joiner);
                    } catch (ReflectiveOperationException e) {
                        logger.log(Level.SEVERE, "An error occurred while getting the packets inside a ClientboundBundlePacket", e);
                        return;
                    }
                } else {
                    appendPacketName(event.getPacketName(), event.getPacket(), b);
                }
                logger.info(b.toString());
            }

            @Override
            public void onReceive(PacketReceiveEvent event) {
                if (isToSkip(event.getPacketName())) {
                    return;
                }
                StringBuilder b = new StringBuilder();
                if (event.getPlayer() != null) {
                    b.append(event.getPlayer().getName()).append(' ');
                }
                b.append("-> ");
                appendPacketName(event.getPacketName(), event.getPacket(), b);
                logger.info(b.toString());
            }
        };
    }

    private static void appendPacketName(String packetName, Object packet, StringBuilder b) {
        if (packetName.length() > 5)
            b.append(packetName);
        else
            b.append(packet.getClass().getName());
    }

    private static void appendPacketName(String packetName, Object packet, StringJoiner j) {
        if (packetName.length() > 5)
            j.add(packetName);
        else
            j.add(packet.getClass().getName());
    }

    private boolean isToSkip(String name) {
        synchronized (LOCK) {
            return allow.isEmpty() ? deny.contains(name) : !allow.contains(name) || deny.contains(name);
        }
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length < 2 || !"packetlogger".equalsIgnoreCase(args[0])) {
            sender.sendMessage("§cUsage: /" + label + " packetlogger <start|stop|status|filter> ...");
            return false;
        }

        if (!sender.hasPermission("packetinjectorapi.packetlogger")) {
            sender.sendMessage("§cYou don't have the permission to execute this command.");
            return false;
        }

        switch (args[1]) {
            case "start":
                if (started) {
                    sender.sendMessage("§cPacketLogger is already started.");
                } else {
                    plugin.getPacketInjectorAPI().getEventManager().registerPacketListener(plugin, packetListener);
                    sender.sendMessage("§aPacketLogger started.");
                    started = true;
                }
                break;
            case "stop":
                if (started) {
                    plugin.getPacketInjectorAPI().getEventManager().unregisterPacketListener(packetListener);
                    sender.sendMessage("§ePacketLogger stopped.");
                    started = false;
                } else {
                    sender.sendMessage("§cPacketLogger is not started.");
                }
                break;
            case "status":
                if (started)
                    sender.sendMessage("§aPacketLogger is enabled.");
                else
                    sender.sendMessage("§ePacketLogger is disabled.");
                break;
            case "filter":
                if (args.length < 3) {
                    sender.sendMessage("§cUsage: /" + label + " packetlogger filter <allow|deny|list|reset|clear> ...");
                    return false;
                }
                switch (args[2]) {
                    case "allow":
                        if (args.length < 4) {
                            sender.sendMessage("§cUsage: /" + label + " packetlogger filter allow <add|remove|clear|reset> ...");
                            return false;
                        }
                        switch (args[3]) {
                            case "add":
                                if (args.length != 5 || args[4].isEmpty()) {
                                    sender.sendMessage("§cUsage: /" + label + " packetlogger filter allow add <packetClassName>");
                                    return false;
                                }
                                synchronized (LOCK) {
                                    allow.add(args[4]);
                                }
                                sender.sendMessage("§aSuccessfully added §e" + args[4] + "§a to allow list.");
                                break;
                            case "remove":
                                if (args.length != 5 || args[4].isEmpty()) {
                                    sender.sendMessage("§cUsage: /" + label + " packetlogger filter allow remove <packetClassName>");
                                    return false;
                                }
                                synchronized (LOCK) {
                                    allow.remove(args[4]);
                                }
                                sender.sendMessage("§aSuccessfully removed §e" + args[4] + "§a from allow list.");
                                break;
                            case "clear":
                                synchronized (LOCK) {
                                    allow.clear();
                                }
                                sender.sendMessage("§aSuccessfully cleared allow list.");
                                break;
                            case "reset":
                                synchronized (LOCK) {
                                    allow.clear();
                                    allow.addAll(DEFAULT_ALLOW_LIST);
                                }
                                sender.sendMessage("§aSuccessfully reset allow list to default.");
                                break;
                            default:
                                sender.sendMessage("§cUsage: /" + label + " packetlogger filter allow <add|remove|clear|reset> ...");
                                return false;
                        }
                        break;
                    case "deny":
                        if (args.length < 4) {
                            sender.sendMessage("§cUsage: /" + label + " packetlogger filter deny <add|remove|clear|reset> ...");
                            return false;
                        }
                        switch (args[3]) {
                            case "add":
                                if (args.length != 5 || args[4].isEmpty()) {
                                    sender.sendMessage("§cUsage: /" + label + " packetlogger filter deny add <packetClassName>");
                                    return false;
                                }
                                synchronized (LOCK) {
                                    deny.add(args[4]);
                                }
                                sender.sendMessage("§aSuccessfully added §e" + args[4] + "§a to deny list.");
                                break;
                            case "remove":
                                if (args.length != 5 || args[4].isEmpty()) {
                                    sender.sendMessage("§cUsage: /" + label + " packetlogger filter deny remove <packetClassName>");
                                    return false;
                                }
                                synchronized (LOCK) {
                                    deny.remove(args[4]);
                                }
                                sender.sendMessage("§aSuccessfully removed §e" + args[4] + "§a from deny list.");
                                break;
                            case "clear":
                                synchronized (LOCK) {
                                    deny.clear();
                                }
                                sender.sendMessage("§aSuccessfully cleared deny list.");
                                break;
                            case "reset":
                                synchronized (LOCK) {
                                    deny.clear();
                                    deny.addAll(DEFAULT_DENY_LIST);
                                }
                                sender.sendMessage("§aSuccessfully reset deny list to default.");
                                break;
                            default:
                                sender.sendMessage("§cUsage: /" + label + " packetlogger filter deny <add|remove|clear|reset> ...");
                                return false;
                        }
                        break;
                    case "list":
                        StringJoiner a = new StringJoiner("§8, §7", "§8[§7", "§8]").setEmptyValue("§7none");
                        StringJoiner d = new StringJoiner("§8, §7", "§8[§7", "§8]").setEmptyValue("§7none");
                        synchronized (LOCK) {
                            for (String s : allow) {
                                a.add(s);
                            }
                            for (String s : deny) {
                                d.add(s);
                            }
                        }
                        sender.sendMessage("§aAllowed packets: " + a);
                        sender.sendMessage("§eDenied packets: " + d);
                        break;
                    case "reset":
                        synchronized (LOCK) {
                            allow.clear();
                            deny.clear();
                            allow.addAll(DEFAULT_ALLOW_LIST);
                            deny.addAll(DEFAULT_DENY_LIST);
                        }
                        sender.sendMessage("§aSuccessfully reset filters to default.");
                        break;
                    case "clear":
                        synchronized (LOCK) {
                            allow.clear();
                            deny.clear();
                        }
                        sender.sendMessage("§aSuccessfully cleared filters.");
                        break;
                    default:
                        sender.sendMessage("§cUsage: /" + label + " packetlogger filter <allow|deny|list|reset|clear> ...");
                        return false;
                }
                break;
            default:
                sender.sendMessage("§cUsage: /" + label + " packetlogger <start|stop|status|filter> ...");
                return false;
        }
        return true;
    }

    @Override
    @Nullable
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0 || !sender.hasPermission("packetinjectorapi.packetlogger")) {
            return Collections.emptyList();
        }
        if (args.length == 1) {
            if ("packetlogger".startsWith(args[0].toLowerCase())) {
                return Collections.singletonList("packetlogger");
            } else {
                return Collections.emptyList();
            }
        }
        if (!"packetlogger".equalsIgnoreCase(args[0])) {
            return Collections.emptyList();
        }
        if (args.length == 2) {
            return PACKET_LOGGER_TAB_COMPLETER.stream().filter(s -> s.startsWith(args[1])).collect(Collectors.toList());
        } else if ("filter".equalsIgnoreCase(args[1])) {
            if (args.length == 3) {
                return FILTER_TAB_COMPLETER.stream().filter(s -> s.startsWith(args[2])).collect(Collectors.toList());
            } else if (args.length == 4 && ("allow".equalsIgnoreCase(args[2]) || "deny".equalsIgnoreCase(args[2]))) {
                return LIST_OPTIONS_TAB_COMPLETER.stream().filter(s -> s.startsWith(args[3])).collect(Collectors.toList());
            }
        }
        return Collections.emptyList();
    }
}
