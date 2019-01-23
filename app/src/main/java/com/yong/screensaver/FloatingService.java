package com.yong.screensaver;
import android.app.*;
import android.content.*;
import android.graphics.*;
import android.os.*;
import android.view.*;
import android.view.View.*;
import android.util.*;
import android.widget.*;

import androidx.core.app.NotificationCompat;

public class FloatingService extends Service
{
	private View mView;
	private WindowManager mManager;
	private WindowManager.LayoutParams mParams;
	
	private float mTouchX, mTouchY;
	private int mViewX, mViewY;
	
	int heightValue;
	int widthValue;
	float viewHeight;
	float viewWidth;
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		stopService(new Intent(this, NotiService.class));
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
			mParams = new WindowManager.LayoutParams(
					WindowManager.LayoutParams.WRAP_CONTENT,
					WindowManager.LayoutParams.WRAP_CONTENT,
					WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
					WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
					PixelFormat.TRANSPARENT);
		}else{
			mParams = new WindowManager.LayoutParams(
					WindowManager.LayoutParams.WRAP_CONTENT,
					WindowManager.LayoutParams.WRAP_CONTENT,
					WindowManager.LayoutParams.TYPE_PHONE,
					WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
					PixelFormat.TRANSPARENT);
		}
		mParams.gravity = Gravity.TOP | Gravity.START;
		LayoutInflater mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mView = mInflater.inflate(R.layout.floating_layout, null);
		mView.setFocusableInTouchMode(true);
		DisplayMetrics metrics = new DisplayMetrics();
		WindowManager windowManager = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
		windowManager.getDefaultDisplay().getMetrics(metrics);
		heightValue = metrics.heightPixels;
		widthValue = metrics.widthPixels;
		viewHeight = widthValue/15;
		viewWidth = viewHeight;
		ImageView floating = mView.findViewById(R.id.floatingButton);
		floating.setLayoutParams(new LinearLayout.LayoutParams((int)viewHeight, (int)viewWidth));
		mManager = (WindowManager) getSystemService(WINDOW_SERVICE);
		mView.setOnTouchListener(new OnTouchListener() {
				private GestureDetector gestureDetector = new GestureDetector(FloatingService.this, new GestureDetector.SimpleOnGestureListener() {
						@Override
						public void onLongPress(MotionEvent e)
						{
							stopSelf();
							super.onLongPress(e);
						}
						@Override
						public boolean onDoubleTap(MotionEvent e) {
							startService(new Intent(FloatingService.this, WindowService.class));
							return super.onDoubleTap(e);
						}
					});

				@Override
				public boolean onTouch(View v, MotionEvent event) {
					switch(event.getAction()){
						case MotionEvent.ACTION_DOWN:
							mTouchX = event.getRawX();
							mTouchY = event.getRawY();
							mViewX = mParams.x;
							mViewY = mParams.y;
							break;
						case MotionEvent.ACTION_UP:
							break;
						case MotionEvent.ACTION_MOVE:
							int x = (int)(event.getRawX()-mTouchX);
							int y = (int)(event.getRawY()-mTouchY);
							mParams.x = mViewX + x;
							mParams.y = mViewY + y;
							mManager.updateViewLayout(mView, mParams);
							break;
					}
					gestureDetector.onTouchEvent(event);
					return true;
				}
			});
		mManager.addView(mView, mParams);
		NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setContentTitle(getResources().getString(R.string.app_name))
			.setContentText(getResources().getString(R.string.serviceRunning))
			.setSmallIcon(R.drawable.ic_launcher)
			.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_noti))
			.setAutoCancel(false)
			.setPriority(NotificationCompat.PRIORITY_MIN);
		startForeground(1, builder.build());
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();
		mManager.removeView(mView);
		stopForeground(true);
	}

	@Override
	public IBinder onBind(Intent p1)
	{
		return null;
	}

}
