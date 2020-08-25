package com.fren_gor.packetUtils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.fren_gor.packetUtils.libraries.Metrics;
import com.fren_gor.packetUtils.libraries.org.inventivetalent.update.spiget.SpigetUpdate;
import com.fren_gor.packetUtils.libraries.org.inventivetalent.update.spiget.UpdateCallback;
import com.fren_gor.packetUtils.libraries.org.inventivetalent.update.spiget.comparator.VersionComparator;
import com.fren_gor.packetUtils.v1_14.PacketInjector_v1_14;
import com.fren_gor.packetUtils.v1_7.PacketInjector_v1_7;
import com.fren_gor.packetUtils.v1_8.PacketInjector_v1_8;

public class PacketInjectorPlugin extends JavaPlugin implements Listener {

	public static final String CHANNEL_HANDLER_NAME = "PacketInjectorAPI";

	private PacketInjector pki;

	public PacketInjector getPki() {
		return pki;
	}

	// private static List<UUID> kicked = new ArrayList<>(10);

	private boolean forceRestart = false;
	public static boolean v1_7 = false;
	public static boolean v1_14 = false;
	private static PacketInjectorPlugin instance;

	public static PacketInjectorPlugin getInstance() {
		return instance;
	}

	public PacketInjector getPacketInjector() {
		return pki;
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onJoin(PlayerJoinEvent e) {

		if (pki == null)
			return;
		pki.addPlayer(e.getPlayer());

	}

	/*
	 * @EventHandler(priority = EventPriority.LOWEST) public void
	 * onKick(PlayerKickEvent e) {
	 * 
	 * if (pki == null) return;
	 * 
	 * pki.removePlayer(e.getPlayer()); kicked.add(e.getPlayer().getUniqueId());
	 * 
	 * }
	 * 
	 * @EventHandler(priority = EventPriority.LOWEST) public void
	 * onQuit(PlayerQuitEvent e) {
	 * 
	 * if (pki == null) return;
	 * 
	 * if (kicked.contains(e.getPlayer().getUniqueId())) {
	 * kicked.remove(e.getPlayer().getUniqueId()); return; }
	 * 
	 * pki.removePlayer(e.getPlayer());
	 * 
	 * }
	 */

	@Override
	public void onLoad() {
		instance = this;

		if (ReflectionUtil.versionIs1_7()) {
			v1_7 = true;
		} else if (ReflectionUtil.versionIsAtLeast1_14()) {
			v1_14 = true;
		}
	}

	@Override
	public void onEnable() {

		if (v1_7) {

			pki = new PacketInjector_v1_7();

		} else if (v1_14) {

			pki = new PacketInjector_v1_14();

		} else {
			pki = new PacketInjector_v1_8();
		}

		new BukkitRunnable() {

			@Override
			public void run() {

				for (Player p : Bukkit.getOnlinePlayers()) {

					pki.addPlayer(p);

				}

				Bukkit.getPluginManager().registerEvents(instance, instance);

			}
		}.runTaskLater(this, 1);

		if (Bukkit.getPluginManager().isPluginEnabled("ProtocolLib")) {

			forceRestart = true;

		}

		SpigetUpdate updater = new SpigetUpdate(this, 57931);

		// This converts a semantic version to an integer and checks if the
		// updated version is greater
		updater.setVersionComparator(VersionComparator.SEM_VER);

		updater.checkForUpdate(new UpdateCallback() {
			@Override
			public void updateAvailable(String newVersion, String downloadUrl, boolean hasDirectDownload) {
				//// A new version is available
				// newVersion - the latest version
				// downloadUrl - URL to the download
				// hasDirectDownload - whether the update is available for a
				//// direct download on spiget.org
				Bukkit.getConsoleSender().sendMessage("§ePacketInjectorAPI is updating!");
				if (hasDirectDownload) {
					if (updater.downloadUpdate()) {
						// Update downloaded, will be loaded when the server
						// restarts
						Bukkit.getConsoleSender()
								.sendMessage("§bUpdate downloaded, will be loaded when the server restarts");
					} else {
						// Update failed
						getLogger().warning("Update download failed, reason is " + updater.getFailReason());
					}
				}
			}

			@Override
			public void upToDate() {
				//// Plugin is up-to-date
				Bukkit.getConsoleSender().sendMessage("§bPacketInjectorAPI is up to date!");
			}
		});

		new Metrics(this);

	}

	@Override
	public void onDisable() {

		for (Player p : Bukkit.getOnlinePlayers()) {

			pki.removePlayer(p);

		}

		if (forceRestart)
			Bukkit.getServer().shutdown();

	}

}
