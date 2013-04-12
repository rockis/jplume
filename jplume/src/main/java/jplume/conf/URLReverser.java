package jplume.conf;

import java.util.regex.Pattern;

import jplume.converters.Converter;
import jplume.view.ViewMethod;

public class URLReverser {

	private URLResolveProvider provider;
	
	public URLReverser(URLResolveProvider provider) {
		this.provider = provider;
	}
	
	public String reverse(final String className, final String methodName, final String[] pathVars) {
		System.out.println(String.format("%s.%s", className, methodName));
		String url = provider.visit("", new URLVisitor<String>() {
			@Override
			public String visit(Pattern pattern, String[] pathVars,
					ViewMethod method, boolean matched) {
//				System.out.println(String.format("%s.%s", method.getMethod().getDeclaringClass().getName(), method.getMethod().getName()));
				if (method.getMethod().getDeclaringClass().getName().equals(className)
						&& method.getMethod().getName().equals(methodName)) {
					ViewMethod.PathArgument[] args = method.getPathArguments();
					if (args.length != pathVars.length) {
						return null;
					}else if(args.length == 0) {
						return pattern.toString();
					}
					for (int i = 0; i < args.length; i++) {
						Class<?> argType = args[i].getType();
						if (!Converter.isValid(argType, pathVars[i])) {
							//TODO throw exception
							return null;
						}
					}
					//TODO replace pathvar within pattern
				}
				return null;
			}
		});
		return url;
	}
}
