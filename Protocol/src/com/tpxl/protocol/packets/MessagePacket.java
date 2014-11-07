package com.tpxl.protocol.packets;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.tpxl.protocol.Packet;
import com.tpxl.protocol.PacketType;

public class MessagePacket extends Packet {

	public String message;
	
	public MessagePacket(String message) 
	{ 
		this.message = message;
	}
	
	public static MessagePacket read(InputStream inputStream) throws IOException
	{
		return new MessagePacket(readString(inputStream));
	}
	
	@Override
	protected void writeData(OutputStream stream) throws IOException {
		writeString(stream, message);
	}
	@Override
	
	public PacketType getType() {
		return PacketType.MESSAGE;
	}

}
