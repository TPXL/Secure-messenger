package com.tpxl.protocol.packets;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.tpxl.protocol.Packet;
import com.tpxl.protocol.PacketType;

public class LoginStatusPacket extends Packet {

	boolean success;
	String message;
	int ID;
	
	public LoginStatusPacket(boolean success, String message, int ID) {
		this.success = success;
		this.message = message;
		this.ID = ID;
	}
	
	protected void writeData(OutputStream stream) throws IOException {
		writeBoolean(stream, success);
		writeString(stream, message);
		writeInt(stream, ID);
	}

	public static LoginStatusPacket read(InputStream stream) throws IOException
	{
		return new LoginStatusPacket(readBoolean(stream), readString(stream), readInt(stream));
	}
	
	@Override
	public PacketType getType() {
		return PacketType.LOGINSTATUS;
	}
	
	public boolean getSuccess()
	{
		return success;
	}
	
	public String getMessage()
	{
		return message;
	}
	
	public int getID()
	{
		return ID;
	}

}
