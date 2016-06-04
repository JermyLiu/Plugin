package cn.com.tiantong.game72.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import com.alibaba.fastjson.JSONObject;

import android.content.Context;

//import sun.misc.BASE64Decoder; 
//import sun.misc.BASE64Encoder; 

public class DES_JAVA {
	
//	private static final String TAG = "DES_JAVA";

	public DES_JAVA() {
	}

	public static String encrypt1(Context con, Map<String, String> dataMap) {
		// DES算法要求有一个可信任的随机数源
		SecureRandom sr = new SecureRandom();
		String rtnStr = "";
		try {
			
			//json处理
			String plainText = turnToJson(dataMap);
			//LH.LogPrint(TAG, "plainText加密：" + plainText);
			if(plainText != null && ! "".equals(plainText.trim())){
				
				byte rawKeyData[] = "20150109".getBytes();/* 用某种方法获得密匙数据 */
				// 从原始密匙数据创建DESKeySpec对象
				DESKeySpec dks;
				dks = new DESKeySpec(rawKeyData);
				// 创建一个密匙工厂，然后用它把DESKeySpec转换成
				// 一个SecretKey对象
				SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
				SecretKey key = keyFactory.generateSecret(dks);
				// Cipher对象实际完成加密操作
				Cipher cipher = Cipher.getInstance("DES");
				// 用密匙初始化Cipher对象
				cipher.init(Cipher.ENCRYPT_MODE, key, sr);
				// 现在，获取数据并加密
				byte data[] = plainText.getBytes("utf-8");/* 用某种方法获取数据 */
				// 正式执行加密操作
				byte[] encryptedData = cipher.doFinal(data);
//				rtnStr = new String(encryptedData);
//				//LH.LogPrint(TAG, "DES加密：" + rtnStr);
				
				rtnStr = encode(encryptedData);
				//LH.LogPrint(TAG, "BASE64处理过的DES密文：" + rtnStr);
			}
			
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		} catch (RuntimeException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return rtnStr;
	}
	
	//把数据转换成json数据
	private static String turnToJson(Map<String, String> dataMap){
		
		if(dataMap != null && ! dataMap.isEmpty()){
			
		   String jsonStr = JSONObject.toJSONString(dataMap);
		   
		   //LH.LogPrint(TAG, "jsonStr-->" + jsonStr);
		   
		   return jsonStr;
		}
		
		return null;
		
	}

	public static String decrypt1(Context con, String encryptText) {
		// DES算法要求有一个可信任的随机数源
		SecureRandom sr = new SecureRandom();
		String rtnStr = "";
		try {
			byte rawKeyData[] = "20150109".getBytes(); /* 用某种方法获取原始密匙数据 */
			// 从原始密匙数据创建一个DESKeySpec对象
			DESKeySpec dks;
			dks = new DESKeySpec(rawKeyData);
			// 创建一个密匙工厂，然后用它把DESKeySpec对象转换成
			// 一个SecretKey对象
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
			SecretKey key = keyFactory.generateSecret(dks);
			// Cipher对象实际完成解密操作
			Cipher cipher = Cipher.getInstance("DES");
			// 用密匙初始化Cipher对象
			cipher.init(Cipher.DECRYPT_MODE, key, sr);
			// 现在，获取数据并解密
			byte encryptedData[] = decode(encryptText);/* 获得经过加密的数据 */
			
			//LH.LogPrint(TAG, "解密：BASH64处理过的DES密文：" + new String(encryptedData));
			
			// byte encryptedData[] = DES.fromBase64(encryptText);/* 获得经过加密的数据
			// */
			// byte[] decoded = Base64.encode(encryptText.getBytes());
			// byte encryptedData[] = (new
			// BASE64Decoder().decodeBuffer(encryptText));/* 获得经过加密的数据 */
			// byte encryptedData[] = encryptText.getBytes("Unicode");/*
			// 获得经过加密的数据 */
			// 正式执行解密操作
			byte decryptedData[] = cipher.doFinal(encryptedData);
			rtnStr = new String(decryptedData, "utf-8");
			//LH.LogPrint(TAG, "DES解密结果：" + rtnStr);
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		}
		/*
		 * catch (IOException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); }
		 */
		catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rtnStr;
	}

	private static final char[] legalChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/-:.".toCharArray();

	/** Base64 encode the given data */
	private static String encode(byte[] data) {
		int start = 0;
		int len = data.length;
		StringBuffer buf = new StringBuffer(data.length * 3 / 2);

		int end = len - 3;
		int i = start;
		int n = 0;

		while (i <= end) {
			int d = ((((int) data[i]) & 0x0ff) << 16)
					| ((((int) data[i + 1]) & 0x0ff) << 8)
					| (((int) data[i + 2]) & 0x0ff);

			buf.append(legalChars[(d >> 18) & 63]);
			buf.append(legalChars[(d >> 12) & 63]);
			buf.append(legalChars[(d >> 6) & 63]);
			buf.append(legalChars[d & 63]);

			i += 3;

			if (n++ >= 14) {
				n = 0;
				buf.append(" ");
			}
		}

		if (i == start + len - 2) {
			int d = ((((int) data[i]) & 0x0ff) << 16)
					| ((((int) data[i + 1]) & 255) << 8);

			buf.append(legalChars[(d >> 18) & 63]);
			buf.append(legalChars[(d >> 12) & 63]);
			buf.append(legalChars[(d >> 6) & 63]);
			buf.append("=");
		} else if (i == start + len - 1) {
			int d = (((int) data[i]) & 0x0ff) << 16;

			buf.append(legalChars[(d >> 18) & 63]);
			buf.append(legalChars[(d >> 12) & 63]);
			buf.append("==");
		}

		return buf.toString();
	}

	private static int decode(char c) {
		if (c >= 'A' && c <= 'Z')
			return ((int) c) - 65;
		else if (c >= 'a' && c <= 'z')
			return ((int) c) - 97 + 26;
		else if (c >= '0' && c <= '9')
			return ((int) c) - 48 + 26 + 26;
		else
			switch (c) {
			case '+':
				return 62;
			case '/':
				return 63;
			case '=':
				return 0;
			default:
				throw new RuntimeException("unexpected code: " + c);
			}
	}

	/**
	 * Decodes the given Base64 encoded String to a new byte array. The byte
	 * array holding the decoded data is returned.
	 */

	private static byte[] decode(String s) {

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			decode(s, bos);
		} catch (IOException e) {
			throw new RuntimeException();
		}
		byte[] decodedBytes = bos.toByteArray();
		try {
			bos.close();
			bos = null;
		} catch (IOException ex) {
			System.err.println("Error while decoding BASE64: " + ex.toString());
		}
		return decodedBytes;
	}

	private static void decode(String s, OutputStream os) throws IOException {
		int i = 0;

		int len = s.length();

		while (true) {
			while (i < len && s.charAt(i) <= ' ')
				i++;

			if (i == len)
				break;

			int tri = (decode(s.charAt(i)) << 18)
					+ (decode(s.charAt(i + 1)) << 12)
					+ (decode(s.charAt(i + 2)) << 6)
					+ (decode(s.charAt(i + 3)));

			os.write((tri >> 16) & 255);
			if (s.charAt(i + 2) == '=')
				break;
			os.write((tri >> 8) & 255);
			if (s.charAt(i + 3) == '=')
				break;
			os.write(tri & 255);

			i += 4;
		}
	}

}
