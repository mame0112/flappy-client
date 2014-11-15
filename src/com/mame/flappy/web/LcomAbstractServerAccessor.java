package com.mame.flappy.web;

import java.util.List;

public interface LcomAbstractServerAccessor {
	/**
	 * send data
	 */
	public void sendData(String servletName, String[] key, String[] value,
			String identifier);

	public void interrupt();

	public void destroyAccessor();

	/**
	 * Set listener
	 */
	public void setListener(LcomWebAccessorListener listener);

	public interface LcomWebAccessorListener {
		public void onResponseReceived(List<String> respList);

		public void onAPITimeout();
	}
}
