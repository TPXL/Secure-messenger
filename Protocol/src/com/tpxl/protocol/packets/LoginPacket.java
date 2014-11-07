package com.tpxl.protocol.packets;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.tpxl.protocol.Packet;
import com.tpxl.protocol.PacketType;

public class LoginPacket extends Packet {

	String username;
	String password;
	
	LoginPacket(String username, String password)
	{
		this.username = username;
		this.password = password;
	}
	
	public static LoginPacket read(InputStream inputStream) throws IOException
	{
		return new LoginPacket(readString(inputStream), readString(inputStream));
	}
	
	@Override
	protected void writeData(OutputStream stream) throws IOException {
		writeString(stream, username);
		writeString(stream, password);
	}

	@Override
	public PacketType getType() {
		return PacketType.LOGIN;
	}

}
