package com.tpxl.protocol.packets;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.tpxl.protocol.Packet;
import com.tpxl.protocol.PacketType;

public class ConnectionStartRequestPacket extends Packet {

	String name;
	int ID;
	byte key[];
	
	public ConnectionStartRequestPacket(String name, int ID, byte[] key)
	{
		this.name = name;
		this.ID = ID;
		this.key = key;
	}
	
	@Override
	protected void writeData(OutputStream stream) throws IOException {
		writeString(stream, name);
		writeInt(stream, ID);
		writeInt(stream, key.length);
		writeByteArray(stream, key);
	}
	
	public static ConnectionStartRequestPacket read(InputStream inputStream) throws IOException
	{
		return new ConnectionStartRequestPacket(readString(inputStream), readInt(inputStream), readByteArray(inputStream, readInt(inputStream)));
	}

	@Override
	public PacketType getType() {
		return PacketType.CONNECTIONSTARTREQUEST;
	}

	public String getName()
	{
		return name;
	}
	
	public int getID()
	{
		return ID;
	}
	
	public byte[] getKey()
	{
		return key;
	}
}
