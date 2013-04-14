package test.jplume;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Stack;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jplume.conf.Settings;
import jplume.template.TemplateEngine;
import jplume.view.ErrorHandler;

class MatchGroup {
	
	private StringBuffer pattern = new StringBuffer();
	private MatchGroup parent = null;
	
	public MatchGroup(MatchGroup parent) {
		this.parent = parent;
	}
	
	public MatchGroup() {}

	public void append(char c) {
		this.pattern.append(c);
	}

	public boolean decline(String var) {
		Matcher m = Pattern.compile("(" + pattern.toString() + ")").matcher(var);
		if (m.matches()) {
			this.parent.pattern.append(m.group(1));
			return true;
		}
		return false;
	}

	public boolean decline(String name, String var) {
		Matcher m = Pattern.compile("(" + pattern.toString() + ")").matcher(var);
		try {
			if (m.matches() && m.group(name) != null) {
				this.parent.pattern.append(m.group(name));
				return true;
			}
		} catch (Exception e) {
			System.out.println("(" + pattern.toString() + ")" + ",");
		}
		return false;
	}
	
	public MatchGroup parent() {
		return parent;
	}
	
	public String toString() {
		return pattern.toString();
	}
}
public class Test {

	
	public static String testIndexed(String pattern, String[] _vars) {
		LinkedList<String> vars = new LinkedList<>(Arrays.asList(_vars));
		MatchGroup top = new MatchGroup();
		char[] cs = pattern.toCharArray();
		char previous = 0;
		
		for(int i = 0; i < cs.length; i++) {
			char c = cs[i];
			if (previous != '\\') {
				if (c == '(') {
					top = new MatchGroup(top);
					previous = c;
					continue;
				}
				if (c == ')') {
					MatchGroup cu = top;
					String var = vars.pollFirst();
					if (!cu.decline(var)) {
						//TODO
					}
					top = top.parent();
					previous = c;
					continue;
				}
			}
			top.append(c);
			previous = c;
		}
		if (vars.size() > 0) {
			//TODO
		}
		String url = top.toString();
		if (url.charAt(0) == '^') {
			url = url.substring(1);
		}
		if (url.charAt(url.length() -1) == '$') {
			url = url.substring(0, url.length() - 1);
		}
		return url;
	}
	
	public static String testNamed(String pattern, Map<String, String> _namedArgs) {
		Map<String, String> namedArgs = new HashMap<String, String>(_namedArgs);
		
		MatchGroup top = new MatchGroup();
		char[] cs = pattern.toCharArray();
		char previous = 0;
		
		for(int i = 0; i < cs.length; i++) {
			char c = cs[i];
			if (previous != '\\') {
				if (c == '(') {
					top = new MatchGroup(top);
					previous = c;
					continue;
				}
				if (c == ')') {
					MatchGroup cu = top;
					boolean matched = false;
					for(String name: namedArgs.keySet()) {
						if (cu.decline(name, namedArgs.get(name))) {
							matched = true;
							namedArgs.remove(name);
							break;
						}
					}
					if (!matched) {
						//TODO
					}
					top = top.parent();
					previous = c;
					continue;
				}
			}
			top.append(c);
			previous = c;
		}
		if (namedArgs.size() > 0) {
			//TODO
		}
		String url = top.toString();
		if (url.charAt(0) == '^') {
			url = url.substring(1);
		}
		if (url.charAt(url.length() -1) == '$') {
			url = url.substring(0, url.length() - 1);
		}
		return url;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
//
		String pp = "^/media/((stylesheet|js|images)/(.*js))/(true|false)$";
		String[] vars = new String[]{"images", "ui.js", "images/ui.js", "false"};
		System.out.println(testIndexed(pp, vars));
		
		pp = "^/media/(?<arg1>[\\w]+)/(?<arg2>[\\d]+)$";
		Map<String, String> namedVars = new HashMap<>();
		namedVars.put("arg1", "ui");
		namedVars.put("arg2", "456");
		System.out.println(testNamed(pp, namedVars));
		
		pp = "^(/media/(?<arg1>[\\w]+))$";
		namedVars.clear();
		namedVars.put("arg1", "ui");
		System.out.println(testNamed(pp, namedVars));
//		int c = 10 * 10000;
//		long s = System.currentTimeMillis();
//		for(int i = 0; i < c; i++)
//			testIndexed(pp, vars);
//		System.out.println(System.currentTimeMillis() - s);
//		for(String s : groups) {
//			System.out.println(s);
//		}
//		Pattern pn = Pattern.compile(pp);
//		Matcher m = pn.matcher("/media/images/ui.js/true");
//		if (m.matches())
//		for(int i = 1 ;i <= m.groupCount(); i++) {
//			System.out.println(m.group(i));
//		}
//		Pattern px = Pattern.compile("\\(([^\\)]+)\\)");
//		StringBuffer sb = new StringBuffer();
//		Matcher m = px.matcher(pp);
//		int i = 0;
//		while (m.find()) {
//			System.out.println(m.group());
//			if (vars[i] == null) {
//				m.appendReplacement(sb, m.group(1));
//			}else{
//				m.appendReplacement(sb, vars[i++]);
//			}
//		}
//		m.appendTail(sb);
//		System.out.println(sb);
	}
}
