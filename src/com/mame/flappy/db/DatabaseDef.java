package com.mame.flappy.db;

import com.mame.flappy.constant.LcomConst;

import android.net.Uri;
import android.provider.BaseColumns;

public class DatabaseDef {

	public static final String AUTHORITY = "com.mame.flappy.db";

	protected static final Uri BASE_URI = Uri.parse("content://" + AUTHORITY);

	static final String DATABASE_NAME = LcomConst.DATABASE_NAME;

	static final int DATABASE_VERSION = 1;

	/**
	 * Friendship
	 */
	public interface FriendshipTable {
		/**
		 * The Friendship table name
		 */
		static final String TABLE_NAME = "friendship";

		/**
		 * Data row MIME type
		 */
		static final String MIME_TYPE = "lcom-friendship";

		/**
		 * Path segment
		 */
		static final String FRIENDSHIP_PATH = "friendship";

		static final String SINGLE_FRIENDSHIP_PATH = "friendship/#";

		/**
		 * Content URI
		 */
		static final Uri URI = Uri.withAppendedPath(BASE_URI, FRIENDSHIP_PATH);
	}

	/**
	 * Column definitions for the Friendship table
	 */
	public interface FriendshipColumns extends BaseColumns {
		static final String FRIEND_ID = "friend_id";

		static final String FRIEND_NAME = "friend_name";

		static final String LAST_SENDER_ID = "last_sender_id";

		static final String LAST_MESSAGE = "last_message";

		static final String MAIL_ADDRESS = "mail_address";

		static final String THUMBNAIL = "thumbnail";

		static final String LAST_CONTACTED_DATE = "last_contact_date";
	}

	/**
	 * Message
	 */
	public interface MessageTable {
		/**
		 * The Message table name
		 */
		static final String TABLE_NAME = "message";

		/**
		 * Data row MIME type
		 */
		static final String MIME_TYPE = "lcom-message";

		/**
		 * Path segment
		 */
		static final String MESSAGE_PATH = "message";

		static final String SINGLE_MESSAGE_PATH = "message/#";

		/**
		 * Content URI
		 */
		static final Uri URI = Uri.withAppendedPath(BASE_URI, MESSAGE_PATH);
	}

	/**
	 * Column definitions for the Message table
	 */
	public interface MessageColumns extends BaseColumns {
		static final String FROM_USER_ID = "from_user_id";

		static final String FROM_USER_NAME = "from_user_name";

		static final String TO_USER_ID = "to_user_id";

		static final String TO_USER_NAME = "to_user_name";

		static final String MESSAGE = "message";

		static final String DATE = "message_date";
	}

	/**
	 * Notification for the number of new message
	 */
	public interface NotificationTable {
		/**
		 * The Notification table name
		 */
		static final String TABLE_NAME = "notificarion";

		/**
		 * Data row MIME type
		 */
		static final String MIME_TYPE = "lcom-notificarion";

		/**
		 * Path segment
		 */
		static final String NOTIFICATION_PATH = "notificarion";

		static final String SINGLE_NOTIFICATION_PATH = "notificarion/#";

		/**
		 * Content URI
		 */
		static final Uri URI = Uri
				.withAppendedPath(BASE_URI, NOTIFICATION_PATH);
	}

	/**
	 * Column definitions for the Notification table
	 */
	public interface NotificationColumns extends BaseColumns {
		static final String FROM_USER_ID = "from_user_id";

		static final String TO_USER_ID = "to_user_id";

		static final String NUMBER = "number";

		static final String EXPIRE_DATE = "expire_date";
	}

	public interface Constants {
		static final int FRIENDSHIP_MATCH = 10;

		static final int MESSAGE_MATCH = 20;

		static final int NOTIFICATION_MATCH = 30;
	}

}
