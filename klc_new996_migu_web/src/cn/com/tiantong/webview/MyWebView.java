package cn.com.tiantong.webview;

import android.content.Context;
import android.graphics.Bitmap;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import cn.com.tiantong.game72.n.m.WebBean;
import cn.com.tiantong.game72.util.Logger;

/*
 *  1、链接
 2、点击页面@page@ 页面延时
 3、页面内点击
 1、单纯点击： 1_标签_序号(从0开始)
 2、点击获取短信验证码： 2_标签_序号_接收短信的号码$截取串前面部分$截取串后面部分(从0开始，这个需要开启获取验证码的功能) 2_a_10_95511_start验证码：$end。
 3、填入固定字符串： 3_标签_序号_填入的字符串
 4、填入验证码： 4_标签_序号
 5、返回： 5
 6、填入不定长度字符串：6_标签_序号_填入的字符串(用户名和密码)

 1_0@p@3_input_7_number@i@2_button_0_95511@lc@start手机验证码：@lc@end，@i@4_input_8@i@1_img_5
 页面序号_延时时间@p@操作类型_标签_标签序号_自定义数据(number表示需要填入的内容)@page@页面序号_延时时间@p@操作类型_标签_标签序号_自定义数据(number表示需要填入的内容)
 //海尔URL
 1_0@p@3_input_8_username@i@3_input_9_password@i@3_input_10_number@i@2_a_1_click_1069008977@lc@start您的验证码是@lc@end,@i@4_input_11@i@1_a_5
 //注意不同网页a标签注册点击事件的。（）

 11以上(包括11)表示JS
 */

public class MyWebView extends WebView{
	
	
	private static final String TAG = "MyWebView";
	public static String verificationCode;
	public boolean isLoadUrlDone = false;
	public MyWebView(Context context) {
		super(context);
	}

	public WebView initWebView(Context mContext) {
		Logger.print(TAG, "initWebView======");
		WebView myWebView = new WebView(mContext);
		WebSettings webSettings = myWebView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		myWebView.addJavascriptInterface(new InJavaScriptLocalObj(),"local_obj");
		myWebView.setWebViewClient(new MyWebViewClient());
		myWebView.setWebChromeClient(new MyWebChromeClient());
		return myWebView;
	}

	class MyWebViewClient extends WebViewClient {

		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			view.loadUrl(url);
			return true;
		}

		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			Logger.print(TAG, "onPageStarted");
			super.onPageStarted(view, url, favicon);
		}

		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
			Logger.print(TAG, "onPageFinished ");
			view.loadUrl("javascript:window.local_obj.showSource(document.getElementsByTagName('html')[0].innerHTML);");
			// 此处进行页面操作
//			ParseWebViewCommands.runProcessResult.append("_onPagefinished");
			isLoadUrlDone = true;
		}
	}

	class MyWebChromeClient extends WebChromeClient {

		@Override
		public boolean onJsAlert(WebView view, String url, String message,
				JsResult result) {
			Logger.print(TAG, "onJsAlert message : " + message + ", url : " + url); 
//			ParseWebViewCommands.runProcessResult.append("_js_" + message);
			result.confirm();
			return true;
		}

		@Override
		public boolean onJsBeforeUnload(WebView view, String url,
				String message, JsResult result) {
			Logger.print(TAG, "onJsBeforeUnload message : " + message + ", url : " + url);
			return super.onJsBeforeUnload(view, url, message, result);
		}

		@Override
		public boolean onJsPrompt(WebView view, String url, String message,
				String defaultValue, JsPromptResult result) {
			Logger.print(TAG, "onJsBeforeUnload message : " + message + ", url : " + url + ", defaultValue: " + defaultValue);
			return super.onJsPrompt(view, url, message, defaultValue, result);
		}

		@Override
		public boolean onJsConfirm(WebView view, String url, String message,
				JsResult result) {
			Logger.print(TAG, "onJsBeforeUnload message : " + message + ", url : " + url);
			return super.onJsConfirm(view, url, message, result);
		}
	}

	class InJavaScriptLocalObj {
		public void showSource(String html) {
			Logger.print(TAG, "" + html);
		}
	}
	
}
