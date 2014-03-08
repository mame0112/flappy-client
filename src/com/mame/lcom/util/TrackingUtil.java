package com.mame.lcom.util;

import android.app.Activity;
import android.content.Context;
import android.os.Build;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.GAServiceManager;
import com.google.analytics.tracking.android.MapBuilder;
import com.google.analytics.tracking.android.Tracker;
import com.mame.lcom.constant.LcomConst;

public class TrackingUtil {

	private final static String TAG_TRACKING = LcomConst.TAG + "/TrackingUtil";

	/** Custom variable 1(Model's name). */
	private static final int CUSTOM_VAR_INDEX_1 = 1;

	/** Custom variable 3(The number of registered friend). */
	private static final int CUSTOM_VAR_INDEX_2 = 2;

	/** Custom variable 4(The number of new message in one time). */
	private static final int CUSTOM_VAR_INDEX_3 = 3;

	/** Custom variable 5(The number of character in one time). */
	private static final int CUSTOM_VAR_INDEX_4 = 4;

	/** Empty */
	private static final int CUSTOM_VAR_INDEX_5 = 5;

	public static final String EVENT_CATEGORY_WELCOME = "Welcome view";

	public static final String EVENT_CATEGORY_LOGIN = "Login view";

	public static final String EVENT_CATEGORY_CREATE_ACCOUNT = "Create account view";

	public static final String EVENT_CATEGORY_CONTACT_LIST = "Contact listview";

	public static final String EVENT_CATEGORY_MESSAGE_INPUT_DIALOG = "Message input dialog";

	public static final String EVENT_CATEGORY_FRIEND_LIST = "Friend list view";

	public static final String EVENT_CATEGORY_CONVERSATION = "Conversation view";

	public static final String EVENT_ACTION_START_OPERATION_EXECUTION = "Start operation";

	public static final String EVENT_ACTION_LOGIN_OPTION = "option menu";

	public static final String EVENT_ACTION_LOGIN_EXECUTION = "login";

	public static final String EVENT_ACTION_CREATE_ACCOUNT_OPTION = "Option menu";

	public static final String EVENT_ACTION_CREATE_ACCOUNT_EXECUTION = "Create account";

	public static final String EVENT_ACTION_INPUT_MESSAGE = "Input message";

	public static final String EVENT_ACTION_FRIEND_LIST = "Friend list";

	public static final String EVENT_ACTION_FRIEND_LIST_OPTION = "Option menu";

	public static final String EVENT_ACTION_CONVERSATION = "Option menu";

	public static final String EVENT_LABEL_LOGIN_BUTTON = "Login";

	public static final String EVENT_LABEL_CREATE_ACCOUNT_BUTTON = "Create account";

	public static final String EVENT_LABEL_LOGIN_EXEC_BUTTON = "Execute login";

	public static final String EVENT_LABEL_CREATE_ACCOUNT_EXEC_BUTTON = "Execute create account";

	public static final String EVENT_LABEL_TOC_BUTTON = "Terms of service";

	public static final String EVENT_LABEL_PRIVACY_BUTTON = "Privacy policy";

	public static final String EVENT_LABEL_SEND_NEW_MESSAGE_BUTTON = "Send new message";

	public static final String EVENT_LABEL_CANCEL_NEW_MESSAGE_BUTTON = "Cancel new message";

	public static final String EVENT_LABEL_CANCEL_MESSAGE_BUTTON = "Cancel message";

	public static final String EVENT_LABEL_FRIEND_LIST_ADD_BUTTON = "Add friend";

	public static final String EVENT_LABEL_FRIEND_LIST_UPDATE_BUTTON = "Update friend list";

	public static final String EVENT_LABEL_FRIEND_LIST_FIRST_ADD_BUTTON = "Update friend list";

	public static final String EVENT_LABEL_FRIEND_LIST_HELP = "help";

	public static final String EVENT_LABEL_FRIEND_LIST_SIGN_OUT = "Sign out";

	public static final String EVENT_LABEL_CONVERSATION_SEND_BUTTON = "Send conversation message";

	/**
	 * Start tracking.
	 * 
	 * @param activity
	 *            {@link Activity}.
	 * @throws IllegalArgumentException
	 *             Parameter is null.
	 */
	public static void trackActivityStart(Activity activity) {

		DbgUtil.showDebug(TAG_TRACKING, "trackActivityStart");

		if (activity == null) {
			throw new IllegalArgumentException("parameter is null");
		}

		// EasyTracker.getInstance(activity).setContext(
		// activity.getApplicationContext(), null, null);
		EasyTracker.getInstance(activity).activityStart(activity);
	}

	/**
	 * Stop tracking.
	 * 
	 * @param activity
	 *            {@link Activity}.
	 * @throws IllegalArgumentException
	 *             Parameter is null.
	 */
	public static void trackActivityStop(Activity activity) {

		DbgUtil.showDebug(TAG_TRACKING, "trackActivityStop");

		if (activity == null) {
			throw new IllegalArgumentException("parameter is null");
		}

		EasyTracker.getInstance(activity).activityStop(activity);
		// EasyTracker.getInstance(activity).dispatch();
	}

	/**
	 * Tracks the number of friend
	 * 
	 */
	public static void trackNumberOfFriend(Context context, int numOfUser) {

		DbgUtil.showDebug(TAG_TRACKING, "trackNumOfFriend");

		if (context == null) {
			throw new IllegalArgumentException("parameter is null");
		}

		EasyTracker.getInstance(context).send(
				MapBuilder
						.createAppView()
						.set(Fields.customDimension(CUSTOM_VAR_INDEX_2),
								String.valueOf(numOfUser)).build());
		// , value)set.setCustomDimension(
		// CUSTOM_VAR_INDEX_3, activity.getCallingPackage());
	}

	/**
	 * Tracks the number of new message
	 */
	public static void trackNumberOfNewMessage(Context context, int numOfMessage) {

		DbgUtil.showDebug(TAG_TRACKING, "trackNumberOfNewMessage");

		EasyTracker.getInstance(context).send(
				MapBuilder
						.createAppView()
						.set(Fields.customDimension(CUSTOM_VAR_INDEX_3),
								String.valueOf(numOfMessage)).build());
	}

	/**
	 * Tracks the number of character in one message
	 */
	public static void trackNumberOfCharInOneMessage(Context context,
			int numOfChar) {

		DbgUtil.showDebug(TAG_TRACKING, "trackNumberOfCharInOneMessage");

		EasyTracker.getInstance(context).send(
				MapBuilder
						.createAppView()
						.set(Fields.customDimension(CUSTOM_VAR_INDEX_4),
								String.valueOf(numOfChar)).build());
	}

	/**
	 * Model's name.
	 */
	public static void trackModel(Context context) {

		DbgUtil.showDebug(TAG_TRACKING, "trackModel");

		EasyTracker.getInstance(context).send(
				MapBuilder
						.createAppView()
						.set(Fields.customDimension(CUSTOM_VAR_INDEX_1),
								Build.MODEL).build());
	}

	/**
	 * Track a pageview, which is analogous to an Activity. If null is passed in
	 * as input, no pageview will be tracked.
	 * 
	 * @param name
	 *            The name of the Activity or view to be tracked.
	 * @throws IllegalArgumentException
	 *             Parameter is null.
	 */
	public static void trackPage(Activity activity, String page) {

		DbgUtil.showDebug(TAG_TRACKING, "trackPage");

		if (page == null) {
			throw new IllegalArgumentException("parameter is null");
		}

		Tracker tracker = EasyTracker.getInstance(activity);
		tracker.set(Fields.SCREEN_NAME, page);
		tracker.send(MapBuilder.createAppView().build());
	}

	/**
	 * Track an Event.
	 * 
	 * @param category
	 *            the category of the event
	 * @param action
	 *            the action of the event
	 * @param label
	 *            the label of the event, can be null
	 * @param value
	 *            the value of the event
	 * @throws IllegalArgumentException
	 *             Parameter is null.
	 */
	public static void trackEvent(Context context, String category,
			String action, String label, int value) {

		DbgUtil.showDebug(TAG_TRACKING, "trackEvent");

		if (category == null || action == null || label == null) {
			throw new IllegalArgumentException("parameter is null");
		}

		EasyTracker.getInstance(context).send(
				MapBuilder.createEvent(category, action, label,
						Long.valueOf(value)).build());
	}

}
