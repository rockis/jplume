package jplume.conf;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jplume.utils.ClassUtil;
import jplume.view.ArgumentBuilder;
import jplume.view.ArgumentBuilder.PathNamedArgument;
import jplume.view.ViewFactory;
import jplume.view.ViewMethod;
import jplume.view.annotations.View;

public class URLResolver extends URLResolveProvider {

	private Pattern pattern;

	private String regex;

	private ViewMethod viewMethod;

	private String viewMethodName;

	private String methodAlias;

	/**
	 * 
	 * @param regex
	 * @param callback
	 *            method name or classname.methodname
	 */
	public URLResolver(String regex, String callback) {
		this.regex = regex;
		this.pattern = Pattern.compile(regex);
		int indexOfLastColon = callback.lastIndexOf(':');
		if (indexOfLastColon > 0) { // callback is classname.methodname
			this.viewMethodName = callback.substring(indexOfLastColon + 1);
			int indexOfLastExcl = this.viewMethodName.lastIndexOf('!');
			if (indexOfLastExcl > 0) {
				this.methodAlias = this.viewMethodName
						.substring(indexOfLastExcl + 1);
				this.viewMethodName = this.viewMethodName.substring(0,
						indexOfLastExcl);
			}
			String className = callback.substring(0, indexOfLastColon);
			try {
				Class<?> actionClass = ClassUtil.forName(className);
				this.setActionClass(actionClass);
			} catch (ClassNotFoundException e) {
				throw new IllegalURLPattern("Class '" + className
						+ "' not found, regex is " + regex + ", callback is "
						+ callback, e);
			}
		} else {
			this.viewMethodName = callback;
		}
	}

	public URLResolver(Method view) {
		View anno = view.getAnnotation(View.class);
		assert view.getAnnotation(View.class) != null;
		this.regex = anno.regex();
		this.viewMethod = ViewFactory.createView(this.pattern, view);
	}

	public void addRegexPrefix(String regexPrefix) {
		if (this.regex.charAt(0) == '^') {
			this.regex = regexPrefix + this.regex.substring(1);
			this.pattern = Pattern.compile(this.regex);
		} else {
			this.pattern = Pattern.compile(regexPrefix + this.regex);
		}
	}

	public void setActionClass(Class<?> actionClass) {
		if (this.viewMethod != null) {
			return;
		}

		List<Method> possibleMethods = new ArrayList<>();

		for (Method method : actionClass.getMethods()) {
			if (method.getName().equals(this.viewMethodName)) {
				possibleMethods.add(method);
			}
		}
		if (possibleMethods.size() == 1) {
			this.viewMethod = ViewFactory.createView(pattern, possibleMethods.get(0));
		} else if (possibleMethods.size() == 0) {
			throw new IllegalURLPattern("Invalid Pattern:" + this.pattern
					+ " No Such Method '" + actionClass.getName() + "."
					+ this.viewMethodName + "'");
		} else {
			for (Method m : possibleMethods) {
				View anno = m.getAnnotation(View.class);
				if ((anno == null && this.methodAlias == null)
						|| (anno != null && anno.alias().equals(
								this.methodAlias))) {
					if (this.viewMethod != null) {
						throw new IllegalURLPattern("Ambiguous method:"
								+ m.getDeclaringClass() + "." + m.getName());
					}
					this.viewMethod = ViewFactory.createView(pattern, m);
				}
			}
			if (this.viewMethod == null) {
				throw new IllegalURLPattern("Invalid Pattern:" + this.pattern
						+ " No eplicit Method '" + actionClass.getName() + "."
						+ this.viewMethodName + "'");
			}
		}
	}

	public <T> T visit(String path, URLVisitor<T> visitor)
			throws IllegalURLPattern {

		Matcher matcher = pattern.matcher(path);

		// System.out.println(String.format("%s %s", pattern.toString(), path));
		Map<String, String> emptyNamedVars = Collections.emptyMap();
		if (!matcher.matches()) {
			return visitor.visit(pattern, new String[0],
					emptyNamedVars, viewMethod, false);
		}
		List<String> indexedVars = new ArrayList<>();
		Map<String, String> namedVars = new HashMap<>();
		
		ArgumentBuilder argBuilder = viewMethod.getArgBuilder();
		if (argBuilder.getPathNamedArgs().size() > 0) {
			for(PathNamedArgument arg : argBuilder.getPathNamedArgs()){
				String argName = arg.getArgName();
				String argVal  = matcher.group(argName);
				if (argVal == null) {
					return visitor.visit(pattern, new String[0],
							emptyNamedVars, viewMethod, false);
				}
				namedVars.put(arg.getArgName(), matcher.group(arg.getArgName()));
			}
		}else{
			for (int i = 1; i <= matcher.groupCount(); i++) {
				indexedVars.add(matcher.group(i));
			}
		}
		
		if (!argBuilder.validate(indexedVars.toArray(new String[0]),
				Collections.unmodifiableMap(namedVars))) {
			return visitor.visit(pattern, indexedVars.toArray(new String[0]),
					Collections.unmodifiableMap(namedVars), viewMethod, false);
		}

		return visitor.visit(pattern, indexedVars.toArray(new String[0]),
				Collections.unmodifiableMap(namedVars), viewMethod, true);
	}

	public String toString() {
		return "<" + this.viewMethod + " " + this.regex + ">";
	}
}