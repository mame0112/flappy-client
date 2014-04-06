package com.mame.lcom.exception;

public class NewMessageNotificationManagerException extends Exception {

	private String mMessage = null;

	public NewMessageNotificationManagerException(String exception) {
		mMessage = exception;
	}

	@Override
	public String getMessage() {
		return mMessage;
	}
}
