package com.tpxl.protocol;

public enum PacketType
{
	MESSAGE((short)1), 			//String message
	HELLO((short)2), 			//int hello, short version
	LOGIN((short)3), 			//string username, string password
	REGISTER((short)4), 		//string username, string password
	SEARCHFRIENDS((short)5), 	//string name
	FRIENDADD((short)6), 		//string name(int ID)
	FRIENDREMOVE((short)7), 	//string name(int ID)
	HELLOSTATUS((short)8), 		//boolean success + string message
	LOGINSTATUS((short)9), 		//boolean success + string message + int ID
	REGISTERSTATUS((short)10),	//boolean success + string message
	SEARCHFRIENDSRESPONSE((short)11),	//string name(int ID)
	FRIENDADDCONFIRM((short)12),		//string name (int ID)
	FRIENDADDCONFIRMRESPONSE((short)13),//boolean success, string name (int ID)
	GOODBYE((short)14),					//string message
	CHANGENICKNAME((short)15),			//string nickname
	FRIENDLIST((short)16),				//string name(int ID)
	CONNECTIONSTARTINFO((short)17),		//string IP
	CONNECTIONSTARTREQUEST((short)18),	//string name(int ID)
	CONNECTIONSTART((short)19);			//string name(int ID)
	
	public final short code;
	private PacketType(short code)
	{ 
		this.code = code;
	}
	
	public final short getCode()
	{
		return code;
	}
}