package com.tpxl.protocol.packets;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.tpxl.protocol.Packet;
import com.tpxl.protocol.PacketType;

public class ConnectionStartRequestPacket extends Packet {

	String name;
	int ID;
	
	ConnectionStartRequestPacket(String name, int ID)
	{
		this.name = name;
		this.ID = ID;
	}
	
	@Override
	protected void writeData(OutputStream stream) throws IOException {
		writeString(stream, name);
		writeInt(stream, ID);
	}
	
	public static ConnectionStartRequestPacket read(InputStream inputStream) throws IOException
	{
		return new ConnectionStartRequestPacket(readString(inputStream), readInt(inputStream));
	}

	@Override
	public PacketType getType() {
		return PacketType.CONNECTIONSTARTREQUEST;
	}

}
