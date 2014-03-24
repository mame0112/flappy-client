package com.mame.lcom.util;

import java.util.Locale;

import com.mame.lcom.constant.LcomConst;

public class LocaleUtil {

	public final static LcomConst.LOCALE_SETTING getCurrentLocale(){
		if (Locale.JAPAN.equals(Locale.getDefault())) {
			return LcomConst.LOCALE_SETTING.JAPANESE;
		} else {
			return LcomConst.LOCALE_SETTING.ENGLISH;
		}
	}
}
