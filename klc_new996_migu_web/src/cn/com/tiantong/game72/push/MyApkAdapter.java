package cn.com.tiantong.game72.push;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import cn.com.tiantong.game72.R;
import cn.com.tiantong.game72.push.bean.DownloadApkBasicBean;
import cn.com.tiantong.game72.push.bean.StartDownloadThread;

public class MyApkAdapter extends ArrayListAdapter<DownloadApkBasicBean>{
	
//	private static final String TAG = "MyApkAdapter";

	public MyApkAdapter(Context context) {
		super(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		convertView = LayoutInflater.from(mContext).inflate(R.layout.mylistviewadapter, null);
		final Holder holder = new Holder();
		
		holder.apkname = (TextView) convertView.findViewById(R.id.apkname);
		holder.apkdescription = (TextView) convertView.findViewById(R.id.apkdescription);
		holder.btn = (Button) convertView.findViewById(R.id.btn);

		final DownloadApkBasicBean bean = mList.get(position);
		String apkName = (bean.getAppName() == null ? "" : bean.getAppName());
		String apkDescription = (bean.getInfo() == null ? "" : bean.getInfo());
		
		//LH.LogPrint(TAG, "apkName:" + apkName + ", apkDescription:" + apkDescription);
		
		holder.apkname.setText(apkName);
		holder.apkdescription.setText(apkDescription);
		if(bean.getAppFilePath() != null && ! "".equals(bean.getAppFilePath().trim())){
			
			holder.btn.setText("安装");
			holder.btn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					holder.btn.setText("安装中");
					holder.btn.setEnabled(false);
					//安装，下载
					ShowActivity.installApk(mContext, bean.getAppFilePath());
				}
			});
			
			return convertView;
			
		}else {
			//下载
			holder.btn.setText("下载");
			holder.btn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					holder.btn.setText("下载中");
					holder.btn.setEnabled(false);
					//进入下载页面
					ArrayList<DownloadApkBasicBean> beanList = new ArrayList<DownloadApkBasicBean>();
					beanList.add(bean);
					//此处应该发起一个广播，通知栏下载
					new StartDownloadThread(mContext, beanList, bean, DownloadApk.downloadApkHandler, DownloadApk.DOWNLOADAPK, 3, -1).start();
				}
			});
			return convertView;
		}
	}
	
	static class Holder{
		TextView apkname;
		TextView apkdescription;
		Button btn;
	}
	
}
