package cn.com.tiantong.game72.push.bean;

import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.content.Context;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.SystemClock;

public class ManageKeyguard {
	private static KeyguardManager myKM = null;
	private static PowerManager pm = null;
	private static KeyguardLock myKL = null;
	private static WakeLock mWakelock = null;
//	final static String tag = "ManageKeyguard";

	public static synchronized void initialize(Context context) {
		if (myKM == null) {
			myKM = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
		}
		if (pm == null) {
			pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
		}
		if (myKL == null) {
			myKL = myKM.newKeyguardLock("");
		}
		if (mWakelock == null) {
			mWakelock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "SimpleTimer");
		}
	}

	public static synchronized boolean inKeyguardRestrictedInputMode() {
		if (myKM != null) {
			//LH.LogPrint(tag, "--inKeyguardRestrictedInputMode = " + myKM.inKeyguardRestrictedInputMode());
			return myKM.inKeyguardRestrictedInputMode();
		}
		return false;
	}

	public static void disableKeyguardlock() {
		//LH.LogPrint(tag, "disableKeyguardlock");

		mWakelock.acquire();
		// mWakelock.release();
		myKL.disableKeyguard();
	}

	public static void enableKeyguardlock() {
		//LH.LogPrint(tag, "enableKeyguardlock");
		// WakeLock mWakelock = pm.newWakeLock(PowerManager.ON_AFTER_RELEASE,
		// "SimpleTimer");
		// mWakelock.acquire();
		mWakelock.release();
		mWakelock = null;
		myKL.reenableKeyguard();
		try {
			pm.goToSleep(SystemClock.uptimeMillis());
		} catch (SecurityException localSecurityException) {
			//LH.LogPrint("enableKeyguardlock", "security");
		}
	}
}