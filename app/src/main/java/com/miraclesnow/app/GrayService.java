package com.miraclesnow.app;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

public class GrayService extends Service {

	private final static int GRAY_SERVICE_ID = 1001;
	private boolean isStart = false;

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub

		if (!isStart){
			isStart = true;
			if (Build.VERSION.SDK_INT < 18) {
				startForeground(GRAY_SERVICE_ID, new Notification());
			} else {
				Intent innerIntent = new Intent(this, GrayInnerService.class);
				startService(innerIntent);
				startForeground(GRAY_SERVICE_ID, new Notification());
			}
		}
		return super.onStartCommand(intent, flags, startId);
	}

	public static class GrayInnerService extends Service {

		@Override
		public IBinder onBind(Intent arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public int onStartCommand(Intent intent, int flags, int startId) {
			// TODO Auto-generated method stub
			startForeground(GRAY_SERVICE_ID, new Notification());
			stopForeground(true);
			stopSelf();
			return super.onStartCommand(intent, flags, startId);
		}
	}

}
