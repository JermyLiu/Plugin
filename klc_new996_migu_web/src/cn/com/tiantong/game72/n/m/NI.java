package cn.com.tiantong.game72.n.m;

/**
 * 网络信息�?
 */
public class NI {
	
	private boolean isConnectToNetwork = true;
	private boolean isProxy = false;
	private String proxyName = "";
	private String proxyHost = "";
	private int proxyPort = 0;
	
	
	public boolean isConnectToNetwork() {
		return isConnectToNetwork;
	}
	public void setConnectToNetwork(boolean isConnectToNetwork) {
		this.isConnectToNetwork = isConnectToNetwork;
	}
	public boolean isProxy() {
		return isProxy;
	}
	public void setProxy(boolean isProxy) {
		this.isProxy = isProxy;
	}
	public String getProxyName() {
		return proxyName;
	}
	public void setProxyName(String proxyName) {
		this.proxyName = proxyName;
	}
	public String getProxyHost() {
		return proxyHost;
	}
	public void setProxyHost(String proxyHost) {
		this.proxyHost = proxyHost;
	}
	
	public int getProxyPort() {
		return proxyPort;
	}
	public void setProxyPort(int proxyPort) {
		this.proxyPort = proxyPort;
	}

}
