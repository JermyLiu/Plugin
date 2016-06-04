package cn.com.tiantong.game72.util;

import java.lang.reflect.Method;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.telephony.TelephonyManager;

public class DualSimUtil {

	public static String getImsi(Context mContext, int card)
			throws NoSuchMethodException {
		String imsi = null;
		boolean platform = false;
		TelephonyManager tm = (TelephonyManager) mContext
				.getSystemService("phone");
		try {
			Class clazz = Class.forName("android.telephony.TelephonyManager");
			Method method = clazz.getMethod("getSubscriberIdGemini",
					new Class[] { Integer.TYPE });
			imsi = (String) method.invoke(tm,
					new Object[] { Integer.valueOf(card) });
			platform = true;
		} catch (Exception localException) {

		} catch (Error localError) {
		}
		if (imsi == null) {
			try {
				Class clazz = Class
						.forName("android.telephony.MSimTelephonyManager");
				Method method = clazz.getMethod("getDefault", null);
				Object object = method.invoke(null, null);
				method = clazz.getMethod("getSubscriberId",
						new Class[] { Integer.TYPE });
				imsi = (String) method.invoke(object,
						new Object[] { Integer.valueOf(card) });
				platform = true;
			} catch (Exception localException1) {
			} catch (Error localError1) {
			}
		}
		if (imsi == null) {
			try {
				Class clazz = Class
						.forName("com.mediatek.telephony.TelephonyManagerEx");
				Method method = clazz.getMethod("getDefault", null);
				Object object = method.invoke(null, null);
				method = clazz.getMethod("getSubscriberId",
						new Class[] { Integer.TYPE });
				imsi = (String) method.invoke(object,
						new Object[] { Integer.valueOf(card) });
				platform = true;
			} catch (Exception localException2) {
			} catch (Error localError2) {
			}
		}
		if (imsi == null) {
			try {
				Class clazz = Class
						.forName("android.telephony.TelephonyManager");
				Method method = clazz.getMethod("getDefault",
						new Class[] { Integer.TYPE });
				Object object = method.invoke(null,
						new Object[] { Integer.valueOf(card) });
				method = clazz.getMethod("getSubscriberId", null);
				imsi = (String) method.invoke(object, null);
				platform = true;
			} catch (Exception localException3) {
			} catch (Error localError3) {
			}
		}
		if (imsi == null) {
			imsi = "";
		}

		if (!platform) {
			throw new NoSuchMethodException();
		}

		return imsi;
	}

	public static int getDefaultSim(Context context)
			throws NoSuchMethodException {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService("connectivity");
		NetworkInfo mobNetInfo = connectivityManager.getNetworkInfo(0);
		try {
			Class clazz = Class.forName("android.net.NetworkInfo");
			Method method = clazz.getMethod("getSimId", null);
			Integer index = (Integer) method.invoke(mobNetInfo, null);
			return index.intValue();
		} catch (Exception localException) {
		} catch (Error localError) {
		}

		try {
			Class clazz = Class
					.forName("android.telephony.MSimTelephonyManager");
			Method method = clazz.getMethod("getDefault", null);
			Object object = method.invoke(null, null);
			method = clazz.getMethod("getPreferredDataSubscription", null);
			Integer index = (Integer) method.invoke(object, null);
			return index.intValue();
		} catch (Exception localException1) {
		} catch (Error localError1) {
		}

		try {
			Class clazz = Class
					.forName("android.telephony.MSimTelephonyManager");
			Method method = clazz.getMethod("getDefault", null);
			Object object = method.invoke(null, null);
			method = clazz.getMethod("getDefaultSubscription", null);
			Integer index = (Integer) method.invoke(object, null);
			return index.intValue();
		} catch (Exception localException2) {
		} catch (Error localError2) {
		}

		try {
			Class clazz = Class.forName("android.telephony.TelephonyManager");
			Method method = clazz.getMethod("getDefault", new Class[0]);
			Object object = method.invoke(null, null);
			Class[] args = { Context.class, Integer.TYPE };
			method = clazz.getMethod("getDefaultSim", args);
			Integer index = (Integer) method.invoke(object, new Object[] {
					context, Integer.valueOf(1) });
			return index.intValue();
		} catch (Exception localException3) {
		} catch (Error localError3) {
		}

		try {
			Class clazz = Class.forName("android.telephony.TelephonyManager");
			Method method = clazz.getMethod("getDefault", new Class[0]);
			Object object = method.invoke(null, null);
			method = clazz.getMethod("getSmsDefaultSim", null);
			return ((Integer) method.invoke(object, null)).intValue();
		} catch (Exception localException4) {
		} catch (Error localError4) {
		}
		try {
			int slotId = Settings.System.getInt(context.getContentResolver(),
					"gprs_connection_setting", -4);
			if ((slotId < 0) || (slotId > 2))
				slotId = 0;
			return slotId;
		} catch (Exception localException5) {
		} catch (Error localError5) {
		}
		throw new NoSuchMethodException();
	}

}
