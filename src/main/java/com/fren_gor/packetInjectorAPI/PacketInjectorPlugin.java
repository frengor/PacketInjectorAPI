//  MIT License
//  
//  Copyright (c) 2020 fren_gor
//  
//  Permission is hereby granted, free of charge, to any person obtaining a copy
//  of this software and associated documentation files (the "Software"), to deal
//  in the Software without restriction, including without limitation the rights
//  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
//  copies of the Software, and to permit persons to whom the Software is
//  furnished to do so, subject to the following conditions:
//  
//  The above copyright notice and this permission notice shall be included in all
//  copies or substantial portions of the Software.
//  
//  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
//  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
//  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
//  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
//  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
//  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
//  SOFTWARE.

package com.fren_gor.packetInjectorAPI;

import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.inventivetalent.update.spiget.SpigetUpdate;
import org.inventivetalent.update.spiget.UpdateCallback;
import org.inventivetalent.update.spiget.comparator.VersionComparator;

import com.fren_gor.packetInjectorAPI.api.PacketEventManager;
import com.fren_gor.packetInjectorAPI.api.PacketInjector;
import com.fren_gor.packetInjectorAPI.impl.PacketInjector_v1_8;
import com.fren_gor.packetInjectorAPI.listeners.PluginDisable;

public class PacketInjectorPlugin extends JavaPlugin implements Listener {

	public static final String CHANNEL_HANDLER_NAME = "PacketInjectorAPI";

	private PacketInjector pki;

	private boolean forceRestart = false;
	private static PacketInjectorPlugin instance;

	public static PacketInjectorPlugin getInstance() {
		return instance;
	}

	public void setPacketInjector(PacketInjector pki) {
		this.pki = pki;
	}

	public PacketInjector getPacketInjector() {
		return pki;
	}

	@EventHandler(priority = EventPriority.LOWEST)
	private void onJoin(PlayerJoinEvent e) {

		if (pki == null)
			return;
		pki.addPlayer(e.getPlayer());

	}

	@Override
	public void onLoad() {
		instance = this;
	}

	@Override
	public void onEnable() {

		Bukkit.getPluginManager().registerEvents(new PluginDisable(), this);

		setPacketInjector(new PacketInjector_v1_8());

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

		final SpigetUpdate updater = new SpigetUpdate(this, 57931);

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
				Bukkit.getConsoleSender().sendMessage("�ePacketInjectorAPI is updating!");
				if (hasDirectDownload) {
					if (updater.downloadUpdate()) {
						// Update downloaded, will be loaded when the server
						// restarts
						Bukkit.getConsoleSender()
								.sendMessage("�bUpdate downloaded, will be loaded when the server restarts");
					} else {
						// Update failed
						getLogger().warning("Update download failed, reason is " + updater.getFailReason());
					}
				}
			}

			@Override
			public void upToDate() {
				//// Plugin is up-to-date
				Bukkit.getConsoleSender().sendMessage("�bPacketInjectorAPI is up to date!");
			}
		});

		new Metrics(this, 57931);

	}

	@Override
	public void onDisable() {

		PacketEventManager.unregisterEveryPacketListener();

		for (Player p : Bukkit.getOnlinePlayers()) {
			pki.removePlayer(p);
		}

		if (forceRestart)
			Bukkit.getServer().shutdown();

	}

}
