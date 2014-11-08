package com.tpxl.server;

import java.io.FileInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyStore;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.util.Properties;

import javax.net.ServerSocketFactory;
import javax.net.SocketFactory;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocketFactory;


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
			//e.printStackTrace();
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
			//e.printStackTrace();
		}
		
		//SSL
		/*Properties p = new Properties();
		p.setProperty("javax.net.ssl.keyStore", "server.jks");
		p.setProperty("javax.net.ssl.keyStorePassword", "123456");
		System.setProperties(p);*/
		
		//SSLServerSocketFactory socketFactory = (SSLServerSocketFactory)SSLServerSocketFactory.getDefault();
		//ServerSocketFactory socketFactory = ServerSocketFactory.getDefault();
		/*
		 * 		Cert made via: 
		 * 
		 *      Create a new keystore and self-signed certificate with corresponding public/private keys. 
		 *		keytool -genkeypair -alias mytest -keyalg RSA -validity 7 -keystore server.jks
		 *		
		 *		     Export and examine the self-signed certificate.
		 *		keytool -export -alias mytest -keystore server.jks -rfc -file server.cer
		 *	 	 
		 *		     Import the certificate into a new truststore.
		 *		keytool -import -alias mytest -file server.cer -keystore client.jks
		 *				 * */
		
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
		
		
		//SSLServerSocketFactory socketFactory = (SSLServerSocketFactory)SSLServerSocketFactory.getDefault();
		
		
		
		try(
			ServerSocket serverSocket = socketFactory.createServerSocket(port);
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
