package com.tpxl.client;

import java.io.FileInputStream;
import java.io.InputStream;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Enumeration;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import org.teleal.cling.UpnpService;
import org.teleal.cling.UpnpServiceImpl;
import org.teleal.cling.support.igd.PortMappingListener;
import org.teleal.cling.support.model.PortMapping;


public class Client implements Runnable{
	
	//static final String serverHostname = "193.95.248.218";
	static final String serverHostname = "127.0.0.1";
	static final int serverPort = 45293;
	static int clientPort = 45292;
	
        MainFrame mainFrame; 
        ClientToServerConnectionHandler serverConnection;
        
        public Client(MainFrame mainFrame)
        {
            this.mainFrame = mainFrame;
        }
        
        @Override
	public void run()
	{
		System.out.println("Running client!");
		ArrayList<UpnpService> services = new ArrayList<UpnpService>();

		try
		{
			
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
					PortMapping mapping = new PortMapping(clientPort, addr.getHostAddress(), PortMapping.Protocol.TCP, "Messenger " + addr.getHostAddress());
					System.out.println("new mapping " + clientPort + " " + addr.getHostAddress());
					UpnpService upnpService = new UpnpServiceImpl(new PortMappingListener(mapping));
					upnpService.getControlPoint().search();
					services.add(upnpService);
				}
			}
			
			
			SSLSocket socket = (SSLSocket) getSecureSocketFactory(new FileInputStream("client.jks")).createSocket(serverHostname, serverPort);
			serverConnection = new ClientToServerConnectionHandler(socket, this);
			serverConnection.run();		//stuff happens here!
			System.out.println("Connection off!");
		}catch(Exception e)
		{
			e.printStackTrace();
			System.exit(-1);
		}
		finally
		{
			for(int i=0; i < services.size(); i++)
			{
				UpnpService s = services.get(i);
				s.shutdown();	// cleanup!
			}
		}
		System.out.println("Client off!");
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
