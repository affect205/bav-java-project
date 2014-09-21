/**
 * alexbalu-alpha7@mail.ru
 */
package com.alex.balyschev.emanager;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.util.Log;


public class PasswordCodec {
	/**
	 * get md5 hashed string
	 * @param String input
	 * @return
	 */
	public static String getMD5(final String input) {
		String md5 = null;
		if ( input == null ) {
			return null;
		}
		try {
			// create md5 digest
			MessageDigest digest = MessageDigest.getInstance("MD5");
			// update input string
			digest.update(input.getBytes(), 0, input.length());
			// convert message digest value to base 16(hex)
			md5 = new BigInteger(1, digest.digest()).toString(16);
			
		} catch(NoSuchAlgorithmException e) {
			Log.i("GETMD5", e.getMessage());
		}
		return md5;
	}
}