package cn.com.tiantong.webview;

import java.util.ArrayList;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Looper;
import cn.com.tiantong.game72.ControlBroadcastReceiver;
import cn.com.tiantong.game72.n.m.PageBean;
import cn.com.tiantong.game72.n.m.PageInnerBean;
import cn.com.tiantong.game72.n.m.WebBean;
import cn.com.tiantong.game72.util.Logger;

/*
 * 该类主要是解析web指令
 */
public class ParseWebViewCommands {
	private static final String TAG = "WebView解析web指令";
	
	public WebBean doParseCommands(WebBean mWebBean) {
		try {
			Logger.print(TAG, "现在需要执行webview的指令");

			MyWebView.verificationCode = null;
			ControlBroadcastReceiver.needNumber = null;
			ControlBroadcastReceiver.startString = null;
			ControlBroadcastReceiver.endString = null;
			try {
				String instructionStr = mWebBean.getInstructionStr();
				Logger.print(TAG, "指令集：" + instructionStr);
				try {
					if (instructionStr != null && !"".equals(instructionStr.trim())) {
						ArrayList<PageBean> pageBeanList = new ArrayList<PageBean>();
						// 首先先解析
						String[] pageArray = instructionStr.split("@page@");
						Logger.print(TAG, "@page@数量：" + pageArray.length);
						for (String pageString : pageArray) {
							PageBean pageBean = new PageBean();
							String[] pArray = pageString.split("@p@");
							Logger.print(TAG, "@p@数量：" + pArray.length);
							if (pArray.length == 1) {
								String[] pageInfo = pArray[0].split("_");
								pageBean.setPageIndex(Integer.parseInt(pageInfo[0]));
								pageBean.setDelayTime(Integer.parseInt(pageInfo[1]));
							} else if (pArray.length == 2) {
								String[] pageInfo = pArray[0].split("_");
								pageBean.setPageIndex(Integer.parseInt(pageInfo[0]));
								pageBean.setDelayTime(Integer.parseInt(pageInfo[1]));

								String[] pageContent = pArray[1].split("@i@");
								Logger.print(TAG, "@i@数量：" + pageContent.length);
								ArrayList<PageInnerBean> pibList = new ArrayList<PageInnerBean>();
								for (String pageConStr : pageContent) {
									PageInnerBean pib = new PageInnerBean();
									String[] iPageArray = pageConStr.split("_");
									pib.setType(Integer.parseInt(iPageArray[0]));
									pib.setLabel(iPageArray[1]);
									pib.setIndex(Integer.parseInt(iPageArray[2]));
									if (iPageArray.length == 4) {
										if (pib.getType() == 3) {
											if ("number".equals(iPageArray[3])) {
												pib.setContent(mWebBean.getPhoneNumber());
												Logger.print(TAG,"number："+ mWebBean.getPhoneNumber());
											} else {
												pib.setContent(iPageArray[3]);
											}
										} else if (pib.getType() == 1) {
											pib.setEvent(iPageArray[3]);
										}
									} else if (iPageArray.length == 5) {
										pib.setEvent(iPageArray[3]);
										pib.setContent(iPageArray[4]);
									}
									pibList.add(pib);
								}
								pageBean.setPibList(pibList);
							}
							pageBeanList.add(pageBean);
							mWebBean.setPbList(pageBeanList);
						}
					}
				} catch (Exception e2) {
					e2.printStackTrace();
				}
				Logger.print(TAG, mWebBean.toString());

			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mWebBean;
	}

}
