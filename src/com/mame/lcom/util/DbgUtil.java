package com.mame.lcom.util;

import android.util.Log;

import com.mame.lcom.constant.LcomConst;

public class DbgUtil {

	public static void showDebug(String tag, String debug){
		//If now debugging
		if(LcomConst.IS_DEBUG){
			Log.d(tag, debug);
		}
	}

}
