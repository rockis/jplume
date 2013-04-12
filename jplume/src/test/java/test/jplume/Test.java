package test.jplume;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Pattern;

import jplume.conf.Settings;
import jplume.template.TemplateEngine;
import jplume.view.ErrorHandler;

public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {

		System.out.println(new Date().toGMTString());
		DateFormat df = new SimpleDateFormat("dd MMM yyyy HH:mm:ss zz", Locale.US);
		df.setTimeZone(TimeZone.getTimeZone("GMT"));
		Date date = new Date();
		System.out.println(df.format(date));
	}
}
