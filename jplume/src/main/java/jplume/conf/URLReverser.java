package jplume.conf;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jplume.converters.Converter;
import jplume.view.ViewMethod;

public class URLReverser {

	private static final Pattern REVERSE_PATTERN = Pattern.compile("\\(([^\\)]+)\\)");
	
	private URLResolveProvider provider;
	
	public URLReverser(URLResolveProvider provider) {
		this.provider = provider;
	}
	
	public String reverse(final String className, final String methodName, final String[] pathVars) {
		String url = provider.visit("", new URLVisitor<String>() {
			@Override
			public String visit(Pattern pattern, String[] vars,
					ViewMethod method, boolean matched) {
				if (method.getMethod().getDeclaringClass().getName().equals(className)
						&& method.getMethod().getName().equals(methodName)) {
					ViewMethod.PathArgument[] args = method.getPathArguments();
					if (args.length != pathVars.length) {
						throw new IllegalArgumentException();
					}else if(args.length == 0) {
						return pattern.toString();
					}
					for (int i = 0; i < args.length; i++) {
						Class<?> argType = args[i].getType();
						if (!Converter.isValid(argType, pathVars[i])) {
							throw new IllegalArgumentException();
						}
					}
					return reverse(pattern.toString(), pathVars);
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
}
