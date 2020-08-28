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

import static org.junit.jupiter.api.Assertions.*;

import java.util.Set;

import com.fren_gor.packetInjectorAPI.events.PacketListener;

public class FullListener extends AbstractListener {

	@Override
	public void checkSendCall() {
		assertTrue(sendCalled, "SendEvent hasn't been called when it should");
	}

	@Override
	public void checkRetriveCall() {
		assertTrue(retriveCalled, "RetriveEvent hasn't been called when it should");
	}

	@Override
	public boolean checkSendSet(Set<PacketListener> set) {
		return set.contains(this);
	}

	@Override
	public boolean checkRetriveSet(Set<PacketListener> set) {
		return set.contains(this);
	}
	
	@Override
	public String sendMessage() {
		return "Send set doesn't contains listener when it should";
	}

	@Override
	public String retriveMessage() {
		return "Retrive set doesn't contains listener when it should";
	}

}
