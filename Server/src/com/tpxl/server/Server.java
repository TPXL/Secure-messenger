package com.tpxl.server;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.util.Collection;
import java.util.Iterator;
import java.util.Properties;

import javax.net.ServerSocketFactory;
import javax.net.SocketFactory;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;


public class Server {
	
	//String hostName = "127.0.0.1";
	static final int port = 45293;
	
	static String driver = "com.mysql.jdbc.Driver";
	static String url = "jdbc:mysql://localhost:3306/messenger"; 
	static String username = "messenger_admin";
	static String password = "password1234";
	
	
	public static void main(String args[]) throws NoSuchAlgorithmException, KeyStoreException, CertificateException, IOException
	{
		System.out.println("Running server");
		//Load the database driver
		
		initDBConnection();
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
		/*
		try
		{
			FileInputStream fis = new FileInputStream("server.cer");
			CertificateFactory cf = CertificateFactory.getInstance("X.509");
			Collection<? extends Certificate> c = cf.generateCertificates(fis);
			Iterator<? extends Certificate> i = c.iterator();
			while (i.hasNext()) {
				Certificate cert = (Certificate)i.next();
				System.out.println(cert);
			}
			
			//Certificate certs[] = (Certificate[])c.toArray();
			Certificate certs[] = new Certificate[c.size()];
			c.toArray(certs);

			KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
			kpg.initialize(1024);
			KeyPair kp = kpg.genKeyPair();
			KeyStore KS = KeyStore.getInstance(KeyStore.getDefaultType());
			KS.load(null, "123456".toCharArray());
			KeyStore.ProtectionParameter protParam = new KeyStore.PasswordProtection("123456".toCharArray());
			KeyStore.PrivateKeyEntry pke = new KeyStore.PrivateKeyEntry(kp.getPrivate(), certs);
			KS.setEntry("muhAlias", pke, protParam);

			System.out.println("Putting into KS: " + kp.getPrivate());
			System.out.println();
			KeyStore.PrivateKeyEntry testpke = (KeyStore.PrivateKeyEntry)KS.getEntry("muhAlias", protParam);
			PrivateKey testpk = testpke.getPrivateKey();
			System.out.println("Reread from KS: " + testpk);
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		*/
		
		
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
			SSLServerSocket serverSocket = (SSLServerSocket)socketFactory.createServerSocket(port);
			)
		{
			System.out.println("Listening for connections!");
			serverSocket.setNeedClientAuth(true);
			SSLSocket socket;
			while((socket = (SSLSocket)serverSocket.accept()) != null)
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
	
	static void initDBConnection()
	{
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
	}
	
}
