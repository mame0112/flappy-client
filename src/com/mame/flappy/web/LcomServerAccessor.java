package com.mame.flappy.web;

import java.util.List;

import com.mame.flappy.constant.LcomConst;
import com.mame.flappy.util.DbgUtil;
import com.mame.flappy.web.LcomAbstractServerAccessor.LcomWebAccessorListener;

public class LcomServerAccessor implements LcomWebAccessorListener {

	private final String TAG = LcomConst.TAG + "/LcomServerAccessor";

	private LcomAbstractServerAccessor mAccessor = null;

	private LcomServerAccessorListener mListener = null;

	public LcomServerAccessor() {
		DbgUtil.showDebug(TAG, "LcomServerAccessor");
		// By default, HTTPS request is used.
		// mAccessor = new LcomHttpWebAPI();
		mAccessor = new LcomHttpsWebAPI();
		mAccessor.setListener(this);
	}

	public void setWebAPISecurity(LcomAbstractServerAccessor accessor) {
		mAccessor = accessor;
	}

	public void sendData(String servletName, String[] key, String[] value,
			String identifier) {
		DbgUtil.showDebug(TAG, "sendData");
		mAccessor.sendData(servletName, key, value, identifier);
	}

	public void interrupt() {
		DbgUtil.showDebug(TAG, "interrupt");
		if (mAccessor != null) {
			mAccessor.interrupt();
		}
	}

	public void setListener(LcomServerAccessorListener listener) {
		DbgUtil.showDebug(TAG, "setListener");
		mListener = listener;
	}

	public void removeListener() {
		if (mListener != null) {
			mListener = null;
		}
	}

	@Override
	public void onResponseReceived(List<String> respList) {
		DbgUtil.showDebug(TAG, "setListener");
		if (mListener != null) {
			mListener.onResponseReceived(respList);
		}
	}

	@Override
	public void onAPITimeout() {
		DbgUtil.showDebug(TAG, "onAPITimeout");
		if (mListener != null) {
			mListener.onAPITimeout();
		}
	}

	public interface LcomServerAccessorListener {
		public void onResponseReceived(List<String> respList);

		public void onAPITimeout();
	}
}
