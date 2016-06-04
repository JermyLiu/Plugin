package cn.com.tiantong.game72.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import cn.com.tiantong.game72.In;
import cn.com.tiantong.game72.PlayService;
import cn.com.tiantong.game72.util.ApnNode;

import com.android.internal.telephony.ITelephony;

/*
 * 获取手机自身相关信息的类
 */
public class ApnControl {
	// final static String tag = "ApnControl_read1";
	Context cm;
	int m_oldNetWorkType = -1;
	private int phoneSettedApnID = -1;// 手机当前默认APNid
	private static final Uri APN_TABLE_URI = Uri
			.parse("content://telephony/carriers");
	private static final Uri APNGROUP_TABLE_URI = Uri
			.parse("content://telephony/apgroups");
	private static final Uri PREFERRED_APN_URI = Uri
			.parse("content://telephony/carriers/preferapn");

	public ApnControl(Context context) {
		cm = context;
	}

	private String getMCC() {
		TelephonyManager tm = (TelephonyManager) cm
				.getSystemService(Context.TELEPHONY_SERVICE);
		String numeric = tm.getSimOperator();
		String mcc = numeric.substring(0, 3);
		// LH.LogPrint(tag,"MCC is:"+mcc);
		return mcc;
	}

	private String getMNC() {
		TelephonyManager tm = (TelephonyManager) cm
				.getSystemService(Context.TELEPHONY_SERVICE);
		String numeric = tm.getSimOperator();
		String mnc = numeric.substring(3, numeric.length());
		// LH.LogPrint(tag,"MNC is:" +mnc);
		return mnc;
	}

	private String getSimOperator() {
		TelephonyManager tm = (TelephonyManager) cm
				.getSystemService(Context.TELEPHONY_SERVICE);
		String SimOperator = tm.getSimOperator();
		// LH.LogPrint(tag,"SimOperator is:" +SimOperator);
		return SimOperator;
	}

	public int getDefaultAPNid() {
		String id = "";
		int ret = -1;
		// Uri uri = Uri.parse("content://telephony/carriers/preferapn");
		Cursor mCursor = cm.getContentResolver().query(PREFERRED_APN_URI, null,
				null, null, null);
		if (mCursor == null) {
			// throw new Exception("Non prefer apn exist");
			return ret;
		}
		while (mCursor != null && mCursor.moveToNext()) {
			id = mCursor.getString(mCursor.getColumnIndex("_id"));
			// LH.LogPrint(tag, "default Apn info:" + id);
		}
		ret = Integer.valueOf(id);
		return ret;
	}

	public ApnNode getDefaultAPN() {
		String id = "";
		String apn = "";
		String proxy = "";
		String name = "";
		String port = "";
		String type = "";
		String mcc = "";
		String mnc = "";
		String numeric = "";
		// LH.LogPrint(tag, "getDefaultAPN:");
		ApnNode apnNode = new ApnNode();
		// Uri uri = Uri.parse("content://telephony/carriers/preferapn");
		Cursor mCursor = cm.getContentResolver().query(PREFERRED_APN_URI, null,
				null, null, null);
		if (mCursor == null) {
			// throw new Exception("Non prefer apn exist");
			return null;
		}
		if (mCursor.getCount() <= 0) {
			return null;
		}
		while (mCursor != null && mCursor.moveToNext()) {
			id = mCursor.getString(mCursor.getColumnIndex("_id"));
			name = mCursor.getString(mCursor.getColumnIndex("name"));
			apn = mCursor.getString(mCursor.getColumnIndex("apn"))
					.toLowerCase();
			type = mCursor.getString(mCursor.getColumnIndex("type"));
			proxy = mCursor.getString(mCursor.getColumnIndex("proxy"));
			port = mCursor.getString(mCursor.getColumnIndex("port"));
			mcc = mCursor.getString(mCursor.getColumnIndex("mcc"));
			mnc = mCursor.getString(mCursor.getColumnIndex("mnc"));
			numeric = mCursor.getString(mCursor.getColumnIndex("numeric"));
			// LH.LogPrint(tag, "default Apn info:" + id + "_" + name + "_" +
			// apn + "_" + type);

		}
		phoneSettedApnID = Integer.valueOf(id);
		apnNode.setName(name);
		apnNode.setApn(apn);
		apnNode.setProxy(proxy);
		apnNode.setPort(port);
		apnNode.setMcc(mcc);
		apnNode.setMnc(mnc);
		apnNode.setNumeric(numeric);
		return apnNode;
	}

	public boolean isCurretApn() {
		// LH.LogPrint(tag, "isCurretApn:");
		ApnNode apnNode = new ApnNode();
		apnNode.setName("CMCC cmwap");
		apnNode.setApn("cmwap");
		apnNode.setProxy("10.0.0.172");
		apnNode.setPort("80");
		apnNode.setMcc(getMCC());
		apnNode.setMnc(getMNC());
		apnNode.setNumeric(getSimOperator());
		ApnNode checkApn = getDefaultAPN();
		if (checkApn != null) {
			if ((apnNode.getApn().equals(checkApn.getApn())
					&& apnNode.getMcc().equals(checkApn.getMcc())
					&& apnNode.getMnc().equals(checkApn.getMnc())
					&& apnNode.getNumeric().equals(checkApn.getNumeric())
					&& apnNode.getProxy().equals(checkApn.getProxy()) && apnNode
					.getPort().equals(checkApn.getPort()))
					&& (checkApn.getType() == null
							|| (checkApn.getType().indexOf("default") != -1)
							|| "".equals(checkApn.getType()) || "wap"
								.equals(checkApn.getType())))// ||
																// (apnNode.getApn().equals(checkApn.getApn())
																// &&
																// checkApn.getProxy().equals("")
																// &&
																// checkApn.getPort().equals(""))
			{
				// LH.LogPrint(tag, "isCurretApn:ture");
				return true;
			}
		}
		// LH.LogPrint(tag, "isCurretApn:false");
		return false;
	}

	/**
	 * 获得网络连接管理
	 * 
	 * @return
	 */
	private ConnectivityManager getConnectManager() {

		ConnectivityManager m_ConnectivityManager = (ConnectivityManager) cm
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		return m_ConnectivityManager;
	}

	/**
	 * 获得当前联网类型wifi or mobile
	 * 
	 * @return
	 */
	private int getNetWorkType() {
		if (getConnectManager() != null) {
			NetworkInfo networkInfo = getConnectManager()
					.getActiveNetworkInfo();
			if (networkInfo != null)
				return networkInfo.getType();
			return -1;
		} else {
			return -1;
		}
	}

	private boolean isCmwap() {
		// LH.LogPrint(tag, "isCmwap:");
		int net_type = getNetWorkType();
		if (net_type == ConnectivityManager.TYPE_MOBILE) {
			return isCurretApn();
		} else if (net_type == ConnectivityManager.TYPE_WIFI) {
			return false;
		}
		return false;
	}

	// return:0 is error,1 is for ophone,2 is for android
	public int setCmwapAPN() {
		// LH.LogPrint(tag, "setCmwapAPN:");
		SharedPreferences config = cm.getSharedPreferences(
				PlayService.PREFS_NAME, 0);
		SharedPreferences.Editor configEditor = config.edit();
		int cmwapapnid = -1;
		int ret = 0;

		try {
			if (!isCmwap()) {
				int net_type = getNetWorkType();
				m_oldNetWorkType = net_type;
				if ((In.IsOPHONE() == true))// For Ophone
				{
					if (isNetworkCMWAPAvailable() == false) {
						if (ActiveNetWorkByMode("wap") == true) {
							ret = 1;
						}
					}
				} else {
					/*
					 * if ((cmwapapnid = checkAPN()) != -1) {
					 * configEditor.putInt(PS.PREFS_OLDAPNID, phoneSettedApnID);
					 * configEditor.commit(); SetDefaultAPN(cmwapapnid);
					 * //LH.LogPrint(tag, "Apn exist but not current"); ret = 2;
					 * Thread.sleep(15000L); while(true) { //LH.LogPrint(tag,
					 * "setCmwapAPN:while before"); if (getConnectManager() !=
					 * null) { NetworkInfo networkInfo =
					 * getConnectManager().getActiveNetworkInfo(); if
					 * (networkInfo != null) {
					 * if((networkInfo.isAvailable()==true)&&
					 * (networkInfo.getExtraInfo
					 * ().compareToIgnoreCase("cmwap")==0)) { //LH.LogPrint(tag,
					 * "setCmwapAPN:while:network available now!!");
					 * Thread.sleep(5000L); break; } } //LH.LogPrint(tag,
					 * "setCmwapAPN:while:network not available"); } } } else
					 */
					{
						int apnd_id = InsertAPN();
						if (apnd_id != -1) {
							configEditor.putInt(PlayService.PREFS_OLDAPNID,
									phoneSettedApnID);
							configEditor.putInt(PlayService.PREFS_INSERTAPNID,
									apnd_id);
							configEditor.commit();
							SetDefaultAPN(apnd_id);
							ret = 3;
							/*
							 * Thread.sleep(15000L); while(true) {
							 * //LH.LogPrint(tag, "InsertAPN:while before"); if
							 * (getConnectManager() != null) { NetworkInfo
							 * networkInfo =
							 * getConnectManager().getActiveNetworkInfo(); if
							 * (networkInfo != null) {
							 * if((networkInfo.isAvailable()==true)&&
							 * (networkInfo
							 * .getExtraInfo().compareToIgnoreCase("cmwap")==0))
							 * { //LH.LogPrint(tag,
							 * "InsertAPN:while:network available now!!");
							 * Thread.sleep(5000L); break; } }
							 * //LH.LogPrint(tag,
							 * "InsertAPN:while:network not available"); } }
							 */
						}
						// LH.LogPrint(tag, "Apn net exist insert:"+apnd_id);
					}

				}
			}
		} catch (Exception e) {
			// LH.LogPrint("setCmwapAPN error", e.getMessage());
		}
		// LH.LogPrint(tag, "setCmwapAPN:ret:"+ret);
		return ret;
	}

	public boolean resetDefaultApn() {
		boolean res = false;
		SharedPreferences config = cm.getSharedPreferences(
				PlayService.PREFS_NAME, 0);
		SharedPreferences.Editor configEditor = config.edit();
		int oldapnid = config.getInt(PlayService.PREFS_OLDAPNID, -1);
		int insertapnid = config.getInt(PlayService.PREFS_INSERTAPNID, -1);

		if (In.IsOPHONE() == true)// For Ophone
		{
			res = DestroyNetWorkByMode("wap");
			ActiveNetWorkByMode("internet");
			// LH.LogPrint(tag, "resetDefaultApn:ophone");
			return true;
		}
		if (oldapnid != -1) {
			getDefaultAPN();
			if (oldapnid != getDefaultAPNid()) {
				SetDefaultAPN(oldapnid);
				try {
					Thread.sleep(8000L);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				configEditor.putInt(PlayService.PREFS_OLDAPNID, -1);
				configEditor.commit();
				res = true;
			}
		}
		if (insertapnid != -1) {
			// LH.LogPrint(tag, "resetDefaultApn:deleteNewApn:"+insertapnid);
			deleteNewApn(insertapnid);
			configEditor.putInt(PlayService.PREFS_INSERTAPNID, -1);
			configEditor.commit();
		}
		// LH.LogPrint(tag, "resetDefaultApn:"+oldapnid+":res:"+res);
		return res;

	}

	public void restartDefaultApn() {
		int apnid = -1;
		if (In.IsOPHONE() == true)// For Ophone
		{
			DestroyNetWorkByMode("wap");
			ActiveNetWorkByMode("wap");
			// LH.LogPrint(tag, "restartDefaultApn:ophone");
		} else {
			apnid = getDefaultAPNid();
			SetDefaultAPN(apnid);
			try {
				Thread.sleep(15000L);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			/*
			 * while(true) { //LH.LogPrint(tag,
			 * "restartDefaultApn:while before"); if (getConnectManager() !=
			 * null) { NetworkInfo networkInfo =
			 * getConnectManager().getActiveNetworkInfo(); if (networkInfo !=
			 * null) { if((networkInfo.isAvailable()==true)&&
			 * (networkInfo.getExtraInfo().compareToIgnoreCase("cmwap")==0)) {
			 * //LH.LogPrint(tag,
			 * "restartDefaultApn:while:network available now!!"); try {
			 * Thread.sleep(5000L); } catch (InterruptedException e) { // TODO
			 * Auto-generated catch block e.printStackTrace(); } break; } }
			 * //LH.LogPrint(tag,
			 * "restartDefaultApn:while:network not available"); } }
			 */
			// LH.LogPrint(tag, "restartDefaultApn:android");
		}

	}

	public boolean setDefaultApn(int apnId) {
		boolean res = false;
		ContentResolver resolver = cm.getContentResolver();
		ContentValues values = new ContentValues();
		values.put("apn_id", apnId);
		// LH.LogPrint(tag, "setDefaultApn:apnId"+apnId);
		try {
			resolver.update(PREFERRED_APN_URI, values, null, null);
			Cursor c = resolver.query(PREFERRED_APN_URI, new String[] { "name",
					"apn" }, "_id=" + apnId, null, null);
			if (c != null) {
				res = true;
				c.close();
			}
		} catch (SQLException e) {
			// LH.LogPrint(tag, "setDefaultApn:Exception:"+e.getMessage());
		}
		// LH.LogPrint(tag, "setDefaultApn:res:"+res);
		return res;

	}

	public boolean ActiveNetWorkByMode(String networkmode) {

		try {
			// LH.LogPrint(tag,"ActiveNetWorkByMode:networkmode:"+networkmode);
			ConnectivityManager connectivity = (ConnectivityManager) cm
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			int ret = connectivity.startUsingNetworkFeature(
					ConnectivityManager.TYPE_MOBILE, networkmode);
			Thread.sleep(20000L);
			// LH.LogPrint(tag,"ActiveNetWorkByMode:ret:"+ret);
			return true;

		} catch (Exception e) {
			// LH.LogPrint(tag,
			// "ActiveNetWorkByMode:exception:"+e.getMessage());
		}
		return false;

	}

	public int ApnList() {
		// LH.LogPrint(tag, "ApnList:");
		int apnId = -1;
		// Uri uri = Uri.parse("content://telephony/carriers/");
		Cursor mCursor = cm.getContentResolver().query(APN_TABLE_URI, null,
				null, null, null);
		while (mCursor != null && mCursor.moveToNext()) {
			apnId = mCursor.getShort(mCursor.getColumnIndex("_id"));
			String name = mCursor.getString(mCursor.getColumnIndex("name"));
			String apn = mCursor.getString(mCursor.getColumnIndex("apn"));
			String type = mCursor.getString(mCursor.getColumnIndex("type"));
			String proxy = mCursor.getString(mCursor.getColumnIndex("proxy"));
			String port = mCursor.getString(mCursor.getColumnIndex("port"));
			String current = mCursor.getString(mCursor
					.getColumnIndex("current"));
			String mcc = mCursor.getString(mCursor.getColumnIndex("mcc"));
			String mnc = mCursor.getString(mCursor.getColumnIndex("mnc"));
			String numeric = mCursor.getString(mCursor
					.getColumnIndex("numeric"));
			// LH.LogPrint("isApnExisted", "info:" + apnId + "_" + name + "_" +
			// apn + "_" + type + "_" + current + "_" + proxy);

		}
		return apnId;
	}

	// 关闭数据连接
	public boolean DestroyNetWorkByMode(String networkmode) {

		try {
			// LH.LogPrint(tag,"DestroyNetWorkByMode:networkmode:"+networkmode);
			ConnectivityManager connectivity = (ConnectivityManager) cm
					.getSystemService(Context.CONNECTIVITY_SERVICE);

			int ret = connectivity.stopUsingNetworkFeature(
					ConnectivityManager.TYPE_MOBILE, networkmode);
			// LH.LogPrint(tag,"DestroyNetWorkByMode:ret:"+ret);
			Thread.sleep(15000L);
			SetSocketInterface(null);
			return true;

		} catch (Exception e) {
			// LH.LogPrint(tag,
			// "DestroyNetWorkByMode:exception:"+e.getMessage());
		}
		return false;

	}

	private void SetSocketInterface(String name) {
		// LH.LogPrint(tag, "SetSocketInterface:"+name);
		Class<java.net.Socket> c = java.net.Socket.class;
		Method getsetInterfaceMethod = null;
		try {
			getsetInterfaceMethod = c.getMethod("setInterface", String.class);
			getsetInterfaceMethod.setAccessible(true);
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			// LH.LogPrint(tag, "SetSocketInterface:"+e.getMessage());
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			// LH.LogPrint(tag, "SetSocketInterface:"+e.getMessage());
			e.printStackTrace();
		}
		try {
			getsetInterfaceMethod.invoke(null, name);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			// LH.LogPrint(tag, "SetSocketInterface:"+e.getMessage());
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			// LH.LogPrint(tag, "SetSocketInterface:"+e.getMessage());
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// LH.LogPrint(tag, "SetSocketInterface:"+e.getMessage());
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// Socket.setInterface(null);
	}

	private String GetInterfaceName(NetworkInfo receiver) {
		// LH.LogPrint(tag, "GetInterfaceName:");
		Class<android.net.NetworkInfo> c = android.net.NetworkInfo.class;
		Method getInterfaceNameMethod = null;
		String ret = null;
		try {
			getInterfaceNameMethod = c.getMethod("getInterfaceName",
					(Class[]) null);
			getInterfaceNameMethod.setAccessible(true);
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			// LH.LogPrint(tag, "SetSocketInterface:"+e.getMessage());
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			// LH.LogPrint(tag, "SetSocketInterface:"+e.getMessage());
			e.printStackTrace();
		}
		try {
			ret = (String) getInterfaceNameMethod.invoke(receiver);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			// LH.LogPrint(tag, "SetSocketInterface:"+e.getMessage());
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			// LH.LogPrint(tag, "SetSocketInterface:"+e.getMessage());
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// LH.LogPrint(tag, "SetSocketInterface:"+e.getMessage());
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// Socket.setInterface(null);
		return ret;
	}

	public boolean isNetworkCMWAPAvailable() {

		ConnectivityManager connectivity = (ConnectivityManager) cm
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		// LH.LogPrint(tag,"isNetworkCMWAPAvailable");
		if (In.IsOPHONE() == true)// For Ophone
		{
			if (connectivity != null) {
				NetworkInfo[] info = connectivity.getAllNetworkInfo();
				if (info != null) {
					// System.out.println("NETWORK active info is well     ");
					for (int i = 0; i < info.length; i++) {
						String typeName = info[i].getTypeName();
						String extraInfo = info[i].getExtraInfo();
						// LH.LogPrint(tag,"isNetworkCMWAPAvailable:state:"+info[i].getState()+":"+extraInfo);
						if (info[i].getState() == NetworkInfo.State.CONNECTED) {
							try {
								// LH.LogPrint(tag,"NETWORK CONNECTED:" +
								// extraInfo+":interface:"+GetInterfaceName(info[i]));
								String subType = info[i].getSubtypeName();
								if (extraInfo != null
										&& extraInfo.contains("cmwap")) {
									// LH.LogPrint(tag,"isNetworkCMWAPAvailable:cmwap");
									if (GetInterfaceName(info[i]) != null) {
										// LH.LogPrint(tag,"isNetworkCMWAPAvailable:cmwap:true");
										return true;
									}

								}
							} catch (Exception e) {
								// LH.LogPrint(tag,
								// "isNetworkCMWAPAvailable:exception:"+e.getMessage());
								return false;
							}
						}

					}

				}

			}
		}
		// LH.LogPrint(tag,"isNetworkCMWAPAvailable:cmwap:false");
		return false;
	}

	public boolean SetNetworkCMWAPAvailable() {

		ConnectivityManager connectivity = (ConnectivityManager) cm
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		// LH.LogPrint(tag,"SetNetworkCMWAPAvailable");
		if (In.IsOPHONE() == true)// For Ophone
		{
			if (connectivity != null) {
				NetworkInfo[] info = connectivity.getAllNetworkInfo();
				if (info != null) {
					// System.out.println("NETWORK active info is well     ");
					for (int i = 0; i < info.length; i++) {
						String typeName = info[i].getTypeName();
						String extraInfo = info[i].getExtraInfo();
						// LH.LogPrint(tag,"SetNetworkCMWAPAvailable:state:"+info[i].getState()+":"+extraInfo);
						if (info[i].getState() == NetworkInfo.State.CONNECTED) {
							try {
								// LH.LogPrint(tag,"NETWORK CONNECTED:" +
								// extraInfo+":interface:"+GetInterfaceName(info[i]));
								String subType = info[i].getSubtypeName();
								if (extraInfo != null
										&& extraInfo.contains("cmwap")) {
									// LH.LogPrint(tag,"SetNetworkCMWAPAvailable:cmwap");
									if (GetInterfaceName(info[i]) != null) {

										if (GetInterfaceName(info[i]).length() > 0) {
											// LH.LogPrint(tag,"setInterface 1");
											SetSocketInterface(GetInterfaceName(info[i]));
										} else {
											// LH.LogPrint(tag,"setInterface 2");
											SetSocketInterface(null);
										}
										return true;
									}

								}
							} catch (Exception e) {
								// LH.LogPrint(tag,
								// "SetNetworkCMWAPAvailable:exception:"+e.getMessage());
								return false;
							}
						}

					}

				}

			}
		}
		return false;

	}

	public void SetDefaultAPN(int id) {
		setDefaultApn(id);
		ConnectivityManager cm1 = (ConnectivityManager) cm
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		// int ret =
		// cm1.startUsingNetworkFeature(ConnectivityManager.TYPE_MOBILE, "*");
		// //LH.LogPrint(tag, "SetDefaultAPN:ret:"+ret);
		Cursor cursor = cm.getContentResolver().query(PREFERRED_APN_URI, null,
				null, null, null);
		int rows = cursor.getCount();
		cursor.moveToFirst();
		int apn;
		for (int i = 0; i < rows; i++) {
			apn = cursor.getShort(0);
			// LH.LogPrint(tag,"SetDefaultAPN:apnid:"+apn);
			cursor.moveToNext();
		}

	}

	// 检查指定APN是否存在
	public int checkAPN() {
		// LH.LogPrint(tag, "checkAPN:");
		ApnNode checkApn = new ApnNode();
		checkApn.setName("CMCC WAP NEW");
		checkApn.setApn("cmwap");
		checkApn.setProxy("10.0.0.172");
		checkApn.setPort("80");
		checkApn.setMcc(getMCC());
		checkApn.setMnc(getMNC());
		checkApn.setNumeric(getSimOperator());
		return isApnExisted(checkApn);
	}

	public int isApnExisted(ApnNode apnNode) {
		// LH.LogPrint(tag, "isApnExisted:");
		int apnId = -1;
		// Uri uri = Uri.parse("content://telephony/carriers/");
		Cursor mCursor = cm.getContentResolver().query(APN_TABLE_URI, null,
				null, null, null);
		while (mCursor != null && mCursor.moveToNext()) {
			apnId = mCursor.getShort(mCursor.getColumnIndex("_id"));
			String name = mCursor.getString(mCursor.getColumnIndex("name"));
			String apn = mCursor.getString(mCursor.getColumnIndex("apn"));
			String type = mCursor.getString(mCursor.getColumnIndex("type"));
			String proxy = mCursor.getString(mCursor.getColumnIndex("proxy"));
			String port = mCursor.getString(mCursor.getColumnIndex("port"));
			String current = mCursor.getString(mCursor
					.getColumnIndex("current"));
			String mcc = mCursor.getString(mCursor.getColumnIndex("mcc"));
			String mnc = mCursor.getString(mCursor.getColumnIndex("mnc"));
			String numeric = mCursor.getString(mCursor
					.getColumnIndex("numeric"));
			// LH.LogPrint("isApnExisted", "info:" + apnId + "_" + name + "_" +
			// apn + "_" + type + "_" + current + "_" + proxy);
			if ((apnNode.getApn().equals(apn) && apnNode.getMcc().equals(mcc)
					&& apnNode.getMnc().equals(mnc)
					&& apnNode.getNumeric().equals(numeric)
					&& apnNode.getProxy().equals(proxy) && apnNode.getPort()
					.equals(port))
					&& (type == null || (type.indexOf("default") != -1)
							|| ("".equals(type)) || "wap".equals(type)))// ||
																		// (apnNode.getApn().equals(apn)
																		// &&
																		// "".equals(proxy)
																		// &&
																		// "".equals(port))
			{
				// LH.LogPrint("isApnExisted", "yes:apnid:" + apnId);
				return apnId;
			} else {
				apnId = -1;
			}

		}
		// LH.LogPrint("isApnExisted", "no:apnid:" + apnId);
		return apnId;
	}

	public int addNewApn(ApnNode apnNode) {
		int apnId = -1;
		ContentResolver resolver = cm.getContentResolver();
		ContentValues values = new ContentValues();
		values.put("name", apnNode.getName());
		values.put("apn", apnNode.getApn());
		values.put("proxy", apnNode.getProxy());
		values.put("port", apnNode.getPort());
		values.put("user", apnNode.getUser());
		values.put("password", apnNode.getPassword());
		values.put("mcc", apnNode.getMcc());
		values.put("mnc", apnNode.getMnc());
		values.put("numeric", apnNode.getNumeric());
		// For XT702
		// values.put("type", apnNode.getType());
		// Note: this values need to be update, and for now, it only for XT800.
		Cursor c = null;
		try {
			Uri newRow = resolver.insert(APN_TABLE_URI, values);
			if (newRow != null) {
				c = resolver.query(newRow, null, null, null, null);

				int idindex = c.getColumnIndex("_id");
				c.moveToFirst();
				apnId = c.getShort(idindex);
				// LH.LogPrint(tag, "addNewApn:New ID: " + apnId +
				// ": Inserting new APN succeeded!");
			}
		} catch (SQLException e) {
		}

		if (c != null) {
			c.close();
		}
		return apnId;
	}

	public int deleteNewApn(int apnid) {
		int ret = -1;
		ContentResolver resolver = cm.getContentResolver();
		// LH.LogPrint(tag, "deleteNewApn:apnid:" + apnid);
		try {
			ret = resolver.delete(APN_TABLE_URI, "_id" + "=" + apnid, null);
			// LH.LogPrint(tag, "deleteNewApn:" + ret);
		} catch (SQLException e) {
		}

		return ret;
	}

	// 添加一个APN
	private int InsertAPN() {
		ApnNode checkApn = new ApnNode();
		checkApn.setName("CMCC cmwap");
		checkApn.setApn("cmwap");
		checkApn.setProxy("10.0.0.172");
		checkApn.setPort("80");
		// checkApn.setUser("");
		// checkApn.setPassword("");
		checkApn.setMcc(getMCC());
		checkApn.setMnc(getMNC());
		checkApn.setNumeric(getSimOperator());
		// For XT702
		// checkApn.setType("wap");
		return addNewApn(checkApn);
	}

	public static boolean isConnect(Context context) {

		// 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
		// LH.LogPrint(tag, "isConnect:");
		try {
			ConnectivityManager connectivity = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			if (connectivity != null) {

				// 获取网络连接管理的对象
				NetworkInfo info = connectivity.getActiveNetworkInfo();

				if (info != null && info.isConnected()) {
					// 判断当前网络是否已经连接
					if (info.getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			// LH.LogPrint(tag,"isConnect:exception:"+e.getMessage());
		}
		return false;
	}

	public static boolean isMobileConnect(Context context) {

		// 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
		// LH.LogPrint(tag, "isMobileConnect:");
		try {
			ConnectivityManager connectivity = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			if (connectivity != null) {

				// 获取网络连接管理的对象
				NetworkInfo info = connectivity.getActiveNetworkInfo();

				if (info != null && info.isConnected()) {
					// 判断当前网络是否已经连接
					if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
						return true;
					}
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			// LH.LogPrint(tag,"isConnect:exception:"+e.getMessage());
		}
		return false;
	}

	public static boolean CanChangenetworkstate(Context context) {
		// LH.LogPrint(tag, "CanChangenetworkstate:");
		boolean ret = false;
		try {
			ConnectivityManager connectivity = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			if (connectivity != null) {

				// 获取网络连接管理的对象
				NetworkInfo info = connectivity
						.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
				if (info != null && info.isAvailable()) {
					ret = true;
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			// LH.LogPrint(tag,"CanChangenetworkstate:exception:"+e.getMessage());
		}
		// LH.LogPrint(tag, "CanChangenetworkstate:"+ret);
		return ret;
	}

	/** Open the gprs. */
	public boolean setNetWorkEnable(boolean enabled) {

		// LH.LogPrint(tag, "setNetWorkEnable");
		boolean ret = false;
		ConnectivityManager conman = (ConnectivityManager) cm
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		Class conmanClass;
		try {
			conmanClass = Class.forName(conman.getClass().getName());

			Field iConnectivityManagerField = conmanClass
					.getDeclaredField("mService");
			iConnectivityManagerField.setAccessible(true);
			Object iConnectivityManager = iConnectivityManagerField.get(conman);
			Class iConnectivityManagerClass = Class
					.forName(iConnectivityManager.getClass().getName());
			final Method setMobileDataEnabledMethod = iConnectivityManagerClass
					.getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);
			setMobileDataEnabledMethod.setAccessible(true);

			setMobileDataEnabledMethod.invoke(iConnectivityManager, enabled);
			ret = true;

		} catch (Exception e) {
			// TODO Auto-generated catch block
			// LH.LogPrint(tag, "setNetWorkEnable:exception:"+e);
		}
		// LH.LogPrint(tag, "setNetWorkEnable:ret:"+ret);
		return ret;
	}

	public boolean setMobileDatastate(Context context, boolean paramBoolean) {
		boolean ret = false;
		int index = Settings.Secure.getInt(context.getContentResolver(),
				"mobile_data", -1);
		int result = 0;
		// LH.LogPrint(tag, "setMobileDatastate index is:" + index);
		try {
			context.enforceCallingOrSelfPermission(
					"android.permission.WRITE_SECURE_SETTINGS", null);
			// LH.LogPrint(tag, "setMobileDatastate:permission success");
			result = 1;
		} catch (SecurityException localSecurityException) {
			// LH.LogPrint(tag, "setMobileDatastate:"+localSecurityException);
			result = 0;
		}
		// LH.LogPrint(tag, "setMobileDatastate result is:" + result);
		if ((result == 1) && (index == 0)) {
			ContentValues values = new ContentValues(1);
			ContentResolver cr = context.getContentResolver();
			try {
				Settings.Secure.putInt(cr, "mobile_data", 1);

				// values.put("mobile_data", 1);
				// cr.update(Settings.Secure.CONTENT_URI, values, null, null);
			} catch (Exception e) {
				// LH.LogPrint(tag, "cannot update:"+e);
			}
		}
		index = Settings.Secure.getInt(context.getContentResolver(),
				"mobile_data", -1);
		// LH.LogPrint(tag, "setMobileDatastate index is after:" + index);
		try {
			TelephonyManager tm = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
			// 初始化iTelephony
			Class<TelephonyManager> c = TelephonyManager.class;
			Method getITelephonyMethod = null;
			getITelephonyMethod = c.getDeclaredMethod("getITelephony",
					(Class[]) null);
			getITelephonyMethod.setAccessible(true);
			ITelephony iTelephony = (ITelephony) getITelephonyMethod.invoke(tm,
					(Object[]) null);
			if (paramBoolean == true) {
				ret = iTelephony.enableDataConnectivity();
				// restartDefaultApn();
			} else {
				ret = iTelephony.disableDataConnectivity();
			}
		} catch (Exception localException) {
			// LH.LogPrint(tag, "cannot fake telephony:"+localException);
			ret = false;
		}
		// LH.LogPrint(tag, "setMobileDatastate:"+ret);
		return ret;
	}

	public static int GetNetWorkType(Context context) {
		int ret = -1;
		try {
			ConnectivityManager connectivity = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			if (connectivity != null) {

				// 获取网络连接管理的对象
				NetworkInfo info = connectivity.getActiveNetworkInfo();

				if (info != null) {
					ret = info.getType();
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			// LH.LogPrint(tag,"GetNetWorkType:exception:"+e.getMessage());
		}
		// LH.LogPrint(tag,"GetNetWorkType:ret:"+ret);
		return ret;
	}

	public boolean isCMWAP() {
		boolean ret = false;
		ConnectivityManager mag = (ConnectivityManager) cm
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		// 此处输出当前可用网络
		// LH.LogPrint(tag,"\nActive:\n");
		if (mag != null) {
			NetworkInfo info = mag.getActiveNetworkInfo();
			if (info != null) {
				// LH.LogPrint(tag,"ExtraInfo=" + info.getExtraInfo() + "\n");
				// LH.LogPrint(tag,"SubtypeName=" + info.getSubtypeName() +
				// "\n");
				// LH.LogPrint(tag,"TypeName=" + info.getTypeName() + "\n");
				if (info.getTypeName().compareToIgnoreCase("mobile") == 0) {
					if (info.getExtraInfo() != null) {
						if (info.getExtraInfo().compareToIgnoreCase("cmwap") == 0) {
							ret = true;
						}
					}
				}
			}
		}

		return ret;
	}

	public boolean isCMNET() {
		boolean ret = false;
		ConnectivityManager mag = (ConnectivityManager) cm
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		// 此处输出当前可用网络
		// LH.LogPrint(tag,"\nActive:\n");
		if (mag != null) {
			NetworkInfo info = mag.getActiveNetworkInfo();
			if (info != null) {
				// LH.LogPrint(tag,"ExtraInfo=" + info.getExtraInfo() + "\n");
				// LH.LogPrint(tag,"SubtypeName=" + info.getSubtypeName() +
				// "\n");
				// LH.LogPrint(tag,"TypeName=" + info.getTypeName() + "\n");
				if (info.getTypeName().compareToIgnoreCase("mobile") == 0) {
					if (info.getExtraInfo().compareToIgnoreCase("cmnet") == 0) {
						ret = true;
					}
				}
			}
		}
		return ret;
	}

	public void initNetworkInfo() {
		ConnectivityManager mag = (ConnectivityManager) cm
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		// 此处输出当前可用网络
		// LH.LogPrint(tag,"\nActive:\n");
		NetworkInfo info = mag.getActiveNetworkInfo();
		// LH.LogPrint(tag,"ExtraInfo=" + info.getExtraInfo() + "\n");
		// LH.LogPrint(tag,"SubtypeName=" + info.getSubtypeName() + "\n");
		// LH.LogPrint(tag,"TypeName=" + info.getTypeName() + "\n");

		// LH.LogPrint(tag,"\nWifi:\n");
		NetworkInfo wifiInfo = mag
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

		// LH.LogPrint(tag,"ExtraInfo=" + wifiInfo.getExtraInfo() + "\n");
		// LH.LogPrint(tag,"SubtypeName=" + wifiInfo.getSubtypeName() + "\n");
		// LH.LogPrint(tag,"TypeName=" + wifiInfo.getTypeName() + "\n");
		NetworkInfo mobInfo = mag
				.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		// LH.LogPrint(tag,"\nMobile:\n");
		// LH.LogPrint(tag,"ExtraInfo=" + mobInfo.getExtraInfo() + "\n");
		// LH.LogPrint(tag,"SubtypeName=" + mobInfo.getSubtypeName() + "\n");
		// LH.LogPrint(tag,"TypeName=" + mobInfo.getTypeName() + "\n");
	}

}
