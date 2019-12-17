package com.fren_gor.packetUtils;

import org.bukkit.entity.Player;

public abstract interface PacketInjector {

	public abstract Class<?> getNetworkManager();

	public abstract Object getNetworkManager(Object ep) throws Exception;

	public abstract PacketHandler addPlayer(Player p);

	public abstract void removePlayer(Player p);

	public abstract PacketHandler getHandler(Player p);

}
