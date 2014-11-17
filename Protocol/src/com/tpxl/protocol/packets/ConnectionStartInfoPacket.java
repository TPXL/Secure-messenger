package com.tpxl.protocol.packets;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.tpxl.protocol.Packet;
import com.tpxl.protocol.PacketType;

public class ConnectionStartInfoPacket extends Packet {

	String IP;
	byte[] key;
        String name;
	
	public ConnectionStartInfoPacket(String IP, String name, byte[] key) {
		this.IP = IP;
                this.name = name;
		this.key = key;
	}
	
	@Override
	protected void writeData(OutputStream stream) throws IOException {
		writeString(stream, IP);
                writeString(stream, name);
		writeInt(stream, key.length);
		writeByteArray(stream, key);
	}

	public static ConnectionStartInfoPacket read(InputStream inputStream) throws IOException
	{
		return new ConnectionStartInfoPacket(readString(inputStream), readString(inputStream), readByteArray(inputStream, readInt(inputStream)));
	}
	
	@Override
	public PacketType getType() {
		return PacketType.CONNECTIONSTARTINFO;
	}
	
	public String getIP()
	{
		return IP;
	}

	public byte[] getKey()
	{
		return key;
	}
        
        public String getName()
        {
            return name;
        }
}
