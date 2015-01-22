package com.gao.dreamaccount.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class MD5Util {

	public static String getMD5(String text) {
		String result = null;
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(text.getBytes());
			byte b[] = md.digest();
			int i;
			StringBuffer buf = new StringBuffer("");
			for (int offset = 0; offset < b.length; offset++) {
				i = b[offset];
				if (i < 0)
					i += 256;
				if (i < 16)
					buf.append("0");
				buf.append(Integer.toHexString(i));
			}
			result = buf.toString(); // md5 32bit
			// result = buf.toString().substring(8, 24))); //md5 16bit
			// Log.d("MD5", "md5 16bit: " + buf.toString().substring(8, 24));
			// Log.d("MD5", "md5 32bit: " + buf.toString());
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return result;
	}
}
