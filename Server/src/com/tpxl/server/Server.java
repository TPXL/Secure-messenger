package com.tpxl.server;

import java.beans.PropertyVetoException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.Timer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.tpxl.protocol.ClientToServerPacketHandler;


public class Server {
	//String hostName = "127.0.0.1";
	static final int port = 45293;
	static final String driver = "com.mysql.jdbc.Driver";
	static final int threadCorePoolSize = 5;
	static final int threadMaxPoolSize = 200;
	static final int threadKeepAliveTime = 300;
	static final TimeUnit threadTimeUnit = TimeUnit.SECONDS;
	static final String database_url = "jdbc:mysql://localhost:3306/messenger_users"; 
	static final String database_username = "messenger_admin";
	static final String database_password = "password1234";
	
	ConcurrentSkipListSet<String> recentlyRegisteredIPs;
	Timer recentlyRegisteredIPTimer;
	Timer interruptServerConnectionTimer;
	ThreadPoolExecutor threadPoolExecutor;
	ComboPooledDataSource comboPooledDataSource;
	ConcurrentHashMap<Integer, ServerConnectionHandler> serverByUserID;
	
	public Server() throws PropertyVetoException
	{
		recentlyRegisteredIPs = new ConcurrentSkipListSet<String>();
		recentlyRegisteredIPTimer = new Timer(true);
		interruptServerConnectionTimer = new Timer(true);
		threadPoolExecutor = new ThreadPoolExecutor(threadCorePoolSize, threadMaxPoolSize, threadKeepAliveTime, threadTimeUnit, new SynchronousQueue<Runnable>());
		comboPooledDataSource = new ComboPooledDataSource();
		comboPooledDataSource.setDriverClass(driver);
		comboPooledDataSource.setJdbcUrl(database_url);
		comboPooledDataSource.setUser(database_username);
		comboPooledDataSource.setPassword(database_password);
		comboPooledDataSource.setMaxPoolSize(threadMaxPoolSize);
		serverByUserID = new ConcurrentHashMap<Integer, ServerConnectionHandler>();
	}
	
	public static void main(String args[]) throws NoSuchAlgorithmException, KeyStoreException, CertificateException, ClassNotFoundException, IOException, PropertyVetoException 
	{
		new Server().start();
	}

	public void start() throws NoSuchAlgorithmException, KeyStoreException, CertificateException, IOException, ClassNotFoundException{
		System.out.println("Running server");
		//Load the database driver
		Class.forName(driver);
		
		//initDBConnection();
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
		
		
		SSLServerSocketFactory socketFactory = getSecureServerSocketFactory(new FileInputStream("server.jks"));
		try
		{
			/*
			SSLContext sslContext = SSLContext.getInstance( "TLS" );
	        KeyManagerFactory km = KeyManagerFactory.getInstance("SunX509");
	        KeyStore ks = KeyStore.getInstance("JKS");
	        ks.load(new FileInputStream("server.jks"), "123456".toCharArray());
	        km.init(ks, "123456".toCharArray());
	        sslContext.init(km.getKeyManagers(), null, null);
	        socketFactory = sslContext.getServerSocketFactory();*/

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
			//serverSocket.setNeedClientAuth(true);
			SSLSocket socket;
			while((socket = (SSLSocket)serverSocket.accept()) != null)
			{
				ServerConnectionHandler connectionHandler = new ServerConnectionHandler(socket, this);
				socket = null;
				//new Thread(connectionHandler).start();
				threadPoolExecutor.execute(connectionHandler);
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
    public static SSLSocketFactory getSecureSocketFactory(InputStream is)
	{
		try
		{
			SSLContext sslContext = SSLContext.getInstance( "TLS" );
			KeyStore clientks = KeyStore.getInstance("JKS");
			clientks.load(is, "123456".toCharArray());
			TrustManagerFactory tm = TrustManagerFactory.getInstance("SunX509");
			tm.init(clientks);
			sslContext.init(null, tm.getTrustManagers(), null);
			return sslContext.getSocketFactory();
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}
	
	public static SSLServerSocketFactory getSecureServerSocketFactory(InputStream is)
	{
		try
		{
			SSLContext sslContext = SSLContext.getInstance( "TLS" );
	        KeyManagerFactory km = KeyManagerFactory.getInstance("SunX509");
	        KeyStore ks = KeyStore.getInstance("JKS");
	        ks.load(is, "123456".toCharArray());
	        km.init(ks, "123456".toCharArray());
	        sslContext.init(km.getKeyManagers(), null, null);
	        return sslContext.getServerSocketFactory();
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}
}
