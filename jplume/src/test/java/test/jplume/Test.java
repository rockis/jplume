package test.jplume;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
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
		
		Pattern px = Pattern.compile("\\(([^\\)]+)\\)");
		StringBuffer sb = new StringBuffer();
		Matcher m = px.matcher(pp);
		int i = 0;
		while (m.find()) {
			m.appendReplacement(sb, vars[i++]);
		}
		System.out.println(sb);
	}
}
