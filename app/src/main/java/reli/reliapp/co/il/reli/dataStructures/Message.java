package reli.reliapp.co.il.reli.dataStructures;

import java.util.Date;

import reli.reliapp.co.il.reli.UserList;

public class Message
{
	private String messageContent;
	private Date date;
	private String senderID;
    private MessageStatus status;

	public Message(String msg, Date date, String senderID)
	{
		this.messageContent = msg;
		this.date = date;
		this.senderID = senderID;
        this.status = MessageStatus.STATUS_SENDING;
	}

	public String getMessageContent()
	{
		return messageContent;
	}

	public void setMessageContent(String messageContent)
	{
		this.messageContent = messageContent;
	}

	public boolean isSentByUser()
	{
        // TODO change according to the implementation of ReliUser
		return (UserList.user.getParseID() == this.senderID);
	}

	public Date getDate()
	{
		return date;
	}

	public void setDate(Date date)
	{
		this.date = date;
	}

	public String getSenderID()
	{
		return senderID;
	}

	public void setSenderID(String senderID)
	{
		this.senderID = senderID;
	}

	public MessageStatus getStatus()
	{
		return status;
	}

	public void setStatus(MessageStatus status)
	{
		this.status = status;
	}
}
