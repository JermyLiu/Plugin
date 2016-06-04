package cn.com.tiantong.game72.util;

import java.security.MessageDigest;

public class MD5 {
	private static MessageDigest md5 = null;

	static {
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (Exception localException) {
		}
	}

	public static byte[] md5(byte[] src) {
		synchronized (md5) {
			return md5.digest(src);
		}
	}

	public static String bytes2hex(byte[] bytes) {
		StringBuffer sb = new StringBuffer(bytes.length * 2);
		for (int i = 0; i < bytes.length; i++) {
			String hex = Integer.toHexString(bytes[i]).toUpperCase();
			sb.append(hex.length() > 2 ? hex.substring(6, 8)
					: hex.length() == 1 ? "0" + hex : hex);
			sb.append("");
		}
		return sb.toString();
	}
}
