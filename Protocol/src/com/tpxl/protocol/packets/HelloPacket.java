package com.tpxl.protocol.packets;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.tpxl.protocol.Packet;
import com.tpxl.protocol.PacketType;

public class HelloPacket extends Packet {

	public int hello;
	public short version;
	
	@Override
	protected void writeData(OutputStream stream) throws IOException {
		writeInt(stream, hello);
		writeShort(stream, version);
	}

	public HelloPacket(int hello, short version)
	{
		this.hello = hello;
		this.version = version;
	}
	
	public static HelloPacket read(InputStream inputStream) throws IOException
	{
		return new HelloPacket(readInt(inputStream), readShort(inputStream));
	}
	
	@Override
	public PacketType getType() {
		return PacketType.HELLO;
	}

}
