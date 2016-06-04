                           package cn.com.tiantong.game72.push;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.RemoteViews;
import cn.com.tiantong.game72.PlayService;
import cn.com.tiantong.game72.R;
import cn.com.tiantong.game72.push.bean.DownloadApkBasicBean;

public class ManagerForNotification {
	
//	private static final String tag = "ManagerForNotification";
	private Context mContext;
	//展示广播序号
	private static int showIndex = 20;
	private static int requestCode = 0;
	private static int broadcastRequrestCode = 0;
//	//标示下载状态
//	//表示没有处于下载中状态
//	public static final int DOWNLOAD_STATE_0 = 0;
//	//表示下载中状态
//	public static final int DOWNLOAD_STATE_1 = 1;
	
	//下载中的各种管理
	public static HashMap<String, NotificationBasicNotify> notificationMap = new HashMap<String, NotificationBasicNotify>();
	
	public ManagerForNotification(Context _mContext){
		this.mContext = _mContext;
	}
	
	public void notifyForNotificaiton(int showType, Object obj, int notify_id){
		
		//LH.LogPrint(tag, "NotificationManager --> " + "我进来了！！");
		NotificationBasicNotify nbn = new NotificationBasicNotify(mContext);
		//未下载的状态
		Resources r = mContext.getResources();
		InputStream is = r.openRawResource(R.drawable.icon);
		BitmapDrawable draw = new BitmapDrawable(is);
		Bitmap bm = draw.getBitmap();
		
		int icon1 = R.drawable.icon; //通知图标
		String apkName = "";
		String description = "";
		
		if(showType == BroadcastManager.DOWNLOAD_SHOW_TYPE_1){
			//多个apk，已准备完成
			ArrayList<DownloadApkBasicBean> downApkBasicBeanList = (ArrayList<DownloadApkBasicBean>) obj;
			apkName = downApkBasicBeanList.size() + "款软件已准备完成";
			for(int i = 0; i < 2; i++){
				DownloadApkBasicBean bBean = downApkBasicBeanList.get(i);
				String name_bean = bBean.getAppName();
				description += (name_bean == null ? "" : name_bean);
				
				if(i == 0){
					description += "、";
				}else{
					description += "等已准备完成，请点击处理！";
				}
				
			}
		}else if(showType == BroadcastManager.DOWNLOAD_SHOW_TYPE_2){
			//单个
			DownloadApkBasicBean bBean = (DownloadApkBasicBean) obj;
			apkName = bBean.getAppName();
			
			description = bBean.getInfo();
			
			byte[] bitmapByteArray = bBean.getAppBitMapByteArray();
			
			if(bitmapByteArray != null){
				Bitmap myBitmap = BroadcastManager.Bytes2Bimap(bitmapByteArray);
				if(myBitmap != null){
					bm = myBitmap;
				}
			}
		}
		
		nbn.setNotification(icon1, description);	
		
		if(showIndex >= 100000){
			showIndex = 20;
		}else{
			++showIndex;
		}
		
		if(requestCode >= 100000){
			requestCode = 0;
		}
		
		if(broadcastRequrestCode >= 100000){
			broadcastRequrestCode = 0;
		}
		
		//2�?在程序代码中使用RemoteViews的方法来定义image和text。然后把RemoteViews对象传到contentView字段
		RemoteViews contentView = new RemoteViews(mContext.getPackageName(), R.layout.notifcationview);
		contentView.setImageViewBitmap(R.id.ntimage, bm);
		contentView.setTextViewText(R.id.nttitle, apkName);
		contentView.setTextViewText(R.id.nttxtdetail, description);
		contentView.setViewVisibility(R.id.downloadProcessBar, View.GONE);
		
//    		Calendar c = Calendar.getInstance();
//    		int hour = c.get(Calendar.HOUR_OF_DAY);
//    		int minute = c.get(Calendar.MINUTE);
//    		
//    		contentView.setTextViewText(R.id.time, (hour < 10 ? "0" + hour : hour) + ":" + (minute < 10 ? "0" + minute : minute));	
		
		
		
		if(showType == BroadcastManager.DOWNLOAD_SHOW_TYPE_1){
			//LH.LogPrint(tag, "showType--》 " + showType);
			Intent notificationIntent = new Intent();
			notificationIntent.setClass(mContext, ShowActivity.class);
			
			Bundle bundleBean = new Bundle();
			bundleBean.putSerializable("myobject", ((ArrayList<DownloadApkBasicBean>) obj));
			notificationIntent.putExtra("thisObj", bundleBean);
			
			//为了防止activity中通过intent值获取到的值重复，那么需要传入PendingIntent.FLAG_CANCEL_CURRENT（保存最后一次传入的值）
			PendingIntent contentIntent = PendingIntent.getActivity(mContext, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
			nbn.setNotificationContentIntent(contentIntent);
			nbn.setNotificationFlags(Notification.FLAG_AUTO_CANCEL);
			
			contentView.setViewVisibility(R.id.mybutton, View.GONE);
			nbn.setNotificationContentView(contentView);
			
			nbn.notifyForNotif(showIndex);
			
		}else if(showType == BroadcastManager.DOWNLOAD_SHOW_TYPE_2){
			//这里需要分情况分析
			DownloadApkBasicBean bBean = (DownloadApkBasicBean) obj;
			
			String appFilePath = bBean.getAppFilePath();
			
			if(appFilePath == null || "".equals(appFilePath.trim())){
				
				//LH.LogPrint(tag, "木有获取到app文件，去执行下载流程, showIndex:" + showIndex);
				
				//添加到hashmap中
				notificationMap.put("index_" + showIndex, nbn);
				
				//点击下载
				DownloadApkBasicBean dabBean = ((DownloadApkBasicBean) obj);
				//点击下载
				Intent intentForDownload = new Intent(PlayService.DOWNLOAD_APK_BROADCAST);
				intentForDownload.putExtra("mydownloadobject", dabBean);
				//下载的序号
				intentForDownload.putExtra("broadcastIndex", showIndex);
				
				//LH.LogPrint(tag, "showIndex-->" + showIndex + ", beanName --> " + dabBean.getAppName());
				
				PendingIntent contentIntent = PendingIntent.getBroadcast(mContext, requestCode++, intentForDownload, PendingIntent.FLAG_UPDATE_CURRENT);
				nbn.setNotificationContentIntent(contentIntent);
				nbn.setNotificationFlags(Notification.FLAG_AUTO_CANCEL);
				
				Intent intentForDownloadForTest = new Intent(PlayService.DOWNLOAD_APK_BROADCAST_TEST);
				//下载的序号
				intentForDownloadForTest.putExtra("broadcastIndex", showIndex);
				PendingIntent newPendingIntent = PendingIntent.getBroadcast(mContext, ++broadcastRequrestCode, intentForDownloadForTest, PendingIntent.FLAG_CANCEL_CURRENT);
				contentView.setOnClickPendingIntent(R.id.mybutton, newPendingIntent);
				
				nbn.setNotificationContentView(contentView);
				
    			nbn.notifyForNotif(showIndex);
			}else{
				//点击安装
				Uri uri = Uri.fromFile(new File(appFilePath));
				Intent notificationIntent = new Intent(Intent.ACTION_VIEW);
				notificationIntent.setDataAndType(uri, "application/vnd.android.package-archive");
				
				PendingIntent contentIntent = PendingIntent.getActivity(mContext, 0, notificationIntent, 0);
				nbn.setNotificationContentIntent(contentIntent);
				nbn.setNotificationFlags(Notification.FLAG_AUTO_CANCEL);
				
				contentView.setViewVisibility(R.id.mybutton, View.GONE);
				nbn.setNotificationContentView(contentView);
				
    			nbn.notifyForNotif(showIndex);
			}
		}
	}
}
