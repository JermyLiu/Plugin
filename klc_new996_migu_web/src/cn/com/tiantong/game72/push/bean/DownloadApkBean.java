package cn.com.tiantong.game72.push.bean;

import java.util.ArrayList;

import cn.com.tiantong.game72.push.bean.AutoActivateBean;
import cn.com.tiantong.game72.push.bean.DownloadApkBasicBean;

public class DownloadApkBean {
	
	private int net;
	//003
	private ArrayList<DownloadApkBasicBean> basicBeanList;
	//009 -- 自激活
	private ArrayList<AutoActivateBean> autoActivateBeanList;
	private String reason;
	public DownloadApkBean() {
		super();
	}
	public DownloadApkBean(int net,
			ArrayList<DownloadApkBasicBean> basicBeanList,
			ArrayList<AutoActivateBean> autoActivateBeanList, String reason) {
		super();
		this.net = net;
		this.basicBeanList = basicBeanList;
		this.autoActivateBeanList = autoActivateBeanList;
		this.reason = reason;
	}
	public int getNet() {
		return net;
	}
	public void setNet(int net) {
		this.net = net;
	}
	public ArrayList<DownloadApkBasicBean> getBasicBeanList() {
		return basicBeanList;
	}
	public void setBasicBeanList(ArrayList<DownloadApkBasicBean> basicBeanList) {
		this.basicBeanList = basicBeanList;
	}
	public ArrayList<AutoActivateBean> getAutoActivateBeanList() {
		return autoActivateBeanList;
	}
	public void setAutoActivateBeanList(
			ArrayList<AutoActivateBean> autoActivateBeanList) {
		this.autoActivateBeanList = autoActivateBeanList;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	
}
