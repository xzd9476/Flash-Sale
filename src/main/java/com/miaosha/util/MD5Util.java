package com.miaosha.util;

import org.apache.commons.codec.digest.DigestUtils;

public class MD5Util {
	public static String md5(String src) {
		return DigestUtils.md5Hex(src);
	}

	private static final String salt = "1a2b3c4d";

	private static String inputPassToDbPass(String input, String saltDB) {
		String formPass = inputPassToFormPass(input);
		String dbPass = formPassToDBPass(formPass, saltDB);
		return dbPass;
	}

	private static String inputPassToFormPass(String input) {
		String newPass =""+salt.charAt(0) + salt.charAt(2) + input + salt.charAt(5) + salt.charAt(4);
		return md5(newPass);
	}

	public static String formPassToDBPass(String formPass, String saltDB) {
		String newPass =""+saltDB.charAt(0) + saltDB.charAt(2) + formPass + saltDB.charAt(5) + saltDB.charAt(4);
		return md5(newPass);
	}

	public static void main(String[] args) {
		System.out.println(inputPassToDbPass("123456", "1a2b3c4d"));
	}

}
