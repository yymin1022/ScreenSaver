package com.yong.screensaver;

import android.app.*;
import android.content.*;
import android.graphics.*;
import android.os.*;

import androidx.core.app.NotificationCompat;

public class NotiService extends Service
{
	@Override
	public void onCreate()
	{
		super.onCreate();
		stopService(new Intent(this, FloatingService.class));

		Intent notificationIntent = new Intent(this, WindowService.class);
        PendingIntent contentIntent = PendingIntent.getService(getApplicationContext(), 0, notificationIntent, 0);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext(), getResources().getString(R.string.app_name))
			.setSmallIcon(R.drawable.ic_launcher)
			.setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.ic_noti))
			.setContentTitle(getResources().getString(R.string.app_name))
			.setContentText(getResources().getString(R.string.clickToStart))
			.setContentIntent(contentIntent)
			.setOngoing(true)
			.setPriority(Notification.PRIORITY_MIN)
			.setAutoCancel(false);

		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
			NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
			NotificationChannel channel = new NotificationChannel(getResources().getString(R.string.app_name), getResources().getString(R.string.app_name), NotificationManager.IMPORTANCE_MIN);
			channel.setDescription(getResources().getString(R.string.app_name));
			notificationManager.createNotificationChannel(channel);
		}
		startForeground(2, notificationBuilder.build());
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();
		stopForeground(true);
	}

	@Override
	public IBinder onBind(Intent p1)
	{
		return null;
	}
}
