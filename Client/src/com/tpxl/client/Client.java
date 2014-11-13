package com.tpxl.client;

import java.io.FileInputStream;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Enumeration;

import javax.net.SocketFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import org.teleal.cling.UpnpService;
import org.teleal.cling.UpnpServiceImpl;
import org.teleal.cling.support.igd.PortMappingListener;
import org.teleal.cling.support.model.PortMapping;

public class Client{
	
	static final String serverHostname = "127.0.0.1";
	static final int serverPort = 45293;
	static int port = 45292;
	public static void main(String args[])
	{
		System.out.println("Running client!");
		ArrayList<UpnpService> services = new ArrayList<UpnpService>();

		try
		{
			/*
			Enumeration<NetworkInterface> n = NetworkInterface.getNetworkInterfaces();
			
			while(n.hasMoreElements())
			{
				NetworkInterface e = n.nextElement();
				Enumeration<InetAddress> a = e.getInetAddresses();
				
				while(a.hasMoreElements())
				{
					InetAddress addr = a.nextElement();
					if(addr instanceof Inet6Address)
						continue;
					if(addr.isLoopbackAddress())
						continue;
					PortMapping mapping = new PortMapping(port, addr.getHostAddress(), PortMapping.Protocol.TCP, "Messenger " + addr.getHostAddress());
					System.out.println("new mapping " + port + " " + addr.getHostAddress());
					UpnpService upnpService = new UpnpServiceImpl(new PortMappingListener(mapping));
					upnpService.getControlPoint().search();
					services.add(upnpService);
				}
			}	//Apparently this is good for P2P!
			*/
			System.out.println("Connecting to server!");
			
			SSLContext sslContext = SSLContext.getInstance( "TLS" );
			KeyStore clientks = KeyStore.getInstance("JKS");
			clientks.load(new FileInputStream("client.jks"), "123456".toCharArray());
			TrustManagerFactory tm = TrustManagerFactory.getInstance("SunX509");
			tm.init(clientks);
			sslContext.init(null, tm.getTrustManagers(), null);
			SSLSocketFactory socketFactory = sslContext.getSocketFactory();
			
			//SSLSocketFactory socketFactory = (SSLSocketFactory)SSLSocketFactory.getDefault();
			//SocketFactory socketFactory = SocketFactory.getDefault();
			
			SSLSocket socket = (SSLSocket )socketFactory.createSocket(serverHostname, serverPort);
			
			
			ClientToServerConnectionHandler serverConnection = new ClientToServerConnectionHandler(socket);
			serverConnection.run();		//stuff happens here!
			System.out.println("Connection off!");
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			for(int i=0; i < services.size(); i++)
			{
				UpnpService s = services.get(i);
				s.shutdown();	//P2P cleanup!
			}
		}
		System.out.println("Client off!");
	}

}
