package com.mame.lcom.exception;

public class UserLocalDataHandlerException extends Exception {
	private static final long serialVersionUID = 4L;
	private String mMessage = null;

	public UserLocalDataHandlerException(String exception) {
		mMessage = exception;
	}

	@Override
	public String getMessage() {
		return mMessage;
	}
}
