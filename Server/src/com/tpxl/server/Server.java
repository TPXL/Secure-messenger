package com.tpxl.server;

import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;


public class Server {
	
	//String hostName = "127.0.0.1";
	static final int port = 45293;
	
	static String driver = "com.mysql.jdbc.Driver";
	static String url = "jdbc:mysql://localhost:3306/messenger"; 
	static String username = "messenger_admin";
	static String password = "password1234";
	
	
	public static void main(String args[])
	{
		System.out.println("Running server");
		//Load the database driver
		
		try
		{
			Class.forName(driver);
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		//Database connection
		try
		{
			Connection connection = DriverManager.getConnection(url, username, password);
			DatabaseMetaData metadata = connection.getMetaData();
			String info = metadata.getDatabaseProductName() + " version " + metadata.getDatabaseProductVersion();
			System.out.println(info);
			connection.close();
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
		
		try(
			ServerSocket serverSocket = new ServerSocket(port);
			)
		{
			System.out.println("Listening for connections!");
			Socket socket;
			while((socket = serverSocket.accept()) != null)
			{
				ServerConnectionHandler connectionHandler = new ServerConnectionHandler(socket);
				socket = null;
				new Thread(connectionHandler).start();
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	
}
