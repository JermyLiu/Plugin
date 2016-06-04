package cn.com.tiantong.game72.util;

import android.util.Log;

public class Logger {
	
	private static final boolean flag = true;
	
	public static void print(String TAG, String context){
		if(flag){
			Log.e(TAG, context);
		}
	}

}
