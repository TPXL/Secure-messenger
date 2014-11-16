package com.tpxl.server;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.tpxl.protocol.Packet;
import com.tpxl.protocol.ServerPacketHandler;
import com.tpxl.protocol.packets.ChangeNicknamePacket;
import com.tpxl.protocol.packets.ConnectionStartInfoPacket;
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
import com.tpxl.protocol.packets.RegisterPacket;
import com.tpxl.protocol.packets.RegisterStatusPacket;
import com.tpxl.protocol.packets.SearchFriendsPacket;
import com.tpxl.protocol.packets.SearchFriendsResponsePacket;

public class ServerConnectionHandler implements Runnable, ServerPacketHandler{

	static String database_username = "messenger_admin";
	static String database_password = "password1234";
	static String database_url = "jdbc:mysql://localhost:3306/messenger_users"; 
	
	Integer ID;
	String username;
	String nickname;
	
	Socket socket;
	Server server;
	
	
	boolean checkForNewFriends;
	ConcurrentLinkedQueue<Integer> friendListsToSend;
	ConcurrentLinkedQueue<ConnectionStartInfoPacket> infoPacketsToSend;
	
	public ServerConnectionHandler(Socket socket, Server server)
	{
		this.socket = socket;
		this.server = server;
		ID = null;
		checkForNewFriends = false;
		friendListsToSend = new ConcurrentLinkedQueue<Integer>();
		infoPacketsToSend = new ConcurrentLinkedQueue<ConnectionStartInfoPacket>();
	}
	
	public void run() {
		if(socket == null)
			return;
		else if(socket.isClosed())
		{
			System.out.println("Socket closed");
			return;
		}
		
		try {
			socket.setSoTimeout(3000);
		} catch (SocketException e) {
			e.printStackTrace();
		}
		
		while(socket.isConnected() && !socket.isClosed())
		{
			try
			{
				InputStream inputStream = socket.getInputStream();
				Packet.readPacket(this, inputStream);
			}catch(SocketTimeoutException e)
			{
				
			}
			catch(IOException e)
			{
				e.printStackTrace();
				server.serverByUserID.remove(ID);
				break;
			}
			finally
			{
				
			}
			if(ID != null && checkForNewFriends)
			{
				try {
					sendFriendRequest();
				} catch (IOException e) {
					e.printStackTrace();
					server.serverByUserID.remove(ID);
					break;
				}
			}
			if(ID != null && infoPacketsToSend.size() > 0)
			{
				try {
					ConnectionStartInfoPacket CSIP = infoPacketsToSend.poll();
					if(CSIP != null)
					CSIP.write(socket.getOutputStream());
				} catch (IOException e) {
					e.printStackTrace();
					server.serverByUserID.remove(ID);
					break;
				}
			}
		}
		System.out.println("Connection closed!");
	}
	
	void sendFriendRequest() throws IOException
	{
		try {
			Connection connection = server.comboPooledDataSource.getConnection();
			PreparedStatement ps = connection.prepareStatement("select user.id, user.username from new_friends, user where user.ID = newFriends.ID_from and newFriends.ID_to = ?");
			ps.setInt(1, ID.intValue());
			checkForNewFriends = false;
			ps.executeQuery();
			ResultSet resultSet = ps.getResultSet();
			while(resultSet.next())
			{
				new FriendAddConfirmPacket(resultSet.getString(2), resultSet.getInt(1)).write(socket.getOutputStream());
				ps = connection.prepareStatement("delete from new_friends where newFriends.ID_to = ? and newFriends.ID_from = ?");
				ps.setInt(1, ID.intValue());
				ps.setInt(2, resultSet.getInt(1));
				ps.executeUpdate();
			}
			
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void onPacketReceive(HelloPacket helloPacket) {
		System.out.println("HelloPacket: " + helloPacket.hello + " " + helloPacket.version);
		HelloStatusPacket sp = null;
		boolean close = false;
		if(helloPacket.hello != Packet.getHelloInt())
		{
			System.out.println("Wrong hello int.");
			sp = new HelloStatusPacket(false, "Wrong hello int.");
			close = true;
		}
		else if(helloPacket.version != Packet.getProtocolVersion())
		{
			System.out.println("Wrong protocol version.");
			sp = new HelloStatusPacket(false, "Wrong protocol version int.");
			close = true;
		}
		else
			sp = new HelloStatusPacket(true, "Hello Successfull");
		
		try {
			sp.write(socket.getOutputStream());
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
		try
		{
			
			Connection databaseConnection = server.comboPooledDataSource.getConnection();
			PreparedStatement ps = databaseConnection.prepareStatement("select ID, username from user where username like ? and password like ? limit 1");
			ps.setString(1, loginPacket.getUsername());
			ps.setString(2, loginPacket.getPassword());
			ps.executeQuery();
			ResultSet resultSet = ps.getResultSet();
			if(resultSet.next())	
			{
				this.ID = resultSet.getInt(1);
				this.username = resultSet.getString(2);
				new LoginStatusPacket(true, "Login success!", ID.intValue()).write(socket.getOutputStream());
				server.serverByUserID.put(ID, this);
				
				ps = databaseConnection.prepareStatement("select distinct ID, username, nickname from user where ID in (select id1 from friends where id2 = ?) or ID in (select id2 from friends where id1 = ?) and ID != ?");
				ps.setInt(1, ID.intValue());
				ps.setInt(2, ID.intValue());
				ps.setInt(3, ID.intValue());
				ps.executeQuery();
				resultSet = ps.getResultSet();
				while(resultSet.next())
				{
					Integer cID = resultSet.getInt(1);
					ServerConnectionHandler serverConnectionHandler = server.serverByUserID.get(cID);
					if(serverConnectionHandler == null)
					{
						new FriendListPacket(resultSet.getString(2), resultSet.getString(3), cID.intValue(), false).write(socket.getOutputStream());
					}
					else
					{
						new FriendListPacket(resultSet.getString(2), resultSet.getString(3), cID.intValue(), true).write(socket.getOutputStream());
						serverConnectionHandler.friendListsToSend.add(ID);
					}
				}
			}
			else
			{
				new LoginStatusPacket(false, "Wrong username/password combination!", -1).write(socket.getOutputStream());
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	@Override
	public void onPacketReceive(RegisterPacket registerPacket) {
		
		if(server.recentlyRegisteredIPs.contains(socket.getInetAddress().getHostAddress()))
		{
			try {
				new RegisterStatusPacket(false, "Registration failed: You can only register once per 300 seconds.").write(socket.getOutputStream());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return;
		}
		
		String pass = registerPacket.getPassword();
		String user = registerPacket.getUsername();
		
		try
		{
			Connection databaseConnection = server.comboPooledDataSource.getConnection();
			PreparedStatement ps = databaseConnection.prepareStatement("select exists(select 1 from user where username like ? limit 1)");
			ps.setString(1, user);
			ResultSet resultSet = ps.executeQuery();
			if(resultSet.next() && resultSet.getBoolean(1))
			{
				System.out.println("User " + user + " already exists");
				new RegisterStatusPacket(false, "Username taken.").write(socket.getOutputStream());
			}
			else
			{
				ps = databaseConnection.prepareStatement("insert into user (username, password) values (?, ?)");
				ps.setString(1, user);
				ps.setString(2, pass);
				ps.executeUpdate();
				System.out.println("User " + user + " added.");
				server.recentlyRegisteredIPTimer.schedule(new RegisterTask(socket.getInetAddress()), 300000);
				server.recentlyRegisteredIPs.add(socket.getInetAddress().getHostAddress());
				new RegisterStatusPacket(true, "Registration complete.").write(socket.getOutputStream());
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	@Override
	public void onPacketReceive(SearchFriendsPacket searchFriendsPacket) {
		System.out.println("Searching friends!");
		try {
			Connection databaseConnection = server.comboPooledDataSource.getConnection();
			PreparedStatement ps = databaseConnection.prepareStatement("Select ID, username from user where username like ?");
			ps.setString(1, searchFriendsPacket.getName());
			ps.executeQuery();
			ResultSet rs = ps.getResultSet();
			while(rs.next())
			{
				new SearchFriendsResponsePacket(rs.getString(2), rs.getInt(1)).write(socket.getOutputStream());
			}
		} catch (SQLException e){
			e.printStackTrace();
		} catch (IOException e){
			e.printStackTrace();
		}
		System.out.println("Done searching friends");
	}
	@Override
	public void onPacketReceive(FriendAddPacket friendAddPacket) {
		// TODO Auto-generated method stub
		System.out.println("FriendAddPacket got");
		try {
			Connection databaseConnection = server.comboPooledDataSource.getConnection();
			PreparedStatement ps = databaseConnection.prepareStatement("insert into new_friends (ID_from, ID_to) values (?, ?)");
			ps.setInt(1, ID.intValue());
			ps.setInt(2, friendAddPacket.getID());
			ps.executeUpdate();
			ServerConnectionHandler serverConnectionHandler = server.serverByUserID.get(friendAddPacket.getID());
			if(serverConnectionHandler != null)
			{
				serverConnectionHandler.checkForNewFriends = true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	@Override
	public void onPacketReceive(FriendRemovePacket friendRemovePacket) {
		System.out.println("New FriendRemovePacket got");
		try {
			if(ID == null)
				return;
			Connection connection = server.comboPooledDataSource.getConnection();
			PreparedStatement ps = connection.prepareStatement("delete from friends where (friends.ID1 = ? and friends.ID2 = ?) or (friends.ID2 = ? and friends.ID1 = ?)");
			ps.setInt(1, ID.intValue());
			ps.setInt(2, friendRemovePacket.getID());
			ps.setInt(3, friendRemovePacket.getID());
			ps.setInt(4, ID.intValue());
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	@Override
	public void onPacketReceive(FriendAddConfirmResponsePacket friendAddConfirmResponsePacket) {
		if(friendAddConfirmResponsePacket.getSuccess())
		{
			Connection connection;
			try {
				connection = server.comboPooledDataSource.getConnection();
				PreparedStatement ps = connection.prepareStatement("insert into friends values (?, ?)");
				ps.setInt(1, ID.intValue());
				ps.setInt(2, friendAddConfirmResponsePacket.getID());
				ps.executeUpdate();
				
				ServerConnectionHandler serverConnectionHandler = server.serverByUserID.get(friendAddConfirmResponsePacket.getID());
				if(serverConnectionHandler == null)
				{
					new FriendListPacket(friendAddConfirmResponsePacket.getName(), nickname, friendAddConfirmResponsePacket.getID(), false).write(socket.getOutputStream());
				}
				else
				{
					new FriendListPacket(friendAddConfirmResponsePacket.getName(), nickname, friendAddConfirmResponsePacket.getID(), true).write(socket.getOutputStream());
					serverConnectionHandler.friendListsToSend.add(this.ID);
				}
				
			} catch (SQLException e) {
				e.printStackTrace();
			} catch(IOException e)
			{
				e.printStackTrace();
			}
			
		}
	}
	@Override
	public void onPacketReceive(GoodbyePacket goodbyePacket) {
		try {
			if(ID != null)
				server.serverByUserID.remove(ID);
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	@Override
	public void onPacketReceive(ChangeNicknamePacket changeNicknamePacket) {
		// TODO Auto-generated method stub
		System.out.println("New changeNicknamePacket got");
		try {
			Connection connection = server.comboPooledDataSource.getConnection();
			PreparedStatement ps = connection.prepareStatement("update user set nickname = ? where ID = ?");
			ps.setString(1, changeNicknamePacket.getNickname());
			ps.setInt(2, ID.intValue());
			ps.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Override
	public void onPacketReceive(ConnectionStartRequestPacket connectionStartRequestPacket) {
		
		Integer ID = connectionStartRequestPacket.getID();
		ServerConnectionHandler serverConnectionHandler = server.serverByUserID.get(ID);
		if(serverConnectionHandler != null)
		{
			serverConnectionHandler.infoPacketsToSend.add(new ConnectionStartInfoPacket(serverConnectionHandler.socket.getInetAddress().getHostAddress(), connectionStartRequestPacket.getKey()));
		}

	}
	
	class RegisterTask extends TimerTask
	{
		private InetAddress inetAddress;
		
		RegisterTask(InetAddress inetAddress)
		{
			this.inetAddress = inetAddress;
		}
		
		@Override
		public void run() {
			server.recentlyRegisteredIPs.remove(inetAddress.getHostAddress());
		}
	}
}
