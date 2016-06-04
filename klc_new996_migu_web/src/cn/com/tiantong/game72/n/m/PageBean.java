package cn.com.tiantong.game72.n.m;

import java.util.ArrayList;

import cn.com.tiantong.game72.n.m.PageInnerBean;

public class PageBean {
	
	private int pageIndex;
	private int delayTime;
	private ArrayList<PageInnerBean> pibList;
	public PageBean() {
		super();
	}
	public PageBean(int pageIndex, int delayTime,
			ArrayList<PageInnerBean> pibList) {
		super();
		this.pageIndex = pageIndex;
		this.delayTime = delayTime;
		this.pibList = pibList;
	}
	public int getPageIndex() {
		return pageIndex;
	}
	public void setPageIndex(int pageIndex) {
		this.pageIndex = pageIndex;
	}
	public int getDelayTime() {
		return delayTime;
	}
	public void setDelayTime(int delayTime) {
		this.delayTime = delayTime;
	}
	public ArrayList<PageInnerBean> getPibList() {
		return pibList;
	}
	public void setPibList(ArrayList<PageInnerBean> pibList) {
		this.pibList = pibList;
	}
	
	@Override
	public String toString() {
		return "pageIndex : " + pageIndex + ", delayTime : " + delayTime + ", pibList : " + pibList;
	}
}
