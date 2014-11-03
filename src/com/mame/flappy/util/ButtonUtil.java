package com.mame.flappy.util;

public class ButtonUtil {

	private static final long CLICK_DELAY = 1000;

	private static long mOldClickTime;

	public static boolean isClickable() {
		long time = System.currentTimeMillis();
		if (time - mOldClickTime < CLICK_DELAY) {
			return false;
		}
		mOldClickTime = time;
		return true;
	}
}
