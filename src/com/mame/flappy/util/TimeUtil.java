package com.mame.flappy.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.content.Context;
import android.content.res.Resources;

import com.mame.flappy.R;
import com.mame.flappy.constant.LcomConst;

public class TimeUtil {

	private final static String TAG = LcomConst.TAG + "/TimeUtil";

	public static long getCurrentDate() {
		Date date1 = new Date();
		return date1.getTime();
	}

	public static String getDateForDisplay(long post, Context context) {
		DbgUtil.showDebug(TAG, "getDateForDisplay");

		long current = getCurrentDate();

		if (current > 0 && post > 0) {
			if (current > post) {
				long diff = current - post;
				// If the post time is not so old (within 1 day)
				if (diff <= LcomConst.TIME_DAY) {
					return changeDisplayDateForWithinOneDay(diff, context);
				} else {
					// Otherwise
					return changeDisplayDateForNotWithinOneDay(context, post);
				}
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	private static String changeDisplayDateForWithinOneDay(long diff,
			Context context) {
		DbgUtil.showDebug(TAG, "changeDisplayDateForWithinOneDay");

		Resources res = context.getResources();
		String PREFIX = res.getString(R.string.str_generic_time_before);
		if (diff < LcomConst.TIME_MIN) {
			long diff_sec = diff / LcomConst.TIME_SECOND;
			return String.valueOf(diff_sec)
					+ res.getString(R.string.str_generic_time_sec) + PREFIX;
		} else if (diff >= LcomConst.TIME_MIN && diff < LcomConst.TIME_HOUR) {
			long diff_sec = diff / LcomConst.TIME_MIN;
			return String.valueOf(diff_sec)
					+ res.getString(R.string.str_generic_time_min) + PREFIX;
		} else if (diff >= LcomConst.TIME_HOUR && diff < LcomConst.TIME_DAY) {
			long diff_sec = diff / LcomConst.TIME_HOUR;
			return String.valueOf(diff_sec)
					+ res.getString(R.string.str_generic_time_hour) + PREFIX;
		} else {
			return res.getString(R.string.str_generic_time_unknown_date);
		}
	}

	private static String changeDisplayDateForNotWithinOneDay(Context context,
			long postDate) {
		DbgUtil.showDebug(TAG, "changeDisplayDateForNotWithinOneDay");

		SimpleDateFormat sdf = new SimpleDateFormat("M/dd");
		return sdf.format(postDate);
	}
}
