package com.tpxl.client;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.Socket;
import java.security.KeyStore;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import com.tpxl.protocol.ClientToServerPacketHandler;
import com.tpxl.protocol.Packet;
import com.tpxl.protocol.packets.ChangeNicknamePacket;
import com.tpxl.protocol.packets.ConnectionStartInfoPacket;
import com.tpxl.protocol.packets.ConnectionStartPacket;
import com.tpxl.protocol.packets.FriendAddConfirmPacket;
import com.tpxl.protocol.packets.FriendAddPacket;
import com.tpxl.protocol.packets.FriendListPacket;
import com.tpxl.protocol.packets.FriendRemovePacket;
import com.tpxl.protocol.packets.GoodbyePacket;
import com.tpxl.protocol.packets.HelloPacket;
import com.tpxl.protocol.packets.HelloStatusPacket;
import com.tpxl.protocol.packets.LoginPacket;
import com.tpxl.protocol.packets.LoginStatusPacket;
import com.tpxl.protocol.packets.MessagePacket;
import com.tpxl.protocol.packets.RegisterStatusPacket;
import com.tpxl.protocol.packets.SearchFriendsResponsePacket;
import com.tpxl.protocol.packets.ConnectionStartRequestPacket;

public class ClientToServerConnectionHandler implements Runnable, ClientToServerPacketHandler{

	Socket socket;
	
	static String publicKeyFile = "pubkey";
	static String privateKeyFiel = "privkey";
	
	ClientToServerConnectionHandler(Socket socket)
	{
		this.socket = socket;
	}
	
	@Override
	public void run() {
		try
		{
			
			new HelloPacket(Packet.getHelloInt(), Packet.getProtocolVersion()).write(socket.getOutputStream());
			boolean acceptor = false;
			if(acceptor)
			{
				new LoginPacket("user1", "password").write(socket.getOutputStream());
				File f = new File(publicKeyFile);
				DataInputStream dis = new DataInputStream(new FileInputStream(f));
				byte[] barray = new byte[(int)f.length()];
				dis.readFully(barray);
				new ConnectionStartRequestPacket("user2", 3, barray);
				
				SSLServerSocketFactory socketFactory = null;
				try
				{
					SSLContext sslContext = SSLContext.getInstance( "TLS" );
			        KeyManagerFactory km = KeyManagerFactory.getInstance("SunX509");
			        KeyStore ks = KeyStore.getInstance("JKS");
			        ks.load(new FileInputStream("server.jks"), "123456".toCharArray());
			        km.init(ks, "123456".toCharArray());
			        sslContext.init(km.getKeyManagers(), null, null);
			        socketFactory = sslContext.getServerSocketFactory();
				}catch(Exception e)
				{
					e.printStackTrace();
				}
				
				SSLServerSocket AcceptingSocket = (SSLServerSocket)socketFactory.createServerSocket(Client.clientPort);
				Socket sock = AcceptingSocket.accept();
				
				new MessagePacket("Es ist werken!").write(sock.getOutputStream());
			}else
			{
				new LoginPacket("user2", "password").write(socket.getOutputStream());
			}
			
			
			
			
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
		System.out.println("GOT A NEW FUCKING MESSAGE!" + messagePacket.getMessage());
	}

	@Override
	public void onPacketReceive(HelloStatusPacket helloStatusPacket) {
		// TODO Auto-generated method stub
		System.out.println(helloStatusPacket.getMessage());
	}

	@Override
	public void onPacketReceive(LoginStatusPacket loginStatusPacket) {
		// TODO Auto-generated method stub
		System.out.println("Success: " + loginStatusPacket.getSuccess() + "(" + loginStatusPacket.getMessage() + " " + loginStatusPacket.getID() + ")");
	}

	@Override
	public void onPacketReceive(RegisterStatusPacket registerStatusPacket) {
		System.out.println("Success: " + registerStatusPacket.getSuccess() + "(" + registerStatusPacket.getMessage() + ")");
	}

	@Override
	public void onPacketReceive(SearchFriendsResponsePacket searchFriendsResponsePacket) {
		// TODO Auto-generated method stub
		System.out.println(searchFriendsResponsePacket.getName() + " " + searchFriendsResponsePacket.getID());
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

	}

	@Override
	public void onPacketReceive(ConnectionStartInfoPacket connectionStartInfoPacket) {
		try
		{
			SSLContext sslContext = SSLContext.getInstance( "TLS" );
			KeyStore clientks = KeyStore.getInstance("JKS");
			clientks.load(new ByteArrayInputStream(connectionStartInfoPacket.getKey()), "123456".toCharArray());
			TrustManagerFactory tm = TrustManagerFactory.getInstance("SunX509");
			tm.init(clientks);
			sslContext.init(null, tm.getTrustManagers(), null);
			SSLSocketFactory socketFactory = sslContext.getSocketFactory();
			
			SSLSocket socket = (SSLSocket )socketFactory.createSocket(connectionStartInfoPacket.getIP(), Client.clientPort);
			
			new Thread(new ClientToClientConnectionHandler(socket)).start();
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}

}
