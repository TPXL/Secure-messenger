package com.tpxl.client;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.Socket;
import java.security.KeyStore;
import java.util.HashMap;
import java.util.Scanner;

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
import com.tpxl.protocol.packets.FriendListPacket;
import com.tpxl.protocol.packets.GoodbyePacket;
import com.tpxl.protocol.packets.HelloPacket;
import com.tpxl.protocol.packets.HelloStatusPacket;
import com.tpxl.protocol.packets.LoginPacket;
import com.tpxl.protocol.packets.LoginStatusPacket;
import com.tpxl.protocol.packets.MessagePacket;
import com.tpxl.protocol.packets.RegisterStatusPacket;
import com.tpxl.protocol.packets.SearchFriendsResponsePacket;
import com.tpxl.protocol.packets.ConnectionStartRequestPacket;
import com.tpxl.protocol.packets.FriendAddConfirmResponsePacket;
import com.tpxl.protocol.packets.FriendAddPacket;
import com.tpxl.protocol.packets.FriendRemovePacket;
import com.tpxl.protocol.packets.RegisterPacket;
import com.tpxl.protocol.packets.SearchFriendsPacket;

import java.util.ArrayList;

import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;

public class ClientToServerConnectionHandler implements Runnable, ClientToServerPacketHandler{

	Socket socket;
	
	static String publicKeyFile = "pub.jks";
	static String privateKeyFile = "priv.jks";
	
        Client client;
        
        ArrayList<SearchFriendsResponsePacket> friendResponses;
        ArrayList<Integer> friends;
        HashMap<Integer,FriendListPacket> friendsByID = new HashMap<Integer,FriendListPacket>();
        
        DefaultListModel searchFriendsListModel;
        DefaultListModel activeFriendsModel;
      String username;
    String nickname;
    Integer ID;
        
	ClientToServerConnectionHandler(Socket socket, Client client)
	{
				this.socket = socket;
                this.client = client;
                searchFriendsListModel = new DefaultListModel();
                client.mainFrame.searchFriendsList.setModel(searchFriendsListModel);
                activeFriendsModel = new DefaultListModel();
                client.mainFrame.activeFriendsList.setModel(activeFriendsModel);
                friendResponses = new ArrayList<SearchFriendsResponsePacket>();
                friends = new ArrayList<Integer>();
                nickname = "";
                ID = 0;
        }
	
	@Override
	public void run() {
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

        public void sendHelloPacket()
        {
            try
            {
                new HelloPacket(Packet.getHelloInt(), Packet.getProtocolVersion()).write(socket.getOutputStream());
            }catch(Exception e)
            {
                e.printStackTrace();
            }
        }
        
        public void sendLoginPacket(String username, String password)
        {
            try
            {
                new LoginPacket(username, password).write(socket.getOutputStream());
            }catch(Exception e)
            {
                e.printStackTrace();
            }
        }
        
        public void sendRegisterPacket(String username, String password)
        {
             try
            {
                new RegisterPacket(username, password).write(socket.getOutputStream());
            }catch(Exception e)
            {
                e.printStackTrace();
            }
        }
	
        public void sendSearchFriendsPacket(String name)
        {
            searchFriendsListModel.clear();
            friendResponses.clear();
            try
            {
                new SearchFriendsPacket(name).write(socket.getOutputStream());
            }catch(Exception e)
            {
                e.printStackTrace();
            }
        }
        
        public void sendFriendAddPacket(String name, int ID)
        {
            try
            {
                new FriendAddPacket(name, ID).write(socket.getOutputStream());
            }catch(Exception e)
            {
                e.printStackTrace();
            }
        }
        
        public void SendFriendRemovePacket(int index)
        {
            try
            {
                new FriendRemovePacket(friendsByID.get(friends.get(index)).getName(), friends.get(index)).write(socket.getOutputStream());
            }catch(Exception e)
            {
                e.printStackTrace();
            }
            
            client.mainFrame.getStatusLabel().setText("Removed " + friendsByID.get(index).getName() + " from friends!");
            friends.remove(index);
            activeFriendsModel.remove(index);
        }
        
        public void sendChangeNicknamePacket(String nickname)
        {
           try
            {
                new ChangeNicknamePacket(nickname).write(socket.getOutputStream());
            }catch(Exception e)
            {
                e.printStackTrace();
            } 
           client.mainFrame.getStatusLabel().setText("Changed nickname!");
        }
        
        public void sendFriendAddConfirmResponsePacket(boolean success, FriendAddConfirmPacket facp)
        {
            try
            {
                new FriendAddConfirmResponsePacket(success, facp.getName(), "", facp.getID()).write(socket.getOutputStream());
            }catch(Exception e)
            {
                e.printStackTrace();
            }
            client.mainFrame.getStatusLabel().setText("Accepted a friendship! :3");
        }
        
        public void sendConnectionStartRequestPacket(String username, int ID, byte[] key)
        {
            try
            {
                new ConnectionStartRequestPacket(username, ID, key).write(socket.getOutputStream());
            }catch(Exception e)
            {
                e.printStackTrace();
            }
        }
        
	@Override
	public void onPacketReceive(MessagePacket messagePacket) {
		// TODO Auto-generated method stub
		System.out.println("GOT A NEW MESSAGE!" + messagePacket.getMessage());
	}

	@Override
	public void onPacketReceive(HelloStatusPacket helloStatusPacket) {
		System.out.println(helloStatusPacket.getMessage());
		//System.out.println("Got a helloStatusPacket!");
                client.mainFrame.getStatusLabel().setText(helloStatusPacket.getMessage());
                if(!helloStatusPacket.getSuccess())
                {
                    JOptionPane.showMessageDialog(client.mainFrame, "Wrong version client", "Server doesn't like you", JOptionPane.ERROR_MESSAGE);
                    System.exit(-1);
                }
	}

	@Override
	public void onPacketReceive(LoginStatusPacket loginStatusPacket) {
		// TODO Auto-generated method stub
		//System.out.println("Success: " + loginStatusPacket.getSuccess() + "(" + loginStatusPacket.getMessage() + " " + loginStatusPacket.getID() + ")");
            client.mainFrame.getStatusLabel().setText(loginStatusPacket.getSuccess() + ": " + loginStatusPacket.getMessage());
            client.mainFrame.getLoginButton().setEnabled(true);
            if(loginStatusPacket.getSuccess())
            {
                client.mainFrame.activateMainPanel();
                this.nickname = loginStatusPacket.getNickname();
                this.username = client.mainFrame.getUsernameTextfield().getText();
                this.ID = loginStatusPacket.getID();
                client.mainFrame.setTitle(client.mainFrame.getUsernameTextfield().getText());
            }
        }

	@Override
	public void onPacketReceive(RegisterStatusPacket registerStatusPacket) {
		//System.out.println("Success: " + registerStatusPacket.getSuccess() + "(" + registerStatusPacket.getMessage() + ")");
            client.mainFrame.getStatusLabel().setText(registerStatusPacket.getSuccess() + ": " + registerStatusPacket.getMessage());
            if(registerStatusPacket.getSuccess())
                sendLoginPacket(client.mainFrame.getUsernameTextfield().getText(), client.mainFrame.getPasswordTextfield().getText());
            client.mainFrame.getLoginButton().setEnabled(true);
        }

        
	@Override
	public void onPacketReceive(SearchFriendsResponsePacket searchFriendsResponsePacket) {
		// TODO Auto-generated method stub
		//System.out.println(searchFriendsResponsePacket.getName() + " " + searchFriendsResponsePacket.getID());
		searchFriendsListModel.addElement(searchFriendsResponsePacket.getName());
                friendResponses.add(searchFriendsResponsePacket);
                //System.out.println("Got a searchFriendsResponsePacket!");
	}

	@Override
	public void onPacketReceive(FriendAddConfirmPacket friendAddConfirmPacket) {
		// TODO Auto-generated method stub
		System.out.println("Got a friendAddConfirmPacket!");
		 if(JOptionPane.showConfirmDialog(client.mainFrame, friendAddConfirmPacket.getName() + " wishes to add you to their friends. Confirm?") == JOptionPane.OK_OPTION)
                 {
                     sendFriendAddConfirmResponsePacket(true, friendAddConfirmPacket);
                 }
                 
        }

	@Override
	public void onPacketReceive(GoodbyePacket goodbyePacket) {
		// TODO Auto-generated method stub
		System.out.println("Got a goodbyePacket!");
		
	}

	@Override
	public void onPacketReceive(FriendListPacket friendListPacket) {
		// TODO Auto-generated method stub
		System.out.println("Got a friendListPacket!");
		String str = (friendListPacket.getOnline()? "(Online)" : "(Offline)") +friendListPacket.getName();
		if(!friendListPacket.getNickname().equals(""))
		{
			str += "  " + friendListPacket.getNickname();
		}
                if(friendsByID.containsKey(friendListPacket.getID()))
                {
                	int index = friends.indexOf(friendListPacket.getID());
                	activeFriendsModel.setElementAt(str, index);
                }
                else
                {
	                activeFriendsModel.addElement(str);
	                friends.add(friendListPacket.getID());
	                friendsByID.put(friendListPacket.getID(), friendListPacket);
                }
	}

	@Override
	public void onPacketReceive(ConnectionStartPacket connectionStartPacket) {
		System.out.println("Got a connectionStartPacket!");
	}

	@Override
	public void onPacketReceive(ConnectionStartInfoPacket connectionStartInfoPacket) {
		System.out.println("Got a CSIP packet!");
		try
		{
			System.out.println("KEY FILE LENGTH OTHER " + connectionStartInfoPacket.getKey().length);
			
			SSLSocketFactory socketFactory = Client.getSecureSocketFactory(new ByteArrayInputStream(connectionStartInfoPacket.getKey()));
			SSLSocket socket = (SSLSocket )socketFactory.createSocket(connectionStartInfoPacket.getIP(), Client.clientPort);
			System.out.println("Starting a new thread!");
			new Thread(new ClientToClientConnectionHandler(socket, username, connectionStartInfoPacket.getName())).start();
			System.out.println("Started a new thread!");
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}

}
