package cn.com.tiantong.game72.n.m;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import cn.com.tiantong.game72.n.m.NI;

import android.content.Context;
import android.net.ConnectivityManager;
import android.text.format.Time;
import android.util.Log;


public class U {

	public  static String toMd5(byte[] bytes) {
	try {
		MessageDigest algorithm = MessageDigest.getInstance("MD5");
		algorithm.reset();
		algorithm.update(bytes);
		return toHexString(algorithm.digest(), "");
	} catch (NoSuchAlgorithmException e) {
		Log.v("util", "toMd5():" + e);
		throw new RuntimeException(e);
		}
	}
	
	private static String toHexString(byte[] bytes, String separator) {
		StringBuilder hexString = new StringBuilder();
		for (byte b : bytes) {
			String hex = Integer.toHexString(0xFF & b);
			if(hex.length()==1){
				hexString.append('0');
			}
			hexString.append(hex).append(separator);
		}
		return hexString.toString();
	}
	
	public static String getDateOfWeek(int nDay)
	{
		String datestr;
		
		Calendar c = Calendar.getInstance(); 
		Date now = c.getTime(); 
		int weekday = now.getDay();
		c.add(Calendar.DATE, nDay-weekday);
		Date date = c.getTime(); 
		datestr = String.format("%d-%02d-%02d", date.getYear()+1900, date.getMonth()+1, date.getDate());
		
		/*
	    SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd"); 
	    c.setTime(c.getTime()); 
	    long l = c.getTimeInMillis(); 
	    l += (nDay-weekday)*24*60*60*1000; 
	    c.setTimeInMillis(l); 
	    datestr = sdf.format(c.getTime());*/

		return datestr;
	}
	
	public static String getDateOfNow(int nDay)
	{
		String datestr;

		Calendar c = Calendar.getInstance(); 
	    SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd"); 
	    c.setTime(c.getTime()); 
	    long l = c.getTimeInMillis(); 
	    l += (nDay)*24*60*60*1000; 
	    c.setTimeInMillis(l); 
	    datestr = sdf.format(c.getTime());

		return datestr;
	}
	
	public static String DATE_FORMAT = "yyyy-MM-dd";
	public static String TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

	public static long TimeStrToLong(String time, String format) 
	{
		try{
			SimpleDateFormat sdf = new SimpleDateFormat(format);
			return sdf.parse(time).getTime();
		}catch (ParseException e) {
			e.printStackTrace();
			Log.v("util", "TimeStrToLong():" + e);
		}
		return 0l;
	}
	
	public static String TimeLongToStr(long time, String format) 
	{
		if (time > 0l) {
			SimpleDateFormat sdf = new SimpleDateFormat(format);
			Date date = new Date(time);
			return sdf.format(date);
		}
		return "";
	}
	
	public static String TrimTime(String datetime)
	{
		/*
		String str[] = datetime.split(" |\\.");
		if(str.length > 1)
		{
			int end = str[1].lastIndexOf(":");
			if(end > 0)
			{
				String time = str[1].substring(0, end);
				return time;
			}
		}*/
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			SimpleDateFormat dsdf = new SimpleDateFormat("HH:mm");
			Date date = new Date(sdf.parse(datetime).getTime());
			return dsdf.format(date);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return datetime;
	}

	public static String GetNowDate()
	{
		Time time = new Time();
		time.setToNow();
		int year = time.year;
		int month = time.month+1;
		int day = time.monthDay;

		String date = String.format("%d-%02d-%02d", year, month, day);
		return date;
	}
	
	public static String GetNowTime()
	{
		Time time = new Time();
		time.setToNow();
		int hour = time.hour;
		int min = time.minute;
		int sec = time.second;

		String st = String.format("%d:%02d:%02d", hour, min, sec);
		return st;
	}
	
	public static String GetNowDateTime()
	{
		Calendar c = Calendar.getInstance(); 
		SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss"); 
		return sdf.format(c.getTime());
	}
	
	public static Date GetNow()
	{
		Time time = new Time();
		time.setToNow();
		int year = time.year;
		int month = time.month;
		int day = time.monthDay;
		int hour = time.hour;
		int min = time.minute;
		int sec = time.second;
		
		Date date = new Date();
		date.setYear(year-1900);
		date.setMonth(month);
		date.setDate(day);
		date.setHours(hour);
		date.setMinutes(min);
		date.setSeconds(sec);
		
		return date;
	}
	
	public static String DateToStr(Date date)
	{
		if(date == null) return "";
		
		String str = String.format("%d-%02d-%02d", date.getYear()+1900, date.getMonth()+1, date.getDate());
		return str;
	}
	
	public static String TimeToStr(Date date)
	{
		if(date == null) return "";
		
		String str = String.format("%d:%02d:%02d", date.getHours(), date.getMinutes(), date.getSeconds());
		return str;
	}
	
	public static String DateTimeToStr(Date date)
	{
		if(date == null) return "";
		
		String str = String.format("%d-%02d-%02d %d:%02d:%02d", date.getYear()+1900, date.getMonth()+1, date.getDate(), date.getHours(), date.getMinutes(), date.getSeconds());
		return str;
	}
	
	/**
	 * 获取网络信息
	 * @param activity
	 * @return
	 */
	public static NI getNetworkInfo(Context context){
		NI myNetworkInfo = new NI();
		try{
			ConnectivityManager manager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
			android.net.NetworkInfo networkInfo = manager.getActiveNetworkInfo();

			if(networkInfo!=null && networkInfo.isAvailable() && networkInfo.isConnected()){
				myNetworkInfo.setConnectToNetwork(true);
				if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
					myNetworkInfo.setProxy(false);
					myNetworkInfo.setProxyName(networkInfo.getTypeName());
				}else{
				    //取得代理信息
					String proxyHost=android.net.Proxy.getDefaultHost();
					if(proxyHost!=null){
						myNetworkInfo.setProxy(true);
						myNetworkInfo.setProxyHost(proxyHost);
						myNetworkInfo.setProxyPort(android.net.Proxy.getDefaultPort());
					}else{
						myNetworkInfo.setProxy(false);
					}
					myNetworkInfo.setProxyName(networkInfo.getExtraInfo());
				}
			}else{
				myNetworkInfo.setConnectToNetwork(false);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return myNetworkInfo;
	}
}
