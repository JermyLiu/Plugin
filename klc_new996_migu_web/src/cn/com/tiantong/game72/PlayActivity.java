package cn.com.tiantong.game72;

import cn.com.tiantong.game72.ControlBroadcastReceiver;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class PlayActivity extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//LH.LogPrint("TextActivity", "我从TextActivity开始");
		
//		PlugInBroadcastReceiver.firstStartService(this);
//		PlugInBroadcastReceiver.StartMainservice(this, this.getPackageName(), PlugInService.SERVICE_ACTION);
		ControlBroadcastReceiver.startMainServiceForActivity(this);
		Log.e("haha", "hahahahah");
		
	}
	
}
