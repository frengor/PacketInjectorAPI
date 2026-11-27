package com.fren_gor.lightInjector;

import com.fren_gor.packetInjectorAPI.tests.PacketEventManagerTest;
import io.netty.channel.Channel;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public abstract class LightInjector {

    static {
        PacketEventManagerTest.initialized = true;
    }

    public LightInjector(@NotNull Plugin plugin) {
    }

    // Keep these methods public since they must be called during tests by PacketEventManagerTest
    public abstract @Nullable Object onPacketSendAsync(@NotNull UUID receiverUuid, @Nullable Player receiver, @NotNull Channel channel, @NotNull Object packet);
    public abstract @Nullable Object onPacketReceiveAsync(@NotNull UUID senderUuid, @Nullable Player sender, @NotNull Channel channel, @NotNull Object packet);

    protected abstract @NotNull String getIdentifier();
}
