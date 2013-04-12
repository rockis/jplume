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

		String pp = "^/include/param/(\\d+)/([\\w]+)/(\\d+)$";
		String[] vars = new String[]{"19", "name", "20"};
		Pattern pt = Pattern.compile(pp);
		pt.matcher("19");
	}
}
