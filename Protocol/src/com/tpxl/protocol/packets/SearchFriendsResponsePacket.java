package com.tpxl.protocol.packets;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.tpxl.protocol.Packet;
import com.tpxl.protocol.PacketType;

public class SearchFriendsResponsePacket extends Packet {

	String name;
	int ID;
	
	public SearchFriendsResponsePacket(String name, int ID)
	{
		this.name = name;
		this.ID = ID;
	}
	
	@Override
	protected void writeData(OutputStream stream) throws IOException {
		writeString(stream, name);
		writeInt(stream, ID);
	}

	public static SearchFriendsResponsePacket read(InputStream stream) throws IOException
	{
		return new SearchFriendsResponsePacket(readString(stream), readInt(stream));
	}
	
	@Override
	public PacketType getType() {
		return PacketType.SEARCHFRIENDSRESPONSE;
	}

}
