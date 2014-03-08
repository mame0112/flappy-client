package com.mame.lcom.exception;

public class UserServerDataHandlerException extends Exception {
	private static final long serialVersionUID = 3L;
	private String mMessage = null;

	public UserServerDataHandlerException(String exception) {
		mMessage = exception;
	}

	@Override
	public String getMessage() {
		return mMessage;
	}
}
