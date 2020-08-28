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

package com.fren_gor.packetInjectorAPI.tests;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;
import java.util.Set;

import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.Plugin;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.fren_gor.packetInjectorAPI.events.PacketEventManager;
import com.fren_gor.packetInjectorAPI.events.PacketListener;
import com.fren_gor.packetInjectorAPI.events.PacketRetriveEvent;
import com.fren_gor.packetInjectorAPI.events.PacketSendEvent;
import com.fren_gor.packetInjectorAPI.listeners.PluginDisable;
import com.fren_gor.packetInjectorAPI.tests.dummyClasses.DummyPlugin;
import com.fren_gor.packetInjectorAPI.tests.dummyClasses.PacketPlayIn;
import com.fren_gor.packetInjectorAPI.tests.dummyClasses.PacketPlayOut;
import com.fren_gor.packetInjectorAPI.tests.listeners.AbstractListener;
import com.fren_gor.packetInjectorAPI.tests.listeners.FullListener;
import com.fren_gor.packetInjectorAPI.tests.listeners.RetriveListener;
import com.fren_gor.packetInjectorAPI.tests.listeners.SendListener;

public class PacketEventManagerTest {

	private static DummyPlugin pl;
	private static AbstractListener[] listeners = new AbstractListener[3];
	private static Map<Plugin, Set<PacketListener>> MAP_LISTENERS;
	private static Set<PacketListener> SEND_LISTENERS, RETRIVE_LISTENERS;

	@SuppressWarnings("unchecked")
	@BeforeAll
	static void init() {
		pl = new DummyPlugin();
		MAP_LISTENERS = (Map<Plugin, Set<PacketListener>>) ReflectionUtil.getField(PacketEventManager.class,
				"MAP_LISTENERS");
		SEND_LISTENERS = (Set<PacketListener>) ReflectionUtil.getField(PacketEventManager.class, "SEND_LISTENERS");
		RETRIVE_LISTENERS = (Set<PacketListener>) ReflectionUtil.getField(PacketEventManager.class,
				"RETRIVE_LISTENERS");

		listeners[0] = new FullListener();

		listeners[1] = new SendListener();

		listeners[2] = new RetriveListener();
	}

	@Test
	@DisplayName("Null Tests")
	void testNull() {

		assertThrows(IllegalArgumentException.class,
				() -> PacketEventManager.registerPacketListener(null, new PacketListener() {
				}), "Plugin may be null in register");
		assertThrows(IllegalArgumentException.class, () -> PacketEventManager.registerPacketListener(pl, null),
				"PacketListener may be null in register");
		assertThrows(IllegalArgumentException.class,
				() -> PacketEventManager.unregisterPacketListener((PacketListener) null),
				"PacketListener may be null in unregister");
		assertThrows(IllegalArgumentException.class, () -> PacketEventManager.unregisterPacketListener((Plugin) null),
				"Plugin may be null in unregister");
		assertThrows(IllegalArgumentException.class, () -> PacketEventManager.callRetriveEvent(null),
				"PacketRetriveEvent may be null in call");
		assertThrows(IllegalArgumentException.class, () -> PacketEventManager.callSendEvent(null),
				"PacketSendEvent may be null in call");

	}

	@Test
	@DisplayName("Register, Call, and Unregister Test")
	void testRegister() {
		for (int i = 0; i < listeners.length; i++)
			internalTestRegister(i);

		PacketEventManager.callSendEvent(new PacketSendEvent(null, new PacketPlayOut()));
		PacketEventManager.callRetriveEvent(new PacketRetriveEvent(null, new PacketPlayIn()));

		for (int i = 0; i < listeners.length; i++)
			internalCallTest(i);

		for (int i = 0; i < listeners.length; i++)
			internalTestUnregister(i);

		assertFalse(MAP_LISTENERS.containsKey(pl), "Plugin hasn't been removed from map");

	}

	void internalTestRegister(int index) {

		AbstractListener listener = listeners[index];

		PacketEventManager.registerPacketListener(pl, listener);

		assertTrue(MAP_LISTENERS.containsKey(pl), "Plugin isn't being registered [" + index + "]");

		assertTrue(MAP_LISTENERS.get(pl).contains(listener), "Listener isn't being registered [" + index + "]");

		assertTrue(listener.checkSendSet(SEND_LISTENERS), listener.sendMessage() + " [" + index + "]");
		assertTrue(listener.checkRetriveSet(RETRIVE_LISTENERS), listener.retriveMessage() + " [" + index + "]");

	}

	void internalTestUnregister(int index) {

		AbstractListener listener = listeners[index];

		PacketEventManager.unregisterPacketListener(listener);

		if (MAP_LISTENERS.containsKey(pl)) {
			assertFalse(MAP_LISTENERS.get(pl).contains(listener), "Listener hasn't been removed [" + index + "]");
		}

		assertFalse(SEND_LISTENERS.contains(listener), "Send set contains listener when it shouldn't [" + index + "]");
		assertFalse(RETRIVE_LISTENERS.contains(listener),
				"Retrive set contains listener when it shouldn't [" + index + "]");

	}

	void internalCallTest(int index) {

		AbstractListener listener = listeners[index];

		listener.checkSendCall();
		listener.checkRetriveCall();

	}

	@Test
	@DisplayName("Plugin unregister Test")
	void testPluginUnregister() {
		PacketListener l = new FullListener();
		PacketEventManager.registerPacketListener(pl, l);

		PacketEventManager.unregisterPacketListener(pl);

		assertFalse(MAP_LISTENERS.containsKey(pl), "Plugin hasn't been unregistered");

		assertFalse(SEND_LISTENERS.contains(l), "Plugin unregistration left listener in send map");
		assertFalse(RETRIVE_LISTENERS.contains(l), "Plugin unregistration left listener in retrive map");
	}

	@Test
	@DisplayName("PluginDisableEvent Test")
	void testPluginDisableEvent() {
		Plugin p = new DummyPlugin();
		PacketEventManager.registerPacketListener(p, new FullListener());
		ReflectionUtil.invoke(new PluginDisable(), "onPluginDisabling", new PluginDisableEvent(p));
		assertFalse(MAP_LISTENERS.containsKey(p), "Plugin hasn't been unregistered by disable event");
	}

	@Test
	@DisplayName("Unregister All Test")
	void testUnrgisterAll() {
		PacketEventManager.registerPacketListener(new DummyPlugin(), new FullListener());
		for (int i = 0; i < listeners.length; i++)
			PacketEventManager.registerPacketListener(pl, listeners[i]);
		PacketEventManager.unregisterEveryPacketListener();
		assertTrue(MAP_LISTENERS.size() == 0, "At least one plugin hasn't been unregistered");
		assertTrue(SEND_LISTENERS.size() == 0, "At least one send listener hasn't been unregistered");
		assertTrue(RETRIVE_LISTENERS.size() == 0, "At least one retrive listener hasn't been unregistered");
	}

}
