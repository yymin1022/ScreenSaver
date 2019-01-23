package com.yong.screensaver;
import android.app.*;
import android.content.*;
import android.graphics.*;
import android.os.*;
import android.util.Log;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import android.provider.*;

import androidx.core.app.NotificationCompat;

public class WindowService extends Service
{
	int originalCapacitiveButtonsState;
	
	private View mView;
	private WindowManager mManager;

	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		stopService(new Intent(this, NotiService.class));
		stopService(new Intent(this, FloatingService.class));
		try{
			originalCapacitiveButtonsState = Settings.System.getInt(getContentResolver(), "button_key_light", 1500);
		}catch(Exception e){
			Log.e("Exception", e.toString());
		}
		try{
			Settings.System.putInt(getContentResolver(), "button_key_light", 0);
		}catch(Exception e){
			Log.e("Exception", e.toString());
		}
		SharedPreferences prefs = getApplicationContext().getSharedPreferences("androesPrefName", MODE_PRIVATE);
		WindowManager.LayoutParams mParams;
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
			mParams = new WindowManager.LayoutParams(
					WindowManager.LayoutParams.MATCH_PARENT,
					WindowManager.LayoutParams.MATCH_PARENT,
					WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
					PixelFormat.RGB_888, 0);
		}else{
			mParams = new WindowManager.LayoutParams(
					WindowManager.LayoutParams.MATCH_PARENT,
					WindowManager.LayoutParams.MATCH_PARENT,
					WindowManager.LayoutParams.TYPE_PHONE,
					PixelFormat.RGB_888, 0);
		}
		mParams.gravity = Gravity.TOP | Gravity.START;
		LayoutInflater mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mView = mInflater.inflate(R.layout.window_layout, null);
		mView.setFocusableInTouchMode(true);
		mManager = (WindowManager) getSystemService(WINDOW_SERVICE);
		final TextView windowHelp = mView.findViewById(R.id.windowTV);
		switch(prefs.getInt("unlockMethod",0)){
			case 0:
				windowHelp.setText(getResources().getString(R.string.windowHelp) + getResources().getString(R.string.volumeButton));
				mView.setOnKeyListener(new OnKeyListener(){
					@Override
					public boolean onKey(View p1, int keyCode, KeyEvent keyEvent)
					{
						if(keyEvent.getAction()==KeyEvent.ACTION_DOWN){
							switch(keyCode){
								case KeyEvent.KEYCODE_VOLUME_UP:
									stopSelf();
									break;
								case KeyEvent.KEYCODE_VOLUME_DOWN:
									stopSelf();
									break;
							}
						}
						return true;
					}
				});
				mView.setOnTouchListener(new OnTouchListener() {
						@Override
						public boolean onTouch(View v, MotionEvent event) {
							windowHelp.setText("");
							return true;
						}
					});
				break;
			case 1:
				windowHelp.setText(getResources().getString(R.string.windowHelp) + getResources().getString(R.string.doubleTap));
				mView.setOnTouchListener(new OnTouchListener() {
					private GestureDetector gestureDetector = new GestureDetector(WindowService.this, new GestureDetector.SimpleOnGestureListener() {
						@Override
						public boolean onDoubleTap(MotionEvent e) {
							stopSelf();	
							return super.onDoubleTap(e);
						}
					});

					@Override
					public boolean onTouch(View v, MotionEvent event) {
						windowHelp.setText("");
						gestureDetector.onTouchEvent(event);
						return true;
					}
				});
				break;
			case 2:
				windowHelp.setText(getResources().getString(R.string.windowHelp) + getResources().getString(R.string.longTap));
				mView.setOnTouchListener(new OnTouchListener() {
					private GestureDetector gestureDetector = new GestureDetector(WindowService.this, new GestureDetector.SimpleOnGestureListener() {
						@Override
						public void onLongPress(MotionEvent e)
						{
							stopSelf();
							super.onLongPress(e);
						}
					});

					@Override
					public boolean onTouch(View v, MotionEvent event) {
						windowHelp.setText("");
						gestureDetector.onTouchEvent(event);
						return true;
					}
				});
				break;
		}
		mManager.addView(mView, mParams);

		NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext(), getResources().getString(R.string.app_name))
				.setSmallIcon(R.drawable.ic_launcher)
				.setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.ic_noti))
				.setContentTitle(getResources().getString(R.string.app_name))
				.setContentText(getResources().getString(R.string.serviceRunning))
				.setOngoing(true)
				.setPriority(Notification.PRIORITY_MIN)
				.setAutoCancel(false);

		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
			NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
			NotificationChannel channel = new NotificationChannel(getResources().getString(R.string.app_name), getResources().getString(R.string.app_name), NotificationManager.IMPORTANCE_MIN);
			channel.setDescription(getResources().getString(R.string.app_name));
			notificationManager.createNotificationChannel(channel);
		}
		startForeground(3, notificationBuilder.build());
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();
		try{
			Settings.System.putInt(getContentResolver(), "button_key_light", originalCapacitiveButtonsState);
		}catch(RuntimeException e){
			Log.e("Exception", e.toString());
		}
		try{
			Settings.System.putLong(getContentResolver(), "button_key_light", originalCapacitiveButtonsState);
		}catch(Exception e){
			Log.e("Exception", e.toString());
		}
		try{
			Settings.Secure.putInt(getContentResolver(), "button_key_light", originalCapacitiveButtonsState);
		}catch(Exception e){
			Log.e("Exception", e.toString());
		}
		try{
			Settings.System.putInt(getContentResolver(), "button_key_light", originalCapacitiveButtonsState);
		}catch(Exception e){
			Log.e("Exception", e.toString());
		}
		mManager.removeView(mView);
		SharedPreferences prefs = getApplicationContext().getSharedPreferences("androesPrefName", MODE_PRIVATE);
		switch(prefs.getInt("startMethod",0)){
			case 0:
				startService(new Intent(this, NotiService.class));
				break;
			case 1:
				startService(new Intent(this, FloatingService.class));
				break;
		}
		stopForeground(true);
	}

	@Override
	public IBinder onBind(Intent p1)
	{
		return null;
	}
}
