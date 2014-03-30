package com.mame.lcom;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.client.ClientProtocolException;

import com.google.android.gcm.GCMRegistrar;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.mame.lcom.constant.LcomConst;
import com.mame.lcom.util.DbgUtil;
import com.mame.lcom.util.HttpClientUtil;
import com.mame.lcom.util.TrackingUtil;

import android.app.Application;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.text.TextUtils;

public class FlappyApplication extends Application {

	private final String TAG = LcomConst.TAG + "/FlappyApplication";

	private GoogleCloudMessaging mGcm = null;

	@Override
	public void onCreate() {
		DbgUtil.showDebug(TAG, "onCreate");

		// Track device information
		TrackingUtil.trackModel(getApplicationContext());

		// final String regId = GCMRegistrar.getRegistrationId(this);
		// if (regId.equals("")) {
		// DbgUtil.showDebug(TAG, "regid is null");
		// // GCMへ端末登録。登録後、GCMIntentService.onRegistered()が呼ばれる。
		// GCMRegistrar.register(this, LcomConst.PROJECT_NUMBER);
		// } else {
		// DbgUtil.showDebug(TAG, "regid is not null");
		// // 登録済みの場合、ここではアプリに登録しなおしているが
		// // Googleのサンプルでは unregister して register しなおしている。
		// String uri = LcomConst.BASE_URL + "?action=register" + "&userId="
		// + USER_ID + "&regId=" + regId;
		// // Util.doGetAsync(uri);
		//
		// HttpClientUtil easyHttpClient = new HttpClientUtil(uri);
		// String responseString;
		// try {
		// // get の場合
		// responseString = easyHttpClient.doGet();
		// DbgUtil.showDebug(TAG, "responseString: " + responseString);
		// } catch (UnsupportedEncodingException e) {
		// DbgUtil.showDebug(TAG,
		// "UnsupportedEncodingException: " + e.getMessage());
		// } catch (ClientProtocolException e) {
		// DbgUtil.showDebug(TAG,
		// "ClientProtocolException: " + e.getMessage());
		// } catch (IOException e) {
		// DbgUtil.showDebug(TAG, "IOException: " + e.getMessage());
		// }
		//
		// }

		// Check for device manifest
		mGcm = GoogleCloudMessaging.getInstance(this);
		registerInBackground();
	}

	private void registerInBackground() {
		new AsyncTask<Void, Void, String>() {
			@Override
			protected String doInBackground(Void... params) {
				String msg = "";
				try {
					if (mGcm == null) {
						mGcm = GoogleCloudMessaging
								.getInstance(getApplicationContext());
					}
					String regid = mGcm.register(LcomConst.PROJECT_NUMBER);
					msg = "Device registered, registration ID=" + regid;
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

	// private void sendMessage(String regId) {
	// DbgUtil.showDebug(TAG, "sendMessage");
	// String uri = LcomConst.BASE_URL + "message_push?action=register"
	// + "&userId=" + USER_ID + "&regId=" + regId;
	// DbgUtil.showDebug(TAG, "uri: " + uri);
	// // Util.doGet(uri);
	// HttpClientUtil easyHttpClient = new HttpClientUtil(uri);
	// String response = easyHttpClient.execDoGet();
	// DbgUtil.showDebug(TAG, "response: " + response);
	// }
	
	private void sendMessage(String regId) {

		String USER_ID = "TEST_USER";

		DbgUtil.showDebug(TAG, "sendMessage");
		String uri = LcomConst.BASE_URL + "message_push?action=register"
				+ "&userId=" + USER_ID + "&regId=" + regId;
		DbgUtil.showDebug(TAG, "uri: " + uri);
		// Util.doGet(uri);
		HttpClientUtil easyHttpClient = new HttpClientUtil(uri);
		String response = easyHttpClient.execDoGet();
		DbgUtil.showDebug(TAG, "response: " + response);
	}

	@Override
	public void onTerminate() {
		DbgUtil.showDebug(TAG, "onTerminate");
		GCMRegistrar.onDestroy(this);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		DbgUtil.showDebug(TAG, "onConfigurationChanged");
	}
}
