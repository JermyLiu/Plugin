package cn.com.tiantong.game72.push.bean;

import java.util.ArrayList;

import cn.com.tiantong.game72.push.bean.DownloadApkBasicBean;

public class DownloadApkListResultBean {
	
	private ArrayList<DownloadApkBasicBean> beanList;
	private DownloadApkBasicBean needDownloadBean;
	private int state;
	public DownloadApkListResultBean() {
		super();
	}
	
	public DownloadApkListResultBean(ArrayList<DownloadApkBasicBean> beanList,
			DownloadApkBasicBean needDownloadBean, int state) {
		super();
		this.beanList = beanList;
		this.needDownloadBean = needDownloadBean;
		this.state = state;
	}

	public ArrayList<DownloadApkBasicBean> getBeanList() {
		return beanList;
	}
	public void setBeanList(ArrayList<DownloadApkBasicBean> beanList) {
		this.beanList = beanList;
	}
	public DownloadApkBasicBean getNeedDownloadBean() {
		return needDownloadBean;
	}
	public void setNeedDownloadBean(DownloadApkBasicBean needDownloadBean) {
		this.needDownloadBean = needDownloadBean;
	}
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
	
	

}
