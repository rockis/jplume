package jplume.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Hashlib {

	public static MD5 md5() {
		return new MD5();
	}
	
	public static class MD5 {
		
		private MessageDigest instance;
		private MD5() {
			try {
				instance = MessageDigest.getInstance("md5");
			} catch (NoSuchAlgorithmException e) {
			}
		}
		public void update(byte[] bytes) {
			instance.update(bytes);
		}
		
		public byte[] digest() {
			return instance.digest();
		}
		
		public String toString() {
			return new String(digest());
		}
	}
}
