package com.mame.flappy.sound;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

import com.mame.flappy.R;
import com.mame.flappy.constant.LcomConst;
import com.mame.flappy.util.DbgUtil;
import com.mame.flappy.util.PreferenceUtil;

public class FlappySoundManager {

	private final static String TAG = LcomConst.TAG + "/FlappySoundManager";

	private static SoundPool mSoundPool = null;

	private static int mNewMsgSoundId = 0;

	private static int mDisappearMsgSoundId = 0;

	private static Context mContext = null;

	public static void initialize(Context context) {
		mSoundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
		mNewMsgSoundId = mSoundPool.load(context, R.raw.new_message, 1);
		mDisappearMsgSoundId = mSoundPool.load(context,
				R.raw.disappear_message, 1);
		mContext = context;
	}

	public static void playNewMessageSound() {
		DbgUtil.showDebug(TAG, "playNewMessageSound");
		if (mSoundPool != null) {
			if (PreferenceUtil.getCurrentSoundSetting(mContext)) {
				mSoundPool.play(mNewMsgSoundId, 1.0f, 1.0f, 0, 0, 1.0f);
			}

		}
	}

	public static void playDisappearMessageSound() {
		if (mSoundPool != null) {
			if (PreferenceUtil.getCurrentSoundSetting(mContext)) {
				mSoundPool.play(mDisappearMsgSoundId, 1.0f, 1.0f, 0, 0, 1.0f);
			}
		}
	}

	public static void releaseSoundSource() {
		DbgUtil.showDebug(TAG, "releaseSoundSource");
		if (mSoundPool != null) {
			mSoundPool.release();
		}
	}

}
