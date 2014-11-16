package com.tpxl.protocol.packets;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.tpxl.protocol.Packet;
import com.tpxl.protocol.PacketType;

public class FriendListPacket extends Packet {

	String name;
	String nickname;
	boolean online;
	int ID;
	
	public FriendListPacket(String name, String nickname, int ID, boolean online) {
		this.name = name;
		this.ID = ID;
		this.online = online;
		this.nickname = nickname;
	}
	
	@Override
	protected void writeData(OutputStream stream) throws IOException {
		writeString(stream, name);
		writeString(stream, nickname);
		writeInt(stream, ID);
		writeBoolean(stream, online);
		
	}

	public static FriendListPacket read(InputStream inputStream) throws IOException
	{
		return new FriendListPacket(readString(inputStream), readString(inputStream), readInt(inputStream), readBoolean(inputStream));
	}
	
	@Override
	public PacketType getType() {
		return PacketType.FRIENDLIST;
	}

	public String getName()
	{
		return name;
	}
	
	public String getNickname()
	{
		return nickname;
	}
	
	public boolean getOnline()
	{
		return online;
	}
	
	public int getID()
	{
		return ID;
	}
}
