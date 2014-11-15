package com.mame.flappy.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;

import android.os.Handler;
import android.util.Log;

import com.mame.flappy.constant.LcomConst;
import com.mame.flappy.util.DbgUtil;

public class LcomHttpWebAPI implements LcomAbstractServerAccessor {
	/**
	 * DEBUG
	 */
	private static final boolean DEBUG = true;

	private static final String TAG = LcomConst.TAG + "/WebAPI";

	// private Context mContext;

	/**
	 * Upload URL (Application identifier for GAE)
	 */
	private static final String BASE_URL = "http://loosecommunication.appspot.com";

	/**
	 * Action ID of Upload
	 */
	private static final int ACT_UPLOAD = 1;

	// private LcomWebAPIListener mListener = null;
	private LcomWebAccessorListener mListener = null;

	// private static Activity mActivity = null;

	private final Handler mHandler = new Handler();

	private final static int API_WAIT_TIMER = 30000;

	private boolean mIsResponed = false;

	// public LcomWebAPI(Activity activity) {
	// mActivity = activity;
	// // this.mContext = context;
	// }

	public void sendData(String servletName, String[] key, String[] value,
			String identifier) {
		String url = BASE_URL + "/" + servletName;
		PostThread mPostThread = new PostThread(ACT_UPLOAD, url, key, value);
		mHandler.postDelayed(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (!mIsResponed) {
					if (mListener != null) {
						mListener.onAPITimeout();
					}
					// TODO
					// Toast.makeText(mActivity,
					// R.string.str_generic_api_call_timeout,
					// Toast.LENGTH_SHORT).show();
				}
			}

		}, API_WAIT_TIMER);
		mPostThread.start();
	}

	private class PostThread extends Thread {
		private String url;
		private int type;
		private List<NameValuePair> postParams;
		private List<String> mRespList = new ArrayList<String>();

		public PostThread(int type, String url, String[] key, String[] value) {
			this.url = url;
			this.type = type;
			postParams = new ArrayList<NameValuePair>();
			for (int i = 0; i < key.length; i++) {
				postParams.add(new BasicNameValuePair(key[i], value[i]));
			}
		}

		@Override
		public void run() {
			HttpClient mHttp = new DefaultHttpClient();

			try {
				// setAPIStopAlarm();
				HttpPost postMethod = new HttpPost(url);

				// Header of Post
				postMethod.setHeader("Content-Type",
						"application/x-www-form-urlencoded");

				// UrlEncode
				UrlEncodedFormEntity sendData = new UrlEncodedFormEntity(
						postParams, "UTF-8");
				postMethod.setEntity(sendData);

				// Connect
				HttpResponse mResponse = mHttp.execute(postMethod);
				Log.d(TAG, "Connecting...");
				// if (DEBUG) {
				// Log.i(TAG, "connecting");
				// }

				// Response Code
				// int resCode = mResponse.getStatusLine().getStatusCode();
				// // Response Type
				// String resType = mResponse.getEntity().getContentType()
				// .getValue();
				// Response Value
				mIsResponed = true;
				HttpEntity httpEntity = mResponse.getEntity();
				String resValue = EntityUtils.toString(httpEntity);
				DbgUtil.showDebug(TAG, "resValue: " + resValue);

				try {
					JSONArray jsonArray = new JSONArray(resValue);
					for (int i = 0; i < jsonArray.length(); i++) {
						mRespList.add(jsonArray.getString(i));
						// Log.d(TAG, mRespList.get(i));
					}
				} catch (JSONException e) {
					Log.e(TAG, "JSONException: " + e.getMessage());
				}
				if (mListener != null) {
					mListener.onResponseReceived(mRespList);
				}
			} catch (IOException e) {
				Log.e(TAG, "error:" + e);
			}
		}
	}

	// public void setListener(LcomWebAPIListener listener) {
	// mListener = listener;
	// }

	public interface LcomWebAPIListener {
		public void onResponseReceived(List<String> respList);

		public void onAPITimeout();
	}

	@Override
	public void setListener(LcomWebAccessorListener listener) {
		mListener = listener;

	}

	@Override
	public void interrupt() {
		// TODO Need to add interrupt operation here.

	}

	@Override
	public void destroyAccessor() {
		// TODO Need to add destory operation here.
	}

}
