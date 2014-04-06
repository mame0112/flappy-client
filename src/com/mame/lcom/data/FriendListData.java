package com.mame.lcom.data;

import android.graphics.Bitmap;

import com.mame.lcom.constant.LcomConst;

public class FriendListData {

	private int mFriendId = LcomConst.NO_USER;

	// private String mFriendName = null;

	private String mFriendName = null;

	private String mLastSenderName = null;

	private int mLastSenderId = LcomConst.NO_USER;

	private String mLastMessage = null;

	private long mMessageDate = 0L;

	private int mNumOfNewMessage = 0;

	private String mMailAddress = null;

	private Bitmap mThumbnail = null;

	public FriendListData(int friendId, String friendName, int lastSenderId,
			String lastMessage, long lastMsgDate, int numOfNewMessage,
			String mailAddress, Bitmap thumbnail) {
		mFriendId = friendId;
		// mFriendName = friendName;
		mFriendName = friendName;
		mLastSenderId = lastSenderId;
		mLastMessage = lastMessage;
		mMessageDate = lastMsgDate;
		mNumOfNewMessage = numOfNewMessage;
		mMailAddress = mailAddress;
		mThumbnail = thumbnail;
	}

	public void setFriendId(int id) {
		mFriendId = id;
	}

	// public void setFriendName(String friendName) {
	// mFriendName = friendName;
	// }

	public void setFriendName(String friendName) {
		mFriendName = friendName;
	}

	public void setLastSender(String lastSender) {
		mLastSenderName = lastSender;
	}

	public void setLastMessage(String lastMessage) {
		mLastMessage = lastMessage;
	}

	public void setMessagDate(long messageDate) {
		mMessageDate = messageDate;
	}

	public void setNumOfNewMessage(int numOfNewMessage) {
		mNumOfNewMessage = numOfNewMessage;
	}

	public void setMailAddress(String mailAddress) {
		mMailAddress = mailAddress;
	}

	public void setThumbnail(Bitmap thumbnail) {
		mThumbnail = thumbnail;
	}

	public int getFriendId() {
		return mFriendId;
	}

	// public String getFriendName() {
	// return mFriendName;
	// }

	public String getFriendName() {
		return mFriendName;
	}

	public int getLastSender() {
		return mLastSenderId;
	}

	public String getLastMessage() {
		return mLastMessage;
	}

	public long getMessagDate() {
		return mMessageDate;
	}

	public int getNumOfNewMessage() {
		return mNumOfNewMessage;
	}

	public String getMailAddress() {
		return mMailAddress;
	}

	public Bitmap getThumbnail() {
		return mThumbnail;
	}

}
