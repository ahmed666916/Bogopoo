package com.bogopoo.liveshow;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;

public class BogopooApplication extends Application {
	private static Context context;
	private static BogopooApplication mInstance;
	
	public static synchronized BogopooApplication getInstance() {
		return mInstance;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		BogopooApplication.context = getApplicationContext();
		mInstance = this;
	}
	
	public static Context getAppContext() {
		return BogopooApplication.context;
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}
}
