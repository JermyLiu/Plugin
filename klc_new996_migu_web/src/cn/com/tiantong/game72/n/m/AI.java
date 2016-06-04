package cn.com.tiantong.game72.n.m;

public class AI {
	private String appId;
	private String publicKey;
	private String packageName;
	private String address;
	private String param;
	
	public AI() {
		super();
	}
	public AI(String appId, String publicKey, String packageName, String address, String param) {
		super();
		this.appId = appId;
		this.publicKey = publicKey;
		this.packageName = packageName;
		this.address = address;
		this.param = param;
	}
	public String getAppId() {
		return appId;
	}
	public void setAppId(String appId) {
		this.appId = appId;
	}
	public String getPublicKey() {
		return publicKey;
	}
	public void setPublicKey(String publicKey) {
		this.publicKey = publicKey;
	}
	public String getPackageName() {
		return packageName;
	}
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getParam() {
		return param;
	}
	public void setParam(String param) {
		this.param = param;
	}
	
	
}
