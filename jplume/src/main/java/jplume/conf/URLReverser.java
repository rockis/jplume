package jplume.conf;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jplume.view.View;

public class URLReverser {

	private URLResolveProvider provider;

	public URLReverser(URLResolveProvider provider) {
		this.provider = provider;
	}

	public String reverse(String className, String methodName) {
		return reverse(className, methodName, new String[0]);
	}

	public String reverse(final String className, final String methodName,
			final String[] pathVars) {
		CloseBracket<LinkedList<String>> cb = new CloseBracket<LinkedList<String>>() {
			public void process(MatchGroup g, LinkedList<String> vars) {
				if (vars.size() == 0) {
					throw new URLReverseNotMatch();
				}
				String var = vars.peekFirst();
				if (g.decline(var)) {
					vars.pollFirst();
				}
			}
		};
		
		LinkedList<String> vars = new LinkedList<String>(Arrays.asList(pathVars));
		String url = reverse(className, methodName, cb, vars);
		if (vars.size() > 0) 
			throw new URLReverseNotMatch();
		return url;
	}

	public String reverse(final String className, final String methodName,
			final Map<String, String> namedVars) {
		CloseBracket<Map<String, String>> cb = new CloseBracket<Map<String, String>>() {
			public void process(MatchGroup g, Map<String, String> vars) {
				String name = g.name();
				if (name == null) {
					g.decline();
				}else{
					if (vars.size() == 0 || !vars.containsKey(name)) {
						throw new URLReverseNotMatch();
					}
					if (g.decline(vars.get(name))) {
						vars.remove(name);
					}
				}
			}
		};
		
		Map<String, String> vars = new HashMap<String, String>(namedVars);
		String url = reverse(className, methodName, cb, vars);
		if (vars.size() > 0) 
			throw new URLReverseNotMatch();
		return url;
	}

	private <T> String reverse(final String className, final String methodName,
			final CloseBracket<T> cb, final T vars) {
		
		String url = provider.visit("", new URLVisitor<String>() {
			@Override
			public String visit(Pattern pattern, String[] no_use1,
					Map<String, String> no_use2, View view,
					boolean matched) {
				String localClassName = className;
				if (localClassName.charAt(0) == '.') {
					localClassName = Settings.get("DEFAULT_PACKAGE_PREFIX") + localClassName;
				}
				if (view.getMethod().getDeclaringClass().getName()
						.equals(localClassName)
						&& view.getMethod().getName().equals(methodName)) {
					try {
						return match(pattern.toString(), cb, vars);
					} catch (URLReverseNotMatch e) {
						return null;
					}
				}
				return null;
			}
		});
		if (url == null) {
			throw new URLReverseException(className + ":" + methodName);
		}
		if (url.length() == 0) {
			return url;
		}
		int start = 0;
		if (url.charAt(0) == '^') {
			start = 1;
		}
		int end = url.length();
		if (url.charAt(url.length() - 1) == '$') {
			end--;
		}
		return url.substring(start, end);
	}
	
	public <T> String match(String pattern, CloseBracket<T> cb, T vars) {
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
					cb.process(top, vars);
					top = top.parent();
					continue;
				}
			}
			top.append(c);
		}
		if (vars instanceof Map) {
			if (((Map<?, ?>)vars).size() > 0) {
				throw new URLReverseNotMatch();
			}
		}else if (vars instanceof Collection<?>){
			if (((Collection<?>)vars).size() > 0) {
				throw new URLReverseNotMatch();
			}
		}
		return top.toString();
	}

	private interface CloseBracket<T> {
		public void process(MatchGroup top, T vars);
	}

	private static class MatchGroup {

		private static final Pattern GROUPNAME_PATTERN = Pattern
				.compile("\\(\\?<([a-zA-Z][a-zA-Z0-9]*)>");
		private StringBuffer regex = new StringBuffer();
		private MatchGroup parent = null;

		public MatchGroup(MatchGroup parent) {
			this.parent = parent;
		}

		public MatchGroup() {
		}

		public void append(char c) {
			this.regex.append(c);
		}

		public String name() {
			Matcher m = GROUPNAME_PATTERN.matcher("(" + this.regex + ")");
			while (m.find()) {
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
			} else {
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
}
