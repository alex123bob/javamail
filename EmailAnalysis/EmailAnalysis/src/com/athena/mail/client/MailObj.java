package com.athena.mail.client;

public class MailObj {
	public String id;
	
	public String senderAddress;
	
	public String receiverAddress;
	
	public String sentTime;
	
	public String receivedTime;
	
	public String mailSubject;
	
	public String mailContent;
	
	public MailObj (String mId, String sender, String receiver, String sT, String rT, String sub, String con){
		id = mId;
		senderAddress = sender;
		receiverAddress = receiver;
		sentTime = sT;
		receivedTime = rT;
		mailSubject = sub;
		mailContent = con;
	}
}
