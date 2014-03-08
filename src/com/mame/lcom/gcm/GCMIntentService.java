package com.mame.lcom.gcm;

import android.content.Context;
import android.content.Intent;

import com.google.android.gcm.GCMBaseIntentService;
import com.mame.lcom.constant.LcomConst;
import com.mame.lcom.util.DbgUtil;

public class GCMIntentService extends GCMBaseIntentService {

	private final String TAG = LcomConst.TAG + "/GCMIntentService";

	public GCMIntentService() {
		super(LcomConst.PROJECT_ID);
	}

	@Override
	protected void onRegistered(Context context, String registrationId) {
		DbgUtil.showDebug(TAG, "onRegisted registrationId:" + registrationId);
	}

	@Override
	protected void onUnregistered(Context context, String registrationId) {
		DbgUtil.showDebug(TAG, "onUnregistered registrationId:"
				+ registrationId);
	}

	@Override
	protected void onMessage(Context context, Intent data) {
		DbgUtil.showDebug(TAG, "onMessage");
	}

	@Override
	protected void onError(Context context, String errorId) {
		DbgUtil.showDebug(TAG, "onError errorId:" + errorId);
	}
}
