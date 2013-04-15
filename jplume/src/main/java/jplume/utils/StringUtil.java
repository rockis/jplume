package jplume.utils;

public class StringUtil {

	public static String capitalize(String str) {
		if (str == null || str.length() == 0) {
			return str;
		}
		char[] cs = str.toCharArray();
		String first = "" + str.charAt(0);
		cs[0] = first.toUpperCase().charAt(0);
		return new String(cs);
	}
}
