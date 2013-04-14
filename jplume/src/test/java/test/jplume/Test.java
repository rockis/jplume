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
	
	private final Pattern GROUPNAME_PATTERN = Pattern.compile("\\(\\?<([a-zA-Z][a-zA-Z0-9]*)>");
	private StringBuffer regex = new StringBuffer();
	private MatchGroup parent = null;
	
	public MatchGroup(MatchGroup parent) {
		this.parent = parent;
	}
	
	public MatchGroup() {}

	public void append(char c) {
		this.regex.append(c);
	}

	public String name() {
		Matcher m = GROUPNAME_PATTERN.matcher("(" + this.regex + ")");
		while(m.find()) {
			return m.group(1);
		}
		return null;
	}
	
	public boolean decline(String var) {
		Pattern pattern = Pattern.compile("(" + regex.toString() + ")");
		Matcher m = pattern.matcher(var);
		if (m.matches()) {
			this.parent.regex.append(m.group(1));
			return true;
		}else{
			this.parent.regex.append(pattern.toString());
		}
		return false;
	}
	
	public boolean decline() {
		Pattern pattern = Pattern.compile("(" + regex.toString() + ")");
		this.parent.regex.append(pattern.toString());
		return false;
	}

	public MatchGroup parent() {
		return parent;
	}
	
	public String toString() {
		return regex.toString();
	}
}
public class Test {

	
	public static String testIndexed(String pattern, String[] _vars) {
		LinkedList<String> vars = new LinkedList<>(Arrays.asList(_vars));
		MatchGroup top = new MatchGroup();
		char[] cs = pattern.toCharArray();
		
		for(int i = 0; i < cs.length; i++) {
			char c = cs[i];
			if (i == 0 || cs[i - 1] != '\\') {
				if (c == '(') {
					top = new MatchGroup(top);
					continue;
				}
				if (c == ')') {
					MatchGroup cu = top;
					String var = vars.peekFirst();
					if (cu.decline(var)) {
						vars.pollFirst();
					}
					top = top.parent();
					continue;
				}
			}
			top.append(c);
		}
		if (vars.size() > 0) {
			System.out.println("err");
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
		
		for(int i = 0; i < cs.length; i++) {
			char c = cs[i];
			if (i == 0 || cs[i - 1] != '\\') {
				if (c == '(') {
					top = new MatchGroup(top);
					continue;
				}
				if (c == ')') {
					MatchGroup cu = top;
					top = top.parent();
					String name = cu.name();
					if (name == null) {
						cu.decline();
					}else{
						if (cu.decline(namedArgs.get(name))) {
							namedArgs.remove(name);
						}
					}
					continue;
				}
			}
			top.append(c);
		}
		if (namedArgs.size() > 0) {
			//TODO
			System.out.println("err2");
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
		String pp = "^/param/(\\d+)/([\\w]+)/([\\d]+)$";
		String[] vars = new String[]{"20", "uijs", "30"};
		System.out.println(testIndexed(pp, vars));
		
		pp = "^/media/(?<arg1>([\\w]+))/(?<arg2>([\\d]+))$";
		Map<String, String> namedVars = new HashMap<>();
		namedVars.put("arg1", "ui");
		namedVars.put("arg2", "456");
		System.out.println(testNamed(pp, namedVars));

		pp = "^(/media/(?<arg1>(js|css)/([\\w]+))$";
		namedVars.clear();
		namedVars.put("arg1", "js/ui");
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
