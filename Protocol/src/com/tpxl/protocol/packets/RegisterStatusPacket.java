package com.tpxl.protocol.packets;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.tpxl.protocol.Packet;
import com.tpxl.protocol.PacketType;

public class RegisterStatusPacket extends Packet {

	boolean success;
	String message;
	
	public RegisterStatusPacket(boolean success, String message)
	{
		this.success = success;
		this.message = message;
	}
	
	@Override
	protected void writeData(OutputStream stream) throws IOException {
		writeBoolean(stream, success);
		writeString(stream, message);
	}

	public static RegisterStatusPacket read(InputStream stream) throws IOException
	{
		return new RegisterStatusPacket(readBoolean(stream), readString(stream));
	}
	
	@Override
	public PacketType getType() {
		return PacketType.REGISTERSTATUS;
	}

}
