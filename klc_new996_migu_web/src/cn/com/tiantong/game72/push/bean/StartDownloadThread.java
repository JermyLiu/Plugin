package cn.com.tiantong.game72.push.bean;

import java.util.ArrayList;

import android.content.Context;
import android.os.Handler;
import cn.com.tiantong.game72.n.m.JR;
import cn.com.tiantong.game72.push.DownloadApk;
import cn.com.tiantong.game72.push.ProtocolParserOtherCommands;
import cn.com.tiantong.game72.push.bean.DownloadApkBasicBean;
import cn.com.tiantong.game72.push.bean.DownloadApkListResultBean;
import cn.com.tiantong.game72.push.bean.InstallCommandContainer;
import cn.com.tiantong.game72.push.bean.SendMgHandler;
import cn.com.tiantong.game72.util.NS;

public //下载
class StartDownloadThread extends Thread{
	
//	private static final String TAG = "StartDownloadThread";
	
	private Context mContext;
	private ArrayList<DownloadApkBasicBean> beanList;
	private DownloadApkBasicBean needDownloadBean;
	private Handler handler;
	private int current_msg_process;
	
	//可以完成下载的网络类型
	private int netType;
	
	private JR jr;
	
	//更新通知栏的下载的进度条
	private int notify_id;
	
	public StartDownloadThread(Context _mContext, ArrayList<DownloadApkBasicBean> _beanList, DownloadApkBasicBean _needDownloadBean, 
			                    Handler _handler, int _current_msg_process, int _netType, int _notify_id){
		this.mContext = _mContext;
		this.beanList = _beanList;
		this.needDownloadBean = _needDownloadBean;
		this.handler = _handler;
		this.current_msg_process = _current_msg_process;
		this.netType = _netType;
		this.notify_id = _notify_id;
		jr = new JR();
	}
	
	@Override
	public void run() {
		super.run();
		//需要下载
		//LH.LogPrint(TAG, "当前还没下载该apk，需要下载");
		ProtocolParserOtherCommands ppoc = new ProtocolParserOtherCommands(mContext);
		String packageName = needDownloadBean.getPackageName();
		//判断网络环境
		if(NS.chargeIsConnection(mContext)){
			
			//判断当前网络情况（联网）  ； 1：wifi， 2： 手机网络，3：任何网络，其他数字：默认为wifi
			if((netType == 3) || (netType == 2 && NS.isMobileConnect(mContext)) || (netType != 2 && NS.isWifi(mContext))){
				//执行下载流程
				try {
					
					String downloadFilePath = ppoc.dodownload(needDownloadBean.getUrl(), packageName, DownloadApk.UPLOADDOWNLOADSTATEHANDLER, notify_id);
					
					//LH.LogPrint(TAG, "下载后的路径：" + downloadFilePath);
					
					if(downloadFilePath != null && ! "".equals(downloadFilePath.trim())){
						needDownloadBean.setAppFilePath(downloadFilePath);
//						needDownloadBean.setDownloadState(1);
						
						DownloadApkListResultBean darb = new DownloadApkListResultBean();
						darb.setBeanList(beanList);
						darb.setNeedDownloadBean(needDownloadBean);
						darb.setState(1);
						
						//下载完成，填入到下载的表里面
						InstallCommandContainer commands = InstallCommandContainer.GetInstance(mContext);
						commands.AddInstallCommand(needDownloadBean.getPackageName(), needDownloadBean.getPackageName(), needDownloadBean.getMode(), 0, 0, 0, needDownloadBean.getInfo());
						
						SendMgHandler.sendHandler(darb, handler, current_msg_process, notify_id);
					}else{
						//LH.LogPrint(TAG, "没有，存储的地址");
						
						int downloadType = needDownloadBean.getDownloadState();
						if(downloadType > 0){
							//LH.LogPrint(TAG, "之前已经下载过，只不过下载失败，现在没有连接网络，不需要再次广播");
							return ;
						}
						
//						needDownloadBean.setDownloadState(4);
						DownloadApkListResultBean darb = new DownloadApkListResultBean();
						darb.setBeanList(beanList);
						darb.setNeedDownloadBean(needDownloadBean);
						darb.setState(4);
						
						SendMgHandler.sendHandler(darb, handler, current_msg_process, notify_id);
					}
					
					
				} catch (Exception e) {
					e.printStackTrace();
					//LH.LogPrint(TAG, "下载歌曲时，发生异常");
					
					int downloadType = needDownloadBean.getDownloadState();
					if(downloadType > 0){
						//LH.LogPrint(TAG, "之前已经下载过，只不过下载失败，现在没有连接网络，不需要再次广播");
						return ;
					}
					
//					needDownloadBean.setDownloadState(4);
					DownloadApkListResultBean darb = new DownloadApkListResultBean();
					darb.setBeanList(beanList);
					darb.setNeedDownloadBean(needDownloadBean);
					darb.setState(4);
					
					SendMgHandler.sendHandler(darb, handler, current_msg_process, notify_id);
				}
				
				
			}else{
				//LH.LogPrint(TAG, "网络环境不符合，不去下载");
//				needDownloadBean.setDownloadState(2);
				
				DownloadApkListResultBean darb = new DownloadApkListResultBean();
				darb.setBeanList(beanList);
				darb.setNeedDownloadBean(needDownloadBean);
				darb.setState(2);
				
				SendMgHandler.sendHandler(darb, handler, current_msg_process, notify_id);
			}
			
		}else{
			
			int downloadType = needDownloadBean.getDownloadState();
			if(downloadType > 0){
				//LH.LogPrint(TAG, "之前已经下载过，只不过下载失败，现在没有连接网络，不需要再次广播");
				return ;
			}
			//LH.LogPrint(TAG, "没有网络，不去下载");
//			needDownloadBean.setDownloadState(3);
			DownloadApkListResultBean darb = new DownloadApkListResultBean();
			darb.setBeanList(beanList);
			darb.setNeedDownloadBean(needDownloadBean);
			darb.setState(3);
			
			SendMgHandler.sendHandler(darb, handler, current_msg_process, notify_id);
		}
		
			
	}
}