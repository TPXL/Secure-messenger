package com.tpxl.protocol.packets;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.tpxl.protocol.Packet;
import com.tpxl.protocol.PacketType;

public class ConnectionStartInfoPacket extends Packet {

	String IP;
	
	public ConnectionStartInfoPacket(String IP) {
		this.IP = IP;
	}
	
	@Override
	protected void writeData(OutputStream stream) throws IOException {
		writeString(stream, IP);
	}

	ConnectionStartInfoPacket read(InputStream inputStream) throws IOException
	{
		return new ConnectionStartInfoPacket(readString(inputStream));
	}
	
	@Override
	public PacketType getType() {
		return PacketType.CONNECTIONSTARTINFO;
	}

}
