package cn.com.tiantong.game72.push;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.widget.RemoteViews;

public class NotificationBasicNotify {
	
	private static final String tag = "NotificationBasicNotify";
	private NotificationManager mNotificationManager;
	private Notification mNotification;
	private Context mContext;
	
	public NotificationBasicNotify(Context _mContext){
		this.mContext = _mContext;
		mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
	}
	
	public void setNotification(int icon, String description){
		mNotification = new Notification(icon, description, System.currentTimeMillis());
	}
	
	/*
	 * 设置点击通知栏的后，消失还是驻留
	 * Notification.FLAG_AUTO_CANCEL; //在通知栏上点击此通知后自动清除此通知
	 * Notification.FLAG_NO_CLEAR; //表明在点击了通知栏清除通知"后，此通知不清除
	 */
	public void setNotificationFlags(int flags){
		mNotification.flags |= flags;
	}
	
	public void setNotificationContentView(RemoteViews contentView){
		mNotification.contentView = contentView;
	}
	
	public void setNotificationContentIntent(PendingIntent contentIntent){
		mNotification.contentIntent = contentIntent;
	}
	
	//发出
	public void notifyForNotif(int index){
		mNotificationManager.notify(index, mNotification);
	}

	public NotificationManager getmNotificationManager() {
		return mNotificationManager;
	}

	public Notification getmNotification() {
		return mNotification;
	}


}
