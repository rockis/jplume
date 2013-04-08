package jplume.core;

import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import jplume.annotations.View;
import jplume.http.Request;
import jplume.http.Response;

public class RequestDispatcher {

	private DispatcherProvider patterns;

	public RequestDispatcher(String urlconf) {
		this.patterns = create(urlconf);
	}

	public RequestDispatcher(URL urlconf) {
		this.patterns = create(urlconf);
	}

	public Response dispatch(Request request) throws URLDispatchException{
		return patterns.dispatch(request);
	}

	public static DispatcherProvider patterns(String actionClassName,
			DispatcherProvider... patterns) {
		Class<?> actionClass;
		try {
			if (actionClassName.length() > 0) {
				actionClass = Class.forName(actionClassName);
				return new URLPatternGroup(actionClass, patterns);
			} else {
				return new URLPatternGroup(patterns);
			}
		} catch (ClassNotFoundException e) {
			throw new URLPatternException("Invalid Pattern: No Such Class '"
					+ actionClassName + "'");
		}
	}

	public static DispatcherProvider pattern(String regex, String callback) {
		return new URLPattern(regex, callback);
	}

	public static DispatcherProvider pattern(String regex, DispatcherProvider patterns) {
		patterns.setRegexPrefix(regex);
		return patterns;
	}

	public static DispatcherProvider include(String urlconf) {
		return create(urlconf);
	}

	public static DispatcherProvider create(String urlconf) {
		if (urlconf.indexOf('/') > 0) { // urlconf is xxx.urls
			if (!urlconf.endsWith(".urls")) {
				urlconf = urlconf + ".urls";
			}
			URL url = RequestDispatcher.class.getClassLoader().getResource(
					urlconf);
			if (url == null) {
				throw new URLPatternException("Cannot read urlconf '"
						+ urlconf + "'");
			}
			// urlconf in classpath
			return create(url);
		} else { // urlconf is class name
			try {
				Class<?> actionClass = Class.forName(urlconf);
				return create(actionClass);
			} catch (ClassNotFoundException e) {
				throw new URLPatternException("Cannot read urlconf '"
						+ urlconf + "'", e);
			}
		}
	}

	public static DispatcherProvider create(URL urlconf) {
		assert urlconf != null;

		ScriptEngineManager sem = new ScriptEngineManager();
		ScriptEngine jsEngine = sem.getEngineByName("js");
		try {
			jsEngine.eval("importPackage(Packages.jplume.core);");
			jsEngine.eval("patterns = RequestDispatcher.patterns;");
			jsEngine.eval("_ = pattern = RequestDispatcher.pattern;");
			jsEngine.eval("include = RequestDispatcher.include;");

			jsEngine.eval(new InputStreamReader(urlconf.openStream()));
			return (DispatcherProvider) jsEngine.get("urlpatterns");

		} catch (ScriptException e) {
			e.printStackTrace();
			throw new URLDispatchException("Urlconf '"
					+ urlconf.toString() + "' has an error", e);
		} catch (IOException e) {
			throw new URLDispatchException("Cannot read urlconf '"
					+ urlconf.toString() + "'", e);
		}
	}

	public static DispatcherProvider create(Class<?> actionClass) {
		Method[] views = actionClass.getMethods();
		List<URLPattern> patterns = new ArrayList<URLPattern>();
		for (Method view : views) {
			if (view.getAnnotation(View.class) != null) {
				patterns.add(new URLPattern(view));
			}
		}
		return new URLPatternGroup(patterns.toArray(new URLPattern[0]));
	}
}
