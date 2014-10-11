package com.mame.flappy.constant;

public class LcomConst {

	public final static String TAG = "LCom";

	public final static String BASE_URL = "http://loosecommunication.appspot.com/";

	public final static String BASE_HTTPS_URL = "https://loosecommunication.appspot.com/";

	public final static String FLAPPY_MAIL_ADDRESS = "flappy.communication@gmail.com";

	public final static String TOC_AUTOHRITY = "tos.html";

	public final static String PRIVACY_AUTOHRITY = "privacypolicy.html";

	public final static String PROFILE_THUMBNAIL = "propfile_thumbnail";

	public final static String DATABASE_NAME = "friendship.db";

	public final static String NULL = "null";

	public final static int API_LEVEL = 1;

	public final static boolean IS_DEBUG = true;

	public final static int NO_USER = -1;

	public final static int MESSAGE_MAX_LENGTH = -1;

	public final static int MAX_MESSAGE_LENGTH = 16;

	public final static int ITEM_ON_SCREEN = 100;

	public final static String DATE_PATTERN = "dd-MM-yy:HH:mm:SS";

	public final static String SERVLET_USER_ID = "servlet_userid";

	public final static String SERVLET_USER_NAME = "servlet_user_name";

	public final static String SERVLET_TARGET_USER_ID = "servlet_target_userid";

	public final static String SERVLET_TARGET_USER_NAME = "servlet_target_user_name";

	public final static String SERVLET_PASSWORD = "servlet_password";

	public final static String SERVLET_MAILADDRESS = "servet_mailAddress";

	public final static String SERVLET_LANGUAGE = "servet_language";

	public final static String SERVLET_MESSAGE_BODY = "servlet_message_body";

	public final static String SERVLET_MESSAGE_DATE = "servlet_message_date";

	public final static String SERVLET_TOTAL_USER_NUM = "servlet_total_user_num";

	public final static String SERVLET_THUMBNAIL = "servlet_thumbnail";

	public final static String SERVLET_ORIGIN = "servlet_origin";

	public final static String SERVLET_DEVICE_ID = "servlet_device_id";

	public final static String SERVLET_API_LEVEL = "servlet_api_level";

	public final static String SERVLET_IDENTIFIER = "servlet_identifier";

	// public final static String SERVLET_CONTEXT_IDENTIFIER =
	// "servlet_identifier";

	public final static String SERVLET_NAME_LOGIN = "servlet/login";

	public final static String SERVLET_NAME_CREATE_ACCOUNT_CHECK_USER_NAME = "servlet/create_account_check_name";

	public final static String SERVLET_NAME_CREATE_ACCOUNT = "servlet/create_account";

	public final static String SERVLET_NAME_NEW_INVITATION = "servlet/new_invitation";

	public final static String SERVLET_NAME_NEW_INVITATION_CONFIRMED = "servlet/new_invitation_confirmed";

	public final static String SERVLET_NAME_NEW_MESSAGE = "servlet/new_message";

	public final static String SERVLET_NAME_ALL_USER_DATA = "servlet/all_user_data";

	public final static String SERVLET_NAME_NEW_MESSAGE_DATE = "servlet/new_message_date";

	public final static String SERVLET_NAME_SEND_ADD_MESSAGE = "servlet/send_add_message";

	public final static String SERVLET_CONVERSATION_DATA = "servlet/conversation_data";

	public final static String SERVLET_FRIEBD_THUMBNAILS = "servlet/friend_thumbnails";

	public final static String SERVLET_REGISTER_DEVICE_ID = "servlet/deviceid_register";

	public final static int MAX_USER_NAME_LENGTH = 16;

	public final static int MIN_USER_NAME_LENGTH = 6;

	public final static String EXTRA_USER_ID = "extra_user_id";

	public final static String EXTRA_USER_NAME = "extra_user_name";

	public final static String EXTRA_TARGET_USER_ID = "extra_target_user_id";

	public final static String EXTRA_TARGET_USER_NAME = "extra_target_user_name";

	public final static String EXTRA_TARGET_MAIL_ADDRESS = "extra_target_mail_address";

	public final static String EXTRA_TARGET_MESSAGE = "extra_target_message";

	public final static String EXTRA_TARGET_NEW_MESSAGES = "extra_target_new_messages";

	public final static String EXTRA_TARGET_NEW_MESSAGES_DATE = "extra_target_new_messages_date";

	public final static String EXTRA_THUMBNAIL = "extra_thumbnail";

	public final static String RESULT_EXTRA_CONTACT_NAME = "result_extra_contact_name";

	public final static String RESULT_EXTRA_CONTACT_ADDRESS = "result_extra_contact_address";

	/**
	 * Time constants
	 */
	public final static long TIME_SECOND = 1000;

	public final static long TIME_MIN = 60 * TIME_SECOND;

	public final static long TIME_HOUR = 60 * TIME_MIN;

	public final static long TIME_DAY = 24 * TIME_HOUR;

	// Check thumanil every 5 days
	public final static long THUMBNAIL_CHECK_INTERVAL = TIME_DAY * 5;

	/**
	 * Login constants
	 */
	public final static int LOGIN_RESULT_OK = 0;

	public final static int LOGIN_RESULT_PARAMETER_NULL = 1;

	public final static int LOGIN_RESULT_LOGIN_FAILED = 2;

	/**
	 * Create account constants
	 */
	public final static int CREATE_ACCOUNT_RESULT_OK = 0;

	public final static int CREATE_ACCOUNT_PARAMETER_NULL = 1;

	public final static int CREATE_ACCOUNT_USER_ALREADY_EXIST = 2;

	public final static int CREATE_ACCOUNT_UNKNOWN_ERROR = 3;

	public final static int CREATE_ACCOUNT_RESULT_OK_WITH_ADDRESS_REGISTERED = 4;

	/**
	 * Send new (Welcome) message constants
	 */
	public final static int INVITATION_NEW_USER_RESULT_OK = 0;

	public final static int INVITATION_EXISTING_USER_RESULT_OK = 1;

	public final static int INVITATION_UNKNOWN_ERROR = 2;

	/**
	 * confirmed new message constants
	 */
	public final static int INVITATION_CONFIRMED_RESULT_OK = 0;

	public final static int INVITATION_CONFIRMED_UNKNOWN_ERROR = 1;

	public final static int INVITATION_CONFIRMED_MAIL_CANNOT_BE_SENT = 2;

	/**
	 * confirmed send message constants
	 */
	public final static int SEND_MESSAGE_RESULT_OK = 0;

	public final static int SEND_MESSAGE_UNKNOWN_ERROR = 1;

	public final static int SEND_MESSAGE_DATE_CANNOT_BE_PARSED = 2;

	public final static int SEND_MESSAGE_CANNOT_BE_SENT_MESSAGE = 3;

	/**
	 * Separator between each item within message
	 */
	public final static String SEPARATOR = "@@";

	public final static String MESSAGE_SEPARATOR = "__";

	public final static String ITEM_SEPARATOR = "##";

	/**
	 * Context for requesting Server data. None should be used for default value
	 * (case).
	 */
	public static enum ServerRequestContext {
		none, requestNewUserData, requestAllNewUserData, sendAndRegiserMessage, requestConversationData, requestFriendThumbnails
	}

	/**
	 * GCM Constants
	 */
	public final static String PROJECT_NUMBER = "558595405440";

	/**
	 * Option menu constants
	 */
	// public final static int MENU_SIGNOUT = 0;

	/**
	 * Constants for locale
	 */
	public static enum LOCALE_SETTING {
		ENGLISH, JAPANESE
	};

	/**
	 * Broadcast receiver name for Push notification
	 */
	public final static String ACTION_PUSH_NOTIFICATION = "com.mame.flappy.server.push_notification";

	/**
	 * Broadcast action name for new message expire
	 */
	public final static String ACTION_MESSAGE_EXPIRE = "com.mame.flappy.server.message_expire";

	/**
	 * Debug constants
	 */
	public final static String DEBUG_SERVLET = "debug_servlet";
}
