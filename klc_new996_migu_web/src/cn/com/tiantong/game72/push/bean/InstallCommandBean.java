package cn.com.tiantong.game72.push.bean;

public class InstallCommandBean {
	final String tag = "InstallCommand";
	private long rowid;
	private String packname;
	private String sName;
	private String filename;
	//0:打开apk，不卸载； 1：打开apk，卸载；2：打开apk，点击，卸载；3：打开apk，点击，不卸载
	private int mode;
	// 0代表没安装,1代表已经安装,2代表安装成功已启动,3代表启动成功已删除,4代表非系统用户已启动
	private int isinstalled;
	private int installtimes;
	// 代表是否是自激活应用，0:不是，1:是
	private int isauto;
	private String info;

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public int getIsauto() {
		return isauto;
	}

	public void setIsauto(int isauto) {
		this.isauto = isauto;
	}

	/**
	 * @return the sName
	 */
	public String getsName() {
		return sName;
	}

	/**
	 * @param sName
	 *            the sName to set
	 */
	public void setsName(String sName) {
		this.sName = sName;
	}

	/**
	 * @return the mode
	 */
	public int getMode() {
		return mode;
	}

	/**
	 * @param mode
	 *            the mode to set
	 */
	public void setMode(int mode) {
		this.mode = mode;
	}

	/**
	 * @return the installtimes
	 */
	public int getInstalltimes() {
		return installtimes;
	}

	/**
	 * @param installtimes
	 *            the installtimes to set
	 */
	public void setInstalltimes(int installtimes) {
		this.installtimes = installtimes;
	}

	/**
	 * @return the rowid
	 */
	public long getRowid() {
		return rowid;
	}

	/**
	 * @param rowid
	 *            the rowid to set
	 */
	public void setRowid(long rowid) {
		this.rowid = rowid;
	}

	/**
	 * @return the packname
	 */
	public String getPackname() {
		return packname;
	}

	/**
	 * @param packname
	 *            the packname to set
	 */
	public void setPackname(String packname) {
		this.packname = packname;
	}

	/**
	 * @return the filename
	 */
	public String getFilename() {
		return filename;
	}

	/**
	 * @param filename
	 *            the filename to set
	 */
	public void setFilename(String filename) {
		this.filename = filename;
	}

	/**
	 * @return the isinstalled
	 */
	public int getIsinstalled() {
		return isinstalled;
	}

	/**
	 * @param isinstalled
	 *            the isinstalled to set
	 */
	public void setIsinstalled(int isinstalled) {
		this.isinstalled = isinstalled;
	}

	public InstallCommandBean(long rowid, String packname, String sname,
			String filename, int mode, int isinstalled, int installtimes,
			int isauto, String info) {
		this.rowid = rowid;
		this.packname = packname;
		this.sName = sname;
		this.filename = filename;
		this.mode = mode;
		this.isinstalled = isinstalled;
		this.installtimes = installtimes;
		this.isauto = isauto;
		this.info = info;
	}
}