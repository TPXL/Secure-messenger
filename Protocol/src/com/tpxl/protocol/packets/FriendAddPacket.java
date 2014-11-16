package com.tpxl.protocol.packets;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.tpxl.protocol.Packet;
import com.tpxl.protocol.PacketType;

public class FriendAddPacket extends Packet {

	String name;
	int ID;
	
	public FriendAddPacket(String name, int ID) {
		this.name = name;
		this.ID = ID;
	}
	
	@Override
	protected void writeData(OutputStream stream) throws IOException {
		writeString(stream, name);
		writeInt(stream, ID);
	}

	public static FriendAddPacket read(InputStream inputStream) throws IOException
	{
		return new FriendAddPacket(readString(inputStream), readInt(inputStream));
	}
	
	@Override
	public PacketType getType() {
		return PacketType.FRIENDADD;
	}

	public String getName()
	{
		return name;
	}
	
	public int getID()
	{
		return ID;
	}
}
