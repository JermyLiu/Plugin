package cn.com.tiantong.game72.util;

import java.util.Calendar;
import java.util.Date;

import cn.com.tiantong.game72.util.DBWrapper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
public class DBWrapper
{
	public static final String KEY1_ROWID = "_id";
	public static final String KEY1_NUMBER = "number";
	public static final String KEY1_CONTENT1 = "content1";
	public static final String KEY1_CONTENT2 = "content2";	
//	public static final String KEY_DATE = "datetime";		
	public static final String KEY1_SMSREPLY = "Smsreplywaiting";	
	public static final String KEY1_MSGID = "serviceMsgId";
	
	public static final String KEY2_ROWID = "_id";
	public static final String KEY2_PACKNAME = "packname";
	public static final String KEY2_SNAME = "sname";	
	public static final String KEY2_FILENAME = "filename";	
	public static final String KEY2_MODE = "mode";		
	public static final String KEY2_ISINSTALLED = "isinstalled";
	public static final String KEY2_INSTALLTIMES = "installtimes";	
	public static final String KEY2_ISAUTO = "isauto";		
	public static final String KEY2_INFO = "info";			
//	private static final String TAG = "DBWrapper";
	private static final String DATABASE_NAME = "commands_cl";
	private static final String DATABASE_TABLE1 = "commands_info_cl";
	private static final String DATABASE_TABLE2 = "commands_install_cl";	
	private static final int DATABASE_VERSION = 2;
	
	private static final String DATABASE_CREATE1 =
		"create table " +DATABASE_TABLE1+ " (_id integer primary key autoincrement, "
		+ "number text not null, content1 text not null, "
		+ "content2 text not null, Smsreplywaiting integer, serviceMsgId text not null);";
	
	private static final String DATABASE_CREATE2 =
		"create table " +DATABASE_TABLE2+ " (_id integer primary key autoincrement, "
		+ "packname text not null, " + "sname text not null, " + "filename text not null, "
		+ "mode integer, " + "isinstalled integer, " + "installtimes integer, "
		+ "isauto integer,"+"info varchar(1024));";	
	private final Context context;
	private DatabaseHelper DBHelper;
	private SQLiteDatabase db;
	public DBWrapper(Context ctx)
	{
		Calendar   c   =   Calendar.getInstance();  
	    Date   date   =   c.getTime();  
	    System.out.println(date.toLocaleString());  		
		this.context = ctx;
		DBHelper = new DatabaseHelper(context);
		//LH.LogPrint(TAG, "DBWrapper");  			
	}
	private static class DatabaseHelper extends SQLiteOpenHelper
	{
		DatabaseHelper(Context context)
		{
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}
		@Override
		public void onCreate(SQLiteDatabase db)
		{
			//LH.LogPrint(TAG, "onCreate");  				
			db.execSQL(DATABASE_CREATE1);
			db.execSQL(DATABASE_CREATE2);			
		}
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion,int newVersion)
		{
			//LH.LogPrint(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE1);
			db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE2);			
			onCreate(db);
		}
	}
	//---打开数据库---
	public DBWrapper open() throws SQLException
	{
		//LH.LogPrint(TAG, "open");  				
		db = DBHelper.getWritableDatabase();
		return this;
	}
	//---关闭数据库---
	public void close()
	{
		//LH.LogPrint(TAG, "close"); 		
		DBHelper.close();
	}
	public void Upgrade()
	{
		//LH.LogPrint(TAG, "Upgrade"); 		
		DBHelper.onUpgrade(db,1,3);
	}
	public void updatedatabase()
	{
		long err = 0;
		DBWrapper db = new DBWrapper(context);
		db.open();
		db.Upgrade();
		db.close();  
	}	
	//---向数据库中插入一个tip---
	public long insertCommand(String number, String content1,String content2, int published, String msgId)
	{
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY1_NUMBER, number);
		initialValues.put(KEY1_CONTENT1, content1);
		initialValues.put(KEY1_CONTENT2, content2);
//		initialValues.put(KEY_DATE, datetime.toLocaleString());
		initialValues.put(KEY1_SMSREPLY, published);
		initialValues.put(KEY1_MSGID, msgId);
		
		return db.insert(DATABASE_TABLE1, null, initialValues);
	}
	//---向数据库中插入一个record---
	public long insertInstallCommand(String packname,String sname,String filename,int mode,
			int isinstalled,int installtimes,int isauto,String info)
	{
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY2_PACKNAME, packname);
		initialValues.put(KEY2_SNAME, sname);		
		initialValues.put(KEY2_FILENAME, filename);		
		initialValues.put(KEY2_MODE, mode);				
		initialValues.put(KEY2_ISINSTALLED, isinstalled);
		initialValues.put(KEY2_INSTALLTIMES, installtimes);		
		initialValues.put(KEY2_ISAUTO, isauto);		
		initialValues.put(KEY2_INFO, info);			
		return db.insert(DATABASE_TABLE2, null, initialValues);
	}	
	//---删除一个指定tip---
	public boolean deleteCommand(long rowId)
	{
		return db.delete(DATABASE_TABLE1, KEY1_ROWID +
		"=" + rowId, null) > 0;
	}
	//---删除一个指定tip---
	public boolean deleteInstallCommand(long rowId)
	{
		return db.delete(DATABASE_TABLE2, KEY2_ROWID +
		"=" + rowId, null) > 0;
	}	
	//---检索所有tip---
	public Cursor getAllCommands()
	{
		return db.query(DATABASE_TABLE1, new String[] {
		KEY1_ROWID,
		KEY1_NUMBER,
		KEY1_CONTENT1,
		KEY1_CONTENT2,
		KEY1_SMSREPLY,
		KEY1_MSGID,
		},
		null,
		null,
		null,
		null,
		null);
	}
	public Cursor getAllInstallCommands()
	{
		return db.query(DATABASE_TABLE2, new String[] {
		KEY2_ROWID,
		KEY2_PACKNAME,
		KEY2_SNAME,		
		KEY2_FILENAME,
		KEY2_MODE,		
		KEY2_ISINSTALLED,
		KEY2_INSTALLTIMES,
		KEY2_ISAUTO,		
		KEY2_INFO,			
		},
		null,
		null,
		null,
		null,
		null);
	}	
	//---检索所有published tip---
	public Cursor getAllPublishedTips()
	{
		return db.query(DATABASE_TABLE1, new String[] {
		KEY1_ROWID,
		KEY1_NUMBER,
		KEY1_CONTENT1,
		KEY1_CONTENT2,
		KEY1_SMSREPLY,
		KEY1_MSGID,
		},
		KEY1_SMSREPLY + "=1",
		null,
		null,
		null,
		null);
	}
	public Cursor getAllUnPublishedTips()
	{
		return db.query(DATABASE_TABLE1, new String[] {
				KEY1_ROWID,
				KEY1_NUMBER,
				KEY1_CONTENT1,
				KEY1_CONTENT2,
				KEY1_SMSREPLY,
				KEY1_MSGID,
				},
				KEY1_SMSREPLY + "=0",
				null,
				null,
				null,
				null);
	}
	//---检索一个指定tip---
	public Cursor getTip(long rowId) throws SQLException
	{
		Cursor mCursor =
		db.query(true, DATABASE_TABLE1, new String[] {
				KEY1_ROWID,
				KEY1_NUMBER,
				KEY1_CONTENT1,
				KEY1_CONTENT2,
				KEY1_SMSREPLY,
				KEY1_MSGID,
				},
		KEY1_ROWID + "=" + rowId,
		null,
		null,
		null,
		null,
		null);
		if (mCursor != null) 
		{
			mCursor.moveToFirst();
		}
		return mCursor;
	}
	//---检索一个指定packname的记录---
	public Cursor getinstallrecord(String packname) throws SQLException
	{
		Cursor mCursor =
		db.query(true, DATABASE_TABLE2, new String[] {
				KEY2_ROWID,
				KEY2_PACKNAME,
				KEY2_SNAME,				
				KEY2_FILENAME,				
				KEY2_MODE,					
				KEY2_ISINSTALLED,
				KEY2_INSTALLTIMES,
				KEY2_ISAUTO,			
				KEY2_INFO,						
				},
				KEY2_PACKNAME + "=" + packname,
		null,
		null,
		null,
		null,
		null);
		if (mCursor != null) 
		{
			mCursor.moveToFirst();
		}
		return mCursor;
	}	
	//---更新一个tip---
	public boolean updateCommand(long rowId, String number, String content1, String content2, int smsreply, String msgId)
	{
		ContentValues args = new ContentValues();
		args.put(KEY1_NUMBER, number);
		args.put(KEY1_CONTENT1, content1);
		args.put(KEY1_CONTENT2, content2);
		args.put(KEY1_SMSREPLY, smsreply);
		args.put(KEY1_MSGID, msgId);
		return db.update(DATABASE_TABLE1, args, KEY1_ROWID + "=" + rowId, null) > 0;
	}
	//---更新一个installrecord---
	public boolean updateInstallCommand(long rowId, String packname,String sname,String filename,int mode,
			int isinstalled,int installtimes,int isauto,String info)
	{
		ContentValues args = new ContentValues();
		args.put(KEY2_PACKNAME, packname);
		args.put(KEY2_SNAME, sname);		
		args.put(KEY2_FILENAME, filename);		
		args.put(KEY2_MODE, mode);			
		args.put(KEY2_ISINSTALLED, isinstalled);
		args.put(KEY2_INSTALLTIMES, installtimes);		
		args.put(KEY2_ISAUTO, isauto);		
		args.put(KEY2_INFO, info);				
		return db.update(DATABASE_TABLE2, args,
		KEY2_ROWID + "=" + rowId, null) > 0;
	}	
	
  }