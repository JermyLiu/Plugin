package cn.com.tiantong.game72.push.bean;

import java.util.ArrayList;

import android.os.Handler;
import android.os.Message;

public class SendMgHandler {
	
	public static void sendHandler(Object obj, Handler handler, int current_msg_process, int notify_id){
		
		Message msg = new Message();
		msg.what = current_msg_process;
		msg.obj = obj;
		msg.arg1 = notify_id;
		
		handler.sendMessage(msg);
		
	}

}
