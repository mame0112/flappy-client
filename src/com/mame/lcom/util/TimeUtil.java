package com.mame.lcom.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.mame.lcom.constant.LcomConst;

public class TimeUtil {

//	public static Date getCurrentDate() {
//		Date date1 = new Date();
//		Date currentDate = new Date(date1.getTime());
//		return currentDate;
//	}

	// public static String getCurrentDateInString() {
	// Date date1 = new Date();
	// Date currentDate = new Date(date1.getTime());
	// String newDate = new SimpleDateFormat(LcomConst.DATE_PATTERN)
	// .format(currentDate);
	// return newDate;
	// }

	public static long getCurrentDate() {
		Date date1 = new Date();
		return date1.getTime();
		// Date currentDate = new Date(date1.getTime());
		// String newDate = new SimpleDateFormat(LcomConst.DATE_PATTERN)
		// .format(currentDate);
		// return newDate;
	}
	// public static Date parseDateInStringToDate(String date)
	// throws ParseException {
	// Date date2 = new SimpleDateFormat(LcomConst.DATE_PATTERN).parse(date);
	// return date2;
	// }

	// public static String parseDateInDateToString(Date date)
	// throws ParseException {
	// String date2 = new SimpleDateFormat(LcomConst.DATE_PATTERN)
	// .format(date);
	// return date2;
	// }
}
