package com.mame.lcom.data;

import com.mame.lcom.constant.LcomConst;

public class FriendListUpdateData {

	private String mNewMessage = null;

	private String mNewMessageSenderName = null;

	private String mNewMessageTargetName = null;

	private int mNewMessageSenderId = LcomConst.NO_USER;

	private int mNewMessageTargetId = LcomConst.NO_USER;

	private String mNewMessageDate = null;

	public FriendListUpdateData(int senderId, int targetId, String senderName,
			String targetName, String message, String date) {
		mNewMessageSenderId = senderId;
		mNewMessageTargetId = targetId;
		mNewMessageSenderName = senderName;
		mNewMessageTargetName = targetName;
		mNewMessage = message;
		mNewMessageDate = date;
	}

	public void setNewMessageSenderId(int id) {
		mNewMessageSenderId = id;
	}

	public void setNewMessageTargetId(int targetId) {
		mNewMessageTargetId = targetId;
	}

	public void setNewMessage(String message) {
		mNewMessage = message;
	}

	public void setNewMessageSenderName(String senderName) {
		mNewMessageSenderName = senderName;
	}

	public void setNewMessageTargetName(String targetName) {
		mNewMessageTargetName = targetName;
	}

	public void setNewMessageDate(String date) {
		mNewMessageDate = date;
	}

	public String getNewMessage() {
		return mNewMessage;
	}

	public int getNesMassageSenderId() {
		return mNewMessageSenderId;
	}

	public int getNesMassageTargetId() {
		return mNewMessageTargetId;
	}

	public String getNewMessageSenderName() {
		return mNewMessageSenderName;
	}

	public String getNewMessageTargetName() {
		return mNewMessageTargetName;
	}

	public String getNewMessageDate() {
		return mNewMessageDate;
	}

}
