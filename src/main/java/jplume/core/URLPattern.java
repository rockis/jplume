package jplume.core;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jplume.annotations.View;
import jplume.http.Request;
import jplume.http.Response;
import jplume.view.ViewMethod;

public class URLPattern implements DispatcherProvider {

	private Pattern pattern;

	private String regex;

	private ViewMethod viewMethod;
	private List<ViewMethod> possibleMethods;

	private String viewMethodName;

	/**
	 * 
	 * @param regex
	 * @param callback
	 *            method name or classname.methodname
	 */
	public URLPattern(String regex, String callback) {
		this.regex = regex;
		this.pattern = Pattern.compile(regex);
		int indexOfLastDot = callback.lastIndexOf('.');
		if (indexOfLastDot > 0) { // callback is classname.methodname
			this.viewMethodName = callback.substring(indexOfLastDot + 1);

			String className = callback.substring(0, indexOfLastDot);
			try {
				Class<?> actionClass = Class.forName(className);
				this.setActionClass(actionClass);
			} catch (ClassNotFoundException e) {
				throw new URLPatternException("Class '" + className
						+ "' not found, regex is " + regex + ", callback is " + callback, e);
			}
		} else {
			this.viewMethodName = callback;
		}
	}

	public URLPattern(Method view) {
		View anno = view.getAnnotation(View.class);
		assert view.getAnnotation(View.class) != null;
		this.regex = anno.pattern();
		this.viewMethod = ViewMethod.create(view);
	}

	public void setRegexPrefix(String regexPrefix) {
		if (this.regex.charAt(0) == '^') {
			this.pattern = Pattern.compile(regexPrefix
					+ this.regex.substring(1));
		} else {
			this.pattern = Pattern.compile(regexPrefix + this.regex);
		}
	}

	public void setActionClass(Class<?> actionClass) {
		if (this.possibleMethods != null) {
			return;
		}
		this.possibleMethods = new ArrayList<ViewMethod>();
		for (Method method : actionClass.getMethods()) {
			if (method.getName().equals(this.viewMethodName)) {
				this.possibleMethods.add(ViewMethod.create(method));
			}
		}
		if (possibleMethods.size() == 0) {
			throw new URLPatternException("Invalid Pattern:"
					+ this.pattern + " No Such Method '"
					+ actionClass.getName() + "." + this.viewMethodName + "'");
		}
	}

	public Response dispatch(Request request) throws URLDispatchException {
		synchronized (this) {
			List<String> pathVars = new ArrayList<String>();
			Matcher matcher = this.pattern.matcher(request.getPath());
			if (!matcher.matches()) {
				return null;
			}
			for (int i = 1; i <= matcher.groupCount(); i++) {
				pathVars.add(matcher.group(i));
			}
			String[] vars = pathVars.toArray(new String[0]);
			if (this.viewMethod == null) {
				for(ViewMethod m : this.possibleMethods){
					if (m.match(vars)){
						this.viewMethod = m;
						break;
					}
				}
				if (this.viewMethod == null){
					return null;
				}
			} else if(!this.viewMethod.match(vars)) {
				return null;
			}
			return this.viewMethod.handle(request, vars);
		}
	}

	public String toString() {
		return "<" + this.viewMethod + " " + this.regex + ">";
	}
}