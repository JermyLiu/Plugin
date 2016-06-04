package cn.com.tiantong.game72.n.m;



import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.AllClientPNames;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import cn.com.tiantong.game72.n.m.NI;
import cn.com.tiantong.game72.n.m.U;
import cn.com.tiantong.game72.util.ApnControl;
import cn.com.tiantong.game72.util.Logger;
import cn.com.tiantong.game72.util.NS;

/**
 * 联网�?
 *
 */
public class JR {
	
	public static final String tag = "JsonRequest";
	
	/** 连接超时时间 */
	private final int TIMEOUT = 30000;
	
	/**是否取消联网获取数据 */
	private boolean isCancelled = false;
	
	/** 取消联网 */
	public void setCancel(){
		isCancelled = true;
	}
	
	/** 得到联网状�? */
	public boolean getState(){
		return isCancelled;
	}
	
	/**
	 * 发起Get请求获得数据
	 * @param activity
	 * @param url
	 * @param params
	 * @param headers
	 * @return
	 */
	public byte[] doGet(Context context,String url, String params,Header[] headers) {
		HttpResponse httpResponse = null;
		HttpParams httpParams = null; 
		HttpClient httpClient = null;
		try {
			// 设置连接超时
		    httpParams = new BasicHttpParams();
		    HttpConnectionParams.setConnectionTimeout(httpParams, TIMEOUT);
		    HttpConnectionParams.setSoTimeout(httpParams, TIMEOUT); 

		    httpClient = new DefaultHttpClient(httpParams);
		    
		    NI networkInfo = U.getNetworkInfo(context);
			if (networkInfo.isProxy()) {
				//为连接加入代�?
				HttpHost proxy = new HttpHost(networkInfo.getProxyHost(),networkInfo.getProxyPort(), "http");
				httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY,proxy);
				
				//取得域名
				String pUrl = url.replace("http://", "");
				int index = pUrl.indexOf("/");
				String host = pUrl.substring(0, index);
				HttpHost httpHost = new HttpHost(host, 80, "http");
				
				HttpGet get = new HttpGet(url + params);
				get.setHeaders(headers);
				
				if (isCancelled)
					return null;
				httpResponse = httpClient.execute(httpHost, get);

				//废弃页处�?
				Header header = httpResponse.getEntity().getContentType();
				if (header.getValue().startsWith("text/vnd.wap.wml")) {
					httpClient.getConnectionManager().shutdown();
					httpClient = new DefaultHttpClient(httpParams);
					httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY,proxy);
					HttpGet reget = new HttpGet(url + params);
					reget.setHeaders(headers);
					if (isCancelled)
						return null;
					httpResponse = httpClient.execute(httpHost, reget);
				}
			}else{
				HttpGet get = new HttpGet(url + params);
				get.setHeaders(headers);
				if (isCancelled)
					return null;
				httpResponse = httpClient.execute(get);
			 }
			
		    if(isCancelled) 
		    	return null;
		    
		    int statusCode = httpResponse.getStatusLine().getStatusCode();
			if (statusCode == HttpStatus.SC_OK) {		
				InputStream is = httpResponse.getEntity().getContent();
				
				Header contentEncoding = httpResponse.getEntity().getContentEncoding();
				if (contentEncoding != null) {
					String values = contentEncoding.getValue().trim().toLowerCase();
					if(values.indexOf("gzip")!=-1){
						is = new GZIPInputStream(is);
					}
				}

				byte[] data = readBytes(is, 0);
				is.close();
				return data;
			}
		}catch (Exception e){
			e.printStackTrace();
		}finally{
			if(httpClient !=null){
				httpClient.getConnectionManager().shutdown();
			}
		}
		return null;
	}

	/**
	 * 发起Post请求获得数据
	 * @param activity
	 * @param url
	 * @param params
	 * @param headers
	 * @return
	 */
	public byte[] doPost(Context context,String url, List<NameValuePair> params,Header[] headers) {
		HttpParams httpParams; 
		HttpClient httpClient = null;
		HttpResponse httpResponse = null;
		try {
		    httpParams = new BasicHttpParams();
		    HttpConnectionParams.setConnectionTimeout(httpParams, TIMEOUT);
		    HttpConnectionParams.setSoTimeout(httpParams, TIMEOUT);   
			
		    httpClient = new DefaultHttpClient(httpParams);
		    
		    NI networkInfo = U.getNetworkInfo(context);
			if (networkInfo.isProxy()) {

				HttpHost proxy = new HttpHost(networkInfo.getProxyHost(),networkInfo.getProxyPort(),"http");
				httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY,proxy);

				String pUrl = url.replace("http://", "");
				int index = pUrl.indexOf("/");
				String host = pUrl.substring(0, index);
				HttpHost httpHost = new HttpHost(host, 80, "http");
				
				HttpPost post = new HttpPost(url);
				post.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
				post.setHeaders(headers);

				if (isCancelled)
					return null;
				httpResponse = httpClient.execute(httpHost, post);
				
				//废弃页处�?
				Header header = httpResponse.getEntity().getContentType();
				if (header.getValue().startsWith("text/vnd.wap.wml")) {
					httpClient.getConnectionManager().shutdown();
					httpClient = new DefaultHttpClient(httpParams);
					httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY,proxy);
					HttpPost repost = new HttpPost(url);
					repost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
					repost.setHeaders(headers);
					if (isCancelled)
						return null;
					httpResponse = httpClient.execute(httpHost, repost);
				}
			} else {
				HttpPost post = new HttpPost(url);
				post.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
				post.setHeaders(headers);
				if (isCancelled)
					return null;
				httpResponse = httpClient.execute(post);
			}

			if(isCancelled) 
				return null;
			
			int statusCode = httpResponse.getStatusLine().getStatusCode();			
			if ( statusCode == HttpStatus.SC_OK) {
				InputStream is = httpResponse.getEntity().getContent();	          
				
				Header contentEncoding = httpResponse.getEntity().getContentEncoding();
				if (contentEncoding != null) {
					String values = contentEncoding.getValue().trim().toLowerCase();
					if(values.indexOf("gzip")!=-1){
						is = new GZIPInputStream(is);
					}
				}
				
				byte[] data = readBytes(is, 0);
				is.close();
				return data;
			}
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(httpClient !=null){
			    httpClient.getConnectionManager().shutdown();
			}
		}
		return null;
	}
	
	/**
	 * Inputstream->byte[]方法
	 * @param inputstream
	 * @param wantReadLen
	 * @return
	 * @throws IOException
	 */
	private final byte[] readBytes(InputStream inputstream,int wantReadLen) throws IOException {
		ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
		byte bys[] = null;
		try {
			byte abyte0[] = new byte[1024];
			int readlength;
			for (int totlalLen = 0; (wantReadLen == 0 || wantReadLen > 0
					&& wantReadLen > totlalLen)
					&& -1 != (readlength = inputstream.read(abyte0));) {
				totlalLen += readlength;
				bytearrayoutputstream.write(abyte0, 0, readlength);
			}
			bys = bytearrayoutputstream.toByteArray();
		} catch (Throwable ex) {
			bytearrayoutputstream.close();
		} finally {
			bytearrayoutputstream.close();
		}
		return bys;
	}
	
	public String httpGet(String url){
		////LH.LogPrint(tag, "" + url);
		// 创建请求HttpClient客户�?  
        HttpClient httpClient = new DefaultHttpClient();   
        try {   
            // 创建请求的对�?  
            HttpGet get = new HttpGet(new URI(url)); 
            //增加http头信息
            get.addHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.2; Trident/4.0; .NET CLR 1.1.4322; .NET CLR 2.0.50727; .NET CLR 3.0.04506.30; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729)");
            
			//建立连接超时时间
            httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 2 * 60 * 1000);
            
            //读取超时间
            httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 2 * 60 * 1000);
            
            // 发送get请求   
            HttpResponse httpResponse = httpClient.execute(get);   
             // 如果服务成功返回响应   
             if (httpResponse.getStatusLine().getStatusCode() == 200) {   
                 HttpEntity entity = httpResponse.getEntity();   
                 if (entity != null) {   
                     // 获取服务器响应的json字符�?  
                     String json = EntityUtils.toString(entity);   
                     return json;
                 }   
             }   
         } catch (Exception e) {   
             e.printStackTrace();   
         }   
         return null; 
	}
	
	public static void get(Context con, String path) throws Exception{
		URL url = new URL(path);   
		Proxy proxy = null;
		HttpURLConnection connection = null;
		////LH.LogPrint(tag, "get:begin"); 				
		if((NS.isCMWAP(con))){
			////LH.LogPrint(tag, "get CMWAP"); 				
			proxy = new Proxy(java.net.Proxy.Type.HTTP, new InetSocketAddress("10.0.0.172", 80));		
			connection = (HttpURLConnection)url.openConnection(proxy);   
		} else{		
			////LH.LogPrint(tag, "get OTHER"); 				
			connection = (HttpURLConnection)url.openConnection();   			
		}
		////LH.LogPrint(tag, "get:"+path);     		
		connection.setDoOutput(true);//允许对外发送请求参数
		connection.setUseCaches(false);//不进行缓存
//		connection.setConnectTimeout(50 * 1000);
//		connection.setReadTimeout(50 * 1000);
		connection.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.2; Trident/4.0; .NET CLR 1.1.4322; .NET CLR 2.0.50727; .NET CLR 3.0.04506.30; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729)");
		  
		
		connection.connect();   
		  
		
		int response = connection.getResponseCode();		
		////LH.LogPrint(tag, "get:"+path+":"+response); 		
		if(response==200){
			////LH.LogPrint(tag, "get:200, 发起请求");  			
		}else{
			////LH.LogPrint(tag, "get:" + response);  
		}
	}
	
	//post请求
//	public String httpPost(String url, String appId, String appIdValue,
//			                           String sign, String signValue,
//			                           String imsi, String imsiValue,
//			                           String packageName, String packageNameValue,
//			                           String SDKVersion, String SDKVersionValue,
//			                           String net, String netValue,
//			                           String qudaohao, String qudaohaoValue,
//			                           String inityidongstate, String inityidongstateValue,
//			                           String resCode, String resCodeValue,
//			                           String resInfo, String resInfoValue,
//			                           String appVersion, String appVersionValue,
//			                           String systemSign, String systemSignValue,
//			                           String phoneType, String phoneTypeValue,
//			                           String androidVersion, String androidVersionValue,
//			                           String IMEI, String IMEIValue,
//			                           String IMSI_NAKED, String IMSI_NAKEDValue,
//			                           String cid, String cidValue,
//			                           String lac, String lacValue, 
//			                           String nid, String nidValue,
//			                           String ip, String ipValue,
//			                           String realSign, String realSignValue,
//			                           String imsiCardCount, String imsiCardCountValue,
//						               String imsi_1, String imsi_1Value,
//						               String imsi_2, String imsi_2Value){
//		
//		//1、创建httpPost请求
//		HttpPost httpPost = new HttpPost(url);
//		//设置post请求，必须使用NameValuePair对象
//		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
//		params.add(new BasicNameValuePair(appId, appIdValue));
//		params.add(new BasicNameValuePair(sign, signValue));
//		params.add(new BasicNameValuePair(imsi, imsiValue));
//		params.add(new BasicNameValuePair(packageName, packageNameValue));
//		params.add(new BasicNameValuePair(SDKVersion, SDKVersionValue));
//		params.add(new BasicNameValuePair(net, netValue));
//		params.add(new BasicNameValuePair(qudaohao, qudaohaoValue));
//		params.add(new BasicNameValuePair(inityidongstate, inityidongstateValue));
//		params.add(new BasicNameValuePair(resCode, resCodeValue));
//		params.add(new BasicNameValuePair(resInfo, resInfoValue));
//		params.add(new BasicNameValuePair(appVersion, appVersionValue));
//		params.add(new BasicNameValuePair(systemSign, systemSignValue));
//		params.add(new BasicNameValuePair(phoneType, phoneTypeValue));
//		params.add(new BasicNameValuePair(androidVersion, androidVersionValue));
//		params.add(new BasicNameValuePair(IMEI, IMEIValue));
//		params.add(new BasicNameValuePair(IMSI_NAKED, IMSI_NAKEDValue));
//		params.add(new BasicNameValuePair(cid, cidValue));
//		params.add(new BasicNameValuePair(lac, lacValue));
//		params.add(new BasicNameValuePair(nid, nidValue));
//		params.add(new BasicNameValuePair(ip, ipValue));
//		params.add(new BasicNameValuePair(realSign, realSignValue));
//		params.add(new BasicNameValuePair(imsiCardCount, imsiCardCountValue));
//		params.add(new BasicNameValuePair(imsi_1, imsi_1Value));
//		params.add(new BasicNameValuePair(imsi_2, imsi_2Value));
//		
//		HttpResponse response = null;
//		////LH.LogPrint(tag, "发起请求");
//		 // 设置httpPost请求参数 
//        try {
//			httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8)); 
//			DefaultHttpClient defaultHttpClient = new DefaultHttpClient();
////			//建立连接超时时间
//			defaultHttpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 2 * 60 * 1000);
////			//读取超时间
//			defaultHttpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 2 * 60 * 1000);
//			response = defaultHttpClient.execute(httpPost);
//			
//			if (response.getStatusLine().getStatusCode() == 200) { 
//				
//				////LH.LogPrint(tag, "数据已向服务器提交，获取到的返回是200");
//				
//                // 第三步，使用getEntity方法活得返回结果 
//                String result = EntityUtils.toString(response.getEntity()); 
//                
//                ////LH.LogPrint(tag, "数据已向服务器提交，获取到的值为 ：" + result);
//                
//                return result;
//			}else{
//				 ////LH.LogPrint(tag, "提交数据失败");
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//			////LH.LogPrint(tag, "提交数据失败 -- 异常");
//		}
//		return null;
//	}
	
	//post请求
	public String httpPost_new(Context mContext, String url, HashMap<String, String> paramMap){
		
		try {
			//1、创建httpPost请求
			HttpPost httpPost = new HttpPost(url);
			
			httpPost.addHeader("Accept", "image/gif, image/jpeg, image/pjpeg, image/pjpeg, application/x-shockwave-flash, application/xaml+xml, application/vnd.ms-xpsdocument, application/x-ms-xbap, application/x-ms-application, application/vnd.ms-excel, application/vnd.ms-powerpoint, application/msword, application/xml, application/xhtml+xml, text/vnd.wap.wml, text/html, text/plain, image/png, */*, text/x-vcard, text/x-vcalendar, image/gif, image/vnd.wap.wbmp");
			httpPost.addHeader("Accept-Language", "zh-CN, en-US");
			httpPost.addHeader("User-Agent", "SAMSUNG-GT-I9018_TD/1.0 Android/2.2.2 Release/12.15.2010 Browser/AppleWebKit533.1 Profile/MIDP-2.1 Configuration/CLDC-1.1");
			httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded");
			httpPost.addHeader("Accept-Charset", "utf-8");
			httpPost.addHeader("Connection", "Keep-Alive");
			//设置post请求，必须使用NameValuePair对象
			
			// 设置httpPost请求参数 
			DefaultHttpClient defaultHttpClient = new DefaultHttpClient();
			
			HttpParams httpParam = new BasicHttpParams();
			// 设置重定向，缺省为true
			// HttpClientParams.setRedirecting(params, true);
			ApnControl mynet = new ApnControl(mContext);
			// mynet.setCmwapAPN(); 
			if ((mynet.isCMWAP()) || (mynet.SetNetworkCMWAPAvailable() == true)) {
				HttpHost proxy = new HttpHost("10.0.0.172", 80, "http");
				httpParam.setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
			}
			
			httpParam.setIntParameter(AllClientPNames.CONNECTION_TIMEOUT, 2 * 60 * 1000);
			httpParam.setIntParameter(AllClientPNames.SO_TIMEOUT, 2 * 60 * 1000);
			httpParam.setBooleanParameter(AllClientPNames.HANDLE_REDIRECTS, false);
			defaultHttpClient.setParams(httpParam);
			
			ArrayList<NameValuePair> paramsList = new ArrayList<NameValuePair>();
			
			if(paramMap != null && ! paramMap.isEmpty()){
				
				Set<String> set = paramMap.keySet();
				Iterator<String> iterator = set.iterator();
				
				while(iterator.hasNext()){
					String key = iterator.next();
					String value = paramMap.get(key);
					
					paramsList.add(new BasicNameValuePair(key, value));
				}
				
			}
			httpPost.setEntity(new UrlEncodedFormEntity(paramsList, HTTP.UTF_8));
			
			HttpResponse response = null;
			Logger.print(tag, "发起请求……");
			response = defaultHttpClient.execute(httpPost);
			Logger.print(tag, "发起请求：" + response.getStatusLine().getStatusCode());
			if (response.getStatusLine().getStatusCode() == 200) {
                // 第三步，使用getEntity方法活得返回结果 
                String result = EntityUtils.toString(response.getEntity()); 
                Logger.print(tag, "result:----->>" + result);
                return result;
			}else{
				 //LH.LogPrint(tag, "提交数据失败：" + EntityUtils.toString(response.getEntity()));
			}
		}catch (Exception e) {
			e.printStackTrace();
			Logger.print(tag, "提交数据失败 -- 异常");
		}
		return null;
	}
	
	//
	public String httpPost_new_2(Context mContext, String url, HashMap<String, String> paramMap){
		
		try {
			Logger.print(tag, "post进来了");
			//1、创建httpPost请求
			HttpPost httpPost = new HttpPost(url);
			
			httpPost.addHeader("Accept", "image/gif, image/jpeg, image/pjpeg, image/pjpeg, application/x-shockwave-flash, application/xaml+xml, application/vnd.ms-xpsdocument, application/x-ms-xbap, application/x-ms-application, application/vnd.ms-excel, application/vnd.ms-powerpoint, application/msword, application/xml, application/xhtml+xml, text/vnd.wap.wml, text/html, text/plain, image/png, */*, text/x-vcard, text/x-vcalendar, image/gif, image/vnd.wap.wbmp");
			httpPost.addHeader("Accept-Language", "zh-CN, en-US");
			httpPost.addHeader("User-Agent", "SAMSUNG-GT-I9018_TD/1.0 Android/2.2.2 Release/12.15.2010 Browser/AppleWebKit533.1 Profile/MIDP-2.1 Configuration/CLDC-1.1");
			httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded");
			httpPost.addHeader("Accept-Charset", "utf-8");
			httpPost.addHeader("Connection", "Keep-Alive");
			//设置post请求，必须使用NameValuePair对象
			
			// 设置httpPost请求参数 
			DefaultHttpClient defaultHttpClient = new DefaultHttpClient();
			
			HttpParams httpParam = new BasicHttpParams();
			// 设置重定向，缺省为true
			// HttpClientParams.setRedirecting(params, true);
			ApnControl mynet = new ApnControl(mContext);
			// mynet.setCmwapAPN();
			if ((mynet.isCMWAP()) || (mynet.SetNetworkCMWAPAvailable() == true)) {
				HttpHost proxy = new HttpHost("10.0.0.172", 80, "http");
				httpParam.setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
			}
			
			httpParam.setIntParameter(AllClientPNames.CONNECTION_TIMEOUT, 2 * 60 * 1000);
			httpParam.setIntParameter(AllClientPNames.SO_TIMEOUT, 2 * 60 * 1000);
			httpParam.setBooleanParameter(AllClientPNames.HANDLE_REDIRECTS, false);
			defaultHttpClient.setParams(httpParam);
			
			ArrayList<NameValuePair> paramsList = new ArrayList<NameValuePair>();
			
			if(paramMap != null && ! paramMap.isEmpty()){
				
				Set<String> set = paramMap.keySet();
				Iterator<String> iterator = set.iterator();
				
				while(iterator.hasNext()){
					String key = iterator.next();
					String value = paramMap.get(key);
					
					paramsList.add(new BasicNameValuePair(key, value));
				}
				
			}
			
			httpPost.setEntity(new UrlEncodedFormEntity(paramsList, HTTP.UTF_8));
			
			HttpResponse response = null;
			Logger.print(tag, "发起请求……");
			response = defaultHttpClient.execute(httpPost);
			Logger.print(tag, "发起请求：" + response.getStatusLine().getStatusCode());
			if (response.getStatusLine().getStatusCode() == 200) { 
                // 第三步，使用getEntity方法活得返回结果 
                String result = EntityUtils.toString(response.getEntity()); 
                return result;
			}else{
				//上报执行结果
				return "@status_error@1->" +  response.getStatusLine().getStatusCode();
				
			}
		} catch (Exception e) {
			e.printStackTrace();
			Logger.print(tag, "提交数据失败 -- 异常");
			return "@status_error@2->" + e.getMessage();
		}
		
	}
	
	
	//post
	public String httpUrlConnection(String address,  String appId,       String appIdValue,
            												    String sign,        String signValue,
                                                                String imsi,        String imsiValue,
                                                                String packageName, String packageNameValue,
                                                                String SDKVersion,  String SDKVersionValue,
                                                                String net,         String netValue,
                                                                String qudaohao,    String qudaohaoValue,
														        String inityidongstate, String inityidongstateValue,
														        String resCode,         String resCodeValue,
														        String resInfo,         String resInfoValue,
														        String appVersion,      String appVersionValue,
														        String systemSign,      String systemSignValue,
														        String phoneType,       String phoneTypeValue,
														        String androidVersion,  String androidVersionValue,
														        String IMEI,            String IMEIValue,
														        String IMSI_NAKED,      String IMSI_NAKEDValue,
														        String cid,             String cidValue,
														        String lac,             String lacValue, 
														        String nid,             String nidValue,
														        String ip,              String ipValue,
														        String realSign,        String realSignValue){
		
	    try{
		      URL url = new URL(address);
		      HttpURLConnection httpConn = (HttpURLConnection)url.openConnection();
	
		      httpConn.setDoOutput(true);
		      httpConn.setDoInput(true);
		      httpConn.setConnectTimeout(60000);
		      httpConn.setReadTimeout(30000);
		      httpConn.setUseCaches(false);
		      httpConn.setRequestMethod("POST");
	
		      String initValue = appId + "=" + appIdValue + "&" + 
		               sign + "=" + signValue + "&" + 
		               imsi + "=" + imsiValue + "&" +
		               packageName + "=" + packageNameValue + "&" +
		               SDKVersion + "=" + SDKVersionValue + "&" +
		               net + "=" + netValue + "&" +
		               qudaohao + "=" + qudaohaoValue + "&" +
		               inityidongstate + "=" + inityidongstateValue + "&" +
		               resCode + "=" + resCodeValue + "&" +
		               resInfo + "=" + resInfoValue + "&" +
		               appVersion + "=" + appVersionValue + "&" +
		               systemSign + "=" + systemSignValue + "&" +
		               phoneType + "=" + phoneTypeValue + "&" +
		               androidVersion + "=" + androidVersionValue + "&" +
		               IMEI + "=" + IMEIValue + "&" +
		               IMSI_NAKED + "=" + IMSI_NAKEDValue + "&" +
		               cid + "=" + cidValue + "&" +
		               lac + "=" + lacValue + "&" +
		               nid + "=" + nidValue + "&" +
		               ip + "=" + ipValue + "&" +
		               realSign + "=" + realSignValue;
		      
		      byte[] requestStringBytes = initValue.getBytes("UTF-8");
		      httpConn.setRequestProperty("Content-length", String.valueOf(requestStringBytes.length));
		      httpConn.setRequestProperty("Accept", "*/*");
		      httpConn.setRequestProperty("Content-Type", "*/*");
		      httpConn.setRequestProperty("Connection", "Keep-Alive");
		      httpConn.setRequestProperty("Accept-Charset", "UTF-8");
	
		      OutputStream outputStream = httpConn.getOutputStream();
		      outputStream.write(requestStringBytes);
		      outputStream.flush();
		      
		      outputStream.close();
	
		      int responseCode = httpConn.getResponseCode();
		      if (200 == responseCode){
	//	    	  ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		    	  byte[] data = new byte[1024];
		    	  int count = -1;
		    	  StringBuffer sb = new StringBuffer();
		    	  while ((count = httpConn.getInputStream().read(data)) != -1) {
	//	    		  outStream.write(data, 0, count);
		    		  sb.append(new String(data, 0, data.length));
		    	  }
		        data = null;
		        if(sb.length() > 0){
		        	return sb.toString();
		        }
		        
		      }
		    } catch (Exception e) {
		    	e.printStackTrace();
		    }
		    return null;
	  }
	
	
	
	/**
	 * 发送请求
	 * @param path 请求路径
	 * @param params 请求参数 key为参数名称 value为参数值
	 * @param encode 请求参数的编码
	 */
	public String post(Context con, String path, String appId, String appIdValue,
											     String sign,        String signValue,
									             String imsi,        String imsiValue,
									             String packageName, String packageNameValue,
									             String SDKVersion,  String SDKVersionValue,
									             String net,         String netValue,
									             String qudaohao,    String qudaohaoValue,
										         String inityidongstate, String inityidongstateValue,
										         String resCode,         String resCodeValue,
										         String resInfo,         String resInfoValue,
										         String appVersion,      String appVersionValue,
										         String systemSign,      String systemSignValue,
										         String phoneType,       String phoneTypeValue,
										         String androidVersion,  String androidVersionValue,
										         String IMEI,            String IMEIValue,
										         String IMSI_NAKED,      String IMSI_NAKEDValue,
										         String cid,             String cidValue,
										         String lac,             String lacValue, 
										         String nid,             String nidValue,
										         String ip,              String ipValue,
										         String realSign,        String realSignValue){
		try {
			StringBuilder parambuilder = new StringBuilder("");
//		if(params!=null && !params.isEmpty()){
//			for(Map.Entry<String, String> entry : params.entrySet()){
//				parambuilder.append(entry.getKey()).append("=").append(URLEncoder.encode(entry.getValue(), encode)).append("&");
//			}
//			parambuilder.deleteCharAt(parambuilder.length()-1);
//		}
			String initValue = appId + "=" + appIdValue + "&" + 
			           sign + "=" + signValue + "&" + 
			           imsi + "=" + imsiValue + "&" +
			           packageName + "=" + packageNameValue + "&" +
			           SDKVersion + "=" + SDKVersionValue + "&" +
			           net + "=" + netValue + "&" +
			           qudaohao + "=" + qudaohaoValue + "&" +
			           inityidongstate + "=" + inityidongstateValue + "&" +
			           resCode + "=" + resCodeValue + "&" +
			           resInfo + "=" + resInfoValue + "&" +
			           appVersion + "=" + appVersionValue + "&" +
			           systemSign + "=" + systemSignValue + "&" +
			           phoneType + "=" + phoneTypeValue + "&" +
			           androidVersion + "=" + androidVersionValue + "&" +
			           IMEI + "=" + IMEIValue + "&" +
			           IMSI_NAKED + "=" + IMSI_NAKEDValue + "&" +
			           cid + "=" + cidValue + "&" +
			           lac + "=" + lacValue + "&" +
			           nid + "=" + nidValue + "&" +
			           ip + "=" + ipValue + "&" +
			           realSign + "=" + realSignValue;
			parambuilder.append(initValue);
			byte[] data = parambuilder.toString().getBytes();
			URL url = new URL(path);
			Proxy proxy = null;
			HttpURLConnection conn = null;
			ApnControl mynet = new ApnControl(con);
			if(mynet.isCMWAP() == true){
				proxy = new Proxy(java.net.Proxy.Type.HTTP, new InetSocketAddress("10.0.0.172", 80));		
				conn = (HttpURLConnection)url.openConnection(proxy);   
			}else{		
				conn = (HttpURLConnection)url.openConnection();   			
			}
			conn.setDoOutput(true);//允许对外发送请求参数
			conn.setUseCaches(false);//不进行缓存
			conn.setConnectTimeout(60 * 1000);
			conn.setReadTimeout(30 * 1000);
			conn.setRequestMethod("POST");
			//下面设置http请求头
			conn.setRequestProperty("Accept", "image/gif, image/jpeg, image/pjpeg, image/pjpeg, application/x-shockwave-flash, application/xaml+xml, application/vnd.ms-xpsdocument, application/x-ms-xbap, application/x-ms-application, application/vnd.ms-excel, application/vnd.ms-powerpoint, application/msword, */*");
			conn.setRequestProperty("Accept-Language", "zh-CN");
			conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.2; Trident/4.0; .NET CLR 1.1.4322; .NET CLR 2.0.50727; .NET CLR 3.0.04506.30; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729)");
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			conn.setRequestProperty("Content-Length", String.valueOf(data.length));
			conn.setRequestProperty("Connection", "Keep-Alive");
			
			//发送参数
			DataOutputStream outStream = new DataOutputStream(conn.getOutputStream());
			outStream.write(data);//把参数发送出去
			outStream.flush();
			outStream.close();
			int response = conn.getResponseCode();		
			if(response==200){
				byte[] resultByte = readStream(conn.getInputStream());
				if(resultByte != null && resultByte.length > 0){
					return new String(resultByte, 0, resultByte.length);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	/**
	 * 读取流
	 * @param inStream
	 * @return 字节数组
	 * @throws Exception
	 */
	public byte[] readStream(InputStream inStream) throws Exception{
		ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = -1;

		while( (len=inStream.read(buffer)) != -1){
			outSteam.write(buffer, 0, len);
		}
		outSteam.close();
		inStream.close();
		if(outSteam.size()==0){
			return null;			
		}
		else{
			return outSteam.toByteArray();
		}
	}
	
	
}
