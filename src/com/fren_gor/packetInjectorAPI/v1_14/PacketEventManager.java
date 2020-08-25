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

package com.fren_gor.packetInjectorAPI.v1_14;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.fren_gor.packetInjectorAPI.events.PacketRetriveEvent;
import com.fren_gor.packetInjectorAPI.events.PacketSendEvent;

import lombok.experimental.UtilityClass;

@UtilityClass
public class PacketEventManager {

	private final static List<PacketSendListener> SEND_LISTENER = new LinkedList<>();
	private final static List<PacketRetriveListener> RETRIVE_LISTENER = new LinkedList<>();
	private final static Object SEND_LOCKER = new Object();
	private final static Object RETRIVE_LOCKER = new Object();

	/**
	 * Register a packet send listener
	 * 
	 * @param listener
	 *            The listener instance
	 */
	public static void registerSendListener(PacketSendListener listener) {
		synchronized (SEND_LOCKER) {
			for (PacketSendListener s : SEND_LISTENER) {
				if (s == listener)
					return;
			}
			SEND_LISTENER.add(listener);
		}
	}

	/**
	 * Register a packet retrive listener
	 * 
	 * @param listener
	 *            The listener instance
	 */
	public static void registerRetriveListener(PacketRetriveListener listener) {
		synchronized (RETRIVE_LOCKER) {
			for (PacketRetriveListener s : RETRIVE_LISTENER) {
				if (s == listener)
					return;
			}
			RETRIVE_LISTENER.add(listener);
		}
	}

	/**
	 * Unregister a packet send listener
	 * 
	 * @param listener
	 *            The listener instance
	 */
	public static void unregisterSendListener(PacketSendListener listener) {
		synchronized (SEND_LOCKER) {
			Iterator<PacketSendListener> it = SEND_LISTENER.iterator();
			while (it.hasNext()) {
				PacketSendListener l = it.next();
				if (l == listener) {
					it.remove();
					return;
				}
			}
		}
	}

	/**
	 * Unregister a packet retrive listener
	 * 
	 * @param listener
	 *            The listener instance
	 */
	public static void unregisterRetriveListener(PacketRetriveListener listener) {
		synchronized (RETRIVE_LOCKER) {
			Iterator<PacketRetriveListener> it = RETRIVE_LISTENER.iterator();
			while (it.hasNext()) {
				PacketRetriveListener l = it.next();
				if (l == listener) {
					it.remove();
					return;
				}
			}
		}
	}

	static void invokeSendListeners(PacketSendEvent e) {
		synchronized (SEND_LOCKER) {
			for (PacketSendListener l : SEND_LISTENER) {
				l.onSend(e);
			}
		}

	}

	static void invokeSendListeners(PacketSendEvent_v1_14 e) {
		synchronized (SEND_LOCKER) {
			for (PacketSendListener l : SEND_LISTENER) {
				l.onSend(e);
			}
		}

	}

	static void invokeRetriveListeners(PacketRetriveEvent e) {
		synchronized (RETRIVE_LOCKER) {
			for (PacketRetriveListener l : RETRIVE_LISTENER) {
				l.onRetrive(e);
			}
		}
	}

	static void invokeRetriveListeners(PacketRetriveEvent_v1_14 e) {
		synchronized (RETRIVE_LOCKER) {
			for (PacketRetriveListener l : RETRIVE_LISTENER) {
				l.onRetrive(e);
			}
		}
	}

}
