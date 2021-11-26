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

import com.fren_gor.packetInjectorAPI.api.PacketInjectorAPI;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;
import java.util.function.BiConsumer;
import java.util.logging.Logger;

/**
 * PacketInjectorAPI main class.
 *
 * @author fren_gor
 */
public class PacketInjectorPlugin extends JavaPlugin {

    private static PacketInjectorPlugin instance;

    private PacketInjectorAPI packetInjectorAPI;

    /**
     * Gets the plugin instance.
     *
     * @return The plugin instance.
     */
    public static PacketInjectorPlugin getInstance() {
        return instance;
    }

    /**
     * Gets the {@link PacketInjectorAPI} instance.
     *
     * @return The {@link PacketInjectorAPI} instance.
     */
    public PacketInjectorAPI getPacketInjectorAPI() {
        return packetInjectorAPI;
    }

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {
        packetInjectorAPI = new PacketInjectorAPI(this);

        Commands commands = new Commands(this);
        PluginCommand command = Bukkit.getPluginCommand("packetinjectorapi");
        if (command != null) {
            command.setExecutor(commands);
            command.setTabCompleter(commands);
        }

        getVersion(this, 57931, (version, rId) -> {
            Logger logger = getLogger();
            String[] currentS = this.getDescription().getVersion().split("\\.");
            String[] onlineS = version.split("\\.");
            int max = Math.max(currentS.length, onlineS.length);
            int[] current = parseVersions(currentS, max), online = parseVersions(onlineS, max);

            boolean upToDate = true;
            for (int i = 0; i < max; i++) {
                if (current[i] > online[i]) {
                    break;
                }
                if (current[i] < online[i]) {
                    upToDate = false;
                    break;
                }
            }
            if (upToDate) {
                logger.info(getName() + " is up to date.");
            } else {
                logger.info("There is a new update available.");
                logger.info("Download it at: https://www.spigotmc.org/resources/" + rId);
            }
        });

        new Metrics(this, 2755);
    }

    @Override
    public void onDisable() {
        instance = null;
    }

    private static void getVersion(final Plugin plugin, final int resourceId, final BiConsumer<String, Integer> consumer) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try (InputStream inputStream = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + resourceId)
                    .openStream(); Scanner scanner = new Scanner(inputStream)) {
                if (scanner.hasNext()) {
                    consumer.accept(scanner.next(), resourceId);
                }
            } catch (Exception exception) {
                plugin.getLogger().info("Cannot look for updates: " + exception.getMessage());
            }
        });
    }

    private static int[] parseVersions(String[] arr, int lenght) {
        int[] ret = new int[lenght];
        int i = 0;
        for (; i < arr.length; i++) {
            ret[i] = Integer.parseInt(arr[i]);
        }
        for (; i < lenght; i++) {
            ret[i] = 0;
        }
        return ret;
    }
}
