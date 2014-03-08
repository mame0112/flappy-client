package com.mame.lcom.data;

import java.util.Date;

public class MessageItemData {

	private int mFromUserId = 0;

	private int mToUserId = 0;

	private String mFromUserName = null;

	private String mToUserName = null;

	private String mMessage = null;

	/**
	 * This date should be stored as UTC.
	 */
	private long mPostedDate = 0L;

	/**
	 * Constructor
	 */
	public MessageItemData(int fromUserId, int toUserId, String fromUserName,
			String toUserName, String message, long postedDate) {
		mFromUserId = fromUserId;
		mToUserId = toUserId;
		mFromUserName = fromUserName;
		mToUserName = toUserName;
		mMessage = message;
		mPostedDate = postedDate;
	}

	public int getFromUserId() {
		return mFromUserId;
	}

	public int getTargetUserId() {
		return mToUserId;
	}

	public String getFromUserName() {
		return mFromUserName;
	}

	public String getToUserName() {
		return mToUserName;
	}

	public String getMessage() {
		return mMessage;
	}

	public long getPostedDate() {
		return mPostedDate;
	}

	public void setFromUserId(int fromUserId) {
		mFromUserId = fromUserId;
	}

	public void setToUserId(int toUserId) {
		mToUserId = toUserId;
	}

	public void setFromUserName(String fromUserName) {
		mFromUserName = fromUserName;
	}

	public void setToUserName(String toUserName) {
		mToUserName = toUserName;
	}

	public void setMessage(String message) {
		mMessage = message;
	}

	public void setPostedDate(long postedDate) {
		mPostedDate = postedDate;
	}
}
