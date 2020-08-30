//  MIT License
//  
//  Copyright (c) 2019 fren_gor
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

package com.fren_gor.packetInjectorAPI.api;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang.Validate;
import org.bukkit.plugin.Plugin;

import com.fren_gor.packetInjectorAPI.api.events.PacketRetriveEvent;
import com.fren_gor.packetInjectorAPI.api.events.PacketSendEvent;
import com.fren_gor.packetInjectorAPI.api.listeners.PacketListener;
import com.fren_gor.packetInjectorAPI.api.listeners.PacketRetriveListener;
import com.fren_gor.packetInjectorAPI.api.listeners.PacketSendListener;

import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;
import it.unimi.dsi.fastutil.objects.ReferenceLinkedOpenHashSet;

/**
 * Class used to handle packet events.
 * 
 * @author fren_gor
 */
public final class PacketEventManager {

	private final static Map<Plugin, Set<PacketListener>> MAP_LISTENERS = new Reference2ReferenceOpenHashMap<>();
	private final static Set<PacketListener> SEND_LISTENERS = new ReferenceLinkedOpenHashSet<>(),
			RETRIVE_LISTENERS = new ReferenceLinkedOpenHashSet<>();

	/**
	 * Register a packet send listener.
	 * 
	 * @param owner
	 *            The plugin owning this listener
	 * @param listener
	 *            The packet send listener
	 */
	public static void registerPacketSendListener(Plugin owner, PacketSendListener listener) {
		registerPacketListener(owner, listener);
	}

	/**
	 * Register a packet retrive listener.
	 * 
	 * @param owner
	 *            The plugin owning this listener
	 * @param listener
	 *            The packet send listener
	 */
	public static void registerPacketRetriveListener(Plugin owner, PacketRetriveListener listener) {
		registerPacketListener(owner, listener);
	}

	/**
	 * Register a packet listener.<br>
	 * <br>
	 * 
	 * The listener can be a:<br>
	 * - {@link PacketListener} if you need to listen to both send and retrive events<br>
	 * - {@link PacketSendListener} if you only need to listen to the send event<br>
	 * - {@link PacketRetriveListener} if you only need to listen to the retrive event<br>
	 * 
	 * @param owner
	 *            The plugin owning this listener
	 * @param listener
	 *            The packet listener
	 */
	public synchronized static void registerPacketListener(Plugin owner, PacketListener listener) {
		Validate.notNull(owner, "Plugin cannot be null");
		Validate.notNull(listener, "PacketListener cannot be null");
		Validate.isTrue(owner.isEnabled(), "Plugin isn't enabled");
		Set<PacketListener> list = MAP_LISTENERS.get(owner);
		if (list == null) {
			list = new ReferenceLinkedOpenHashSet<>();
			MAP_LISTENERS.put(owner, list);
		}
		if (list.add(listener)) {
			if (listener instanceof PacketSendListener) {
				SEND_LISTENERS.add(listener);
			} else if (listener instanceof PacketRetriveListener) {
				RETRIVE_LISTENERS.add(listener);
			} else {
				SEND_LISTENERS.add(listener);
				RETRIVE_LISTENERS.add(listener);
			}
		}
	}

	/**
	 * Unregister a packet listener
	 * 
	 * @param listener
	 *            The packet listener
	 */
	public synchronized static void unregisterPacketListener(PacketListener listener) {
		Validate.notNull(listener, "PacketListener cannot be null");
		// Check if listener is registered
		if (SEND_LISTENERS.remove(listener) | RETRIVE_LISTENERS.remove(listener)) { // Bitwise OR to execute both removes
			Iterator<Entry<Plugin, Set<PacketListener>>> it = MAP_LISTENERS.entrySet().iterator();
			while (it.hasNext()) {
				Entry<Plugin, Set<PacketListener>> l = it.next();
				if (l.getValue().remove(listener) && l.getValue().size() == 0) {
					it.remove(); // Free memory
				}
			}
		}
	}

	/**
	 * Unregister all the packet listeners of a plugin.<br>
	 * Does nothing if plugin isn't registered.
	 * 
	 * @param owner
	 *            The plugin owning the listeners
	 */
	public synchronized static void unregisterPacketListener(Plugin owner) {
		Validate.notNull(owner, "Plugin cannot be null");
		Set<PacketListener> list = MAP_LISTENERS.remove(owner);
		if (list != null)
			for (PacketListener p : list) {
				SEND_LISTENERS.remove(p);
				RETRIVE_LISTENERS.remove(p);
			}
	}

	/**
	 * Unregister all the registered packet listeners
	 */
	public synchronized static void unregisterEveryPacketListener() {
		MAP_LISTENERS.clear();
		SEND_LISTENERS.clear();
		RETRIVE_LISTENERS.clear();
	}

	/**
	 * Call a send event
	 * 
	 * @param event
	 *            The event to trigger
	 */
	public synchronized static void callSendEvent(PacketSendEvent event) {
		Validate.notNull(event, "PacketSendEvent cannot be null");
		for (PacketListener p : SEND_LISTENERS)
			p.onSend(event);
	}

	/**
	 * Call a retrive event
	 * 
	 * @param event
	 *            The event to trigger
	 */
	public synchronized static void callRetriveEvent(PacketRetriveEvent event) {
		Validate.notNull(event, "PacketRetriveEvent cannot be null");
		for (PacketListener l : RETRIVE_LISTENERS)
			l.onRetrive(event);
	}

	private PacketEventManager() {
		new UnsupportedOperationException("Cannot instantiate!");
	}

}
