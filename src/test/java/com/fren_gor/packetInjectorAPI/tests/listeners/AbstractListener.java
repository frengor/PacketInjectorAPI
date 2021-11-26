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

package com.fren_gor.packetInjectorAPI.tests.listeners;

import java.util.Set;

import com.fren_gor.packetInjectorAPI.api.events.PacketReceiveEvent;
import com.fren_gor.packetInjectorAPI.api.events.PacketSendEvent;
import com.fren_gor.packetInjectorAPI.api.listeners.PacketListener;

public abstract class AbstractListener implements PacketListener {

	protected boolean sendCalled, receiveCalled;

	@Override
	public void onSend(PacketSendEvent event) {
		sendCalled = true;
	}

	@Override
	public void onReceive(PacketReceiveEvent event) {
		receiveCalled = true;
	}

	public abstract void checkSendCall();

	public abstract void checkReceiveCall();

	public abstract boolean checkSendSet(Set<Object> set, Object internalListener);

	public abstract boolean checkReceiveSet(Set<Object> set, Object internalListener);

	public abstract String sendMessage();

	public abstract String receiveMessage();

}
