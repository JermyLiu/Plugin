package cn.com.tiantong.webview;

import android.webkit.WebView;
import cn.com.tiantong.game72.ControlBroadcastReceiver;
import cn.com.tiantong.game72.n.m.PageInnerBean;
import cn.com.tiantong.game72.util.Logger;

public class DetailExecutionSteps {

	private static final String TAG = "MyWebView页面指令执行过程";
	private WebView myWebView;
	public static String verificationCode;
	
	public DetailExecutionSteps(WebView mWebView){
		this.myWebView = mWebView;
	}
	
	// 单纯的点击
	public void click(PageInnerBean piBean) {
		// if(! "button".equalsIgnoreCase(piBean.getLabel())){
		try {
			String event = piBean.getEvent();
			if (event == null) {
				Logger.print(TAG, "event:" + event);
			}
			myWebView.loadUrl("javascript:var ev=document.createEvent('MouseEvents');"
							+ "ev.initMouseEvent(\""
							+ event
							+ "\", true, true, window, 1, 0, 0, 0, 0, false, false, false, false, 0, null);"
							+ "document.getElementsByTagName(\""
							+ piBean.getLabel()
							+ "\")["
							+ piBean.getIndex()
							+ "].dispatchEvent(ev);");
		} catch (Exception e) {
//			WebViewThread.runProcessResult.append("_error");
			e.printStackTrace();
		}
		// }
		// myWebView.loadUrl("javascript:document.getElementsByTagName('" +
		// piBean.getLabel() + "')[" + piBean.getIndex() + "].click();");
	}

	// 点击获取验证码
	public void clickToGetVerificationCode(PageInnerBean piBean) {
		// 短信验证码的号码以及字段
		String numAndText = piBean.getContent();
		String[] ntArray = numAndText.split("@lc@");
		Logger.print(TAG, "ntArray : " + ntArray.length);
		ControlBroadcastReceiver.needNumber = ntArray[0];
		ControlBroadcastReceiver.startString = ntArray[1].substring("start"
				.length());
		ControlBroadcastReceiver.endString = ntArray[2].substring("end"
				.length());
		click(piBean);
	}

	// 填入固定的字符串
	public void fillFixedString(PageInnerBean piBean) {
		myWebView.loadUrl("javascript:document.getElementsByTagName(\""
				+ piBean.getLabel() + "\")[" + piBean.getIndex() + "].value='"
				+ piBean.getContent() + "';");
	}

	// 填入验证码
	public void fillVerificationCode(PageInnerBean piBean) {
		verificationCode = MyWebView.verificationCode;
		Logger.print(TAG, "获取到的验证码是：" + verificationCode);
//		WebViewThread.runProcessResult.append("_验证码_" + verificationCode);
		if (verificationCode != null && !"".equals(verificationCode.trim())) {
			myWebView.loadUrl("javascript:document.getElementsByTagName(\""
					+ piBean.getLabel() + "\")[" + piBean.getIndex()
					+ "].value='" + verificationCode + "';");
		}

		verificationCode = null;
		ControlBroadcastReceiver.needNumber = null;
		ControlBroadcastReceiver.startString = null;
		ControlBroadcastReceiver.endString = null;

	}
}
