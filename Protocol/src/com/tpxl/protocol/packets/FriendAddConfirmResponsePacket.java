package com.tpxl.protocol.packets;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.tpxl.protocol.Packet;
import com.tpxl.protocol.PacketType;

public class FriendAddConfirmResponsePacket extends Packet {

	boolean success;
	String name;
	String nickname;
	int ID;
	
	public FriendAddConfirmResponsePacket(boolean success, String name, String nickname, int ID) {
		this.success = success;
		this.name = name;
		this.nickname = nickname;
		this.ID = ID;
	}
	
	@Override
	protected void writeData(OutputStream stream) throws IOException {
		writeBoolean(stream, success);
		writeString(stream, name);
		writeString(stream, nickname);
		writeInt(stream, ID);
	}

	public static FriendAddConfirmResponsePacket read(InputStream inputStream) throws IOException
	{
		return new FriendAddConfirmResponsePacket(readBoolean(inputStream), readString(inputStream), readString(inputStream), readInt(inputStream));
	}
	
	@Override
	public PacketType getType() {
		return PacketType.FRIENDADDCONFIRMRESPONSE;
	}
	
	public boolean getSuccess()
	{
		return success;
	}
	
	public String getName()
	{
		return name;
	}
	
	public int getID()
	{
		return ID;
	}
	
	public String getNickname()
	{
		return nickname;
	}
}
