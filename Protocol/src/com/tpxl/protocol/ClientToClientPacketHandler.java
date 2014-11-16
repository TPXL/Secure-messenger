package com.tpxl.protocol;

import com.tpxl.protocol.packets.MessagePacket;

public interface ClientToClientPacketHandler {
	public void onPacketReceive(MessagePacket messagePacket);
}
