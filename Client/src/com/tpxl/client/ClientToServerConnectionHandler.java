package com.tpxl.client;

import java.net.Socket;

import com.tpxl.protocol.ClientPacketHandler;
import com.tpxl.protocol.Packet;
import com.tpxl.protocol.packets.ConnectionStartPacket;
import com.tpxl.protocol.packets.FriendAddConfirmPacket;
import com.tpxl.protocol.packets.FriendListPacket;
import com.tpxl.protocol.packets.GoodbyePacket;
import com.tpxl.protocol.packets.HelloPacket;
import com.tpxl.protocol.packets.HelloStatusPacket;
import com.tpxl.protocol.packets.LoginStatusPacket;
import com.tpxl.protocol.packets.MessagePacket;
import com.tpxl.protocol.packets.RegisterPacket;
import com.tpxl.protocol.packets.RegisterStatusPacket;
import com.tpxl.protocol.packets.SearchFriendsResponsePacket;

public class ClientToServerConnectionHandler implements Runnable, ClientPacketHandler{

	Socket socket;
	
	ClientToServerConnectionHandler(Socket socket)
	{
		this.socket = socket;
	}
	
	@Override
	public void run() {
		try
		{
			new HelloPacket(Packet.getHelloInt(), Packet.getProtocolVersion()).write(socket.getOutputStream());
			new RegisterPacket("user2", "password").write(socket.getOutputStream());
			while(!socket.isClosed() && socket.isConnected())
			{
				Packet.readPacket(this, socket.getInputStream());
			}
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void onPacketReceive(MessagePacket messagePacket) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPacketReceive(HelloStatusPacket helloStatusPacket) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPacketReceive(LoginStatusPacket loginStatusPacket) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPacketReceive(RegisterStatusPacket registerStatusPacket) {
		System.out.println("Success: " + registerStatusPacket.getSuccess() + "(" + registerStatusPacket.getMessage() + ")");
	}

	@Override
	public void onPacketReceive(
			SearchFriendsResponsePacket searchFriendsResponsePacket) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPacketReceive(FriendAddConfirmPacket friendAddConfirmPacket) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPacketReceive(GoodbyePacket goodbyePacket) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPacketReceive(FriendListPacket friendListPacket) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPacketReceive(ConnectionStartPacket connectionStartPacket) {
		// TODO Auto-generated method stub
		
	}

}
