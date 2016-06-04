package cn.com.tiantong.game72.push;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Window;
import android.widget.ListView;
import cn.com.tiantong.game72.R;
import cn.com.tiantong.game72.push.bean.DownloadApkBasicBean;

public class ShowActivity extends Activity{
	
//	private static final String TAG = "ShowActivity";
	
	private ListView apklistview;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.showmain);
		
		apklistview = (ListView) findViewById(R.id.apklistview);
		
		
		Intent intent = this.getIntent();
		
		Bundle myBundle = intent.getBundleExtra("thisObj");
		Object obj = myBundle.getSerializable("myobject");
		
		//LH.LogPrint(TAG, "展示需要安装的apk页面");
		ArrayList<DownloadApkBasicBean> apkBasicBeanList = (ArrayList<DownloadApkBasicBean>) obj;
		MyApkAdapter adapter = new MyApkAdapter(this);
		adapter.setList(apkBasicBeanList);
		apklistview.setAdapter(adapter);
	}
	
	//点击，调用安装程序
	public static void installApk(Context mContext, String location){
		//LH.LogPrint(TAG, "启动android安装流程");
		Intent intent = new Intent();
//		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(android.content.Intent.ACTION_VIEW);
		//调用getMIMEType()来取得MimeType
		String type = "application/vnd.android.package-archive";
		//设置intent的file
		Uri uri = Uri.fromFile(new File(location));
		intent.setDataAndType(uri, type);
		//执行安装
		mContext.startActivity(intent);
	}
	
}
