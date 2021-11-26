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

import be.seeseemelk.mockbukkit.MockBukkit;
import com.comphenix.tinyprotocol.TinyProtocol;
import com.fren_gor.packetInjectorAPI.api.PacketEventManager;
import com.fren_gor.packetInjectorAPI.api.PacketInjectorAPI;
import com.fren_gor.packetInjectorAPI.api.listeners.PacketListener;
import com.fren_gor.packetInjectorAPI.tests.dummyClasses.DummyPacket;
import com.fren_gor.packetInjectorAPI.tests.dummyClasses.SimpleChannel;
import com.fren_gor.packetInjectorAPI.tests.listeners.AbstractListener;
import com.fren_gor.packetInjectorAPI.tests.listeners.FullListener;
import com.fren_gor.packetInjectorAPI.tests.listeners.ReceiveListener;
import com.fren_gor.packetInjectorAPI.tests.listeners.SendListener;
import org.bukkit.Bukkit;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.Plugin;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PacketEventManagerTest {

    public static boolean initialized;

    private static Constructor<?> internalListenerConstructor;

    private PacketInjectorAPI api;
    private Plugin pl;
    private TinyProtocol packetHandler;
    private final AbstractListener[] listeners = new AbstractListener[3];
    private Map<Plugin, Set<Object>> MAP_LISTENERS;
    private Set<Object> SEND_LISTENERS, RECEIVE_LISTENERS;

    @BeforeAll
    static void init() throws Exception {
        Class.forName("com.comphenix.tinyprotocol.TinyProtocol");

        assertTrue(initialized, "Cannot load TinyProtocol dummy class for tests.");

        internalListenerConstructor = Class.forName("com.fren_gor.packetInjectorAPI.api.PacketEventManager$RegisteredListener").getConstructor(PacketListener.class);
        internalListenerConstructor.setAccessible(true);
    }

    @BeforeEach
    @SuppressWarnings("unchecked")
    void setUp() throws Exception {
        MockBukkit.mock();
        pl = MockBukkit.createMockPlugin();
        api = new PacketInjectorAPI(pl);

        packetHandler = (TinyProtocol) setAccessible(PacketInjectorAPI.class.getDeclaredField("handler")).get(api);

        MAP_LISTENERS = (Map<Plugin, Set<Object>>) setAccessible(PacketEventManager.class.getDeclaredField("MAP_LISTENERS")).get(api.getEventManager());
        SEND_LISTENERS = (Set<Object>) setAccessible(PacketEventManager.class.getDeclaredField("SEND_LISTENERS")).get(api.getEventManager());
        RECEIVE_LISTENERS = (Set<Object>) setAccessible(PacketEventManager.class.getDeclaredField("RECEIVE_LISTENERS")).get(api.getEventManager());

        listeners[0] = new FullListener();
        listeners[1] = new SendListener();
        listeners[2] = new ReceiveListener();
    }

    private <A extends AccessibleObject> A setAccessible(A a) throws Exception {
        a.setAccessible(true);
        return a;
    }

    @AfterEach
    void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    @DisplayName("Null Tests")
    void testNull() {
        PacketListener listener = new FullListener();

        assertThrows(NullPointerException.class, () -> api.getEventManager().registerPacketListener(null, listener), "Plugin may be null in register");
        assertThrows(NullPointerException.class, () -> api.getEventManager().registerPacketListener(pl, null), "PacketListener may be null in register");
        assertThrows(NullPointerException.class, () -> api.getEventManager().unregisterPacketListener((PacketListener) null), "PacketListener may be null in unregister");
        assertThrows(NullPointerException.class, () -> api.getEventManager().unregisterPacketListener((Plugin) null), "Plugin may be null in unregister");
    }

    @Test
    @DisplayName("Register, Call, and Unregister Test")
    void testRegister() throws Exception {
        for (int i = 0; i < listeners.length; i++)
            internalTestRegister(i);

        packetHandler.onPacketOutAsync(null, new SimpleChannel(), new DummyPacket());
        packetHandler.onPacketInAsync(null, new SimpleChannel(), new DummyPacket());

        for (int i = 0; i < listeners.length; i++)
            internalCallTest(i);

        for (int i = 0; i < listeners.length; i++)
            internalTestUnregister(i);

        assertFalse(MAP_LISTENERS.containsKey(pl), "Plugin hasn't been removed from map");
    }

    void internalTestRegister(int index) throws Exception {
        AbstractListener listener = listeners[index];

        api.getEventManager().registerPacketListener(pl, listener);

        assertTrue(MAP_LISTENERS.containsKey(pl), "Plugin hasn't been registered [" + index + "]");

        Object internalListener = internalListenerConstructor.newInstance(listener);
        assertTrue(MAP_LISTENERS.get(pl).contains(internalListener), "Listener hasn't been registered [" + index + "]");

        assertTrue(listener.checkSendSet(SEND_LISTENERS, internalListener), listener.sendMessage() + " [" + index + "]");
        assertTrue(listener.checkReceiveSet(RECEIVE_LISTENERS, internalListener), listener.receiveMessage() + " [" + index + "]");
    }

    void internalTestUnregister(int index) throws Exception {
        AbstractListener listener = listeners[index];

        api.getEventManager().unregisterPacketListener(listener);

        Object internalListener = internalListenerConstructor.newInstance(listener);
        if (MAP_LISTENERS.containsKey(pl)) {
            assertFalse(MAP_LISTENERS.get(pl).contains(internalListener), "Listener hasn't been removed [" + index + "]");
        }

        assertFalse(SEND_LISTENERS.contains(internalListener), "Send set contains listener when it shouldn't [" + index + "]");
        assertFalse(RECEIVE_LISTENERS.contains(internalListener), "Receive set contains listener when it shouldn't [" + index + "]");
    }

    void internalCallTest(int index) {
        AbstractListener listener = listeners[index];

        listener.checkSendCall();
        listener.checkReceiveCall();
    }

    @Test
    @DisplayName("Plugin unregister Test")
    void testPluginUnregister() throws Exception {
        PacketListener l = new FullListener();
        api.getEventManager().registerPacketListener(pl, l);

        api.getEventManager().unregisterPacketListener(pl);

        assertFalse(MAP_LISTENERS.containsKey(pl), "Plugin hasn't been unregistered");

        Object internalListener = internalListenerConstructor.newInstance(l);
        assertFalse(SEND_LISTENERS.contains(internalListener), "Plugin unregistration left listener in send map");
        assertFalse(RECEIVE_LISTENERS.contains(internalListener), "Plugin unregistration left listener in receive map");
    }

    @Test
    @DisplayName("PluginDisableEvent Test")
    void testPluginDisableEvent() {
        final Plugin p = MockBukkit.createMockPlugin();
        api.getEventManager().registerPacketListener(p, new FullListener());
        Bukkit.getPluginManager().callEvent(new PluginDisableEvent(pl));
        MockBukkit.getMock().getPluginManager().assertEventFired(PluginDisableEvent.class);
        assertFalse(MAP_LISTENERS.containsKey(p), "Plugin hasn't been unregistered by disable event");
    }

    @Test
    @DisplayName("Unregister not-registered plugin Test")
    void testUnregisteredPluginUnregisterEvent() {
        assertDoesNotThrow(() -> api.getEventManager().unregisterPacketListener(MockBukkit.createMockPlugin()), "PacketEventManager#unregisterPacketListener(Plugin) throws exceptions when not-registered plugin gets unregistered");
    }

    @Test
    @DisplayName("Unregister All Test")
    void testUnregisterAll() {
        api.getEventManager().registerPacketListener(MockBukkit.createMockPlugin(), new FullListener());
        for (AbstractListener listener : listeners) {
            api.getEventManager().registerPacketListener(pl, listener);
        }
        api.getEventManager().unregisterEveryPacketListener();

        assertEquals(0, MAP_LISTENERS.size(), "At least one plugin hasn't been unregistered");
        assertEquals(0, SEND_LISTENERS.size(), "At least one send listener hasn't been unregistered");
        assertEquals(0, RECEIVE_LISTENERS.size(), "At least one receive listener hasn't been unregistered");
    }
}
