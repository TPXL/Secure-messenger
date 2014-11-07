package com.tpxl.server;

import java.io.IOException;
import java.net.Socket;

import com.tpxl.protocol.Packet;
import com.tpxl.protocol.ServerPacketHandler;
import com.tpxl.protocol.packets.ChangeNicknamePacket;
import com.tpxl.protocol.packets.ConnectionStartRequestPacket;
import com.tpxl.protocol.packets.FriendAddConfirmResponsePacket;
import com.tpxl.protocol.packets.FriendAddPacket;
import com.tpxl.protocol.packets.FriendRemovePacket;
import com.tpxl.protocol.packets.GoodbyePacket;
import com.tpxl.protocol.packets.HelloPacket;
import com.tpxl.protocol.packets.LoginPacket;
import com.tpxl.protocol.packets.MessagePacket;
import com.tpxl.protocol.packets.RegisterPacket;
import com.tpxl.protocol.packets.SearchFriendsPacket;

public class ServerConnectionHandler implements Runnable, ServerPacketHandler{

	public Socket socket;
	
	public ServerConnectionHandler(Socket socket)
	{
		this.socket = socket;
	}
	
	public void run() {
		if(socket == null)
			return;
		else if(socket.isClosed())
		{
			System.out.println("Socket closed");
			return;
		}
		while(!socket.isClosed())
		{
			try
			{
				Packet.readPacket(this, socket.getInputStream());
			}catch(Exception e)
			{
				System.out.println(e.getMessage());
				e.printStackTrace();
			}
		}
		System.out.println("Connection closed!");
	}
	@Override
	public void onPacketReceive(HelloPacket helloPacket) {
		System.out.println("HelloPacket: " + helloPacket.hello + " " + helloPacket.version);
		MessagePacket mp = null;
		boolean close = false;
		if(helloPacket.hello != Packet.getHelloInt())
		{
			System.out.println("Wrong hello int.");
			mp = new MessagePacket("Wrong hello int.");
			close = true;
		}
		else if(helloPacket.version != Packet.getProtocolVersion())
		{
			System.out.println("Wrong protocol version.");
			mp = new MessagePacket("Wrong protocol version int.");
			close = true;
		}
		else
			mp = new MessagePacket("HelloSuccessfull");
		
		try {
			mp.write(socket.getOutputStream());
		} catch (IOException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		if(close)
		{
			try {
				socket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onPacketReceive(LoginPacket loginPacket) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onPacketReceive(RegisterPacket registerPacket) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onPacketReceive(SearchFriendsPacket searchFriendsPacket) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onPacketReceive(FriendAddPacket friendAddPacket) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onPacketReceive(FriendRemovePacket friendRemovePacket) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onPacketReceive(
			FriendAddConfirmResponsePacket friendAddConfirmResponsePacket) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onPacketReceive(GoodbyePacket goodbyePacket) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onPacketReceive(ChangeNicknamePacket changeNicknamePacket) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onPacketReceive(
			ConnectionStartRequestPacket connectionStartRequestPacket) {
		// TODO Auto-generated method stub
	}
}
