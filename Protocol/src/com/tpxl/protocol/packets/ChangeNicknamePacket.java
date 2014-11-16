package com.tpxl.protocol.packets;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.tpxl.protocol.Packet;
import com.tpxl.protocol.PacketType;

public class ChangeNicknamePacket extends Packet {

	String nickname;
	
	public ChangeNicknamePacket(String nickname)
	{
		this.nickname = nickname;
	}
	
	public static ChangeNicknamePacket read(InputStream inputStream) throws IOException
	{
		return new ChangeNicknamePacket(readString(inputStream));
	}
	
	@Override
	protected void writeData(OutputStream stream) throws IOException {
		writeString(stream, nickname);
	}
	
	@Override
	public PacketType getType() {
		return PacketType.CHANGENICKNAME;
	}
	
	public String getNickname()
	{
		return nickname;
	}

}
