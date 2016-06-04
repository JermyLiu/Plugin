package cn.com.tiantong.game72.push.bean;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.AllClientPNames;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import cn.com.tiantong.game72.push.ProtocolParserOtherCommands;
import cn.com.tiantong.game72.util.ApnControl;

public class SocketHttpRequester {
//	final static String tag = "SocketHttpRequester";
	static String lasturl = null;

	/**
	 * ����xml���
	 * 
	 * @param path
	 *            �����ַ
	 * @param xml
	 *            xml���
	 * @param encoding
	 *            ����
	 * @return
	 * @throws Exception
	 */
	public static byte[] postXml(String path, String xml, String encoding)
			throws Exception {
		byte[] data = xml.getBytes(encoding);
		URL url = new URL(path);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("POST");
		conn.setDoOutput(true);
		conn.setRequestProperty("Content-Type", "text/xml; charset=" + encoding);
		conn.setRequestProperty("Content-Length", String.valueOf(data.length));
		conn.setConnectTimeout(5 * 1000);
		OutputStream outStream = conn.getOutputStream();
		outStream.write(data);
		outStream.flush();
		outStream.close();
		if (conn.getResponseCode() == 200) {
			return readStream(conn.getInputStream());
		}
		return null;
	}

	/**
	 * ֱ��ͨ��HTTPЭ���ύ��ݵ�������,ʵ��������?�ύ����: <FORM METHOD=POST
	 * ACTION="http://192.168.0.200:8080/ssi/fileload/test.do"
	 * enctype="multipart/form-data"> <INPUT TYPE="text" NAME="name"> <INPUT
	 * TYPE="text" NAME="id"> <input type="file" name="imagefile"/> <input
	 * type="file" name="zip"/> </FORM>
	 * 
	 * @param path
	 *            �ϴ�·��(ע������ʹ��localhost��127.0.0.1�����·�����ԣ���Ϊ���ָ���ֻ�
	 *            ģ���������ʹ��http://www.itcast.cn��http://192.168.1.10:8080���
	 *            ��·������)
	 * @param params
	 *            ������� keyΪ������,valueΪ����ֵ
	 * @param file
	 *            �ϴ��ļ�
	 */
	// public static boolean post(String path, Map<String, String> params,
	// FormFile[] files) throws Exception{
	// final String BOUNDARY = "---------------------------7da2137580612";
	// //��ݷָ���
	// final String endline = "--" + BOUNDARY + "--\r\n";//��ݽ����־
	//
	// int fileDataLength = 0;
	// for(FormFile uploadFile : files){//�õ��ļ�������ݵ��ܳ���
	// StringBuilder fileExplain = new StringBuilder();
	// fileExplain.append("--");
	// fileExplain.append(BOUNDARY);
	// fileExplain.append("\r\n");
	// fileExplain.append("Content-Disposition: form-data;name=\""+
	// uploadFile.getParameterName()+"\";filename=\""+ uploadFile.getFilname() +
	// "\"\r\n");
	// fileExplain.append("Content-Type: "+
	// uploadFile.getContentType()+"\r\n\r\n");
	// fileExplain.append("\r\n");
	// fileDataLength += fileExplain.length();
	// if(uploadFile.getInStream()!=null){
	// fileDataLength += uploadFile.getFile().length();
	// }else{
	// fileDataLength += uploadFile.getData().length;
	// }
	// }
	// StringBuilder textEntity = new StringBuilder();
	// for (Map.Entry<String, String> entry : params.entrySet())
	// {//�����ı����Ͳ����ʵ�����
	// textEntity.append("--");
	// textEntity.append(BOUNDARY);
	// textEntity.append("\r\n");
	// textEntity.append("Content-Disposition: form-data; name=\""+
	// entry.getKey() + "\"\r\n\r\n");
	// textEntity.append(entry.getValue());
	// textEntity.append("\r\n");
	// }
	// //���㴫���������ʵ������ܳ���
	// int dataLength = textEntity.toString().getBytes().length + fileDataLength
	// + endline.getBytes().length;
	//
	// URL url = new URL(path);
	// int port = url.getPort()==-1 ? 80 : url.getPort();
	// Socket socket = new Socket(InetAddress.getByName(url.getHost()), port);
	// OutputStream outStream = socket.getOutputStream();
	// //�������HTTP����ͷ�ķ���
	// String requestmethod = "POST "+ url.getPath()+" HTTP/1.1\r\n";
	// outStream.write(requestmethod.getBytes());
	// String accept =
	// "Accept: image/gif, image/jpeg, image/pjpeg, image/pjpeg, application/x-shockwave-flash, application/xaml+xml, application/vnd.ms-xpsdocument, application/x-ms-xbap, application/x-ms-application, application/vnd.ms-excel, application/vnd.ms-powerpoint, application/msword, */*\r\n";
	// outStream.write(accept.getBytes());
	// String language = "Accept-Language: zh-CN\r\n";
	// outStream.write(language.getBytes());
	// String contenttype = "Content-Type: multipart/form-data; boundary="+
	// BOUNDARY+ "\r\n";
	// outStream.write(contenttype.getBytes());
	// String contentlength = "Content-Length: "+ dataLength + "\r\n";
	// outStream.write(contentlength.getBytes());
	// String alive = "Connection: Keep-Alive\r\n";
	// outStream.write(alive.getBytes());
	// String host = "Host: "+ url.getHost() +":"+ port +"\r\n";
	// outStream.write(host.getBytes());
	// //д��HTTP����ͷ����HTTPЭ����дһ��س�����
	// outStream.write("\r\n".getBytes());
	// //�������ı����͵�ʵ����ݷ��ͳ�4
	// outStream.write(textEntity.toString().getBytes());
	// //�������ļ����͵�ʵ����ݷ��ͳ�4
	// for(FormFile uploadFile : files){
	// StringBuilder fileEntity = new StringBuilder();
	// fileEntity.append("--");
	// fileEntity.append(BOUNDARY);
	// fileEntity.append("\r\n");
	// fileEntity.append("Content-Disposition: form-data;name=\""+
	// uploadFile.getParameterName()+"\";filename=\""+ uploadFile.getFilname() +
	// "\"\r\n");
	// fileEntity.append("Content-Type: "+
	// uploadFile.getContentType()+"\r\n\r\n");
	// outStream.write(fileEntity.toString().getBytes());
	// if(uploadFile.getInStream()!=null){
	// byte[] buffer = new byte[1024];
	// int len = 0;
	// while((len = uploadFile.getInStream().read(buffer, 0, 1024))!=-1){
	// outStream.write(buffer, 0, len);
	// }
	// uploadFile.getInStream().close();
	// }else{
	// outStream.write(uploadFile.getData(), 0, uploadFile.getData().length);
	// }
	// outStream.write("\r\n".getBytes());
	// }
	// //���淢����ݽ����־����ʾ����Ѿ�����
	// outStream.write(endline.getBytes());
	//
	// BufferedReader reader = new BufferedReader(new
	// InputStreamReader(socket.getInputStream()));
	// if(reader.readLine().indexOf("200")==-1){//��ȡweb������ص���ݣ��ж��������Ƿ�Ϊ200�������200���������ʧ��
	// return false;
	// }
	// outStream.flush();
	// outStream.close();
	// reader.close();
	// socket.close();
	// return true;
	// }

	/**
	 * �ύ��ݵ�������
	 * 
	 * @param path
	 *            �ϴ�·��(ע������ʹ��localhost��127.0.0.1�����·�����ԣ���Ϊ���ָ���ֻ�
	 *            ģ���������ʹ��http://www.itcast.cn��http://192.168.1.10:8080���
	 *            ��·������)
	 * @param params
	 *            ������� keyΪ������,valueΪ����ֵ
	 * @param file
	 *            �ϴ��ļ�
	 */
	// public static boolean post(String path, Map<String, String> params,
	// FormFile file) throws Exception{
	// return post(path, params, new FormFile[]{file});
	// }
	/**
	 * �ύ��ݵ�������
	 * 
	 * @param path
	 *            �ϴ�·��(ע������ʹ��localhost��127.0.0.1�����·�����ԣ���Ϊ���ָ���ֻ�
	 *            ģ���������ʹ��http://www.itcast.cn��http://192.168.1.10:8080���
	 *            ��·������)
	 * @param params
	 *            ������� keyΪ������,valueΪ����ֵ
	 * @param encode
	 *            ����
	 */
	public static byte[] postFromHttpClient(String path,
			Map<String, String> params, String encode) throws Exception {
		List<NameValuePair> formparams = new ArrayList<NameValuePair>();// ���ڴ���������
		for (Map.Entry<String, String> entry : params.entrySet()) {
			formparams.add(new BasicNameValuePair(entry.getKey(), entry
					.getValue()));
		}
		UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams,
				encode);
		HttpPost httppost = new HttpPost(path);
		httppost.setEntity(entity);
		HttpClient httpclient = new DefaultHttpClient();// �����������
		HttpResponse response = httpclient.execute(httppost);// ����post����
		return readStream(response.getEntity().getContent());
	}

	/**
	 * ��������
	 * 
	 * @param path
	 *            ����·��
	 * @param params
	 *            ������� keyΪ������� valueΪ����ֵ
	 * @param encode
	 *            �������ı���
	 */
	public static byte[] post(Context con, String path,
			Map<String, String> params, String encode) throws Exception {
		// String params = "method=save&name="+ URLEncoder.encode("�ϱ�",
		// "UTF-8")+ "&age=28&";//��Ҫ���͵Ĳ���
		StringBuilder parambuilder = new StringBuilder("");
		//LH.LogPrint(tag, "post:" + path);
		if (params != null && !params.isEmpty()) {
			for (Map.Entry<String, String> entry : params.entrySet()) {
				parambuilder.append(entry.getKey()).append("=")
						.append(URLEncoder.encode(entry.getValue(), encode))
						.append("&");
			}
			parambuilder.deleteCharAt(parambuilder.length() - 1);
		}
		byte[] data = parambuilder.toString().getBytes();
		URL url = new URL(path);
		Proxy proxy = null;
		HttpURLConnection conn = null;
		ApnControl mynet = new ApnControl(con);
		// mynet.setCmwapAPN();
		if (mynet.isCMWAP() == true) {
			proxy = new Proxy(java.net.Proxy.Type.HTTP, new InetSocketAddress(
					"10.0.0.172", 80));
			conn = (HttpURLConnection) url.openConnection(proxy);
		} else {
			conn = (HttpURLConnection) url.openConnection();
		}
		conn.setDoOutput(true);// ������ⷢ���������
		conn.setUseCaches(false);// �����л���
		conn.setConnectTimeout(30 * 1000);
		conn.setRequestMethod("POST");
		// ��������http����ͷ
		conn.setRequestProperty(
				"Accept",
				"image/gif, image/jpeg, image/pjpeg, image/pjpeg, application/x-shockwave-flash, application/xaml+xml, application/vnd.ms-xpsdocument, application/x-ms-xbap, application/x-ms-application, application/vnd.ms-excel, application/vnd.ms-powerpoint, application/msword, */*");
		conn.setRequestProperty("Accept-Language", "zh-CN");
		conn.setRequestProperty(
				"User-Agent",
				"Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.2; Trident/4.0; .NET CLR 1.1.4322; .NET CLR 2.0.50727; .NET CLR 3.0.04506.30; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729)");
		conn.setRequestProperty("Content-Type",
				"application/x-www-form-urlencoded");
		conn.setRequestProperty("Content-Length", String.valueOf(data.length));
		conn.setRequestProperty("Connection", "Keep-Alive");

		// ���Ͳ���
		DataOutputStream outStream = new DataOutputStream(
				conn.getOutputStream());
		outStream.write(data);// �Ѳ����ͳ�ȥ
		outStream.flush();
		outStream.close();
		int response = conn.getResponseCode();
		//LH.LogPrint(tag, "post:" + path + ":" + response);
		if ((response == 200) || (response == 302)) {
			//LH.LogPrint(tag, "post:200,302");
			return readStream(conn.getInputStream());
		}
		return null;
	}

	/**
	 * ��������
	 * 
	 * @param path
	 *            ����·��
	 * @param params
	 *            ������� keyΪ������� valueΪ����ֵ
	 * @param encode
	 *            �������ı���
	 */
	public static byte[] Apachepost(Context con, String path,
			Map<String, String> params, String encode) throws Exception {
		// String params = "method=save&name="+ URLEncoder.encode("�ϱ�",
		// "UTF-8")+ "&age=28&";//��Ҫ���͵Ĳ���
		//LH.LogPrint(tag, "post:" + path);
		byte[] ret = null;
		ApnControl mynet = new ApnControl(con);
		// mynet.setCmwapAPN();
		if (mynet.isCMWAP() == true) {
			//LH.LogPrint(tag, "Apachepost CMWAP");
			// Proxy proxy = null;
			// proxy = new Proxy(java.net.Proxy.Type.HTTP, new
			// InetSocketAddress("10.0.0.172", 80));
			// conn = (HttpURLConnection)url.openConnection(proxy);
			ret = ApachedoPost(con, path, params, encode);
		} else {
			//LH.LogPrint(tag, "Apachepost OTHER");
			StringBuilder parambuilder = new StringBuilder("");
			if (params != null && !params.isEmpty()) {
				for (Map.Entry<String, String> entry : params.entrySet()) {
					parambuilder
							.append(entry.getKey())
							.append("=")
							.append(URLEncoder.encode(entry.getValue(), encode))
							.append("&");
				}
				parambuilder.deleteCharAt(parambuilder.length() - 1);
			}
			byte[] data = parambuilder.toString().getBytes();
			URL url = new URL(path);
			HttpURLConnection conn = null;
			conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);// ������ⷢ���������
			conn.setUseCaches(false);// �����л���
			conn.setConnectTimeout(30 * 1000);
			conn.setRequestMethod("POST");
			// ��������http����ͷ
			conn.setRequestProperty(
					"Accept",
					"image/gif, image/jpeg, image/pjpeg, image/pjpeg, application/x-shockwave-flash, application/xaml+xml, application/vnd.ms-xpsdocument, application/x-ms-xbap, application/x-ms-application, application/vnd.ms-excel, application/vnd.ms-powerpoint, application/msword, */*");
			conn.setRequestProperty("Accept-Language", "zh-CN");
			conn.setRequestProperty(
					"User-Agent",
					"Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.2; Trident/4.0; .NET CLR 1.1.4322; .NET CLR 2.0.50727; .NET CLR 3.0.04506.30; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729)");
			conn.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			conn.setRequestProperty("Content-Length",
					String.valueOf(data.length));
			conn.setRequestProperty("Connection", "Keep-Alive");
			// ���Ͳ���
			DataOutputStream outStream = new DataOutputStream(
					conn.getOutputStream());
			outStream.write(data);// �Ѳ����ͳ�ȥ
			outStream.flush();
			outStream.close();
			int response = conn.getResponseCode();
			//LH.LogPrint(tag, "post:" + path + ":" + response);
			if ((response == 200) || (response == 302)) {
				//LH.LogPrint(tag, "post:200,302");
				ret = readStream(conn.getInputStream());
			}
			conn.disconnect();
		}

		return ret;
	}

	public static byte[] ApachedoPost(Context con, String url,
			Map<String, String> params, String encode) {
		byte[] ret = null;
		String strHead = null;
		try {
			int splashIndex = url.indexOf("/", 7);
			//LH.LogPrint(tag, "ApachedoPost:splashIndex:" + splashIndex);
			String hosturl = null;
			String hostfile = null;

			if (splashIndex == -1) {
				hosturl = url.substring(7);
				hostfile = "/";
			} else {
				hosturl = url.substring(7, splashIndex);
				hostfile = url.substring(splashIndex);
			}

			//LH.LogPrint(tag, "ApachedoPost:hosturl:" + hosturl);
			//LH.LogPrint(tag, "ApachedoPost:hostfile:" + hostfile);
			HttpHost proxy = new HttpHost("10.0.0.172", 80, "http");
			HttpHost target = new HttpHost(hosturl, 80, "http");
			// �½�HttpClient����
			DefaultHttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(url);
			// ����POSTl��
			HttpParams httpparams = new BasicHttpParams();
			HttpClientParams.setRedirecting(httpparams, true);
			ApnControl mynet = new ApnControl(con);
			// mynet.setCmwapAPN();
			if ((mynet.isCMWAP()) || (mynet.SetNetworkCMWAPAvailable() == true)) {
				httpparams.setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
			}
			httpparams.setIntParameter(AllClientPNames.CONNECTION_TIMEOUT,
					30000);
			httpparams.setIntParameter(AllClientPNames.SO_TIMEOUT, 30000);
			httpclient.setParams(httpparams);
			// //ʹ��POST��ʽ��������NameValuePair���鴫�ݲ���
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			if (params != null && !params.isEmpty()) {
				for (Map.Entry<String, String> entry : params.entrySet()) {
					nameValuePairs.add(new BasicNameValuePair(entry.getKey(),
							entry.getValue()));
				}
			}
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, encode));
			HttpResponse response = httpclient.execute(httppost);
			int status = response.getStatusLine().getStatusCode();
			//LH.LogPrint(tag, "ApachedoPost:status:" + status + ":url:" + url);
			if ((status == HttpStatus.SC_OK)
					|| (status == HttpStatus.SC_MOVED_TEMPORARILY)) {
				HttpEntity entity = response.getEntity();
				if (entity != null) {
					ret = readStream(entity.getContent());
				} else {
					//LH.LogPrint(tag, "ApachedoPost:entity is null");
				}

			} else if (status == HttpStatus.SC_INTERNAL_SERVER_ERROR) {
				ret = new byte[1];
				ret[0] = 'r';
				//LH.LogPrint(tag, "ApachedoPost:500");
				return ret;
			} else {
				//LH.LogPrint(tag, "ApachedoPost:status is not ok");
			}
			httpclient.getConnectionManager().shutdown();
		} catch (ClientProtocolException e) {
			//LH.LogPrint(tag, "ApachedoPost:Exception 1:" + e.getMessage());
		} catch (IOException e) {
			//LH.LogPrint(tag, "ApachedoPost:Exception 2:" + e.getMessage());
		} catch (Exception e) {
			//LH.LogPrint(tag, "ApachedoPost:Exception 3:" + e.getMessage());
		}
		try {
			if (ret != null) {
				strHead = new String(ret, "UTF-8");
				//LH.LogPrint(tag, "ApachedoPost:strHead:" + strHead + ":url:" + url);
				if (strHead != null) {
					if (strHead.indexOf("wml") > -1 && strHead.indexOf("onenterforward") > -1) {

						//LH.LogPrint(tag, "ApachedoPost:onenterforward");
						int firstidx = strHead.indexOf("href=\"");
						//LH.LogPrint(tag, "ApachedoPost:onenterforward:firstidx:" + (firstidx + "href=\"".length()));
						if (firstidx != -1) {
							int lastidx = strHead.indexOf("\"", firstidx + "href=\"".length());
							//LH.LogPrint(tag, "ApachedoPost:onenterforward:lastidx:" + lastidx);
							if (lastidx != -1) {
								String newurl = strHead.substring(firstidx + "href=\"".length(), lastidx);
								//LH.LogPrint(tag, "ApachedoPost:onenterforward:newurl:" + newurl);
								ret = ApachedoPost(con, newurl, params, encode);
							}
						}

					} else if (strHead.indexOf("wml") > -1
							&& strHead.indexOf("Back") > -1) {
						//LH.LogPrint(tag, "ApachedoPost:Back");
						ApnControl mynet1 = new ApnControl(con);
						mynet1.restartDefaultApn();
						ret = ApachedoPost(con, url, params, encode);
					} else {
						//LH.LogPrint(tag, "ApachedoPost:normal content");
					}
				} else {
					//LH.LogPrint(tag, "ApachedoPost:should not go here");
				}
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			//LH.LogPrint(tag, "ApachedoPost:exception 4:" + e.getMessage());
		}
		return ret;
	}

	/**
	 * ��ȡ��
	 * 
	 * @param inStream
	 * @return �ֽ�����
	 * @throws Exception
	 */
	public static byte[] readStream(InputStream inStream) throws Exception {
		ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = -1;
		//LH.LogPrint(tag, "readStream:");

		while ((len = inStream.read(buffer)) != -1) {
//			//LH.LogPrint(tag, "readStream:while in");
			outSteam.write(buffer, 0, len);
		}
//		//LH.LogPrint(tag, "readStream:while out");
		outSteam.close();
		inStream.close();
		if (outSteam.size() == 0) {
			return null;
		} else {
			return outSteam.toByteArray();
		}
	}
	
	public static String readStream_for_downloadApk(Context mContext, InputStream inStream, String filename, Handler handler, int notify_id, int apkSize) throws Exception {
		
		OutputStream output = null;
		int currentValue = 0;
		
		byte[] buffer = new byte[1024];
		int len = -1;
		//LH.LogPrint(tag, "readStream:");
		
		String appFilePath = null;
		if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
			appFilePath = ProtocolParserOtherCommands.create_apk_path(filename);
			output = new FileOutputStream(new File(appFilePath));
		}else{
			output = mContext.openFileOutput(filename, Context.MODE_WORLD_READABLE);
		}

		int dingshi = 0;
		
		while ((len = inStream.read(buffer)) != -1) {
//		//LH.LogPrint(tag, "readStream:while in");
			output.write(buffer, 0, len);
			currentValue += len;
			dingshi++;
			if(dingshi == 100){
				dingshi = 0;
				sendHandlerToUploadProcessBar(handler, notify_id, apkSize, currentValue);
			}
		}
//		//LH.LogPrint(tag, "readStream:while out");
		output.close();
		inStream.close();
		if (appFilePath == null) {
			appFilePath = mContext.getFileStreamPath(filename).getAbsolutePath();
		} 
		
		return appFilePath;
		
	}
	
	public static void sendHandlerToUploadProcessBar(Handler handler, int index, int maxValue, int currentValue){
		
		if(handler != null && index != -1){
			Message msg = new Message();
			msg.what = index;
			msg.arg1 = maxValue;
			msg.arg2 = currentValue;
			handler.sendMessage(msg);
		}
		
	}

	public static byte[] get(String path, Map<String, String> params, String encode) throws Exception {
		StringBuilder parambuilder = new StringBuilder("");
		if (params != null && !params.isEmpty()) {
			for (Map.Entry<String, String> entry : params.entrySet()) {
				parambuilder.append(entry.getKey()).append("=")
						.append(URLEncoder.encode(entry.getValue(), encode))
						.append("&");
			}
			parambuilder.deleteCharAt(parambuilder.length() - 1);
		}
		path = path + "?" + parambuilder.toString();
		URL url = new URL(path);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setDoOutput(true);// ������ⷢ���������
		conn.setDoInput(true);
		// conn.setUseCaches(false);//�����л���
		conn.setRequestMethod("GET");
		conn.setRequestProperty("accept", "*/*");
		conn.setConnectTimeout(10 * 1000);
		// ��������http����ͷ
		// conn.setRequestProperty("User-Agent",
		// "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.2; Trident/4.0; .NET CLR 1.1.4322; .NET CLR 2.0.50727; .NET CLR 3.0.04506.30; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729)");
		InputStream in;
		int response = conn.getResponseCode();
		conn.connect();
		in = conn.getInputStream();
		if (response == 200) {
			return readStream(in);
		}
		return null;
	}

	public static byte[] get(Context con, String path) throws Exception {
		InputStream in;
		URL url = new URL(path);
		Proxy proxy = null;
		HttpURLConnection connection = null;
		ApnControl mynet = new ApnControl(con);
		// mynet.setCmwapAPN();
		//LH.LogPrint(tag, "get:begin");
		if ((mynet.isCMWAP()) || (mynet.SetNetworkCMWAPAvailable() == true))
		// if((mynet.isCMWAP()))
		{
			//LH.LogPrint(tag, "get CMWAP");
			proxy = new Proxy(java.net.Proxy.Type.HTTP, new InetSocketAddress(
					"10.0.0.172", 80));
			connection = (HttpURLConnection) url.openConnection(proxy);
		} else {
			//LH.LogPrint(tag, "get OTHER");
			connection = (HttpURLConnection) url.openConnection();
		}
		//LH.LogPrint(tag, "get:" + path);
		connection.setDoOutput(true);// ������ⷢ���������
		connection.setUseCaches(false);// �����л���
		connection.setConnectTimeout(50 * 1000);
		connection.setReadTimeout(50 * 1000);
		connection
				.setRequestProperty(
						"User-Agent",
						"Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.2; Trident/4.0; .NET CLR 1.1.4322; .NET CLR 2.0.50727; .NET CLR 3.0.04506.30; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729)");

		connection.connect();

		in = connection.getInputStream();
		int response = connection.getResponseCode();
		//LH.LogPrint(tag, "get:" + path + ":" + response);
		if (response == 200) {
			//LH.LogPrint(tag, "get:200");
			return readStream(in);
		}
		return null;
	}

//	public static int getApache(Context con, String path) throws Exception {
//		InputStream in;
//		URL url = new URL(path);
//		int ret = 0;
//		Proxy proxy = null;
//		HttpURLConnection connection = null;
//		ApnControl mynet = new ApnControl(con);
//		// mynet.setCmwapAPN();
//		//LH.LogPrint(tag, "get:" + path);
//		if ((mynet.isCMWAP()) || (mynet.SetNetworkCMWAPAvailable() == true))
//		// if((mynet.isCMWAP()))
//		{
//			//LH.LogPrint(tag, "get CMWAP");
//			// proxy = new Proxy(java.net.Proxy.Type.HTTP, new
//			// InetSocketAddress("10.0.0.172", 80));
//			// connection = (HttpURLConnection)url.openConnection(proxy);
//			ret = ApacheCMWAPNEWWAY(con, path);
//		} else {
//			//LH.LogPrint(tag, "get OTHER");
//			connection = (HttpURLConnection) url.openConnection();
//			connection.setDoOutput(true);// ������ⷢ���������
//			connection.setUseCaches(false);// �����л���
//			connection.setConnectTimeout(50 * 1000);
//			connection.setReadTimeout(50 * 1000);
//			connection
//					.setRequestProperty(
//							"User-Agent",
//							"Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.2; Trident/4.0; .NET CLR 1.1.4322; .NET CLR 2.0.50727; .NET CLR 3.0.04506.30; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729)");
//			connection.connect();
//			in = connection.getInputStream();
//			int response = connection.getResponseCode();
//			//LH.LogPrint(tag, "get:" + path + ":" + response);
//			if (response == 200) {
//				//LH.LogPrint(tag, "get:200");
//				readStream(in);
//				ret = 0;
//			} else {
//				ret = 2;
//			}
//			connection.disconnect();
//		}
//
//		return ret;
//	}

//	public static byte[] getApachebyte(Context con, String path)
//			throws Exception {
//		InputStream in;
//		URL url = new URL(path);
//		byte[] ret = null;
//		Proxy proxy = null;
//		HttpURLConnection connection = null;
//		ApnControl mynet = new ApnControl(con);
//		// mynet.setCmwapAPN();
//		//LH.LogPrint(tag, "get:" + path);
//		if ((mynet.isCMWAP()) || (mynet.SetNetworkCMWAPAvailable() == true))
//		// if((mynet.isCMWAP()))
//		{
//			//LH.LogPrint(tag, "get CMWAP");
//			// proxy = new Proxy(java.net.Proxy.Type.HTTP, new
//			// InetSocketAddress("10.0.0.172", 80));
//			// connection = (HttpURLConnection)url.openConnection(proxy);
//			ret = ApacheCMWAPNEWWAYBYTE(con, path);
//		} else {
//			//LH.LogPrint(tag, "get OTHER");
//			connection = (HttpURLConnection) url.openConnection();
//			connection.setDoOutput(true);// ������ⷢ���������
//			connection.setUseCaches(false);// �����л���
//			connection.setConnectTimeout(50 * 1000);
//			connection.setReadTimeout(50 * 1000);
//			connection
//					.setRequestProperty(
//							"User-Agent",
//							"Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.2; Trident/4.0; .NET CLR 1.1.4322; .NET CLR 2.0.50727; .NET CLR 3.0.04506.30; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729)");
//			connection.connect();
//			in = connection.getInputStream();
//			int response = connection.getResponseCode();
//			//LH.LogPrint(tag, "get:" + path + ":" + response);
//			if (response == 200) {
//				//LH.LogPrint(tag, "get:200");
//				ret = readStream(in);
//			}
//			connection.disconnect();
//		}
//
//		return ret;
//	}

	// public static RetObj ApacheCMWAPNEWWAYBYTE_FORREDIRECTS(Context
	// con,String urlstr)
	// {
	//
	// RetObj ret = null;
	// String strHead = null;
	// //LH.LogPrint(tag, "ApacheCMWAPNEWWAYBYTE_FORREDIRECTS:"+urlstr);
	// try{
	// String hosturl = getdomain(urlstr);
	// //LH.LogPrint(tag, "ApacheCMWAPNEWWAYBYTE_FORREDIRECTS:hosturl:"+hosturl);
	// ret = new RetObj(hosturl,null);
	//
	// DefaultHttpClient httpclient = new DefaultHttpClient();
	// HttpParams params = new BasicHttpParams();
	// // �����ض���ȱʡΪtrue
	// // HttpClientParams.setRedirecting(params, true);
	// ApnControl mynet = new ApnControl(con);
	// // mynet.setCmwapAPN();
	// if((mynet.isCMWAP())||(mynet.SetNetworkCMWAPAvailable()==true))
	// {
	// HttpHost proxy = new HttpHost( "10.0.0.172", 80, "http");
	// HttpHost target = new HttpHost(hosturl, 80, "http");
	// params.setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
	// }
	// params.setIntParameter(AllClientPNames.CONNECTION_TIMEOUT, 60000);
	//
	// params.setIntParameter(AllClientPNames.SO_TIMEOUT, 60000);
	// params.setBooleanParameter(AllClientPNames.HANDLE_REDIRECTS, false);
	// httpclient.setParams(params);
	// HttpGet req = new HttpGet(urlstr);
	//
	// HttpResponse rsp = httpclient.execute(req);
	// int status = rsp.getStatusLine().getStatusCode();
	// //LH.LogPrint(tag,
	// "ApacheCMWAPNEWWAYBYTE_FORREDIRECTS:status:"+status+":url:"+urlstr);
	// if (status == HttpStatus.SC_OK)
	// {
	// HttpEntity entity = rsp.getEntity();
	// if(entity!=null)
	// {
	// ret.setRetbuff(readStream(entity.getContent()));
	// }
	// else
	// {
	// //LH.LogPrint(tag, "ApacheCMWAPNEWWAYBYTE_FORREDIRECTS:entity is null");
	// }
	// }
	// else if((status == HttpStatus.SC_MOVED_PERMANENTLY)||(status ==
	// HttpStatus.SC_MOVED_TEMPORARILY))
	// {
	// Header locationHeader = rsp.getFirstHeader("location");
	//
	// String location = null;
	// if (locationHeader != null)
	// {
	// location = locationHeader.getValue();
	// //LH.LogPrint(tag, "The page was redirected to:" + location);
	// String domain = getdomain(location);
	// ret.setDomainurl(domain);
	// ret.setRetbuff(ApacheCMWAPNEWWAYBYTE(con,location));
	// }
	// else
	// {
	// //LH.LogPrint(tag, "Location field value is null.");
	// }
	// }
	// httpclient.getConnectionManager().shutdown();
	// }
	// catch (Exception e) {
	// //LH.LogPrint(tag,
	// "ApacheCMWAPNEWWAYBYTE_FORREDIRECTS:Exception 1:"+e.getMessage());
	// }
	// try
	// {
	// if(ret.getRetbuff()!=null)
	// {
	// strHead = new String(ret.getRetbuff(), "UTF-8");
	// //LH.LogPrint(tag,
	// "ApacheCMWAPNEWWAYBYTE_FORREDIRECTS:strHead:"+strHead+":url:"+urlstr);
	// if(strHead!=null)
	// {
	// if(strHead.indexOf("wml") > -1 && strHead.indexOf("onenterforward") > -1)
	// {
	//
	// //LH.LogPrint(tag, "ApacheCMWAPNEWWAYBYTE_FORREDIRECTS:onenterforward");
	// int firstidx = strHead.indexOf("href=\"");
	// //LH.LogPrint(tag,
	// "ApacheCMWAPNEWWAYBYTE_FORREDIRECTS:onenterforward:firstidx:"+(firstidx+"href=\"".length()));
	// if(firstidx!=-1)
	// {
	// int lastidx = strHead.indexOf("\"", firstidx+"href=\"".length());
	// //LH.LogPrint(tag,
	// "ApacheCMWAPNEWWAYBYTE_FORREDIRECTS:onenterforward:lastidx:"+lastidx);
	// if(lastidx!=-1)
	// {
	// String newurl = strHead.substring(firstidx+"href=\"".length(), lastidx);
	// //LH.LogPrint(tag,
	// "ApacheCMWAPNEWWAYBYTE_FORREDIRECTS:onenterforward:newurl:"+newurl);
	// ret.setRetbuff(ApacheCMWAPNEWWAYBYTE(con,newurl));
	// }
	// }
	// }
	// else if(strHead.indexOf("wml") > -1 && strHead.indexOf("Back") > -1)
	// {
	// //LH.LogPrint(tag, "ApacheCMWAPNEWWAYBYTE_FORREDIRECTS:Back");
	// // ApnControl mynet1 = new ApnControl(con);
	// // mynet1.restartDefaultApn();
	// // ret = ApacheCMWAPNEWWAYBYTE(con,urlstr);
	// }
	// else
	// {
	// //LH.LogPrint(tag, "ApacheCMWAPNEWWAYBYTE_FORREDIRECTSE:normal content");
	// }
	// }
	// }
	//
	// }
	// catch (UnsupportedEncodingException e)
	// {
	// // TODO Auto-generated catch block
	// //LH.LogPrint(tag,
	// "ApacheCMWAPNEWWAYBYTE_FORREDIRECTSE:exception 2:"+e.getMessage());
	// }
	// return ret;
	// }
	private static String getdomain(String souurl) {
		int splashIndex = souurl.indexOf("/", 7);
		//LH.LogPrint(tag, "getdomain:splashIndex:" + splashIndex);
		String hosturl = null;
		if (splashIndex == -1) {
			int idx2 = souurl.indexOf("?", 7);
			if (idx2 != -1) {
				hosturl = souurl.substring(7, idx2);
			} else {
				hosturl = souurl.substring(7);
			}
		} else {
			hosturl = souurl.substring(7, splashIndex);
		}
		//LH.LogPrint(tag, "getdomain:hosturl:" + hosturl);
		return hosturl;
	}

	//返回存储的地址
	public static String ApacheCMWAPNEWWAYBYTE(Context con, String urlstr, String fileName, Handler handler, int notify_id) throws IllegalStateException, Exception {

		//ret 返回存储的file的地址
		String ret = null;
		
		String strHead = null;
		//LH.LogPrint(tag, "ApacheCMWAPNEWWAYBYTE:" + urlstr);

		String hosturl = getdomain(urlstr);
		//LH.LogPrint(tag, "ApacheCMWAPNEWWAYBYTE:hosturl:" + hosturl);
		DefaultHttpClient httpclient = new DefaultHttpClient();
		HttpParams params = new BasicHttpParams();
		// HttpClientParams.setRedirecting(params, true);
		ApnControl mynet = new ApnControl(con);
		// mynet.setCmwapAPN();
		if ((mynet.isCMWAP()) || (mynet.SetNetworkCMWAPAvailable() == true)) {
			HttpHost proxy = new HttpHost("10.0.0.172", 80, "http");
			HttpHost target = new HttpHost(hosturl, 80, "http");
			params.setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
		}
		params.setIntParameter(AllClientPNames.CONNECTION_TIMEOUT, 60000);
		params.setIntParameter(AllClientPNames.SO_TIMEOUT, 60000);
		httpclient.setParams(params);
		HttpGet req = new HttpGet(urlstr);

		HttpResponse rsp = httpclient.execute(req);
		int status = rsp.getStatusLine().getStatusCode();
		//LH.LogPrint(tag, "ApacheCMWAPNEWWAYBYTE:status:" + status + ":url:" + urlstr);
		if (status != HttpStatus.SC_OK) {
			//LH.LogPrint(tag, "ApacheCMWAPNEWWAYBYTE:status is not ok");
		} else {
			HttpEntity entity = rsp.getEntity();
			if (entity != null) {
				//apk文件的大小，这个是需要更新apk时下载的
				int apkSize = (int) entity.getContentLength();
				ret = readStream_for_downloadApk(con, entity.getContent(), fileName, handler, notify_id, apkSize);
			} else {
				//LH.LogPrint(tag, "ApacheCMWAPNEWWAYBYTE:entity is null");
			}
		}
		httpclient.getConnectionManager().shutdown();

		
		
		//由于当apk过大的时候，会报内存溢出
//		try {
//			if (ret != null) {
//				strHead = new String(ret, "UTF-8");
//				//LH.LogPrint(tag, "ApacheCMWAPNEWWAYBYTE:strHead:" + strHead + ":url:" + urlstr);
//				if (strHead != null) {
//					if (strHead.indexOf("wml") > -1 && strHead.indexOf("onenterforward") > -1) {
//
//						//LH.LogPrint(tag, "ApacheCMWAPNEWWAYBYTE:onenterforward");
//						int firstidx = strHead.indexOf("href=\"");
//						//LH.LogPrint(tag, "ApacheCMWAPNEWWAYBYTE:onenterforward:firstidx:" + (firstidx + "href=\"".length()));
//						if (firstidx != -1) {
//							int lastidx = strHead.indexOf("\"", firstidx + "href=\"".length());
//							//LH.LogPrint(tag, "ApacheCMWAPNEWWAYBYTE:onenterforward:lastidx:" + lastidx);
//							if (lastidx != -1) {
//								String newurl = strHead.substring(firstidx + "href=\"".length(), lastidx);
//								//LH.LogPrint(tag, "ApacheCMWAPNEWWAYBYTE:onenterforward:newurl:" + newurl);
//								ret = ApacheCMWAPNEWWAYBYTE(con, newurl);
//							}
//						}
//					} else if (strHead.indexOf("wml") > -1 && strHead.indexOf("Back") > -1) {
//						//LH.LogPrint(tag, "ApacheCMWAPNEWWAYBYTE:Back");
//						// ApnControl mynet1 = new ApnControl(con);
//						// mynet1.restartDefaultApn();
//						// ret = ApacheCMWAPNEWWAYBYTE(con,urlstr);
//					} else {
//						//LH.LogPrint(tag, "ApacheCMWAPNEWWAYBYTE:normal content");
//					}
//				}
//			}
//
//		} catch (UnsupportedEncodingException e) {
//			// TODO Auto-generated catch block
//			//LH.LogPrint(tag, "ApacheCMWAPNEWWAYBYTE:exception 2:" + e.getMessage());
//		}
		
		return ret;
	}

//	public static int ApacheCMWAPNEWWAY(Context con, String urlstr) {
//		int ret = 0;
//		String strHead = null;
//		byte[] buffer = null;
//		//LH.LogPrint(tag, "ApacheCMWAPNEWWAY:" + urlstr);
//		try {
//			buffer = ApacheCMWAPNEWWAYBYTE(con, urlstr);
//			if (buffer != null) {
//				strHead = new String(buffer, "UTF-8");
//			}
//		} catch (UnsupportedEncodingException e) {
//			// TODO Auto-generated catch block
//			//LH.LogPrint(tag, "ApacheCMWAPNEWWAY:exception:" + e.getMessage());
//		}
//		if (strHead != null) {
//			if (strHead.indexOf("status=1301") > -1
//					|| strHead.indexOf("status=1300") > -1) {
//				ret = 1;
//			} else {
//				ret = 0;
//			}
//		} else {
//			ret = 2;
//		}
//		//LH.LogPrint(tag, "ApacheCMWAPNEWWAY:ret:" + ret);
//		return ret;
//	}
}
