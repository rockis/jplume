package jplume.conf;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jplume.view.ViewMethod;
import jplume.view.annotations.View;

public class URLResolver extends URLResolveProvider {

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
	public URLResolver(String regex, String callback) {
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
				throw new URLResolveException("Class '" + className
						+ "' not found, regex is " + regex + ", callback is " + callback, e);
			}
		} else {
			this.viewMethodName = callback;
		}
	}

	public URLResolver(Method view) {
		View anno = view.getAnnotation(View.class);
		assert view.getAnnotation(View.class) != null;
		this.regex = anno.pattern();
		this.viewMethod = ViewMethod.create(this.pattern, view);
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
				this.possibleMethods.add(ViewMethod.create(this.pattern, method));
			}
		}
		if (possibleMethods.size() == 1) {
			this.viewMethod = possibleMethods.get(0);
		}else if (possibleMethods.size() == 0) {
			throw new URLResolveException("Invalid Pattern:"
					+ this.pattern + " No Such Method '"
					+ actionClass.getName() + "." + this.viewMethodName + "'");
		}else{
			for (ViewMethod m : possibleMethods) {
				System.out.println(m.getMethod().getName());
			}
		}
	}

	public <T> T visit(String path, URLVisitor<T> visitor) throws URLResolveException {
		
		List<String> pathVars = new ArrayList<String>();
		Matcher matcher = pattern.matcher(path);
//		System.out.println(String.format("%s %s", pattern.toString(), path));
		if (!matcher.matches()) {
			return visitor.visit(pattern, new String[0], viewMethod, false);
		}
		for (int i = 1; i <= matcher.groupCount(); i++) {
			pathVars.add(matcher.group(i));
		}
		String[] vars = pathVars.toArray(new String[0]);
		
		synchronized (this) {
			if (viewMethod == null) {
				for(ViewMethod m : this.possibleMethods){
					if (m.match(vars)){
						viewMethod = m;
						break;
					}
				}
				if (viewMethod == null){
					return visitor.visit(pattern, vars, viewMethod, false);
				}
			}
		}
		if(!viewMethod.match(vars)) {
			return visitor.visit(pattern, vars, viewMethod, false);
		}
		
		return	visitor.visit(pattern, vars, viewMethod, true);
	}

	public String toString() {
		return "<" + this.viewMethod + " " + this.regex + ">";
	}
}