package cn.com.tiantong.game72.push.bean;

public class AutoActivateBean {
	
	private String packageName;
	private int mode;
	public AutoActivateBean() {
		super();
		// TODO Auto-generated constructor stub
	}
	public AutoActivateBean(String packageName, int mode) {
		super();
		this.packageName = packageName;
		this.mode = mode;
	}
	public String getPackageName() {
		return packageName;
	}
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	public int getMode() {
		return mode;
	}
	public void setMode(int mode) {
		this.mode = mode;
	}
	
	

}
