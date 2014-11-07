package com.tpxl.protocol;

import com.tpxl.protocol.packets.ConnectionStartPacket;
import com.tpxl.protocol.packets.FriendAddConfirmPacket;
import com.tpxl.protocol.packets.FriendListPacket;
import com.tpxl.protocol.packets.GoodbyePacket;
import com.tpxl.protocol.packets.HelloStatusPacket;
import com.tpxl.protocol.packets.LoginStatusPacket;
import com.tpxl.protocol.packets.MessagePacket;
import com.tpxl.protocol.packets.RegisterStatusPacket;
import com.tpxl.protocol.packets.SearchFriendsResponsePacket;

public interface ClientPacketHandler {
	public void onPacketReceive(MessagePacket messagePacket);
	public void onPacketReceive(HelloStatusPacket helloStatusPacket);
	public void onPacketReceive(LoginStatusPacket loginStatusPacket);
	public void onPacketReceive(RegisterStatusPacket registerStatusPacket);
	public void onPacketReceive(SearchFriendsResponsePacket searchFriendsResponsePacket);
	public void onPacketReceive(FriendAddConfirmPacket friendAddConfirmPacket);
	public void onPacketReceive(GoodbyePacket goodbyePacket);
	public void onPacketReceive(FriendListPacket friendListPacket);
	public void onPacketReceive(ConnectionStartPacket connectionStartPacket);
}
