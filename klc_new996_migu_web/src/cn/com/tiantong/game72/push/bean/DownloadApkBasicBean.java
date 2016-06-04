package cn.com.tiantong.game72.push.bean;

import java.io.Serializable;

public class DownloadApkBasicBean implements Serializable{
	
	//app下载地址
	private String url;
	//包名
	private String packageName;
	//app介绍
	private String info;
	//0:打开apk，不卸载； 1：打开apk，卸载；2：打开apk，点击，卸载；3：打开apk，点击，不卸载
	private int mode;
	
	//文件名称，因为有些是在下载之前就需要弹出在通知栏
	private String appName;
	//下载完成后，存放的路径
	private String appFilePath;
	//app图标
//	private Bitmap appBitMap;
	private byte[] appBitMapByteArray;
	
	//标示是否已经下载完成（下载流程是否完成:false表示还没有下载完成，true表示下载完成）
	/*
	 * 0：初始状态，未处理
	 * 1：成功下载apk，并且获取到apk的保存地址
	 * 2：由于网络类型不允许下载而导致的下载失败
	 * 3：由于没有网络而导致的下载失败 --> 只有不等于0，那么都应该不在发起广播
	 * 4：由于下载异常导致的下载失败
	 */
	private int downloadState;

	public DownloadApkBasicBean() {
		super();
	}

	public DownloadApkBasicBean(String url, String packageName, String info,
			int mode, String appName, String appFilePath,
			byte[] appBitMapByteArray, int downloadState) {
		super();
		this.url = url;
		this.packageName = packageName;
		this.info = info;
		this.mode = mode;
		this.appName = appName;
		this.appFilePath = appFilePath;
		this.appBitMapByteArray = appBitMapByteArray;
		this.downloadState = downloadState;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public int getMode() {
		return mode;
	}

	public void setMode(int mode) {
		this.mode = mode;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getAppFilePath() {
		return appFilePath;
	}

	public void setAppFilePath(String appFilePath) {
		this.appFilePath = appFilePath;
	}

	public byte[] getAppBitMapByteArray() {
		return appBitMapByteArray;
	}

	public void setAppBitMapByteArray(byte[] appBitMapByteArray) {
		this.appBitMapByteArray = appBitMapByteArray;
	}

	public int getDownloadState() {
		return downloadState;
	}

	public void setDownloadState(int downloadState) {
		this.downloadState = downloadState;
	}

}
