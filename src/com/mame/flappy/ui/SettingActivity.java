package com.mame.flappy.ui;

import com.mame.flappy.R;
import com.mame.flappy.constant.LcomConst;
import com.mame.flappy.util.DbgUtil;
import com.mame.flappy.util.PreferenceUtil;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceFragment;

public class SettingActivity extends Activity {

	private final static String TAG = LcomConst.TAG + "/SettingActivity";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getFragmentManager().beginTransaction()
				.replace(android.R.id.content, new FlappySettingFragment())
				.commit();
	}

	public static class FlappySettingFragment extends PreferenceFragment
			implements OnSharedPreferenceChangeListener {
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.setting_preference);

			setSummaryText();

		}

		@Override
		public void onResume() {
			super.onResume();
			getPreferenceScreen().getSharedPreferences()
					.registerOnSharedPreferenceChangeListener(this);
		}

		@Override
		public void onPause() {
			super.onPause();
			getPreferenceScreen().getSharedPreferences()
					.unregisterOnSharedPreferenceChangeListener(this);
		}

		@Override
		public void onSharedPreferenceChanged(
				SharedPreferences sharedPreferences, String key) {
			setSummaryText();

		}

		private void setSummaryText() {
			CheckBoxPreference soundPreference = (CheckBoxPreference) getPreferenceScreen()
					.findPreference("setting_general_sound_preference");
			if (soundPreference.isChecked()) {
				soundPreference
						.setSummary(R.string.str_flappy_setting_notification_sound_value_on);
				PreferenceUtil.setCurrentSoundSetting(getActivity(), true);
			} else {
				soundPreference
						.setSummary(R.string.str_flappy_setting_notification_sound_value_off);
				PreferenceUtil.setCurrentSoundSetting(getActivity(), false);
			}

			CheckBoxPreference vibPreference = (CheckBoxPreference) getPreferenceScreen()
					.findPreference("setting_general_vibration_preference");
			if (vibPreference.isChecked()) {
				vibPreference
						.setSummary(R.string.str_flappy_setting_notification_vibration_value_on);
				PreferenceUtil.setCurrentVibrationSetting(getActivity(), true);
			} else {
				vibPreference
						.setSummary(R.string.str_flappy_setting_notification_vibration_value_off);
				PreferenceUtil.setCurrentVibrationSetting(getActivity(), false);
			}
		}
	}

}
