package com.bogopoo.liveshow;

import android.app.Service;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.widget.ImageView;

public class AlwaysOnTopService extends Service {
	// ALWAYS-ON-TOP POPUP VIEW
	private ImageView mPopupView;
	
	// SETTING MANAGER
	private SettingManager mSettingManager;
	
	private WindowManager.LayoutParams params;
	private WindowManager mWindowManager;
	
	private boolean isLongPressed;
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();

		mSettingManager = SettingManager.getManager(this);
		
		// PARAMETERS FOR PLACING THE VIEW ON TOP OF SCREEN
		params = new WindowManager.LayoutParams(
				WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.TYPE_PHONE,
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
				PixelFormat.TRANSLUCENT);
		params.gravity = Gravity.START | Gravity.TOP;
		
		params.x = (Integer) SettingManager.getPrefValue(SettingManager.PREFKEY_CUR_POSITION_X, 0);
		params.y = (Integer) SettingManager.getPrefValue(SettingManager.PREFKEY_CUR_POSITION_Y, 0);
		
		// ALWAYS-ON-TOP VIEW
		mPopupView = new ImageView(this);
		mPopupView.setImageDrawable(getResources().getDrawable(R.drawable.jeein));
		mPopupView.setOnTouchListener(mTouchListener);
		mPopupView.setOnLongClickListener(mHoldListener);
		
		// WINDOW MANAGER
		mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
		mWindowManager.addView(mPopupView, params);
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		setMaximumViewPosition();
		adjustViewPosition();
	}

	private float mTouchStartX;
	private float mTouchStartY;
	private int mViewStartX;
	private int mViewStartY;
	private int mViewMaxX;
	private int mViewMaxY;
	private int mScaledTouchSlop;
	private boolean isMoved;
	private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			v.onTouchEvent(event);
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				// SET MAXIMUM POSITION OF THE POPUP VIEW
				setMaximumViewPosition();
				
				mTouchStartX = event.getRawX();
				mTouchStartY = event.getRawY();
				mViewStartX = params.x;
				mViewStartY = params.y;
				break;
			case MotionEvent.ACTION_MOVE:
				if (isLongPressed == true) {
					int distanceX = (int) (event.getRawX() - mTouchStartX);
					int distanceY = (int) (event.getRawY() - mTouchStartY);
					mScaledTouchSlop = ViewConfiguration.get(AlwaysOnTopService.this).getScaledTouchSlop();
					if (Math.abs(distanceX) > mScaledTouchSlop || Math.abs(distanceY) > mScaledTouchSlop || isMoved == true) {
						params.x = mViewStartX + distanceX;
						params.y = mViewStartY + distanceY;
						adjustViewPosition();
						mWindowManager.updateViewLayout(mPopupView, params);
						isMoved = true;
					} else {
						isMoved = false;
					}
				}
				break;
			case MotionEvent.ACTION_UP:
				v.performClick();
				if (isLongPressed == true) {
					isLongPressed = false;
					if (isMoved == true) {
						isMoved = false;
						SettingManager.setPrefValue(SettingManager.PREFKEY_CUR_POSITION_X, params.x);
						SettingManager.setPrefValue(SettingManager.PREFKEY_CUR_POSITION_Y, params.y);
					} else {
						// STOP SERVICE BY ITSELF
						AlwaysOnTopService.this.stopSelf();
					}
				}
				break;
			default:
				break;
			}
			return true;
		}
	};
	
	private View.OnLongClickListener mHoldListener = new View.OnLongClickListener() {
		@Override
		public boolean onLongClick(View v) {
			isLongPressed = true;
			return true;
		}
	};
	
	private void setMaximumViewPosition() {
		DisplayMetrics matrix = new DisplayMetrics();
		mWindowManager.getDefaultDisplay().getMetrics(matrix);
		mViewMaxX = matrix.widthPixels - mPopupView.getWidth();
		mViewMaxY = matrix.heightPixels - mPopupView.getHeight();
	}
	
	private void adjustViewPosition() {
		if (params.x > mViewMaxX) params.x = mViewMaxX;
		if (params.y > mViewMaxY) params.y = mViewMaxY;
		if (params.x < 0) params.x = 0;
		if (params.y < 0) params.y = 0;
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mPopupView != null) {
			 ((WindowManager) getSystemService(WINDOW_SERVICE)).removeView(mPopupView);
			 mPopupView = null;
		}
	}
}
