package com.mame.flappy.util;

import com.mame.flappy.constant.LcomConst;

public class StringUtil {

	public static boolean isValidCharsForAddress(String input) {
		for (int i = 0; i < input.length(); i++) {
			char c = input.charAt(i);

			// "From ! to ~ (All ascii code)
			// if (((c >= '\u0033') && ((c <= '\u0126')))){
			if (((c >= 0x21) && ((c <= 0x7e)))) {
				// Nothing to do.
			} else {
				return false;
			}
		}
		return true;
	}

	public static boolean checkMailAddress(String address) {
		if (address == null) {
			return false;
		}

		if (address.indexOf("@") == -1) {
			return false;
		}

		if (address.contains(" ")) {
			return false;
		}

		return true;
	}

	public static boolean isContainPreservedCharacters(String input) {

		boolean isContain = false;

		if (input != null) {
			if (input.contains(LcomConst.SEPARATOR)) {
				isContain = true;
			} else if (input.contains(LcomConst.MESSAGE_SEPARATOR)) {
				isContain = true;
			} else if (input.contains(LcomConst.ITEM_SEPARATOR)) {
				isContain = true;
			}
		}
		return isContain;
	}

}
