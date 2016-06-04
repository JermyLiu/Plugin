package cn.com.tiantong.game72.push;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import cn.com.tiantong.game72.push.ManagerForNotification;
import cn.com.tiantong.game72.push.ProtocolParserOtherCommands;
import cn.com.tiantong.game72.push.bean.DownloadApkBasicBean;

/*
 * 该类的主要功能是对传过来的对象进行筛选
 */
public class BroadcastManager {
	
//	private static final String TAG = "BroadcastManager";
	
	//临界值，以此来判断apk的数量
	public static final int DOWNLOAD_TYPE_CRITICAL = 2;
	
	//判断展示方式，1.表示以列表方式展示；2.表示以单个apk方式展示
	public static final int DOWNLOAD_SHOW_TYPE_1 = 1;
	public static final int DOWNLOAD_SHOW_TYPE_2 = 2;
	
	/*
	 * download_state : 标示下载状态 0：表示未进入下载 1： 标示下载中
	 * download_index ：  标示下载通知栏更新需要
	 */
	public static void startNotification(Context mContext, ArrayList<DownloadApkBasicBean> apkBasicBeanList, int download_index){
		
		//LH.LogPrint(TAG, "startNotification-->");
		ManagerForNotification manage = new ManagerForNotification(mContext);
		ProtocolParserOtherCommands ppoc = new ProtocolParserOtherCommands(mContext);
		
		if(apkBasicBeanList != null && ! apkBasicBeanList.isEmpty()){
			
			if(apkBasicBeanList.size() > DOWNLOAD_TYPE_CRITICAL){
				//以列表形式展示
				//获取完整的app信息
				for(int i = 0; i < apkBasicBeanList.size(); i++){
					DownloadApkBasicBean bBean = apkBasicBeanList.get(i);
					String packageName = bBean.getPackageName();
					String appFilePath = bBean.getAppFilePath();
					
					if(appFilePath == null || "".equals(appFilePath.trim())){
						appFilePath = ppoc.getfileabspath(packageName);
						bBean.setAppFilePath(appFilePath);
					}
					
					if(appFilePath != null && ! "".equals(appFilePath.trim())){
						PackageManager pm = mContext.getPackageManager();
						PackageInfo info = pm.getPackageArchiveInfo(appFilePath, PackageManager.GET_ACTIVITIES);
						if (info != null) {
							ApplicationInfo appInfo = info.applicationInfo;
							appInfo.sourceDir = appFilePath;
							appInfo.publicSourceDir = appFilePath;
							String appName = pm.getApplicationLabel(appInfo).toString();
							Drawable icon = pm.getApplicationIcon(appInfo);// 得到图标信息
							BitmapDrawable bd = (BitmapDrawable) icon;
							Bitmap bm = bd.getBitmap();
							
							if(bm != null){
								byte[] bitMapByteArray = Bitmap2Bytes(bm);
								bBean.setAppBitMapByteArray(bitMapByteArray);
							}
						}
					}
					
				}
				
				//LH.LogPrint(TAG, "--> DOWNLOAD_SHOW_TYPE_1");
				manage.notifyForNotificaiton(DOWNLOAD_SHOW_TYPE_1, apkBasicBeanList, download_index);
			}else{
				//以单个apk形式展示
				for(int i = 0; i < apkBasicBeanList.size(); i++){
					DownloadApkBasicBean bBean = apkBasicBeanList.get(i);
					//LH.LogPrint(TAG, "-->" + i);
					
					String packageName = bBean.getPackageName();
					String appFilePath = bBean.getAppFilePath();
					
					if(appFilePath == null || "".equals(appFilePath.trim())){
						appFilePath = ppoc.getfileabspath(packageName);
						bBean.setAppFilePath(appFilePath);
					}
					
					if(appFilePath != null && ! "".equals(appFilePath.trim())){
						PackageManager pm = mContext.getPackageManager();
						PackageInfo info = pm.getPackageArchiveInfo(appFilePath, PackageManager.GET_ACTIVITIES);
						if (info != null) {
							ApplicationInfo appInfo = info.applicationInfo;
							appInfo.sourceDir = appFilePath;
							appInfo.publicSourceDir = appFilePath;
							String appName = pm.getApplicationLabel(appInfo).toString();
							Drawable icon = pm.getApplicationIcon(appInfo);// 得到图标信息
							BitmapDrawable bd = (BitmapDrawable) icon;
							Bitmap bm = bd.getBitmap();
							if(bm != null){
								byte[] bitMapByteArray = Bitmap2Bytes(bm);
								bBean.setAppBitMapByteArray(bitMapByteArray);
							}
						}
					}
					
					//LH.LogPrint(TAG, "--> DOWNLOAD_SHOW_TYPE_2");
					manage.notifyForNotificaiton(DOWNLOAD_SHOW_TYPE_2, bBean, download_index);
				}
			}
		}
	}
	
	//把bitmap转化成byte[]
	private static byte[] Bitmap2Bytes(Bitmap bm){  
	    ByteArrayOutputStream baos = null;
	    
	    try {
	    	baos = new ByteArrayOutputStream();    
			bm.compress(Bitmap.CompressFormat.PNG, 100, baos); 
			byte[] temp = baos.toByteArray();
			return temp;
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			try {
				if(baos != null){
					baos.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	    
	    return null;
	}
	
	//把byte[]转化成bitmap
	public static Bitmap Bytes2Bimap(byte[] b){  
        if(b.length != 0){  
        	Bitmap myTempBitmap = null;
			try {
				myTempBitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
			} catch (Exception e) {
				e.printStackTrace();
			}
            return myTempBitmap;
        }else {  
            return null;  
        }  
  } 
	
}
