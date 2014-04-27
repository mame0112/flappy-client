package com.mame.lcom.data;

import com.mame.lcom.constant.LcomConst;

public class NotificationContentData {

	private int mToUserId = LcomConst.NO_USER;

	private int mFromUserId = LcomConst.NO_USER;

	private int mNumberOfMessage = 0;

	private long mExpireDate = 0L;

	public NotificationContentData(int toUserId, int fromUserId, int number,
			long expireData) {
		mToUserId = toUserId;
		mFromUserId = fromUserId;
		mNumberOfMessage = number;
		mExpireDate = expireData;
	}

	public int getToUserId() {
		return mToUserId;
	}

	public int getFromUserId() {
		return mFromUserId;
	}

	public int getNumberOfMesage() {
		return mNumberOfMessage;
	}

	public long getExpireData() {
		return mExpireDate;
	}

	public void setToUserId(int toUserId) {
		mToUserId = toUserId;
	}

	public void setFromUserId(int fromUserId) {
		mFromUserId = fromUserId;
	}

	public void setNumberOfMessage(int numOfMessage) {
		mToUserId = numOfMessage;
	}

	public void setExpireDate(long expireDate) {
		mExpireDate = expireDate;
	}

}
