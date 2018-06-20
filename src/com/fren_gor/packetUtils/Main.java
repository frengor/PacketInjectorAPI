package com.fren_gor.packetUtils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.fren_gor.packetUtils.libraries.org.inventivetalent.update.spiget.SpigetUpdate;
import com.fren_gor.packetUtils.libraries.org.inventivetalent.update.spiget.UpdateCallback;
import com.fren_gor.packetUtils.libraries.org.inventivetalent.update.spiget.comparator.VersionComparator;
import com.fren_gor.packetUtils.v1_7.PacketInjector_v1_7;

public class Main extends JavaPlugin implements Listener {

	private PacketInjector pki;
	private PacketInjector_v1_7 pki_1_7;
	private boolean forceRestart = false;
	private boolean v1_7 = false;

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onJoin(PlayerJoinEvent e) {

		if (v1_7)
			pki_1_7.addPlayer(e.getPlayer());
		else
			pki.addPlayer(e.getPlayer());

	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onQuit(PlayerQuitEvent e) {

		if (v1_7)
			pki_1_7.removePlayer(e.getPlayer());
		else
			pki.removePlayer(e.getPlayer());

	}

	@Override
	public void onEnable() {

		if (ReflectionUtil.getVersion().startsWith("v1_7")) {
			v1_7 = true;
		}

		new BukkitRunnable() {

			@Override
			public void run() {

				if (v1_7) {
					
					pki_1_7 = new PacketInjector_v1_7();
					
					for (Player p : Bukkit.getOnlinePlayers()) {

						pki_1_7.addPlayer(p);

					}
				} else {
					
					pki = new PacketInjector();
					
					for (Player p : Bukkit.getOnlinePlayers()) {

						pki.addPlayer(p);

					}
				}

			}
		}.runTaskLater(this, 1);

		Bukkit.getPluginManager().registerEvents(this, this);

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

	}

	@Override
	public void onDisable() {

		if (forceRestart)
			Bukkit.getServer().shutdown();

	}

}
