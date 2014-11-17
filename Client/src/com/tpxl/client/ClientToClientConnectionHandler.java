package com.tpxl.client;

import java.net.Socket;

import com.tpxl.protocol.ClientToClientPacketHandler;
import com.tpxl.protocol.Packet;
import com.tpxl.protocol.packets.MessagePacket;

public class ClientToClientConnectionHandler implements ClientToClientPacketHandler, Runnable {

	String messages;
	Socket socket;
	
        String myUsername;
        String otherUsername;
        
	MessagingFrame messagingFrame;
        
        ClientToClientConnectionHandler(Socket socket, String myUsername, String otherUsername)
	{
		this.socket = socket;
		messages = "";
                messagingFrame = new MessagingFrame();
                this.myUsername = myUsername;
                this.otherUsername = otherUsername;
	}
	
	@Override
	public void run() {
                messagingFrame.clientToClientConnectionHandler = this;
                messagingFrame.setVisible(true);
		System.out.println("P2P connection started!");
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
        
        public void sendMessagePacket(String message)
        {

            messagingFrame.textArea.setText(messagingFrame.textArea.getText() + myUsername + ": " + message +"\n");
            try
            {
                new MessagePacket(message).write(socket.getOutputStream());
            }catch(Exception e)
            {
                e.printStackTrace();
            }
        }
	
	public void onPacketReceive(MessagePacket messagePacket) {
            messagingFrame.textArea.setText(messagingFrame.textArea.getText() +otherUsername + ": " + messagePacket.getMessage()+ "\n");
	}
}
