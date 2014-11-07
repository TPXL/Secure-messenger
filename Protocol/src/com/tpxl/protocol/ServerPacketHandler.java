package com.tpxl.protocol;

import com.tpxl.protocol.packets.ChangeNicknamePacket;
import com.tpxl.protocol.packets.ConnectionStartRequestPacket;
import com.tpxl.protocol.packets.FriendAddConfirmResponsePacket;
import com.tpxl.protocol.packets.FriendAddPacket;
import com.tpxl.protocol.packets.FriendRemovePacket;
import com.tpxl.protocol.packets.GoodbyePacket;
import com.tpxl.protocol.packets.HelloPacket;
import com.tpxl.protocol.packets.LoginPacket;
import com.tpxl.protocol.packets.RegisterPacket;
import com.tpxl.protocol.packets.SearchFriendsPacket;

public interface ServerPacketHandler {
	public void onPacketReceive(HelloPacket helloPacket);
	public void onPacketReceive(LoginPacket loginPacket);
	public void onPacketReceive(RegisterPacket registerPacket);
	public void onPacketReceive(SearchFriendsPacket searchFriendsPacket);
	public void onPacketReceive(FriendAddPacket friendAddPacket);
	public void onPacketReceive(FriendRemovePacket friendRemovePacket);
	public void onPacketReceive(FriendAddConfirmResponsePacket friendAddConfirmResponsePacket);
	public void onPacketReceive(ChangeNicknamePacket changeNicknamePacket);
	public void onPacketReceive(GoodbyePacket goodbyePacket);
	public void onPacketReceive(ConnectionStartRequestPacket connectionStartRequestPacket);
}
