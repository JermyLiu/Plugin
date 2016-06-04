package cn.com.tiantong.game72.util;



import java.util.HashMap;



import android.content.Context;

import cn.com.tiantong.game72.ControlBroadcastReceiver;
import cn.com.tiantong.game72.util.UmengUtil;

import com.umeng.analytics.AnalyticsConfig;
import com.umeng.analytics.MobclickAgent;

public class UmengUtil {
	
	private static UmengUtil thisUmengUtil;
	
	private UmengUtil(){
		AnalyticsConfig.setAppkey("55fb7b3267e58e456a003940");
		AnalyticsConfig.setChannel(ControlBroadcastReceiver.QUDAOHAO);
	}
	
	public static UmengUtil getIntent(){
		if(thisUmengUtil == null){
			thisUmengUtil = new UmengUtil();
		}
		
		return thisUmengUtil;
	}
	
	//umeng发送次数
	public void sendUmengStatics(Context mContext, String staId, HashMap<String, String> staMap){
		
		if(staMap != null){
			MobclickAgent.onEvent(mContext, staId, staMap);
		}else{
			MobclickAgent.onEvent(mContext, staId);
		}
		
		
	}
	
	//Umeng启动
	public void startUmeng(Context mContext){
		try {
			MobclickAgent.onResume(mContext);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
    //Umeng终止
	public void stopUmeng(Context mContext){
		try {
			MobclickAgent.onPause(mContext);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//Umeng报错
	public void onError(Context mContext, Throwable th){
		try {
			MobclickAgent.reportError(mContext, th);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
