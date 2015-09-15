package il.co.reli.dataStructures;

import java.util.Date;

import il.co.reli.main.MainActivity;

public class Message
{
	private String messageContent;
	private Date date;
	private String senderID;
    private MessageStatus status;
	private String senderName;

	public Message(String msg, Date date, String senderID, String senderName)
	{
		this.messageContent = msg;
		this.date = date;
		this.senderID = senderID;
        this.status = MessageStatus.STATUS_SENDING;
		this.senderName = senderName;
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
		return (MainActivity.user.getParseID().equals(this.senderID));
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
		return this.senderID;
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

	public String getSenderName() {
		return this.senderName;
	}
}