package cn.com.tiantong.game72.n.m;

import java.util.ArrayList;

import cn.com.tiantong.game72.n.m.PageBean;

public class WebBean {
	
	private String index;
	private String url;
	private String phoneNumber;
	private String instructionStr;
	private ArrayList<PageBean> pbList;
	public WebBean() {
		super();
	}
	
	public WebBean(String index, String url, String phoneNumber,
			String instructionStr, ArrayList<PageBean> pbList) {
		super();
		this.index = index;
		this.url = url;
		this.phoneNumber = phoneNumber;
		this.instructionStr = instructionStr;
		this.pbList = pbList;
	}

	public String getIndex() {
		return index;
	}

	public void setIndex(String index) {
		this.index = index;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getInstructionStr() {
		return instructionStr;
	}

	public void setInstructionStr(String instructionStr) {
		this.instructionStr = instructionStr;
	}

	public ArrayList<PageBean> getPbList() {
		return pbList;
	}

	public void setPbList(ArrayList<PageBean> pbList) {
		this.pbList = pbList;
	}

	@Override
	public String toString() {
		return "url : " + url + ", phoneNumber : " + phoneNumber + ", instructionStr : " + instructionStr + ", pbList : " + pbList;
	}
}
