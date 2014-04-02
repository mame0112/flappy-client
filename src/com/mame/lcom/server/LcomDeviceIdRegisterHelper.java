package com.mame.lcom.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.AsyncTask;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.mame.lcom.constant.LcomConst;
import com.mame.lcom.util.DbgUtil;
import com.mame.lcom.util.PreferenceUtil;
import com.mame.lcom.web.LcomWebAPI;
import com.mame.lcom.web.LcomWebAPI.LcomWebAPIListener;

public class LcomDeviceIdRegisterHelper implements LcomWebAPIListener {

	private final String TAG = LcomConst.TAG + "/LcomDeviceIdRegisterHelper";

	private GoogleCloudMessaging mGcm = null;

	private ArrayList<LcomPushRegistrationHelperListener> mListeners = new ArrayList<LcomPushRegistrationHelperListener>();

	private LcomWebAPI mWebAPI = null;

	public LcomDeviceIdRegisterHelper() {
		mWebAPI = new LcomWebAPI();
		mWebAPI.setListener(this);
	}

	public void getAndRegisterDeviceId(Activity activity, int userId) {
		DbgUtil.showDebug(TAG, "getAndRegisterDeviceId");

		// Check for device manifest
		mGcm = GoogleCloudMessaging.getInstance(activity);
		registerInBackground(activity, userId);
	}

	private void registerInBackground(final Activity activity, final int userId) {
		new AsyncTask<Void, Void, String>() {
			@Override
			protected String doInBackground(Void... params) {
				String msg = "";
				try {
					if (mGcm == null) {
						mGcm = GoogleCloudMessaging.getInstance(activity);
					}
					String regid = mGcm.register(LcomConst.PROJECT_NUMBER);
					msg = "Device registered, registration ID=" + regid;

					// Store device id just in case
					PreferenceUtil.setPushDeviceId(activity, regid);

					// Register device id to server
					registerDeviceId(regid, userId);
					// sendMessage(regid);
				} catch (IOException ex) {
					msg = "Error :" + ex.getMessage();
				}
				DbgUtil.showDebug(TAG, "msg: " + msg);
				return msg;
			}

			@Override
			protected void onPostExecute(String msg) {
			}
		}.execute(null, null, null);
	}

	private void registerDeviceId(String deviceId, int userId) {
		if (userId != LcomConst.NO_USER) {
			DbgUtil.showDebug(TAG, "registerMessagepushDeviceId");

			String origin = TAG;
			String key[] = { LcomConst.SERVLET_ORIGIN,
					LcomConst.SERVLET_USER_ID, LcomConst.SERVLET_DEVICE_ID };
			String value[] = { origin, String.valueOf(userId), deviceId };
			mWebAPI.sendData(LcomConst.SERVLET_REGISTER_DEVICE_ID, key, value);

		}
	}

	public void removePushRegistrationListener(
			LcomPushRegistrationHelperListener listener) {
		if (mListeners.contains(listener)) {
			mListeners.remove(listener);
		}
	}

	public void setPushRegistrationListener(
			LcomPushRegistrationHelperListener listener) {
		mListeners.add(listener);
	}

	public interface LcomPushRegistrationHelperListener {
		public void onDeviceIdRegistrationFinished(boolean result);
	}

	@Override
	public void onResponseReceived(List<String> respList) {
		DbgUtil.showDebug(TAG, "onResponseReceived");

		if (respList != null && respList.size() != 0) {
			String origin = respList.get(0);
			if (origin != null) {
				// If we come here after register push device id
				if (origin.equals(TAG)) {
					DbgUtil.showDebug(TAG, "finish to register device id");
					for (LcomPushRegistrationHelperListener listener : mListeners) {
						listener.onDeviceIdRegistrationFinished(true);
					}
				} else {
					DbgUtil.showDebug(TAG, "other origin");
				}
			} else {
				DbgUtil.showDebug(TAG, "origin is null");
			}
		} else {
			DbgUtil.showDebug(TAG, "respList is null or size 0");
		}

	}

	@Override
	public void onAPITimeout() {
		DbgUtil.showDebug(TAG, "onAPITimeout");

	}
}
