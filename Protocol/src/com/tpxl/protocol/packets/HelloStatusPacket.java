package com.tpxl.protocol.packets;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.tpxl.protocol.Packet;
import com.tpxl.protocol.PacketType;

public class HelloStatusPacket extends Packet {

	boolean success;
	String message;
	
	public HelloStatusPacket(boolean success, String message) {
		this.success = success;
		this.message = message;
	}
	
	@Override
	protected void writeData(OutputStream stream) throws IOException {
		writeBoolean(stream, success);
		writeString(stream, message);
	}

	public static HelloStatusPacket read(InputStream stream) throws IOException
	{
		return new HelloStatusPacket(readBoolean(stream), readString(stream));
	}
	
	@Override
	public PacketType getType() {
		return PacketType.HELLOSTATUS;
	}

}
