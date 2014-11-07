package com.tpxl.protocol.packets;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.tpxl.protocol.Packet;
import com.tpxl.protocol.PacketType;

public class GoodbyePacket extends Packet {

	String message;
	
	public GoodbyePacket(String message)
	{
		this.message = message;
	}
	
	@Override
	protected void writeData(OutputStream stream) throws IOException {
		writeString(stream, message);
	}

	public static GoodbyePacket read(InputStream stream) throws IOException
	{
		return new GoodbyePacket(readString(stream));
	}
	
	@Override
	public PacketType getType() {
		return PacketType.GOODBYE;
	}

}
