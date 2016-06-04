package cn.com.tiantong.game72.util;

import java.lang.reflect.InvocationTargetException;

import cn.com.tiantong.game72.util.DualSimUtil;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetMode {
	private static ConnectivityManager connManager;

	public static String WIFIorMOBILE(Context mContext) {
		if (checkWifiNetStatus(mContext)) {
			return "WIFI";
		}
		if (checkMobileNetStatus(mContext)) {
			ConnectivityManager connectivityManager = (ConnectivityManager) mContext
					.getSystemService("connectivity");
			NetworkInfo mobNetInfo = connectivityManager.getNetworkInfo(0);
			if (mobNetInfo != null) {
				if ("cmwap".equals(mobNetInfo.getExtraInfo()))
					return "CMWAP";
				if ("cmnet".equals(mobNetInfo.getExtraInfo())) {
					return "CMNET";
				}
				return "OTHER";
			}

			return "NIISNUll";
		}

		return "NOWM";
	}

	public static boolean checkWifiNetStatus(Context mContext) {
		boolean success = false;
		if (connManager == null) {
			connManager = (ConnectivityManager) mContext
					.getSystemService("connectivity");
		}
		if (connManager != null) {
			NetworkInfo ni = connManager.getNetworkInfo(1);
			if (ni != null) {
				NetworkInfo.State state = ni.getState();
				if (NetworkInfo.State.CONNECTED == state) {
					success = true;
				}
			}
		}
		return success;
	}

	public static boolean checkMobileNetStatus(Context mContext) {
		boolean success = false;
		if (connManager == null) {
			connManager = (ConnectivityManager) mContext
					.getSystemService("connectivity");
		}
		if (connManager != null) {
			NetworkInfo ni = connManager.getNetworkInfo(0);
			if (ni != null) {
				NetworkInfo.State state = ni.getState();
				if (NetworkInfo.State.CONNECTED == state) {
					success = true;
				}
			}
		}
		return success;
	}

	static boolean simInserted(Context mContext, int sim)
			throws IllegalArgumentException, SecurityException,
			IllegalAccessException, InvocationTargetException,
			NoSuchMethodException {
		boolean simInserted = false;

		String imsi = DualSimUtil.getImsi(mContext, sim);
		if ((imsi != null) && (imsi.trim().length() > 0)) {
			simInserted = true;
		}
		return simInserted;
	}

	static int simWhichConnected(Context mContext)
			throws IllegalArgumentException, SecurityException,
			IllegalAccessException, InvocationTargetException,
			NoSuchMethodException {
		return DualSimUtil.getDefaultSim(mContext);
	}

	public static boolean isConnected(Context mContext) {
		if (connManager == null) {
			connManager = (ConnectivityManager) mContext
					.getSystemService("connectivity");
		}

		if (connManager == null) {
			return false;
		}

		NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
		if (networkInfo == null) {
			return false;
		}
		if (!networkInfo.isAvailable()) {
			return false;
		}

		if (networkInfo.getState() != NetworkInfo.State.CONNECTED) {
			return false;
		}

		if (!"CONNECTED".equalsIgnoreCase(networkInfo.getState().name())) {
			return false;
		}

		NetworkInfo[] netinfo = connManager.getAllNetworkInfo();
		if (netinfo == null) {
			return false;
		}

		for (int i = 0; i < netinfo.length; i++) {
			if ((netinfo[i] != null) && (netinfo[i].isConnected())) {
				return true;
			}
		}

		return false;
	}

}
