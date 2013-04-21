package jplume.utils;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import javax.activation.MimetypesFileTypeMap;

public class Http {

	private static DateFormat gmtDateFormat = new SimpleDateFormat("dd MMM yyyy HH:mm:ss zz", Locale.US);
	static {
		gmtDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
	}
	public static final String toGMT(Date date) {
		return gmtDateFormat.format(date);
	}
	
	public static final String toGMT(long millseconds) {
		Date d = new Date();
		d.setTime(millseconds);
		return gmtDateFormat.format(d);
	}
	
	public static final long fromGMT(String gmt) {
		try {
			if (gmt == null) return -1;
			return gmtDateFormat.parse(gmt).getTime();
		} catch (ParseException | NumberFormatException e) {
			return -1;
		}
	}
	

	private static final MimetypesFileTypeMap mimeTypes = new MimetypesFileTypeMap();
	
	public static String getMimeType(File file) {
		return mimeTypes.getContentType(file);
	}
	
}
