package com.tpxl.protocol;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

import com.tpxl.protocol.packets.ChangeNicknamePacket;
import com.tpxl.protocol.packets.ConnectionStartPacket;
import com.tpxl.protocol.packets.ConnectionStartRequestPacket;
import com.tpxl.protocol.packets.FriendAddConfirmPacket;
import com.tpxl.protocol.packets.FriendAddConfirmResponsePacket;
import com.tpxl.protocol.packets.FriendAddPacket;
import com.tpxl.protocol.packets.FriendListPacket;
import com.tpxl.protocol.packets.FriendRemovePacket;
import com.tpxl.protocol.packets.GoodbyePacket;
import com.tpxl.protocol.packets.HelloPacket;
import com.tpxl.protocol.packets.HelloStatusPacket;
import com.tpxl.protocol.packets.LoginPacket;
import com.tpxl.protocol.packets.LoginStatusPacket;
import com.tpxl.protocol.packets.MessagePacket;
import com.tpxl.protocol.packets.RegisterPacket;
import com.tpxl.protocol.packets.RegisterStatusPacket;
import com.tpxl.protocol.packets.SearchFriendsPacket;
import com.tpxl.protocol.packets.SearchFriendsResponsePacket;

public abstract class Packet {
	
	static final short version = 1;
	protected static final int helloInt = 1234567890;
		
	private final static Charset STR_CHARSET = Charset.forName("UTF-8");
	
	protected abstract void writeData(OutputStream stream) throws IOException;
	
	public abstract PacketType getType();
	
	public static int getHelloInt()
	{
		return helloInt;
	}
	
	public static short getProtocolVersion()
	{
		return version;
	}
	
	public static void readPacket(ServerPacketHandler ph, InputStream stream) throws IOException
	{
		short ID = readShort(stream);
		if(ID == PacketType.HELLO.getCode())
		{
			ph.onPacketReceive(HelloPacket.read(stream));
		}
		else if(ID == PacketType.LOGIN.getCode())
		{
			ph.onPacketReceive(LoginPacket.read(stream));
		}
		else if(ID == PacketType.REGISTER.getCode())
		{
			ph.onPacketReceive(RegisterPacket.read(stream));
		}
		else if(ID == PacketType.SEARCHFRIENDS.getCode())
		{
			ph.onPacketReceive(SearchFriendsPacket.read(stream));
		}
		else if(ID == PacketType.FRIENDADD.getCode())
		{
			ph.onPacketReceive(FriendAddPacket.read(stream));
		}
		else if(ID == PacketType.FRIENDREMOVE.getCode())
		{
			ph.onPacketReceive(FriendRemovePacket.read(stream));
		}
		else if(ID == PacketType.FRIENDADDCONFIRMRESPONSE.getCode())
		{
			ph.onPacketReceive(FriendAddConfirmResponsePacket.read(stream));
		}
		else if(ID == PacketType.CHANGENICKNAME.getCode())
		{
			ph.onPacketReceive(ChangeNicknamePacket.read(stream));
		}
		else if(ID == PacketType.CONNECTIONSTARTREQUEST.getCode())
		{
			ph.onPacketReceive(ConnectionStartRequestPacket.read(stream));
		}
		else if(ID == PacketType.GOODBYE.getCode())
		{
			ph.onPacketReceive(GoodbyePacket.read(stream));
		}
		else
		{
			//invalid ID
		}
	}
	
	public static void readPacket(ClientToServerPacketHandler ph, InputStream stream) throws IOException
	{
		short ID = readShort(stream);
		if(ID == PacketType.HELLOSTATUS.getCode())
		{
			ph.onPacketReceive(HelloStatusPacket.read(stream));
		}
		else if(ID == PacketType.LOGINSTATUS.getCode())
		{
			ph.onPacketReceive(LoginStatusPacket.read(stream));
		}
		else if(ID == PacketType.REGISTERSTATUS.getCode())
		{
			ph.onPacketReceive(RegisterStatusPacket.read(stream));
		}
		else if(ID == PacketType.SEARCHFRIENDSRESPONSE.getCode())
		{
			ph.onPacketReceive(SearchFriendsResponsePacket.read(stream));
		}
		else if(ID == PacketType.MESSAGE.getCode())
		{
			ph.onPacketReceive(MessagePacket.read(stream));
		}
		else if(ID == PacketType.FRIENDADDCONFIRM.getCode())
		{
			ph.onPacketReceive(FriendAddConfirmPacket.read(stream));
		}
		else if(ID == PacketType.FRIENDLIST.getCode())
		{
			ph.onPacketReceive(FriendListPacket.read(stream));
		}
		else if(ID == PacketType.CONNECTIONSTARTINFO.getCode())
		{
			ph.onPacketReceive(FriendListPacket.read(stream));
		}
		else if(ID == PacketType.CONNECTIONSTART.getCode())
		{
			ph.onPacketReceive(ConnectionStartPacket.read(stream));
		}
		else
		{
			//invalid ID
		}
	}

	public static void readPacket(ClientToClientPacketHandler ph, InputStream stream) throws IOException
	{
		short ID = readShort(stream);
		if(ID == PacketType.MESSAGE.getCode())
		{
			ph.onPacketReceive(MessagePacket.read(stream));
		}
	}
	
	
	public void write(OutputStream stream) throws IOException
	{
		writeShort(stream, getType().getCode());
		writeData(stream);
	}
	
	protected static void writeBoolean(OutputStream stream, boolean flag) throws IOException
	{
		if(flag)
			stream.write((byte)1);
		else
			stream.write((byte)0);
	}
	
	protected static boolean readBoolean(InputStream stream) throws IOException
	{
		int data = stream.read();
		if(data == 0)
			return false;
		else
			return true;
	}
	
	protected static void writeByte(OutputStream stream, byte data) throws IOException {
		stream.write(data);
	}
	
	protected static byte readByte(InputStream stream) throws IOException {
		int data = stream.read();
		if (data < 0) throw new EOFException();
		return (byte) data;
	}
	
	protected static void writeByteArray(OutputStream stream, byte[] data) throws IOException {
		stream.write(data);
	}
	
	protected static byte[] readByteArray(InputStream stream, int size) throws IOException {
		byte[] data = new byte[size];
		int read = 0;
		while (read < data.length) {
			int ret = stream.read(data, read, data.length - read);
			if (ret < 0) throw new EOFException();
			read += ret;
		}
		return data;
	}
	
	protected static void writeShort(OutputStream stream, short data) throws IOException {
		byte[] b = new byte[2];
		b[0] = (byte) ((data >> 8) & 0xFF);
		b[1] = (byte) ((data >> 0) & 0xFF);
		stream.write(b);
	}
	
	protected static short readShort(InputStream stream) throws IOException {
		byte[] b = readByteArray(stream, 2);
		short data = 0;
		data += (((short) b[0]) & 0xff) << 8;
		data += (((short) b[1]) & 0xff) << 0;
		return data;
	}
	
	protected static void writeInt(OutputStream stream, int data) throws IOException {
		byte[] b = new byte[4];
		b[0] = (byte) ((data >> 24) & 0xFF);
		b[1] = (byte) ((data >> 16) & 0xFF);
		b[2] = (byte) ((data >>  8) & 0xFF);
		b[3] = (byte) ((data >>  0) & 0xFF);
		stream.write(b);
	}
	
	protected static int readInt(InputStream stream) throws IOException {
		byte[] b = readByteArray(stream, 4);
		int data = 0;
		data += (((int) b[0])&0xFF) << 24;
		data += (((int) b[1])&0xFF) << 16;
		data += (((int) b[2])&0xFF) << 8;
		data += (((int) b[3])&0xFF) << 0;
		return data;
	}
	
	protected static void writeString(OutputStream stream, String data) throws IOException {
		byte[] str = data.getBytes(STR_CHARSET);
		writeShort(stream, (short) str.length);
		stream.write(str);
	}
	
	protected static String readString(InputStream stream) throws IOException {
		return new String(readByteArray(stream, readShort(stream)), STR_CHARSET);
	}
	
}