package jplume.conf;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jplume.converters.Converter;
import jplume.view.ViewMethod;

public class URLReverser {

	private static final Pattern REVERSE_PATTERN = Pattern
			.compile("\\(([^\\)]+)\\)");

	private URLResolveProvider provider;

	public URLReverser(URLResolveProvider provider) {
		this.provider = provider;
	}

	public String reverse(String className, String methodName) {
		return reverse(className, methodName, new String[0]);
	}
	public String reverse(final String className, final String methodName,
			final String[] pathVars) {
		String url = provider.visit("", new URLVisitor<String>() {
			@Override
			public String visit(Pattern pattern, String[] vars,
					Map<String, String> namedVars, ViewMethod method,
					boolean matched) {
				if (method.getMethod().getDeclaringClass().getName()
						.equals(className)
						&& method.getMethod().getName().equals(methodName)) {
					// ViewMethod.PathIndexedArgument[] args =
					// method.getPathArguments();
					// if (args.length != pathVars.length) {
					// throw new IllegalArgumentException();
					// }else if(args.length == 0) {
					// return pattern.toString();
					// }
					// for (int i = 0; i < args.length; i++) {
					// Class<?> argType = args[i].getType();
					// if (!Converter.validate(argType, pathVars[i])) {
					// throw new IllegalArgumentException();
					// }
					// }
					// return reverse(pattern.toString(), pathVars);
				}
				return null;
			}
		});
		return url;
	}
	
	public String reverse(final String className, final String methodName,
			final Map<String, String> namedVars) {
		String url = provider.visit("", new URLVisitor<String>() {
			@Override
			public String visit(Pattern pattern, String[] vars,
					Map<String, String> namedVars, ViewMethod method,
					boolean matched) {
				if (method.getMethod().getDeclaringClass().getName()
						.equals(className)
						&& method.getMethod().getName().equals(methodName)) {
					// ViewMethod.PathIndexedArgument[] args =
					// method.getPathArguments();
					// if (args.length != pathVars.length) {
					// throw new IllegalArgumentException();
					// }else if(args.length == 0) {
					// return pattern.toString();
					// }
					// for (int i = 0; i < args.length; i++) {
					// Class<?> argType = args[i].getType();
					// if (!Converter.validate(argType, pathVars[i])) {
					// throw new IllegalArgumentException();
					// }
					// }
					// return reverse(pattern.toString(), pathVars);
				}
				return null;
			}
		});
		return url;
	}
	
	private String reverse(String pattern, String[] pathVars) {
		StringBuffer sb = new StringBuffer();
		Matcher m = REVERSE_PATTERN.matcher(pattern);
		int i = 0;
		while (m.find()) {
			m.appendReplacement(sb, pathVars[i++]);
		}
		if (sb.charAt(0) == '^') {
			return sb.substring(1);
		}
		return sb.toString();
	}
	
	public void match(String pattern, Matcher matcher) {
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
//					matcher.match(cu);
					top = top.parent();
					previous = c;
					continue;
				}
			}
			top.append(c);
			previous = c;
		}
//		if (namedArgs.size() > 0) {
//			//TODO
//		}
//		String url = top.toString();
//		if (url.charAt(0) == '^') {
//			url = url.substring(1);
//		}
//		if (url.charAt(url.length() -1) == '$') {
//			url = url.substring(0, url.length() - 1);
//		}
//		return url;
	}
	
	private interface MMatcher {
		
		public void match(MatchGroup top);
		
	}

	private class MatchGroup {
	
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
}
