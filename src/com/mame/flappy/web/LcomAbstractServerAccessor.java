package com.mame.flappy.web;

import java.util.List;

public interface LcomAbstractServerAccessor {
	/**
	 * send data
	 */
	public void sendData(String servletName, String[] key, String[] value);

	/**
	 * Set listener
	 */
	public void setListener(LcomWebAccessorListener listener);
	
	public interface LcomWebAccessorListener{
		public void onResponseReceived(List<String> respList);

		public void onAPITimeout();
	}
}
