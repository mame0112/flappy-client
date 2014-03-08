package com.mame.lcom.exception;

public class FriendDataManagerException extends Exception {

	private static final long serialVersionUID = 2L;
	private String mMessage = null;

	public FriendDataManagerException(String exception) {
		mMessage = exception;
	}

	@Override
	public String getMessage() {
		return mMessage;
	}
}
