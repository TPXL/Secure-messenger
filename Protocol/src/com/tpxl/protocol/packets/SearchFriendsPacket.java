package com.tpxl.protocol.packets;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.tpxl.protocol.Packet;
import com.tpxl.protocol.PacketType;

public class SearchFriendsPacket extends Packet {

	String name;
	
	public SearchFriendsPacket(String name)
	{
		this.name = name;
	}
	
	protected void writeData(OutputStream stream) throws IOException {
		writeString(stream, name);
	}

	public static SearchFriendsPacket read(InputStream stream) throws IOException
	{
		return new SearchFriendsPacket(readString(stream));
	}
	
	@Override
	public PacketType getType() {
		return PacketType.SEARCHFRIENDS;
	}

}
