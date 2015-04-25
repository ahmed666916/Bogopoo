package com.bogopoo.liveshow;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.preference.PreferenceManager;

public class SettingManager {
	private static SettingManager mInstance = null;
	private static Context mContext;
	private static SharedPreferences mPref;
	private static SharedPreferences.Editor mEditor;
	
	// SHARED PREFERENCES KEYS
	public static final String PREFKEY_CUR_POSITION_X = "curPositionX";
	public static final String PREFKEY_CUR_POSITION_Y = "curPositionY";

	synchronized static public SettingManager getManager(Context context) {
		return (mInstance != null) ? (mInstance) : (mInstance = new SettingManager(context));
	}
	
	private SettingManager(Context context) {
		mContext = context;
		mPref = PreferenceManager.getDefaultSharedPreferences(mContext);
		mEditor = mPref.edit();
		mPref.registerOnSharedPreferenceChangeListener(mPreferenceListener);
	}
	
	private OnSharedPreferenceChangeListener mPreferenceListener = new OnSharedPreferenceChangeListener() {
		@Override
		public void onSharedPreferenceChanged(SharedPreferences pref, String key) {
			// TODO: ACTION FOR EACH KEY
		}
	};
	
	public static SharedPreferences getSharedPreferences() {
		if (mInstance == null)
			mInstance = getManager(BogopooApplication.getAppContext());
		return mPref;
	}
	
	public static Object getPrefValue(String key, Object defValue) {
		if (mInstance == null)
			mInstance = getManager(BogopooApplication.getAppContext());
		
		if (defValue.getClass() == Boolean.class) {
			return mPref.getBoolean(key, (Boolean) defValue);
		} else if (defValue.getClass() == String.class) {
			return mPref.getString(key, (String) defValue);
		} else if (defValue.getClass() == Integer.class) {
			return mPref.getInt(key, (Integer) defValue);
		} else {
			return null;
		}
	}
	
	public static void setPrefValue(String key, Object value) {
		if (mInstance == null)
			mInstance = getManager(BogopooApplication.getAppContext());
		
		if (value.getClass() == Boolean.class) {
			mEditor.putBoolean(key, (Boolean) value);
		} else if (value.getClass() == String.class) {
			mEditor.putString(key, (String) value);
		} else if (value.getClass() == Integer.class) {
			mEditor.putInt(key, (Integer) value);
		} else {
			return;
		}
		mEditor.commit();
	}
}
