package com.mame.flappy.exception;

public class WebAPIException extends Exception {

	private static final long serialVersionUID = 1L;
	
	private String mMessage = null;
	
	public WebAPIException(String exception){
		mMessage = exception;
	}
	
	@Override
	public String getMessage(){
		return mMessage;
	}


}
