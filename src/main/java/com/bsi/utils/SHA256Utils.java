package com.bsi.utils;

import com.bsi.framework.core.utils.StringUtils;
import org.apache.commons.codec.binary.Base64;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SHA256Utils {

	public static String getSHA256(String str,String digest) {
		MessageDigest messageDigest;
		String encodestr = "";
		try {
			messageDigest = MessageDigest.getInstance(digest);
			messageDigest.update(str.getBytes(StandardCharsets.UTF_8));
			encodestr = byte2Hex(messageDigest.digest());
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return encodestr;
	}

	public static String getSHA256(String str) {
		return getSHA256(str,"SHA-256");
	}

	private static String byte2Hex(byte[] bytes) {
		StringBuffer stringBuffer = new StringBuffer();
		String temp = null;
		for (int i = 0; i < bytes.length; i++) {
			temp = Integer.toHexString(bytes[i] & 0xFF);
			if (temp.length() == 1) {
				stringBuffer.append("0");
			}
			stringBuffer.append(temp);
		}
		return stringBuffer.toString();
	}

	/**
	 * 加密传入的字符串
	 *
	 * @param key  密钥key
	 * @param body 加密字符串
	 * @return 加密结果
	 */
	public static String generateSignature(String key, String body, String name)
		throws InvalidKeyException, NoSuchAlgorithmException, IllegalStateException {
		if(name == null){
			name = "HamcSHA256";
		}
		return base_64(hMac(key, body, name));
	}

	/**
	 * hamc加密算法
	 *
	 * @param macKey  秘钥key
	 * @param macData 加密内容-响应消息体
	 * @return 加密密文
	 */
	private static byte[] hMac(String macKey, String macData, String name)
			throws NoSuchAlgorithmException, InvalidKeyException, IllegalStateException {
		SecretKeySpec secret = new SecretKeySpec(macKey.getBytes(), name);
		Mac mac = Mac.getInstance(name);
		mac.init(secret);
		return mac.doFinal(macData.getBytes(StandardCharsets.ISO_8859_1));
	}

	/**
	 * 加密传入的字符串
	 *
	 * @param key  密钥key
	 * @param body 加密字符串
	 * @param name 加密方法名
	 * @return 加密结果
	 */
	public static String generateSignature_UTF(String key, String body, String name)
			throws InvalidKeyException, NoSuchAlgorithmException, IllegalStateException {
		return base_64(hMacMD5(key, body, name));
	}

	/**
	 * hamc加密算法
	 *
	 * @param macKey  秘钥key
	 * @param macData 加密内容-响应消息体
	 * @param name 加密方法名
	 * @return 加密密文
	 */
	private static byte[] hMacMD5(String macKey, String macData, String name)
			throws NoSuchAlgorithmException, InvalidKeyException, IllegalStateException {
		SecretKeySpec secret = new SecretKeySpec(macKey.getBytes(), name);
		Mac mac = Mac.getInstance(name);
		mac.init(secret);
		return mac.doFinal(macData.getBytes(StandardCharsets.UTF_8));
	}

	/**
	 * 字节数组转字符串
	 *
	 * @param bytes 字节数组
	 * @return 字符串
	 */
	private static String base_64(byte[] bytes) {
		return new String(Base64.encodeBase64(bytes));
	}

	public static byte[] base_64_decode_bytes(byte[] bytes) {
		return Base64.decodeBase64(bytes);
	}
	public static byte[] base_64_decode_bytes(String string) {
		return base_64_decode_bytes(string.getBytes());
	}
	public static byte[] toBytes(String str,String charset){
		return StringUtils.isEmpty(charset) ? str.getBytes():str.getBytes(Charset.forName(charset));
	}
}
