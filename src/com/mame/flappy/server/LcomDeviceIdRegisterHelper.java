package com.mame.flappy.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Handler;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.mame.flappy.R;
import com.mame.flappy.constant.LcomConst;
import com.mame.flappy.ui.ProgressDialogFragment;
import com.mame.flappy.util.DbgUtil;
import com.mame.flappy.util.LcomGPServiceUtil;
import com.mame.flappy.util.NetworkUtil;
import com.mame.flappy.util.PreferenceUtil;
import com.mame.flappy.util.VersionUtil;
import com.mame.flappy.web.LcomWebAPI;
import com.mame.flappy.web.LcomWebAPI.LcomWebAPIListener;

public class LcomDeviceIdRegisterHelper implements LcomWebAPIListener {

	private final String TAG = LcomConst.TAG + "/LcomDeviceIdRegisterHelper";

	private GoogleCloudMessaging mGcm = null;

	private ArrayList<LcomPushRegistrationHelperListener> mListeners = new ArrayList<LcomPushRegistrationHelperListener>();

	private LcomWebAPI mWebAPI = null;

	private ProgressDialogFragment mProgressDialog = null;

	private Activity mActivity = null;

	private LcomGPServiceUtil mGPServiceUtil = new LcomGPServiceUtil();

	private String mRegId = null;

	private Handler mHandler = new Handler();

	public LcomDeviceIdRegisterHelper(Activity activity) {
		mWebAPI = new LcomWebAPI();
		mWebAPI.setListener(this);

		mActivity = activity;

		mProgressDialog = ProgressDialogFragment.newInstance(
				activity.getString(R.string.str_login_progress_title),
				activity.getString(R.string.str_generic_wait_desc));

	}

	public boolean isDeviceIdAvailable(Context context) {
		DbgUtil.showDebug(TAG, "isDeviceIdAvailable");
		if (checkGPService()) {
			if (VersionUtil.getCurrentAppVersion(context) == VersionUtil
					.getStoredAppVersion(context)) {
				String deviceId = PreferenceUtil.getPushDeviceId(context);
				if (deviceId != null && !deviceId.isEmpty()) {
					return true;
				}
			}
		}
		return false;
	}

	public void checkGPSAndAndRegisterDeviceId(Activity activity, int userId) {
		DbgUtil.showDebug(TAG, "getAndRegisterDeviceId");

		// If Google play service has already been installed, we try to get GCP
		// device id
		if (checkGPService()) {
			mProgressDialog.show(mActivity.getFragmentManager(), "progress");

			// Check for device manifest
			mGcm = GoogleCloudMessaging.getInstance(activity);

			if (getRegistrationId(activity).isEmpty()) {
				if (NetworkUtil.isNetworkAvailable(activity, mHandler)) {
					registerInBackground(activity, userId);
				} else {
					if (!mActivity.isFinishing() && mProgressDialog != null
							&& mProgressDialog.isShowing()) {
						mProgressDialog.dismiss();
					}
				}
			}

		} else {
			// TODO need to check behavior
			// We nothing to do.
			DbgUtil.showDebug(TAG, "Google Play service is not installed");
		}
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
					mRegId = regid;
					msg = "Device registered, registration ID=" + regid;

					// Register device id to server
					registerDeviceId(regid, userId);
					// sendMessage(regid);
				} catch (IOException ex) {
					msg = "IXException:" + ex.getMessage();
				}
				DbgUtil.showDebug(TAG, "msg: " + msg);
				return msg;
			}

			@Override
			protected void onPostExecute(String msg) {
			}
		}.execute(null, null, null);
	}

	/**
	 * Gets the current registration ID for application on GCM service.
	 * <p>
	 * If result is empty, the app needs to register.
	 * 
	 * @return registration ID, or empty string if there is no existing
	 *         registration ID.
	 */
	private String getRegistrationId(Context context) {
		String registrationId = PreferenceUtil.getPushDeviceId(context);
		if (registrationId == null || registrationId.isEmpty()) {
			DbgUtil.showDebug(TAG, "Registration not found");
			return "";
		}
		// Check if app was updated; if so, it must clear the registration ID
		// since the existing regID is not guaranteed to work with the new
		// app version.
		int registeredVersion = PreferenceUtil
				.getCurrentAppVersionForGCM(context);
		int currentVersion = VersionUtil.getCurrentAppVersion(context);
		if (registeredVersion != currentVersion) {
			DbgUtil.showDebug(TAG, "App version changed");
			return "";
		}
		return registrationId;
	}

	private void registerDeviceId(String deviceId, int userId) {
		if (userId != LcomConst.NO_USER) {
			DbgUtil.showDebug(TAG, "registerMessagepushDeviceId");

			String origin = TAG;
			String key[] = { LcomConst.SERVLET_ORIGIN,
					LcomConst.SERVLET_USER_ID, LcomConst.SERVLET_DEVICE_ID,
					LcomConst.SERVLET_API_LEVEL };
			String value[] = { origin, String.valueOf(userId), deviceId,
					String.valueOf(LcomConst.API_LEVEL) };
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

		if (!mActivity.isFinishing() && mProgressDialog != null
				&& mProgressDialog.isShowing()) {
			mProgressDialog.dismiss();
		}

		if (respList != null && respList.size() != 0) {
			String origin = respList.get(0);
			if (origin != null) {
				// If we come here after register push device id
				if (origin.equals(TAG)) {
					DbgUtil.showDebug(TAG, "finish to register device id");

					// Store device id just in case
					PreferenceUtil.setPushDeviceId(mActivity, mRegId);

					// Store app version for the Device iD
					int version = VersionUtil.getCurrentAppVersion(mActivity);
					PreferenceUtil.setCurrentAppVersionForGCM(mActivity,
							version);

					for (LcomPushRegistrationHelperListener listener : mListeners) {
						listener.onDeviceIdRegistrationFinished(true);
					}
				} else {
					DbgUtil.showDebug(TAG, "other origin");
					for (LcomPushRegistrationHelperListener listener : mListeners) {
						listener.onDeviceIdRegistrationFinished(false);
					}
				}
			} else {
				DbgUtil.showDebug(TAG, "origin is null");
				for (LcomPushRegistrationHelperListener listener : mListeners) {
					listener.onDeviceIdRegistrationFinished(false);
				}
			}
		} else {
			DbgUtil.showDebug(TAG, "respList is null or size 0");
			for (LcomPushRegistrationHelperListener listener : mListeners) {
				listener.onDeviceIdRegistrationFinished(false);
			}
		}

	}

	private boolean checkGPService() {
		DbgUtil.showDebug(TAG, "checkGPService");
		// Chieck if Google play service is supported or nort.
		boolean isGPServiceSupport = mGPServiceUtil
				.checkPlayServices(mActivity);
		DbgUtil.showDebug(TAG, "Google play suopport: " + isGPServiceSupport);
		return isGPServiceSupport;
	}

	@Override
	public void onAPITimeout() {
		DbgUtil.showDebug(TAG, "onAPITimeout");

		if (!mActivity.isFinishing() && mProgressDialog != null
				&& mProgressDialog.isShowing()) {
			mProgressDialog.dismiss();
		}

		for (LcomPushRegistrationHelperListener listener : mListeners) {
			listener.onDeviceIdRegistrationFinished(false);
		}

	}
}
