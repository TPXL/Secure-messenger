package com.tpxl.protocol.packets;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.tpxl.protocol.Packet;
import com.tpxl.protocol.PacketType;

public class RegisterPacket extends Packet {

	String username;
	String password;
	
	public RegisterPacket(String username, String password)
	{
		this.username = username;
		this.password = password;
	}
	
	@Override
	protected void writeData(OutputStream stream) throws IOException {
		writeString(stream, username);
		writeString(stream, password);
	}

	public static RegisterPacket read(InputStream stream) throws IOException
	{
		return new RegisterPacket(readString(stream), readString(stream));
	}
	
	@Override
	public PacketType getType() {
		return PacketType.REGISTER;
	}
	
	public String getUsername()
	{
		return username;
	}
	
	public String getPassword()
	{
		return password;
	}
}
