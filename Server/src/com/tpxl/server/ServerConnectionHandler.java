package com.tpxl.server;

import java.io.IOException;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

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
import com.tpxl.protocol.packets.RegisterStatusPacket;
import com.tpxl.protocol.packets.SearchFriendsPacket;

public class ServerConnectionHandler implements Runnable, ServerPacketHandler{

	static String database_username = "messenger_admin";
	static String database_password = "password1234";
	static String database_url = "jdbc:mysql://localhost:3306/messenger_users"; 
	
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
		while(socket.isConnected() && !socket.isClosed())
		{
			try
			{
				Packet.readPacket(this, socket.getInputStream());
			}catch(Exception e)
			{
				System.out.println(e.getMessage());
				e.printStackTrace();
				break;
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
		String pass = registerPacket.getPassword();
		String user = registerPacket.getUsername();
		
		//Database connection
		try
		{
			Connection databaseConnection = DriverManager.getConnection(database_url, database_username, database_password);
			PreparedStatement ps = databaseConnection.prepareStatement("select * from user where username like ?");
			ps.setString(1, user);
			ResultSet resultSet = ps.executeQuery();
			if(resultSet.next())
			{
				System.out.println("User already exists");
				new RegisterStatusPacket(false, "Username taken.").write(socket.getOutputStream());
			}
			else
			{
				ps = databaseConnection.prepareStatement("insert into user (username, password) values (?, ?)");
				ps.setString(1, user);
				ps.setString(2, pass);
				ps.executeUpdate();
				System.out.println("User " + user + " added.");
				new RegisterStatusPacket(true, "Registration complete.").write(socket.getOutputStream());
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
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
