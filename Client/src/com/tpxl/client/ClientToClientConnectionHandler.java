package com.tpxl.client;

import java.net.Socket;

import com.tpxl.protocol.ClientToClientPacketHandler;
import com.tpxl.protocol.Packet;
import com.tpxl.protocol.packets.MessagePacket;

public class ClientToClientConnectionHandler implements ClientToClientPacketHandler, Runnable {

	String messages;
	Socket socket;
	
	ClientToClientConnectionHandler(Socket socket)
	{
		this.socket = socket;
		messages = "";
	}
	
	@Override
	public void run() {
		try
		{
			while(!socket.isClosed() && socket.isConnected())
			{
				Packet.readPacket(this, socket.getInputStream());
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void onPacketReceive(MessagePacket messagePacket) {
		messages = messages+"\n"+messagePacket.getMessage();
	}
}
